package net.mobabel.model;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import net.mobabel.item.ItemUTF8Lib;
import com.java4ever.apime.io.GZIP;

import de.enough.polish.ui.Screen;
import de.enough.polish.ui.TextField;

public class InputMethodUtf8 {
//#if polish.TextField.useDirectInput && !polish.blackberry
	//#define tmp.forceDirectInput
	//#define tmp.directInput
//#elif polish.css.textfield-direct-input && !polish.blackberry
	//#define tmp.directInput
	//#define tmp.allowDirectInput
//#endif
//#if polish.key.supportsAsciiKeyMap || polish.key.maybeSupportsAsciiKeyMap
	//#define tmp.supportsAsciiKeyMap
//#endif

	public static int KEY_BACKSPACE = -8;
//#if {cfg.useLocalInput} == true
	Hashtable hashtablePinY = new Hashtable();

	String[] wordKey;

	Vector wordKeys = new Vector();

	int[] inputKey = new int[10];

	int currentPos;

	/**
	 * matched unicode char array
	 */
	String[] currentInputPad;

	public boolean stateUtfInputNow = false;

	/**
	 * matched Pinyin array
	 */
	public Vector keyAvailable = new Vector(0, 1);

	/**
	 * save last pinyin
	 */
	Vector wordsBuffer;

	int select, charSelect;

	int screenW, screenH;

	int num_row;// hanzi number in each line

	int charPage;// page

	//the chinese letter
	public String inputChar;

	public int inputMode;// 0letter 1number 2pinyin

	//String[] inputModeTxt = { "abc", "Abc", "ABC", "123", "PinY", "Nat." };

	/**
	 * input content, the first dimenstion is real content--unicode char, 
	 * 2-d and 3-d is used to the content by the cursur
	 */
	public String content[] = { "", "", "" };

	public int charPos;// Cursur position

	long time1, time2;// push the button time

	int charKeyPos;// change the letter

	String[][] charKey = new String[][] { { "a", "b", "c" }, { "d", "e", "f" },
			{ "g", "h", "i" }, { "j", "k", "l" }, { "m", "n", "o" },
			{ "p", "q", "r", "s" }, { "t", "u", "v" }, { "w", "x", "y", "z" } };

	private Hashtable tableUtf8LocalCode = null;
	private int hanziBoxLength;
	private int hanziFontSpace = 20;//UnicodeFunc.hanziFontWB + 2;

	private Image rightarrow = null, leftarrow = null;

	private static InputMethodUtf8 instance = null;
	
	private Screen screen = null;

	public InputMethodUtf8() {		
		//#ifdef polish.ScreenWidth:defined
			//#= screenW = ${polish.ScreenWidth};
			//#= screenH = ${polish.ScreenHeight};
		//#else
		//TODO there is other way to set this screen size?
			//#= screenW = 176;
			//#= screenH = 208;
		//#endif

		this.hanziBoxLength = screenW - 5*2;
		num_row = this.hanziBoxLength / hanziFontSpace;
		charPage = 0;
		clearCache();
		readWords();

		//this.tableUtf8LocalCode = this.controller.getUtf8GbTable();
		//this.hanziDotLib = this.controller.getHanziDotLib();
		this.tableUtf8LocalCode = ItemUTF8Lib.getInstance().getTableUtf8LocalCode();
		try{
			rightarrow =  Image.createImage("/rightarrow.png");
			leftarrow =  Image.createImage("/leftarrow.png");
		}catch(Exception e){
			//#debug error
			System.out.println("Error when load arrow image" + e.toString());
		}
	}
	
	public InputMethodUtf8(Screen screen) {	
		//#ifdef polish.ScreenWidth:defined
			//#= screenW = ${polish.ScreenWidth};
			//#= screenH = ${polish.ScreenHeight};
		//#else
		//TODO there is other way to set this screen size?
			//#= screenW = 176;
			//#= screenH = 208;
		//#endif
		this.hanziBoxLength = screenW - 5*2;
		num_row = this.hanziBoxLength / hanziFontSpace;
		charPage = 0;
		clearCache();
		readWords();

		//this.tableUtf8LocalCode = this.controller.getUtf8GbTable();
		//this.hanziDotLib = this.controller.getHanziDotLib();
		this.tableUtf8LocalCode = ItemUTF8Lib.getInstance().getTableUtf8LocalCode();
		try{
			rightarrow =  Image.createImage("/rightarrow.png");
			leftarrow =  Image.createImage("/leftarrow.png");
		}catch(Exception e){
			//#debug error
			System.out.println("Error when load arrow image" + e.toString());
		}
		this.screen = screen;
	}

