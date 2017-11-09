/*
 * Created on 2006-9-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.mobabel.midlet;

import java.io.IOException;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringItem;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

import de.enough.polish.util.Locale;

import net.mobabel.item.ItemGlobal;
import net.mobabel.model.CommonFunc;
import net.mobabel.model.Setting;
import net.mobabel.util.UIController;

/**
 * @author leelight
 *
 */
public class Momego extends MIDlet implements Runnable
{
	public final Display display;

	private static UIController controller = null;

	private Image iconSplash = null;

	private final Momego midlet = this;
	
    private boolean isInitialized;
    private boolean splashIsShown;
	
    public Gauge gauge = null;
    
    public int state = 0;
    
    public static int GAUGE_MAX = 50;
    
	public Momego() {
		super();		
		display = Display.getDisplay(this);	
	}

	protected void startApp() throws MIDletStateChangeException {
		String smsPort = getAppProperty("SMS-Port");
		if(smsPort == null)
			smsPort = Setting.SMS_PORT;
		ItemGlobal.getInstance().setSMS_Port(smsPort);
		
		Thread splashThread = new Thread(new Momego.SplashScreen());
		splashThread.start();

		Thread thisThread = new Thread(this);
		thisThread.start(); 
	}
	
    public void run(){
        while(!splashIsShown){
            Thread.yield();
        }       
        try {
			doTimeConsumingInit();
		} catch (IOException e) {
        	//#debug error
    	    System.out.println(e.toString());
		}       
        while(true){
            // soft loop           
            Thread.yield();
        }
    }
    
    private void doTimeConsumingInit() throws IOException{ 
        // Just mimic some lengthy initialization for 3 secs
        //long endTime= System.currentTimeMillis()+ Setting.StartWait_Time;
        //while(System.currentTimeMillis()<endTime ){}

		controller=new UIController(this);
		controller.init();
        isInitialized=true;		
    }

	protected void pauseApp() {
		this.notifyPaused();
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		controller = null;
	}

	public void showCurrent(Displayable disp) {
		display.setCurrent(disp);
	}

	public void showCurrent(Alert alert, Displayable disp) {
		display.setCurrent(alert, disp);
	}

	public Displayable getCurrent() {
		return display.getCurrent();
	}

	public void showScreen(Displayable d) {
		display.setCurrent(d);
	}

	public void showScreen(Alert a, Displayable d) {
		if (a == null) {
			showScreen(d);
		} else {
			//System.out.println("showing Alert: " + a.getClass().getName()+ "; next screen: " + d == null ? "!!! error: null" : d.getClass().getName());
			display.setCurrent(a, d);
		}
	}

	public void exit(boolean arg0) {
		try {
			destroyApp(arg0);
			notifyDestroyed();
		} catch (MIDletStateChangeException e) {
        	//#debug error
    	    System.out.println("Error in exit " +e.toString());
		}
	}

	public Displayable initApp() {
		return null;
	}
	
	/*
	 * used by contreiller
	 */
	public void update(){	
		if(state < GAUGE_MAX){
			gauge.setValue(state);			
		}
		state++;
	}
	
    public class SplashScreen implements Runnable{
        public SplashCanvas splashCanvas;
        
        public void run(){
            splashCanvas = new SplashCanvas(null);      
            display.setCurrent(splashCanvas);
            while(!isInitialized){
                try{
                    Thread.yield();
                }catch(Exception e){}
            }
        }   
        
    }   
    
    public class SplashCanvas extends Form {

    	//public Gauge gauge = null;
    	//public int GAUGE_MAX = 20;
    	//private int state = 0;
    	
		public SplashCanvas(String title) {
	    	//#style splashForm
			super("");
			try {
				iconSplash=Image.createImage("/splash.png");
			    //#style spalshImage
			    this.append(iconSplash);
		    } catch (java.io.IOException e) {
		    	iconSplash=null;
		    	//#debug error
				System.out.println("Load image error when initializing Exception happens:" + e.getMessage());
		    }		    
		    //#style splashText
		    //#= StringItem version = new StringItem("",Locale.get("item.verison")+CommonFunc.getVersion(Setting.Version), Item.PLAIN);
		    //#= this.append(version);
		    
			//#style loadSplashGaugeItem
		    gauge = new Gauge("", false, GAUGE_MAX, 0);
		    gauge.setValue(0);
		    this.append(gauge);	
		    
		    splashIsShown=true;
		}
		
    }

}
