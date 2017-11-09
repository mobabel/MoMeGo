/*
 * Created on 2005-5-17
 */
package net.mobabel.view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import net.mobabel.item.ItemOption;
import net.mobabel.model.CommonFunc;
import net.mobabel.model.RecordStoreConfig;
import net.mobabel.model.Setting;
import net.mobabel.util.UIController;

import de.enough.polish.util.Locale;

/**
 * @author Administrator
 * Written by leelight. All rights reserved by leelight.
 */
public class ScreenRegister extends Form implements CommandListener, ItemCommandListener{

	private UIController    controller = null;
	private ScreenMain screenmain = null;

	private TextField tfInputCode = null, tfMachineCode = null;
	private RecordStoreConfig   dictRecords = null;
	private StringItem register = null;

	public ScreenRegister(UIController controller, ScreenMain screenmain){
		//#style subScreen
		super(Locale.get("title.register"));
		this.controller=controller;
		this.screenmain = screenmain;

		//#style input
		tfMachineCode= new TextField(Locale.get("item.machinecode"),"",8,TextField.UNEDITABLE);
		
		if(!ItemOption.getInstance().getRegister()){
			//#style input
			tfInputCode= new TextField(Locale.get("item.inputregcode"),"",8,TextField.ANY);
			controller.focusCurrentItem(tfInputCode);
			
			//#style button
			register = new StringItem("",Locale.get("cmd.register"), Item.BUTTON);
			register.addCommand(UIController.okCommand);
			register.setItemCommandListener(this);
		}else{
			//#style input
			tfInputCode= new TextField(Locale.get("item.machinecode"),"********",8,TextField.UNEDITABLE);
		}

		this.append(tfMachineCode);
		this.append(tfInputCode);
		if(!ItemOption.getInstance().getRegister()){
			this.append(register);
		}

		tfMachineCode.setString(ItemOption.getInstance().getMachineCode());

		this.addCommand(UIController.backCommand);	    
		this.setCommandListener(this);
	}


	public void commandAction(Command c, Displayable disp){
		if(c==UIController.backCommand){				
			controller.showMainScreen();
		}
	}

	public void commandAction(Command c, Item item) {
		if(item == register){
			if(c==UIController.okCommand ) {
				String strTemp = tfInputCode.getString().trim();
				if(strTemp.equals("")){
					controller.showCurrent(AlertFactory.createErrorAlert(Locale.get("message.regemptycode")),this);
				}else{
					boolean breg = CommonFunc.register(tfMachineCode.getString(),tfInputCode.getString().trim());
					if(breg){						
						try {
							dictRecords = null;
							dictRecords = new RecordStoreConfig(controller);
							ItemOption.getInstance().setRegister(breg);
							
							if(dictRecords.updateRS(ItemOption.getInstance())) {
								controller.initializeOption(ItemOption.getInstance());
								screenmain.controller = controller;
								//screenmain.refresh(false);
								controller.showCurrent(AlertFactory.createInfoAlert(Locale.get("message.regsucc"),Setting.alertInfoTime),screenmain);
							}
							else {
								controller.showCurrent(AlertFactory.createWarningAlert( Locale.get("message.regerror"),Setting.alertWarningTime));
							} 
						} catch (Exception e) {
							//#debug error
							System.out.println("register:"  + e.getMessage());
							controller.showCurrent(AlertFactory.createErrorAlert(Locale.get("message.regerror")), this);
						}
	
					}
					else{
						controller.showCurrent(AlertFactory.createErrorAlert(Locale.get("message.regfail")),this);
					}
				}
	
			} 
		}
	}
}
