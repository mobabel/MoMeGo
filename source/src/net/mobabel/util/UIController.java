/*
 * Created on 2006-9-18
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.mobabel.util;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

import net.mobabel.item.ItemMessage;
import net.mobabel.item.ItemOption;
import net.mobabel.item.ItemUTF8Lib;
import net.mobabel.model.RecordStoreConfig;

import net.mobabel.midlet.Momego;
import net.mobabel.model.CommonFunc;
import net.mobabel.model.InputMethodUtf8;
import net.mobabel.model.MessageWatcher;
import net.mobabel.model.RecordStoreFontLib;
import net.mobabel.model.Setting;
import net.mobabel.model.UnicodeFunc;
import net.mobabel.view.ScreenMain;
import com.java4ever.apime.io.GZIP;

import de.enough.polish.util.Locale;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.Item;


/**
 * @author Administrator
 */
public class UIController {
	private Momego midlet = null;
	public ScreenMain screenmain = null;
	private RecordStoreConfig   dictRecords = null;
	public Screen nextScreen = null;

	private ItemOption itemoption = null;
	public InputMethodUtf8 imutf8 = null;

	private Display display = null;

	public static Command okCommand = new Command(Locale.get("cmd.ok"),Command.OK, 1);

	public static Command selectCommand = new Command(Locale.get("cmd.select"), Command.ITEM, 0);
	
	public static Command continueCommand = new Command(Locale.get("cmd.continue"), Command.CANCEL, 0);

	public static Command backCommand = new Command(Locale.get("cmd.back"),Command.BACK,2);
	
	public static Command cancelCommand = new Command(Locale.get("cmd.cancel"),Command.CANCEL, 0);
	
	public static Command saveCommand = new Command(Locale.get("cmd.save"), Command.ITEM, 1);
	
	public static Command replyCommand = new Command(Locale.get("cmd.reply"), Command.ITEM, 2);
	
	public static Command readCommand = new Command(Locale.get("cmd.read"), Command.ITEM, 0);

	public static ItemUTF8Lib itemutf8lib = ItemUTF8Lib.getInstance();

	private boolean hasStorageSpace4Fontdotlib = false;
	
	public Alert alert = null;
	
	public Form loading = null;
	
	public ItemMessage itemMsgComing = new ItemMessage();
	
	public ItemMessage itemMsgCompiling = new ItemMessage();
	
	MessageWatcher msgwatcher = null;

	public UIController(Momego midlet){
		this.midlet = midlet;
		this.display = midlet.display;
	}

	public void init(){
		try {
			this.initializeOption(null);
			RecordStoreFontLib rsfontlib = new RecordStoreFontLib(this);
			itemutf8lib.setRSFontLib(rsfontlib);

			//#if ${cfg.osHasFontAlready} !=true
			this.readTableUtf8LocalCode();		
			//#endif	

			//initialize option, check there is fontdotlib in database
			if(!itemoption.getHasStoredFontdotlib()){
				//#debug debug
				System.out.println("has not stored font ");
				//if has space in database, ready for import fontdotlib
				if(rsfontlib.hasStorageSpace()){
					hasStorageSpace4Fontdotlib = true;
				}
				//#if  ${cfg.osHasFontAlready} !=true
				this.readFontDotLib();			
				//#else			
				this.simulateReadFile();
				//#endif	
			}else{
				//#debug debug
				System.out.println("has stored font ");
				itemutf8lib.setUseFontDotLibInMemory(false);
				//#if ${cfg.osHasFontAlready} !=true
				//do nothing
				//#else			
				this.simulateReadFile();
				//#endif	
			}
			
		} catch (Exception e) {
			//#debug error
			System.out.println("Error in init: " + e.toString());
		}

		this.imutf8 = new InputMethodUtf8();

		screenmain = new ScreenMain(this);
		showCurrent(screenmain);
        msgwatcher = new MessageWatcher(this);
        msgwatcher.run();
	}


	/**
	 * initialize the option setting, if not existes, get from record store
	 * if yes, refresh by given value
	 * @param itemoption
	 */
	public synchronized void initializeOption(ItemOption itemoption)
	{					
		try{
			if(itemoption == null){
				dictRecords = null;
				dictRecords = new RecordStoreConfig(this);
				this.itemoption = dictRecords.getRS();
			}else{
				this.itemoption = itemoption;
			}
			this.setItemOption(this.itemoption);
		}catch(Exception e){
			//#debug error
			System.out.println("Error in initializeOption: " + e.getMessage());
		}finally{
			//dictRecords.closeRS();
		}
	}

	/**
	 * simulate reading file for loading gauge
	 *
	 */
	private void simulateReadFile(){
		int size = 5000;
		int step = size/100;
		for (int j = 0; j < size; j++) {            
			if(j%step==0)
				this.midlet.update();
		}
	}

