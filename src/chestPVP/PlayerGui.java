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
		float textScale = 1;
		int textSpace = 0, xPos = 0;
		switch (s) {
		case 1:
			textScale = 0.4f;
			textSpace = 4;
			xPos = 30;
			break;
		case 2:
			textScale = 0.7f;
			textSpace = 7;
			xPos = 50;
			break;
		case 3:
			textScale = 1f;
			textSpace = 10;
			xPos = 80;
			break;
		}

		SpoutPlayer pl = (SpoutPlayer) player;
		pl.getMainScreen().removeWidgets(plugin);
		GenericLabel playerName = new GenericLabel();
		GenericLabel score = new GenericLabel();
		playerName.setText(ChatColor.AQUA + "Player:").setX(4).setY(3).setHeight(5);
		playerName.setScale(textScale);
		score.setText(ChatColor.AQUA + "Score:").setX(xPos).setY(3).setHeight(5);
		score.setScale(textScale);
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
			lineP.setHeight(5);
			lineP.setScale(textScale);
			lineS = new GenericLabel();
			lineS.setY(3 + textSpace + (l * textSpace));
			lineS.setHeight(5);
			lineS.setX(xPos);
			lineS.setText(plugin.scores.get(p) + "");
			lineS.setScale(textScale);
			pl.getMainScreen().attachWidgets(plugin, lineS, lineP);
		}
	}
}