	public static InputMethodUtf8 getInstance(){
		if (InputMethodUtf8.instance == null)
		{
			InputMethodUtf8.instance = new InputMethodUtf8();
		}
		return InputMethodUtf8.instance;
	}

	public static InputMethodUtf8 getInstance(Screen screen){
		if (InputMethodUtf8.instance == null)
		{
			InputMethodUtf8.instance = new InputMethodUtf8(screen);
		}
		return InputMethodUtf8.instance;
	}
	
	/** 
	 * clear the cache 
	 */
	public void clearCache() {
		currentPos = 0;
		wordsBuffer = new Vector(0, 1);
		for (int i = 0; i < inputKey.length; i++)
			inputKey[i] = -1;
	}

	/**
	 * clear the input content
	 *
	 */
	public void clsContent() {
		content = new String[] { "", "", "" };
		charPos = 0;
		charSelect = 0;
		charPage = 0;
	}

	/**
	 * 
	 *
	 */
	public void doUp() {
		if (select > 0)
			select--;
		currentInputPad = null;
		charSelect = 0;
		charPage = 0;
	}

	/**
	 * 
	 *
	 */
	public void doDown() {
		if (select < keyAvailable.size() - 1)
			select++;
		currentInputPad = null;
		charSelect = 0;
		charPage = 0;
	}

	/**
	 * 
	 *
	 */
	public void doLeft(){
		if (charSelect > 0)
			charSelect--;
		if (charSelect % num_row == num_row - 1)
			charPage--;
	}

	/**
	 * 
	 *
	 */
	public void doRight(){
		if (charSelect < currentInputPad.length - 1)
			charSelect++;
		if (charSelect % num_row == 0 && charSelect != 0)
			charPage++;
	}

	/**
	 * 
	 *
	 */
	public void doFire(){
		//#if tmp.directInput
		if (inputMode == TextField.MODE_PINYIN) {
			if ((inputChar = getChar()) != null) {
				clearCache();
				keyAvailable.removeAllElements();
				currentInputPad = null;
				//this.stateUtfInputNow = false;
			}
		}
		//#endif
	}

	private int PinYinBoxWidth = 0;
	private int PinYinBoxStartX = 5;
	private int PinYinBoxStartY = 60;
	//private int menubarHeight = 65;
	private int fontHeight = 0;
	private int fontBorder = 1;

	/**
	 * get the maximum width of pinyin
	 * @param font
	 * @return
	 */
	private int getPinYinBoxWidth(Font font){
		this.PinYinBoxWidth = font.stringWidth("shuang") + 4;
		return this.PinYinBoxWidth;
	}

	/**
	 * 
	 * @param font
	 * @return
	 */
	private int getFontHeight(Font font){
		this.fontHeight = UnicodeFunc.getUtf8FontHeight(font);
		return  this.fontHeight;
	}

	/**
	 * 
	 * @param starty
	 * @param leftBorder
	 */
	public void setPinYinBoxStartY(int starty, int leftBorder){
		this.PinYinBoxStartY = starty + 22;
		this.PinYinBoxStartX = leftBorder;
	}

	public static int inputBoxBgColor = 0xC8C8F9;
	public static int inputBoxBgColorFocused = 0x1B06CE;
	public static int inputFontColor = 0x000000;
	public static int inputFontColorFocused = 0x003366;
	public static int inputPinYinColorFocused = 0xFFFFFF;
	public static int layoutPinyin = Graphics.TOP | Graphics.LEFT;
	public static int layoutChar = Graphics.TOP | Graphics.HCENTER;

