package net.mobabel.model;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Vector;

import net.mobabel.item.ItemMessage;

import de.enough.polish.util.TextUtil;

public class CommonFunc {

	/*
	 * split string with special char .
	 * @param original: string will be splited
	 * @param regex: splited char
	 * @return the splited string array
	 */
	public static String[] split(String original, String regex) {
		int startIndex = 0;
		Vector v = new Vector();
		String[] str = null;
		int index = 0;
		startIndex = original.indexOf(regex);

		while (startIndex < original.length() && startIndex != -1) {
			String temp = original.substring(index, startIndex);
			//System.out.println(" " + startIndex);
			v.addElement(temp);
			index = startIndex + regex.length();
			startIndex = original.indexOf(regex, startIndex + regex.length());
		}
		v.addElement(original.substring(index + 1 - regex.length()));

		str = new String[v.size()];
		for (int i = 0; i < v.size(); i++) {
			str[i] = (String) v.elementAt(i);
		}
		return str;
	}

	/*   
	 * Replaceone sector in a string paragraph with another string
	 * @param _text: string paragraph
	 * @param _searchStr: string will be replaced
	 * @param _replacementStr: replacement string
	 * @return the replaced string paragraph
	 */
	public static String replace(String _text, String _searchStr, String _replacementStr)
	{
//		String buffer to store str
		StringBuffer sb = new StringBuffer();

//		Search for search
		int pos = _text.indexOf(_searchStr);

//		Iterate to add string
		while (pos != -1)
		{
			sb.append(_text.substring(0, pos)).append(_replacementStr);

			_text = _text.substring(pos + _searchStr.length());
			pos = _text.indexOf(_searchStr);
		}

//		Create string
		sb.append(_text);

		return sb.toString();
	} 


	/*
	 * Devide String to StringArray convert
	 * @param org:String
	 * @return:String Array
	 */
	public static String[] String2StringArray(String org){
		String[] out = new String[org.length()];
		for(int lent=0;lent<org.length();lent++){
			out[lent]=org.substring(lent,lent+1);
			//System.out.println("sa_result: "+sa_result[lent]);
		}
		return out;
	}

	/*
	 * Convert the first char in the string to UpperCase
	 * @param org: String
	 * @return: converted string
	 */
	public static String FirstCharToUpperCase(String org){
		String rlt="";
		if(org.length()==0)
			return org;
		else{
			rlt=org.substring(0,1).toUpperCase()+org.substring(1);	
			return rlt;	
		}
	}

	/**
	 * format the orginal word, 
	 * if has multi-form, display all
	 * if is group word, display the group, without the keyword
	 * 1, find$found,finds, return the "find, found,finds"
	 * 2, kind#a kind of,   return "a kind of"
	 * @param words
	 * @return
	 */
	public static String formatWord(String words){
		try{	
			if(words.indexOf(UnicodeFunc.WordDivideChar)>=0){
				words = replace(words, UnicodeFunc.WordDivideLetter, ", ");
				return words.trim();
			}
			else if(words.indexOf(UnicodeFunc.WordGroupChar)>=0){
				return words.substring(words.indexOf(UnicodeFunc.WordGroupChar)+1, words.length()).trim();
			}
			else{
				return words.trim();
			}
		}catch(Exception e){
			//#debug error
			//System.out.println("Error in formatWord: "+e.getMessage());
			return words.trim();
		}
	}

	public static int[] appendInt(int[] oldarray, int newelement){
		try{
			int[] newarray = {newelement};
			return appendInt(oldarray, newarray);
		}catch(Exception e){
			//#debug error
			System.out.println("Error in appendInt: "+e.getMessage());
			return oldarray;
		}
	}

	public static int[] appendInt(int[] oldarray,int[] newarray){
		int[] last = oldarray;
		try{
			if(newarray == null || oldarray == null){
				return oldarray;
			}
			int oldlen = oldarray.length;
			int newlen = newarray.length;
			if(newlen == 0)
				return oldarray;

			last = new int[oldlen + newlen];
			System.arraycopy(oldarray, 0, last, 0, oldlen);
			System.arraycopy(newarray, 0, last, oldlen, newlen);

		}catch(Exception e){
			//#debug error
			System.out.println("E in appendInt: "+e.getMessage());
			return oldarray;
		}
		oldarray = null;
		newarray = null;
		return last;
	}

