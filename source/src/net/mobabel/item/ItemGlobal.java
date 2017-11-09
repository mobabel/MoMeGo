package net.mobabel.item;

public class ItemGlobal {

	public static ItemGlobal instance = null;
	
	private static String SMS_Port = "";
	
	public final static int SCREEN_TYPE_INBOX = 1;
	
	public final static int SCREEN_TYPE_OUTBOX = 2;
	
	public final static int SCREEN_TYPE_SENTMESSAGE = 3;
	
	public final static int SCREEN_TYPE_SAVEDMESSAGE = 4;
	
	public final static int SCREEN_TYPE_DRAFT = 5;
	
	public final static int SCREEN_TYPE_TEMPLATE = 6;
	
	public final static int SCREEN_TYPE_RECYCLE = 7;
	
	public final static int SCREEN_TYPE_COMPILER = 8;
	
	public final static int SCREEN_TYPE_REPLY_FROM_INBOX = 9;
	
	public final static int SCREEN_TYPE_CONTACTLIST = 10;
	
	public final static int SCREEN_TYPE_CONTACTGROUP = 11;
	
	public final static int SCREEN_TYPE_SEND_FROM_CONTACTLIST = 12;
	
	private static int screenType = SCREEN_TYPE_INBOX;
	
	public ItemGlobal(){
		
	}
	
	public static ItemGlobal getInstance(){
		if(instance == null){
			instance = new ItemGlobal();
		}
		return instance;
	}
	
	public static void setSMS_Port(String SMS_Port_){
		SMS_Port = SMS_Port_;
	}
	
	public static String getSMS_Port(){
		return SMS_Port;
	}
	
	public static void setScreenType(int screenType_){
		screenType = screenType_;
	}
	
	public static int getScreenType(){
		return screenType;
	}
	
	
	
	
}
