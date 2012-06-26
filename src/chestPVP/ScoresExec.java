package chestPVP;

import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScoresExec implements CommandExecutor {
	ChestPVP plugin;

	public ScoresExec(ChestPVP chestPVP) {
		plugin = chestPVP;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (plugin.started) {
			sender.sendMessage("SCORES " + ChatColor.GRAY + "[First to 15 win]");
			for (Entry<Player, Integer> entry : plugin.scores.entrySet()) {
				sender.sendMessage(ChatColor.AQUA + entry.getKey().getName()
						+ ChatColor.WHITE + ": " + entry.getValue());
			}
		} else
			sender.sendMessage("Game has not started yet.");
		return true;
	}

}
