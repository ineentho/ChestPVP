package chestPVP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.player.SpoutPlayer;

public class PlayerGui {

	public static void updateAllScoreGui(ChestPVP plugin) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			updateScoreGui(p, plugin);
		}
	}

	public static void updateScoreGui(Player player, ChestPVP plugin) {
		// UI SIZE
		Integer s = plugin.scoreSize.get(player);
		if (s == null)
			s = 3;
		int textHeight = 0, textSpace = 0; 
		switch (s) {
		case 1:
			textHeight = 2;
			textSpace = 3;
			break;
		case 2:
			textHeight = 4;
			textSpace = 7;
			break;
		case 3:
			textHeight = 6;
			textSpace = 10;
			break;
		}

		SpoutPlayer pl = (SpoutPlayer) player;
		pl.getMainScreen().removeWidgets(plugin);
		GenericLabel playerName = new GenericLabel();
		GenericLabel score = new GenericLabel();
		playerName.setText(ChatColor.AQUA + "Player:").setX(4).setY(3).setHeight(textHeight);
		score.setText(ChatColor.AQUA + "Score:").setX(80).setY(3).setHeight(textHeight);
		pl.getMainScreen().attachWidget(plugin, playerName);
		pl.getMainScreen().attachWidget(plugin, score);
		int l = -1;
		for (Player p : Bukkit.getOnlinePlayers()) {
			l++;
			GenericLabel lineP;
			GenericLabel lineS;
			lineP = new GenericLabel();
			lineP.setY(3 + textSpace + (l * textSpace));
			lineP.setX(4);
			lineP.setText(p.getName());
			lineP.setHeight(textHeight);
			lineS = new GenericLabel();
			lineS.setY(3 + textSpace + (l * textSpace));
			lineS.setHeight(textHeight);
			lineS.setX(80);
			lineS.setText(plugin.scores.get(p) + "");
			pl.getMainScreen().attachWidgets(plugin, lineS, lineP);
		}
	}
}