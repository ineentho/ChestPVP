package chestPVP;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.player.SpoutPlayer;

public class PlayerGui {

	public static void updateAllScoreGui(ChestPVP plugin){
		for (Player p : Bukkit.getOnlinePlayers()) {
			updateScoreGui(p,plugin);
		}
	}
	public static void updateScoreGui(Player player, ChestPVP plugin) {
		SpoutPlayer pl = (SpoutPlayer) player;
		pl.getMainScreen().removeWidgets(plugin);
		GenericLabel playerName = new GenericLabel();
		GenericLabel score = new GenericLabel();
		playerName.setText(ChatColor.AQUA + "Player:").setX(4).setY(3).setHeight(6);
		score.setText(ChatColor.AQUA + "Score:").setX(80).setY(3);
		pl.getMainScreen().attachWidget(plugin, playerName);
		pl.getMainScreen().attachWidget(plugin, score);
		int l=-1;
		for (Player p : Bukkit.getOnlinePlayers()) {
			l++;
			GenericLabel lineP;
			GenericLabel lineS;
			lineP = new GenericLabel();
			lineP.setY(3 + 10 + (l * 10));
			lineP.setHeight(5);
			lineP.setX(4);
			lineP.setText(p.getName());
			lineS = new GenericLabel();
			lineS.setY(3 + 10 + (l * 10));
			lineS.setHeight(5);
			lineS.setX(80);
			lineS.setText(plugin.scores.get(p) + "");
			pl.getMainScreen().attachWidgets(plugin, lineS, lineP);
		}
	}
}