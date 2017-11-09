package net.mobabel.model;

import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStoreException;

import net.mobabel.item.ItemMessage;
import net.mobabel.util.UIController;
import net.mobabel.view.AlertFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


import de.enough.polish.util.Locale;

/**
 * Class Description: Set some public setting of classes, which were used
 * oftenly
 */
public class RecordStoreMessage {

	private static RecordStore RS;

	private String RECORDSTORE_NAME = "message";

	private UIController controller = null;

	public RecordStoreMessage(UIController controller)
	throws IOException, RecordStoreException {
		this.controller = controller;
		openRS();
	}

	/** Open RecordStore */
	private synchronized void openRS() throws IOException, RecordStoreException {
		//#debug debug
		System.out.println("Begin to load RecordStore");
		try {
			RS = RecordStore.openRecordStore(this.RECORDSTORE_NAME, true);
			if (RS.getNumRecords() == 0) {
				//#debug debug
				System.out.println("No record");
			}
		} catch (Exception e) {
			//#debug error
			System.out.println("Err openRS: " + e.getMessage());
		}
	}

	private synchronized boolean checkRSSize() throws IOException,
	RecordStoreException {
		//#debug debug
		System.out.println("Begin to load RecordStore");
		try {
			int size = RS.getSizeAvailable() / 1024;
			if (size >= 10) {
				//#debug debug
				System.out.println("Has RMS room");
				return true;
			} else {
				//#debug debug
				System.out.println("No RMS room");
				return false;
			}
		} catch (Exception e) {
			//#debug error
			System.out.println("Err checkRSSize: " + e.getMessage());
		}
		return false;
	}

	public synchronized void closeRS() {
		if (RS != null) {
			try {
				RS.closeRecordStore();
				RS = null;
			} catch (RecordStoreException e) {
				//#debug error
				System.out.println("Err closeRS: " + e.getMessage());
			}
		}
	}

	/*
	 * addRSb here will be used only once when the user first set the operation
	 * and initialize the recordstore
	 */
	public boolean addRS(ItemMessage message) {
		boolean hassize = false;
		try {
			hassize = checkRSSize();
		} catch (Exception e) {
			//#debug error
			System.out.println("Err checkRSSize: " + e.getMessage());
		}
		if (hassize) {
			byte data[] = null;
			ByteArrayOutputStream baos = null;
			DataOutputStream dos = null;
			try {
				baos = new ByteArrayOutputStream();
				dos = new DataOutputStream(baos);

				int id = RS.getNextRecordID();
				dos.writeByte(CommonFunc.unsignedByte(message.getStatus()));
				dos.writeUTF(message.getAddress());
				dos.writeLong(message.getTimestamb());
				dos.writeByte(CommonFunc.unsignedByte(message.getType()));
				dos.writeUTF(message.getText());
				dos.writeInt(message.getData().length);
				dos.write(message.getData());

				data = baos.toByteArray();
				//int sameid = hasSameRecord(message);
				RS.addRecord(data, 0, data.length);
			} catch (Exception e) {
				//#debug error
				System.out.println("Error: " + e.toString());
				return false;
			} finally {
				try{
					if(baos != null){
						baos.close();
					}
					if(dos != null){
						dos.close();
					}
				}catch(Exception ignore){
				}
			}

			return true;
		} else {
			//#debug error
			System.out.println("Error: there is no RMS room, can not add record! ");
			controller.showCurrent(AlertFactory.createErrorAlert(Locale.get("message.normsspace")));
			return false;
		}
	}

