package Snykkk.arena;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import Snykkk.Game;
import Snykkk.utils.GameState;
import Snykkk.utils.Role;

public class Arena {

	public int id = 0;//the arena id
    public Location spawn = null;//spawn location for the arena
    public List<Location> spawnsList = new ArrayList<Location>();
    public List<Location> spawnsCount = new ArrayList<Location>();
    List<String> players = new ArrayList<String>();//list of players
    List<String> alives = new ArrayList<String>();//list of players
    List<String> spectators = new ArrayList<String>();//list of players

    public HashMap<Location, Player> spawns = new HashMap<Location, Player>();
    public HashMap<String, Location> locplayers = new HashMap<String, Location>();
    
    public HashMap<String, Integer> victimsoi = new HashMap<String, Integer>();
    public HashMap<String, Integer> victimdan = new HashMap<String, Integer>();
    
    public HashMap<String, String> voteSoi = new HashMap<String, String>();
    public HashMap<String, String> voteDan = new HashMap<String, String>();
    
    public Set<String> victimbaove = new HashSet<String>();
    public HashMap<String, String> hasBaove = new HashMap<String, String>();
    
    public Set<String> victimphuthuycuu = new HashSet<String>();
    public Set<String> hasCuu = new HashSet<String>();
    public Set<String> victimphuthuygiet = new HashSet<String>();
    public Set<String> hasGiet = new HashSet<String>();
    public Set<String> lockedPhuthuy = new HashSet<String>();
    
    public Set<String> thosanChet = new HashSet<String>();
    public Set<String> thosanChon = new HashSet<String>();
    
    public Set<String> victimtientri = new HashSet<String>();
    
    public List<String> baove = new ArrayList<String>();
    public List<String> soi = new ArrayList<String>();
    public List<String> tientri = new ArrayList<String>();
    public List<String> phuthuy = new ArrayList<String>();
    public List<String> danlang = new ArrayList<String>();
    public List<String> tihi = new ArrayList<String>();
    public List<String> thosan = new ArrayList<String>();

    public List<String> pheDan = new ArrayList<String>();
    public List<String> pheSoi = new ArrayList<String>();
    
    public GameState state;
    public Role role;
    public ArenaManager am;

    String prefix = Game.m.getConfig().getString("msg.prefix");
    
    //now let's make a few getters/setters, and a constructor
    public Arena(Location loc, int id, HashMap<String, Location> locs){
        this.spawn = loc;
        this.id = id;
        this.state = GameState.WAITING;
        this.role = Role.UNKNOWN;
        this.locplayers = locs;
    }

    public int getId(){
        return this.id;
    }
    
    public GameState getState() {
        return this.state;
    }
	
	public void setState(GameState state) {
	    this.state = state;
	}
	
	public Role getRole() {
		return this.role;
	}
	
	public void setRole(Role role) {
		this.role = role;
	}

    public List<Location> getSpawns(){
        return this.spawnsList;
    }

    public List<String> getPheDan(){
        return this.pheDan;
    }

    public List<String> getPheSoi(){
        return this.pheSoi;
    }

    public List<String> getPlayers(){
        return this.players;
    }

    public List<String> getAlives(){
        return this.alives;
    }

    public List<String> getSpec(){
        return this.spectators;
    }

    public List<String> getDanlang(){
        return this.danlang;
    }

    public List<String> getSoi(){
        return this.soi;
    }

    public List<String> getTientri(){
        return this.tientri;
    }

    public List<String> getPhuthuy(){
        return this.phuthuy;
    }

    public List<String> getBaove(){
        return this.baove;
    }

    public List<String> getTihi(){
        return this.tihi;
    }

    public List<String> getThosan(){
        return this.thosan;
    }

    
}