	/**
	 * paint the input method dialog
	 * @param g
	 * @param font
	 */
	public void paintInput(Graphics g, Font font) {
		try {
			//#if tmp.directInput
			if (inputMode != TextField.MODE_PINYIN)
				return;
			//#endif
			if(this.PinYinBoxWidth == 0){
				this.PinYinBoxWidth = getPinYinBoxWidth(font);
			}
			if(fontHeight == 0){
				fontHeight = getFontHeight(font);
			}
			int starty;
			int HanziBoxStartY = PinYinBoxStartY + keyAvailable.size() * 20 + 2;
			//#if tmp.directInput
			if (inputMode == TextField.MODE_PINYIN) {
				//if has the matched PinYin, then draw PinYin box
				if (keyAvailable != null && keyAvailable.size() > 0) {
					//starty = screenH - keyAvailable.size() * 20;
					this.drawPinyinBox(g, PinYinBoxStartX, PinYinBoxStartY, PinYinBoxWidth, keyAvailable.size() * (fontHeight+fontBorder*2), inputBoxBgColor);
					g.setColor(0);
					for (int i = 0; i < keyAvailable.size(); i++) {
						String key = (String) keyAvailable.elementAt(i);

						if (i == select){
							g.setColor(inputBoxBgColorFocused);
							g.fillRect(PinYinBoxStartX + 1, PinYinBoxStartY + i * fontHeight, PinYinBoxWidth-fontBorder*2, fontHeight);
							g.setColor(inputPinYinColorFocused);			
							g.drawString(key, PinYinBoxStartX + fontBorder*4, PinYinBoxStartY + i * fontHeight, layoutPinyin);
						}
						else{
							//g.setColor(inputBoxBgColor);
							//g.fillRect(PinYinBoxStartX, PinYinBoxStartY + i * 18, PinYinBoxWidth-fontBorder*2, 18);
							g.setColor(inputFontColor); 						
							g.drawString(key, PinYinBoxStartX + fontBorder*4, PinYinBoxStartY + i * fontHeight, layoutPinyin);
						}

						if (i == select) {
							//TODO improve the search?
							/* find the right pinyin */
							for (int m = 0; m < wordKeys.size(); m++) {
								/* find the right words group */
								if (key.equals(wordKeys.elementAt(m).toString())) {
									currentInputPad = (String[]) hashtablePinY.get(key);
									break;
								}
							}
						}
					}
				}

				if (currentInputPad != null) {
					starty = HanziBoxStartY; //screenH - menubarHeight;
					int charPageCurrent = currentInputPad.length / this.num_row;

					drawTextBox(g, 0xefefef, 0x121212, inputBoxBgColor, 5, starty, this.hanziBoxLength, this.fontHeight+this.fontBorder*2+2, fontBorder, 0);
					//System.out.println("charPageCurrent and charPage :" + charPageCurrent+"  "+charPage);
					//in the left, draw right arrow
					if(charPageCurrent >0 && charPage==0){
						g.drawImage(rightarrow, this.hanziBoxLength-2, starty + 2, layoutChar);
					}
					//in the middle, draw left and right arrow
					else if(charPageCurrent >0 && charPage>0 && charPage<charPageCurrent){
						g.drawImage(leftarrow, 8, starty + 2, layoutChar);
						g.drawImage(rightarrow, this.hanziBoxLength-2, starty + 2, layoutChar);
					}
					//in the right, draw left arrow
					else if(charPageCurrent >0 && charPage==charPageCurrent){
						g.drawImage(leftarrow, 8, starty + 2, layoutChar);
					}
					//dont draw any arrow
					else if(charPageCurrent == 0){

					}

					for (int i = this.charPage * this.num_row; i < (this.charPage + 1) * this.num_row; i++) {
						//System.out.println(i + "." + charPage + "." + num_row+ "." + charSelect);
						if (i > currentInputPad.length - 1){		
							return;
						}
						if(charSelect > currentInputPad.length - 1){
							charSelect = currentInputPad.length - 1;
						}
						if (i == charSelect) {
							g.setColor(inputBoxBgColorFocused);
							g.drawRect(10 + (i - charPage * num_row) * hanziFontSpace, starty /*+ 1*/, UnicodeFunc.hanziFontWB, this.fontHeight+this.fontBorder*2);							
							drawStringSingleChinese(g, currentInputPad[i], 18 + (i - charPage * num_row) * hanziFontSpace, starty + 2, inputFontColorFocused, inputBoxBgColor/*inputBoxBgColorFocused*/, layoutChar);
						}else{
							drawStringSingleChinese(g, currentInputPad[i], 18 + (i - charPage * num_row) * hanziFontSpace, starty + 2, inputFontColor, inputBoxBgColor, layoutChar);
						}	
					}
				}
			}
			//#endif
			//draw the input state info, useless now
			//this.drawStringShadow(g, inputFontColorFocused, 0, 0, inputModeTxt[inputMode],screenW, 15, 50);
		} catch (Exception e) {
			//#debug error
			System.out.println("Error in paintInput" + e.toString());
		}
	}

