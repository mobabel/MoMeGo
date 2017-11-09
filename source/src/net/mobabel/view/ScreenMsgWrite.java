package net.mobabel.view;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import net.mobabel.item.ItemContact;
import net.mobabel.item.ItemGlobal;
import net.mobabel.item.ItemOption;
import net.mobabel.item.ItemMessage;
import net.mobabel.util.UIController;
import net.mobabel.model.CommonFunc;
import net.mobabel.model.ConnectionManager;
import net.mobabel.model.RecordStoreMessage;
import net.mobabel.model.RequestObserver;
import net.mobabel.model.Setting;
//import net.mobabel.model.WaitInfo;

import de.enough.polish.ui.FramedForm;
import de.enough.polish.util.Locale;

public class ScreenMsgWrite extends Form implements CommandListener, ItemCommandListener,ItemStateListener, RequestObserver{

	private UIController    controller = null;
	private TextField tfContent = null;
	ConnectionManager connmanager = null;
	RecordStoreMessage rsmsg = null;
	Displayable parent = null;
	int maxlength = 70;
	
	private int type = ItemGlobal.getInstance().getScreenType();

	public ScreenMsgWrite(UIController controller, Displayable parent) {
		//#style subScreen
		super(Locale.get("title.smsWrite"));
		this.controller=controller;
		this.parent = parent;
		
		//1120 bits, 70 chars, 140 byte
		//#style input
		tfContent= new TextField("", "",maxlength,TextField.ANY);
		this.append(tfContent);
		tfContent.setItemCommandListener(this);  
		//this.focus(tfContent);	
		
		switch(type){
		case ItemGlobal.SCREEN_TYPE_INBOX:
			this.setTitle(Locale.get("title.smsInbox"));
			//show the sms and reset the unread status to read
			if(controller.itemMsgComing.getStatus() == ItemMessage.STATUS_UNREAD){
				try {
					controller.itemMsgComing.setStatus(ItemMessage.STATUS_READED);
					if(rsmsg == null)
						rsmsg = new RecordStoreMessage(controller);
					rsmsg.updateRS(controller.itemMsgComing.getId(), controller.itemMsgComing);
				} catch (Exception e) {
					controller.alert = AlertFactory.createErrorAlert(Locale.get("error.rmsexception")+e.toString());
					controller.showCurrent(controller.alert, this);
				}
			}
			
			//TODO show the contact detail
			tfContent.setLabel(controller.itemMsgComing.getAddress() +"\n" + CommonFunc.getDateString(controller.itemMsgComing.getTimestamb()));
			if(controller.itemMsgComing.getType() == ItemMessage.TYPE_TextMessage)
				tfContent.setString(controller.itemMsgComing.getText());
			else if(controller.itemMsgComing.getType() == ItemMessage.TYPE_BinaryMessage){
				//TODO
				tfContent.setString(controller.itemMsgComing.getText());
			}
			tfContent.setMaxSize(controller.itemMsgComing.getText().length());
			tfContent.setConstraints(TextField.UNEDITABLE);
			
			this.addCommand(UIController.replyCommand);	
			break;
		case ItemGlobal.SCREEN_TYPE_OUTBOX:
			this.setTitle(Locale.get("title.smsOutbox"));
			//TODO show the contact detail
			tfContent.setLabel(controller.itemMsgComing.getAddress() +"\n" + CommonFunc.getDateString(controller.itemMsgComing.getTimestamb()));
			if(controller.itemMsgComing.getType() == ItemMessage.TYPE_TextMessage)
				tfContent.setString(controller.itemMsgComing.getText());
			else if(controller.itemMsgComing.getType() == ItemMessage.TYPE_BinaryMessage){
				//TODO
				tfContent.setString(controller.itemMsgComing.getText());
			}
			tfContent.setMaxSize(maxlength);
			tfContent.setConstraints(TextField.ANY);
			this.addCommand(UIController.continueCommand);
			break;
		case ItemGlobal.SCREEN_TYPE_SENTMESSAGE:
			this.setTitle(Locale.get("title.smsSentMessages"));
			//TODO show the contact detail
			tfContent.setLabel(controller.itemMsgComing.getAddress() +"\n" + CommonFunc.getDateString(controller.itemMsgComing.getTimestamb()));
			if(controller.itemMsgComing.getType() == ItemMessage.TYPE_TextMessage)
				tfContent.setString(controller.itemMsgComing.getText());
			else if(controller.itemMsgComing.getType() == ItemMessage.TYPE_BinaryMessage){
				//TODO
				tfContent.setString(controller.itemMsgComing.getText());
			}
			tfContent.setMaxSize(controller.itemMsgComing.getText().length());
			tfContent.setConstraints(TextField.UNEDITABLE);
			break;
		case ItemGlobal.SCREEN_TYPE_SAVEDMESSAGE:
			this.setTitle(Locale.get("title.smsSavedMessages"));
			//TODO show the contact detail
			tfContent.setLabel(controller.itemMsgComing.getAddress() +"\n" + CommonFunc.getDateString(controller.itemMsgComing.getTimestamb()));
			if(controller.itemMsgComing.getType() == ItemMessage.TYPE_TextMessage)
				tfContent.setString(controller.itemMsgComing.getText());
			else if(controller.itemMsgComing.getType() == ItemMessage.TYPE_BinaryMessage){
				//TODO
				tfContent.setString(controller.itemMsgComing.getText());
			}
			tfContent.setMaxSize(controller.itemMsgComing.getText().length());
			tfContent.setConstraints(TextField.UNEDITABLE);
			break;
		case ItemGlobal.SCREEN_TYPE_DRAFT:
			this.setTitle(Locale.get("title.smsDraft"));
			tfContent.setLabel(CommonFunc.getDateString(controller.itemMsgComing.getTimestamb()));
			if(controller.itemMsgComing.getType() == ItemMessage.TYPE_TextMessage)
				tfContent.setString(controller.itemMsgComing.getText());
			else if(controller.itemMsgComing.getType() == ItemMessage.TYPE_BinaryMessage){
				//TODO
				tfContent.setString(controller.itemMsgComing.getText());
			}
			tfContent.setMaxSize(maxlength);
			tfContent.setConstraints(TextField.ANY);
			this.addCommand(UIController.continueCommand);
			break;
		case ItemGlobal.SCREEN_TYPE_TEMPLATE:
			this.setTitle(Locale.get("title.smsTemplates"));
			
			break;
		case ItemGlobal.SCREEN_TYPE_RECYCLE:
			this.setTitle(Locale.get("title.smsRecycle"));
			
			break;
		case ItemGlobal.SCREEN_TYPE_COMPILER:
			this.setTitle(Locale.get("title.smsWrite"));
			
			this.addCommand(UIController.continueCommand);
			break;
		case ItemGlobal.SCREEN_TYPE_REPLY_FROM_INBOX:
			this.setTitle(Locale.get("cmd.reply"));
			
			tfContent.setLabel(controller.itemMsgComing.getAddress());
			tfContent.setMaxSize(maxlength);
			tfContent.setConstraints(TextField.ANY);
			this.addCommand(UIController.continueCommand);
			break;
		case ItemGlobal.SCREEN_TYPE_SEND_FROM_CONTACTLIST:
			this.setTitle(Locale.get("title.smsWrite"));
			
			this.addCommand(UIController.continueCommand);	
		}

		this.addCommand(UIController.backCommand);	
		this.setItemStateListener(this);
		this.setCommandListener(this);

	}

