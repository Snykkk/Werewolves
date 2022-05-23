package Snykkk.arena;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;

import Snykkk.Game;
import Snykkk.GameListener;
import Snykkk.api.FSkullLib;
import Snykkk.api.FBar;
import Snykkk.api.FItem;
import Snykkk.api.FNum;
import Snykkk.api.FSkull;
import Snykkk.players.SP;
import Snykkk.players.SPlayer;
import Snykkk.utils.ChatUtils;
import Snykkk.utils.GameState;
import Snykkk.utils.Role;

public class ArenaManager {

    public HashMap<Integer, Integer> spawnsCounts = new HashMap<Integer, Integer>();
	public int spawnCount = 0;
    //make a new instance of the class
    public static ArenaManager am = new ArenaManager();
    //list of arenas
    public List<Arena> arenas = new ArrayList<Arena>();
    int arenaSize = 0;
    public static int required = 12;
    public int waiting = Game.m.getConfig().getInt("time.starting");
    public int endtime = Game.m.getConfig().getInt("time.ending");
    public int started = Game.m.getConfig().getInt("time.started");
    public int baovetime = Game.m.getConfig().getInt("time.baove");
    public int soitime = Game.m.getConfig().getInt("time.soi");
    public int tientritime = Game.m.getConfig().getInt("time.tientri");
    public int phuthuytime = Game.m.getConfig().getInt("time.phuthuy");
    public int thosantime = Game.m.getConfig().getInt("time.thosan");
    public int danlangtime = Game.m.getConfig().getInt("time.danlang");
    public int skiptime = Game.m.getConfig().getInt("time.skip");
    
    String prefix = Game.m.getConfig().getString("msg.prefix");
    FileConfiguration mfc = Game.m.getConfig();
    
    //role
    public HashMap<Player, Role> roles;
    public Role role;

    static Game plugin;
    public ArenaManager(Game arenaPVP) {
        plugin = arenaPVP;
    }

    public ArenaManager(){

    }

    //we want to get an instance of the manager to work with it statically
    public static ArenaManager getManager(){
        return am;
    }

    //get an Arena object from the list
    public Arena getArena(int i){
        for(Arena a : arenas){
            if(a.getId() == i){
                return a;
            }
        }
        return null;
    }
    
    //add players to the arena, save their inventory
    public void addPlayer(Player p, int i){
        Arena a = getArena(i);//get the arena you want to join
        if(a == null){//make sure it is not null
            p.sendMessage(ChatUtils.color(prefix + mfc.getString("msg.not-exist").replaceAll("%ARENA%", i + "")));
            return;
        }
        if(a.getPlayers().size() >= required) {
        	p.sendMessage(ChatUtils.color(prefix + mfc.getString("msg.full")));
        }
        else if(a.getState() == GameState.INGAME || a.getState() == GameState.ENDING || a.getState() == GameState.NIGHT || a.getState() == GameState.DAY) {
			addSpec(p);
			p.sendMessage(ChatUtils.color(prefix + mfc.getString("msg.ingame")));
		}
        else {
        	Collections.shuffle(a.spawnsList);
        	
        	GameListener.lobby.remove(p);
	        a.getPlayers().add(p.getName());
	        a.getAlives().add(p.getName());
	        
	        for(int l=0; l < a.spawnsList.size(); l ++) {
		        if(!a.locplayers.containsValue(a.spawnsList.get(l))) {
		        	a.locplayers.put(p.getName(), a.spawnsList.get(l));
		        }
	        }
	        
	        for (String pi : a.getPlayers()) {
	        	Player player = Bukkit.getPlayer(pi);
	        	player.sendMessage(ChatUtils.color(prefix + Game.m.getConfig().getString("msg.waiting.pjoin").replaceAll("%player%", p.getName()).replaceAll("%room%", a.getPlayers().size() + "")));
			}
	
	        p.getInventory().setArmorContents(null);
	        p.getInventory().clear();
	        p.setFoodLevel(20);
	        p.setHealth(20);
	        p.setGameMode(GameMode.ADVENTURE);
	        
	        p.getInventory().setItem(8, new FItem(Material.COMPASS).setName("§c§lRỜI PHÒNG")
					.addLore("")
					.toItemStack());
	        
	        plugin.gameScoreboard.scoreLobby(p, 30);
	
	        p.teleport(a.spawn);//teleport to the arena spawn
	        
	        if(a.getPlayers().size() == required && (a.getState() == GameState.WAITING || a.getState() == GameState.STARTING)) {
	        	waiting(i);
	        	for (String name : a.getPlayers()) {
					Player pi = Bukkit.getPlayer(name);
					pi.sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.cooldown").replaceAll("%sec%", "10")));
					pi.playSound(pi.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 2, 2);
				}
	        }
        }
    }

    //remove players
    public void removePlayer(Player p){
        Arena a = null;//make an arena
        for(Arena arena : arenas){
            if(arena.getPlayers().contains(p.getName())){
                a = arena;//if the arena has the player, the arena field would be the arena containing the player
            }
            //if none is found, the arena will be null
        }
        if(a == null){//make sure it is not null
            p.sendMessage(ChatUtils.color(prefix + Game.m.getConfig().getString("msg.notingame")));
            return;
        }
        if(a.getState() == GameState.INGAME || a.getState() == GameState.ENDING) {	
        	a.getSpec().remove(p.getName());
        }
        GameListener.lobby.add(p);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        
        String ss = Game.m.getConfig().getString("Lobby", "");

        World w = Bukkit.getWorld(ss.split(" ")[0]);
		
		double x = FNum.rd(ss.split(" ")[1]);
		double y = FNum.rd(ss.split(" ")[2]);
		double z = FNum.rd(ss.split(" ")[3]);
		double yaw = FNum.rd(ss.split(" ")[4]);
		double pitch = FNum.rd(ss.split(" ")[5]);
		
		Location loc = new Location(w, x, y, z);
		loc.setYaw((float) yaw);
		loc.setPitch((float) pitch);
		
        p.teleport(loc);
        a.locplayers.remove(p.getName());

        new BukkitRunnable() {
    		@Override
            public void run() {
    			p.removePotionEffect(PotionEffectType.BLINDNESS);
    			p.removePotionEffect(PotionEffectType.INVISIBILITY);
    			p.getActivePotionEffects().clear();
    		}
    	}.runTaskLater(plugin, 10);
    	
        if(a.getSpec().contains(p.getName())) {
        	a.getSpec().remove(p.getName());
            a.getPlayers().remove(p.getName());
            a.getAlives().remove(p.getName());
            if(a.getPheDan().contains(p.getName())) {
            	a.getPheDan().remove(p.getName());
            } else { 
            	a.getPheSoi().remove(p.getName());
            }
        }
        else if(a.getBaove().contains(p.getName())) {
        	a.getBaove().remove(p.getName());
            a.getPlayers().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getPheDan().remove(p.getName());
        }
        else if(a.getSoi().contains(p.getName())) {
        	a.getSoi().remove(p.getName());
            a.getPlayers().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getPheSoi().remove(p.getName());
        }
        else if(a.getTientri().contains(p.getName())) {
        	a.getTientri().remove(p.getName());
            a.getPlayers().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getPheDan().remove(p.getName());
        }
        else if(a.getPhuthuy().contains(p.getName())) {
        	a.getPhuthuy().remove(p.getName());
            a.getPlayers().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getPheDan().remove(p.getName());
        }
        else if(a.getDanlang().contains(p.getName())) {
        	a.getDanlang().remove(p.getName());
            a.getPlayers().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getPheDan().remove(p.getName());
        } else if(a.getTihi().contains(p.getName())) {
        	a.getTihi().remove(p.getName());
            a.getPlayers().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getPheDan().remove(p.getName());
        } else if (a.getThosan().contains(p.getName())) {
        	a.getThosan().remove(p.getName());
            a.getPlayers().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getPheDan().remove(p.getName());
    	}else {
            a.getPlayers().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getPheDan().remove(p.getName());
        }
        p.setFireTicks(0);
        p.setGameMode(GameMode.ADVENTURE);
        p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        if(a.getState() == GameState.WAITING || a.getState() == GameState.STARTING) {
        	p.sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.prefix") + Game.m.getConfig().getString("msg.leave")));
        }
        for (String pi : a.getPlayers()) {
        	Player player = Bukkit.getPlayer(pi);
        	player.sendMessage(ChatUtils.color(prefix + Game.m.getConfig().getString("msg.waiting.pleft").replaceAll("%player%", p.getName()).replaceAll("%room%", a.getPlayers().size() + "")));
		}
    }
    