	private Image hanziImage;

	/**
	 * draw one single chinese with position and text/bg color
	 * if OS has font already, draw the native string
	 * @param g
	 * @param singleChinese
	 * @param x
	 * @param y
	 * @param textColor
	 * @param bgColor
	 * @param anchor
	 */
	private void drawStringSingleChinese(Graphics g, String singleChinese, int x, int y, int textColor, int bgColor, int anchor){
		int offset = 0;
		byte[] hanzi = new byte[2];
		byte[] gbcode = null;
		try{
			//#if {cfg.osHasFontAlready} != true
			if(this.tableUtf8LocalCode.containsKey(singleChinese)){
				gbcode = (byte[])this.tableUtf8LocalCode.get(singleChinese);
			}else{
				//can not find this hanzi,then use empty square to take place of it
				hanzi[0] = (byte) (0xa1);
				hanzi[1] = (byte) (0xf5);
			}
			//if the string is hanzi
			if(gbcode != null){
				//hanzi = UnicodeFunc.Gb2312CodeToByteArray(gbcode);
				hanzi = gbcode;
				//System.out.println("this is hanzi: "+hanzi[0] +" " +hanzi[1]);
			}else{
				//can not find this hanzi,then use empty square to take place of it
				hanzi[0] = (byte) (0xa1);
				hanzi[1] = (byte) (0xf5);
			}		
			while (/*y < 300 &&*/ offset < hanzi.length) {
				int b = hanzi[offset] & 0xff;
				if (b > 0x7f) {
					this.hanziImage = UnicodeFunc.createSingleChineseWord12(hanzi, offset, textColor, bgColor);
					g.drawImage(this.hanziImage, x, y, anchor);
					this.hanziImage = null;
					offset += 2;
				} else { 
					//english letter,but maybe error
					//#debug error
					System.out.println("english letter? check it!");
					offset++;
				}
			}
			offset = 0;
			//#else
			g.setColor(textColor);
			g.drawString(singleChinese, x, y, anchor);
			//#endif

		}catch(Exception e){
			//#debug error
			System.out.println("Error in drawStringChinese" + e.toString());
		}
	}

	public void drawStringShadow(Graphics g, int test1, int test2, int test3, String inputModeTxt, int width, int x, int y) {
		g.drawString(inputModeTxt, x, y, Graphics.BASELINE|Graphics.HCENTER);

	}

	/**
	 * draw the pinyin box
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public void drawPinyinBox(Graphics g, int x, int y, int width,
			int height, int color) {
		g.setColor(color);
		g.fillRect(x, y, width, height);
	}

	/**
	 * draw the char collection box
	 * @param g
	 * @param borderupcolor
	 * @param borderdowncolor
	 * @param bgcolor
	 * @param stx
	 * @param sty
	 * @param width
	 * @param height
	 * @param borderW
	 * @param type
	 */
	public void drawTextBox(Graphics g, int borderupcolor, int borderdowncolor, int bgcolor,
			int stx, int sty, int width, int height, int borderW, int type) {
		if (type == 0) {
			g.setColor(borderupcolor);
			g.fillRect(stx - borderW, sty - borderW, width + 2 * borderW, height + 2 * borderW);
			g.setColor(borderdowncolor);
			g.fillRect(stx, sty, width + borderW, height + borderW);
			g.setColor(bgcolor);
			g.fillRect(stx, sty, width, height);
		} else {
			g.setColor(borderdowncolor);
			g.fillRect(stx - borderW, sty - borderW, width + 2 * borderW, height + 2 * borderW);
			g.setColor(borderupcolor);
			g.fillRect(stx, sty, width + borderW, height + borderW);
			g.setColor(bgcolor);
			g.fillRect(stx, sty, width, height);
		}
	}

