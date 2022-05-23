package Snykkk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import Snykkk.api.FNum;
import Snykkk.arena.Arena;
import Snykkk.arena.ArenaManager;
import Snykkk.menu.PlayerInRoom;
import Snykkk.utils.GameState;

public class GameListener implements Listener{

    static List<String> players = new ArrayList<String>();
    public static List<Player> lobby = new ArrayList<Player>();
    static Game plugin;
    

    public GameListener(Game plugin){
        GameListener.plugin = plugin;
    }
    
//    @EventHandler
//	public void onMove(PlayerMoveEvent e) {
//		Player p = e.getPlayer();
//		if(ArenaManager.getManager().isInGame(p)) {
//			Arena a = null;//make an arena
//	        for(Arena arena : ArenaManager.am.arenas){
//	            if(arena.getPlayers().contains(p.getName())){
//	                a = arena;
//	            }
//	        }
//	        if(a == null || !a.getPlayers().contains(p.getName())){
//	            p.sendMessage("Invalid operation!");
//	            return;
//	        }
//			if(!a.getSpec().contains(p.getName()) && (a.getState() == GameState.INGAME || a.getState() == GameState.ENDING)) {
//				e.setTo(e.getFrom());
//			}
//		}
//		
//	}

    @EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		lobby.remove(p);
		if(ArenaManager.getManager().isInGame(p)) {
			ArenaManager.getManager().removePlayer(p);
		}
	}
    
    @EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		new BukkitRunnable() {
			public void run() {
				lobby.add(p);
			}
		}.runTaskLater(Game.m, 5);
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
			public void run() {
				p.teleport(loc);
				p.setGameMode(GameMode.ADVENTURE);
    			p.getInventory().clear();
    			p.getActivePotionEffects().clear();
    			p.removePotionEffect(PotionEffectType.BLINDNESS);
    			p.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
		}.runTaskLater(Game.m, 5);
    }
    
    @EventHandler
	public void Player_Chat_Format(AsyncPlayerChatEvent e) {
    	Player p = e.getPlayer();
    	if(ArenaManager.getManager().isInGame(p)) {
    		e.setCancelled(true);
			Arena a = null;//make an arena
	        for(Arena arena : ArenaManager.am.arenas){
	            if(arena.getPlayers().contains(p.getName())){
	                a = arena;
	            }
	        }
	        if(a == null || !a.getPlayers().contains(p.getName())){
	            p.sendMessage("Invalid operation!");
	            return;
	        }
	        if(a.getState() == GameState.WAITING || a.getState() == GameState.STARTING) {
	        	for(String pi : a.getPlayers()) {
	        		Player players = Bukkit.getPlayer(pi);
		        	players.sendMessage("§7[?] " + p.getName() + " §f» §7" + e.getMessage());
	        	}
	        }
	        if(a.getPheDan().contains(p.getName()) && a.getState() == GameState.DAY) {
	        	if(a.getAlives().contains(p.getName())) {
	        		for(String alives : a.getPlayers()) {
		        		 Player players = Bukkit.getPlayer(alives);
		        		 players.sendMessage("§7[§2DÂN§7] " + p.getName() + " §f» §7" + e.getMessage());
		        	}
	        	}
	        }
	        if(a.getSoi().contains(p.getName())) {
	        	if(a.getState() == GameState.NIGHT) {
		        	 for(String soi : a.getSoi()) {
		        		 Player players = Bukkit.getPlayer(soi);
		        		 players.sendMessage("§7[§cSÓI§7] " + p.getName() + " §f» §7" + e.getMessage());
		        	 }
	        	}
	        	else if(a.getState() == GameState.DAY) {
	        		for(String alives : a.getPlayers()) {
		        		 Player players = Bukkit.getPlayer(alives);
		        		 players.sendMessage("§7[§2DÂN§7] " + p.getName() + " §f» §7" + e.getMessage());
		        	}
	        	}
	        }
	        if(a.getSpec().contains(p.getName())) {
		        for(String specs : a.getSpec()) {
		        	Player players = Bukkit.getPlayer(specs);
		        	players.sendMessage("§7[§8KHÁN GIẢ§7] " + p.getName() + " §f» §7" + e.getMessage());
		        }
	        }
    	}
    	else { 
    		for(Player online : lobby) {
    			online.sendMessage(p.getDisplayName() + " §f» §7" + e.getMessage());
    		}
    	}

    	e.setCancelled(true);
    }
    
    List<String> allowed_command = Arrays.asList("ms", "masoi");
    
    @EventHandler
	public void aa(PlayerCommandPreprocessEvent e) {
    	if(!e.getPlayer().isOp()) {
    		if(ArenaManager.getManager().isInGame(e.getPlayer())) {
		    	if (!allowed_command.contains(e.getMessage().substring(1).split(" ")[0])) {
					e.setMessage("/ms no");
				}
    		}
    	}
	}

    public static void add(Player p){
        final String name = p.getName();
        players.add(name);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            @Override
            public void run(){
                players.remove(name);
            }
        }, 100L);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
	public void click1 (InventoryClickEvent e) {
		

		Player p = (Player) e.getWhoClicked();
		
		if(ArenaManager.getManager().isInGame(p)) {
			Arena a = null;//make an arena
	        for(Arena arena : ArenaManager.am.arenas){
	            if(arena.getPlayers().contains(p.getName())){
	                a = arena;
	            }
	        }
	        if(a == null || !a.getPlayers().contains(p.getName())){
	            p.sendMessage("Invalid operation!");
	            return;
	        }
			try {
				if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§e§lCHỌN NGƯỜI CHƠI")) {
					e.setCancelled(true);
					PlayerInRoom.open(p, a.getId());
				}
				if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§c§lRỜI PHÒNG")) {
					e.setCancelled(true);
					ArenaManager.am.removePlayer(p);
				}
			} catch (NullPointerException nullPointerException) {}
		
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void click2 (PlayerInteractEvent e) {
		
		if (e.getHand() != EquipmentSlot.HAND) {return;}
		
		if (e.getAction().toString().contains("RIGHT")) {
			try {
			      
				Player p = e.getPlayer();
				ItemStack i = p.getInventory().getItemInMainHand();
				if(ArenaManager.getManager().isInGame(p)) {
					Arena a = null;//make an arena
			        for(Arena arena : ArenaManager.am.arenas){
			            if(arena.getPlayers().contains(p.getName())){
			                a = arena;
			            }
			        }
			        if(a == null || !a.getPlayers().contains(p.getName())){
			            p.sendMessage("Invalid operation!");
			            return;
			        }
			        if (i.getItemMeta().getDisplayName().startsWith("§e§lCHỌN NGƯỜI CHƠI")) {
						PlayerInRoom.open(p, a.getId());
						e.setCancelled(true);
					}
			        if (i.getItemMeta().getDisplayName().startsWith("§c§lRỜI PHÒNG")) {
						ArenaManager.am.removePlayer(p);
						e.setCancelled(true);
						
					}
				}
			}
			catch (Exception ex) {}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void drop (PlayerDropItemEvent e) {
		e.setCancelled(true);
		try {
			if (e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals("§e§lCHỌN NGƯỜI CHƠI")) {
				e.setCancelled(true);
			}
			if (e.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals("§c§lRỜI PHÒNG")) {
				e.setCancelled(true);
			}
		}
		catch (Exception ex) {}
	}

}