	public static byte[] appendByte(byte[] oldarray, byte[] newarray){
		byte[] last = oldarray;
		try{
			if(newarray == null || oldarray == null){
				return oldarray;
			}
			int oldlen = oldarray.length;
			int newlen = newarray.length;
			if(newlen == 0)
				return oldarray;

			last = new byte[oldlen + newlen];
			System.arraycopy(oldarray, 0, last, 0, oldlen);
			System.arraycopy(newarray, 0, last, oldlen, newlen);

		}catch(Exception e){
			//#debug error
			System.out.println("E in appendByte: "+e.getMessage());
			return oldarray;
		}
		oldarray = null;
		newarray = null;
		return last;
	}
	
	public static ItemMessage[] appendItemMessages(ItemMessage[] oldarray, ItemMessage newelement){
		try{
			ItemMessage[] newarray = {newelement};
			return appendItemMessages(oldarray, newarray);
		}catch(Exception e){
			//#debug error
			System.out.println("Error in appendItemTrans: "+e.getMessage());
			return oldarray;
		}
	}

	public static ItemMessage[] appendItemMessages(ItemMessage[] oldarray,ItemMessage[] newarray){
		ItemMessage[] last = oldarray;
		try{
			if(newarray == null || oldarray == null){
				return oldarray;
			}
			int oldlen = oldarray.length;
			int newlen = newarray.length;
			if(newlen == 0)
				return oldarray;

			last = new ItemMessage[oldlen + newlen];
			System.arraycopy(oldarray, 0, last, 0, oldlen);
			System.arraycopy(newarray, 0, last, oldlen, newlen);

		}catch(Exception e){
			//#debug error
			System.out.println("E in appendItemTrans: "+e.getMessage());
			return oldarray;
		}
		oldarray = null;
		newarray = null;
		return last;
	}

	/**
	 * 
	 * @param Object[] oldarray
	 * @param Object newelement
	 * @return
	 */
	public static Object[] appendArray(Object[] oldarray, Object newelement){
		try{
			Object[] newarray = {newelement};
			return appendArray(oldarray, newarray);
		}catch(Exception e){
			//#debug error
			System.out.println("Error in appendStringArray: "+e.getMessage());
			return oldarray;
		}
	}

	/**
	 * 
	 * @param Object[] oldarray
	 * @param Object[] newarray
	 * @return
	 */
	public static Object[] appendArray(Object[] oldarray, Object[] newarray){
		Object[] last = oldarray;
		try{
			if(newarray == null || oldarray == null){
				return oldarray;
			}
			int oldlen = oldarray.length;
			int newlen = newarray.length;
			if(newlen == 0)
				return oldarray;

			last = new Object[oldlen + newlen];
			System.arraycopy(oldarray, 0, last, 0, oldlen);
			System.arraycopy(newarray, 0, last, oldlen, newlen);

		}catch(Exception e){
			//#debug error
			System.out.println("E in appendArray: "+e.getMessage());
			return oldarray;
		}
		oldarray = null;
		newarray = null;
		return last;
	}

	/**
	 * format the sending URL, replace the space with %2B
	 * @param orgurl
	 * @return
	 */
	public static String formatUrlString(String orgurl){
		orgurl = UnicodeFunc.fixUmlaut(orgurl);
		String outputUrl = orgurl;
		try{			
			//outputUrl = TextUtil.encodeUrl(orgurl);//only use utf-8
			//outputUrl = URLEncoder.encode(orgurl);
			//not compatible for N95
			//outputUrl = CommonFunc.replace(new String(orgurl.getBytes("UTF-8"),"UTF-8"), " ", "+");

			outputUrl = CommonFunc.replace(orgurl, " ", "+");

			//outputUrl = orgurl.replace(' ', '+');
			return outputUrl;
		}catch(Exception e){
			//#debug error
			//System.out.println("Error in formatUrlString: "+e.getMessage());
			return CommonFunc.replace(orgurl, " ", "%2B");
		}
	}


	//encryption and decryption
	public static String writeMachineCode(){
		String strcode="LMFEQIBW";
		StringBuffer sbcode=new StringBuffer();
		int[] seed =new int[8];
		char[] code =new char[8];
		Random rdm=new Random();
		for(int i=0;i<8;i++){
			seed[i] =(rdm.nextInt()>>>1)%24+65;
			code[i]=(char)(seed[i]);
			sbcode.append(code[i]);
		}
		//System.out.println("strcode: "+sbcode.toString());
		strcode = sbcode.toString();
		return strcode;
	}

