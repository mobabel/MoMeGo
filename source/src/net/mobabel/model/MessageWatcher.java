/**
 * 
 */
package net.mobabel.model;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.PushRegistry;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.rms.RecordStoreException;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;

import de.enough.polish.util.Locale;

import net.mobabel.item.ItemGlobal;
import net.mobabel.item.ItemMessage;
import net.mobabel.util.UIController;
import net.mobabel.view.AlertFactory;

/**
 * @author leeglanz
 *
 */
public class MessageWatcher implements CommandListener, Runnable, MessageListener{

	private UIController controller = null;

	private Thread thread;

	/** SMS message connection for inbound text messages. */
	MessageConnection smsconn;

	/** Connections detected at start up. */
	String[] connections;

	/** Current message read from the network. */
	Message msg;

	ItemMessage message = null;

	RecordStoreMessage rsmsg = null;

	public MessageWatcher(UIController controller){
		this.controller = controller;

		String smsPort = ItemGlobal.getInstance().getSMS_Port();
		/** SMS connection to be read. */
		String smsConnection = "sms://:" + smsPort;

		/** Open the message connection. */
		if (smsconn == null) {
			try {
				smsconn = (MessageConnection)Connector.open(smsConnection);
				smsconn.setMessageListener(this);
			} catch (IOException ioe) {
				//#debug debug
				System.out.println("Err open listener: " + ioe.toString());
				controller.alert = AlertFactory.createErrorAlert(Locale.get("error.failtoopensmslistener")+ioe.toString());
				controller.showCurrent(controller.alert);
				//try to close the opened connection
			}
		}

		/** Initialize the text if we were started manually. */
		connections = PushRegistry.listConnections(true);

		if ((connections == null) || (connections.length == 0)) {
			//#debug debug
			System.out.println("Waiting for SMS on port " + smsPort + "...");
			//content.setString("Waiting for SMS on port " + smsPort + "...");
		}
	}

	public void run() {
		/** Check for sms connection. */
		try {
			msg = smsconn.receive();
			if (msg != null) {
				message = new ItemMessage();
				String senderAddress = msg.getAddress();
				senderAddress = CommonFunc.replace(senderAddress, "sms://", "");
				if(senderAddress.indexOf(":") > 0 ){
					senderAddress = senderAddress.substring(0, senderAddress.indexOf(":"));
				}
				
				String msgcontent;
				controller.itemMsgComing.setAddress(senderAddress);
				message.setAddress(senderAddress);
				message.setTimestamb(msg.getTimestamp().getTime());
				message.setStatus(ItemMessage.STATUS_UNREAD);

				if (msg instanceof TextMessage) {
					msgcontent = ((TextMessage)msg).getPayloadText();
					message.setType(ItemMessage.TYPE_TextMessage);
					message.setText(msgcontent);
				} else {
					StringBuffer buf = new StringBuffer();
					byte[] data = ((BinaryMessage)msg).getPayloadData();

					for (int i = 0; i < data.length; i++) {
						int intData = (int)data[i] & 0xFF;

						if (intData < 0x10) {
							buf.append("0");
						}

						buf.append(Integer.toHexString(intData));
						buf.append(' ');
					}

					msgcontent = buf.toString();
					message.setType(ItemMessage.TYPE_BinaryMessage);
					message.setData(data);
					message.setText(msgcontent);
				}
				//save the message into RMS
				try {
					if(rsmsg == null)
						rsmsg = new RecordStoreMessage(controller);
					rsmsg.addRS(message);
				} catch (Exception e) {
					controller.alert = AlertFactory.createErrorAlert(Locale.get("error.rmsexception")+e.toString());
					controller.showCurrent(controller.alert);
				}


				controller.alert = null;
				controller.alert = AlertFactory.createMessageAlert(Locale.get("item.messageFrom") + senderAddress, msgcontent, Setting.alertMessageTime);
				controller.showCurrent(controller.alert);
				AlertNotifier.play();
			}
		} catch (IOException e) {
			//#debug error
			System.out.println("Can not open sms listener: " + e.toString());
			controller.alert = AlertFactory.createErrorAlert(Locale.get("error.failtoopensmslistener")+e.toString());
			controller.showCurrent(controller.alert);
		}

	}

	public void quit(){
		if (smsconn != null) {
			try {
				smsconn.close();
			} catch (IOException ignore) {
			}
		}
	}

	public void commandAction(Command cmd, Displayable dis) {


	}

	public void notifyIncomingMessage(MessageConnection msgconn) {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}

	}




}