	/**
	 * 
	 *
	 */
	private void readTableUtf8LocalCode(){
		Hashtable tableUtf8LocalCode = new Hashtable();
		int size = 0;
		String utf8 = null, gbcode;
		byte[] buf = null;
		byte[] gbcodebyte = new byte[2];
		ItemUTF8Lib.getInstance().inputstream = null;
		try { 
			ItemUTF8Lib.getInstance().inputstream = getClass().getResourceAsStream(Setting.FileGbcodeTable);
			//#= ItemUTF8Lib.getInstance().inputstream.skip(Setting.keyLength);
			int count ;
			byte[] countbyte = new byte[4];
			ItemUTF8Lib.getInstance().inputstream.read(countbyte);
			count = UnicodeFunc.byteArrayToInt(countbyte);
			buf = CommonFunc.toByteArray(ItemUTF8Lib.getInstance().inputstream);
			buf = GZIP.inflate(buf);
			size = buf.length;
			//System.out.println("size: " + size + " / "+count);
			/**
			 * 18783 = 2 + 6(4+2) + 5(3+2)x3755 (yes, this size is the size in winrar, why??)
			 * 22539 = 1 + 8(6+2) + 6(4+2)x3755 (not)
			 */
			//TODO, 12 is the section count of font12
			int step = count/(Momego.GAUGE_MAX-13);
			//skip the first utf-8 flag
			for (int i = 2; i < size; i++) {
				for(int j = 0; j < count; j++){
					if(j == 0){
						//the first square char is 4 byte, but should skip 1, why??
						utf8 = UnicodeFunc.getStringUtf8(buf, i+1, 3);
						i = i + 4;
					}else{
						utf8 = UnicodeFunc.getStringUtf8(buf, i, 3);
						i = i + 3;
						//System.out.println("i: " + i);
					}
					gbcodebyte = new byte[]{buf[i], buf[i+1]};
					i = i + 2;
					gbcode = UnicodeFunc.ByteArrayToHexString(gbcodebyte);
					tableUtf8LocalCode.put(utf8, gbcodebyte);
					//System.out.println("gbcode: " + gbcode);
					if(j%step==0)
						this.midlet.update();
				}
			}
/*			ByteArrayInputStream bis = new ByteArrayInputStream(buf);
			DataInputStream in = new DataInputStream(bis);
			System.out.println("count: " + count);
			byte[] gbcodebyte = new byte[2];
			for(int i = 0; i < count; i++){
				utf8 = in.readUTF();
				in.read(gbcodebyte);
				gbcode = UnicodeFunc.ByteArrayToHexString(gbcodebyte);
				tableUtf8LocalCode.put(utf8, gbcode);
				System.out.println("gbcode: " + gbcode);
			}*/
			itemutf8lib.setTableUtf8LocalCode(tableUtf8LocalCode);
		} catch (Exception e) { 
			//#debug error
			System.out.println("Error in readTableUtf8LocalCode: " +e.toString());
		}finally{
			try{
				if(ItemUTF8Lib.getInstance().inputstream != null){
					ItemUTF8Lib.getInstance().inputstream.close();
					ItemUTF8Lib.getInstance().inputstream = null;
				}
				buf = null;
				tableUtf8LocalCode = null;
			}catch(Exception e){
				//ignore
			}
			System.gc();
		}
	}

	public void readFontDotLib(){
		//#if ${cfg.osHasFontAlready} !=true
		ItemUTF8Lib.getInstance().inputstream  = null;
		ByteArrayOutputStream out = null;
		try {
			ItemUTF8Lib.getInstance().inputstream =getClass().getResourceAsStream(Setting.hanziDotLibFileName+"0");
			byte[] buffer = new byte[1];
			int read;
			while ( (read = ItemUTF8Lib.getInstance().inputstream.read(buffer, 0, 1)) != -1) {		
			}
			int fontfilecount = buffer[0];
			// #debug debug
			//System.out.println("fontfilecount: " + fontfilecount);


			int blockSize = UnicodeFunc.FONT_OFFSET_SIZE * RecordStoreFontLib.BLOCK_LENGTH;
			int blocknum = 4;
			//read 400 chars in each buffer
			int bufferSize = blockSize * blocknum; //18x100x4
			buffer = new byte[ bufferSize ];
			out = new ByteArrayOutputStream(bufferSize);
			for(int i = 0;i<fontfilecount;i++){
				ItemUTF8Lib.getInstance().inputstream = null;
				ItemUTF8Lib.getInstance().inputstream =getClass().getResourceAsStream(Setting.hanziDotLibFileName+(i+1));						
				while ( (read = ItemUTF8Lib.getInstance().inputstream.read(buffer, 0, bufferSize)) != -1) {
					if(hasStorageSpace4Fontdotlib){
						itemutf8lib.getRSFontLib().addFontBlock(buffer, blocknum);
					}else{
						out.write(buffer, 0, read);
					}
				}
				this.midlet.update();
			}
			if(hasStorageSpace4Fontdotlib){
				itemoption.setHasStoredFontdotlib(true);
				dictRecords.updateRS(itemoption);
				itemutf8lib.setUseFontDotLibInMemory(false);
			}
			else{
				itemutf8lib.setFontDotLib(out.toByteArray());
				itemutf8lib.setUseFontDotLibInMemory(true);
			}

		} catch (Exception e) {
			//#debug error
			System.out.println("error in load HanziDot file: " + e.toString());
		}finally{
			try{
				if(ItemUTF8Lib.getInstance().inputstream != null){
					ItemUTF8Lib.getInstance().inputstream.close();
					ItemUTF8Lib.getInstance().inputstream = null;
				}
				if(out != null){
					out.close();
					out = null;
				}
			}catch(Exception e){
				//ignore
			}
			System.gc();
		}
		//#else
			//TODO
			for(int i=0;i<13;i++)
				this.midlet.update();
		//#endif
	}

	public void setItemOption(ItemOption itemoption){
		this.itemoption = itemoption;
	}

	public ItemOption getItemOption(){
		return this.itemoption; 
	}

	public void showCurrent(Displayable disp){
		midlet.showCurrent(disp);
	}
	public void showCurrent(Alert alert, Displayable disp){
		midlet.showCurrent(alert, disp);
	}

	public void showMainScreen(){
		midlet.showCurrent(this.screenmain);
	}

	public void focusCurrentItem(Item item){
		this.display.setCurrentItem(item);
	}

	public void quit(){
		msgwatcher.quit();
		midlet.exit(false);
	}


}
