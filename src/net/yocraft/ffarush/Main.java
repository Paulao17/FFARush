package net.yocraft.ffarush;

import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static Main instance;
	public static final String version = "1.1.0";
	public static final int build = 2;
	public static final String website = "http://yocraft.net/Plugins/FFARush.html";
	public static ConfigManager conf;
	public static Arena arena;
	public static boolean upToDate;
	public static Update update;

	@Override
	public void onEnable() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Starting FFARush v" + version + " by " + ChatColor.RED
				+ "Paulao17" + ChatColor.BLUE + ".");
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Check us out at " + website + ".");
		getServer().createWorld(new WorldCreator("FFARush"));
		instance = this;
		conf = new ConfigManager();
		conf.load(instance);
		this.getCommand("ffarush").setExecutor(new Commandffarush());
		MyListener list = new MyListener();
		list.load();
		getServer().getPluginManager().registerEvents(list, this);
		arena = new Arena();
		arena.load();
		update = new Update();
		try {
			update.load();
			update.update(Bukkit.getConsoleSender());
		} catch (IOException e) {
			e.printStackTrace();
		}

		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully started FFARush.");
	}

	@Override
	public void onDisable() {

	}
}
