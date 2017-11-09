package net.mobabel.model;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.mobabel.item.ItemUTF8Lib;

import de.enough.polish.util.ArrayList;
import de.enough.polish.util.TextUtil;

import java.util.Vector;

public class UnicodeFunc {

	public final static int UNICODE_FONT_HEIGHT = 12;
	public final static int UNICODE_FONT_WIDTH = 12; 
	/**
	 * for small font 12x12 dot lib->12x12/8 = 18 byte
	 */
	public final static int FONT_OFFSET_SIZE = UNICODE_FONT_HEIGHT * UNICODE_FONT_WIDTH / 8; //12x12 /8 =18

	public static int UNICODECHAR_SPACE = 0;
	public static int LATINCHAR_SPACE = 0;
	//font height and width with border
	public static int hanziFontHB = 14;
	public static int hanziFontWB = 14;

	/**
	 * use 0x5E, because this char can be displayed in real phone as block
	 */
	public static char charNull = 0x5E; //  //0x0,  0xB3(asc179)
	public static char charNewLine = 0x0A; // NL line feed,new line, '\n'
	public static char charTab = 0x0B; // VT line feed,new line, '\t'
	public static char charReturn = 0x0D; //CR carriage return
	public static char charSeparator = 0x7C; //SEPARATOR  '|';

	public static char WordDivideChar = '$';
	public static String WordDivideLetter = "$";
	public static char WordDivideCharReplace = ',';
	public static char WordGroupChar = '#';
	public static String WordGroupLetter = "#";

	public static int[] verify = { 128, 64, 32, 16, 8, 4, 2, 1 };

	public static char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/*
	 * @gb2312 code in string format, for example "D2BB"
	 * @return byte array: 
	 * first change to: 0xd2 and 0xbb in Hex String format 
	 * then turn to byte[]
	 */
	public static byte[] Gb2312CodeToByteArray(String in) {
		byte[] out = new byte[2];
		int[] hex = new int[2];
		String gbcode0 = in.substring(0, 2);
		String gbcode1 = in.substring(2, 4);
		hex[0] = parseHex(gbcode0.toUpperCase());
		hex[1] = parseHex(gbcode1.toUpperCase());	
		out[0] = (byte) (hex[0]);
		out[1] = (byte) (hex[1]);
		return out;
	}
	
	public static String ByteArrayToHexString(byte[] b) {
		StringBuffer s = new StringBuffer(2 * b.length);
		for (int i = 0; i < b.length; i++) {
			int v = b[i] & 0xff;
			s.append((char)hexDigit[v >> 4]);
			s.append((char)hexDigit[v & 0xf]);
		}
		return s.toString();
	}

	/*
	 * Hex to decimal
	 */
	public static int digitVal(char c) {
		if (c == '0')
			return 0;
		if (c == '1')
			return 1;
		if (c == '2')
			return 2;
		if (c == '3')
			return 3;
		if (c == '4')
			return 4;
		if (c == '5')
			return 5;
		if (c == '6')
			return 6;
		if (c == '7')
			return 7;
		if (c == '8')
			return 8;
		if (c == '9')
			return 9;
		if (c == 'A')
			return 10;
		if (c == 'B')
			return 11;
		if (c == 'C')
			return 12;
		if (c == 'D')
			return 13;
		if (c == 'E')
			return 14;
		if (c == 'F')
			return 15;
		else
			return 0; // may want to return a negative, or throw exception
	}

	/**
	 * 
	 * @param hexString
	 *            XX
	 * @return hex int in 0xXX format
	 */
	public static int parseHex(String hexString) {
		int value = 0;
		for (int i = 0; i < hexString.length(); i++) {
			value = value * 16 + digitVal(hexString.charAt(i));
		}
		return value ;//& 0x0f;
	}
	
	/**
	 * Convert the byte array to an int.
	 *
	 * @param b The byte array
	 * @return The integer
	 */
	public static int byteArrayToInt(byte[] b) {
		return byteArrayToInt(b, 0);
	}

	/**
	 * Convert the byte array to an int starting from the given offset.
	 *
	 * @param b The byte array
	 * @param offset The array offset
	 * @return The integer
	 */
	public static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

