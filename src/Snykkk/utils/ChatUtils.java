package Snykkk.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import Snykkk.Game;

public class ChatUtils {
	
	public static Game plugin;
	
	public static void bc(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage("§7[§e§lMA SÓI§7] " + msg.replaceAll("&", "§"));
		}
	}
	
	public static String color(String s) { return ChatColor.translateAlternateColorCodes('&', s); }

	public static void msg(Player p, String msg) {
		p.sendMessage(plugin.getConfig().getString("msg.prefix") + color(msg));
	}
	
}
