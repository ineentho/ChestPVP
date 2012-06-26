package chestPVP;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.MaterialManager;

public class ChestPVP extends JavaPlugin {
	boolean started = false;
	public int top;
	String endString;
	public boolean ended = false;
	public long startTime;
	HashMap<Player, Integer> scores = new HashMap<Player, Integer>();

	public void onDisable() {
		Helper.updateStatus("Resetting world");
		Helper.updateScoreboard(new HashMap<Player, Integer>());
		Iterator<Player> it = this.getServer().getWorlds().get(0).getPlayers()
				.iterator();
		for (Iterator<Player> i = it; i.hasNext();) {
			Player p = i.next();
			p.closeInventory();
			p.kickPlayer("Resetting world, please reconnect in 10 seconds.   "
					+ ChatColor.GREEN + ChatColor.UNDERLINE + "www.ChestPVP.tk");
		}
	}

	public void onEnable() {
		try {
			Helper.updateScoreboard(scores);
			Helper.updateStatus("Waiting for players");
			MaterialManager mm = SpoutManager.getMaterialManager();
			WallGlass wallGlass = new WallGlass(this);
			Listener listener = new Listener();
			listener.plugin = this;
			getServer().getPluginManager().registerEvents(listener, this);
			CommandExec cmdExec = new CommandExec(this);
			ScoresExec scoresExec = new ScoresExec(this);
			getCommand("start").setExecutor(cmdExec);
			getCommand("event").setExecutor(cmdExec);
			getCommand("scores").setExecutor(scoresExec);
			getCommand("s").setExecutor(scoresExec);

			World w = getServer().getWorlds().get(0);
			w.setPVP(false);
			w.setSpawnLocation(100, w.getHighestBlockYAt(100, 100), 100);
			int x, y, z = 0;
			// Set biome to winter type
			w.setStorm(false);
			w.setWeatherDuration(Integer.MAX_VALUE);

			/*for (x = 0; x < 200; x++) {
				for (z = 0; z < 200; z++) {
					w.setBiome(x, z, Biome.TAIGA);
				}
			}*/

			// Create glass walls
			for (x = 1; x < 200; x++) {
				z = 0;
				for (y = 0; y < 200; y++) {
					Block b = w.getBlockAt(x, y, z);
					if (bWall(b))
						if (y % 5 == 0)
							mm.overrideBlock(b, wallGlass, (byte) 3);
						else
							mm.overrideBlock(b, wallGlass, (byte) 1);
				}
				z = 200;
				for (y = 0; y < 200; y++) {
					Block b = w.getBlockAt(x, y, z);
					if (bWall(b))
						if (y % 5 == 0)
							mm.overrideBlock(b, wallGlass, (byte) 1);
						else
							mm.overrideBlock(b, wallGlass, (byte) 3);
				}
			}
			z = 0;
			for (z = 1; z < 200; z++) {
				x = 0;
				for (y = 0; y < 200; y++) {
					Block b = w.getBlockAt(x, y, z);
					if (bWall(b))
						if (y % 5 == 0)
							mm.overrideBlock(b, wallGlass);
						else
							mm.overrideBlock(b, wallGlass, (byte) 2);

				}
				x = 200;
				for (y = 0; y < 200; y++) {
					Block b = w.getBlockAt(x, y, z);
					if (bWall(b))
						if (y % 5 == 0)
							mm.overrideBlock(b, wallGlass, (byte) 2);
						else
							mm.overrideBlock(b, wallGlass);
				}
			}

			// Create the center building
			x = 100;
			z = 100;
			y = w.getHighestBlockYAt(x, z) - 1;
			top = y + 20;
			for (x = 98; x < 103; x++) {
				for (z = 98; z < 103; z++) {
					for (int ny = y; ny < top; ny++) {
						if (ny == y)
							w.getBlockAt(x, y, z).setType(Material.GLOWSTONE);
						else if (x == 98 && z == 98 || x == 98 && z == 102
								|| x == 102 && z == 98 || x == 102 && z == 102)
							w.getBlockAt(x, ny, z).setType(Material.BRICK);
						else
							w.getBlockAt(x, ny, z).setType(Material.AIR);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Something went wrong starting the server");
			this.getServer().shutdown();
		}
	}

	boolean bWall(Block b) {
		Material m = b.getType();
		if (m == Material.STONE || m == Material.GRASS || m == Material.DIRT
				|| m == Material.BEDROCK || m == Material.SAND
				|| m == Material.GRAVEL || m == Material.GOLD_ORE
				|| m == Material.IRON_ORE || m == Material.COAL_ORE
				|| m == Material.WOOD || m == Material.LAPIS_ORE
				|| m == Material.SANDSTONE || m == Material.DIAMOND_ORE
				|| m == Material.REDSTONE_ORE || m == Material.CLAY
				|| m == Material.PUMPKIN || m == Material.HUGE_MUSHROOM_1
				|| m == Material.HUGE_MUSHROOM_2 || m == Material.MYCEL)
			return false;
		else
			return true;
	}
}
