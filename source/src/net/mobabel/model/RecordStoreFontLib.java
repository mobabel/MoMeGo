/*
 * Created on 2005-2-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.mobabel.model;

import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

import net.mobabel.util.UIController;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Class Description: Set some public setting of classes, which were used
 * oftenly
 */
public class RecordStoreFontLib {

	private static RecordStore RS = null;

	private UIController controller = null;

	public static String RECORDSTORE_NAME = "dictfontlib";

	public static int BLOCK_LENGTH = 100;
	
	//TODO only for chinese font
	public static int RECORDSTORE_MINSIZE = 100000; //>94300 ,100k

	public RecordStoreFontLib(UIController controller) throws IOException,RecordStoreException {
		this.controller = controller;
		openRS();
	}

	/**
	 * 
	 * @throws IOException
	 * @throws RecordStoreException
	 */
	private synchronized void openRS() throws IOException, RecordStoreException {
		try {
			RS = RecordStore.openRecordStore(
					RECORDSTORE_NAME, true);
		} catch (Exception e) {
			//#debug error
			System.out.println("Err openRS: "+ e.getMessage());
		}
	}

	/**
	 * Close RecordStore
	 *
	 */
	public synchronized void closeRS() {
		if (RS != null) {
			try {
				RS.closeRecordStore();
				RS = null;
			} catch (RecordStoreException ex) {
				//#debug error
				System.out.println("Error in closeRS: " + ex.getMessage());
			}
		}
	}
	
