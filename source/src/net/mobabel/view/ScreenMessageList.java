package net.mobabel.view;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import de.enough.polish.util.Locale;

import net.mobabel.item.ItemGlobal;
import net.mobabel.item.ItemMessage;
import net.mobabel.model.RecordStoreMessage;
import net.mobabel.model.CommonFunc;
import net.mobabel.util.UIController;

/**
 * @author lihui
 */
public class ScreenMessageList extends Form implements CommandListener, ItemCommandListener {
	
	private int type = ItemGlobal.getInstance().getScreenType();

	private UIController controller = null;

	private Command deleteCommand = new Command(Locale.get("cmd.remove"), Command.ITEM, 2);

	private Command addCommand;

	private Command editCommand;

	private Command ordertimeCommand;

	private Command orderabcCommand;

	private Displayable parentScreen = null;

	private StringItem stritems[] = null;

	private RecordStoreMessage rs = null;

	private ItemMessage[] messages = null;

	private int size;
	
	private Alert confirmAlert = null;

	private int recordPointer = 0;

	public ScreenMessageList(UIController controller, Displayable parentScreen) {
		//#style subScreen
		super(Locale.get("title.smsInbox"));
		this.controller = controller;
		this.parentScreen = parentScreen;
		this.type = ItemGlobal.getInstance().getScreenType();
		
		if(type == ItemGlobal.SCREEN_TYPE_INBOX){
			this.setTitle(Locale.get("title.smsInbox"));
			refreshMessagesInbox();
		}
		else if(type == ItemGlobal.SCREEN_TYPE_OUTBOX){	
			this.setTitle(Locale.get("title.smsOutbox"));
			refreshMessagesoutbox();
		}
		else if(type == ItemGlobal.SCREEN_TYPE_SENTMESSAGE){
			this.setTitle(Locale.get("title.smsSentMessages"));
			refreshMessagesSended();
		}
		else if(type == ItemGlobal.SCREEN_TYPE_SAVEDMESSAGE){	
			this.setTitle(Locale.get("title.smsSavedMessages"));
			refreshMessagesSaved();
		}
		else if(type == ItemGlobal.SCREEN_TYPE_DRAFT){
			this.setTitle(Locale.get("title.smsDraft"));
			refreshMessagesDraft();
		}
		else if(type == ItemGlobal.SCREEN_TYPE_TEMPLATE){	
			this.setTitle(Locale.get("title.smsTemplates"));
			refreshMessagesTemplate();
		}
		else if(type == ItemGlobal.SCREEN_TYPE_RECYCLE){	
			this.setTitle(Locale.get("title.smsRecycle"));
			refreshMessagesRecycle();
		}
		
		this.setCommandListener(this);
		this.addCommand(UIController.backCommand);
		

	}

	public void refreshMessagesInbox() {
		this.deleteAll();
		try {
			this.rs = null;
			this.rs = new RecordStoreMessage(this.controller);
			messages = rs.getAllRSTime(ItemMessage.STATUS_INBOX);
		} catch (Exception e) {
			//#debug error
			System.out.println("Error when load wordbook: " + e.getMessage());
		}

		this.size = messages.length;
		//#debug debug
		System.out.println("The number of wordbook: " + this.size);

		stritems = new StringItem[this.size];
		for (int i = 0; i < this.size; i++) {
			if(messages[i].getStatus() == ItemMessage.STATUS_UNREAD) {
				//#style msgUnread
				stritems[i] = new StringItem(messages[i].getText(), CommonFunc.getDateString(messages[i].getTimestamb()), Item.LAYOUT_CENTER);
			}else{
				//#style msgRead
				stritems[i] = new StringItem(messages[i].getText(), CommonFunc.getDateString(messages[i].getTimestamb()), Item.LAYOUT_CENTER);
				stritems[i].addCommand(UIController.saveCommand);
			}
			this.append(stritems[i]);
			stritems[i].addCommand(UIController.selectCommand);
			stritems[i].addCommand(deleteCommand);
			stritems[i].setItemCommandListener(this);
		}
		
		if(this.size == 0){
			stritems = new StringItem[1];
			//#style listItem
			stritems[0] = new StringItem(null, Locale.get("item.norecords"), Item.LAYOUT_CENTER);
			this.append(stritems[0]);
		}
	}
	