	/*
	 * for short hanzi dot lib
	 */
	private static int offset_size_big = 24;//8x2 x 12/8 = 24
	/*
	 * hanziDotLib the gb2312 font dot library ch could be one single chinese byte
	 * array or chinese strings byte array
	 */
	public static Image createSingleChineseWord(byte[] hanziDotLib, byte[] ch,
			int off, int textColor, int bgColor) {
		if(bgColor == 0 )bgColor = 0x00ffffff;
		// Create mutable image
		Image imfont = Image.createImage(12, 12);
		// Get graphics object to draw onto the image
		Graphics graphics = imfont.getGraphics();
		int q1 = ch[off] & 0xff;
		int q2 = ch[off + 1] & 0xff;
		int offset = (q1 - 0xa1) * 94 * offset_size_big;
		q2 -= 0xa1;
		offset += q2 * offset_size_big;
		//System.out.println("point postion: " + offset);

		for (int h = 0; h < 12; h++) {
			byte b = hanziDotLib[offset++];
			for (int w = 0; w < 8; w++) {
				//graphics.setColor(0xE22F16);
				//drawPoint(w, h, graphics, true);
				//graphics.drawLine(w, h, w, h);
				if ((b & verify[w]) == verify[w]) {
					graphics.setColor(textColor);
					drawPoint(w, h, graphics);
					//graphics.drawLine(w, h, w, h);
				} else {
					graphics.setColor(bgColor);
					drawPoint(w, h, graphics);
					//graphics.drawLine(w, h, w, h);
				}
			}
			b = hanziDotLib[offset++];
			// System.out.println("offset: "+offset);
			for (int w = 0; w < 4; w++) {
				if ((b & verify[w]) == verify[w]) {
					graphics.setColor(textColor);
					drawPoint(w + 8, h, graphics);
					//graphics.drawLine(w + 8, h, w + 8, h);
				} else {
					graphics.setColor(bgColor);
					drawPoint(w + 8, h, graphics);
					//graphics.drawLine(w + 8, h, w + 8, h);
				}
			}
		}
		return imfont;
	}

/*	public static void blend(int[] raw, int alphaValue, int maskColor, int dontmaskColor){
		int len = raw.length;

		for(int i=0; i<len; i++){
			int a = 0;
			int color = (raw[i] & 0x00FFFFFF);
			if(maskColor==color){
				a = 0;
			}else if(dontmaskColor==color){
				a = 255;
			}else if(alphaValue>0){
				a = alphaValue;
			}

			a = (a<<24);
			color += a;
			raw[i] = color;
		}
	}
	public static void blend(int[] raw, int alphaValue){
		blend(raw, alphaValue, 0xFFFFFFFF, 0xFFFFFFFF);
	}*/

	public static Image createSingleChineseWord12(byte[] ch,
			int off, int textColor, int bgColor) {
		// Create mutable image
		Image imfont = getFongBg();
		byte[] fontDotLib = null;
		if(ItemUTF8Lib.getInstance().getUseFontDotLibInMemory()){
			fontDotLib = ItemUTF8Lib.getInstance().getFontDotLib();
			// #debug error
			//System.out.println("dot in memory " );
		}else{
			fontDotLib = ItemUTF8Lib.getInstance().getRSFontLib().getCharByte(ch);
			// #debug error
			//System.out.println("dot in rs " );
		}

		try{
			if(bgColor == 0){
				//TODO should be dynamic!!!!!
				bgColor = 0x00ffffff;
				//#if ${mocs.skin} == halloween2008
				bgColor = 0x0009080D;
				//#endif
			}
			// Get graphics object to draw onto the image
			Graphics graphics = imfont.getGraphics();
			/*graphics.drawRGB(RGBData, 0, 12, 0, 0, 12, 12, true);*/
			int offset = 0;
			if(ItemUTF8Lib.getInstance().getUseFontDotLibInMemory()){
				int q1 = ch[off] & 0xff;
				int q2 = ch[off + 1] & 0xff;		
				offset = ((q1 - 0xa1) * 94 + (q2 - 0xa1)) * FONT_OFFSET_SIZE;
				// #debug debug
				//System.out.println("offset: "+offset);
			}
			//System.out.println("point postion: " + offset);		 
			for (int h = 0; h < 12; h++) {
				//0,2,4,6,8,10
				if(h%2==0){
					byte b = fontDotLib[offset++];
					for (int w = 0; w < 8; w++) {
						if ((b & verify[w]) == verify[w]) {
							graphics.setColor(textColor);
							drawPoint(w, h, graphics);
						} else {
							graphics.setColor(bgColor);
							drawPoint(w, h, graphics);
						}
					}
					b = fontDotLib[offset++];
					for (int w = 0; w < 8; w++) {
						if(w < 4){
							if ((b & verify[w]) == verify[w]) {
								graphics.setColor(textColor);
								drawPoint(w + 8, h, graphics);
							} else {
								graphics.setColor(bgColor);
								drawPoint(w + 8, h, graphics);
							}
						}else{// go to next line
							if ((b & verify[w]) == verify[w]) {
								graphics.setColor(textColor);
								drawPoint(w-4, h+1, graphics);
							} else {
								graphics.setColor(bgColor);
								drawPoint(w-4, h+1, graphics);
							}
						}
					}
				}//1,3,5,7,9,11
				else if(h%2==1){
					byte b = fontDotLib[offset++];
					for (int w = 0; w < 8; w++) {
						if ((b & verify[w]) == verify[w]) {
							graphics.setColor(textColor);
							drawPoint(w+4, h, graphics);
						} else {
							graphics.setColor(bgColor);
							drawPoint(w+4, h, graphics);
						}
					}
				}			
			}
		}catch(Exception e){
			//#debug error
			System.out.println("createSingleChineseWord12: "+e.getMessage());
			return imfont;
		}
		return imfont;
	}
	
