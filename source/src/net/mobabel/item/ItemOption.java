/*
 * Created on 2005-2-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.mobabel.item;

import net.mobabel.model.Setting;

/**
 * ItemOperation class contains the server list's properties,
 * which include server name and server URL
 */
public class ItemOption {
	private int id;
	private boolean hasStoredFontdotlib = false;
    private boolean btips = Setting.btips;
    private int resultmax = Setting.resultmax;
    private int approlevel= Setting.approlevel;
    private boolean dbarray0 = Setting.dbarray0;//Basic database
    private boolean dbarray1 = Setting.dbarray1;//user dababase 
    //private boolean lang0 = false;//English
    //private boolean lang1 = false;//Deutsch
    //private boolean lang2 = false;//Chinese
    private boolean net0 = true;//other net
    private boolean net1 = false;//China net
    private String  MachineCode = "LMFEQIBW";//LMFEQIBW
    private boolean Register = Setting.bRegister;
    
    private static ItemOption instance = null;
    
    public ItemOption(){
    	
    }
    
    public static ItemOption getInstance()
	{
		if (ItemOption.instance == null)
		{
			ItemOption.instance = new ItemOption();
		}
		return ItemOption.instance;
	}
    
       
    
    public int getId(){return id;}   
    public void setId(int _id){this.id=_id;}
    
    public boolean getHasStoredFontdotlib(){return hasStoredFontdotlib;}   
    public void setHasStoredFontdotlib(boolean hasStoredFontdotlib){this.hasStoredFontdotlib=hasStoredFontdotlib;}
    public boolean gettips(){return btips;}   
    public void settips(boolean _btips){this.btips=_btips;}
    public int getresultmax(){return resultmax;}   
    public void setresultmax(int _resultmax){this.resultmax=_resultmax;}
    public int getapprolevel(){return approlevel;}   
    public void setapprolevel(int _approlevel){this.approlevel=_approlevel;}
    public boolean getdbarray0(){return dbarray0;}   
    public void setdbarray0(boolean _dbarray0){this.dbarray0=_dbarray0;}
    public boolean getdbarray1(){return dbarray1;}   
    public void setdbarray1(boolean _dbarray1){this.dbarray1=_dbarray1;}
    /*public boolean getdbarray2(){return dbarray2;}   
    public void setdbarray2(boolean _dbarray2){this.dbarray2=_dbarray2;}
    public boolean getlang0(){return lang0;}   
    public void setlang0(boolean _lang0){this.lang0=_lang0;}
    public boolean getlang1(){return lang1;}   
    public void setlang1(boolean _lang1){this.lang1=_lang1;}
    public boolean getlang2(){return lang2;}   
    public void setlang2(boolean _lang2){this.lang2=_lang2;}*/
    public boolean getnet0(){return net0;}   
    public void setnet0(boolean net0){this.net0=net0;}
    public boolean getnet1(){return net1;}   
    public void setnet1(boolean net1){this.net1=net1;}
    public String getMachineCode(){return MachineCode;}   
    public void setMachineCode(String MachineCode){this.MachineCode=MachineCode;}
    public boolean getRegister(){return Register;}   
    public void setRegister(boolean Register){this.Register=Register;}
    
    
}
