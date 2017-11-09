package net.mobabel.view;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;

import net.mobabel.model.RequestObserver;
import net.mobabel.util.UIController;

import de.enough.polish.ui.IconItem;
import de.enough.polish.util.Locale;

public class ScreenLoading extends Form implements CommandListener{

	private static UIController controller = null;

	private Displayable parentScreen = null;

	public final IconItem iconitem;

	Image wait[] = null;

	private boolean cancelThis = false;
	
	RequestObserver observer = null;
	
	static TimerTask task = null;
	
	static Timer timer = null;
	
	public ScreenLoading(UIController controller, Displayable parentScreen, String message, RequestObserver observer) {
		//#style processbarInfo
		super(message);
		this.controller = controller;
		this.parentScreen = parentScreen;
		this.observer = observer;

		wait = null;
		wait = new Image[6];

		int mInterval = 250;
		final int mMaximum = 6;

		//#style alertMessage
		this.iconitem = new IconItem(Locale.get("messages.loading"),null);
		this.append(iconitem);

		try {
			wait[0] = Image.createImage("/loading0.png");
			wait[1] = Image.createImage("/loading1.png");
			wait[2] = Image.createImage("/loading2.png");
			wait[3] = Image.createImage("/loading3.png");
			wait[4] = Image.createImage("/loading4.png");
			wait[5] = Image.createImage("/loading5.png");
		} catch (Exception e) {
			//#debug error
			System.out.println("waitinfo load images: " + e.toString());
		}
		try{
/*			if(task!=null){
				task.cancel();
				task = null;
			}*/
			if(task != null)
				task.cancel();
			task = null;
			task = new TimerTask() {
				int mCount;
				public void run() {
					mCount = (mCount + 1) % mMaximum;
					iconitem.setImage(wait[mCount]);
					// #debug error
					//System.out.println("haha: " + mCount);
				}
			};
			if(timer != null)
				timer.cancel();
			timer = new Timer();
			while(!cancelThis){
				timer.schedule(task, 0, mInterval);
			}
		}catch(Exception e) {
			//#debug error
			System.out.println("startTimer waitinfo: " + e.toString());
		}
		this.addCommand(UIController.cancelCommand);
		this.setCommandListener(this);
	}

	public void commandAction(Command c, Displayable dis) {
		if(c == UIController.cancelCommand){
			wait = null;
			cancelThis = true;
			this.observer.stop();
			this.controller.showCurrent(parentScreen);
		}
	}

	public void setLoadingText(String text){
		iconitem.setText(text);
	}

}
