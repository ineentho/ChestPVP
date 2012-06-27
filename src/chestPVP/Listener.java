package chestPVP;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.entity.CraftArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Listener implements org.bukkit.event.Listener {
	public ChestPVP plugin;
	Random r = new Random();
	int countdownTime = 0;
	int task = -1;

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getType() == Material.CHEST) {
				// plugin.getServer().broadcastMessage(name +
				// " found a chest!");
			}
		} else if (e.getAction() == Action.LEFT_CLICK_BLOCK
				&& e.getClickedBlock().getType() == Material.CHEST) {
			// plugin.getServer().broadcastMessage(name + " found a chest!");
			e.getClickedBlock().setType(Material.AIR);
		}
	}

	@EventHandler
	public void onInvClose(final InventoryCloseEvent e) {
		Inventory inv = e.getInventory();
		if (inv.getType() == InventoryType.CHEST) {
			Chest b = (Chest) inv.getHolder();
			e.getPlayer().getWorld().getBlockAt(b.getLocation())
					.setType(Material.AIR);
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		plugin.scores.remove(e.getPlayer());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		boolean suicide = false;
		if (plugin.started && !plugin.ended) {
			// Player died
			event.setDroppedExp(100);
			EntityDamageEvent cause = event.getEntity().getLastDamageCause();
			boolean delayedKill = false;
			Player delayedKiller = null;
			if (damagers.containsKey(event.getEntity())) {
				delayedKill = true;
				delayedKiller = damagers.get(event.getEntity());
				damagers.remove(event.getEntity());
			}
			if (cause instanceof EntityDamageByEntityEvent || delayedKill) {
				Player pl;
				if (delayedKill)
					pl = delayedKiller;
				else {
					EntityDamageByEntityEvent c = (EntityDamageByEntityEvent) cause;
					if (c.getDamager() instanceof Player)
						pl = (Player) c.getDamager();
					else
						pl = ((Player) ((CraftArrow) c.getDamager())
								.getShooter());
				}
				if (pl == event.getEntity()) {
					suicide = true;
				} else {
					plugin.scores.put(pl, plugin.scores.get(pl) + 2);
					event.getEntity()
							.getServer()
							.broadcastMessage(
									ChatColor.AQUA + pl.getName()
											+ ChatColor.WHITE + " got "
											+ ChatColor.AQUA + "2"
											+ ChatColor.WHITE
											+ " points, increasing to "
											+ ChatColor.AQUA
											+ plugin.scores.get(pl)
											+ ChatColor.WHITE
											+ " total points.");
					if (plugin.scores.get(pl) > 14) {
						plugin.getServer()
								.broadcastMessage(
										ChatColor.AQUA
												+ pl.getName()
												+ ChatColor.WHITE
												+ " won! Server restarting & resetting in one minute.");
						Helper.updateStatus(pl.getName()
								+ "won, resetting soon");
						plugin.endString = "<span style='color: white'>"
								+ pl.getName() + " </span>won. Resetting in ";
						Calendar c1 = Calendar.getInstance();
						c1.setTimeZone(TimeZone.getTimeZone("UTC"));
						plugin.startTime = c1.getTime().getTime();
						plugin.ended = true;
						plugin.getServer()
								.getScheduler()
								.scheduleSyncDelayedTask(plugin,
										new Runnable() {
											public void run() {
												plugin.getServer()
														.dispatchCommand(
																Bukkit.getConsoleSender(),
																"stop");
											}
										}, 1200L);
					} else
						suicide = true;
				}
				if (suicide) {
					pl = (Player) event.getEntity();
					event.setDeathMessage(ChatColor.AQUA + pl.getName()
							+ ChatColor.WHITE + " lost " + ChatColor.AQUA + "1"
							+ ChatColor.WHITE + " point, decrasing to "
							+ ChatColor.AQUA + plugin.scores.get(pl)
							+ ChatColor.WHITE + " total points.");
				}
				pl = (Player) event.getEntity();
				plugin.scores.put(pl, plugin.scores.get(pl) - 1);

			} else {
				// Suicide
				Player pl = event.getEntity();
				plugin.scores.put(pl, plugin.scores.get(pl) - 1);
				event.setDeathMessage(ChatColor.AQUA + pl.getName()
						+ ChatColor.WHITE + " lost " + ChatColor.AQUA + "1"
						+ ChatColor.WHITE
						+ " point due to suicide, decrasing to "
						+ ChatColor.AQUA + plugin.scores.get(pl)
						+ ChatColor.WHITE + " total points.");
			}
			Location l = event.getEntity().getLocation();
			World w = plugin.getServer().getWorlds().get(0);
			w.strikeLightningEffect(l).setFireTicks(0);
			Helper.updateScoreboard(plugin.scores);
			ItemStack item = new ItemStack(Material.DIAMOND, 1);
			w.dropItem(l, item);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Material m = e.getBlock().getType();
		if (m != Material.WOOL && m != Material.OBSIDIAN
				&& m != Material.SNOW_BLOCK && m != Material.SNOW)
			e.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(final BlockPlaceEvent e) {
		Material m = e.getBlock().getType();
		if (m == Material.ENCHANTMENT_TABLE || m == Material.FURNACE
				|| m == Material.WORKBENCH || m == Material.BOOKSHELF) {
			plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask(plugin, new Runnable() {
						public void run() {
							e.getBlock().setType(Material.AIR);
						}
					}, 2400L);
		}
	}

	@EventHandler
	public void onDisconnect(PlayerQuitEvent e) {
		plugin.scores.remove(e.getPlayer());
		Helper.updateScoreboard(plugin.scores);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (plugin.started) {
			plugin.scores.put(event.getPlayer(), 0);
			Helper.reSpawn(event.getPlayer(), false);
			Helper.updateScoreboard(plugin.scores);
		} else {
			Helper.reSpawn(event.getPlayer(), true);

			HashMap<Player, Integer> map = new HashMap<Player, Integer>();
			for (Player p : plugin.getServer().getOnlinePlayers())
				map.put(p, -100000);
			Helper.updateScoreboard(map);
			// Check if we should start
			int p = plugin.getServer().getOnlinePlayers().length;
			if (p == 1)
				event.setJoinMessage("Waiting for more players, at least "
						+ ChatColor.AQUA + "2" + ChatColor.WHITE
						+ " players are required to start.");
			else if (p == 2) {
				if (task == -1) {
					event.setJoinMessage("Enough players has connected, starting in "
							+ ChatColor.AQUA
							+ "3"
							+ ChatColor.WHITE
							+ " minutes.");
					Calendar ca = Calendar.getInstance();
					ca.setTimeZone(TimeZone.getTimeZone("UTC"));
					final long startTime = ca.getTime().getTime();
					task = plugin.getServer().getScheduler()
							.scheduleSyncRepeatingTask(plugin, new Runnable() {
								public void run() {
									// This is run every second until the event
									// starts
									if (plugin.started) {
										plugin.getServer().getScheduler()
												.cancelTask(task);
										return;
									}
									countdownTime++;
									if (countdownTime == 180) {
										plugin.getServer().dispatchCommand(
												Bukkit.getConsoleSender(),
												"start");
										plugin.getServer().getScheduler()
												.cancelTask(task);
										return;
									}
									if (countdownTime % 10 == 0)
										plugin.getServer().broadcastMessage(
												"Starting in " + ChatColor.AQUA
														+ (180 - countdownTime)
														+ ChatColor.WHITE
														+ " seconds.");
									// Update website
									Calendar c = Calendar.getInstance();
									c.setTimeZone(TimeZone.getTimeZone("UTC"));
									long t = c.getTime().getTime();
									int diff = (int) (180000 - (t - startTime));
									int m = (diff / 1000) / 60;
									int s = (diff / 1000) % 60;
									String sM = String.valueOf(m);
									String sS = String.valueOf(s);
									if (sM.length() == 1)
										sM = "0" + sM;
									if (sS.length() == 1)
										sS = "0" + sS;
									Helper.updateStatus("Game starting in <span style='color:white'>("
											+ sM + ":" + sS + ")</span>");
								}

							}, 20L, 20L);
				}
			}
		}
	}

	@EventHandler
	public void reSpawnEvent(final PlayerRespawnEvent event) {
		plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						Helper.reSpawn(event.getPlayer(), false);
					}
				}, 1L);
	}

	@EventHandler
	public void walkOut(PlayerMoveEvent e) {
		double x = e.getTo().getX();
		double z = e.getTo().getZ();
		if (x < 0 || x > 200 || z < 0 || z > 200) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("Don't try to sneak under the fence!");
		}
	}

	Map<Player, Player> damagers = new HashMap<Player, Player>();

	@EventHandler
	public void onDmg(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player LD;
			if (e.getDamager() instanceof CraftArrow) {
				e.setDamage((int) Math.ceil(e.getDamage() / 2f)); // Nerf bows
				LD = (Player) ((CraftArrow) e.getDamager()).getShooter();
			} else
				LD = (Player) e.getDamager();
			Player damaged = (Player) e.getEntity();
			damagers.put(damaged, LD);
			// LD damaged damaged
		}

	}

	@EventHandler
	void onLoot(final PlayerPickupItemEvent e) {
		plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						cleanInv(e.getPlayer());
					}
				}, 0L);
	}

	@EventHandler
	void onGetItem(InventoryClickEvent e) {
		cleanInv((Player) e.getWhoClicked());
	}

	@SuppressWarnings("deprecation")
	boolean cleanInv(Player p) {
		Inventory i = p.getInventory();
		int nr = 0;
		for (ItemStack item : i.getContents()) {
			if (item != null && item.getType() != Material.AIR) {
				boolean r = false;
				Material m = item.getType();

				// BOOTS
				if (m == Material.LEATHER_BOOTS)
					if (i.all(m).size() > 1)
						r = true;
					else if (i.contains(Material.IRON_BOOTS))
						r = true;
					else if (i.contains(Material.DIAMOND_BOOTS))
						r = true;
				if (m == Material.IRON_BOOTS)
					if (i.all(m).size() > 1)
						r = true;
					else if (i.contains(Material.DIAMOND_BOOTS))
						r = true;
				if (m == Material.DIAMOND_BOOTS)
					if (i.all(m).size() > 1)
						r = true;

				// LEGGINGS
				if (m == Material.LEATHER_LEGGINGS)
					if (i.all(m).size() > 1)
						r = true;
					else if (i.contains(Material.IRON_LEGGINGS))
						r = true;
					else if (i.contains(Material.DIAMOND_LEGGINGS))
						r = true;
				if (m == Material.IRON_LEGGINGS)
					if (i.all(m).size() > 1)
						r = true;
					else if (i.contains(Material.DIAMOND_LEGGINGS))
						r = true;
				if (m == Material.DIAMOND_LEGGINGS)
					if (i.all(m).size() > 1)
						r = true;

				// CHESTPLATE
				if (m == Material.LEATHER_CHESTPLATE)
					if (i.all(m).size() > 1)
						r = true;
					else if (i.contains(Material.IRON_CHESTPLATE))
						r = true;
					else if (i.contains(Material.DIAMOND_CHESTPLATE))
						r = true;
				if (m == Material.IRON_CHESTPLATE)
					if (i.all(m).size() > 1)
						r = true;
					else if (i.contains(Material.DIAMOND_CHESTPLATE))
						r = true;
				if (m == Material.DIAMOND_CHESTPLATE)
					if (i.all(m).size() > 1)
						r = true;

				// HELMET
				if (m == Material.LEATHER_HELMET)
					if (i.all(m).size() > 1)
						r = true;
					else if (i.contains(Material.IRON_HELMET))
						r = true;
					else if (i.contains(Material.DIAMOND_HELMET))
						r = true;
				if (m == Material.IRON_HELMET)
					if (i.all(m).size() > 1)
						r = true;
					else if (i.contains(Material.DIAMOND_HELMET))
						r = true;
				if (m == Material.DIAMOND_HELMET)
					if (i.all(m).size() > 1)
						r = true;

				// SWORDS
				if (m == Material.WOOD_SWORD)
					if (i.all(m).size() > 1)
						r = true;
					else if (i.contains(Material.STONE_SWORD))
						r = true;
					else if (i.contains(Material.IRON_SWORD))
						r = true;
					else if (i.contains(Material.DIAMOND_SWORD))
						r = true;
				if (m == Material.STONE_SWORD)
					if (i.all(m).size() > 1)
						r = true;
					else if (i.contains(Material.IRON_SWORD))
						r = true;
					else if (i.contains(Material.DIAMOND_SWORD))
						r = true;
				if (m == Material.IRON_SWORD)
					if (i.all(m).size() > 1)
						r = true;
					else if (i.contains(Material.DIAMOND_SWORD))
						r = true;

				if (r) {
					System.out.println("Removing from slot number " + nr);
					i.setItem(nr, new ItemStack(Material.AIR));
					p.updateInventory();
					return true;
				}
			}
			nr++;
		}
		return false;
	}
	// CHUNKS
	/*
	 * @EventHandler public void chunkLoad(ChunkPopulateEvent e) { Chunk c =
	 * e.getChunk(); //delChunk(c); }
	 * 
	 * private void delChunk(Chunk c) { for (int x = 0; x < 16; x++) { for (int
	 * y = 0; y < 128; y++) { for (int z = 0; z < 16; z++) { Block b =
	 * c.getBlock(x, y, z); if (b.getX() > 200 && b.getType() != Material.AIR) {
	 * b.setType(Material.AIR);
	 * System.out.println("Removed block at "+b.getLocation()); } } } } }
	 */
}
