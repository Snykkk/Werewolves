package Snykkk;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import Snykkk.api.FNum;
import Snykkk.arena.ArenaManager;
import Snykkk.menu.PlayerInRoom;
import Snykkk.players.SP;
import Snykkk.players.SPlayer;
import Snykkk.score.GameScoreboard;
import Snykkk.utils.ChatUtils;

public class Game extends JavaPlugin {
	
	public static Game m;
	public String server_version = "";
	public GameScoreboard gameScoreboard;
	
	@Override
    public void onEnable(){
		m = this;
        if(!getDataFolder().exists())
            getDataFolder().mkdir();

		getConfig().options().copyDefaults(true);
		saveConfig();
		insClass();

        new ArenaManager(this);
        ArenaManager.getManager().loadGames();
        
        server_version = Bukkit.getServer().getClass().getPackage().getName();
		server_version = server_version.substring(server_version.lastIndexOf(".") + 1);
		server_version = server_version.substring(1, server_version.length());
		server_version = server_version.toUpperCase();
        

		Bukkit.getConsoleSender().sendMessage("§b|===========================================|");
		Bukkit.getConsoleSender().sendMessage("§b|             Werewolves MCFAMILY           |");
		Bukkit.getConsoleSender().sendMessage("§b|                    " + m.getConfig().getString("phienban.ver") + "§b                  |");
		Bukkit.getConsoleSender().sendMessage("§b|                  " + m.getConfig().getString("phienban.author") + "§b                |");
		Bukkit.getConsoleSender().sendMessage("§b|                sv.mcfamily.vn             |");
		Bukkit.getConsoleSender().sendMessage("§b|===========================================|");

		Bukkit.getPluginManager().registerEvents(new SP(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerInRoom(), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        
        for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getGameMode() == GameMode.SURVIVAL) {
				p.setGameMode(GameMode.ADVENTURE);
			}
			new BukkitRunnable() {
        		@Override
                public void run() {
        			p.getInventory().clear();
        			p.getActivePotionEffects().clear();
        			p.removePotionEffect(PotionEffectType.BLINDNESS);
        			
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
		        			p.teleport(loc);
		        		}
		        	}.runTaskLater(m, 10);
        		}
        	}.runTaskLater(m, 10);
        	if(!GameListener.lobby.contains(p)) {
        		GameListener.lobby.add(p);
        	}
		}
    }

    @Override
    public void onDisable(){
        reloadConfig();
		Bukkit.getConsoleSender().sendMessage("§bMaSoi is disabling ...");
		
    }
    
    public void insClass() {
    	gameScoreboard = new GameScoreboard();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String a, String args[]){
    	if (a.equalsIgnoreCase("masoi") || a.equalsIgnoreCase("ms")) {
	        if(!(sender instanceof Player)){
	            sender.sendMessage("Aaaaaaaaaaaaaaaa you gayyyyy!");
	            return true;
	        }
	
	        Player p = (Player) sender;
    		if (args.length == 0) {
    			p.sendMessage(ChatUtils.color("============================"));
    			p.sendMessage(ChatUtils.color(""));
    			if (sender.hasPermission("masoi.admin")) {
    				p.sendMessage(ChatUtils.color("&e/masoi tao&7: tạo đấu trường"));
    				p.sendMessage(ChatUtils.color("&e/masoi xoa <id>&7: tạo đấu trường"));
    				p.sendMessage(ChatUtils.color("&e/masoi addspawn <id>&7: thêm spawn"));
    				p.sendMessage(ChatUtils.color("&e/masoi setspawn&7: đặt sảnh chính"));
    				p.sendMessage(ChatUtils.color(""));
    			}
    			p.sendMessage(ChatUtils.color("&e/masoi joinrandom&7: vào đấu trường"));
    			p.sendMessage(ChatUtils.color("&e/masoi thamgia <id>&7: vào đấu trường"));
    			p.sendMessage(ChatUtils.color("&e/masoi roi&7: rời đấu trường"));
    			p.sendMessage(ChatUtils.color("&e/masoi thongtin&7: xem thông tin"));
    			p.sendMessage(ChatUtils.color(""));
    			p.sendMessage(ChatUtils.color("============================"));
    		}
	
	        if(args.length == 1 && args[0].equals("tao") && sender.isOp()){
	            ArenaManager.getManager().createArena(p.getLocation());
	            p.sendMessage(ChatUtils.color(m.getConfig().getString("msg.prefix") + m.getConfig().getString("msg.created") + " " + p.getLocation().getWorld().getName() + "," + p.getLocation().getX() + "," + p.getLocation().getY() + "," + p.getLocation().getZ()));
	
	            return true;
	        }
	        if(args.length == 2 && args[0].equals("join")){
	            if(args.length != 2){
	                p.sendMessage(ChatUtils.color(m.getConfig().getString("msg.prefix") + m.getConfig().getString("msg.pls")));
	                return true;
	            }
	            int num = 0;
	            try{
	                num = Integer.parseInt(args[1]);
	            }catch(NumberFormatException e){
	                p.sendMessage(ChatUtils.color(m.getConfig().getString("msg.prefix") + m.getConfig().getString("msg.not-exist").replaceAll("%ARENA%", num + "")));
	            }
	            ArenaManager.getManager().addPlayer(p, num);
	
	            return true;
	        }
	        if(args.length == 1 && args[0].equals("joinrandom")){
	        	int num = FNum.randomInt(1, m.getConfig().getIntegerList("Arenas.Lists").size());
	            ArenaManager.getManager().addPlayer(p, num);
	
	            return true;
	        }
	        if(args.length == 1 && args[0].equals("roi")){
	            ArenaManager.getManager().removePlayer(p);
	
	            return true;
	        }
	        if(args.length == 2 && args[0].equals("xoa") && sender.isOp()){
	            if(args.length != 2){
	                p.sendMessage(ChatUtils.color(m.getConfig().getString("msg.prefix") + m.getConfig().getString("msg.pls")));
	                return true;
	            }
	            int num = 0;
	            try{
	                num = Integer.parseInt(args[1]);
	            }catch(NumberFormatException e){
	                p.sendMessage(ChatUtils.color(m.getConfig().getString("msg.prefix") + m.getConfig().getString("msg.not-exist").replaceAll("%ARENA%", num + "")));
	            }
	            ArenaManager.getManager().removeArena(num);
	
	            return true;
	        }
	        
	        if(args.length == 2 && args[0].equals("open") && sender.isOp()){
	        	int num = 0;
	            try{
	                num = Integer.parseInt(args[1]);
	            }catch(NumberFormatException e){
	                p.sendMessage(ChatUtils.color(m.getConfig().getString("msg.prefix") + m.getConfig().getString("msg.not-exist").replaceAll("%ARENA%", num + "")));
	            }
	            PlayerInRoom.open((Player) sender, num);
	        }

    		if (args.length == 2 && args[0].equals("addspawn")) {
	            int num = 0;
	            try{
	                num = Integer.parseInt(args[1]);
	            }catch(NumberFormatException e){
	                p.sendMessage(ChatUtils.color(m.getConfig().getString("msg.prefix") + m.getConfig().getString("msg.not-exist").replaceAll("%ARENA%", num + "")));
	            }
	            
    			String s = p.getLocation().getWorld().getName() + " " + p.getLocation().getX() + " " + p.getLocation().getY() + " " + p.getLocation().getZ();
    			
    			ArenaManager.getManager().addSpawn(s, num);
    			p.sendMessage(ChatUtils.color(m.getConfig().getString("msg.addspawn").replaceAll("%ARENA%", num + "").replaceAll("%SPAWN%", ArenaManager.am.spawnsCounts.toString() + "")));

	            return true;
    		}
	        if (args.length == 1 && args[0].equals("setspawn") && sender.isOp()) {
    			String s = p.getLocation().getWorld().getName() + " " + p.getLocation().getX() + " " + p.getLocation().getY() + " " + p.getLocation().getZ() + " " + p.getLocation().getYaw() + " " + p.getLocation().getPitch();
    			
    			m.getConfig().set("Lobby", s);
    			m.saveConfig();
    			p.sendMessage(ChatUtils.color(m.getConfig().getString("msg.prefix") + m.getConfig().getString("msg.set.spawn")));
    			
    			return true;
    		}
    		if (args.length == 1 && args[0].equals("thongtin")) {
    			if (sender instanceof Player) {
	    			SPlayer sp = SP.get(p);
	    			List<String> message = m.getConfig().getStringList("thongtin");
			        
			        for (String s : message) {
			        	p.sendMessage(ChatUtils.color(s).replaceAll("%player%", p.getName()).replaceAll("%games%", "" + sp.getGames()).replaceAll("%wins%", "" + sp.getWins()).replaceAll("%close%", "" + sp.getClose()));
			        }
			        
			        return true;
    			}
    		}
    		if (args.length == 1 && args[0].equals("no")) {
    			sender.sendMessage(ChatUtils.color("&cKhông thể dùng lệnh trong game!"));
    		}
    	}

        return false;
    }

}