	/**
	 * handle all pressed key
	 * @param keyCode
	 * @param inputMode
	 * @return
	 */
	public boolean keyPressed(int keyCode, int inputMode) {
		// #debug debug
		//System.out.println("keyPressed");
		this.inputMode = inputMode;
		try{
			//time2 = System.currentTimeMillis();
			
			//#if tmp.directInput
				//#if tmp.supportsAsciiKeyMap
					if (keyCode >= 32
							&& (keyCode < Canvas.KEY_NUM0 || keyCode > Canvas.KEY_NUM9)
							&& (keyCode != Canvas.KEY_POUND && keyCode != Canvas.KEY_STAR)
							&& (keyCode <= 126) // only allow ascii characters for the initial input...
							//&& ( !getScreen().isSoftKey(keyCode, gameAction) )
							&& inputMode == TextField.MODE_PINYIN
					){
						if (checkWord(keyCode)) {
							//if click >10, will have overflow bug, reset it
							/*if(currentPos+1 == inputKey.length-1){
								currentPos = 0;
							}*/
							inputKey[currentPos++] = keyCode - 32;
							currentInputPad = null;
						}
						select = 0;
						//if is key for char, set state to true, prepare the further input
						this.stateUtfInputNow = true;
						return true;
					}
				//#else
				//0 keycode is 48, 2 keycode is 50, 9 keycode is 57
				if (keyCode >= Canvas.KEY_NUM0 && keyCode <= Canvas.KEY_NUM9) {
					if (inputMode == TextField.MODE_PINYIN && keyCode >= Canvas.KEY_NUM2) {
						// #debug debug
						//System.out.println("key 2-8");
						if (checkWord(keyCode - Canvas.KEY_NUM2)) {
							//if click >10, will have overflow bug, reset it
							/*if(currentPos+1 == inputKey.length-1){
								currentPos = 0;
							}*/
							inputKey[currentPos++] = keyCode - Canvas.KEY_NUM2;
							currentInputPad = null;
						}
						select = 0;
						//if is key for char, set state to true, prepare the further input
						this.stateUtfInputNow = true;
						return true;
					}
					/*else if (inputMode == TextField.MODE_NUMBERS){
						inputChar = "" + (keyCode - Canvas.KEY_NUM0);
						this.stateUtfInputNow = false;
					}else if (inputMode == TextField.MODE_LOWERCASE) {
						if (keyCode >= 50) {
							System.out.println("Push button time: " + time2 + "." + time1);
							if ((time2 - time1) < 500) {
								System.out.println("Continue to push button");
								charKeyPos++;
								if (charKeyPos == charKey[keyCode - 50].length)
									charKeyPos = 0;
								System.out.println("Content length: "
										+ content[0].length() + " Cursor position: "
										+ charPos);
								if (charPos == content[0].length()) {
									System.out.println("test1");
									content[0] = content[0].substring(0, charPos - 1);
									charPos--;
									System.out.println("test2");
								} else
									content[0] = content[0].substring(0, charPos - 1)
											+ content[0].substring(charPos, content[0]
													.length());
								refreshContent();
							}
							inputChar = charKey[keyCode - 50][charKeyPos];
						}
						this.stateUtfInputNow = false;
					}*/
				}

/*				else if (keyCode == Canvas.UP){
					doUp();
					return true;
				}
				else if (keyCode == Canvas.DOWN){
					doDown();
					return true;
				}
				else if (keyCode == Canvas.LEFT) {
					doLeft();
					return true;
				}
				else if (keyCode == Canvas.RIGHT) {
					doRight();
					return true;
				}
				else if (keyCode == Canvas.FIRE) {
					doFire();
					return true;
				}
				*/
				//change the input method 
				/*else if (keyCode == Canvas.KEY_POUND) {
					this.inputMode++;
					if (this.inputMode == inputModeTxt.length){
						this.inputMode = 0;
					}
				}*/
				//delete the input pinyin, not the chinese
				//#if polish.key.RightSoftKey:defined
				//#= else if (keyCode == ${polish.key.RightSoftKey} )
				{
					// #debug debug
					//System.out.println("RightSoftKey");
					backWord();

					//#= return true;
				}
				//#endif
				
				//#ifdef polish.key.ClearKey:defined
					//#= else if (keyCode == ${polish.key.ClearKey}
				//#else
					//#= else if ( keyCode == KEY_BACKSPACE 
				//#endif
					//#if polish.key.backspace:defined
					//#= || keyCode == ${polish.key.backspace}
					//#endif
				//#= ) 
				{
					// #debug debug
					//System.out.println("ClearKey or backspace");
					/*if (charPos >= 2) {
						content[0] = content[0].substring(0, charPos - 2)
						+ content[0].substring(charPos, content[0].length());
						charPos -= 2;
					} 
					else if (charPos > 0) {
						if (charPos == content[0].length())
							content[0] = content[0].substring(0, charPos - 1);
						else
							content[0] = content[0].substring(0, charPos - 1) + content[0].substring(charPos, content[0].length());
						charPos--;
					}
					refreshContent();
					*/
					backWord();
					return true;
				}
				//#endif
			//#endif
			//time1 = time2;
			//#= return true;
		}catch(Exception e){
			//#debug error
			System.out.println("Error in keyPressed in InputMethodUtf8: " + e.toString());
			return false;
		}
	}