	public boolean updateRS(int id, ItemMessage message) {
		// #debug debug
		//System.out.println("update id: "+id);
		byte data[] = null;
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;
		try {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);

			//dos.writeShort(CommonFunc.unsignedShort(id));
			dos.writeByte(CommonFunc.unsignedByte(message.getStatus()));
			dos.writeUTF(message.getAddress());
			dos.writeLong(message.getTimestamb());
			dos.writeByte(CommonFunc.unsignedByte(message.getType()));
			dos.writeUTF(message.getText());
			dos.writeInt(message.getData().length);
			dos.write(message.getData());

			data = baos.toByteArray();

			RS.setRecord(id, data, 0, data.length);
		} catch (Exception e) {
			//#debug error
			System.out.println("Err in updateRS: " + e.toString());
			return false;
		}finally{
			try{
				if(baos != null){
					baos.close();
				}
				if(dos != null){
					dos.close();
				}
			}catch(Exception e){
				//ignore
			}
		}
		return true;
	}

	/**
	 * visit one Record
	 * 
	 */
	public ItemMessage getRS(int id) {
		// #debug debug
		//System.out.println("get id: "+id);
		ItemMessage message = new ItemMessage();
		byte data[] = null;
		ByteArrayInputStream bais = null;
		DataInputStream dis = null;
		try {
			if (RS.getNumRecords() > 0) {
				data = RS.getRecord(id);
				bais = new ByteArrayInputStream(data);
				dis = new DataInputStream(bais);

				message.setId(id);
				message.setStatus(dis.readUnsignedByte());
				message.setAddress(dis.readUTF());
				message.setTimestamb(dis.readLong());
				message.setType(dis.readUnsignedByte());
				message.setText(dis.readUTF());
				if(message.getType() == ItemMessage.TYPE_BinaryMessage){
					int length = dis.readInt();
					byte[] content = new byte[length];
					dis.readFully(content);
					message.setData(content);
				}
			}
		}catch (RecordStoreException ex) {
			//#debug error
			System.out.println("Error in getRS: " + ex.toString());
		}catch (Exception ex) {
			//#debug error
			System.out.println("Error in getRS: " + ex.toString());
		}finally{
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
		}

		return message;
	}

	/**
	 * if it is same record, return the id
	 * if not, return -1
	 * @param message
	 * @return
	 */
	public int hasSameRecord(ItemMessage message) {
		RecordEnumeration renum = null;
		try {
			renum = RS.enumerateRecords(null, null, false);
			while (renum.hasNextElement()){
				int i = renum.nextRecordId();
				if (CommonFunc.equalsIgnoreCase(getRS(i).getText(), message.getText())
						&& CommonFunc.equalsIgnoreCase(getRS(i).getAddress(), message.getAddress()) ){
					return i;
				}
			}
		} catch (RecordStoreException ex) {
			//#debug error
			System.out.println("Error in hasSameRecord: " + ex.getMessage());
		} finally {
			try {
				renum.destroy();
			} catch (Exception e) {
				//ignore
			}
		}
		return -1;
	}

	/*
	 * get all records in oder of time,used for history order
	 */
	public ItemMessage[] getAllRSTime(int status) {
		ItemMessage[] messages = new ItemMessage[0];
		RecordEnumeration renum = null;
		int ind = 0;
		StatusFilter filter = null;
		try {
			filter = new StatusFilter(status);
			renum = RS.enumerateRecords(filter, new RSTimeComparator(), false);

			//now is older first
			while (renum.hasNextElement()) {
				// previousRecordId also advances the record pointer, so we
				// have no
				// need to call previousRecord() as well.
				ind = renum.nextRecordId();
				// Retrieve the data from the next record
				// and make it the current record
				// Set up a byte array buffer and some helper streams
				// byte[] rec = new byte[RS.getRecordSize(ind)];
				// rec = RS.getRecord(ind);
				messages = CommonFunc.appendItemMessages(messages, getRS(ind));
			}

		} catch (RecordStoreException ex) {
			//#debug error
			System.out.println("Err1 in getAllRSTime: " + ex.getMessage());
		} finally {
			try {
				renum.destroy();
				filter = null;
			} catch (Exception e) {
				//ignore
			}
		}
		return messages;
	}

	/*
	 * get all records in oder of letter,used for letter order
	 */
	public ItemMessage[] getAllRSLetter() {
		ItemMessage[] messages = new ItemMessage[0];
		RecordEnumeration renum = null;
		int ind = 0;
		try {
			renum = RS.enumerateRecords(null, new RSLetterComparator(),
					false);
			if (renum != null) {
				while (renum.hasPreviousElement()) {
					// previousRecordId also advances the record pointer, so we
					// have no need to call previousRecord() as well.
					ind = renum.previousRecordId();
					// Retrieve the data from the next record
					// and make it the current record
					// Set up a byte array buffer and some helper streams
					// byte[] rec = new byte[RS.getRecordSize(ind)];
					// rec = RS.getRecord(ind);
					messages = CommonFunc.appendItemMessages(messages, getRS(ind));
				}
			} else {
				//#debug info
				System.out.println("renums is null");
			}
		} catch (RecordStoreException ex) {
			//#debug error
			System.out.println("Err in getAllRSLetter: " + ex.getMessage());
		} finally {
			try {
				renum.destroy();
			} catch (Exception e) {
				//ignore
			}

		}
		return messages;
	}

	/*
	 * get all records without order
	 */
	public ItemMessage[] getAllRS() {
		ItemMessage[] messages = new ItemMessage[0];
		RecordEnumeration renum = null;
		try {
			renum = RS.enumerateRecords(null, null, false);
			while (renum.hasNextElement()){
				messages = CommonFunc.appendItemMessages(messages, getRS(renum.nextRecordId()));
			}
		} catch (RecordStoreException ex) {
			//#debug error
			System.out.println("Error in getAllRS: " + ex.getMessage());
		} finally {
			try {
				renum.destroy();
			} catch (Exception e) {
				//ignore
			}
		}
		return messages;
	}

	private int getOldestRecord() {
		int index = 1;
		RecordEnumeration renum = null;
		try {
			renum = RS.enumerateRecords(null, new RSTimeComparator(), false);
			while (renum.hasPreviousElement()){
				//#debug info
				System.out.println("the oldest record id is: " + index);
				return renum.previousRecordId();
			}
		} catch (Exception e) {
			//#debug error
			System.out.println("Error in getOldestRecord: " + e.getMessage());
		}finally {
			try {
				renum.destroy();
			} catch (Exception e) {
				//ignore
			}
		}
		//#debug info
		System.out.println("the oldest record id is: " + index);
		return index;
	}

	public boolean deleteRS(ItemMessage message){
		return deleteRS(message.getId());
	}

	public boolean deleteRS(int id) {
		try {
			//#debug info
			System.out.println("Now delete record: " + id);
			RS.deleteRecord(id);
		} catch (Exception e) {
			//#debug error
			System.out.println("Error in deleteRS: " + e.getMessage());
			return false;
		}

		return true;
	}
}

