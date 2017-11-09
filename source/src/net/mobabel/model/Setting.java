package net.mobabel.model;

import net.mobabel.util.UIController;

public class Setting {

	private UIController    controller;
	private RecordStoreConfig   dictRecords;

	/** Set the start waiting time for 3 seconds*/
	public static final int StartWait_Time = 3000;

	/** Set the waiting time (ms)
	 * Set a long time to wait for the Getrequest*/
	public static final int GetrequestWait_Time = 20000;

	//#= public static final String Version = "${version}";

	public static String RECORDSTORE_USERBASENAME = "user";

	//#= public static int keyLength = ${cfg.keyLength};
	
	//#if polish.Vendor == BlackBerry or polish.Vendor == RIM
	//#= public static String SMS_PORT = "0";
	//#else
	public static String SMS_PORT = "50018";
	//#endif
	

	public static int limitationWord = 20;
	public static int limitationHistory = 20;
	public static int unlimitationWord = 999;
	public static int unlimitationHistory = 50;

	public static int alertInfoTime = 2000;//ms
	public static int alertWarningTime = 3000;//ms
	public static int alertMessageTime = 3000;//ms

	public static String registerkey = "lee";

	public static final String FileGbcodeTable = "/utfgb.dat";
	public static final String hanziDotLibFileName= "/fon12";
	public static final String FileNameInputTable = "/input.dat"; //for cn is pinyin

	public static boolean btips = true;
	public static int resultmax =6;
	public static int approlevel=0;
	public static boolean dbarray0 = true;//Basic Database
	public static boolean dbarray1 = false;//user
	public static boolean net0 = true;//other net
	public static boolean net1 = false;//China net
	public static boolean bRegister=false;

}
