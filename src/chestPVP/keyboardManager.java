package chestPVP;

import org.getspout.spoutapi.event.input.KeyBindingEvent;
import org.getspout.spoutapi.keyboard.BindingExecutionDelegate;

public class keyboardManager implements BindingExecutionDelegate {

	ChestPVP plugin;
	public keyboardManager(ChestPVP plugin){
	this.plugin=plugin;
	}
	public void keyPressed(KeyBindingEvent e) {
		// Change size
		Integer s = plugin.scoreSize.get(e.getPlayer());
		if(s==null)
			s = 3;
		else if (s == 1)
			s = 2;
		else if (s == 2)
			s = 3;
		else if (s == 3)
			s = 1;
		plugin.scoreSize.put(e.getPlayer(), s);
		PlayerGui.updateScoreGui(e.getPlayer(), plugin);

	}

	public void keyReleased(KeyBindingEvent arg0) {
	}

}