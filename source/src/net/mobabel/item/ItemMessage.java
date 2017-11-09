package net.mobabel.item;

public class ItemMessage {
	
	public final static int TYPE_TextMessage = 1;
	
	public final static int TYPE_BinaryMessage = 2;
	
	public final static int TYPE_MultipartMessage = 3;
	
	private int type = TYPE_TextMessage;
	
	public static int STATUS_UNREAD = 0;
	
	public static int STATUS_READED = 1;
	
	public static int STATUS_INBOX = 8;
	
	public static int STATUS_SENDED = 2;
	
	public static int STATUS_OUTBOX = 3;
	
	public static int STATUS_SAVED = 4;
	
	public static int STATUS_DRAFT = 5;
	
	public static int STATUS_TEMPLATE = 6;
	
	public static int STATUS_RECYCLE = 7;
	
	private int status = STATUS_UNREAD;
	
	private String address = "";

	private String text = "";

	private byte[] data = new byte[0]; 

	private long timestamb = 0;

	private int id = 0;
	
	private boolean replyed = false;

	public ItemMessage() { 
	}
	
	/**
	 * set the sms address of message, it is phone number
	 * @param address
	 */
	public void setAddress(String address){
		this.address=address;
	}
	
	public String getAddress(){
		return this.address;
	}  
	
	public void setText(String text){
		this.text=text;
	}
	
	public String getText(){
		return this.text;
	}  
	
	public void setData(byte[] data){
		this.data=data;
	}
	
	public byte[] getData(){
		return this.data;
	}  

	public long getTimestamb(){
		return this.timestamb;
	}

	public void setTimestamb(long timestamb){
		this.timestamb=timestamb;
	}
	
	public int getType(){
		return this.type;
	}

	public void setType(int type){
		this.type=type;
	}
	
	public int getStatus(){
		return this.status;
	}

	public void setStatus(int status){
		this.status=status;
	}

	public int getId(){
		return this.id;
	}

	public void setId(int id){
		this.id=id;
	}
	
	public boolean getReplyed(){
		return this.replyed;
	}

	public void setReplyed(boolean replyed){
		this.replyed = replyed;
	}
	
}