	/**
	 * get the font image as background image
	 * @return
	 */
	private static Image getFongBg(){
		try{
			//return Image.createImage("/fg.png");
			return Image.createImage(UnicodeFunc.UNICODE_FONT_WIDTH, UnicodeFunc.UNICODE_FONT_HEIGHT);
		}catch(Exception e){
			return Image.createImage(UnicodeFunc.UNICODE_FONT_WIDTH, UnicodeFunc.UNICODE_FONT_HEIGHT);
		}
	}

	public static Image createSingleChineseWord12o(byte[] ch,
			int off, int textColor, int bgColor) {
		byte[] fontDotLib = ItemUTF8Lib.getInstance().getFontDotLib();
		// Create mutable image
		Image imfont = Image.createImage(UNICODE_FONT_WIDTH, UNICODE_FONT_HEIGHT);
		/*		int[] RGBData = new int[UNICODE_FONT_WIDTH*UNICODE_FONT_HEIGHT];
		int alf = 10;//0-10
		int tmp = ((alf*255/10) << 24)|0x00ffffff;
		for(int i=0;i<RGBData.length;i++)
			RGBData[i] &= tmp;*/
		//blend(RGBData, 0);
		//Image im = Image.createRGBImage(RGBData,UNICODE_FONT_WIDTH,UNICODE_FONT_HEIGHT,true);

		try{
			if(bgColor == 0 )bgColor = 0x00ffffff;
			// Get graphics object to draw onto the image
			Graphics graphics = imfont.getGraphics();
			/*graphics.drawRGB(RGBData, 0, 12, 0, 0, 12, 12, true);*/
			int q1 = ch[off] & 0xff;
			int q2 = ch[off + 1] & 0xff;		
			int offset = ((q1 - 0xa1) * 94 + (q2 - 0xa1)) * FONT_OFFSET_SIZE;
			//System.out.println("point postion: " + offset);		 
			for (int h = 0; h < 12; h++) {
				//0,2,4,6,8,10
				if(h%2==0){
					byte b = fontDotLib[offset++];
					for (int w = 0; w < 8; w++) {
						if ((b & verify[w]) == verify[w]) {
							graphics.setColor(textColor);
							drawPoint(w, h, graphics);
						} else {
							//graphics.setColor(bgColor);
							//drawPoint(w, h, graphics);
						}
					}
					b = fontDotLib[offset++];
					for (int w = 0; w < 8; w++) {
						if(w < 4){
							if ((b & verify[w]) == verify[w]) {
								graphics.setColor(textColor);
								drawPoint(w + 8, h, graphics);
							} else {
								//graphics.setColor(bgColor);
								//drawPoint(w + 8, h, graphics);
							}
						}else{// go to next line
							if ((b & verify[w]) == verify[w]) {
								graphics.setColor(textColor);
								drawPoint(w-4, h+1, graphics);
							} else {
								//graphics.setColor(bgColor);
								//drawPoint(w-4, h+1, graphics);
							}
						}
					}
				}//1,3,5,7,9,11
				else if(h%2==1){
					byte b = fontDotLib[offset++];
					for (int w = 0; w < 8; w++) {
						if ((b & verify[w]) == verify[w]) {
							graphics.setColor(textColor);
							drawPoint(w+4, h, graphics);
						} else {
							//graphics.setColor(bgColor);
							//drawPoint(w+4, h, graphics);
						}
					}
				}			
			}
		}catch(Exception e){
			//#debug error
			System.out.println("createSingleChineseWord12o: "+e.getMessage());
			return imfont;
		}
		return imfont;
	}

	/*
	 * true draw with text color
	 * false draw with background color
	 * TODO can not be used with polish stringitemutf8
	 */
	public static void drawPoint(int x, int y, Graphics g) {
		g.drawLine(x, y, x, y);
	}
	
	/**
	 * 
	 * @param chr
	 * @return
	 */
	public static boolean isChineseChar(char chr){
		return isChineseChar(chr+"");
	}

	/**
	 * 
	 * check whether the single string is chinese
	 * @notice: the special char must be updated if new language is added
	 * @param str
	 * @return
	 */
	public static boolean isChineseChar(String str) {
		if (null == str || "".equals(str)) {
			return false;
		}
		byte[] abytes = null;
		try {
			abytes = str.getBytes("UTF-8");
		} catch (Exception ex) {
		}
		//TODO, focus on language, not all
		//chinese chars or related chars have 3-4 byte
		//the special char take also 2 byte, is not chinese
		if(abytes.length>2){
			/*//the europe char is between 128-255(0x7F-0xFF)
			if(abytes[0]== 0x00 && abytes[1]<= 0xFF){
				return false;
			}
			 */
			/*if(("\u00e1" +
					"\u00e2" +
					"\u00e3" +
					"\u00e4" +
					"\u00e5" +
					"\u00e6" +
					"\u00e7" +
					"\u00e8" +
					"\u00e9" +
					"\u00ea" +
					"\u00eb" +
					"\u00ec" +
					"\u00ed" +
					"\u00ee" +
					"\u00ef" +
					"\u00f1" +
					"\u00f2" +
					"\u00f3" +
					"\u00f4" +
					"\u00f5" +
					"\u00f6" +
					"\u00df" +
					"\u00f9" +
					"\u00fa" +
					"\u00fb" +
					"\u00fc" +
			"\u00fd").indexOf(s.toLowerCase())>0){
				return false;
			}
			return true;*/
			return true;
		}
		//the left will be one char
		else{
			return false;
		}
	}

