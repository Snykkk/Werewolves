package Snykkk.players;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SP implements Listener {
	
	public static HashMap<String, SPlayer> data = new HashMap<String, SPlayer>();
	
	public SP() {
		for (Player pi : Bukkit.getOnlinePlayers()) {
			
			SPlayer sp = new SPlayer(pi);
			
			sp.loadFromFile();
			
			data.put(pi.getUniqueId().toString(), sp);
		}
	}

	public static SPlayer get (Player p) {
		SPlayer sp = data.get(p.getUniqueId().toString());
		
		return sp;
	}
	
	public static SPlayer getOffline(String uid) {
		
		Player p = Bukkit.getPlayer(uid);
		
		if (p == null) {
			return new SPlayer().loadOffline(uid);
		}
		return get(p);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void loadData (PlayerJoinEvent e) {
		try {
			e.getPlayer().spigot().setCollidesWithEntities(false);
			
			SPlayer sp = new SPlayer(e.getPlayer());
			
			sp.loadFromFile();
			
			data.put(e.getPlayer().getUniqueId().toString(), sp);

		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
	
	@EventHandler
	public void saveData (PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		data.remove(p.getUniqueId().toString());
	}

}
