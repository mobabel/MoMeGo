package net.mobabel.model;

import javax.microedition.lcdui.Image;
import java.util.TimerTask;
import java.util.Timer;

import de.enough.polish.ui.ScreenInfo;

public class WaitInfo {
	private TimerTask task = null;
	private Timer timer = null;
	private Image wait[] = new Image[4];
	private int mInterval = 300;//250
	private int mCount;
	private int mMaximum = 4;
	public boolean stillInGauge = true;
	

	public WaitInfo ()
	{
		mCount = 0;
		
		try {
		   wait[0] = Image.createImage("/loading_1.png");
		   wait[1] = Image.createImage("/loading_2.png");
		   wait[2] = Image.createImage("/loading_3.png");
		   wait[3] = Image.createImage("/loading_4.png");
		   
		} catch (Exception e) {
			System.out.println("waitinfo load images: " + e.toString());
		}
	}
	

	public void startTimer() {
		ScreenInfo.setImage(null);
		
		// This a aspect of a concrete device can be weaved in by J2ME-Polish
		int xMsgWidth = 70;
		int yMsgHeight = 4;
		
		//#if (polish.ScreenWidth >= 128) && (polish.ScreenWidth < 176)
		//#=  xMsgWidth = ${polish.ScreenWidth} - 58;
		//#=  yMsgHeight = 4;
		//#elif (polish.ScreenWidth >= 176) && (polish.ScreenWidth < 240)
		//#=  xMsgWidth = ${polish.ScreenWidth} - 58;
		//#=  yMsgHeight = 8;
		//#elif (polish.ScreenWidth >= 240) && (polish.ScreenWidth < 320)
		//#=  xMsgWidth = ${polish.ScreenWidth} - 58;
		//#=  yMsgHeight = 12;
		//#elif (polish.ScreenWidth >= 320)
		//#=  xMsgWidth = ${polish.ScreenWidth} - 58;
		//#=  yMsgHeight = 12;
		//#endif
		ScreenInfo.setPosition(xMsgWidth, yMsgHeight);

		try{
		//Create a Timer to update the display.
	    task = new TimerTask() {
	    	
		    public void run() {
			    	mCount = (mCount + 1) % mMaximum;
			    	//System.out.println("mCount " +mCount);
			    	ScreenInfo.setImage(wait[mCount]);
			    	//ScreenInfo.setText("loading");
		    }
	    };
	    
		timer = new Timer();
		timer.schedule(task, 0, mInterval);
		System.out.println("Start schedule (" + xMsgWidth + "," + yMsgHeight + ")" );
		}catch(Exception e) {
			System.out.println("Error in start schedule: " + e.toString());
		}
		stillInGauge = true;
	}

	public void stopTimer() {
        ScreenInfo.setImage(null);
        //ScreenInfo.setText("");
		if( timer != null ) timer.cancel();
		System.out.println("Stop schedule");
		stillInGauge = false;
	}
}