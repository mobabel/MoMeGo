package net.mobabel.view;

import javax.microedition.lcdui.*;

import net.mobabel.item.ItemMessage;
import net.mobabel.item.ItemUTF8Lib;
import net.mobabel.model.CommonFunc;
import net.mobabel.model.Setting;
import net.mobabel.util.UIController;
import net.mobabel.model.RecordStoreMessage;
import de.enough.polish.util.Locale;

public class ScreenResult extends Form implements CommandListener{

	private UIController    controller;
	private Displayable        parent;

	private Command searchCommand= new Command(Locale.get("cmd.search"),Command.OK,1);;
	private Command saveCommand= new Command(Locale.get("cmd.save"),Command.OK,2);
	private StringItem ResultField = null;
	private RecordStoreMessage rs = null;
	private ItemMessage itemtranslation = null, itemtrans = null;
	private String tempword, temptrans;
	private boolean noresult = false;
	private boolean isFromWordBook = false;
	private int SearchType;
	
	public ScreenResult(UIController controller, ItemMessage itemtranslation, Displayable parent, 
			boolean isFromWordBook, int SearchType) {
		//#style subScreen
		super("");
		this.controller=controller;
		this.parent = parent;
		this.itemtranslation = itemtranslation;
		this.isFromWordBook = isFromWordBook;
		this.SearchType = SearchType;

		itemtrans = new ItemMessage();

		if(this.itemtranslation == null){
			tempword = Locale.get("message.noresult");
			temptrans = Locale.get("message.tryothersearch");
			noresult = true;
		}else{
			//tempword=itemtranslation.getWord().replace(Setting.WordDevideChar,Setting.WordDevideCharReplace);
			tempword = CommonFunc.formatWord(itemtranslation.getText());
			temptrans = itemtranslation.getText();

			itemtrans.setText(itemtranslation.getText());
			itemtrans.setText(temptrans);
		}

		//#style listDetailItem
		ResultField = new StringItem(tempword,"", Item.LAYOUT_LEFT);

		//TODO debug for utf8 label
		//ResultField.setLabel(temptrans);
		ResultField.setText(temptrans);
		this.append(ResultField);

		if(!isFromWordBook){
			if(!noresult){					
				try{
					rs = null;
					rs = new RecordStoreMessage(controller);
					//#debug debug
					System.out.println("record id: " + rs.hasSameRecord(itemtrans));
					if(rs.hasSameRecord(itemtrans) == -1){
						this.addCommand(saveCommand);
					}
				}catch (Exception e) {
					//#debug error
					System.out.println("Error when check word: " + e.getMessage());
				}finally{
					rs.closeRS();
				}
				
				//go to save histoty
				try {
					rs = null;
					rs = new RecordStoreMessage(controller);
					boolean successful = rs.addRS(itemtrans);
					//#debug debug
					System.out.println("Add History successfully? "+ successful);
				} catch (Exception e) {
					//#debug error
					System.out.println("Error when save history: " + e.getMessage());
				}
			}
		}

		if(!isFromWordBook){
			this.addCommand(searchCommand); 
		}
		this.addCommand(UIController.backCommand);

		this.setCommandListener(this);
	}


	public void commandAction(Command c, Displayable disp){
		if(disp == this){
			if(c == searchCommand ) {}
			else if(c == UIController.backCommand){				
				controller.showCurrent(parent);
			} 
			else if(c == saveCommand){				
				try {
					rs = null;
					rs = new RecordStoreMessage(controller);
					boolean successful = rs.addRS(itemtrans);
					if(successful){
						controller.showCurrent(AlertFactory.createInfoAlert(Locale.get("message.savesucc"),Setting.alertInfoTime));		
						this.removeCommand(saveCommand);
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


}

