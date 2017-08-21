package net.yocraft.ffarush;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class MyListener implements Listener {
	public static Inventory inv;

	public void load() {
		inv = Bukkit.createInventory(null, 36);
		ItemStack leave = new ItemStack(Material.BED, 1);
		ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
		ItemMeta meta = sword.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "PLAY!");
		sword.setItemMeta(meta);
		ItemMeta meta2 = leave.getItemMeta();
		meta2.setDisplayName(ChatColor.RED + "To lobby");
		leave.setItemMeta(meta2);
		inv.setItem(4, sword);
		inv.setItem(8, leave);
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		if (Bukkit.getWorld("FFARush").equals(e.getPlayer().getWorld()))
			if (e.getBlock().getType().equals(Material.TNT)) {
				e.getBlock().setType(Material.AIR);
				TNTPrimed tnt = (TNTPrimed) e.getPlayer().getWorld().spawnEntity(e.getBlock().getLocation(),
						EntityType.PRIMED_TNT);
				tnt.setFuseTicks(ConfigManager.conf.getInt("primedTntExplode"));
				tnt.setYield(ConfigManager.conf.getInt("tntYield"));
			} else if (ConfigManager.blocks.contains(e.getBlock().getType())) {
				final Block b = e.getBlock();
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
					@Override
					public void run() {
						b.setType(Material.AIR);
					}
				}, ConfigManager.conf.getLong("blockremove"));
			}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (Bukkit.getWorld("FFARush").equals(e.getEntity().getWorld()))
			if (e.getEntityType().equals(EntityType.PLAYER)) {
				if (e.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
					e.setDamage(ConfigManager.conf.getInt("tntDamage"));
				}
			}
	}

	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent e) {
		if (!ConfigManager.conf.getBoolean("tntDamage"))
			if (Bukkit.getWorld("FFARush").equals(e.getEntity().getWorld())) {
				e.blockList().clear();
			}
	}

	@EventHandler
	public void PlayerDamageReceive(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player damaged = (Player) e.getEntity();

			if (e.getDamager() instanceof Player) {
				Player damager = (Player) e.getDamager();

				if (damaged.getWorld().equals(Bukkit.getWorld("FFARush"))) {
					if ((damaged.getHealth() - e.getDamage()) <= 0) {
						e.setCancelled(true);
						damaged.teleport((Location) ConfigManager.conf.get("lobbyloc"));
						damaged.setHealth(20);
						Bukkit.broadcastMessage(ChatColor.BLUE + damager.getDisplayName() + ChatColor.WHITE + " killed "
								+ damaged.getDisplayName());
						damaged.getInventory().setContents(inv.getContents());
						damaged.updateInventory();
					}
				}
			}
		}
	}

	@EventHandler
	public void Interact(PlayerInteractEvent e) {
		if (e.getPlayer().getWorld().equals(Bukkit.getWorld("FFARush"))) {
			Action action = e.getAction();
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				Player p = e.getPlayer();
				// ItemStack item = p.getItemInHand();

				if (p.getItemInHand().getType().equals(Material.ANVIL))
					p.sendMessage("wth?");

				if (p.getItemInHand().getType() == Material.DIAMOND_SWORD) {
					p.sendMessage("vent");
					p.sendMessage(ChatColor.GREEN + "Joining the arena!");
					Main.arena.addPlayer(p);
				}
				if (p.getItemInHand().getType().equals(Material.BED)) {
					p.sendMessage(ChatColor.RED + "Leaving.");
					if (ConfigManager.conf.getBoolean("useBungee")) {
						// Untested
						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("Connect");
						out.writeUTF(ConfigManager.conf.getString("bungeeLobby"));
						p.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
					} else {
						p.performCommand(ConfigManager.conf.getString("leaveCmd"));
					}
				}
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (e.getPlayer().isOp())
			Main.update.update((CommandSender) e.getPlayer());
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (e.getEntity().getLocation().getWorld().getName().equals("FFARush")) {
			if (ConfigManager.conf.contains("dropsRemoved") && ConfigManager.conf.getBoolean("dropsRemoved"))
				for (ItemStack i : e.getDrops()) {
					i.setAmount(0);
				}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn (PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (p.getLocation().getWorld().getName().equals("FFARush")) {
			e.setRespawnLocation((Location) ConfigManager.conf.get("lobbyloc"));
			p.getInventory().clear();
			p.getInventory().setContents(inv.getContents());
			p.updateInventory();
		}
	}
}