	/**
	 * delete the last input pinyin char, not the unicode char
	 *
	 */
	public void backWord() {
		try{
			// #debug debug
			//System.out.println("wordsBuffer.size(): "+ wordsBuffer.size());
			if (wordsBuffer.size() > 0) {
				keyAvailable = (Vector) wordsBuffer.elementAt(wordsBuffer.size() - 1);
				wordsBuffer.removeElementAt(wordsBuffer.size() - 1);
				currentInputPad = null;
			}else{
				this.stateUtfInputNow = false;
			}
		}catch(Exception e){
			//#debug error
			System.out.println("Error in backWord: "+e.toString());
		}
	}

	/**
	 * 
	 * @param temp
	 * @param key
	 * @param ik
	 */
	public void checkWords(Vector temp, String key, int ik) {
		int p;
		try{
			//#if tmp.supportsAsciiKeyMap
				//#debug debug
				System.out.println("ik in checkWords" + ik);
				char current = (char) (' ' + (ik - 32));
				try {
					String name  = this.screen.getKeyName( ik );
					if (name != null && name.length() == 1) {
						current = name.charAt(0);
					}
				} catch (IllegalArgumentException e) {
					// ignore
				} catch (Exception e) {
					// ignore
				}
				//#debug debug
				System.out.println("current in checkWords" + current);
				int m;
				for (m = 0; m < wordKeys.size(); m++) {
					if (wordKeys.elementAt(m).toString().indexOf(key + current) == 0) {
						temp.addElement(new String(key + current));
						break;
					}
				}
			//#else
				for (p = 0; p < charKey[ik].length; p++) {
					//#= int m;
					for (m = 0; m < wordKeys.size(); m++) {
						if (wordKeys.elementAt(m).toString().indexOf(key + charKey[ik][p]) == 0) {
							temp.addElement(new String(key + charKey[ik][p]));
							break;
						}
					}
				}
			//#endif
		}catch(Exception e){
			//#debug error
			System.out.println("Error in checkWords: " + e.toString());
		}
	}

	/**
	 * check it is the right pinyin
	 * 
	 * @param ik
	 * @return
	 */
	public boolean checkWord(int ik) {
		Vector temp = new Vector(0, 1);
		String key = "";
		try{
			if (keyAvailable.size() > 0) {
				for (int i = 0; i < keyAvailable.size(); i++) {
					key = (String) keyAvailable.elementAt(i);
					checkWords(temp, key, ik);
				}
			} else
				checkWords(temp, key, ik);
			if (temp.size() > 0) {
				wordsBuffer.addElement(keyAvailable);
				keyAvailable = temp;
				return true;
			}
		}catch(Exception e){
			//#debug error
			System.out.println("Error in checkWord" + e.toString());
		}
		return false;
	}

