package net.yocraft.ffarush;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static Main instance;
	public static final String version = "0.1";
	public static final int build = 1;
	public static final String website = "*WEBSITE*";
	public static FileConfiguration conf;
	public static Inventory inventory;
	public static MemorySection armor;
	public static Arena arena;
	public static List<Material> blocks = new ArrayList<Material>();

	@Override
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Starting FFARush v" + version + " by " + ChatColor.RED
				+ "Paulao17" + ChatColor.BLUE + ".");
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Check us out at " + website + ".");
		instance = this;
		this.getCommand("ffarush").setExecutor(new Commandffarush());

		getServer().createWorld(new WorldCreator("FFARush"));
		getServer().getPluginManager().registerEvents(new MyListener(), this);
		try {
			configProcedure();
		} catch (IOException e) {
			e.printStackTrace();
		}
		arena = new Arena();
		arena.load();

		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully started FFARush.");
	}

	@Override
	public void onDisable() {

	}

	public void configProcedure() throws IOException {
		if (!new File(getDataFolder(), "config.yml").exists()) {
			InputStream initialStream = getResource("config.yml");
			byte[] buffer = new byte[initialStream.available()];
			initialStream.read(buffer);
			OutputStream outStream = new FileOutputStream(new File(getDataFolder(), "config.yml"));
			outStream.write(buffer);
			outStream.close();
			initialStream.close();
		}
		conf = getConfig();
		if (!new File(getDataFolder(), "inventory.yml").exists()) {
			InputStream initialStream = getResource("inventory.yml");
			byte[] buffer = new byte[initialStream.available()];
			initialStream.read(buffer);
			OutputStream outStream = new FileOutputStream(new File(getDataFolder(), "inventory.yml"));
			outStream.write(buffer);
			outStream.close();
			initialStream.close();
		}
		YamlConfiguration inv = new YamlConfiguration();
		try {
			inv.load(new File(getDataFolder(), "inventory.yml"));
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		if (inv.contains("inv")) {
			inventory = Utils.fromBase64(inv.getString("inv"));
			armor = (MemorySection) inv.get("armor");

		}
		if (!new File(getDataFolder(), "Locations.yml").exists()) {
			InputStream initialStream = getResource("Locations.yml");
			byte[] buffer = new byte[initialStream.available()];
			initialStream.read(buffer);
			OutputStream outStream = new FileOutputStream(new File(getDataFolder(), "Locations.yml"));
			outStream.write(buffer);
			outStream.close();
			initialStream.close();
		}
		if (!new File(getDataFolder(), "Blocks.yml").exists()) {
			InputStream initialStream = getResource("Blocks.yml");
			byte[] buffer = new byte[initialStream.available()];
			initialStream.read(buffer);
			OutputStream outStream = new FileOutputStream(new File(getDataFolder(), "Blocks.yml"));
			outStream.write(buffer);
			outStream.close();
			initialStream.close();
		}
		YamlConfiguration block = new YamlConfiguration();
		try {
			block.load(new File(getDataFolder(), "Blocks.yml"));
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		List<String> l = (ArrayList<String>) block.get("blocks");
		for (String s : l) {
			blocks.add(Material.getMaterial(s));
		}
	}
}
