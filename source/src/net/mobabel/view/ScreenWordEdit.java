package net.mobabel.view;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemStateListener;


import net.mobabel.item.ItemMessage;
import net.mobabel.model.Setting;
import net.mobabel.util.UIController;
import net.mobabel.model.RecordStoreMessage;
import javax.microedition.lcdui.TextField;

import de.enough.polish.util.Locale;

public class ScreenWordEdit extends Form implements CommandListener, ItemStateListener{

	private UIController    controller = null;
	private ScreenMessageList        screenwordbook = null;

	private Command        saveCommand = null;

	private TextField transField = null;
	
	private TextField wordField = null;

	private RecordStoreMessage rs = null;
	private ItemMessage itemtrans = null;
	private int maxEditSize = 150;

	public ScreenWordEdit(UIController controller, ItemMessage itemtrans, ScreenMessageList screenwordbook) {
		//#style subScreen
		super("");
		this.controller=controller;
		this.screenwordbook = screenwordbook;
		this.itemtrans = itemtrans;
				
		saveCommand= new Command(Locale.get("cmd.save"),Command.SCREEN,1);
		
		//#style input
		wordField = new TextField("", "", 30, TextField.ANY);
		wordField.setInitialInputMode("UCB_BASIC_LATIN");
		this.append(wordField);

		//#style inputLong
		transField = new TextField(maxEditSize+"", "", maxEditSize, Item.LAYOUT_LEFT);

		//controller.showCurrent(transField);
		this.append(transField);
		

		if(this.itemtrans != null){
			//wordField.setString(itemtrans.getWord());
			//transField.setString(itemtrans.getTranslation());
		}
		
		this.addCommand(saveCommand);
		this.addCommand(UIController.backCommand);
		this.setCommandListener(this);
		this.setItemStateListener(this);
	}


	public void commandAction(Command c, Displayable disp){
		if(disp == this){
			if(c == UIController.backCommand){				
				controller.showCurrent(screenwordbook);
			} 
			else if(c == saveCommand){				
				try {
					if(wordField.getString().equals("") || transField.getString().equals("")){
						controller.showCurrent(AlertFactory.createWarningAlert(Locale.get("message.emptykeyword")),this);
						return;
					}					
					rs = null;
					rs = new RecordStoreMessage(controller);
					boolean successful = false;
					//if save new
					if(this.itemtrans != null){
						successful = rs.updateRS(itemtrans.getId(), itemtrans);
					}//if save old
					else{
						itemtrans = new ItemMessage();
						//itemtrans.setWord(wordField.getString());
						//itemtrans.setTranslation(transField.getString());
						successful = rs.addRS(itemtrans);
					}
					
					if(successful){
						screenwordbook.refreshUserWordsByLetter();
						controller.showCurrent(AlertFactory.createInfoAlert(Locale.get("message.savesucc"),Setting.alertInfoTime),screenwordbook);		
					}else{
						controller.showCurrent(AlertFactory.createWarningAlert(Locale.get("message.savefail"),Setting.alertWarningTime));
					}
				} catch (Exception e) {
					//#debug error
					System.out.println("Error when save to WordBook: " + e.getMessage());
					controller.showCurrent(AlertFactory.createErrorAlert(e , Locale.get("message.savefail")));
				}
			} 
		}
	}


	public void itemStateChanged(Item item) {
		if(item == transField){
			int leftlength = this.maxEditSize - transField.getString().length();
			transField.setLabel(leftlength+"");
		}
		
	}

}

