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

import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class MyListener implements Listener {
	public static ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
	static {
		ItemMeta meta = sword.getItemMeta();
		meta.setDisplayName(ChatColor.GREEN + "PLAY!");
		sword.setItemMeta(meta);
	}
	public static ItemStack leave = new ItemStack(Material.BARRIER);
	static {
		ItemMeta meta = leave.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "To lobby");
		leave.setItemMeta(meta);
	}

	// Listens to Events.
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		if (Bukkit.getWorld("FFARush").equals(e.getPlayer().getWorld()))
			if (e.getBlock().getType().equals(Material.TNT)) {
				e.getBlock().setType(Material.AIR);
				TNTPrimed tnt = (TNTPrimed) e.getPlayer().getWorld().spawnEntity(e.getBlock().getLocation(),
						EntityType.PRIMED_TNT);
				tnt.setFuseTicks(Main.conf.getInt("primedTntExplode"));
				tnt.setYield(Main.conf.getInt("tntYield"));
			} else 
			if (Main.blocks.contains(e.getBlock().getType())) {
				final Block b= e.getBlock();
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.instance, new Runnable() {
		            @Override
		            public void run() {
		                b.setType(Material.AIR);
		            }
		        }, Main.conf.getLong("blockremove"));
			}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (Bukkit.getWorld("FFARush").equals(e.getEntity().getWorld()))
			if (e.getEntityType().equals(EntityType.PLAYER)) {
				if (e.getCause().equals(DamageCause.ENTITY_EXPLOSION)) {
					e.setDamage(Main.conf.getInt("tntDamage"));
				}
			}
	}

	@EventHandler
	public void onEntityExplodeEvent(EntityExplodeEvent e) {
		if (!Main.conf.getBoolean("tntDamage"))
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
						damaged.teleport((Location) Main.conf.get("lobbyloc"));
						damaged.setHealth(20);
						Bukkit.broadcastMessage(ChatColor.BLUE + damager.getDisplayName() + ChatColor.WHITE + " killed "
								+ damaged.getDisplayName());
						Inventory i = damaged.getInventory();
						i.clear();

						i.setItem(4, sword);

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
				if (p.getItemInHand().equals(sword)) {
					p.sendMessage(ChatColor.GREEN + "Joining the arena!");
					Main.arena.addPlayer(p);
				}
				if (p.getItemInHand().equals(leave)) {
					p.sendMessage(ChatColor.RED + "Leaving.");
					if (Main.conf.getBoolean("useBungee")) {
						// Untested
						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("Connect");
						out.writeUTF(Main.conf.getString("bungeeLobby"));
						p.sendPluginMessage(Main.instance, "BungeeCord", out.toByteArray());
					} else {
						p.performCommand(Main.conf.getString("leaveCmd"));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (e.getPlayer().isOp())
				Main.update.update((CommandSender)e.getPlayer());
	}
}
