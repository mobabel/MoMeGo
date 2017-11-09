package net.mobabel.view;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;

import net.mobabel.model.RequestObserver;
import net.mobabel.util.UIController;

import de.enough.polish.util.Locale;


/**
 * Factory for creating Alerts of varoius types
 * @author martin
 * @author LiHui
 */
public class AlertFactory {

	public static String getMessage(Exception e, String addon) {
		String message;

		if (e.getMessage().equals("")) {
			message = e.getClass().getName();
			message = addon + "\n" + "error.exception" + message;
		} /*else if (e instanceof InvalidCipherTextException) {
      message = e.getMessage();
      message = addon + "\n" + "error.cypherException"+ message;
    } else if (e instanceof DataLengthException) {
      message = e.getMessage();
      message = addon + "\n" + "error.cypherException" + message;
    } else if (e instanceof IOException) {
      message = e.getMessage();
      message = addon + "\n" + "error.ioException"+ message;
    }*/ else {
    	message = addon+ "\n" + e.getMessage() + "\n(" + e.getClass().getName() + ")";
    }

		return message;
	}


	public static Alert createErrorAlert(Exception e, String addon) {
		return createErrorAlert(getMessage(e, addon));
	}


	public static Alert createErrorAlert(String alertMsg) {
		//#style alertError  
		Alert alert = new Alert(Locale.get("title.alerterror"), null, null, null);
		alert.setTimeout(Alert.FOREVER);
		//#style alertMessage
		alert.setString(alertMsg);
		alert.setType(AlertType.ERROR);
		return alert;
	}


	public static Alert createInfoAlert(String msg) {
		return createInfoAlert(msg, -1);
	}


	public static Alert createInfoAlert(String msg, int timeout) {
		//#style alertInfo
		Alert alert = new Alert(Locale.get("title.alertinfo"), null, null, null);
		alert.setType(AlertType.INFO);
		//#style alertMessage
		alert.setString(msg);
		alert.setTimeout(timeout > 0 ? timeout : Alert.FOREVER);
		return alert;
	}


	public static Alert createWarningAlert(String msg) {
		return createWarningAlert( msg, -1);
	}

	public static Alert createWarningAlert(String msg, int timeout) {
		//#style alertWarning 
		Alert alert = new Alert(Locale.get("title.alertwarning"), null, null, null);
		alert.setType(AlertType.WARNING);
		//#style alertMessage
		alert.setString(msg);
		alert.setTimeout(timeout > 0 ? timeout : Alert.FOREVER);
		return alert;
	}

	public static Command yesCommand = new Command(Locale.get("cmd.yes"),Command.OK, 1);
	
	public static Command noCommand = new Command(Locale.get("cmd.no"),Command.CANCEL,1);
	
	public static Command backCommand = new Command(Locale.get("cmd.back"),Command.SCREEN, 1);

	public static Alert createYesOrNoAlert(String msg) {
		//#style alertInfo
		Alert alert = new Alert("", msg, null, null);
		alert.setType(AlertType.INFO);
	    //#style alertMessage
		alert.setString(msg);
		alert.addCommand(yesCommand);
		alert.addCommand(noCommand);
		return alert;
	}
	
	public static Alert createQuestionAlert(String msg) {
		//#style alertInfo
		Alert alert = new Alert("", msg, null, null);
		alert.setType(AlertType.INFO);
	    //#style alertMessage
		alert.setString(msg);
		alert.addCommand(yesCommand);
		alert.addCommand(noCommand);
		alert.addCommand(backCommand);
		return alert;
	}

	public static Alert createMessageAlert(String title, String msg) {
		return createMessageAlert(title, msg, -1);
	}


	public static Alert createMessageAlert(String title, String msg, int timeout) {
		//#style alertInfo4Message
		Alert alert = new Alert(title, null, null, null);
		alert.setType(AlertType.INFO);
		//#style smsMessage
		alert.setString(msg);
		alert.setTimeout(timeout > 0 ? timeout : Alert.FOREVER);
		return alert;
	}
	
	  public static Form createLoadingForm(UIController controller, Displayable parentScreen, String message, RequestObserver observer) {
		  return new ScreenLoading(controller, parentScreen, message, observer);
	  }

}
