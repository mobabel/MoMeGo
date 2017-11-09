package net.mobabel.view;

import java.io.IOException;

import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStoreException;

import net.mobabel.item.ItemOption;
import net.mobabel.model.RecordStoreConfig;
import net.mobabel.model.Setting;
import net.mobabel.util.UIController;

import de.enough.polish.util.Locale;

public class ScreenOperation extends Form implements CommandListener{

	private UIController    controller= null;
	private ScreenMain screenmain = null;

	private Gauge approximateGauge = null,resultmaxGauge= null;
	private ChoiceGroup tipsCG= null,dictbaseCG= null,langCG= null, netCG = null;

	private boolean btips =false;
	private int approlevel=1;
	private int resultmax=5;//5-12
	private boolean dbarray0 = true;//Basic Database
	private boolean dbarray1 = false;//user database
	/*	    private boolean dbarray2 = false;//    
	    private boolean lang0 = true;//English
	    private boolean lang1 = false;//Deutsch
	    private boolean lang2 = false;//Chinese
	 */	    
	private boolean net0 = true;//other net
	private boolean net1 = false;//China net
	private String machinecode = "";
	private boolean register = false;

	private RecordStoreConfig   rsconfig = null;
	private ItemOption itemoption = null;

	//#if polish.Vendor == Motorola
	//# public static Command saveCommand = new Command(Locale.get("cmd.save"),Command.SCREEN, 2); 
	//#elif polish.Vendor == Sony-Ericsson
	//# public static Command saveCommand = new Command(Locale.get("cmd.save"),Command.SCREEN, 2);
	//#else
	public static Command saveCommand = new Command(Locale.get("cmd.save"), Command.SCREEN, 2);
	//#endif

	public ScreenOperation(UIController controller, ScreenMain screenmain) {
		//#style subScreen
		super(Locale.get("title.option"));
		this.controller=controller;
		this.screenmain = screenmain;


		//#style operationChoiceGroup
		tipsCG = new ChoiceGroup("",Choice.MULTIPLE);
		//#style operationMultipleItem
		tipsCG.append(Locale.get("item.optiontip"),null);

		//#style operationGaugeItem
		resultmaxGauge = new Gauge(Locale.get("item.optionmaxresult"),true, 12, 5);
		//#style operationGaugeItem
		approximateGauge = new Gauge(Locale.get("item.optionmatchlevel"),true, 3, 1);

		//#style operationChoiceGroup
		dictbaseCG = new ChoiceGroup(Locale.get("item.optionselectdb"),Choice.MULTIPLE);
		//#style operationMultipleItem
		dictbaseCG.append(Locale.get("item.optionbasicdb"),null);
		//#style operationMultipleItem
		dictbaseCG.append(Locale.get("item.optionuserdb"),null);

		/*			//#style operationChoiceGroup
			langCG = new ChoiceGroup(Locale.get("item.optionsetlanguage"),Choice.EXCLUSIVE);
			//#style operationItem
			langCG.append(Locale.get("item.optionlanen"),null);
			//#style operationItem
			langCG.append(Locale.get("item.optionlande"),null);
			//#style operationItem
			langCG.append("",null);*/

		//#style operationChoiceGroup
		netCG = new ChoiceGroup(Locale.get("item.optionsetnet"),Choice.EXCLUSIVE);
		//#style operationItem
		netCG.append(Locale.get("item.optionsetnetother"),null);
		//#style operationItem
		netCG.append(Locale.get("item.optionsetnetchina"),null);

		refreshOperation();

		this.append(tipsCG);
		this.append(resultmaxGauge);
		this.append(approximateGauge);
		this.append(dictbaseCG);
		this.append(netCG);

		this.addCommand(saveCommand);      
		this.addCommand(UIController.backCommand);

		this.setCommandListener(this);
	}


	/**********************************************************
		//refresh the operation from the RS and display on the screen
	 **********************************************************/
	private /*synchronized*/ void refreshOperation()
	{			
/*		try{
			rsconfig = new RecordStoreConfig();
			itemoption = rsconfig.getRS();
		}catch(Exception exc){
			System.out.println("When load refreshOperation Exception happens:" + exc.getMessage());
		}*/
		itemoption = ItemOption.getInstance();
		tipsCG.setSelectedIndex(0,itemoption.gettips());
		resultmaxGauge.setValue(itemoption.getresultmax());
		approximateGauge.setValue(itemoption.getapprolevel());
		dictbaseCG.setSelectedIndex(0,itemoption.getdbarray0());
		dictbaseCG.setSelectedIndex(1,itemoption.getdbarray1());
		/*			dictbaseCG.setSelectedIndex(2,operationitem.getdbarray2());
			langCG.setSelectedIndex(0,operationitem.getlang0());
			langCG.setSelectedIndex(1,operationitem.getlang1());
			langCG.setSelectedIndex(2,operationitem.getlang2());*/
		netCG.setSelectedIndex(0,itemoption.getnet0());
		netCG.setSelectedIndex(1,itemoption.getnet1());
		this.machinecode = itemoption.getMachineCode();
		this.register = itemoption.getRegister();
	}


	public void commandAction(Command c, Displayable disp){
		if(c==saveCommand ) {
			btips = tipsCG.isSelected(0);
			resultmax = resultmaxGauge.getValue();
			if(resultmax<5) {approlevel=5;}	
			approlevel = approximateGauge.getValue();
			if(approlevel==0) {approlevel=1;}		
			dbarray0=dictbaseCG.isSelected(0);
			dbarray1=dictbaseCG.isSelected(1);
			/*			  dbarray2=dictbaseCG.isSelected(2);
			  lang0=langCG.isSelected(0);
			  lang1=langCG.isSelected(1);
			  lang2=langCG.isSelected(2);*/
			net0 = netCG.isSelected(0);
			net1 = netCG.isSelected(1);

			ItemOption.getInstance().settips(btips);
			ItemOption.getInstance().setresultmax(resultmax);
			ItemOption.getInstance().setapprolevel(approlevel);
			ItemOption.getInstance().setdbarray0(dbarray0);
			ItemOption.getInstance().setdbarray1(dbarray1);
			/*			  ItemOption.getInstance().setdbarray2(dbarray2);
			  ItemOption.getInstance().setlang0(lang0);
			  ItemOption.getInstance().setlang1(lang1);
			  ItemOption.getInstance().setlang2(lang2);*/
			ItemOption.getInstance().setnet0(net0);
			ItemOption.getInstance().setnet1(net1);
			ItemOption.getInstance().setMachineCode(this.machinecode);
			ItemOption.getInstance().setRegister(this.register);

			boolean succ=false;
			try {
				rsconfig = new RecordStoreConfig(controller);
				succ = rsconfig.updateRS(ItemOption.getInstance());
				if(succ) {
					controller.initializeOption(ItemOption.getInstance());
					this.screenmain.controller = controller;
					controller.showCurrent(AlertFactory.createInfoAlert(Locale.get("message.savesucc"),Setting.alertInfoTime),this.screenmain);
				}else {
					controller.showCurrent(AlertFactory.createWarningAlert(Locale.get("message.savefail"),Setting.alertWarningTime));
				} 
			} catch (IOException e) {
				//#debug error
				System.out.println( e.getMessage());
				controller.showCurrent(AlertFactory.createErrorAlert(Locale.get("message.savefail")), this);
			} catch (RecordStoreException e) {
				//#debug error
				System.out.println( e.getMessage());
				controller.showCurrent(AlertFactory.createErrorAlert(Locale.get("message.savefail")),this);
			}
		}
		else if(c==UIController.backCommand){				
			controller.showMainScreen();
		}
	}


}

