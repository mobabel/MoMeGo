package net.mobabel.view;

import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import net.mobabel.item.ItemOption;
import net.mobabel.model.OnlineFunc;
import net.mobabel.util.UIController;

import de.enough.polish.util.Locale;

public class ScreenOnlineDict extends Form implements CommandListener, ItemCommandListener{
	
	private UIController    controller = null;
	private TextField inputdeField = null;

    private ChoiceGroup dcCG = null;
    private OnlineFunc dictonlinefunc = null;
    
    String strTemp;

    private int resultmax;
    private StringItem search;
    private Image decnIcon,cndeIcon;
    private Displayable parent = null;
    

	public ScreenOnlineDict(UIController controller, Displayable parent){
	    //#style subScreen
		super("");
		this.controller=controller;
		this.parent = parent;

		resultmax = ItemOption.getInstance().getresultmax(); 

		
		//  # = try {
		   //  # = Locale.loadTranslations("/${modict.word}.loc");
		//  # = } catch (IOException e) { System.out.println("Error: Can not load translation loc file"); }
	    
	    try{
	    	decnIcon = Image.createImage("/wt.png");
	    	cndeIcon = Image.createImage("/tw.png");
	    }catch (java.io.IOException x) {
	    	decnIcon=null;
	    	cndeIcon = null;
			System.out.println("Load image error when initializing Exception happens:" + x.getMessage());
	    }
	    
	    //#style operationChoiceGroup
		dcCG = new ChoiceGroup("",Choice.EXCLUSIVE);
		//#style operationMultipleItem
		dcCG.append("",decnIcon);
		//#style operationMultipleItem
		dcCG.append("",cndeIcon);
		dcCG.setSelectedIndex(0, true);
		this.append(dcCG);
		
		//#style input
		inputdeField= new TextField("", "", 20, TextField.ANY);
		this.append(inputdeField);
		controller.focusCurrentItem(inputdeField);
		this.focus(1);
		
		//#style button
	    search = new StringItem("",Locale.get("cmd.search"), Item.BUTTON);
	    search.addCommand(UIController.okCommand);
	    search.setItemCommandListener(this);
		this.append(search);
	         
        this.addCommand(UIController.backCommand);
        this.setCommandListener(this);
        
	}
	
	public void reset(){
		inputdeField.setString("");
	}
	
	 public void commandAction(Command c, Displayable disp){
		  if(c==UIController.backCommand){				
		    	controller.showMainScreen();
	        }
	    }
	 
	 public void commandAction(Command c, Item item) {
		  strTemp = inputdeField.getString().toLowerCase().trim();
		  if(c==UIController.okCommand ) {
			  if(strTemp.equals("")){
				  controller.showCurrent(AlertFactory.createErrorAlert(Locale.get("message.emptykeyword")),this);
			  }else{
				  dictonlinefunc = new OnlineFunc();
				  dictonlinefunc.getrequest(controller,strTemp, this,resultmax, dcCG.getSelectedIndex());
			  }
		  }
	 }
		
		
}


