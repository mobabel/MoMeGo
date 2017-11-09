package net.mobabel.model;

import javax.microedition.lcdui.Display;
import javax.microedition.media.Manager;
//#if polish.midp2
import javax.microedition.media.Player;
//#elif polish.api.nokia-ui
//#= import com.nokia.mid.ui.DeviceControl;
//#endif

public class AlertNotifier implements Runnable{
	
	private static AlertNotifier instance = null;
	
	private static final int[] notes = { 69, 69, 75, 71} /*{ 69, 70, 71, 72, 73, 74, 75, 76 }*/;
    static Player wavPlayer = null;
    static Player tonePlayer = null;
    private static int ip = 0;
    private static boolean stopSound = false;
    private static Display display;
	
	public static AlertNotifier getInstance(Display display){
		if(instance == null){
			instance = new AlertNotifier(display);
		}
		return instance;
		
	}
	
	public AlertNotifier(Display display){
		this.display = display;
		new Thread(this).start();
	}
	
	
	public static void playTone(){
		try {
			for(int i=0;i<notes.length;i++){
	            Manager.playTone(notes[i], 1000, 100);
			}
        } catch (Exception ex) {
        	//#debug error
            System.out.println("Err playTone: "+ ex.toString());
        }
	}
	
	public static void stopTone(){
        stopSound = true;

        if (tonePlayer != null) {
            tonePlayer.close();
            tonePlayer = null;
        }
	}
	
	public static void playVibration(){
		try {
			//#if polish.midp2
				display.vibrate(500);
			//#elif polish.api.nokia-ui
				//#= DeviceControl.startVibra(100, 500);
			//#endif
        } catch (Exception ex) {
        	//#debug error
            System.out.println("Err PlayVibration: "+ ex.toString());
        }
	}
	
	public static void play(){
		//boolean bell = ConfigurationManager.getInstance().getConfigurationValueBoolean("alertformbell");
		//boolean vibration = ConfigurationManager.getInstance().getConfigurationValueBoolean("alertformvibration");
		boolean bell = true;
		boolean vibration = true;
		if(bell){
			playTone();
		}
		if(vibration){
			playVibration();
		}
		
	}

	public void run() {
		
	}
	
	
	
}