	public boolean hasStorageSpace(){
		try {
			//#debug debug
			System.out.println("space available: "+RS.getSizeAvailable());
			if(RS.getSizeAvailable() >= RECORDSTORE_MINSIZE)
				return true;
			else 
				return false;
		} catch (RecordStoreNotOpenException e) {
			//#debug error
			System.out.println("Error in hasStorageSpace: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * store each BLOCK_LENGTH(here is 100 chars) in one record
	 * @param datablock: font dot byte data depends on offsetsize x BLOCK_LENGTH x blocknum
	 * @param blocknum: if defaultLength(18x100x4) is not equal to datablock length(maybe less than defaultLength), 
	 * which means that is the end of fotdotlib file
	 * @return
	 */
	public void addFontBlock(byte[] datablock, int blocknum) {
		byte data[] = null;
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;
		try {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);
			int defaultLength = UnicodeFunc.FONT_OFFSET_SIZE * RecordStoreFontLib.BLOCK_LENGTH * blocknum;
			if(defaultLength == datablock.length){
				int charcount = BLOCK_LENGTH * blocknum;
				for(int i = 0;i < charcount; i++){
					dos.write(datablock, i*UnicodeFunc.FONT_OFFSET_SIZE, UnicodeFunc.FONT_OFFSET_SIZE);
					data = baos.toByteArray();
					if(i%BLOCK_LENGTH == BLOCK_LENGTH-1){
						RS.addRecord(data, 0, data.length);
						baos = null;
						dos = null;
						baos = new ByteArrayOutputStream();
						dos = new DataOutputStream(baos);
					}
				}
			}
			//end file, the last block
			else{
				int charcount = datablock.length / UnicodeFunc.FONT_OFFSET_SIZE;
				int sumblockcount = charcount%BLOCK_LENGTH==0?charcount/BLOCK_LENGTH:((charcount-charcount%BLOCK_LENGTH)/BLOCK_LENGTH +1);
				int blockcount = 0;
				for(int i=0;i<charcount;i++){
					dos.write(datablock, i*UnicodeFunc.FONT_OFFSET_SIZE, UnicodeFunc.FONT_OFFSET_SIZE);
					data = baos.toByteArray();

					if(i%BLOCK_LENGTH == BLOCK_LENGTH-1){
						blockcount++;
						// #debug debug
						//System.out.println("data length: "+data.length);
						RS.addRecord(data, 0, data.length);
						baos = null;
						dos = null;
						baos = new ByteArrayOutputStream();
						dos = new DataOutputStream(baos);
						// #debug debug
						//System.out.println("blockcount: "+blockcount);
						if(blockcount == sumblockcount-1){
							//if last block is complete block
							if(charcount%BLOCK_LENGTH == 0 ){
								dos.writeInt(BLOCK_LENGTH);
							}else{
								// #debug debug
								//System.out.println("left: "+charcount%BLOCK_LENGTH);
								dos.writeInt(charcount%BLOCK_LENGTH);
							}
						}
					}else if(i == charcount-1){
						blockcount++;
						// #debug debug
						//System.out.println("last data length: "+data.length);
						RS.addRecord(data, 0, data.length);
						// #debug debug
						//System.out.println("blockcount: "+blockcount);
					}
				}
			}
			
		} catch (Exception e) {
			//#debug error
			System.out.println("Err in addFontBlock: " + e.getMessage());
		}finally{
			try{
				if(baos != null){
					baos.close();
				}
				if(dos != null){
					dos.close();
				}
				data = null;
			}catch(Exception e){
				//ignore
			}
		}
	}

	/**
	 * import the whole fontdot library
	 * @param fontlib
	 * @return true if successfully import fontdotlib into database
	 * @bug, the last fontdot block has problem by rending, maybe the block cut is wrong?
	 */
	/*public boolean addRS(byte[] fontlib) {
		byte data[] = null;
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;
		try {
			//#debug debug
			System.out.println("space available before: "+RS.getSizeAvailable());
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);
			int charcount = fontlib.length / UnicodeFunc.FONT_OFFSET_SIZE;//12x12/8->5170 char
			int sumblockcount = charcount%BLOCK_LENGTH==0?charcount/BLOCK_LENGTH:((charcount-charcount%BLOCK_LENGTH)/BLOCK_LENGTH +1);
			int blockcount = 0;
			//#debug debug
			System.out.println("charcount: "+charcount);
			for(int i=0;i<charcount;i++){
				dos.write(fontlib, i*UnicodeFunc.FONT_OFFSET_SIZE, UnicodeFunc.FONT_OFFSET_SIZE);
				data = baos.toByteArray();

				if(i%BLOCK_LENGTH == BLOCK_LENGTH-1){
					blockcount++;
					// #debug debug
					//System.out.println("data length: "+data.length);
					RS.addRecord(data, 0, data.length);
					baos = null;
					dos = null;
					baos = new ByteArrayOutputStream();
					dos = new DataOutputStream(baos);
					// #debug debug
					//System.out.println("blockcount: "+blockcount);
					if(blockcount == sumblockcount-1){
						//if last block is complete block
						if(charcount%BLOCK_LENGTH == 0 ){
							dos.writeInt(BLOCK_LENGTH);
						}else{
							// #debug debug
							//System.out.println("left: "+charcount%BLOCK_LENGTH);
							dos.writeInt(charcount%BLOCK_LENGTH);
						}
					}
				}else if(i == charcount-1){
					blockcount++;
					// #debug debug
					//System.out.println("last data length: "+data.length);
					RS.addRecord(data, 0, data.length);
					// #debug debug
					//System.out.println("blockcount: "+blockcount);
				}
			}
			//#debug debug
			System.out.println("space available end: "+RS.getSizeAvailable());
			return true;
		} catch (Exception e) {
			//#debug error
			System.out.println("Err in addRSb: " + e.getMessage());
			return false;
		}
		finally{
			try{
				if(baos != null){
					baos.close();
				}
				if(dos != null){
					dos.close();
				}
				data = null;
			}catch(Exception e){
				//ignore
			}
		}
	}*/

	/**
	 * update the whole font dot library
	 * @param fontlib
	 * @return
	 */
/*	public boolean updateRS(byte[] fontlib) {
		byte data[] = null;
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;
		try {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);

			int charcount = fontlib.length / UnicodeFunc.FONT_OFFSET_SIZE;//12x12->5170 char
			int sumblockcount = charcount%BLOCK_LENGTH==0?charcount/BLOCK_LENGTH:((charcount-charcount%BLOCK_LENGTH)/BLOCK_LENGTH +1);
			int blockcount = 0;
			for(int i=0;i<charcount;i++){			
				dos.write(fontlib, i*UnicodeFunc.FONT_OFFSET_SIZE, UnicodeFunc.FONT_OFFSET_SIZE);
				data = baos.toByteArray();

				if(i%BLOCK_LENGTH == BLOCK_LENGTH-1){
					blockcount++;
					RS.setRecord(blockcount, data, 0, data.length);
					baos = null;
					dos = null;
					baos = new ByteArrayOutputStream();
					dos = new DataOutputStream(baos);
					// #debug debug
					//System.out.println("blockcount: "+blockcount);
					if(blockcount == sumblockcount-1){
						//if last block is complete block
						if(charcount%BLOCK_LENGTH == 0 ){
							dos.writeInt(BLOCK_LENGTH);
						}else{
							dos.writeInt(charcount%BLOCK_LENGTH);
						}
					}
				}else if(i == charcount-1){
					blockcount++;
					RS.setRecord(blockcount, data, 0, data.length);
					// #debug debug
					//System.out.println("blockcount: "+blockcount);
				}
			}
			return true;
		} catch (Exception e) {
			//#debug error
			System.out.println("Err in updateRS: " + e.getMessage());
			return false;
		}finally{
			try{
				if(baos != null){
					baos.close();
				}
				if(dos != null){
					dos.close();
				}
				data = null;
			}catch(Exception e){
				//ignore
			}
		}
	}*/

	/**
	 * get the byte array for one char
	 * @param char_
	 * @return
	 */
	public byte[] getCharByte(byte[] char_) {
		ByteArrayInputStream bais = null;
		DataInputStream dis = null;
		byte data[] = null;
		byte[] charbyte = new byte[UnicodeFunc.FONT_OFFSET_SIZE];
		try {
			int q1 = char_[0] & 0xff;
			int q2 = char_[1] & 0xff;		
			int offset = ((q1 - 0xa1) * 94 + (q2 - 0xa1)) * UnicodeFunc.FONT_OFFSET_SIZE;
			// #debug debug
			//System.out.println("offset: "+offset);

			int blockpointer = (offset/UnicodeFunc.FONT_OFFSET_SIZE)/BLOCK_LENGTH + 1;			
			// #debug debug
			//System.out.println("blockpointer: "+blockpointer);

			int charpos = (offset/UnicodeFunc.FONT_OFFSET_SIZE)%BLOCK_LENGTH + 1;
			// #debug debug
			//System.out.println("charpos: "+charpos);

			data = RS.getRecord(blockpointer);
			bais = new ByteArrayInputStream(data);
			dis = new DataInputStream(bais);
			for(int i=0;i<charpos; i++){
				dis.read(charbyte);
			}

		} catch (RecordStoreException ex) {
			//#debug error
			System.out.println("Error in getCharByte:"  + ex.getMessage());
		}catch (Exception e) {
			//#debug error
			System.out.println("Error in getCharByte:"  + e.getMessage());
		}finally{
			try{
				if(bais != null){
					bais.close();
				}
				if(dis != null){
					dis.close();
				}
				data = null;
			}catch(Exception e){
				//ignore
			}
		}

		return charbyte;
	}

	/**
	 * get the whole font dot library
	 * @return
	 */
/*	public byte[] getAllFontDotLib() {
		byte[] fontlib = new byte[0];
		byte data[] = null;
		byte charbyte[] = new byte[UnicodeFunc.FONT_OFFSET_SIZE];
		ByteArrayInputStream bais = null;
		DataInputStream dis = null;
		try {
			int blockcount = RS.getNumRecords();
			for(int i=0;i<blockcount; i++){
				data = RS.getRecord(i+1);
				// #debug debug
				//System.out.println("data length: "+i+" "+data.length);
				bais = new ByteArrayInputStream(data);
				dis = new DataInputStream(bais);

				if(i != blockcount-1){
					for(int j=0;j<BLOCK_LENGTH;j++){
						dis.read(charbyte);
						fontlib = CommonFunc.appendByte(fontlib, charbyte);
					}
				}else{
					int charcountinlastblock = dis.readInt();
					for(int j=0;j<charcountinlastblock;j++){
						dis.read(charbyte);
						fontlib = CommonFunc.appendByte(fontlib, charbyte);
					}
				}
			}
		} catch (RecordStoreException ex) {
			//#debug error
			System.out.println("Error in getAllFontDotLib:"  + ex.getMessage());
		}catch (Exception e) {
			//#debug error
			System.out.println("Error in getAllFontDotLib:"  + e.getMessage());
		}finally{
			try{
				try{
					if(bais != null){
						bais.close();
					}
					if(dis != null){
						dis.close();
					}
				}catch(Exception e){
					//ignore
				}
				data = null;
			}catch(Exception e){
				//ignore
			}
		}
		//#debug debug
		System.out.println("fontlib sum length: "+fontlib.length);
		return fontlib;
	}*/

	/**
	 * delete one recorde by given id
	 * @param id
	 * @return
	 */
	public boolean deleteRS(int id) {
		try {
			System.out.println("Now delete record: " + id);
			RS.deleteRecord(id);
			return true;
		} catch (Exception e) {
			//#debug error
			System.out.println("Error in deleteRS:"  + e.getMessage());
			return false;
		}
	}

	/**
	 * drop the database
	 * @return
	 */
	public boolean dropDatabase() {
		try {
			//#debug debug
			System.out.println("Now drop database");
			if(RS!=null){
				closeRS();
			}
			RecordStore.deleteRecordStore(RECORDSTORE_NAME);
			//then open new one
			openRS();
			return true;
		} catch (Exception e) {
			//#debug error
			System.out.println("Error in dropDatabase:"  + e.getMessage());
			return false;
		}
	}

}