	/**
	 * consider of newline and tab
	 * tab will insert 8 space instead
	 * 
	 * @param str
	 * @param font
	 * @param rowMaxW
	 * @return
	 */
/*	public static final String[] clipString(String str,Font font,int rowMaxW){
		if(str == null)
			return null;
		//if(rowMaxW < font.charWidth('哈'))
		//rowMaxW = font.charWidth('哈');         
		int strID = 0;
		int rowW = 0;
		Vector strManager = new Vector();
		char ch = ' ';
		while(str.length() > strID){
			ch = str.charAt(strID);
			switch(ch)
			{
			case '\n':
				strManager.addElement(str.substring(0,strID));
				str = str.substring(strID+1);
				rowW = 0;
				strID = 0;
				break;
			case '\t':
				StringBuffer sb = new StringBuffer(str);
				sb.deleteCharAt(strID);
				sb.insert(strID,"       ");
				str = sb.toString();
				break;
			default:
				if(rowW + font.charWidth(ch) > rowMaxW){
					strManager.addElement(str.substring(0,strID));
					str = str.substring(strID);
					rowW = 0;
					strID = 0;
				}else{
					rowW += font.charWidth(ch);
					strID++;
				}
			}
		}
		strManager.addElement(str);
		String[] o_Str = new String[strManager.size()];
		strManager.copyInto(o_Str);
		return o_Str;
	}*/

	/*
	 * break the string into string parts,
	 *  and the width of each part is no more than line width
	 *  padding left does not work from polish, so minus (paddingRight) for each line
	 */
	public static String[] wrap(String text, Font font, int lineWidth){
		int paddingRight = 10;
		String[] lines = null;
		Vector lineManager = new Vector();
		StringBuffer linebuffer = new StringBuffer();
		String singleletter = null;
		char ch = ' ';
		int width = 0;
		for(int i = 0, length = text.length(); i < length; i++ ){
			ch = text.charAt(i);
			singleletter = text.substring(i, i+1);
			//dont display the charNull from textfieldutf8
			//if(singleletter != String.valueOf(charNull)){
			
			switch(ch)
			{
			case '\n':
				lineManager.addElement(linebuffer.toString());
				linebuffer = new StringBuffer();
				width = 0;
				break;
			case '\t':
				linebuffer.append("  ");
				width += font.stringWidth("  ");
				break;
			default:
				//#if {cfg.osHasFontAlready} != true
					if(isChineseChar(singleletter)){
						width += UNICODE_FONT_WIDTH + UNICODECHAR_SPACE;
					}else{
						width += font.stringWidth(singleletter);
					}
				//#else
					width += font.stringWidth(singleletter);
				//#endif
				linebuffer.append(singleletter);
			}

			if(width >= lineWidth-paddingRight){
				width = 0;
				lineManager.addElement(linebuffer.toString());
				linebuffer = new StringBuffer();
			}
			if(width<lineWidth-paddingRight && i == text.length()-1){
				lineManager.addElement(linebuffer.toString());
			}

			//System.out.println("width: " + width);
			//}
		}
		int size = lineManager.size();
/*		if(size<=0){
			lineManager.addElement(text);
			size = 1;
		}*/
		lines = new String[size];
		lineManager.copyInto(lines);
		for(int i = 0; i < size; i++ ){
			// #debug debug
			//System.out.println("lines[i]: " + lines[i]);
		}

		return lines;
	}

	
	/**
	 * get the current char width
	 * @param cha
	 * @param font
	 * @return
	 */
	public static int getUtf8CharWidth(char cha, Font font){
		int charwidth = 0;
		int chatwidth = font.charWidth( cha );
		if( charwidth  >= UNICODE_FONT_WIDTH)
			return chatwidth;
		else
			return UNICODE_FONT_WIDTH;
	}
	
	/**
	 * 
	 * @param font
	 * @return
	 */
	public static int getUtf8FontHeight(Font font){
		if(UNICODE_FONT_HEIGHT >= font.getHeight()){
			return UNICODE_FONT_HEIGHT;
		}else 	
			return font.getHeight();
	}