	public void refreshMessagesoutbox() {
		this.deleteAll();
		try {
			this.rs = null;
			this.rs = new RecordStoreMessage(this.controller);
			messages = rs.getAllRSTime(ItemMessage.STATUS_OUTBOX);
		} catch (Exception e) {
			//#debug error
			System.out.println("Error when load wordbook: " + e.getMessage());
		}

		this.size = messages.length;
		//#debug debug
		System.out.println("The number of wordbook: " + this.size);

		stritems = new StringItem[this.size];
		for (int i = 0; i < this.size; i++) {
			//#style msgOutbox
			stritems[i] = new StringItem(messages[i].getText(), CommonFunc.getDateString(messages[i].getTimestamb()), Item.LAYOUT_CENTER);
			this.append(stritems[i]);

			stritems[i].addCommand(UIController.selectCommand);
			stritems[i].addCommand(deleteCommand);
			stritems[i].setItemCommandListener(this);
		}
		
		if(this.size == 0){
			stritems = new StringItem[1];
			//#style listItem
			stritems[0] = new StringItem(null, Locale.get("item.norecords"), Item.LAYOUT_CENTER);
			this.append(stritems[0]);
		}
	}

	public void refreshMessagesSended() {
		this.deleteAll();
		try {
			this.rs = null;
			this.rs = new RecordStoreMessage(this.controller);
			messages = rs.getAllRSTime(ItemMessage.STATUS_SENDED);
		} catch (Exception e) {
			//#debug error
			System.out.println("Error when load wordbook: " + e.getMessage());
		}

		this.size = messages.length;
		//#debug debug
		System.out.println("The number of wordbook: " + this.size);

		stritems = new StringItem[this.size];
		for (int i = 0; i < this.size; i++) {
			//#style msgSended
			stritems[i] = new StringItem(messages[i].getText(), CommonFunc.getDateString(messages[i].getTimestamb()), Item.LAYOUT_CENTER);

			this.append(stritems[i]);
			stritems[i].addCommand(UIController.selectCommand);
			stritems[i].addCommand(deleteCommand);
			stritems[i].setItemCommandListener(this);
		}
		
		if(this.size == 0){
			stritems = new StringItem[1];
			//#style listItem
			stritems[0] = new StringItem(null, Locale.get("item.norecords"), Item.LAYOUT_CENTER);
			this.append(stritems[0]);
		}
	}
	
	public void refreshMessagesSaved() {
		this.deleteAll();
		try {
			this.rs = null;
			this.rs = new RecordStoreMessage(this.controller);
			messages = rs.getAllRSTime(ItemMessage.STATUS_SAVED);
		} catch (Exception e) {
			//#debug error
			System.out.println("Error when load wordbook: " + e.getMessage());
		}

		this.size = messages.length;
		//#debug debug
		System.out.println("The number of wordbook: " + this.size);

		stritems = new StringItem[this.size];
		for (int i = 0; i < this.size; i++) {
			//#style msgSaved
			stritems[i] = new StringItem(messages[i].getText(), CommonFunc.getDateString(messages[i].getTimestamb()), Item.LAYOUT_CENTER);
			this.append(stritems[i]);
			stritems[i].addCommand(UIController.selectCommand);
			stritems[i].addCommand(deleteCommand);
			stritems[i].setItemCommandListener(this);
		}
		
		if(this.size == 0){
			stritems = new StringItem[1];
			//#style listItem
			stritems[0] = new StringItem(null, Locale.get("item.norecords"), Item.LAYOUT_CENTER);
			this.append(stritems[0]);
		}
	}
	
	public void refreshMessagesDraft() {
		this.deleteAll();
		try {
			this.rs = null;
			this.rs = new RecordStoreMessage(this.controller);
			messages = rs.getAllRSTime(ItemMessage.STATUS_DRAFT);
		} catch (Exception e) {
			//#debug error
			System.out.println("Error when load wordbook: " + e.getMessage());
		}

		this.size = messages.length;
		//#debug debug
		System.out.println("The number of wordbook: " + this.size);
		stritems = new StringItem[this.size];
		for (int i = 0; i < this.size; i++) {
			//#style msgDraft
			stritems[i] = new StringItem(messages[i].getText(), CommonFunc.getDateString(messages[i].getTimestamb()), Item.LAYOUT_CENTER);

			this.append(stritems[i]);
			stritems[i].addCommand(UIController.selectCommand);
			stritems[i].addCommand(deleteCommand);
			stritems[i].setItemCommandListener(this);
		}
		
		if(this.size == 0){
			stritems = new StringItem[1];
			//#style listItem
			stritems[0] = new StringItem(null, Locale.get("item.norecords"), Item.LAYOUT_CENTER);
			this.append(stritems[0]);
		}
	}
	
