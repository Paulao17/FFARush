package net.yocraft.ffarush;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class Update {
	private int build;

	public void update(CommandSender sender) {
		if (!(build == Main.build)) {
			sender.sendMessage(ChatColor.RED + "[FFARush] You are not up to date.");
			sender.sendMessage(
					ChatColor.RED + "[FFARush] Update now at http://yocraft.net/Plugins/FFARush-Downloads.html");
		}
	}

	public void load() throws MalformedURLException, IOException {
		if (netIsAvailable()) {
			Scanner s = new Scanner(new URL("http://yocraft.net/versions/FFARush.txt").openStream(), "UTF-8");
			String out = s.useDelimiter("").next();
			s.close();
			build = Integer.parseInt(out);
		} else {
			build = Main.build;
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[FFARush] Tried to update but network seems to be unavailable.");
		}
	}

	private static boolean netIsAvailable() {
		try {
			final URL url = new URL("http://yocraft.net");
			final URLConnection conn = url.openConnection();
			conn.connect();
			return true;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			return false;
		}
	}
}
