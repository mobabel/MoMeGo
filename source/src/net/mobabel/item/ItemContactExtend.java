package net.mobabel.item;

public class ItemContactExtend {
	String name = "";

	String value = "";
	
	int fieldvalue = -1;
	
	public ItemContactExtend(){
		
	}
	
	public ItemContactExtend(String name, String value, int fieldvalue){
		this.name = name;
		this.value = value;
		this.fieldvalue = fieldvalue;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}	

	public void setFieldValue(int fieldvalue){
		this.fieldvalue = fieldvalue;
	}
	
	public int getFieldValue(){
		return this.fieldvalue;
	}
	
}