	public void refreshMessagesTemplate() {
		this.deleteAll();
		try {
			this.rs = null;
			this.rs = new RecordStoreMessage(this.controller);
			messages = rs.getAllRSTime(ItemMessage.STATUS_TEMPLATE);
		} catch (Exception e) {
			//#debug error
			System.out.println("Error when load wordbook: " + e.getMessage());
		}

		this.size = messages.length;
		//#debug debug
		System.out.println("The number of wordbook: " + this.size);

		stritems = new StringItem[this.size];
		for (int i = 0; i < this.size; i++) {
			//#style msgTemplate
			stritems[i] = new StringItem(messages[i].getText(), CommonFunc.getDateString(messages[i].getTimestamb()), Item.LAYOUT_CENTER);
			this.append(stritems[i]);
			stritems[i].addCommand(UIController.selectCommand);
			stritems[i].addCommand(deleteCommand);
			stritems[i].setItemCommandListener(this);
		}
		
		if(this.size == 0){
			stritems = new StringItem[1];
			//#style listItem
			stritems[0] = new StringItem(null, Locale.get("item.norecords"), Item.LAYOUT_CENTER);
			this.append(stritems[0]);
		}
	}
	
	public void refreshMessagesRecycle() {
		this.deleteAll();
		try {
			this.rs = null;
			this.rs = new RecordStoreMessage(this.controller);
			messages = rs.getAllRSTime(ItemMessage.STATUS_RECYCLE);
		} catch (Exception e) {
			//#debug error
			System.out.println("Error when load wordbook: " + e.getMessage());
		}

		this.size = messages.length;
		//#debug debug
		System.out.println("The number of wordbook: " + this.size);

		stritems = new StringItem[this.size];
		for (int i = 0; i < this.size; i++) {
			//#style msgTemplate
			stritems[i] = new StringItem(messages[i].getText(), CommonFunc.getDateString(messages[i].getTimestamb()), Item.LAYOUT_CENTER);
			this.append(stritems[i]);
			stritems[i].addCommand(UIController.selectCommand);
			stritems[i].addCommand(deleteCommand);
			stritems[i].setItemCommandListener(this);
		}
		
		if(this.size == 0){
			stritems = new StringItem[1];
			//#style listItem
			stritems[0] = new StringItem(null, Locale.get("item.norecords"), Item.LAYOUT_CENTER);
			this.append(stritems[0]);
		}
	}
	
	private void refreshHistoryByLetter() {
		this.deleteAll();
		try {
			this.rs = null;
			this.rs = new RecordStoreMessage(this.controller);
			messages = rs.getAllRSLetter();
		} catch (Exception e) {
			//#debug error
			System.out.println("Error when load history: " + e.getMessage());
		}

		this.size = messages.length;
		//#debug debug
		System.out.println("The number of history: " + this.size);

		stritems = new StringItem[this.size];
		for (int i = 0; i < this.size; i++) {
			//#style listItem
			stritems[i] = new StringItem(messages[i].getText(), CommonFunc.getDateString(messages[i].getTimestamb()), Item.LAYOUT_CENTER);
			this.append(stritems[i]);
			stritems[i].addCommand(UIController.selectCommand);
			stritems[i].setItemCommandListener(this);
		}
	}
	
	public void refreshUserWordsByLetter() {
		this.deleteAll();
		try {
			this.rs = null;
			this.rs = new RecordStoreMessage(this.controller);
			messages = rs.getAllRSLetter();
		} catch (Exception e) {
			//#debug error
			System.out.println("Error when load wordbook: " + e.getMessage());
		}

		this.size = messages.length;
		//#debug debug
		System.out.println("The number of wordbook: " + this.size);

		stritems = new StringItem[this.size];
		for (int i = 0; i < this.size; i++) {
			//#style listItem
			stritems[i] = new StringItem(messages[i].getText(), CommonFunc.getDateString(messages[i].getTimestamb()), Item.LAYOUT_CENTER);

			this.append(stritems[i]);
			stritems[i].addCommand(UIController.selectCommand);
			stritems[i].addCommand(editCommand);
			stritems[i].addCommand(deleteCommand);
			stritems[i].setItemCommandListener(this);
		}
		
		if(this.size == 0){
			stritems = new StringItem[1];
			//#style listItem
			stritems[0] = new StringItem(null, Locale.get("item.norecords"), Item.LAYOUT_CENTER);
			this.append(stritems[0]);
		}
	}