	/**
	 * calculate the width of string
	 * @param text
	 * @param font
	 * @return
	 */
	public static int stringUtf8Width(String text, Font font){
		int width = 0;
		if(text ==null)return 0;
		try{
			//#if {cfg.osHasFontAlready} != true
				String singleletter = "";
				for(int i = 0, length = text.length(); i < length; i++ ){
					singleletter = text.substring(i, i+1);
					if(isChineseChar(singleletter)){
						width += UNICODE_FONT_WIDTH;
					}else{
						width += font.stringWidth(singleletter);
					}        	
				}
			//#else
				width = font.stringWidth(text);
			//#endif
		}catch (Exception e) {
			//#debug error
			System.out.println("Error in stringUtf8Width: "+e.toString());
		}
		// #debug debug
		//System.out.println("text&width: " +text+"&"+ width);
		return width;
	}

	/**
	 * Component for readUnicodeFileUTF8
	 * @param is
	 * @return
	 */
	public final static int readNextCharFromStreamUTF8(InputStream is) {
		int c = -1;
		if (is == null)
			return c;
		boolean complete = false;

		try {
			int byteVal;
			int expecting = 0;
			int composedVal = 0;

			while (!complete && (byteVal = is.read()) != -1) {
				if (expecting > 0 && (byteVal & 0xC0) == 0x80) { /* 10xxxxxx */
					expecting--;
					composedVal = composedVal
					| ((byteVal & 0x3F) << (expecting * 6));
					if (expecting == 0) {
						c = composedVal;
						complete = true;
						// System.out.println("appending: U+" +
						// Integer.toHexString(composedVal) );
					}
				} else {
					composedVal = 0;
					expecting = 0;
					if ((byteVal & 0x80) == 0) { /* 0xxxxxxx */
						// one byte character, no extending byte expected
						c = byteVal;
						complete = true;
						// System.out.println("appending: U+" +
						// Integer.toHexString(byteVal) );
					} else if ((byteVal & 0xE0) == 0xC0) { /* 110xxxxx */
						expecting = 1; // expecting 1 extending byte
						composedVal = ((byteVal & 0x1F) << 6);
					} else if ((byteVal & 0xF0) == 0xE0) { /* 1110xxxx */
						expecting = 2; // expecting 2 extending bytes
						composedVal = ((byteVal & 0x0F) << 12);
					} else if ((byteVal & 0xF8) == 0xF0) { /* 11110xxx */
						expecting = 3; // expecting 3 extending bytes
						composedVal = ((byteVal & 0x07) << 18);
					} else {
						// non conformant utf-8, ignore or catch error
					}
				}
			}

		} catch (Exception e) {
			//#debug error
			System.out.println("Error in readNextCharFromStreamUTF8: "+e.toString());
		}

		return c;
	}

	/**
	 * Component for readUnicodeFileUTF8
	 * @param codePoint
	 * @param surrogatePair
	 */
	public final static void supplementCodePointToSurrogatePair(int codePoint,
			int[] surrogatePair) {
		int high4 = ((codePoint >> 16) & 0x1F) - 1;
		int mid6 = ((codePoint >> 10) & 0x3F);
		int low10 = codePoint & 0x3FF;

		surrogatePair[0] = (0xD800 | (high4 << 6) | (mid6));
		surrogatePair[1] = (0xDC00 | (low10));
	}

	/**
	 * get the string in utf-8
	 * @param buf
	 * @param start
	 * @param len
	 * @return
	 */
	public static String getStringUtf8(byte[] buf, int start, int len) {
		String strutf8 = "";
		try {
			strutf8 = new String(buf, start, len, "UTF-8");
		} catch (Exception e/*UnsupportedEncodingException e*/) {
			//#debug error
			System.out.println("Error in getStringUtf8: " +e.toString());
		} 
		return strutf8;
	}

	public static byte[] String2UTF8(String str){
		byte[] buffer=null;
		try{
			ByteArrayOutputStream baos=new ByteArrayOutputStream(50);
			DataOutputStream dos=new DataOutputStream(baos);
			dos.writeUTF(str);

			buffer=baos.toByteArray();
			//for(int i=0;i<buffer.length;i++)
			//{System.out.print(buffer[i]+" byte ");}
		} catch (IOException e) {
			//#debug error
			System.out.println("Error in String2UTF8: "+e);
		}
		return buffer;
	}

	/**
	 * delete the charNull in one text string
	 * @param text
	 * @return
	 */
	public static String StringDeleteCharNull(String text){
		if(text==null) return null;
		if(text.equals(""))return "";
		char[] chararray = text.toCharArray();
		StringBuffer tem = new StringBuffer();
		try{
			for(int i=0;i<chararray.length;i++){
				if(chararray[i]!= charNull){
					tem.append(chararray[i]);
				}
			}
		}catch (Exception e) {
			//#debug error
			System.out.println("Error in StringDeleteCharNull: "+e);
		}
		if(tem.length() == 0){
			return "";
		}
		return tem.toString();
	}

