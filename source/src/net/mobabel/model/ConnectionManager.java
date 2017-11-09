package net.mobabel.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import net.mobabel.item.ItemContact;
import net.mobabel.item.ItemGlobal;
import net.mobabel.item.ItemMessage;

import de.enough.polish.util.Locale;

/**
 *  Class ConnectionManager
 *  handles the HttpConnection and the Http-GET request of a query and sends
 *  messages and data to the object observing the request
 * 
 * 
 *  @author leelight
 * 
 *  @version     %I%, %G%
 *  @since       1.0 
 * 
 */
public class ConnectionManager extends Thread
{
	/**
	 * to store the bytes before we know how big the array will be
	 */
	ByteArrayOutputStream bos = null;

	/**
	 * Reference to the Http Connection
	 */
	HttpConnection con = null;

	MessageConnection msgconn = null;

	/**
	 * Store the data in an Array when we know how much it will be
	 */
	byte[] data;

	/**
	 * Reference to the object implementing the RequestObserver, observing this
	 * HTTP request
	 */
	RequestObserver iObserver = null;

	/**
	 * String holding the Url for the request
	 */
	private String iQueryUrl;

	Vector requestBuffer = new Vector();

	/**
	 * String holding the data that will be sent to server
	 */
	private String sendData;

	/**
	 * Flag check the request Get or Post
	 */
	private boolean isRequestGet = true;

	/**
	 * Reference to the Input Stream
	 */
	InputStream is = null;

	/**
	 * DataOutputStream
	 */
	DataOutputStream dos = null;

	/**
	 * Response code from the Http-GET Request
	 */
	private int rc;

	public boolean stopThread = false;

	private Thread mostRecent = this;

	private Thread thisThread = null;

	public final static int ConnectionType_SMSCONNECTION = 0;

	public final static int ConnectionType_HTTPCONNECTION = 1;
	
	public final static int ConnectionType_SCANPIM = 2;

	private int ConnectionType = ConnectionType_SMSCONNECTION;

	private ItemMessage message = null;

	/**
	 * Constructor of the class ConnectionManager
	 * 
	 * @param aObserver  the object observing this request
	 */
	public ConnectionManager(RequestObserver aObserver)
	{
		iObserver = aObserver;
	}

	/**
	 * Method stopConnection
	 * stops the thread running the HttpConnection and the Http-GET request
	 * 
	 * @return the success of the operation
	 */
	public boolean stopConnection()
	{
		iObserver.onProgressNotification("Stop the thread!");
		stopThread = true;
		mostRecent = null;
		//this.interrupt();
		return true;
	}

	public synchronized void sendSMS(ItemMessage message){
		this.message = message;
		stopThread = false;
		mostRecent = this;
		ConnectionType = ConnectionType_SMSCONNECTION;
		new Thread(this).start();
	}
	
	public void scanPIM(){
		stopThread = false;
		mostRecent = this;
		ConnectionType = ConnectionType_SCANPIM;
		new Thread(this).start();
	}

	/**
	 * Method httpGet()
	 * starts the thread running the HttpConnection and the Http-GET request
	 * 
	 * @param aQueryUrl    the URL for the Http-GET request 
	 */
	public void httpGet(String aQueryUrl)
	{
		isRequestGet = true;
		//#debug debug
		System.out.println("GET RequestURL: " + aQueryUrl );
		requestBuffer.addElement(aQueryUrl);
		if (this.isAlive())
		{
			return;
		}
		stopThread = false;
		mostRecent = this;
		ConnectionType = ConnectionType_HTTPCONNECTION;
		new Thread(this).start();
	}

	/**
	 * Method httpPost
	 * start the data sending per POST HttpConnection
	 * 
	 * @param httpPost		data in String format which will be send to server
	 */
	public void httpPost(String sendData){

		stopThread = false;
		mostRecent = this;
		ConnectionType = ConnectionType_HTTPCONNECTION;
		new Thread(this).start();
	}
	
	public void stopThread(){
		stopThread = true;
		mostRecent = null;
	}