	public void itemStateChanged(Item item) {
		if(item==tfContent){

		}
	}

	public void reset(){
		tfContent.setString("");
	}


	public void commandAction(Command c, Displayable dis){
		if(dis == this){
			if(c==UIController.backCommand){
				switch(type){
				case ItemGlobal.SCREEN_TYPE_INBOX:
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_INBOX);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenMessageList(controller, null);
					break;
				case ItemGlobal.SCREEN_TYPE_OUTBOX:
					controller.showCurrent(parent);
					break;
				case ItemGlobal.SCREEN_TYPE_SENTMESSAGE:
					controller.showCurrent(parent);
					break;
				case ItemGlobal.SCREEN_TYPE_SAVEDMESSAGE:
					controller.showCurrent(parent);
					break;
				case ItemGlobal.SCREEN_TYPE_DRAFT:
					
					break;
				case ItemGlobal.SCREEN_TYPE_TEMPLATE:
					
					break;
				case ItemGlobal.SCREEN_TYPE_RECYCLE:
					
					break;
				case ItemGlobal.SCREEN_TYPE_COMPILER:
					if(tfContent.getString().length() != 0){
						controller.itemMsgCompiling = new ItemMessage();
						controller.itemMsgCompiling.setType(ItemMessage.TYPE_TextMessage);
						controller.itemMsgCompiling.setText(tfContent.getString());
						controller.itemMsgCompiling.setTimestamb(System.currentTimeMillis());
						controller.itemMsgCompiling.setStatus(ItemMessage.STATUS_DRAFT);

						controller.alert = AlertFactory.createQuestionAlert(Locale.get("message.quitandsavemessage"));
						controller.showCurrent(controller.alert,this);
						controller.alert.setCommandListener(this);
					}else{
						controller.showMainScreen();
					}
					break;
				case ItemGlobal.SCREEN_TYPE_REPLY_FROM_INBOX:
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_INBOX);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenMessageList(controller, this);
					controller.showCurrent(controller.nextScreen);
					break;
				default:controller.showMainScreen();
				}

			}
			else if(c == UIController.replyCommand){
				switch(type){
				case ItemGlobal.SCREEN_TYPE_INBOX:
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_REPLY_FROM_INBOX);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenMsgWrite(controller, this);
					controller.showCurrent(controller.nextScreen);
					break;
				case ItemGlobal.SCREEN_TYPE_OUTBOX:
					
					break;
				case ItemGlobal.SCREEN_TYPE_SENTMESSAGE:
					
					break;
				case ItemGlobal.SCREEN_TYPE_SAVEDMESSAGE:
					
					break;
				case ItemGlobal.SCREEN_TYPE_DRAFT:
					
					break;
				case ItemGlobal.SCREEN_TYPE_TEMPLATE:
					
					break;
				case ItemGlobal.SCREEN_TYPE_RECYCLE:
					
					break;
				case ItemGlobal.SCREEN_TYPE_COMPILER:
					
					break;
				}
			}
			else if(c == UIController.continueCommand){
				if(tfContent.getString().length() == 0){
					controller.alert = AlertFactory.createWarningAlert(Locale.get("message.emptykeyword"));
					controller.showCurrent(controller.alert,this);
					return;
				}
				switch(type){
				case ItemGlobal.SCREEN_TYPE_COMPILER:
					controller.itemMsgCompiling = new ItemMessage();
					controller.itemMsgCompiling.setType(ItemMessage.TYPE_TextMessage);
					controller.itemMsgCompiling.setText(tfContent.getString());
					controller.itemMsgCompiling.setTimestamb(System.currentTimeMillis());

					controller.nextScreen = new ScreenContactList(controller, this);
					controller.showCurrent(controller.nextScreen);
					break;
				case ItemGlobal.SCREEN_TYPE_REPLY_FROM_INBOX:
					controller.itemMsgCompiling = new ItemMessage();
					//TODO
					controller.itemMsgCompiling.setType(ItemMessage.TYPE_TextMessage);
					controller.itemMsgCompiling.setAddress(controller.itemMsgComing.getAddress());
					controller.itemMsgCompiling.setText(tfContent.getString());
					controller.itemMsgCompiling.setTimestamb(System.currentTimeMillis());

					if(connmanager == null)
						connmanager = new ConnectionManager(this);
					connmanager.sendSMS(controller.itemMsgCompiling);
					break;
				case ItemGlobal.SCREEN_TYPE_OUTBOX:
					controller.itemMsgCompiling = controller.itemMsgComing;
					//maybe the content has been changed
					controller.itemMsgCompiling.setText(tfContent.getString());
					controller.itemMsgCompiling.setTimestamb(System.currentTimeMillis());

					if(connmanager == null)
						connmanager = new ConnectionManager(this);
					connmanager.sendSMS(controller.itemMsgCompiling);
					break;
				case ItemGlobal.SCREEN_TYPE_DRAFT:
					controller.itemMsgCompiling = controller.itemMsgComing;
					controller.itemMsgCompiling.setText(tfContent.getString());
					controller.itemMsgCompiling.setTimestamb(System.currentTimeMillis());

					controller.nextScreen = new ScreenContactList(controller, this);
					controller.showCurrent(controller.nextScreen);
					break;
				case ItemGlobal.SCREEN_TYPE_SEND_FROM_CONTACTLIST:
					controller.itemMsgCompiling.setText(tfContent.getString());
					controller.itemMsgCompiling.setTimestamb(System.currentTimeMillis());

					if(connmanager == null)
						connmanager = new ConnectionManager(this);
					connmanager.sendSMS(controller.itemMsgCompiling);
					break;
				}

			}
		}else if(dis == controller.alert){
			if(c == AlertFactory.yesCommand){
				try {
					if(rsmsg == null)
						rsmsg = new RecordStoreMessage(controller);
					controller.itemMsgCompiling.setStatus(ItemMessage.STATUS_DRAFT);
					rsmsg.addRS(controller.itemMsgCompiling);
				} catch (Exception e) {
					controller.alert = AlertFactory.createErrorAlert(Locale.get("error.rmsexception")+e.toString());
					controller.showCurrent(controller.alert);
				}
				controller.showMainScreen();
			}else if(c == AlertFactory.noCommand){
				controller.showMainScreen();
			}else if(c == AlertFactory.backCommand){
				controller.showCurrent(this);
			}
		}
	}

	public void commandAction(Command c, Item item) {

	}

	public void onDownloadStart() {
		controller.loading = AlertFactory.createLoadingForm(controller, this, "", this);	
		controller.showCurrent(controller.loading);
	}

	public void onAborted(String reason) {
		if(type == ItemGlobal.SCREEN_TYPE_REPLY_FROM_INBOX){			
			controller.itemMsgCompiling.setStatus(ItemMessage.STATUS_OUTBOX);
			try {
				if(rsmsg == null)
					rsmsg = new RecordStoreMessage(controller);
				rsmsg.addRS(controller.itemMsgCompiling);
			} catch (Exception e) {
				controller.alert = AlertFactory.createErrorAlert(Locale.get("error.rmsexception")+e.toString());
				controller.showCurrent(controller.alert);
			}
			ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_INBOX);
			controller.nextScreen = null;
			controller.nextScreen = new ScreenMessageList(controller, this);
			controller.alert = AlertFactory.createErrorAlert(reason);
			controller.showCurrent(controller.alert, controller.nextScreen);
		}else if(type == ItemGlobal.SCREEN_TYPE_OUTBOX){
			//fail to send again, just update the timestamb and content
			try {
				if(rsmsg == null)
					rsmsg = new RecordStoreMessage(controller);
				rsmsg.updateRS(controller.itemMsgCompiling.getId(), controller.itemMsgCompiling);
			} catch (Exception e) {
				controller.alert = AlertFactory.createErrorAlert(Locale.get("error.rmsexception")+e.toString());
				controller.showCurrent(controller.alert);
			}
			ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_OUTBOX);
			controller.nextScreen = null;
			controller.nextScreen = new ScreenMessageList(controller, this);
			controller.alert = AlertFactory.createErrorAlert(reason);
			controller.showCurrent(controller.alert, controller.nextScreen);
		}else if(type == ItemGlobal.SCREEN_TYPE_SEND_FROM_CONTACTLIST){
			controller.itemMsgCompiling.setStatus(ItemMessage.STATUS_OUTBOX);
			try {
				if(rsmsg == null)
					rsmsg = new RecordStoreMessage(controller);
				rsmsg.addRS(controller.itemMsgCompiling);
			} catch (Exception e) {
				controller.alert = AlertFactory.createErrorAlert(Locale.get("error.rmsexception")+e.toString());
				controller.showCurrent(controller.alert, parent);
				return;
			}
			ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_CONTACTLIST);
			controller.showCurrent(controller.alert, parent);
		}
	}

	public void onBytesLoaded(int bytes) {	

	}

	public void onDownloadFinished(byte[] array) {	
		this.controller.showCurrent(this);
		if(type == ItemGlobal.SCREEN_TYPE_REPLY_FROM_INBOX){
			controller.itemMsgCompiling.setStatus(ItemMessage.STATUS_SENDED);
			try {
				if(rsmsg == null)
					rsmsg = new RecordStoreMessage(controller);
				rsmsg.addRS(controller.itemMsgCompiling);
			} catch (Exception e) {
				ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_INBOX);
				controller.nextScreen = null;
				controller.nextScreen = new ScreenMessageList(controller, this);
				controller.alert = AlertFactory.createErrorAlert(Locale.get("error.rmsexception")+e.toString());
				controller.showCurrent(controller.alert, controller.nextScreen);
				return;
			}
			ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_INBOX);
			controller.nextScreen = null;
			controller.nextScreen = new ScreenMessageList(controller, this);
			controller.alert = AlertFactory.createInfoAlert(Locale.get("message.sendsmssucc"), Setting.alertInfoTime);
			controller.showCurrent(controller.alert, controller.nextScreen);
		}else if(type == ItemGlobal.SCREEN_TYPE_OUTBOX){
			//send succfully, just update the status
			controller.itemMsgCompiling.setStatus(ItemMessage.STATUS_SENDED);
			try {
				if(rsmsg == null)
					rsmsg = new RecordStoreMessage(controller);
				rsmsg.updateRS(controller.itemMsgCompiling.getId(), controller.itemMsgCompiling);
			} catch (Exception e) {
				ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_OUTBOX);
				controller.nextScreen = null;
				controller.nextScreen = new ScreenMessageList(controller, this);
				controller.alert = AlertFactory.createErrorAlert(Locale.get("error.rmsexception")+e.toString());
				controller.showCurrent(controller.alert);
				return;
			}
			ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_OUTBOX);
			controller.nextScreen = null;
			controller.nextScreen = new ScreenMessageList(controller, this);
			controller.alert = AlertFactory.createInfoAlert(Locale.get("message.sendsmssucc"), Setting.alertInfoTime);
		}else if(type == ItemGlobal.SCREEN_TYPE_SEND_FROM_CONTACTLIST){
			controller.itemMsgCompiling.setStatus(ItemMessage.STATUS_SENDED);
			try {
				if(rsmsg == null)
					rsmsg = new RecordStoreMessage(controller);
				rsmsg.addRS(controller.itemMsgCompiling);
			} catch (Exception e) {
				ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_CONTACTLIST);
				controller.showCurrent(controller.alert, parent);
				return;
			}
			ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_CONTACTLIST);
			controller.alert = AlertFactory.createInfoAlert(Locale.get("message.sendsmssucc"), Setting.alertInfoTime);
			controller.showCurrent(controller.alert, parent);
		}
	}

	public void onProgressNotification(String message) {	

	}

	public void onScanFinished(ItemContact[] itemcontacts) {

	}

	public void stop() {
		connmanager.stopThread();
	}
	
}