	public static boolean register(String input, String mcode){
		boolean breg=false;
		StringBuffer sbcode=new StringBuffer();
		char[] tem =input.toCharArray(); 
		int[] t=new int[8];
		char[] code =new char[8];
		for(int i=0;i<8;i++){
			t[i]=(tem[7-i]-65)*371;//(A-Z)(65-90)*371
			//even-->0-9
			if((i+2)%2==0){code[i]=(char) (t[i]%9+48);}
			//odd-->a-z
			else{code[i]=(char) (t[i]%24+97);}

		}
		for(int i=0;i<8;i++){
			sbcode.append(code[i]);
		}
		//System.out.println("strcode: "+sbcode.toString());
		if(mcode.equals(sbcode.toString())
				//#if modict.betatest		
				||mcode.equals(Setting.registerkey)
				//#endif        		
		){
			breg=true;
		}
		else{breg=false;}
		return breg;
	}

	public static int key =1;
	public static byte[] decyption(byte[] in,int size){
		int j;
		byte[]   out = new byte[size];
		for(int i=0;i<size;i++){
			j=in[i]+1;
			out[i]=(byte)j;	
		}
		return in;		
	}

	/**
	 * Retrieves a resource as a byte array.
	 * @param in the input stream of the resource, the input stream will be closed automatically
	 * @return the resource as byte array
	 * @throws IOException when the resource could not be read
	 */
	public static byte[] toByteArray(InputStream in)
	throws IOException
	{
		try {
			int bufferSize = /*in.available()*/8*1024;
			/*if (bufferSize <= 0) {
				bufferSize = 8*1024;
			}*/
			byte[] buffer = new byte[ bufferSize ];
			ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);
			int read;
			while ( (read = in.read(buffer, 0, bufferSize)) != -1) {
				out.write(buffer, 0, read);
			}
			return out.toByteArray();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException( e.toString() );
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	public static boolean equalsIgnoreCase(String str1, String str2){
		//#if polish.cldc1.1
		//# return str1.equalsIgnoreCase(str2);
		//#else
		if (str2 == null || str1.length() != str2.length() )
		{
			return false;
		}
		return str1.toLowerCase().equals(str2.toLowerCase());
		//#endif
	}

	/**
	 * 
	 * @param version 01021111->1.2
	 * @return
	 */
	public static String getVersion(String version){
		if(version.length()<6)
			return version;
		String major = version.substring(0,2);
		String minor = version.substring(2,4);
		if(major.startsWith("0")){
			major = major.substring(1, 2);
		}
		/*if(minor.startsWith("0")){
			minor = minor.substring(1, 2);
		}*/
		return major+"."+minor;
	}

	/**
	 * Check the phone number for validity
	 * Valid phone numbers contain only the digits 0 thru 9, and may contain
	 * a leading '+'.
	 */
	public static boolean isValidPhoneNumber(String number) {
		char[] chars = number.toCharArray();

		if (chars.length == 0) {
			return false;
		}else if (chars.length <= 7) {
			return false;
		}

		int startPos = 0;

		// initial '+' is OK
		if (chars[0] == '+') {
			startPos = 1;
		}

		for (int i = startPos; i < chars.length; ++i) {
			if (!Character.isDigit(chars[i])) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * return real unsigned byte from one integer
	 * @param b
	 * @return
	 * @notice :bei client, int error = in.readByte() & 0x80;
	 */
	public static byte unsignedByte(int b){
		int myByte = b;
		byte realByte = (byte)myByte;
		return realByte;
	}
	
	/**
	 * return real unsigned short from one integer
	 * @param i
	 * @return
	 * @notice :bei client, int error = in.readShort() & 0x80;
	 */
	public static short unsignedShort(int i){
		int myShort = i;	
		short realShort = (short)myShort;
		return realShort;
	}
	
	/**
	 * turn timemillis to 30.04.2008 15:34
	 * @param timemilles
	 * @return
	 */
	public static String getDateString(long timemilles){
		Calendar calstart = Calendar.getInstance();
		Date date = new Date();
		date.setTime(timemilles);
		calstart.setTime(date);

		String year = CommonFunc.doubleDigit(calstart.get(Calendar.YEAR));
		String month = CommonFunc.doubleDigit(calstart.get(Calendar.MONTH)+1);
		String day = CommonFunc.doubleDigit(calstart.get(Calendar.DAY_OF_MONTH));
		String hour = CommonFunc.doubleDigit(calstart.get(Calendar.HOUR_OF_DAY));
		String minute = CommonFunc.doubleDigit(calstart.get(Calendar.MINUTE));
		
		String datetime =  day + "."+ month + "."+  year + " " + hour+ ":"+minute;

		// #debug debug
		//System.out.println("mobile hardcode time:"+ datetime);
		return datetime;
	}
	
	/**
	 * if the number has one digit, turn it to 2 digits
	 * @param number
	 * @return
	 */
	public static String doubleDigit( int number )
	{
		if( number < 10 )
			return "0" + number;
		else
			return "" + number;
	}
	
}