	/**
	 *  Method run()
	 *  overwritten Method of the Class Thread, starts a Thread connecting via
	 *  HttpConnection to the server, sending a Http-GET request to this server
	 *  and downloading the response data from a Stream
	 */
	public void run()
	{
		thisThread = Thread.currentThread();
		do
		{
			/*if (requestBuffer.size() > 0)
			{
				iQueryUrl = (String) requestBuffer.elementAt(0);
				requestBuffer.removeElementAt(0);
			}
			else
			{
				break;
			}*/

			try
			{
				iObserver.onDownloadStart();
				if(ConnectionType == ConnectionType_SMSCONNECTION){
					try {
						String addr = "sms://"+message.getAddress()+":"+ItemGlobal.getInstance().getSMS_Port();
						//#debug debug
						System.out.println("send sms addr: "+ addr);
						msgconn = (MessageConnection)Connector.open(addr);

						switch(message.getType()){
						case ItemMessage.TYPE_TextMessage:
							TextMessage msg = (TextMessage)msgconn.newMessage(MessageConnection.TEXT_MESSAGE);
							msg.setPayloadText(message.getText());
							msgconn.send(msg);
							break;

						}
						
						iObserver.onDownloadFinished(null);
					} catch (IOException e) {
						//#debug error
						System.out.println("Err when send message: "+ e.toString());
						iObserver.onAborted(Locale.get("error.sendsmsfail")+e.toString());
					}				
				}
				else if(ConnectionType == ConnectionType_SCANPIM){
					PIMFunc pinfunc = new PIMFunc();
					ItemContact[] itemcontacts = pinfunc.readPIM();
					
					iObserver.onScanFinished(itemcontacts);
				}
				else if(ConnectionType == ConnectionType_HTTPCONNECTION){
					//#debug debug
					System.out.println("Connecting to: " + iQueryUrl);
					// open the connection
					con = (HttpConnection) Connector.open(iQueryUrl);
					// set the Request method
					if(isRequestGet){
						con.setRequestMethod(HttpConnection.GET);
						iObserver.onProgressNotification("Connection open");
					}
					else{
						con.setRequestProperty("Content-Length", Integer.toString(this.sendData.length()));
						con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
						con.setRequestProperty("Accept","application/octet-stream" );
						con.setRequestMethod(HttpConnection.POST);
						dos = con.openDataOutputStream();
						dos.write(sendData.getBytes());
						dos.flush();
						//iObserver.onProgressNotification("Connection open and send raw data");
						//#debug debug
						System.out.println("Connection open and send raw data");
					}

					// receive a Response code...
					rc = con.getResponseCode();

					if (rc != HttpConnection.HTTP_OK)
					{
						// ... and throw a Exception when Code is not 200 (HTTP_OK)
						throw new IOException("HTTP response code: " + rc);
					}
					else
					{
						iObserver.onProgressNotification("HTTP response code: " + rc);
					}

					// open a input stream to the server
					is = con.openInputStream();

					// get the answers content type
					String type = con.getType();
					iObserver.onProgressNotification("Content Type: " + type);

					if(	stopThread || mostRecent != thisThread)
						return;
					try{

					}catch(Exception e){
						stopThread = true;
						mostRecent = null;
						return;
					}

					// Try to get the answers content length
					int len = (int) con.getLength();
					iObserver.onProgressNotification("Stream Length: " + len);

					// if we get a content length, read chunks from stream.....
					if (len > 0)
					{
						int actual = 0;
						int bytesread = 0;
						data = new byte[len];
						while ((bytesread != len) && (actual != -1))
						{
							actual = is.read(data, bytesread, len - bytesread);
							bytesread += actual;
							// notify the observer that bytes were loaded
							iObserver.onBytesLoaded(bytesread);
						}
						//iObserver.onBytesLoaded(bytesread);
						// .... or read just byte by byte until the stream is over
					}
					else
					{
						int ch;
						int counter = 0;
						bos = new ByteArrayOutputStream();
						while ((ch = is.read()) != -1)
						{
							counter++;
							bos.write(ch);
							if (counter % 1024 == 0)
								iObserver.onBytesLoaded(counter);
						}
						iObserver.onBytesLoaded(counter);
						data = bos.toByteArray();
					}
					iObserver.onProgressNotification("Transaction finished");

					// notify the Observer, that the download is finished
					if(	!stopThread && mostRecent == thisThread)
						iObserver.onDownloadFinished(data);
				}

			}
			catch (ClassCastException e)
			{
				iObserver.onProgressNotification("Error in Url: " + e.getMessage());
				iObserver.onAborted(Locale.get("error.urlError"));
				//e.printStackTrace();
			}
			catch (IOException e)
			{
				iObserver.onProgressNotification("Connection Error: " + e.getMessage());
				iObserver.onAborted(Locale.get("error.connectionError"));
				//e.printStackTrace();
			}
			catch (Exception e)
			{
				iObserver.onProgressNotification("Error: " + e.toString());
				iObserver.onAborted(Locale.get("error.exception"));
				//e.printStackTrace();
			}
			finally
			{
				// close all open streams
				if (con != null)
				{
					try
					{
						con.close();
					}
					catch (IOException e)
					{
						// Can be ignored here
					}
				}
				if (is != null)
				{
					try
					{
						is.close();
					}
					catch (IOException e)
					{
						// Can be ignored here
					}
				}
				if (bos != null)
				{
					try
					{
						bos.close();
					}
					catch (IOException e)
					{
						// Can be ignored here
					}
				}
				if (dos != null)
				{
					try
					{
						dos.close();
					}
					catch (IOException e)
					{
						// Can be ignored here
					}
				}
				if (msgconn != null)
				{
					try
					{
						msgconn.close();
					}
					catch (IOException e)
					{
						// Can be ignored here
					}
				}
			}
		} while (/*requestBuffer.size() > 0*/mostRecent == thisThread);
	}
}
