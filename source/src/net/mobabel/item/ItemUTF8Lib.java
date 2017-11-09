package net.mobabel.item;

import java.io.InputStream;
import java.util.Hashtable;

import net.mobabel.model.RecordStoreFontLib;

public class ItemUTF8Lib {
	
	private byte[] fontDotLib = null;
	
	private Hashtable tableUtf8LocalCode = null;
	
	private static ItemUTF8Lib instance = null;
	
	public InputStream inputstream  = null;
	
	private RecordStoreFontLib rsfontlib = null;
	
	private boolean useFontDotLibInMemory = true;
	
	public static ItemUTF8Lib getInstance()
	{
		if (ItemUTF8Lib.instance == null)
		{
			ItemUTF8Lib.instance = new ItemUTF8Lib();
		}
		return ItemUTF8Lib.instance;
	}
	
	public ItemUTF8Lib(){
		
	}

	public byte[] getFontDotLib(){
		return this.fontDotLib;
	} 
	
	public void setFontDotLib(byte[] fontDotLib){
		this.fontDotLib = fontDotLib;
	}
    
    /**
     * For chinese,it is table for utf8--gb2312
     * @param tableUtf8LocalCode
     */
    public void setTableUtf8LocalCode(Hashtable tableUtf8LocalCode){
    	this.tableUtf8LocalCode = tableUtf8LocalCode;
    }
    
    public Hashtable getTableUtf8LocalCode(){
    	return this.tableUtf8LocalCode;
    }
    
    public void setInputStream(InputStream is){
    	this.inputstream = is;
    }
    
    public InputStream getInputStream(){
    	return this.inputstream;
    }
    
    public void setRSFontLib(RecordStoreFontLib rsfontlib){
    	this.rsfontlib = rsfontlib;
    }
    
    public RecordStoreFontLib getRSFontLib(){
    	return this.rsfontlib;
    }
    
    public void setUseFontDotLibInMemory(boolean useFontDotLibInMemory){
    	this.useFontDotLibInMemory = useFontDotLibInMemory;
    }
    
    public boolean getUseFontDotLibInMemory(){
    	return this.useFontDotLibInMemory;
    }
}