	public void commandAction(Command c, Displayable dis) {
		if(dis == this){
			if (c == UIController.backCommand) {
				controller.showMainScreen();
			} else if (c == addCommand) {
				controller.nextScreen = new ScreenWordEdit(controller, null, this);
				controller.showCurrent(controller.nextScreen );
			} else if (c == orderabcCommand) {
				this.removeCommand(this.orderabcCommand);
				this.addCommand(this.ordertimeCommand);
				refreshHistoryByLetter();
			} else if (c == ordertimeCommand) {
				this.removeCommand(this.ordertimeCommand);
				this.addCommand(this.orderabcCommand);
				
			}
		}
		else if(dis == this.confirmAlert){
			if(c == AlertFactory.noCommand){
				controller.showCurrent(this);
			}else if(c == AlertFactory.yesCommand){
				try {			
					int id = messages[this.recordPointer].getId();
					boolean succ = this.rs.deleteRS(id);
					if (succ) {
						controller.showCurrent(this);
						switch(type){
						case ItemGlobal.SCREEN_TYPE_INBOX:
							refreshMessagesInbox();
							break;
						case ItemGlobal.SCREEN_TYPE_OUTBOX:
							refreshMessagesoutbox();
							break;
						case ItemGlobal.SCREEN_TYPE_SENTMESSAGE:
							refreshMessagesSended();
							break;
						case ItemGlobal.SCREEN_TYPE_SAVEDMESSAGE:
							refreshMessagesSaved();
							break;
						case ItemGlobal.SCREEN_TYPE_DRAFT:
							refreshMessagesDraft();
							break;
						case ItemGlobal.SCREEN_TYPE_TEMPLATE:
							refreshMessagesTemplate();
							break;
						case ItemGlobal.SCREEN_TYPE_RECYCLE:
							refreshMessagesRecycle();
							break;
						}
					} else {
						controller.showCurrent(AlertFactory.createWarningAlert(Locale.get("message.deletewordfail")), this);
					}
				} catch (Exception e) {
					//#debug error
					System.out.println("Error when delete message: "+ e.toString());
					controller.showCurrent(AlertFactory.createWarningAlert(Locale.get("message.deletewordfail")));
				}finally{
					
				}
			}
			
		}
	}

	public void commandAction(Command c, Item item) {
		for (int i = 0; i < this.size; i++) {
			if (item == stritems[i]) {
				if (c == UIController.selectCommand) {
					if(type == ItemGlobal.SCREEN_TYPE_INBOX){
						controller.itemMsgComing = messages[i];
						//TODO
						controller.itemMsgComing.setType(ItemMessage.TYPE_TextMessage);
						controller.nextScreen = null;
						controller.nextScreen = new ScreenMsgWrite(controller, this);
						controller.showCurrent(controller.nextScreen);
					}else if(type == ItemGlobal.SCREEN_TYPE_OUTBOX){
						controller.itemMsgComing = messages[i];
						controller.nextScreen = null;
						controller.nextScreen = new ScreenMsgWrite(controller, this);
						controller.showCurrent(controller.nextScreen);
					}
					else if(type == ItemGlobal.SCREEN_TYPE_SENTMESSAGE){
						controller.itemMsgComing = messages[i];
						controller.nextScreen = null;
						controller.nextScreen = new ScreenMsgWrite(controller, this);
						controller.showCurrent(controller.nextScreen);
					}
					else if(type == ItemGlobal.SCREEN_TYPE_SAVEDMESSAGE){
						controller.itemMsgComing = messages[i];
						controller.nextScreen = null;
						controller.nextScreen = new ScreenMsgWrite(controller, this);
						controller.showCurrent(controller.nextScreen);
					}
					else if(type == ItemGlobal.SCREEN_TYPE_DRAFT){
						controller.itemMsgComing = messages[i];
						controller.nextScreen = null;
						controller.nextScreen = new ScreenMsgWrite(controller, this);
						controller.showCurrent(controller.nextScreen);
					}
				} 
				else if (c == UIController.saveCommand) {
					this.recordPointer = i;
					int id = messages[this.recordPointer].getId();
					if(type == ItemGlobal.SCREEN_TYPE_INBOX){
						//save it to Saved Messages
						messages[this.recordPointer].setStatus(ItemMessage.STATUS_SAVED);
						boolean succ = this.rs.updateRS(id, messages[this.recordPointer]);
						if (succ) {
							refreshMessagesInbox();
						}else{
							controller.showCurrent(AlertFactory.createWarningAlert(Locale.get("message.savefail")), this);
						}	
					}
					
				}
				else if (c == deleteCommand) {
					this.recordPointer = i;
					confirmAlert = AlertFactory.createYesOrNoAlert(Locale.get("message.deleteword"));
					confirmAlert.setCommandListener(this);		
					controller.showCurrent(confirmAlert);
				} else if (c == editCommand) {
					controller.nextScreen = null;
					controller.nextScreen = new ScreenWordEdit(controller, messages[i], this);
					controller.showCurrent(controller.nextScreen );
				}
			}
		}
		
	}

}
