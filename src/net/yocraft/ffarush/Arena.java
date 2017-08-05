package net.yocraft.ffarush;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

public class Arena {
	YamlConfiguration locs;
	public void load() {
		locs = new YamlConfiguration();
		try {
			locs.load(new File(Main.instance.getDataFolder(), "Locations.yml"));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public void addLocation(Location loc, String s) {
		locs.set("loc" + s, loc);
		try {
			locs.save(new File(Main.instance.getDataFolder(), "Locations.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addPlayer(Player p) {
		int randomNum = ThreadLocalRandom.current().nextInt(1, 16 + 1);
		String target = "loc" + randomNum;
		if (locs.contains(target)) {
			p.teleport((Location)locs.get(target));
			PlayerInventory pi = p.getInventory();
			Inventory targetinv = Main.inventory;
			pi.clear();
			pi.setContents(targetinv.getContents());
			MemorySection m = Main.armor;
			pi.setHelmet(m.getItemStack("h"));
			pi.setChestplate(m.getItemStack("c"));
			pi.setLeggings(m.getItemStack("l"));
			pi.setBoots(m.getItemStack("b"));
			
			p.updateInventory();
		} else addPlayer(p);
	}
}
