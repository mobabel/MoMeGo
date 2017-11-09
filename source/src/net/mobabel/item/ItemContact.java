package net.mobabel.item;

import java.util.Vector;

public class ItemContact {
	
	private String UID = "";
	
	private String NAME_FORMATTED = "";
	
	private String NAME = "";
	
	private String NAME_GIVEN = ""; 
	
	private String NAME_FAMILY = "";
	
	private String ADDR = "";
	
	private String ADDR_COUNTRY = "";
	
	private String ADDR_LOCALITY = "";
	
	private String ADDR_POSTALCODE = "";
	
	private String ADDR_STREET = "";
	
	private String ATTR_HOME = "";
	
	private Vector TEL = new Vector();
	
	private String GeneralPhone = "";
	
	private String EMAIL = "";
	
	private String URL = "";
	
	private String NOTE = "";
	
	private String NICKNAME = "";
	
	private String ORG = "";
	
	private String TITLE = "";
	
	private String BIRTHDAY = "";
	
	private String REVISION = "";
	
	private String PUBLIC_KEY_STRING = "";
	
	private String PHOTO_URL = "";
	
	private byte[] PHOTO = new byte[0];
	
	private byte[] PUBLIC_KEY = new byte[0];
	
	private boolean isModified = false;
	
	private boolean hasBackuped = false;
	
	private int rsId = -1;
	
	private Vector ItemContactExtends = new Vector();
	
	public void setRsId(int rsId){
		this.rsId = rsId;
	}
	
	public int getRsId(){
		return this.rsId;
	}
	
	public void setUID(String UID){
		this.UID = UID;
	}
	
	public String getUID(){
		return this.UID;
	}
	
	/**
	 * SE phone does not support NAME_FORMATTED.
	 * @param NAME
	 */
	public void setNAME_FORMATTED(String NAME_FORMATTED){
		this.NAME_FORMATTED = NAME_FORMATTED;
	}
	
	public String getNAME_FORMATTED(){
		return this.NAME_FORMATTED;
	}
	
	/**
	 * some times is same as NAME_FORMATTED, but SE phone does not support NAME_FORMATTED.
	 * @param NAME
	 */
	public void setNAME(String NAME){
		this.NAME = NAME;
	}
	
	public String getNAME(){
		return this.NAME;
	}
	
	public void setNAME_GIVEN(String NAME_GIVEN){
		this.NAME_GIVEN = NAME_GIVEN;
	}
	
	public String getNAME_GIVEN(){
		return this.NAME_GIVEN;
	}
	
	public void setNAME_FAMILY(String NAME_FAMILY){
		this.NAME_FAMILY = NAME_FAMILY;
	}
	
	public String getNAME_FAMILY(){
		return this.NAME_FAMILY;
	}

	public void setADDR(String ADDR){
		this.ADDR = ADDR;
	}
	
	public String getADDR(){
		return this.ADDR;
	}
	
	public void setADDR_COUNTRY(String ADDR_COUNTRY){
		this.ADDR_COUNTRY = ADDR_COUNTRY;
	}
	
	public String getADDR_COUNTRY(){
		return this.ADDR_COUNTRY;
	}
	
	public void setADDR_LOCALITY(String ADDR_LOCALITY){
		this.ADDR_LOCALITY = ADDR_LOCALITY;
	}
	
	public String getADDR_LOCALITY(){
		return this.ADDR_LOCALITY;
	}
	
	public void setADDR_POSTALCODE(String ADDR_POSTALCODE){
		this.ADDR_POSTALCODE = ADDR_POSTALCODE;
	}
	
	public String getADDR_POSTALCODE(){
		return this.ADDR_POSTALCODE;
	}
	
	public void setADDR_STREET(String ADDR_STREET){
		this.ADDR_STREET = ADDR_STREET;
	}
	