	/**
	 * extend the CompareTo to fit the special chars like umlaut
	 * @param first
	 * @param last
	 * @return
	 */
	public static int CompareTo(String first, String last){
		first = first.toLowerCase();
		last = last.toLowerCase();
		if(first.compareTo(last)==0)
			return 0;
		else{
			//de
			//#if ${modict.word} == de or ${modict.translation}==de
			if(first.indexOf("\u00E4")>=0)
				first = CommonFunc.replace(first, "\u00E4", "ae");
			if(first.indexOf("\u00F6")>=0)
				first = CommonFunc.replace(first, "\u00F6", "oe");
			if(first.indexOf("\u00FC")>=0)
				first = CommonFunc.replace(first, "\u00FC", "ue");
			if(first.indexOf("\u00DF")>=0)
				first = CommonFunc.replace(first, "\u00DF", "ss");
			//#endif

			//fr
			//#if ${modict.word} == fr or ${modict.translation}==fr
			if(first.indexOf("\u00e0")>=0)
				first = CommonFunc.replace(first, "\u00e0", "a0");
			if(first.indexOf("\u00e2")>=0)
				first = CommonFunc.replace(first, "\u00e2", "a1");
			if(first.indexOf("\u00c9")>=0)
				first = CommonFunc.replace(first, "\u00c9", "e0");
			if(first.indexOf("\u00e8")>=0)
				first = CommonFunc.replace(first, "\u00e8", "e1");
			if(first.indexOf("\u00e9")>=0)
				first = CommonFunc.replace(first, "\u00e9", "e2");
			if(first.indexOf("\u00ea")>=0)
				first = CommonFunc.replace(first, "\u00ea", "e3");
			if(first.indexOf("\u00eb")>=0)
				first = CommonFunc.replace(first, "\u00eb", "e4");
			if(first.indexOf("\u00ee")>=0)
				first = CommonFunc.replace(first, "\u00ee", "i0");
			if(first.indexOf("\u00ef")>=0)
				first = CommonFunc.replace(first, "\u00ef", "i1");
			if(first.indexOf("\u00f4")>=0)
				first = CommonFunc.replace(first, "\u00f4", "o0");
			if(first.indexOf("\u00db")>=0)
				first = CommonFunc.replace(first, "\u00db", "u0");
			if(first.indexOf("\u00f9")>=0)
				first = CommonFunc.replace(first, "\u00f9", "u1");
			if(first.indexOf("\u00fc")>=0)
				first = CommonFunc.replace(first, "\u00fc", "u2");
			if(first.indexOf("\u00e7")>=0)
				first = CommonFunc.replace(first, "\u00e7", "c0");
			if(first.indexOf("\u0153")>=0)
				first = CommonFunc.replace(first, "\u0153", "oe");
			//#endif

			//es
			//#if ${modict.word} == es or ${modict.translation}== es
			if(first.indexOf("\u00e1")>=0)
				first = CommonFunc.replace(first, "\u00e1", "a");
			if(first.indexOf("\u00e9")>=0)
				first = CommonFunc.replace(first, "\u00e9", "e");
			if(first.indexOf("\u00ed")>=0)
				first = CommonFunc.replace(first, "\u00ed", "i");
			if(first.indexOf("\u00f3")>=0)
				first = CommonFunc.replace(first, "\u00f3", "o");
			if(first.indexOf("\u00fa")>=0)
				first = CommonFunc.replace(first, "\u00fa", "u0");
			if(first.indexOf("\u00fc")>=0)
				first = CommonFunc.replace(first, "\u00fc", "u1");
			//#endif
			
			//it
			//#if ${modict.word} == it or ${modict.translation} == it
			if(first.indexOf("\u00e0")>=0)
				first = CommonFunc.replace(first, "\u00e0", "a");
			if(first.indexOf("\u00e8")>=0)
				first = CommonFunc.replace(first, "\u00e8", "e1");
			if(first.indexOf("\u00e9")>=0)
				first = CommonFunc.replace(first, "\u00e9", "e2");
			if(first.indexOf("\u00ec")>=0)
				first = CommonFunc.replace(first, "\u00ec", "i");
			if(first.indexOf("\u00e9")>=0)
				first = CommonFunc.replace(first, "\u00e9", "o");
			if(first.indexOf("\u00f9")>=0)
				first = CommonFunc.replace(first, "\u00f9", "u");
			//#endif
			//-----------------------------------------------------

			//de
			//#if ${modict.word} == de or ${modict.translation}==de
			if(last.indexOf("\u00E4")>=0)
				last = CommonFunc.replace(last, "\u00E4", "ae");
			if(last.indexOf("\u00F6")>=0)
				last = CommonFunc.replace(last, "\u00F6", "oe");
			if(last.indexOf("\u00FC")>=0)
				last = CommonFunc.replace(last, "\u00FC", "ue");
			if(last.indexOf("\u00DF")>=0)
				last = CommonFunc.replace(last, "\u00DF", "ss");	
			//#endif
			//fr
			//#if ${modict.word} == fr or ${modict.translation}==fr
			if(last.indexOf("\u00e0")>=0)
				last = CommonFunc.replace(last, "\u00e0", "a0");
			if(last.indexOf("\u00e2")>=0)
				last = CommonFunc.replace(last, "\u00e2", "a1");
			if(last.indexOf("\u00c9")>=0)
				last = CommonFunc.replace(last, "\u00c9", "e0");
			if(last.indexOf("\u00e8")>=0)
				last = CommonFunc.replace(last, "\u00e8", "e1");
			if(last.indexOf("\u00e9")>=0)
				last = CommonFunc.replace(last, "\u00e9", "e2");
			if(last.indexOf("\u00ea")>=0)
				last = CommonFunc.replace(last, "\u00ea", "e3");
			if(last.indexOf("\u00eb")>=0)
				last = CommonFunc.replace(last, "\u00eb", "e4");
			if(last.indexOf("\u00ee")>=0)
				last = CommonFunc.replace(last, "\u00ee", "i0");
			if(last.indexOf("\u00ef")>=0)
				last = CommonFunc.replace(last, "\u00ef", "i1");
			if(last.indexOf("\u00f4")>=0)
				last = CommonFunc.replace(last, "\u00f4", "o0");
			if(last.indexOf("\u00db")>=0)
				last = CommonFunc.replace(last, "\u00db", "u0");
			if(last.indexOf("\u00f9")>=0)
				last = CommonFunc.replace(last, "\u00f9", "u1");
			if(last.indexOf("\u00fc")>=0)
				last = CommonFunc.replace(last, "\u00fc", "u2");
			if(last.indexOf("\u00e7")>=0)
				last = CommonFunc.replace(last, "\u00e7", "c0");
			if(last.indexOf("\u0153")>=0)
				last = CommonFunc.replace(last, "\u0153", "oe");
			//#endif
			
			//es
			//#if ${modict.word} == es or ${modict.translation}== es
			if(last.indexOf("\u00e1")>=0)
				last = CommonFunc.replace(last, "\u00e1", "a");
			if(last.indexOf("\u00e9")>=0)
				last = CommonFunc.replace(last, "\u00e9", "e");
			if(last.indexOf("\u00ed")>=0)
				last = CommonFunc.replace(last, "\u00ed", "i");
			if(last.indexOf("\u00f3")>=0)
				last = CommonFunc.replace(last, "\u00f3", "o");
			if(last.indexOf("\u00fa")>=0)
				last = CommonFunc.replace(last, "\u00fa", "u0");
			if(last.indexOf("\u00fc")>=0)
				last = CommonFunc.replace(last, "\u00fc", "u1");
			//#endif
			
			//it
			//#if ${modict.word} == it or ${modict.translation} == it
			if(last.indexOf("\u00e0")>=0)
				last = CommonFunc.replace(last, "\u00e0", "a");
			if(last.indexOf("\u00e8")>=0)
				last = CommonFunc.replace(last, "\u00e8", "e1");
			if(last.indexOf("\u00e9")>=0)
				last = CommonFunc.replace(last, "\u00e9", "e2");
			if(last.indexOf("\u00ec")>=0)
				last = CommonFunc.replace(last, "\u00ec", "i");
			if(last.indexOf("\u00e9")>=0)
				last = CommonFunc.replace(last, "\u00e9", "o");
			if(last.indexOf("\u00f9")>=0)
				last = CommonFunc.replace(last, "\u00f9", "u");
			//#endif

			// #debug debug
			//System.out.println(first+"/"+last);
			return first.compareTo(last);
		}
	}

