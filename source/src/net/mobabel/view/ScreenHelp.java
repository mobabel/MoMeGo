/*
 * Created on 2006-9-4
 */
package net.mobabel.view;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;

import net.mobabel.model.CommonFunc;
import net.mobabel.model.Setting;
import net.mobabel.util.UIController;

import de.enough.polish.util.Locale;

/**
 * @author Administrator
 * Written by leelight. All rights reserved by leelight.
 */
public class ScreenHelp extends Form implements CommandListener{
	private UIController  controller = null;
	private Image iconmodict = null;
	private StringItem help = null;
	private Displayable parent = null;
	
	private Image iconmydict=null;
	
    private StringItem about1 = null, about = null;
	
	public static final int TYPE_HELP = 0;
	
	public static final int TYPE_ABOUT = 1;
	
	private int type = TYPE_HELP;

    public ScreenHelp(UIController controller, Displayable parent, int type){
    	//#style subScreen
        super(Locale.get("title.help"));
        this.controller=controller;
        this.parent = parent;
        this.type = type;

	    //#style itemDetailText
	    about1 = new StringItem("",Locale.get("message.about1")+ CommonFunc.getVersion(Setting.Version), Item.PLAIN);
	    //#style itemDetailText
	    about = new StringItem("",Locale.get("message.about"), Item.PLAIN);
        
        if(this.type == TYPE_HELP){
	        try {
	        	iconmodict=Image.createImage("/splash.png");	       
		    } catch (java.io.IOException x) {
		    	iconmodict=null;
				System.out.println("Load image error when initializing Exception happens:" + x.getMessage());
		    }
		    //#style itemDetailText
		    help = new StringItem("",Locale.get("message.help"), Item.PLAIN);
		    
	    	//#style itemDetailImage
	        this.append(iconmodict);
	        this.append(help);
        }
        else if(this.type == TYPE_ABOUT){
        	this.setTitle(Locale.get("title.about"));
        	try {
            	iconmodict=Image.createImage("/splash.png");
    	        //iconmydict=Image.createImage("/mydictlogo.png");	       
    	    } catch (IOException e) {
    	    	iconmodict=null;
    	    	//iconmydict = null;
    	    	//#debug error
    			System.out.println("Load image error: " + e.getMessage());
    	    }

        	//#style itemDetailImage
            this.append(iconmodict);
            this.append(about1);
            this.append(about);
        	// #style itemDetailImage
            //this.append(iconmydict);
        }
        
	    this.addCommand(UIController.backCommand);
        this.setCommandListener(this);
    }
    
	public void commandAction(Command c, Displayable dis) {
		if(c == UIController.backCommand){
			controller.showCurrent(this.parent);
		}
	}
}
