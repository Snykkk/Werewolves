package Snykkk.score;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Scoreboard;

import Snykkk.arena.Arena;
import Snykkk.arena.ArenaManager;
import Snykkk.utils.ChatUtils;

public class GameScoreboard {
	
	private static ScoreboardManager bm;
	private static Scoreboard b;
	private static Objective o;
	private static Score role;
	private static Score time;
	private static Score name;
	private static Score str;
	private static Score str1;
	private static Score str2;
	private static Score str3;
	private static Score ip;
	private static Score players;
	
	public void scoreGame(Player p) {
		Arena a = null;//make an arena
        for(Arena arena : ArenaManager.am.arenas){
            if(arena.getPlayers().contains(p.getName())){
                a = arena;
            }
     
        }
        
		bm = Bukkit.getScoreboardManager();
		b = bm.getNewScoreboard();
		o = b.registerNewObjective("masoi", "");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatUtils.color("&e&lMA SÓI"));

		str3 = o.getScore(ChatUtils.color("&7"));
		str3.setScore(7);
		
		name = o.getScore(ChatUtils.color("&7 Phòng ") + a.getId());
		name.setScore(6);
		
		str1 = o.getScore(ChatUtils.color("&7"));
		str1.setScore(5);
		
		players = o.getScore(ChatUtils.color("&f Còn sống: &e" + a.getAlives().size()));
		players.setScore(4);
		
		str2 = o.getScore("");
		str2.setScore(3);
		
		if(a.getBaove().contains(p.getName())) {
			role = o.getScore(ChatUtils.color("&e Vai trò: &bBảo vệ"));
			role.setScore(2);
		} else if(a.getSoi().contains(p.getName())) {
			role = o.getScore(ChatUtils.color("&e Vai trò: &cSói"));
			role.setScore(2);
		} else if(a.getTientri().contains(p.getName())) {
			role = o.getScore(ChatUtils.color("&e Vai trò: &5Tiên tri"));
			role.setScore(2);
		} else if(a.getPhuthuy().contains(p.getName())) {
			role = o.getScore(ChatUtils.color("&e Vai trò: &aPhù thủy"));
			role.setScore(2);
		} else if(a.getDanlang().contains(p.getName())){
			role = o.getScore(ChatUtils.color("&e Vai trò: &2Dân làng"));
			role.setScore(2);
		} else if(a.getTihi().contains(p.getName())){
			role = o.getScore(ChatUtils.color("&e Vai trò: &8Ti hí"));
			role.setScore(2);
		} else if(a.getThosan().contains(p.getName())){
			role = o.getScore(ChatUtils.color("&e Vai trò: &6Thợ săn"));
			role.setScore(2);
		} else {
			role = o.getScore(ChatUtils.color("&e Vai trò: &fUnknown"));
			role.setScore(2);
		}

		str = o.getScore("");
		str.setScore(1);

		ip = o.getScore(ChatUtils.color("&e&l SV.MCFAMILY.VN"));
		ip.setScore(0);
		
		p.setScoreboard(b);
	}
	
	public void scoreLobby(Player p, int countdown) {
		Arena a = null;//make an arena
        for(Arena arena : ArenaManager.am.arenas){
            if(arena.getPlayers().contains(p.getName())){
                a = arena;//if the arena has the player, the arena field would be the arena containing the player
            }
            //if none is found, the arena will be null
        }
        
		bm = Bukkit.getScoreboardManager();
		b = bm.getNewScoreboard();
		o = b.registerNewObjective("masoi", "");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		o.setDisplayName(ChatUtils.color("&e&lMA SÓI"));

		str2 = o.getScore("");
		str2.setScore(6);
		
		
		name = o.getScore(ChatUtils.color("&7 Phòng ") + a.getId());
		name.setScore(5);
		
		str1 = o.getScore(ChatUtils.color("&7"));
		str1.setScore(4);		
		
		str3 = o.getScore(ChatUtils.color("&f Bắt đầu trong:"));
		str3.setScore(3);
		
		time = o.getScore(ChatUtils.color("&a    " + countdown + " giây"));
		time.setScore(2);
		

		str = o.getScore(ChatUtils.color("&7"));
		str.setScore(1);

		ip = o.getScore(ChatUtils.color("&e&l SV.MCFAMILY.VN"));
		ip.setScore(0);
		
		p.setScoreboard(b);
	}

}
