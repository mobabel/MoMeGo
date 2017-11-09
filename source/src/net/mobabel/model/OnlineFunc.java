package net.mobabel.model;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

import net.mobabel.item.ItemMessage;
import net.mobabel.model.CommonFunc;
import net.mobabel.model.Setting;
import net.mobabel.util.UIController;
import net.mobabel.view.AlertFactory;
import net.mobabel.view.ScreenResult;


import de.enough.polish.util.Locale;

public class OnlineFunc {

	private UIController    controller = null;
	private Displayable parent = null;
	private ScreenResult dictresult = null;

	public static final int SEARCH_TYPE_W2T = 0;

	public static final int SEARCH_TYPE_T2W = 1;

	private boolean first = true;

	public void getrequest(UIController control,final String keyword,final Displayable parent,final int resultmax, final int type){
		this.controller = control;
		this.parent = parent;

		//String temp = "kw="+URLEncoder.encode(keyword, "UTF-8");
		String temp = "kw= "+keyword+" ";
		if(type == SEARCH_TYPE_W2T){

		}else{
	
		}
		final String postData = temp;
		//final String urlstring_ = Setting.transUrl + "?";

		Thread GetrequestThread = new Thread() {
			private boolean B_parseStop;
			HttpConnection httpConnection =null ;
			DataInputStream dis =null;
			DataOutputStream dos = null;

			//StringBuffer transresult = new StringBuffer("");
			String trans =" ";//not empty
			String content="";

			public void run() {}


		};//thread
		GetrequestThread.start(); 

	}

	public byte[] loadData(InputStream is) throws Exception{
		ByteArrayOutputStream bos = null;
		byte[] tempBuf = new byte[16];
		int bytesRead = 0;
		try {
			bos = new ByteArrayOutputStream();
			while ((bytesRead = is.read(tempBuf)) != -1) {
				bos.write(tempBuf, 0, bytesRead);
			}
		} catch (IOException e) {
			throw new Exception(Locale.get("message.loaddataerror"));
		} 
		finally {
			try {
				if(bos != null)
					bos.close();
			} catch (Exception e) {              
			}
		}
		return bos.toByteArray();
	}

	public static String byte2utf8(String s){    
		if (s==null){ 
			return ""; 
		}    
		if (s.equals(""))
		{ 
			return s; 
		}    
		try{      
			return new String(s.getBytes(),"UTF-8");     
		}    catch(Exception e){     
			return s;     
		}  
	}

}