	static final char SEPARATOR = '|';
	byte[]   buf = null;
	char NEWLINE = (char)(0x0D);

	public void readWords() {
		int size = 0;
		int num = 0;
		int length = 0;
		String key;
		String value;
		String[] words;
		try 
		{
			ItemUTF8Lib.getInstance().inputstream = null;
			ItemUTF8Lib.getInstance().inputstream = getClass().getResourceAsStream(Setting.FileNameInputTable);   
			//#= ItemUTF8Lib.getInstance().inputstream.skip(Setting.keyLength);
			buf = CommonFunc.toByteArray(ItemUTF8Lib.getInstance().inputstream);
			buf = GZIP.inflate(buf);
			size = buf.length;
			boolean firstline = true;

			for (int i = 0; i < size; i++) { 
				int j; 
				key = null;
				value = null;
				words = null;
				for (j = i; buf[j] != SEPARATOR; j++);
				key = UnicodeFunc.getStringUtf8(buf, i, j-i);
				if(firstline){
					//the first line is a, but it read ?a, utf file has 2 byte header 
					key = key.substring(key.length()>1?1:0,key.length());
					firstline = false;
				}
				// #debug debug
				//System.out.println("key: "+key);
				for (i = ++j; buf[i] != NEWLINE; i++);
				value = UnicodeFunc.getStringUtf8(buf, j, i-j);
				//3 byte one chinese word
				length = (i-j)/3;
				// #debug debug
				//System.out.println("This pinyin has words: "+ length +"  "+ value);
				words = new String[length];
				for (int k = 0; k < length; k++) {
					words[k] = value.substring(k, 1 + k);
					// #debug debug
					//System.out.println("words[]: "+words[k]);
				}
				this.hashtablePinY.put(key, words);
				this.wordKeys.insertElementAt(key, num);

				num++;
			}
		} catch (Exception e) {
			//#debug error
			System.out.println("Error in readWords: " + e.toString());
		}finally{
			try{
				if(ItemUTF8Lib.getInstance().inputstream != null){
					ItemUTF8Lib.getInstance().inputstream.close();
				}
			}catch(Exception e){
				//ignore
			}
		}

	}

	/* read pinyin table */
	public void readWords1() {
		try {
			InputStream is;
			String[] words;
			int fontNum = 0;
			is = getClass().getResourceAsStream(Setting.FileNameInputTable);
			StringBuffer sb = new StringBuffer();
			int c;
			int pos = 0;
			while ((c = is.read()) != -1) {
				if (c == NEWLINE) {
					if (pos == 0 && fontNum == 0) {
						// fontNum=Integer.parseInt(sb.toString());
						fontNum = 409;
						wordKey = new String[fontNum];
						System.out.println("Pinyin table length: "+ wordKey.length);
					} else {
						String key;
						byte[] data1 = new byte[sb.toString().length()];
						for (int i = 0; i < data1.length; i++)
							data1[i] = (byte) sb.charAt(i);
						String s = new String(data1, "UTF-8");
						key = s.substring(0, s.indexOf(SEPARATOR))
						.toLowerCase();
						// System.out.println(key);
						words = new String[s.length() - s.indexOf(SEPARATOR) - 1];
						for (int j = 0; j < words.length; j++) {
							words[j] = s.substring(
									s.indexOf(SEPARATOR) + 1 + j, s
									.indexOf(SEPARATOR)
									+ 2 + j);
						}
						hashtablePinY.put(key, words);
						wordKey[pos++] = key;
					}
					sb = new StringBuffer();
				} else if (c != 0x0d) {
					sb.append((char) c);
				}
			}
			String key;
			byte[] data1 = new byte[sb.toString().length()];
			for (int i = 0; i < data1.length; i++)
				data1[i] = (byte) sb.charAt(i);
			String s = new String(data1, "UTF-8");
			key = s.substring(0, s.indexOf(SEPARATOR));
			words = new String[s.length() - s.indexOf(SEPARATOR) - 1];
			for (int j = 0; j < words.length; j++) {
				words[j] = s.substring(s.indexOf(SEPARATOR) + 1 + j, s
						.indexOf(SEPARATOR)
						+ 2 + j);
			}
			hashtablePinY.put(key, words);
			wordKey[pos++] = key;
		} catch (Exception e) {
			//#debug error
			System.out.println("Error in readWords: " + e.toString());
		}
	}

