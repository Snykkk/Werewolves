package Snykkk.players;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import Snykkk.Game;

public class SPlayer {
	
	public String name = "";
	public String uuid = "";

	boolean offline = false;
	
	public FileConfiguration fc = null;

	public SPlayer (Player p) {
		name = p.getName();
		uuid = p.getUniqueId().toString();
	}
	
	public SPlayer() {}

	public SPlayer loadOffline(String uid) {
		if (SP.data.containsKey(uid)) {
			return SP.data.get(uid);
		}
		offline = true;
		uuid = uid;
		
		loadFromFile();
		
		return this;
	}

	
	public void refresh() {
		SP.data.put(uuid, this);
		//safeSave();	
	}
	
	public void loadFromFile() {
		File f = new File(Game.m.getDataFolder() + "/player", uuid + ".yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		
		if (f.exists()) {
			name = fc.getString("name", "");
			uuid = fc.getString("uuid", "");
			
			this.fc = fc;
		}
		else
		if (!f.exists()) {
			
			fc.set("name", name);
			fc.set("uuid", uuid);
			fc.set("sotran", 0);
			fc.set("thang", 0);
			fc.set("thua", 0);
			fc.set("inGame", false);
			try {fc.save(f);} catch (Exception ex) {}
			
			this.fc = fc;
		}
	}
	
	static long lastsave = 0;
	
	public void safeSave() {
		if (lastsave <= System.currentTimeMillis()) {
			saveToFile();			
		}
	}
	
	public void saveToFile() {
		

		File f = new File(Game.m.getDataFolder() + "/player", uuid + ".yml");
		try {
			lastsave = System.currentTimeMillis() + 10000;
			fc.set("savems", System.currentTimeMillis() + 10000);
			fc.save(f);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public int getGames() {
		return fc.getInt("sotran", 0);
	}
	
	public int getWins() {
		return fc.getInt("thang", 0);
	}
	public int getClose() {
		return fc.getInt("thua", 0);
	}
	
	public boolean getInGame() {
		return fc.getBoolean("inGame", false);
	}
}
