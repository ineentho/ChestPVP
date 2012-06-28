package chestPVP;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;
import java.util.TimeZone;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

public class CommandExec implements CommandExecutor {
	public ChestPVP plugin;
	boolean snowEvent = false;
	Random r = new Random();

	public CommandExec(ChestPVP chestPVP) {
		this.plugin = chestPVP;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("start")) {
			if (!sender.isOp())
				return false;

			if (!plugin.started) {
				plugin.started = true;
				plugin.getServer()
						.broadcastMessage(
								ChatColor.AQUA
										+ "Game started"
										+ ChatColor.WHITE
										+ ", 10 chests has been spred all over the world. First player to 15 points will win, good luck!");
				startTask();

				World w = plugin.getServer().getWorlds().get(0);
				w.setPVP(true);
				for (Iterator<Player> i = w.getPlayers().iterator(); i.hasNext();) {
					Player p = i.next();
					plugin.scores.put(p, 0);
					Helper.reSpawn(p, false);
				}
				Helper.updateScoreboard(plugin.scores);
				PlayerGui.updateAllScoreGui(plugin);

				for (int i = 0; i < 10; i++) {
					spawnChest(false);
				}
				Calendar c = Calendar.getInstance();
				c.setTimeZone(TimeZone.getTimeZone("UTC"));
				plugin.startTime = c.getTime().getTime();
				plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
					public void run() {
						Calendar c = Calendar.getInstance();
						c.setTimeZone(TimeZone.getTimeZone("UTC"));
						long t = c.getTime().getTime();
						int diff;
						if (plugin.ended)
							diff = (int) (60000 - (t - plugin.startTime));
						else
							diff = (int) (t - plugin.startTime);
						int m = (diff / 1000) / 60;
						int s = (diff / 1000) % 60;
						String sM = String.valueOf(m);
						String sS = String.valueOf(s);
						if (sM.length() == 1)
							sM = "0" + sM;
						if (sS.length() == 1)
							sS = "0" + sS;
						if (!plugin.ended)
							Helper.updateStatus("Game in progress <span style='color:white'>(" + sM + ":" + sS
									+ ")</span>");
						else
							Helper.updateStatus(plugin.endString + " <span style='color:white'>(" + sM + ":" + sS
									+ ")</span>");
					}
				}, 0L, 20L);
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("event")) {
			if (!sender.isOp())
				return false;
			event(Integer.parseInt(args[0]));
			return true;
		}
		return false;
	}

	void startTask() {
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				spawnChest(false);
			}
		}, 0L, 100L);
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				event();
			}
			// }, 3000L, 3000L);
		}, 1000L, 3000L);

		// Remove snow
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				if (!snowEvent) {
					for (int i = 0; i < 100; i++) {
						removeSnowBlock();
					}
				}
			}
		}, 0L, 1L);
	}

	void removeSnowBlock() {
		int x = r.nextInt(200);
		int z = r.nextInt(200);
		World w = plugin.getServer().getWorlds().get(0);
		Block b = w.getHighestBlockAt(x, z).getRelative(BlockFace.DOWN);
		if (b.getType() == Material.SNOW_BLOCK)
			b.setType(Material.AIR);
	}

	void event() {
		int y = r.nextInt(7);
		if (y == 5)
			y = 2;
		event(y);
	}

	void eventNotification(String e, Material m) {
		World w = plugin.getServer().getWorlds().get(0);
		for (Iterator<Player> i = w.getPlayers().iterator(); i.hasNext();) {
			Player p = i.next();
			SpoutPlayer sp = (SpoutPlayer) p;
			if (sp.isSpoutCraftEnabled())
				sp.sendNotification("Event started", e, m);
			else
				p.sendMessage(ChatColor.GREEN + "Event started: " + ChatColor.WHITE + e);
		}
	}

	void event(int y) {
		if (y == 0) {
			// Reveal enemies
			eventNotification("Smoke Pillars", Material.COAL);
			final World w = plugin.getServer().getWorlds().get(0);
			final int s = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					for (Iterator<Player> i = w.getPlayers().iterator(); i.hasNext();) {
						Player p = i.next();
						for (int h = 0; h < 200; h++) {
							Location loc = p.getLocation();
							loc.setY(h);
							w.playEffect(loc, Effect.SMOKE, 0);
						}
					}
				}
			}, 0L, 1L);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					plugin.getServer().getScheduler().cancelTask(s);
				}
			}, 100L);
		} else if (y == 1) {
			// Exp rain
			eventNotification("Exp rain in the center!", Material.EXP_BOTTLE);
			final World w = plugin.getServer().getWorlds().get(0);
			final int s = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					Location loc = new Location(w, 100, plugin.top - 5, 100);
					((ExperienceOrb) w.spawn(loc, ExperienceOrb.class)).setExperience(20);
				}
			}, 0L, 5L);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					plugin.getServer().getScheduler().cancelTask(s);
				}
			}, 200L);

		} else if (y == 2) {
			// Group hug!
			eventNotification("Group hug!", Material.BED);
			final World w = plugin.getServer().getWorlds().get(0);
			for (Iterator<Player> i = w.getPlayers().iterator(); i.hasNext();) {
				Player p = i.next();
				Location loc = new Location(p.getWorld(), 100, p.getWorld().getHighestBlockYAt(100, 100), 100);
				p.teleport(loc);
			}
		} else if (y == 3) {
			// Diamonds!
			eventNotification("Diamonds in the center!", Material.DIAMOND);
			final World w = plugin.getServer().getWorlds().get(0);
			final int s = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					int i = r.nextInt(100);
					Location loc = new Location(w, 100, plugin.top - 5, 100);
					Material m;
					if (i == 0)
						m = Material.DIAMOND_AXE;
					else if (i == 1)
						m = Material.DIAMOND_BOOTS;
					else if (i == 2)
						m = Material.DIAMOND_CHESTPLATE;
					else if (i == 3)
						m = Material.DIAMOND_HELMET;
					else if (i == 4)
						m = Material.DIAMOND_HOE;
					else if (i == 5)
						m = Material.DIAMOND_LEGGINGS;
					else if (i == 6)
						m = Material.DIAMOND_PICKAXE;
					else if (i == 7)
						m = Material.DIAMOND_SPADE;
					else if (i == 8)
						m = Material.DIAMOND_SWORD;
					else
						m = Material.DIAMOND;
					ItemStack item = new ItemStack(m, 1);
					w.dropItem(loc, item);
				}
			}, 0L, 20L);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					plugin.getServer().getScheduler().cancelTask(s);
				}
			}, 100L);
		} else if (y == 4) {
			// Chest madness!
			eventNotification("Chest madness!", Material.CHEST);
			for (int i = 0; i < 20; i++) {
				spawnChest(true);
			}
		} else if (y == 5) {
			snowEvent = true;
			final World w = plugin.getServer().getWorlds().get(0);
			w.setStorm(true);
			w.setWeatherDuration(950);
			eventNotification("Snowstorm!", Material.SNOW_BLOCK);
			final int s = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					for (int i = 0; i < 50; i++) {
						spawnSnowBlock();
					}
				}
			}, 0L, 1L);
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					plugin.getServer().getScheduler().cancelTask(s);
					w.setWeatherDuration(Integer.MAX_VALUE);
					snowEvent = false;
				}
			}, 1000L);
		} else if (y == 6) {
			final World w = plugin.getServer().getWorlds().get(0);
			eventNotification("Cows!", Material.FLINT);
			Location loc = new Location(w, 100, plugin.top - 10, 100);
			for (int i = 0; i < 20; i++) {
				w.spawnCreature(loc, EntityType.COW);
			}
		}
	}

	void spawnSnowBlock() {
		int x = r.nextInt(200);
		int z = r.nextInt(200);
		World w = plugin.getServer().getWorlds().get(0);
		Block b = w.getHighestBlockAt(x, z);
		b.setType(Material.SNOW_BLOCK);
	}

	void spawnChest(boolean close) {
		int x;
		int z;
		if (!close) {
			x = r.nextInt(200);
			z = r.nextInt(200);
		} else {
			x = 90 + r.nextInt(20);
			z = 90 + r.nextInt(20);
		}
		World w = plugin.getServer().getWorlds().get(0);
		Block b = w.getHighestBlockAt(x, z);
		b.setType(Material.CHEST);
		final Chest c = (Chest) b.getState();
		int ammItems = 2 + r.nextInt(5);
		for (int itemNr = 0; itemNr < ammItems; itemNr++) {
			int nr = r.nextInt(95);
			Material mat;
			int amm = 1;
			if (nr < 1)
				mat = Material.TNT; // 1
			else if (nr < 5)
				mat = Material.WOOD_SWORD; // 4
			else if (nr < 7)
				mat = Material.STONE_SWORD; // 2
			else if (nr < 8)
				mat = Material.IRON_SWORD; // 1
			else if (nr < 9)
				mat = Material.OBSIDIAN; // 1
			else if (nr < 13)
				mat = Material.BOOK; // 4
			else if (nr < 15)
				mat = Material.APPLE; // 2
			else if (nr < 16)
				mat = Material.GOLDEN_APPLE; // 1
			else if (nr < 23) {
				mat = Material.ARROW; // 5
				amm = r.nextInt(2) + 1;
			} else if (nr < 26)
				mat = Material.CACTUS; // 2
			else if (nr < 28)
				mat = Material.BOW; // 5
			else if (nr < 30)
				mat = Material.ENCHANTMENT_TABLE; // 2
			else if (nr < 35) {
				mat = Material.WOOL; // 5
				amm = r.nextInt(20) + 2;
			} else if (nr < 37)
				mat = Material.FLINT_AND_STEEL; // 2
			else if (nr < 39)
				mat = Material.LEATHER_BOOTS; // 2
			else if (nr < 41)
				mat = Material.LEATHER_CHESTPLATE; // 2
			else if (nr < 43)
				mat = Material.LEATHER_HELMET; // 2
			else if (nr < 45)
				mat = Material.LEATHER_LEGGINGS; // 2
			else if (nr < 46)
				mat = Material.IRON_HELMET; // 1
			else if (nr < 47)
				mat = Material.IRON_BOOTS; // 1
			else if (nr < 48)
				mat = Material.IRON_CHESTPLATE; // 1
			else if (nr < 49)
				mat = Material.IRON_LEGGINGS; // 1
			else if (nr < 50)
				mat = Material.DIAMOND; // 1
			else if (nr < 55)
				mat = Material.WORKBENCH; // 5
			else if (nr < 57)
				mat = Material.STICK; // 2
			else if (nr < 60) {
				mat = Material.BOOKSHELF; // 3
				amm = r.nextInt(5) + 1;
			} else
				mat = Material.EXP_BOTTLE; // 20
			c.getInventory().addItem(new ItemStack(mat, amm));
		}
		final Location loc = c.getLocation();
		// Remove chest after 2 minutes
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				c.getBlockInventory().clear();
				plugin.getServer().getWorlds().get(0).getBlockAt(loc).setType(Material.AIR);
			}
		}, 2400L);
	}
}