/**
 * no special comparator, maybe ordered by id
 * @author leeglanz
 *
 */
class Comparator implements RecordComparator
{
	public int compare(byte[] record1, byte[] record2)
	{
		String string1 = new String(record1), 
		string2= new String(record2);
		int comparison = string1.compareTo(string2);
		if (comparison == 0)
			return RecordComparator.EQUIVALENT;
		else if (comparison < 0)
			return RecordComparator.PRECEDES;
		else
			return RecordComparator.FOLLOWS;
	}
}

class StatusFilter implements RecordFilter 
{
	private ByteArrayInputStream stream;
	private DataInputStream reader;
	private int status;
	public StatusFilter(int status_){
		this.status = status_;
	}
	public boolean matches(byte[] rec) 
	{
		stream = new ByteArrayInputStream(rec);
		reader = new DataInputStream(stream);

		int status_;
		try {
			status_ = reader.readUnsignedByte();
			//status = reader.readUTF();
			if(this.status == ItemMessage.STATUS_INBOX){
				if(status_ == ItemMessage.STATUS_READED || status_ == ItemMessage.STATUS_UNREAD  ) 
				{
					return true;
				}else{
					return false;
				}
			}else{
				if(status_ == this.status) 
				{
					return true;
				}else{
					return false;
				}
			}
		} catch (IOException e) {
			return false;
		}

	}
}
