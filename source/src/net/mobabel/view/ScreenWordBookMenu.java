/*
 * Created on 2006-9-4
 */
package net.mobabel.view;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import net.mobabel.util.UIController;

import de.enough.polish.util.Locale;

/**
 * @author Administrator
 * Written by leelight. All rights reserved by leelight.
 */
public class ScreenWordBookMenu extends List implements CommandListener{
	private UIController    controller = null;
	private Displayable parent = null;
	
	
    public ScreenWordBookMenu(UIController controller, Displayable parent){
    	//#style menuScreen
        super("", List.IMPLICIT);
        this.controller=controller;
        this.parent = parent;
        
		//#style subMenuItem
		this.append("", null);
		//#style subMenuItem
		this.append("", null);
		//#style subMenuItem
		this.append("", null);
		//#style subMenuItem
		this.append("", null);
		
	    this.addCommand(UIController.backCommand);
        this.setCommandListener(this);
    }
    
	public void commandAction(Command c, Displayable dis) {
		if(dis == this){
			if(c == UIController.backCommand){
				controller.showCurrent(this.parent);
			}
			else if(c == List.SELECT_COMMAND){
				int index = this.getSelectedIndex();
				controller.nextScreen = null;
				if(index!=3){
					controller.nextScreen = new ScreenMessageList(controller, this);
				}else{
					controller.nextScreen = new ScreenReciteMenu(controller, this);
				}
				controller.showCurrent(controller.nextScreen);
			}
		}
	}
}
