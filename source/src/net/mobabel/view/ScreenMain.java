package net.mobabel.view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import net.mobabel.view.AlertFactory;
import net.mobabel.view.ScreenHelp;
import net.mobabel.view.ScreenOperation;
import net.mobabel.view.ScreenRegister;
import net.mobabel.view.ScreenMsgWrite;

import net.mobabel.item.ItemGlobal;
import net.mobabel.item.ItemOption;
import net.mobabel.util.UIController;


import de.enough.polish.util.Locale;

public class ScreenMain extends List implements CommandListener {

	public UIController controller = null;

	private boolean breg = false;

	private Command exitCommand = new Command(Locale.get("cmd.quit"),
			Command.EXIT, 2);

	private Command aboutCommand = new Command(Locale.get("cmd.about"),
			Command.OK, 2);

	public ScreenMain(UIController controller) {
		//#style mainScreen
		super(Locale.get("title.mainmenu"),List.IMPLICIT);
		this.controller = controller;

		refresh(true);

		this.setSelectCommand(UIController.selectCommand);
		this.addCommand(aboutCommand);
		this.addCommand(exitCommand);
		this.setCommandListener(this);
	}

	public void refresh(boolean isFirst){
		if(isFirst)
			this.deleteAll();

		breg = ItemOption.getInstance().getRegister();

		//#style mainMenuItem
		this.append(Locale.get("title.smsWrite"), null);
		//#style mainMenuItem
		this.append(Locale.get("title.smsInbox"), null);
		//#style mainMenuItem
		this.append(Locale.get("title.smsOutbox"), null);
		//#style mainMenuItem
		this.append(Locale.get("title.smsSentMessages"), null);
		//#style mainMenuItem
		this.append(Locale.get("title.smsSavedMessages"), null);
		//#style mainMenuItem
		this.append(Locale.get("title.smsDraft"), null);
		//#style mainMenuItem
		this.append(Locale.get("title.smsTemplates"), null);
		//#style mainMenuItem
		this.append(Locale.get("title.smsRecycle"), null);
		//#style mainMenuItem
		this.append(Locale.get("title.contact"), null);
		//#style mainMenuItem
		this.append(Locale.get("title.contactGroup"), null);
		if (!breg){
			//#style mainMenuItem
			this.append(Locale.get("title.register"), null);
		}	
	}

	public void commandAction(Command c, Displayable dis) {		
		int funcindex = this.getSelectedIndex();
		if(dis == this){
			if (c == UIController.selectCommand) {
				if (funcindex == 0) {
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_COMPILER);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenMsgWrite(controller, this);
					controller.showCurrent(controller.nextScreen);
				}
				if (funcindex == 1) {
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_INBOX);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenMessageList(controller, this);
					controller.showCurrent(controller.nextScreen);
				}
				if (funcindex == 2) {
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_OUTBOX);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenMessageList(controller, this);
					controller.showCurrent(controller.nextScreen);
				}
				if (funcindex == 3) {
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_SENTMESSAGE);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenMessageList(controller, this);
					controller.showCurrent(controller.nextScreen);
				}
				if (funcindex == 4) {
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_SAVEDMESSAGE);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenMessageList(controller, this);
					controller.showCurrent(controller.nextScreen);
				}
				if (funcindex == 5) {
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_DRAFT);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenMessageList(controller, this);
					controller.showCurrent(controller.nextScreen);
				}
				if (funcindex == 6) {
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_TEMPLATE);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenMessageList(controller, this);
					controller.showCurrent(controller.nextScreen);
				}
				if (funcindex == 7) {
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_RECYCLE);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenMessageList(controller, this);
					controller.showCurrent(controller.nextScreen);
				}
				if (funcindex == 8) {
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_CONTACTLIST);
					controller.nextScreen = null;
					controller.nextScreen = new ScreenContactList(controller, this);
					controller.showCurrent(controller.nextScreen);
				}
				if (funcindex == 9) {
					ItemGlobal.getInstance().setScreenType(ItemGlobal.SCREEN_TYPE_CONTACTGROUP);
					controller.nextScreen = null;
					//controller.nextScreen = new ScreenMessageList(controller, this);
					//controller.showCurrent(controller.nextScreen);
				}
				if (funcindex == 10) {
					controller.nextScreen = null;
					controller.nextScreen = new ScreenRegister(controller, this);
					controller.showCurrent(controller.nextScreen);
				}
			}
			else if (c == exitCommand) {
				controller.alert = AlertFactory.createYesOrNoAlert(Locale.get("message.quit"));
				controller.alert.setCommandListener(this);		
				controller.showCurrent(controller.alert);
			}
			else if (c == aboutCommand) {
				controller.nextScreen = null;
				controller.nextScreen = new ScreenHelp(controller, this, ScreenHelp.TYPE_ABOUT);
				controller.showCurrent(controller.nextScreen);
			}
		}else if(dis == this.controller.alert){
			if(c == AlertFactory.noCommand){
				controller.showCurrent(this);
			}else if(c == AlertFactory.yesCommand){
				controller.quit();
			}
		}

	}

}
