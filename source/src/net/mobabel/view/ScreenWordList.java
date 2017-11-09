package net.mobabel.view;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import net.mobabel.item.ItemMessage;
import net.mobabel.util.UIController;

import de.enough.polish.util.Locale;

public class ScreenWordList extends List implements CommandListener {

	private UIController controller = null;

	private Displayable parent = null;

	private ScreenResult formresult = null;

	private ItemMessage[] itemtrans = null;

	private String instring;

	private Command nextCommand = null;
	
	private String prefix;
	
	private int SearchType = 0;

	public ScreenWordList(UIController control, ItemMessage[] itemtrans,
			Displayable parent, boolean hasNextMatched, String prefix, int SearchType) {
		//#style subScreen
		super("", List.IMPLICIT);
		this.controller = control;
		this.itemtrans = itemtrans;
		this.parent = parent;
		this.prefix = prefix;
		this.SearchType = SearchType;
		
		int size = itemtrans.length;
		nextCommand = new Command(Locale.get("cmd.nextwords"),Command.SCREEN, 2);

		for (int i = 0; i < size; i++) {
			//instring = itemtrans[i].getWord();
			int j = instring.indexOf('$');
			if (j >= 0) {
				if(i%2 == 0){
					//#style listItem
					this.append(instring.substring(0, j), null);
				}else{
					//#style listItem
					this.append(instring.substring(0, j), null);
				}
			} else {
				if(i%2 == 0){
					//#style listItem
					this.append(instring, null);
				}else{
					//#style listItem
					this.append(instring, null);
				}
			}
		}

		this.addCommand(UIController.backCommand);
		if(hasNextMatched)
			this.addCommand(nextCommand);
		this.setCommandListener(this);
	}

	public void commandAction(Command c, Displayable dis) {
		int wordindex = this.getSelectedIndex();
		if (c == List.SELECT_COMMAND) {
			formresult = new ScreenResult(controller,itemtrans[wordindex], this, false, this.SearchType);
			controller.showCurrent(formresult);
		} else if (c == UIController.backCommand) {
			controller.showCurrent(parent);
		}
		else if (c == nextCommand) {}
	}

}
