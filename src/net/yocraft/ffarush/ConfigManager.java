package net.yocraft.ffarush;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.ChatColor;

public class ConfigManager {
	public ArrayList<String> options = new ArrayList<String>();
	private ArrayList<String> files = new ArrayList<String>();
	private File dataFolder;

	public static FileConfiguration conf;
	private YamlConfiguration inv;
	public static Inventory inventory;
	public static MemorySection armor;
	public static YamlConfiguration block;
	public static List<Material> blocks = new ArrayList<Material>();
	
	public ConfigManager() {
		files.add("config.yml");
		files.add("Blocks.yml");
		files.add("inventory.yml");
		files.add("Locations.yml");
	}
	
	public void load(Main instance) {
		try {
			loadFiles(instance);
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed to load files");
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Failed. Invalid configuration.");
			e.printStackTrace();
		}
	}
	
	private void loadFiles(Main instance) throws IOException, InvalidConfigurationException {
		dataFolder = instance.getDataFolder();
		if (!dataFolder.exists()) dataFolder.mkdirs();
		for (String file : files) {
			File target = new File(dataFolder, file);
			if (!target.exists()) {
				InputStream initialStream = instance.getResource(file);
				byte[] buffer = new byte[initialStream.available()];
				initialStream.read(buffer);
				OutputStream outStream = new FileOutputStream(target);
				outStream.write(buffer);
				outStream.close();
				initialStream.close();
			}
		}
		conf = instance.getConfig();
		inv = new YamlConfiguration();
		block = new YamlConfiguration();
		inv.load(new File(dataFolder, "inventory.yml"));
		if (inv.contains("inv")) {
			inventory = Utils.fromBase64(inv.getString("inv"));
			armor = (MemorySection) inv.get("armor");
		}
		block.load(new File(dataFolder, "Blocks.yml"));
		@SuppressWarnings("unchecked")
		List<String> l = (ArrayList<String>) block.get("blocks");
		for (String s : l) {
			blocks.add(Material.getMaterial(s));
		}
	}
}
