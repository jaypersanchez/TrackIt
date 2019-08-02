package main;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

public class PasswordMenu implements CommandListener {
	
	private IdenMIDlet parent;
	private Display display;
    private Form f;
    private TextField tfPassword;
    
    private Command enterCommand = new Command("Enter", Command.ITEM, 1);
    
    public PasswordMenu(IdenMIDlet m) {
    	
    	parent = m;
    	display = Display.getDisplay(parent);
    	
    	f = new Form("Trak It iDEN");
        
    	tfPassword = new TextField("Password: ", "", 30, TextField.NUMERIC);
        
    	f.append(tfPassword);
                
        f.addCommand(enterCommand);
        f.setCommandListener(this);

        display.setCurrent(f);
    }
    
    public void commandAction(Command c, Displayable s) {
    	if (c == enterCommand) {
    		if (tfPassword.getString().equals("457886")) {
    			display.setCurrent(parent.f);
    		}
    		else {
    			Alert a = new Alert("Trak It iDEN", "\n\nIncorrect\nPassword",
            			null, AlertType.ERROR);
        				a.setTimeout(2000);
        				display.setCurrent(a);
    		}
        }
    }
}
