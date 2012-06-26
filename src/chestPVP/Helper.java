package chestPVP;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Helper {
	static Random r = new Random();

	static public void reSpawn(Player player, boolean center) {
		int x, z;
		if (center) {
			x = 100;
			z = 100;
		} else {
			x = r.nextInt(200);
			z = r.nextInt(200);
		}
		Location loc = new Location(player.getWorld(), x + 0.5, player
				.getWorld().getHighestBlockYAt(x, z), z + 0.5);
		player.teleport(loc);
		player.setHealth(20);
		player.setFoodLevel(20);
	}

	static public void updateStatus(String s) {
		try {
			FileWriter fw = new FileWriter("N:/www/chestpvp.tk/status.txt");
			fw.write(s);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static public void updateScoreboard(HashMap<Player, Integer> scores){
		try {
			FileWriter fw = new FileWriter("N:/www/chestpvp.tk/scoreboard.txt");
			String s = "";
			//<tr><td>Alpha</td><td>6</</tr>
			for (Entry<Player, Integer> entry : scores.entrySet()) {
				String n = entry.getKey().getName();
				String v = entry.getValue().toString();
				if(entry.getValue()==-100000)
					v = "-";
					
				s+="<tr><td>"+n+"</td><td>"+v+"</td></tr>";
			}
			
			fw.write(s);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