	public String getChar() {
		if (currentInputPad != null)
			return currentInputPad[charSelect];
		else
			return null;
	}

	public void setInputMode(int type) {
		inputMode = type;
	}

	public int getInputMode() {
		return inputMode;
	}

	public void refreshContent() {
		try{
			if (content[0].length() > 0) {
				if (charPos == 0) {
					content[1] = " " + content[0];
					content[2] = "|" + content[0];
				} else if (charPos == content[0].length()) {
					content[1] = content[0] + " ";
					content[2] = content[0] + "|";
				} else {
					content[1] = content[0].substring(0, charPos) + " "
					+ content[0].substring(charPos, content[0].length());
					content[2] = content[0].substring(0, charPos) + "|"
					+ content[0].substring(charPos, content[0].length());
				}
			}
			// #debug debug
			//System.out.println(content[0]);
			// #debug debug
			//System.out.println(content[1]);
			// #debug debug
			//System.out.println(content[2]);
		}catch(Exception e){
			//#debug error
			System.out.println("Error in refreshContent: " + e.toString());
		}
	}

	/**
	 * Move the cursor
	 * 
	 * @param direct
	 */
	public void moveCharPos(int direct) {
		if (direct == 0) {
			if (charPos > 2) {
				//#debug debug
				System.out.println(content[0].substring(charPos - 2, charPos));
				/*
				 * if(PaintCanvas.checkFace(content[0].substring(charPos-2,charPos))>-1)
				 * charPos-=2; else charPos--;
				 */
			} else if (charPos > 0)
				charPos--;
		} else {
			if (charPos < content[0].length() - 2) {
				/*
				 * if(PaintCanvas.checkFace(content[0].substring(charPos+1,charPos+3))>-1)
				 * charPos+=2; else charPos++;
				 */
			} else if (charPos < content[0].length())
				charPos++;
		}
		refreshContent();
	}
//#endif	
}

/*
public void keyPressedInput(int i)
 {
  if(!chooseFace&&down&&imt.keyAvailable.size()==0)
  {
   chooseFace=true;
   faceNo=7;
  }
  else if(!chooseFace&&left&&imt.keyAvailable.size()==0)
  {
   imt.moveCharPos(0);
  }
  else if(!chooseFace&&right&&imt.keyAvailable.size()==0)
  {
   imt.moveCharPos(1);
  }
  else if(chooseFace)
  {
   if(left)
   {
    if(faceNo==0)
     faceNo=face.rows*face.columns-1;
    else
     faceNo--;
   }
   else if(right)
   {
    if(faceNo==face.rows*face.columns-1)
     faceNo=0;
    else
     faceNo++;
   }
   else if(up)
    chooseFace=false;
   else if(fire||softLeft)
   {
    imt.content[0]+=faceSign[faceNo];
    imt.charPos+=2;
    imt.refreshContent();
   }
  }
  else if(softRight)
  {
   if(imt.keyAvailable==null||imt.keyAvailable.size()==0)
   {
    drawMessage=-1;
    imt.clsContent();
   }
   else
    imt.keyPressed(-7);
  }
  else if(softLeft)
  {
   if(imt.keyAvailable.size()==0)
   {

    if(imt.content[0]!=null&&imt.content[0].length()>0)
    {
     sendMessage(drawMessage);
    }
    else
     setNotice("Sorry, you have input empty!");
    drawMessage=-1;
   }
  }
  else
  {
   imt.keyPressed(i);
   if(imt.inputChar!=null)
   {
    if(imt.content[0]==null||imt.content[0].length()==0)
    imt.content[0]=imt.inputChar;
    else
     imt.content[0]=imt.content[0].substring(0,imt.charPos)+imt.inputChar+imt.content[0].substring(imt.charPos, imt.content[0].length());
    imt.inputChar=null;
    imt.charPos++;
   }
  }
  imt.refreshContent();
 }
 */
