package net.mobabel.view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import net.mobabel.item.ItemContact;
import net.mobabel.item.ItemGlobal;
import net.mobabel.item.ItemMessage;
import net.mobabel.model.CommonFunc;
import net.mobabel.model.ConnectionManager;
import net.mobabel.model.RecordStoreMessage;
import net.mobabel.model.RequestObserver;
import net.mobabel.model.Setting;
import net.mobabel.util.UIController;

import java.util.Vector;
import java.util.Enumeration;

import de.enough.polish.util.Locale;

/**
 * Screen for telephone book
 * copyright M-Way Solutions GmbH 2007
 * @author LiHui
 */
public class ScreenContactList extends Form implements CommandListener,
ItemCommandListener , RequestObserver{

	private UIController controller;

	private int count;

	private StringItem[] contactItems;

	private Displayable parent = null;

	//private Vector persons;

	RecordStoreMessage rsmsg = null;

	ConnectionManager connmanager = null;

	ItemContact[] itemcontacts;

	private TextField tel = null;

	private int type = ItemGlobal.getInstance().getScreenType();

	public static Command sendsmsCommand = new Command(Locale.get("cmd.sendsms"), Command.ITEM, 1);

	public static Command editCommand = new Command(Locale.get("cmd.edit"), Command.ITEM, 2);

	public static Command deleteCommand = new Command(Locale.get("cmd.delete"), Command.ITEM, 3);

	public ScreenContactList(UIController controller, Displayable parent) {
		//#style subScreen
		super(Locale.get("title.contact"));

		this.controller = controller;
		this.parent = parent;

		if(type == ItemGlobal.SCREEN_TYPE_DRAFT  || type == ItemGlobal.SCREEN_TYPE_COMPILER){
			//#style input
			tel= new TextField("", "+49", 20,TextField.PHONENUMBER);
			this.append(tel);
			tel.addCommand(UIController.continueCommand);
			tel.setItemCommandListener(this);  
		}else if(type == ItemGlobal.SCREEN_TYPE_CONTACTLIST){

		}

		/*		persons = readPIM();
		//if on phone does not allow to access PIM, the persons is null
		if(persons!=null){
			count = persons.size();
			personItems = new StringItem[count];

			for(int i=0;i<count;i++){
				String[] tem = (String[])persons.elementAt(i);
				//#style listDetailItem 
				personItems[i] = new StringItem(tem[0].trim(),tem[1].trim(),Item.LAYOUT_CENTER);
		    	this.append(personItems[i]);
		    	personItems[i].addCommand(UIController.selectCommand);
		    	personItems[i].setItemCommandListener(this);
			}
		}*/
	    //#if polish.api.pdaapi or polish.api.pimapi or polish.api.jsr75
			if(connmanager == null)
				connmanager = new ConnectionManager(this);
			connmanager.scanPIM();
		//#endif

		addCommand(UIController.backCommand);
		setCommandListener(this);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == UIController.backCommand) {
			controller.showCurrent(parent);
		}

	}

	public void commandAction(Command c, Item item) {
		if(type == ItemGlobal.SCREEN_TYPE_DRAFT  || type == ItemGlobal.SCREEN_TYPE_COMPILER){
			if(c == UIController.continueCommand){
				if(item == this.tel){
					if(!CommonFunc.isValidPhoneNumber(this.tel.getString())){
						controller.alert = AlertFactory.createErrorAlert("Invalid phone number");
						controller.showCurrent(controller.alert, this );
						return;
					}
					controller.itemMsgCompiling.setAddress(this.tel.getString());
					//send the sms
					if(connmanager == null)
						connmanager = new ConnectionManager(this);
					connmanager.sendSMS(controller.itemMsgCompiling);
				}
			}
			else if (c == UIController.selectCommand) {
				for(int i=0;i<count;i++){
					if (item == contactItems[i]) {
						//go back to ScreenRecommend
						try {
							//String[] tem = (String[])persons.elementAt(i);	
							controller.itemMsgCompiling.setAddress(itemcontacts[i].getGeneralPhone());
							//send the sms
							if(connmanager == null)
								connmanager = new ConnectionManager(this);
							connmanager.sendSMS(controller.itemMsgCompiling);

						} catch (Exception e) {
							//#debug error
							System.out.println("Error when send select one person: "+ e.toString());

						}

					}
				}
			}
		}else if(type == ItemGlobal.SCREEN_TYPE_CONTACTLIST){
			//show detail
			if (c == UIController.selectCommand) {

			}
			//go to message compiler
			else if (c == sendsmsCommand) {
				for(int i=0;i<count;i++){
					if (item == contactItems[i]) {
						try {
							ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_SEND_FROM_CONTACTLIST);
							controller.itemMsgCompiling.setAddress(itemcontacts[i].getGeneralPhone());
							controller.nextScreen = null;
							controller.nextScreen = new ScreenMsgWrite(controller, this);
							controller.showCurrent(controller.nextScreen);
						} catch (Exception e) {
							//#debug error
							System.out.println("Error when send select one person: "+ e.toString());

						}

					}
				}

			}
			else if (c == editCommand) {

			}
			else if (c == deleteCommand) {

			}

		}
	}

	/*public Vector readPIM() {
		try {
			Vector pim = new Vector(0, 1);
			ContactList cl = (ContactList) (PIM.getInstance().openPIMList(
					PIM.CONTACT_LIST, PIM.READ_ONLY));
			Enumeration en = cl.items();
			Vector c = new Vector(0, 1);
			while (en.hasMoreElements()) {
				c.addElement((Contact) (en.nextElement()));
			}
			System.out.println(c.size());
			for (int i = 0; i < c.size(); i++) {
				Contact temp = (Contact) (c.elementAt(i));
				if (cl.isSupportedField(Contact.TEL)) {
					String name = "", phone = "";
					if (cl.isSupportedField(Contact.FORMATTED_NAME)) {
						for (int m = 0; m < temp.countValues(Contact.FORMATTED_NAME); m++)
							name += " " + temp.getString(Contact.FORMATTED_NAME, m);
					}
					name += " ";

					//if one person has one more number
					for (int m = 0; m < temp.countValues(Contact.TEL); m++){
						phone = temp.getString(Contact.TEL, m);
						if(!phone.equals("") || phone!=null)
							pim.addElement(new String[] { name, phone });
					}					
				}
			}
			return pim;
		} catch (Exception e) {
			//#debug error
			System.out.println("Failed to read PIM: " + e);
			return null;
		}
	}*/

	public void onDownloadStart() {
		controller.loading = AlertFactory.createLoadingForm(controller, this, "", this);	
		controller.showCurrent(controller.loading);
	}

	public void onAborted(String reason) {
		//save the un-sended sms into rms
		controller.itemMsgCompiling.setStatus(ItemMessage.STATUS_OUTBOX);
		try {
			if(rsmsg == null)
				rsmsg = new RecordStoreMessage(controller);
			rsmsg.addRS(controller.itemMsgCompiling);
		} catch (Exception e) {
			controller.alert = AlertFactory.createErrorAlert(Locale.get("error.rmsexception")+e.toString());
			controller.showCurrent(controller.alert);
		}
		controller.alert = AlertFactory.createErrorAlert(reason);
		controller.showCurrent(controller.alert, controller.screenmain);

	}

	public void onBytesLoaded(int bytes) {	

	}

	public void onDownloadFinished(byte[] array) {	
		this.controller.showCurrent(this);
		type = ItemGlobal.getInstance().getScreenType();
		if(type == ItemGlobal.SCREEN_TYPE_COMPILER){
			controller.itemMsgCompiling.setStatus(ItemMessage.STATUS_SENDED);
			try {
				if(rsmsg == null)
					rsmsg = new RecordStoreMessage(controller);
				rsmsg.addRS(controller.itemMsgCompiling);
			} catch (Exception e) {
				controller.alert = AlertFactory.createErrorAlert(Locale.get("error.rmsexception")+e.toString());
				controller.showCurrent(controller.alert, controller.screenmain);
				return;
			}
			controller.alert = AlertFactory.createInfoAlert(Locale.get("message.sendsmssucc"), Setting.alertInfoTime);
			controller.showCurrent(controller.alert, controller.screenmain);
		}else if(type == ItemGlobal.SCREEN_TYPE_DRAFT){
			controller.itemMsgCompiling.setStatus(ItemMessage.STATUS_SENDED);
			try {
				if(rsmsg == null)
					rsmsg = new RecordStoreMessage(controller);
				rsmsg.updateRS(controller.itemMsgCompiling.getId(), controller.itemMsgCompiling);
			} catch (Exception e) {
				ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_DRAFT);
				controller.nextScreen = null;
				controller.nextScreen = new ScreenMessageList(controller, null);
				controller.alert = AlertFactory.createErrorAlert(Locale.get("error.rmsexception")+e.toString());
				controller.showCurrent(controller.alert, controller.nextScreen);
				return;
			}
			ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_DRAFT);
			controller.nextScreen = null;
			controller.nextScreen = new ScreenMessageList(controller, null);
			controller.alert = AlertFactory.createInfoAlert(Locale.get("message.sendsmssucc"), Setting.alertInfoTime);
			controller.showCurrent(controller.alert, controller.nextScreen);
		}
	}

	public void onProgressNotification(String message) {	

	}

	public void onScanFinished(ItemContact[] itemcontacts) {
		this.controller.showCurrent(this);
		this.itemcontacts = itemcontacts;
		count = this.itemcontacts.length;
		contactItems = new StringItem[count];

		//#debug debug
		System.out.println("contact number: "+ count);
		if(type == ItemGlobal.SCREEN_TYPE_DRAFT  || type == ItemGlobal.SCREEN_TYPE_COMPILER){
			for(int i=0;i<count;i++){
				// #debug debug
				//System.out.println("phone/address: "+ this.itemcontacts[i].getGeneralPhone()+this.itemcontacts[i].getNAME_FORMATTED());
				//#style contact
				contactItems[i] = new StringItem(itemcontacts[i].getNAME_FORMATTED(),
						itemcontacts[i].getGeneralPhone(),Item.LAYOUT_CENTER);
				this.append(contactItems[i]);
				contactItems[i].addCommand(UIController.selectCommand);
				contactItems[i].setItemCommandListener(this);
			}
		}else if(type == ItemGlobal.SCREEN_TYPE_CONTACTLIST){
			for(int i=0;i<count;i++){
				// #debug debug
				//System.out.println("phone/address: "+ this.itemcontacts[i].getGeneralPhone()+this.itemcontacts[i].getNAME_FORMATTED());
				//#style contact
				contactItems[i] = new StringItem(itemcontacts[i].getNAME_FORMATTED(),
						itemcontacts[i].getGeneralPhone(),Item.LAYOUT_CENTER);
				this.append(contactItems[i]);
				contactItems[i].addCommand(UIController.selectCommand);
				contactItems[i].addCommand(sendsmsCommand);
				contactItems[i].addCommand(editCommand);
				//TODO if is the sim contacts, no delete
				contactItems[i].addCommand(deleteCommand);
				contactItems[i].setItemCommandListener(this);
			}
		}
	}

	public void stop() {
		connmanager.stopThread();
	}

}
