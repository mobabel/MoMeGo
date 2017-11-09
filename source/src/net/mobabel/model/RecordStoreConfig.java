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

import net.mobabel.item.ItemOption;
import net.mobabel.model.Setting;
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
public class RecordStoreConfig {

	private static RecordStore operation_RS = null;

	private UIController controller = null;

	public static String RECORDSTORE_OPERATIONNAME = "dictoperation";

	public RecordStoreConfig(UIController controller) throws IOException,RecordStoreException {
		//#debug debug
		System.out.println("Set dictrecord Constructor");
		this.controller = controller;
		openRS();
	}

	/** Open RecordStore, if not then create new */
	private synchronized void openRS() throws IOException, RecordStoreException {
		try {
			operation_RS = RecordStore.openRecordStore(
					RECORDSTORE_OPERATIONNAME, true);
			//TODO delete later, only for test
			//deleteDatabase();
			if (operation_RS.getNumRecords() == 0) {
				//#debug debug
				System.out.println("No record");
				// If these are no record, load the default record of server
				DefaultRS();
			} else {
				//#debug debug
				System.out.println("Find record");
			}
		} catch (Exception e) {
			//#debug error
			System.out.println("Err openRS: "+ e.getMessage());
		}
	}

	// Close RecordStore
	public synchronized void closeRS() {
		if (operation_RS != null) {
			try {
				operation_RS.closeRecordStore();
				operation_RS = null;
			} catch (RecordStoreException ex) {
				//#debug error
				System.out.println("Error in closeRS: " + ex.getMessage());
			}
		}
	}

	/*
	 * At the beginning of loading, if there are no record in the recordstore,we
	 * can add some default operation to it.
	 */
	private void DefaultRS() throws IOException, RecordStoreException {
		System.out.println("load the default operation");
		byte data[] = null;
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;
		try {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);

			dos.writeBoolean(Setting.btips);
			dos.writeInt(Setting.resultmax);
			dos.writeInt(Setting.approlevel);
			dos.writeBoolean(Setting.dbarray0);
			dos.writeBoolean(Setting.dbarray1);
/*			dos.writeBoolean(Setting.dbarray2);
			dos.writeBoolean(Setting.lang0);
			dos.writeBoolean(Setting.lang1);
			dos.writeBoolean(Setting.lang2);*/
			dos.writeBoolean(Setting.net0);
			dos.writeBoolean(Setting.net1);
			dos.writeUTF(CommonFunc.writeMachineCode());
			dos.writeBoolean(Setting.bRegister);

			data = baos.toByteArray();
			
			operation_RS.addRecord(data, 0, data.length);
		} catch (Exception e) {
			//#debug error
			System.out.println("Er in DefaultRS: " + e.getMessage());
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

	/** Add Record */

	/*
	 * addRSb here will be used only once when the user first set the operation
	 * and initialize the recordstore
	 */
	public boolean addRSb(ItemOption operitem) {
		if(operation_RS == null) return false;
		byte data[] = null;
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;
		try {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);

			dos.writeBoolean(operitem.gettips());
			dos.writeInt(operitem.getresultmax());
			dos.writeInt(operitem.getapprolevel());
			dos.writeBoolean(operitem.getdbarray0());
			dos.writeBoolean(operitem.getdbarray1());
/*			dos.writeBoolean(operitem.getdbarray2());
			dos.writeBoolean(operitem.getlang0());
			dos.writeBoolean(operitem.getlang1());
			dos.writeBoolean(operitem.getlang2());*/
			dos.writeBoolean(operitem.getnet0());
			dos.writeBoolean(operitem.getnet1());
			dos.writeUTF(operitem.getMachineCode());
			dos.writeBoolean(operitem.getRegister());

			data = baos.toByteArray();

			operation_RS.addRecord(data, 0, data.length);
			return true;
		} catch (Exception e) {
			//#debug error
			System.out.println("Error1 in addRSb: " + e.getMessage());
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
			closeRS();
		}
	}

	/** update the Record */  
	public boolean updateRS(ItemOption operitem) {
		byte data[] = null;
		ByteArrayOutputStream baos = null;
		DataOutputStream dos = null;
		try {
			baos = new ByteArrayOutputStream();
			dos = new DataOutputStream(baos);

			dos.writeBoolean(operitem.gettips());
			dos.writeInt(operitem.getresultmax());
			dos.writeInt(operitem.getapprolevel());
			dos.writeBoolean(operitem.getdbarray0());
			dos.writeBoolean(operitem.getdbarray1());
/*			dos.writeBoolean(operitem.getdbarray2());
			dos.writeBoolean(operitem.getlang0());
			dos.writeBoolean(operitem.getlang1());
			dos.writeBoolean(operitem.getlang2());*/
			dos.writeBoolean(operitem.getnet0());
			dos.writeBoolean(operitem.getnet1());
			dos.writeUTF(operitem.getMachineCode());
			dos.writeBoolean(operitem.getRegister());

			data = baos.toByteArray();

			// only the first record was used
			operation_RS.setRecord(1, data, 0, data.length);
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
			closeRS();
		}
	}

	/**
	 * visit one Record id==1, because only the first record will be used always
	 */
	public ItemOption getRS() {
		ItemOption operitem = new ItemOption();
		ByteArrayInputStream bais = null;
		DataInputStream dis = null;
		byte data[] = null;
		try {
			if (operation_RS.getNumRecords() > 0) {
				data = operation_RS.getRecord(1);
				bais = new ByteArrayInputStream(data);
				dis = new DataInputStream(bais);

				operitem.settips(dis.readBoolean());
				operitem.setresultmax(dis.readInt());
				operitem.setapprolevel(dis.readInt());
				operitem.setdbarray0(dis.readBoolean());
				operitem.setdbarray1(dis.readBoolean());
		/*		operitem.setdbarray2(dis.readBoolean());
				operitem.setlang0(dis.readBoolean());
				operitem.setlang1(dis.readBoolean());
				operitem.setlang2(dis.readBoolean());*/
				operitem.setnet0(dis.readBoolean());
				operitem.setnet1(dis.readBoolean());
				operitem.setMachineCode(dis.readUTF());
				operitem.setRegister(dis.readBoolean());
			}
		} catch (RecordStoreException ex) {
			//#debug error
			System.out.println("Error in getRS:"  + ex.getMessage());
		}catch (Exception e) {
			//#debug error
			System.out.println("Error in getRS:"  + e.getMessage());
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

		return operitem;
	}

	public boolean deleteRSb(int id) {
		try {
			System.out.println("Now delete record: " + id);
			operation_RS.deleteRecord(id);
			return true;
		} catch (Exception e) {
			//#debug error
			System.out.println("Error in deleteRSb:"  + e.getMessage());
			return false;
		}
	}

	public boolean deleteDatabase() {
		try {
			//#debug debug
			System.out.println("Now delete database");
			if(operation_RS!=null){
				closeRS();
			}
			RecordStore.deleteRecordStore(RECORDSTORE_OPERATIONNAME);
			return true;
		} catch (Exception e) {
			//#debug error
			System.out.println("Error in deleteDatabase:"  + e.getMessage());
			return false;
		}
	}

}