	/**
	 * 
	 * @param word
	 * @param prefix
	 * @param matchSpecialChar, flag to match the special char like umlaut
	 * @return
	 */
	public static boolean startsWithIgnoreCase(String word, String prefix, boolean matchSpecialChar){
		word = word.toLowerCase();
		prefix = prefix.toLowerCase();
		if(!matchSpecialChar){
			return word.startsWith(prefix);
		}else{
			word = replaceSpecialChar(word);
			prefix = replaceSpecialChar(prefix);
			return word.startsWith(prefix);
		}
	}

	/**
	 * TODO should use ae,oe,ue for german?
	 * replace the special char, does not care some chars have same mother
	 * @param org, in lowercase, dont care the uppercase
	 * @return
	 */
	public static String replaceSpecialChar(String org){
		try{
			//de
			//#if ${modict.word} == de or ${modict.translation}==de
			if(org.indexOf("\u00E4")>=0)
				org = CommonFunc.replace(org, "\u00E4", "a");
			if(org.indexOf("\u00F6")>=0)
				org = CommonFunc.replace(org, "\u00F6", "o");
			if(org.indexOf("\u00FC")>=0)
				org = CommonFunc.replace(org, "\u00FC", "u");
			if(org.indexOf("\u00DF")>=0)
				org = CommonFunc.replace(org, "\u00DF", "ss");
			//#endif

			//fr
			//#if ${modict.word} == fr or ${modict.translation}==fr
			if(org.indexOf("\u00e0")>=0)
				org = CommonFunc.replace(org, "\u00e0", "a");
			if(org.indexOf("\u00e2")>=0)
				org = CommonFunc.replace(org, "\u00e2", "a");
			if(org.indexOf("\u00c9")>=0)
				org = CommonFunc.replace(org, "\u00c9", "e");
			if(org.indexOf("\u00e8")>=0)
				org = CommonFunc.replace(org, "\u00e8", "e");
			if(org.indexOf("\u00e9")>=0)
				org = CommonFunc.replace(org, "\u00e9", "e");
			if(org.indexOf("\u00ea")>=0)
				org = CommonFunc.replace(org, "\u00ea", "e");
			if(org.indexOf("\u00eb")>=0)
				org = CommonFunc.replace(org, "\u00eb", "e");
			if(org.indexOf("\u00ee")>=0)
				org = CommonFunc.replace(org, "\u00ee", "i");
			if(org.indexOf("\u00ef")>=0)
				org = CommonFunc.replace(org, "\u00ef", "i");
			if(org.indexOf("\u00f4")>=0)
				org = CommonFunc.replace(org, "\u00f4", "o");
			if(org.indexOf("\u00db")>=0)
				org = CommonFunc.replace(org, "\u00db", "u");
			if(org.indexOf("\u00f9")>=0)
				org = CommonFunc.replace(org, "\u00f9", "u");
			if(org.indexOf("\u00fc")>=0)
				org = CommonFunc.replace(org, "\u00fc", "u");
			if(org.indexOf("\u00e7")>=0)
				org = CommonFunc.replace(org, "\u00e7", "c");
			if(org.indexOf("\u0153")>=0)
				org = CommonFunc.replace(org, "\u0153", "oe");
			//#endif	
			
			//es
			//#if ${modict.word} == es or ${modict.translation}== es
			if(org.indexOf("\u00e1")>=0)
				org = CommonFunc.replace(org, "\u00e1", "a");
			if(org.indexOf("\u00e9")>=0)
				org = CommonFunc.replace(org, "\u00e9", "e");
			if(org.indexOf("\u00ed")>=0)
				org = CommonFunc.replace(org, "\u00ed", "i");
			if(org.indexOf("\u00f3")>=0)
				org = CommonFunc.replace(org, "\u00f3", "o");
			if(org.indexOf("\u00fa")>=0)
				org = CommonFunc.replace(org, "\u00fa", "u");
			if(org.indexOf("\u00fc")>=0)
				org = CommonFunc.replace(org, "\u00fc", "u");
			//#endif
			
			//it
			//#if ${modict.word} == it or ${modict.translation} == it
			if(org.indexOf("\u00e0")>=0)
				org = CommonFunc.replace(org, "\u00e0", "a");
			if(org.indexOf("\u00e8")>=0)
				org = CommonFunc.replace(org, "\u00e8", "e");
			if(org.indexOf("\u00e9")>=0)
				org = CommonFunc.replace(org, "\u00e9", "e");
			if(org.indexOf("\u00ec")>=0)
				org = CommonFunc.replace(org, "\u00ec", "i");
			if(org.indexOf("\u00e9")>=0)
				org = CommonFunc.replace(org, "\u00e9", "o");
			if(org.indexOf("\u00f9")>=0)
				org = CommonFunc.replace(org, "\u00f9", "u");
			//#endif

			// #debug info
			//System.out.println("string in replaceSpecialChar: "+ org);
			return org;
		}catch(Exception e){
			//#debug error
			//System.out.println("E in replaceSpecialChar: "+e.getMessage());
			return org;
		}

	}

	/**
	 * 
	 * and Server side should delete the space before it!!!
	 * @param org
	 * @return
	 */
	public static String fixUmlaut(String org){
		String out = org;
		try{
			if(org.indexOf("\u00fc")>0 || org.indexOf("\u00dc")>0){
				out = TextUtil.replace(org, "\u00fc", " ue");
				out = TextUtil.replace(out, "\u00dc", " Ue");
			}
			if (org.indexOf("\u00E4") > 0 || org.indexOf("\u00C4") > 0) {
				out = TextUtil.replace(out, "\u00E4", " ae");
				out = TextUtil.replace(out, "\u00C4", " Ae");
			}
			if (org.indexOf("\u00F6") > 0 || org.indexOf("\u00D6") > 0) {
				out = TextUtil.replace(out, "\u00F6", " oe");
				out = TextUtil.replace(out, "\u00D6", " Oe");
			}
			if (org.indexOf("\u00df") > 0) {
				out = TextUtil.replace(out, "\u00df", " ss");
			}
			// #debug info
			//System.out.println("string in fixUmlaut: "+ out);
			return out;
		}catch(Exception e){
			//#debug error
			//System.out.println("E in fixUmlaut: "+e.getMessage());
			return org;
		}
	}

}