    public void addSpec(Player p) {
    	Arena a = null;//make an arena
        for(Arena arena : arenas){
            if(arena.getPlayers().contains(p.getName())){
                a = arena;//if the arena has the player, the arena field would be the arena containing the player
            }
            //if none is found, the arena will be null
        }
        if(a == null || !a.getPlayers().contains(p.getName())){//make sure it is not null
            p.sendMessage("Invalid operation!");
            return;
        }
        if(a.getBaove().contains(p.getName())) {
        	a.getBaove().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getSpec().add(p.getName());
        }
        else if(a.getSoi().contains(p.getName())) {
        	a.getSoi().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getSpec().add(p.getName());
        }
        else if(a.getTientri().contains(p.getName())) {
        	a.getTientri().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getSpec().add(p.getName());
        }
        else if(a.getPhuthuy().contains(p.getName())) {
        	a.getPhuthuy().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getSpec().add(p.getName());
        }
        else if(a.getThosan().contains(p.getName())) {
        	a.getThosan().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getSpec().add(p.getName());
        }
        else if(a.getTihi().contains(p.getName())) {
        	a.getTihi().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getSpec().add(p.getName());
        }
        else if(a.getDanlang().contains(p.getName())) {
        	a.getDanlang().remove(p.getName());
            a.getAlives().remove(p.getName());
            a.getSpec().add(p.getName());
        } else {
            a.getAlives().remove(p.getName());
            a.getSpec().add(p.getName());
        }
        new BukkitRunnable() {
    		@Override
            public void run() {
    			p.removePotionEffect(PotionEffectType.BLINDNESS);
    			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 2));
    		}
    	}.runTaskLater(plugin, 10);
		p.teleport(a.spawn);
        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.getInventory().setItem(8, new FItem(Material.COMPASS).setName("§c§lRỜI PHÒNG")
				.addLore("")
				.toItemStack());
    	
    }

    public void waiting(int i) {
    	Arena a = getArena(i);//get the arena you want to join
        if(a == null){//make sure it is not null
            return;
        }
        a.setState(GameState.STARTING);
    	new BukkitRunnable() {
    		@Override
    		public void run() {
    			if(waiting > 0) {
    				
    				if(a.getPlayers().size() == required) {
    					waiting = waiting - 1;
    					for (String name : a.getPlayers()) {
							Player p = Bukkit.getPlayer(name);
	    					p.sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.cooldown").replaceAll("%sec%", waiting + "")));
    						p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 2, 2);
	    				}
    					a.getPlayers().forEach(name -> plugin.gameScoreboard.scoreLobby(Bukkit.getPlayer(name), waiting));
    				} else {
    					a.setState(GameState.WAITING);
    					for (String name : a.getPlayers()) {
    						Player p = Bukkit.getPlayer(name);
	    					p.sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.stoping")));
    					}
    					waiting = 10;
    					this.cancel();
    					
    				}
    			} else {
    				this.cancel();
    				gameStart(i);
    				started(i);
    			}
    		}
    	}.runTaskTimer(plugin, 0, 20l);
    }
    
    public void gameStart(int i) {
    	Arena a = getArena(i);
        if(a == null){
            return;
        }
        String ss = Game.m.getConfig().getString("Arenas." + i + ".Lobby", "");
        
        World w = Bukkit.getWorld(ss.split(",")[0]);
        w.setTime(1000);
        
        Collections.shuffle(a.getPlayers());
        
	    for (String name : a.getPlayers()) {
			Player p = Bukkit.getPlayer(name);
	        new BukkitRunnable() {
	    		@Override
	            public void run() {
	    			p.getInventory().clear();
	    			p.removePotionEffect(PotionEffectType.BLINDNESS);
	    			p.removePotionEffect(PotionEffectType.INVISIBILITY);
	    			p.getActivePotionEffects().clear();
	    		}
	    	}.runTaskLater(plugin, 10);
			p.sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.started")));
			
			if(a.locplayers.containsKey(p.getName())) {
				p.teleport(a.locplayers.get(p.getName()));
			}
			
			if(a.getBaove().size() < 1) {
				a.getBaove().add(name);
				a.getPheDan().add(name);
				FBar.sendTitleBar(p, "&fBạn là", ChatUtils.color("&b&lBẢO VỆ"));
			}
			else if(a.getSoi().size() < 4) {
				a.getSoi().add(name);
				a.getPheSoi().add(name);
				FBar.sendTitleBar(p, "&fBạn là", ChatUtils.color("&c&lSÓI"));
			}
//			else if(a.getTihi().size() < 1) {
//				a.getTihi().add(name);
//				a.getPheDan().add(name);
//				FBar.sendTitleBar(p, "&fBạn là", ChatUtils.color("&8&lTI HÍ"));
//			}
			else if(a.getThosan().size() < 1) {
				a.getThosan().add(name);
				a.getPheDan().add(name);
				FBar.sendTitleBar(p, "&fBạn là", ChatUtils.color("&6&lTHỢ SĂN"));
			}
			else if(a.getTientri().size() < 1) {
				a.getTientri().add(name);
				a.getPheDan().add(name);
				FBar.sendTitleBar(p, "&fBạn là", ChatUtils.color("&5&lTIÊN TRI"));
			}
			else if(a.getPhuthuy().size() < 1) {
				a.getPhuthuy().add(name);
				a.getPheDan().add(name);
				FBar.sendTitleBar(p, "&fBạn là", ChatUtils.color("&a&lPHÙ THỦY"));
			}
			else if(a.getDanlang().size() < 4) {
				a.getDanlang().add(name);
				a.getPheDan().add(name);
				FBar.sendTitleBar(p, "&fBạn là", ChatUtils.color("&2&lDÂN LÀNG"));
			}
		}
        
    }
    
    public void started(int i) {
    	Arena a = getArena(i);
        if(a == null){
            return;
        }
        a.getPlayers().forEach(name -> plugin.gameScoreboard.scoreGame(Bukkit.getPlayer(name)));
        a.locplayers.clear();
        a.setState(GameState.DAY);
    	new BukkitRunnable() {
    		@Override
    		public void run() {
    			if(started > 0) {    				
    				started = started - 1;
    				for(String name : a.getPlayers()) {
    					Player p = Bukkit.getPlayer(name);
    					FBar.sendActionBar(p, ChatUtils.color(Game.m.getConfig().getString("msg.time.night").replaceAll("%sec%", started + "")));
    				}
    			} else {
    				bvTime(i);
    				started = Game.m.getConfig().getInt("time.started");
    				this.cancel();
    			}
    		}
    	}.runTaskTimer(plugin, 0, 20l);
    }
    
    public void bvTime(int i) {
    	baovetime = Game.m.getConfig().getInt("time.baove");
    	Arena a = getArena(i);
        if(a == null){
            return;
        }
        String ss = Game.m.getConfig().getString("Arenas." + i + ".Lobby", "");
        World w = Bukkit.getWorld(ss.split(",")[0]);
        w.setTime(20000);
        a.setState(GameState.NIGHT);
        a.setRole(Role.BAOVE);
        for(String name : a.getPlayers()) {
        	Player p = Bukkit.getPlayer(name);
        	p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_LAND, 2, 2);
        }
        for(String name : a.getAlives()) {
        	Player p = Bukkit.getPlayer(name);
        	p.getInventory().clear();
        	new BukkitRunnable() {
        		@Override
                public void run() {
        			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 2));
        		}
        	}.runTaskLater(plugin, 10);
        	if(a.getBaove().contains(name)) {
        		FBar.sendTitleBar(Bukkit.getPlayer(name), "", ChatUtils.color("&a&lLượt của bạn"));
        		new BukkitRunnable() {
            		@Override
                    public void run() {
            			p.getActivePotionEffects().clear();
            			p.removePotionEffect(PotionEffectType.BLINDNESS);
            		}
            	}.runTaskLater(plugin, 10);
            	p.getInventory().setItem(0, new FItem(FSkull.byName(p.getName())).setName("§e§lCHỌN NGƯỜI CHƠI")
        				.addLore("")
        				.addLore("§aChuột trái để chọn")
        				.addLore("")
        				.toItemStack());
        	}
        }
        //check xem win hay chua
        if(checkWins(i) == true) {
        	gameStop(i);
        } else {
        	new BukkitRunnable() {
        		@Override
        		public void run() {
        			if(baovetime > 0) {
        				if(a.getBaove().isEmpty()) {
        					soiTime(i);
        					this.cancel();
        				} else {
	        				baovetime = baovetime - 1;
	        				for(String name : a.getPlayers()) {
	        					Player p = Bukkit.getPlayer(name);
	        					FBar.sendActionBar(p, ChatUtils.color(Game.m.getConfig().getString("msg.time.baove").replaceAll("%sec%", baovetime + "")));
	        				}
        				}
        			} else {
        				soiTime(i);
        				baovetime = Game.m.getConfig().getInt("time.baove");
        				this.cancel();
        			}
        		}
        	}.runTaskTimer(plugin, 0, 20l);	
        }
    }
    
    public void soiTime(int i) {
    	Arena a = getArena(i);
        if(a == null){
            return;
        }
        a.setRole(Role.SOI);
        for(String name : a.getPlayers()) {
        	Player p = Bukkit.getPlayer(name);
        	p.playSound(p.getLocation(), Sound.ENTITY_WOLF_HOWL, 2, 2);
        }
        for(String name : a.getAlives()) {
        	Player p = Bukkit.getPlayer(name);
        	p.getInventory().clear();
        	new BukkitRunnable() {
        		@Override
                public void run() {
        			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 2));
        		}
        	}.runTaskLater(plugin, 10);
        	if(a.getSoi().contains(name)) {
        		FBar.sendTitleBar(Bukkit.getPlayer(name), "", ChatUtils.color("&a&lLượt của bạn"));
        		new BukkitRunnable() {
            		@Override
                    public void run() {
            			p.getActivePotionEffects().clear();
            			p.removePotionEffect(PotionEffectType.BLINDNESS);
                		p.getInventory().setHelmet(new ItemStack(FSkullLib.wolf()));
            		}
            	}.runTaskLater(plugin, 10);
            	p.getInventory().setItem(0, new FItem(FSkull.byName(p.getName())).setName("§e§lCHỌN NGƯỜI CHƠI")
        				.addLore("")
        				.addLore("§aChuột trái để chọn")
        				.addLore("")
        				.toItemStack());
        	}
        	if(a.getTihi().contains(name)) {
        		p.getInventory().setHelmet(new ItemStack(Material.PUMPKIN, 64));
        		p.getInventory().addItem(new ItemStack(Material.PUMPKIN, 2304));
        		FBar.sendTitleBar(Bukkit.getPlayer(name), "", ChatUtils.color("&a&lBạn có thể nhìn thấy sói săn"));
        		new BukkitRunnable() {
            		@Override
                    public void run() {
            			p.getActivePotionEffects().clear();
            			p.removePotionEffect(PotionEffectType.BLINDNESS);
            		}
            	}.runTaskLater(plugin, 10);
        	}
        }
        //check xem win hay chua
        if(checkWins(i) == true) {
        	gameStop(i);
        } else {
        	new BukkitRunnable() {
        		@Override
        		public void run() {
        			if(soitime > 0) {
        				if(a.getSoi().isEmpty()) {
        					checkWins(i);
        					new BukkitRunnable() {
        						@Override
        	                    public void run() {
        							gameStop(i);
        						}
        					}.runTaskLater(plugin, 40);
        					this.cancel();
        				} else {
	        				soitime = soitime - 1;
	        				for(String name : a.getPlayers()) {
	        					Player p = Bukkit.getPlayer(name);
	        					FBar.sendActionBar(p, ChatUtils.color(Game.m.getConfig().getString("msg.time.soi").replaceAll("%sec%", soitime + "")));
	        				}
        				}
        			} else {
        				if(a.getTientri().isEmpty()) {
        					ptTime(i);
        				} else {
        					ttTime(i);
        				}
        				soitime = Game.m.getConfig().getInt("time.soi");
        				this.cancel();
        			}
        		}
        	}.runTaskTimer(plugin, 0, 20l);
        }
        
    }
    
    public void ttTime(int i) {
    	tientritime = Game.m.getConfig().getInt("time.tientri");
    	Arena a = getArena(i);
        if(a == null){
            return;
        }
        a.setRole(Role.TIENTRI);
        for(String name : a.getPlayers()) {
        	Player p = Bukkit.getPlayer(name);
        	p.playSound(p.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, 2, 2);
        }
        for(String name : a.getAlives()) {
        	Player p = Bukkit.getPlayer(name);
        	p.getInventory().clear();
        	new BukkitRunnable() {
        		@Override
                public void run() {
        			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 2));
        		}
        	}.runTaskLater(plugin, 10);
        	if(a.getTientri().contains(name)) {
        		FBar.sendTitleBar(Bukkit.getPlayer(name), "", ChatUtils.color("&a&lLượt của bạn"));
        		new BukkitRunnable() {
            		@Override
                    public void run() {
            			p.getActivePotionEffects().clear();
            			p.removePotionEffect(PotionEffectType.BLINDNESS);
            		}
            	}.runTaskLater(plugin, 10);
            	p.getInventory().setItem(0, new FItem(FSkull.byName(p.getName())).setName("§e§lCHỌN NGƯỜI CHƠI")
        				.addLore("")
        				.addLore("§aChuột trái để chọn")
        				.addLore("")
        				.toItemStack());
        	}
        }
        //check xem win hay chua
        if(checkWins(i) == true) {
        	gameStop(i);
        } else {
        	new BukkitRunnable() {
        		@Override
        		public void run() {
        			if(tientritime > 0) {    	
        				if(a.getTientri().isEmpty()) {
        					ptTime(i);
        					this.cancel();
        				} else {
	        				tientritime = tientritime - 1;
	        				for(String name : a.getPlayers()) {
	        					Player p = Bukkit.getPlayer(name);
	        					FBar.sendActionBar(p, ChatUtils.color(Game.m.getConfig().getString("msg.time.tientri").replaceAll("%sec%", tientritime + "")));
	        				}
        				}
        			} else {
        				if(a.getPhuthuy().isEmpty()) {
        					timetoDay(i);
        				} else ptTime(i);
        				tientritime = Game.m.getConfig().getInt("time.tientri");
        				this.cancel();
        			}
        		}
        	}.runTaskTimer(plugin, 0, 20l);
        }
        
    }
    
    public void ptTime(int i) {
    	phuthuytime = Game.m.getConfig().getInt("time.phuthuy");
    	Arena a = getArena(i);
        if(a == null){
            return;
        }
        a.setRole(Role.PHUTHUY);
        for(String name : a.getPlayers()) {
        	Player p = Bukkit.getPlayer(name);
        	p.playSound(p.getLocation(), Sound.ENTITY_PARROT_IMITATE_WITCH, 2, 2);
        }
        for(String name : a.getAlives()) {
        	Player p = Bukkit.getPlayer(name);
        	p.getInventory().clear();
        	for(String phuthuy : a.getPhuthuy()) {
        		if(a.victimsoi.containsKey(name)) {
        			if(a.victimsoi.get(name) >= a.getSoi().size()) {
        				Bukkit.getPlayer(phuthuy).sendMessage(ChatUtils.color("&e" + name + " &flà mục tiêu của &cSói&f!"));
        			}
        		}
        	}
        	new BukkitRunnable() {
        		@Override
                public void run() {
        			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 2));
        		}
        	}.runTaskLater(plugin, 10);
        	if(a.getPhuthuy().contains(name)) {
        		FBar.sendTitleBar(Bukkit.getPlayer(name), "", ChatUtils.color("&a&lLượt của bạn"));
        		new BukkitRunnable() {
            		@Override
                    public void run() {
            			p.getActivePotionEffects().clear();
            			p.removePotionEffect(PotionEffectType.BLINDNESS);
            		}
            	}.runTaskLater(plugin, 10);
            	if(!a.hasCuu.isEmpty() && !a.hasGiet.isEmpty()) {
            		FBar.sendTitleBar(p, "", ChatUtils.color("&fBạn còn 0 bình cứu và 0 bình giết"));
            	} else {
            		if(!a.hasCuu.isEmpty() && a.hasGiet.isEmpty()) {
                		FBar.sendTitleBar(p, "", ChatUtils.color("&fBạn còn 0 bình cứu và 1 bình giết"));
            		} else if (a.hasCuu.isEmpty() && !a.hasGiet.isEmpty()){
                		FBar.sendTitleBar(p, "", ChatUtils.color("&fBạn còn 1 bình cứu và 0 bình giết"));
            		} else if (a.hasCuu.isEmpty() && a.hasGiet.isEmpty()) {
                		FBar.sendTitleBar(p, "", ChatUtils.color("&fBạn còn 1 bình cứu và 1 bình giết"));
            		}
            		p.getInventory().setItem(0, new FItem(FSkull.byName(p.getName())).setName("§e§lCHỌN NGƯỜI CHƠI")
        				.addLore("")
        				.addLore("§aChuột trái để chọn")
        				.addLore("")
        				.toItemStack());
            	}
        	}
        }

        //check xem win hay chua
        if(checkWins(i) == true) {
        	gameStop(i);
        } else {
            new BukkitRunnable() {
        		@Override
        		public void run() {
        			if(phuthuytime > 0) {
        				if(a.getPhuthuy().isEmpty()) {
        					timetoDay(i);
        					this.cancel();
        				} else {
        					phuthuytime = phuthuytime - 1;
	        				for(String name : a.getPlayers()) {
	        					Player p = Bukkit.getPlayer(name);
	        					FBar.sendActionBar(p, ChatUtils.color(Game.m.getConfig().getString("msg.time.phuthuy").replaceAll("%sec%", phuthuytime + "")));
	        				}
        				}
        			} else {
        				timetoDay(i);
        				phuthuytime = Game.m.getConfig().getInt("time.phuthuy");
        				this.cancel();
        			}
        		}
        	}.runTaskTimer(plugin, 0, 20l);	
        }
    }
    
    public void timetoDay(int i) {
    	Arena a = getArena(i);
        if(a == null){
            return;
        }
        a.setRole(Role.UNKNOWN);
        String ss = Game.m.getConfig().getString("Arenas." + i + ".Lobby", "");
        World w = Bukkit.getWorld(ss.split(",")[0]);
        w.setTime(1000);
        for(String name : a.getPlayers()) {
        	Player p = Bukkit.getPlayer(name);
        	p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 2, 2);
        }
        
        for(String name : a.getAlives()) {
        	Player p = Bukkit.getPlayer(name);
        	p.getInventory().clear();
        	new BukkitRunnable() {
        		@Override
                public void run() {
        			p.getActivePotionEffects().clear();
        			p.removePotionEffect(PotionEffectType.BLINDNESS);
        		}
        	}.runTaskLater(plugin, 10);
        }
        //check xem win hay chua
        if(checkWins(i) == true) {
        	gameStop(i);
        } else {
        	new BukkitRunnable() {
        		@Override
        		public void run() {
        			if(skiptime > 0) {
        				skiptime = skiptime - 1;
        				for(String name : a.getPlayers()) {
        					Player p = Bukkit.getPlayer(name);
        					FBar.sendActionBar(p, ChatUtils.color(Game.m.getConfig().getString("msg.time.today").replaceAll("%sec%", skiptime + "")));
        				}
        			} else {
        				for(String name : a.getAlives()) {
        					String role = a.getBaove().contains(name) ? "Bảo vệ" : 
        						a.getTientri().contains(name) ? "Tiên tri" : 
        							a.getPhuthuy().contains(name) ? "Phù thủy" : 
        								a.getDanlang().contains(name) ? "Dân làng" : 
        									a.getTihi().contains(name) ? "Ti hí" :
        										a.getThosan().contains(name) ? "Thợ săn" :
        											a.getSoi().contains(name) ? "Sói" : "Unknown";
        					if(a.victimsoi.containsKey(name) && (a.victimsoi.get(name) > (a.getSoi().size()/2))) {
    				        	if(a.victimbaove.contains(name)) {
    				        		for(String all : a.getPlayers()) {
    				        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.protection").replaceAll("%player%", name)));
    				        		}
    				        	} else if(a.victimphuthuycuu.contains(name)){
    				        		for(String all : a.getPlayers()) {
    				        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.saved").replaceAll("%player%", name)));
    				        		}
    				        	} else {
    					        	if(a.getThosan().contains(name)) {
    					        		if(a.victimphuthuycuu.contains(name)) {
    					        			for(String all : a.getPlayers()) {
    						        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.saved").replaceAll("%player%", name)));
    						        		}
    					        		}
    					        		else if (a.victimbaove.contains(name)) {
    					        			for(String all : a.getPlayers()) {
    						        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.protection").replaceAll("%player%", name)));
    						        		}
    					        		} else {
    					        			a.thosanChet.add(name);
    						        		for(String all : a.getPlayers()) {
    						        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color("&6Thợ săn &fsẽ nhắm bắn một người trước khi chết!"));
    						        		}
    					        		}
    					        	} else {
    				        			for(String all : a.getPlayers()) {
    					        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.death").replaceAll("%player%", name).replaceAll("%role%", role)));
    					        		}
    					        		addSpec(Bukkit.getPlayer(name));
    					        	}
    				        	}
        			    	}
        			        if(a.victimphuthuygiet.contains(name) && !a.victimsoi.containsKey(name)) {
        			        	if(a.victimbaove.contains(name)) {
        			        		for(String all : a.getPlayers()) {
        			        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.protection").replaceAll("%player%", name)));
        			        		}
        			        	} else {
        			        		if(a.getThosan().contains(name) && !a.victimbaove.contains(name)) {
        			        			if(a.victimbaove.contains(name)) {
    					        			for(String all : a.getPlayers()) {
    						        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.protection").replaceAll("%player%", name)));
    						        		}
    					        		} else {
    					        			a.thosanChet.add(name);
    						        		for(String all : a.getPlayers()) {
    						        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color("&6Thợ săn &fsẽ nhắm bắn một người trước khi chết!"));
    						        		}
    					        		}
        				        	} else {
        				        		for(String all : a.getPlayers()) {
        				        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.death").replaceAll("%player%", name).replaceAll("%role%", role)));
        				        		}
        					    		addSpec(Bukkit.getPlayer(name));
        				        	}
        			        	}
        			        }
        				}
        				new BukkitRunnable() {
        	        		@Override
        	                public void run() {
        	        			if(a.thosanChet.isEmpty()) {
        	        				danlangTime(i);
        	        			} else {
        	        				checkThosan(i);
        	        			}
        	        		}
        	        	}.runTaskLater(plugin, 40);
        	            a.setState(GameState.DAY);
        	        	skiptime = Game.m.getConfig().getInt("time.skip");
        				this.cancel();
        			}
        		}
        	}.runTaskTimer(plugin, 0, 20l);
        }
    }
    
    public void checkThosan(int i) {
    	Arena a = getArena(i);
        if(a == null){
            return;
        }
        a.setRole(Role.THOSAN);
        for(String name : a.getPlayers()) {
        	Player p = Bukkit.getPlayer(name);
        	p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 2, 2);
        }
        for(String name : a.getAlives()) {
        	Player p = Bukkit.getPlayer(name);
        	if(a.getThosan().contains(name)) {
        		FBar.sendTitleBar(Bukkit.getPlayer(name), "", ChatUtils.color("&a&lBắn 1 người trước khi chết"));
        		new BukkitRunnable() {
            		@Override
                    public void run() {
            			p.getActivePotionEffects().clear();
            			p.removePotionEffect(PotionEffectType.BLINDNESS);
            		}
            	}.runTaskLater(plugin, 10);
            	p.getInventory().setItem(0, new FItem(FSkull.byName(p.getName())).setName("§e§lCHỌN NGƯỜI CHƠI")
        				.addLore("")
        				.addLore("§aChuột trái để chọn")
        				.addLore("")
        				.toItemStack());
        	}
        }
        if(checkWins(i) == true) {
        	gameStop(i);
        } else {
            new BukkitRunnable() {
        		@Override
        		public void run() {
        			if(thosantime > 0) {
        				if(a.thosanChon.isEmpty()) {
	        				thosantime = thosantime - 1;
	        				for(String all : a.getPlayers()) {
	        					FBar.sendActionBar(Bukkit.getPlayer(all), ChatUtils.color(Game.m.getConfig().getString("msg.time.thosan").replaceAll("%sec%", thosantime + "")));
	        				}
        				} else {
        					a.thosanChet.clear();
        					danlangTime(i);
            				thosantime = Game.m.getConfig().getInt("time.thosan");
            				this.cancel();
        				}
        			} else {
        				for(String name : a.getAlives()) {
        					String role = a.getBaove().contains(name) ? "Bảo vệ" : 
        						a.getTientri().contains(name) ? "Tiên tri" : 
        							a.getPhuthuy().contains(name) ? "Phù thủy" : 
        								a.getDanlang().contains(name) ? "Dân làng" : 
        									a.getTihi().contains(name) ? "Ti hí" :
        										a.getThosan().contains(name) ? "Thợ săn" :
        											a.getSoi().contains(name) ? "Sói" : "Unknown";
        					if(a.getThosan().contains(name)) {
	        					for(String all : a.getPlayers()) {
				        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.death").replaceAll("%player%", name).replaceAll("%role%", role)));
				        		}
        						addSpec(Bukkit.getPlayer(name));
        					}
        					
        				}
        				new BukkitRunnable() {
        	        		@Override
        	                public void run() {
            					a.thosanChet.clear();
                				danlangTime(i);
        	        		}
        				}.runTaskLater(plugin, 20);

        				thosantime = Game.m.getConfig().getInt("time.thosan");
        				this.cancel();
        			}
        		}
        	}.runTaskTimer(plugin, 0, 20l);	
        }
    }
    
    public void danlangTime(int i) {
    	Arena a = getArena(i);
        if(a == null){
            return;
        }
        a.setRole(Role.DANLANG);
        a.getPlayers().forEach(name -> plugin.gameScoreboard.scoreGame(Bukkit.getPlayer(name)));
        for(String name : a.getPlayers()) {
        	Player p = Bukkit.getPlayer(name);
        	p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 2, 2);
        }
        for(String name : a.getAlives()) {
        	FBar.sendTitleBar(Bukkit.getPlayer(name), "", ChatUtils.color("&a&lSáng rồi dậy thôi!"));
        	Player p = Bukkit.getPlayer(name);
        	p.getInventory().setItem(0, new FItem(FSkull.byName(p.getName())).setName("§e§lCHỌN NGƯỜI CHƠI")
    				.addLore("")
    				.addLore("§aChuột trái để chọn")
    				.addLore("")
    				.toItemStack());
        }
        //check xem win hay chua
        if(checkWins(i) == true) {
        	gameStop(i);
        } else {
        	new BukkitRunnable() {
        		@Override
        		public void run() {
        			if(danlangtime > 0) {
        				if(a.getDanlang().isEmpty() && a.getBaove().isEmpty() && a.getTientri().isEmpty() && a.getPhuthuy().isEmpty() && a.getTihi().isEmpty() && a.getThosan().isEmpty()) {
        					checkWins(i);
        					new BukkitRunnable() {
        						@Override
        	                    public void run() {
        							gameStop(i);
        						}
        					}.runTaskLater(plugin, 40);
        					this.cancel();
        				} else {
	        				danlangtime = danlangtime - 1;
	        				for(String name : a.getPlayers()) {
	        					Player p = Bukkit.getPlayer(name);
	        					FBar.sendActionBar(p, ChatUtils.color(Game.m.getConfig().getString("msg.time.danlang").replaceAll("%sec%", danlangtime + "")));
	        				}
        				}
        			} else {
        				if(!a.victimdan.isEmpty()) {
        					voteDanlang(i);
        				} else skipTime(i);
    		        	danlangtime = Game.m.getConfig().getInt("time.danlang");
    		        	this.cancel();
        			}
        		}
        	}.runTaskTimer(plugin, 0, 20l);
        }
      
    }
    
    public void voteDanlang(int i) {
    	Arena a = getArena(i);
        if(a == null){
            return;
        }
        a.setRole(Role.UNKNOWN);
        for(String name : a.getAlives()) {
        	FBar.sendTitleBar(Bukkit.getPlayer(name), "", ChatUtils.color("&a&lKiểm tra phiếu bầu ..."));
        	Bukkit.getPlayer(name).getInventory().clear();
		}
        //check xem win hay chua
        if(checkWins(i) == true) {
        	gameStop(i);
        } else {
        	new BukkitRunnable() {
         		@Override
         		public void run() {
         			if(skiptime > 0) {
         				skiptime = skiptime - 1;
         				for(String name : a.getPlayers()) {
         					Player p = Bukkit.getPlayer(name);
         					FBar.sendActionBar(p, ChatUtils.color(Game.m.getConfig().getString("msg.time.chotphieu").replaceAll("%sec%", skiptime + "")));
         				}
         			} else {
         				for(String name : a.getAlives()) {
	         				if(a.victimdan.containsKey(name) && (a.victimdan.get(name) > (a.getAlives().size()/2))) {
         		        		String role = a.getBaove().contains(name) ? "Bảo vệ" : 
			         							a.getTientri().contains(name) ? "Tiên tri" : 
			         								a.getPhuthuy().contains(name) ? "Phù thủy" : 
			         									a.getDanlang().contains(name) ? "Dân làng" : 
			         										a.getTihi().contains(name) ? "Ti hí" :
			         											a.getThosan().contains(name) ? "Thợ săn" :
			         												a.getSoi().contains(name) ? "Sói" : "Unknown";
         		        		if(a.getThosan().contains(name)){
         		        			a.thosanChet.add(name);
         			        		for(String all : a.getPlayers()) {
         			        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color("&6Thợ săn &fsẽ nhắm bắn một người trước khi chết!"));
         			        		}
         		        		} else {
         			        		for(String all : a.getPlayers()) {
         			        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.death").replaceAll("%player%", name).replaceAll("%role%", role)));
         							}
         				    		addSpec(Bukkit.getPlayer(name));
         		        		}
	         				}
         				}
         				new BukkitRunnable() {
        	        		@Override
        	                public void run() {
        	        			if(a.thosanChet.isEmpty()) {
        	        				skipTime(i);
        	        			} else {
        	        				checkThosanvote(i);
        	        			}
        	        		}
        	        	}.runTaskLater(plugin, 40);
        	        	skiptime = Game.m.getConfig().getInt("time.skip");
        				this.cancel();
         			}
         		}
         	}.runTaskTimer(plugin, 0, 20l);
        }
    }
    
    public void checkThosanvote(int i) {
    	Arena a = getArena(i);
        if(a == null){
            return;
        }
        a.setRole(Role.THOSAN);
        for(String name : a.getPlayers()) {
        	Player p = Bukkit.getPlayer(name);
        	p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 2, 2);
        }
        for(String name : a.getAlives()) {
        	Player p = Bukkit.getPlayer(name);
        	if(a.getThosan().contains(name)) {
        		FBar.sendTitleBar(Bukkit.getPlayer(name), "", ChatUtils.color("&a&lBắn 1 người trước khi chết"));
        		new BukkitRunnable() {
            		@Override
                    public void run() {
            			p.getActivePotionEffects().clear();
            			p.removePotionEffect(PotionEffectType.BLINDNESS);
            		}
            	}.runTaskLater(plugin, 10);
            	p.getInventory().setItem(0, new FItem(FSkull.byName(p.getName())).setName("§e§lCHỌN NGƯỜI CHƠI")
        				.addLore("")
        				.addLore("§aChuột trái để chọn")
        				.addLore("")
        				.toItemStack());
        	}
        }
        if(checkWins(i) == true) {
        	gameStop(i);
        } else {
            new BukkitRunnable() {
        		@Override
        		public void run() {
        			if(thosantime > 0) {
        				if(a.thosanChon.isEmpty()) {
	        				thosantime = thosantime - 1;
	        				for(String all : a.getPlayers()) {
	        					FBar.sendActionBar(Bukkit.getPlayer(all), ChatUtils.color(Game.m.getConfig().getString("msg.time.thosan").replaceAll("%sec%", thosantime + "")));
	        				}
        				} else {
        					skipTime(i);
	        				thosantime = Game.m.getConfig().getInt("time.thosan");
	        				this.cancel();
        				}
        			} else {
        				for(String name : a.getAlives()) {
        					String role = a.getBaove().contains(name) ? "Bảo vệ" : 
        						a.getTientri().contains(name) ? "Tiên tri" : 
        							a.getPhuthuy().contains(name) ? "Phù thủy" : 
        								a.getDanlang().contains(name) ? "Dân làng" : 
        									a.getTihi().contains(name) ? "Ti hí" :
        										a.getThosan().contains(name) ? "Thợ săn" :
        											a.getSoi().contains(name) ? "Sói" : "Unknown";
        					if(a.getThosan().contains(name)) {
	        					for(String all : a.getPlayers()) {
				        			Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.death").replaceAll("%player%", name).replaceAll("%role%", role)));
				        		}
        						addSpec(Bukkit.getPlayer(name));
        					}
        					
        				}
        				new BukkitRunnable() {
        	        		@Override
        	                public void run() {
        	        			skipTime(i);
        	        		}
        				}.runTaskLater(plugin, 20);

        				thosantime = Game.m.getConfig().getInt("time.thosan");
        				this.cancel();
        			}
        		}
        	}.runTaskTimer(plugin, 0, 20l);	
        }
    }
    				
    public void skipTime(int i) {
    	Arena a = getArena(i);//get the arena you want to join
        if(a == null){//make sure it is not null
            return;
        }
        for(String name : a.getAlives()) {
        	Bukkit.getPlayer(name).getInventory().clear();
		}
		a.victimdan.clear();
		a.voteDan.clear();
		a.victimsoi.clear();
		a.voteSoi.clear();
		a.thosanChet.clear();
		a.thosanChon.clear();
		a.lockedPhuthuy.clear();
		a.victimbaove.clear();
		a.victimtientri.clear();
		a.victimphuthuycuu.clear();
		a.victimphuthuygiet.clear();

        a.getPlayers().forEach(name -> plugin.gameScoreboard.scoreGame(Bukkit.getPlayer(name)));
        a.setRole(Role.UNKNOWN);
        //check xem win hay chua
        if(checkWins(i) == true) {
        	gameStop(i);
        } else {
            new BukkitRunnable() {
        		@Override
        		public void run() {
        			if(skiptime > 0) {
        				skiptime = skiptime - 1;
        				for(String name : a.getPlayers()) {
        					Player p = Bukkit.getPlayer(name);
        					FBar.sendActionBar(p, ChatUtils.color(Game.m.getConfig().getString("msg.time.night").replaceAll("%sec%", skiptime + "")));
        				}
        			} else {
        				if(a.getBaove().isEmpty()) {
        					String ss = Game.m.getConfig().getString("Arenas." + i + ".Lobby", "");
        			        World w = Bukkit.getWorld(ss.split(",")[0]);
        			        w.setTime(20000);
        			        a.setState(GameState.NIGHT);
        					soiTime(i);
        				}
        				else bvTime(i);
        				
        				skiptime = Game.m.getConfig().getInt("time.skip");
        				this.cancel();
        			}
        		}
        	}.runTaskTimer(plugin, 0, 20l);
        }
    }
    
    public boolean checkWins(int i) {
    	Arena a = getArena(i);
        if(a == null){
            return false;
        }
        if(a.getSoi().isEmpty()) {
        	for(String all : a.getPlayers()) {
        		Player loz = Bukkit.getPlayer(all);
        		SPlayer sp = SP.get(loz);
        		sp.fc.set("sotran", sp.getGames() + 1);
        		sp.saveToFile();
        		FBar.sendTitleBar(loz, "&a&lTHẮNG", "&fPhe dân");
        	}
        	for(String phedan : a.getPheDan()) {
        		Player loz = Bukkit.getPlayer(phedan);
        		SPlayer sp = SP.get(loz);
        		sp.fc.set("thang", sp.getWins() + 1);
        		sp.saveToFile();
        	}
        	for(String phesoi : a.getPheSoi()) {
        		Player loz = Bukkit.getPlayer(phesoi);
        		SPlayer sp = SP.get(loz);
        		sp.fc.set("thua", sp.getClose() + 1);
        		sp.saveToFile();
        	}
        	Bukkit.broadcastMessage(ChatUtils.color("&a&lDân làng" + Game.m.getConfig().getString("msg.bc-win").replaceAll("%id%", i + "")));
        	a.setState(GameState.DAY);
        	a.setRole(Role.UNKNOWN);
        	return true;
        }
        if (a.getBaove().isEmpty() && a.getDanlang().isEmpty() && a.getPhuthuy().isEmpty() && a.getTientri().isEmpty() && a.getTihi().isEmpty() && a.getThosan().isEmpty()){
        	for(String all : a.getPlayers()) {
        		Player loz = Bukkit.getPlayer(all);
        		SPlayer sp = SP.get(loz);
        		sp.fc.set("sotran", sp.getGames() + 1);
        		sp.saveToFile();
        		FBar.sendTitleBar(loz, "&a&lTHẮNG", "&cPhe sói");
        	}
        	for(String phedan : a.getPheDan()) {
        		Player loz = Bukkit.getPlayer(phedan);
        		SPlayer sp = SP.get(loz);
        		sp.fc.set("thua", sp.getWins() + 1);
        		sp.saveToFile();
        	}
        	for(String phesoi : a.getPheSoi()) {
        		Player loz = Bukkit.getPlayer(phesoi);
        		SPlayer sp = SP.get(loz);
        		sp.fc.set("thang", sp.getClose() + 1);
        		sp.saveToFile();
        	}
        	Bukkit.broadcastMessage(ChatUtils.color("&c&lSói" + Game.m.getConfig().getString("msg.bc-win").replaceAll("%id%", i + "")));
        	a.setState(GameState.DAY);
        	a.setRole(Role.UNKNOWN);
        	return true;
        }
        
        return false;
    }
    
    
    public void gameStop(int i) {
    	Arena a = getArena(i);//get the arena you want to join
        if(a == null){//make sure it is not null
            return;
        }
        new BukkitRunnable() {
    		@Override
    		public void run() {
    			if(endtime > 0) {
    				endtime = endtime - 1;
    				for(String name : a.getPlayers()) {
    					Player p = Bukkit.getPlayer(name);
    					FBar.sendActionBar(p, ChatUtils.color(Game.m.getConfig().getString("msg.time.end").replaceAll("%sec%", endtime + "")));
    				}
    			} else {
    				for(String name : a.getPlayers()) {
    					
    					Bukkit.getPlayer(name).getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
    					
    					Player p = Bukkit.getPlayer(name);
    					String ss = Game.m.getConfig().getString("Lobby", "");
    				    World w = Bukkit.getWorld(ss.split(" ")[0]);
    				    double x = FNum.rd(ss.split(" ")[1]);
    					double y = FNum.rd(ss.split(" ")[2]);
    					double z = FNum.rd(ss.split(" ")[3]);
    					double yaw = FNum.rd(ss.split(" ")[4]);
    					double pitch = FNum.rd(ss.split(" ")[5]);
    					Location loc = new Location(w, x, y, z);
    					loc.setYaw((float) yaw);
    					loc.setPitch((float) pitch);
    					new BukkitRunnable() {
    			    		@Override
    			            public void run() {
    			    			p.removePotionEffect(PotionEffectType.BLINDNESS);
    			    			p.removePotionEffect(PotionEffectType.INVISIBILITY);
    			    			p.getActivePotionEffects().clear();
    			    		}
    			    	}.runTaskLater(plugin, 10);
    					new BukkitRunnable() {
    		        		@Override
    		                public void run() {
    		        			removePlayer(p);
    		        			p.teleport(loc);
    		        		}
    		        	}.runTaskLater(plugin, 10);
    				}
    				for(String name : a.getSpec()) {
    					Player p = Bukkit.getPlayer(name);
    					String ss = Game.m.getConfig().getString("Lobby", "");
    				    World w = Bukkit.getWorld(ss.split(" ")[0]);
    				    double x = FNum.rd(ss.split(" ")[1]);
    					double y = FNum.rd(ss.split(" ")[2]);
    					double z = FNum.rd(ss.split(" ")[3]);
    					double yaw = FNum.rd(ss.split(" ")[4]);
    					double pitch = FNum.rd(ss.split(" ")[5]);
    					Location loc = new Location(w, x, y, z);
    					loc.setYaw((float) yaw);
    					loc.setPitch((float) pitch);
    					new BukkitRunnable() {
    			    		@Override
    			            public void run() {
    			    			p.removePotionEffect(PotionEffectType.BLINDNESS);
    			    			p.removePotionEffect(PotionEffectType.INVISIBILITY);
    			    			p.getActivePotionEffects().clear();
    			    		}
    			    	}.runTaskLater(plugin, 10);
    					new BukkitRunnable() {
    		        		@Override
    		                public void run() {

    		        			p.teleport(loc);
    		        			removePlayer(p);
    		        			
    		        		}
    		        	}.runTaskLater(plugin, 10);
    				}
    				resetArena(i);
    				this.cancel();
    			}
    		}
    	}.runTaskTimer(plugin, 0, 20l);
    }
    
    public void resetArena(int i) {
    	Arena a = getArena(i);//get the arena you want to join
        if(a == null){//make sure it is not null
            return;
        }
        a.setState(GameState.WAITING);
        a.getPlayers().clear();
        a.getSpec().clear();
        a.getAlives().clear();
        a.spawns.clear();
        a.spawnsCount.clear();
        a.locplayers.clear();
        
        a.pheDan.clear();
        a.pheSoi.clear();
        
        a.voteDan.clear();
        a.voteSoi.clear();
        
        a.hasBaove.clear();
        a.victimbaove.clear();
        a.victimdan.clear();
        a.victimphuthuycuu.clear();
        a.hasCuu.clear();
        a.victimphuthuygiet.clear();
        a.hasGiet.clear();
        a.victimsoi.clear();
        a.victimtientri.clear();
        
        a.thosanChet.clear();
        
        a.getTihi().clear();
        a.getThosan().clear();
        a.getBaove().clear();
        a.getSoi().clear();
        a.getTientri().clear();
        a.getPhuthuy().clear();
        a.getDanlang().clear();
        a.getSpawns().clear();
        
        Bukkit.getConsoleSender().sendMessage("Load " + i);
        if(plugin.getConfig().getStringList("Arenas." + i + ".Spawns").isEmpty()) {
        	return;
        }
        for(String str : plugin.getConfig().getStringList("Arenas." + a.getId() + ".Spawns")) {
        	Location spawnsLists;
        	World w = Bukkit.getWorld(str.split(" ")[0]);
			double x = FNum.rd(str.split(" ")[1]);
			double y = FNum.rd(str.split(" ")[2]);
			double z = FNum.rd(str.split(" ")[3]);
			spawnsLists = new Location(w, x, y, z);
			a.getSpawns().add(spawnsLists);
            Bukkit.getConsoleSender().sendMessage("Spawn load " + a.getId() + " - " + spawnsLists.toString());
        }
    }    

    //create arena
    public Arena createArena(Location l){
        int num = arenaSize + 1;
        arenaSize++;

        Arena a = new Arena(l, num, new HashMap<String, Location>());
        arenas.add(a);

        plugin.getConfig().set("Arenas." + num + ".Lobby", serializeLoc(l));
        plugin.getConfig().set("Arenas." + num + ".maxPlayers", 12);
        List<Integer> list = plugin.getConfig().getIntegerList("Arenas.Lists");
        list.add(num);
        plugin.getConfig().set("Arenas.Lists", list);
        plugin.saveConfig();

        return a;
    }
    
    public void addSpawn(String ss, int i) {
    	Arena a = getArena(i);
    	if(a == null) {
            return;
        }
    	List<String> list = plugin.getConfig().getStringList("Arenas." + i + ".Spawns");
    	list.add(ss);
    	if(spawnsCounts.containsKey(1)) {
    		spawnsCounts.put(i, spawnsCounts.get(i) + 1);
    	} else spawnsCounts.put(i, 1);
    	plugin.getConfig().set("Arenas." + i + ".Spawns", list);
    	plugin.saveConfig();
    }

    public Arena reloadArena(Location l) {
        int num = arenaSize + 1;
        arenaSize++;
 
        Arena a = new Arena(l, num, new HashMap<String, Location>());
        arenas.add(a);
 
        return a;
    }
    
    public void removeArena(int i) {
        Arena a = getArena(i);
        if(a == null) {
            return;
        }
        arenas.remove(a);

        plugin.getConfig().set("Arenas." + i, null);
        List<Integer> list = plugin.getConfig().getIntegerList("Arenas.Lists");
        list.remove(i);
        plugin.getConfig().set("Arenas.Lists", list);
        plugin.saveConfig();    
    }

    public boolean isInGame(Player p){
        for(Arena a : arenas){
            if(a.getPlayers().contains(p.getName()))
            	return true;
        }
        return false;
    }

    public void loadGames(){
        arenaSize = 0;      

        if(plugin.getConfig().getIntegerList("Arenas.Lists").isEmpty()){
            return;
        }
        
        for(int i : plugin.getConfig().getIntegerList("Arenas.Lists")){
            Arena a = reloadArena(ditmeLoc(plugin.getConfig().getString("Arenas." + i + ".Lobby")));
            a.id = i;
            Bukkit.getConsoleSender().sendMessage("Load " + i);
            if(plugin.getConfig().getStringList("Arenas." + i + ".Spawns").isEmpty()) {
            	return;
            }
            for(String str : plugin.getConfig().getStringList("Arenas." + a.getId() + ".Spawns")) {
            	Location spawnsLists;
            	World w = Bukkit.getWorld(str.split(" ")[0]);
    			double x = FNum.rd(str.split(" ")[1]);
    			double y = FNum.rd(str.split(" ")[2]);
    			double z = FNum.rd(str.split(" ")[3]);
    			spawnsLists = new Location(w, x, y, z);
    			a.getSpawns().add(spawnsLists);
                Bukkit.getConsoleSender().sendMessage("Spawn load " + a.getId() + " - " + spawnsLists.toString());
            }
        }
    }

    public String serializeLoc(Location l){
        return l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ();
    }
    public Location deserializeLoc(String s){
        String[] st = s.split(",");
        return new Location(Bukkit.getWorld(st[0]), Integer.parseInt(st[1]), Integer.parseInt(st[2]), Integer.parseInt(st[3]));
    }
    
    public Location ditmeLoc(String ss) {
    	return new Location(Bukkit.getWorld(ss.split(",")[0]), FNum.rd(ss.split(",")[1]), FNum.rd(ss.split(",")[2]), FNum.rd(ss.split(",")[3]));
    }

    public Location unSeterilizeLocation(String paramString) { 
    	String[] arrayOfString = paramString.split(" "); 
    	return new Location(Bukkit.getWorld(arrayOfString[0]), Double.valueOf(arrayOfString[1]).doubleValue(), Double.valueOf(arrayOfString[2]).doubleValue(), Double.valueOf(arrayOfString[3]).doubleValue(), Float.valueOf(arrayOfString[4]).floatValue(), Float.valueOf(arrayOfString[5]).floatValue()); 
    }
    
}