	public String getADDR_STREET(){
		return this.ADDR_STREET;
	}
	
/*	public void setATTR_HOME(String ATTR_HOME){
		this.ATTR_HOME = ATTR_HOME;
	}
	
	public String getATTR_HOME(){
		return this.ATTR_HOME;
	}*/
	
	public void setGeneralPhone(String GeneralPhone){
		this.GeneralPhone = GeneralPhone;
	}
	
	public String getGeneralPhone(){
		return this.GeneralPhone;
	}
	
/*	public void appendTEL(String telnumber){
		TEL.addElement(telnumber);
	}
	
	public void insertTELAt(String telnumber, int pos){
		TEL.insertElementAt(telnumber, pos);
	}
	
	public void setTEL(Vector TEL){
		this.TEL = TEL;
	}
	
	public Vector getTEL(){
		return this.TEL;
	}*/
	
	public Vector getItemContactExtends(){
		return this.ItemContactExtends;
	}
	
	public void setItemContactExtends(Vector ItemContactExtends){
		this.ItemContactExtends = ItemContactExtends;
	}
	
	public void addItemContactExtend(ItemContactExtend itemcontactextend){
		this.ItemContactExtends.addElement(itemcontactextend);
	}
	
	public void insertItemContactExtendAt(ItemContactExtend itemcontactextend, int pos){
		this.ItemContactExtends.insertElementAt(itemcontactextend, pos);
	}
	
	public void setEMAIL(String EMAIL){
		this.EMAIL = EMAIL;
	}
	
	public String getEMAIL(){
		return this.EMAIL;
	}
	
	public void setURL(String URL){
		this.URL = URL;
	}
	
	public String getURL(){
		return this.URL;
	}
	
	public void setNOTE(String NOTE){
		this.NOTE = NOTE;
	}
	
	public String getNOTE(){
		return this.NOTE;
	}
	
	public void setNICKNAME(String NICKNAME){
		this.NICKNAME = NICKNAME;
	}
	
	public String getNICKNAME(){
		return this.NICKNAME;
	}
	
	public void setORG(String ORG){
		this.ORG = ORG;
	}
	
	public String getORG(){
		return this.ORG;
	}
	
	public void setTITLE(String TITLE){
		this.TITLE = TITLE;
	}
	
	public String getTITLE(){
		return this.TITLE;
	}
	
	public void setBIRTHDAY(String BIRTHDAY){
		this.BIRTHDAY = BIRTHDAY;
	}
	
	public String getBIRTHDAY(){
		return this.BIRTHDAY;
	}
	
	public void setREVISION(String REVISION){
		this.REVISION = REVISION;
	}
	
	public String getREVISION(){
		return this.REVISION;
	}
	
	public void setPUBLIC_KEY_STRING(String PUBLIC_KEY_STRING){
		this.PUBLIC_KEY_STRING = PUBLIC_KEY_STRING;
	}
	
	public String getPUBLIC_KEY_STRING(){
		return this.PUBLIC_KEY_STRING;
	}
	
	public void setPHOTO_URL(String PHOTO_URL){
		this.PHOTO_URL = PHOTO_URL;
	}
	
	public String getPHOTO_URL(){
		return this.PHOTO_URL;
	}
	
	public void setPHOTO(byte[] PHOTO){
		this.PHOTO = PHOTO;
	}
	
	public byte[] getPHOTO(){
		return this.PHOTO;
	}
	
	public void setPUBLIC_KEY(byte[] PUBLIC_KEY){
		this.PUBLIC_KEY = PUBLIC_KEY;
	}
	
	public byte[] getPUBLIC_KEY(){
		return this.PUBLIC_KEY;
	}
	
	public void setisModified(boolean isModified){
		this.isModified = isModified;
	}
	public boolean getisModified(){
		return this.isModified;
	}
	
	public void sethasBackuped(boolean hasBackuped){
		this.hasBackuped = hasBackuped;
	}
	
	public boolean gethasBackuped(){
		return this.hasBackuped;
	}
}
