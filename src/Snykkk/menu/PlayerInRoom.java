package Snykkk.menu;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Snykkk.Game;
import Snykkk.arena.Arena;
import Snykkk.arena.ArenaManager;
import Snykkk.api.FBar;
import Snykkk.api.FItem;
import Snykkk.api.FSkull;
import Snykkk.utils.ChatUtils;
import Snykkk.utils.GameState;
import Snykkk.utils.Role;

public class PlayerInRoom implements Listener{
	
	static String prefix = Game.m.getConfig().getString("msg.prefix");
    static FileConfiguration mfc = Game.m.getConfig();
	
	public static Arena getArena(int i){
        for(Arena a : ArenaManager.am.arenas){
            if(a.getId() == i){
                return a;
            }
        }
        return null;
    }
	
	public static void open (Player p, int id) {
		//00 01 02 03 04 05 06 07 08
		//09 10 11 12 13 14 15 16 17
		Inventory inv = Bukkit.createInventory(null, 18, "§5Chọn người");
		
		Arena a = getArena(id);
        if(a == null){
            p.sendMessage(ChatUtils.color(prefix + mfc.getString("msg.not-exist").replaceAll("%ARENA%", id + "")));
            return;
        }
        for(String name : a.getAlives()) {
        	inv.addItem(new FItem(FSkull.byName(name)).setName("§e" + name)
					.addLore("")
					.addLore("§aChuột trái để chọn")
					.addLore(a.getPhuthuy().contains(p.getName()) && a.getState() == GameState.NIGHT, "§eChuột phải để cứu")
					.addLore("")
					.toItemStack());
        }
		
		p.openInventory(inv);
	}
	
	@EventHandler
	public void c (InventoryClickEvent e) {
		if (e.getInventory().getName().contains("Chọn người")) {
			e.setCancelled(true);
			
			Player p = (Player) e.getWhoClicked();
			
			Arena a = null;//make an arena
	        for(Arena arena : ArenaManager.am.arenas){
	            if(arena.getPlayers().contains(p.getName())){
	                a = arena;//if the arena has the player, the arena field would be the arena containing the player
	            }
	            //if none is found, the arena will be null
	        }
	        if(a == null || !a.getPlayers().contains(p.getName())){//make sure it is not null
	            p.sendMessage("Invalid operation!");
	            return;
	        }
			
			try {
				ItemStack i = e.getCurrentItem();
				String name = e.getCurrentItem().getItemMeta().getDisplayName();
				
				if (i.getItemMeta().getLore().contains("§aChuột trái để chọn") && e.getClick() == ClickType.LEFT) {
					Player victim = Bukkit.getPlayer(ChatColor.stripColor(name));
					if(a.getSoi().contains(p.getName()) && a.getState() == GameState.NIGHT) {
						if(a.voteSoi.containsKey(p.getName())) {
							if(a.voteSoi.get(p.getName()).contains(victim.getName())) {
								p.sendMessage(ChatUtils.color("&cBạn đã chọn &e" + victim.getName() + " &crồi!"));
							} else {
								a.victimsoi.put(a.voteSoi.get(p.getName()), a.victimsoi.get(a.voteSoi.get(p.getName())) - 1);
								if(!a.victimsoi.containsKey(victim.getName())) {
									a.victimsoi.put(victim.getName(), 1);
									for(String soi : a.getSoi()) {
										Bukkit.getPlayer(soi).sendMessage(ChatUtils.color("&c" + p.getName() + " &fđã chọn &e" + victim.getName() + " &7(1)"));
									}
								} else {
									a.victimsoi.put(victim.getName(), a.victimsoi.get(victim.getName()) + 1);
									for(String soi : a.getSoi()) {
										Bukkit.getPlayer(soi).sendMessage(ChatUtils.color("&c" + p.getName() + " &fđã chọn &e" + victim.getName() + " &7(" + a.victimsoi.get(victim.getName()) + ")"));
									}
								}
								a.voteSoi.put(p.getName(), victim.getName());
							}
						} else {
							a.voteSoi.put(p.getName(), victim.getName());
							if(!a.victimsoi.containsKey(victim.getName())) {
								a.victimsoi.put(victim.getName(), 1);
								for(String soi : a.getSoi()) {
									Bukkit.getPlayer(soi).sendMessage(ChatUtils.color("&c" + p.getName() + " &fđã chọn &e" + victim.getName() + " &7(1)"));
								}
							} else {
								a.victimsoi.put(victim.getName(), a.victimsoi.get(victim.getName()) + 1);
								for(String soi : a.getSoi()) {
									Bukkit.getPlayer(soi).sendMessage(ChatUtils.color("&c" + p.getName() + " &fđã chọn &e" + victim.getName() + " &7(" + a.victimsoi.get(victim.getName()) + ")"));
								}
							}
						}
					} else if(a.getBaove().contains(p.getName()) && a.victimbaove.isEmpty() && a.getState() == GameState.NIGHT && a.getRole() == Role.BAOVE) {
						if(a.victimbaove.isEmpty()) {
							if(a.hasBaove.isEmpty()) {
								a.victimbaove.add(victim.getName());
								a.hasBaove.put(p.getName(), victim.getName());
								p.sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.victim").replace("%player%", victim.getName())));
							} else {
								if(!a.hasBaove.get(p.getName()).contains(victim.getName())) {
									a.victimbaove.add(victim.getName());
									a.hasBaove.put(p.getName(), victim.getName());
									p.sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.victim").replace("%player%", victim.getName())));
								} else {
									p.sendMessage(ChatUtils.color("&cBạn đã bảo vệ &e" + victim.getName() +" &cđêm trước rồi! Vui lòng chọn người khác!"));
								}
							}
						} else {
							if(a.hasBaove.isEmpty()) {
								a.victimbaove.clear();
								a.victimbaove.add(victim.getName());
								a.hasBaove.put(p.getName(), victim.getName());
								p.sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.victim").replace("%player%", victim.getName())));
							} else {
								if(!a.hasBaove.get(p.getName()).contains(victim.getName())) {
									a.victimbaove.clear();
									a.victimbaove.add(victim.getName());
									a.hasBaove.put(p.getName(), victim.getName());
									p.sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.victim").replace("%player%", victim.getName())));
								} else {
									p.sendMessage(ChatUtils.color("&cBạn đã bảo vệ &e" + victim.getName() +" &cđêm trước rồi! Vui lòng chọn người khác!"));
								}
							}
						}
					} else if(a.getTientri().contains(p.getName()) && a.victimtientri.isEmpty() && a.getState() == GameState.NIGHT && a.getRole() == Role.TIENTRI) {
						a.victimtientri.add(victim.getName());
						String role = a.getBaove().contains(victim.getName()) ? "Bảo vệ" : 
							a.getTientri().contains(victim.getName()) ? "Tiên tri" : 
								a.getPhuthuy().contains(victim.getName()) ? "Phù thủy" : 
									a.getDanlang().contains(victim.getName()) ? "Dân làng" : 
										a.getTihi().contains(victim.getName()) ? "Ti hí" :
											a.getThosan().contains(victim.getName()) ? "Thợ săn" :
												a.getSoi().contains(victim.getName()) ? "Sói" : "Unknown";
						FBar.sendTitleBar(p, "", ChatUtils.color(Game.m.getConfig().getString("msg.seer").replace("%player%", victim.getName()).replace("%role%", role)));
						p.sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.victim").replace("%player%", victim.getName())));
					} else if(a.getPhuthuy().contains(p.getName()) && a.victimphuthuygiet.isEmpty() && a.lockedPhuthuy.isEmpty() && a.getState() == GameState.NIGHT && a.getRole() == Role.PHUTHUY) {
						if(a.hasGiet.isEmpty()) {
							a.victimphuthuygiet.add(victim.getName());
							a.lockedPhuthuy.add(p.getName());
							a.hasGiet.add(victim.getName());
							p.sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.victim").replace("%player%", victim.getName())));
						} else {
							p.sendMessage(ChatUtils.color("&cBạn đã hết bình giết rồi!"));
						}
					} else if(a.getThosan().contains(p.getName()) && a.getRole() == Role.THOSAN) {
						String role = a.getBaove().contains(victim.getName()) ? "Bảo vệ" : 
    						a.getTientri().contains(victim.getName()) ? "Tiên tri" : 
    							a.getPhuthuy().contains(victim.getName()) ? "Phù thủy" : 
    								a.getDanlang().contains(victim.getName()) ? "Dân làng" : 
    									a.getTihi().contains(victim.getName()) ? "Ti hí" :
    										a.getThosan().contains(victim.getName()) ? "Thợ săn" :
    											a.getSoi().contains(victim.getName()) ? "Sói" : "Unknown";
						for(String all : a.getPlayers()) {
							Bukkit.getPlayer(all).sendMessage(ChatUtils.color("&c" + p.getName() + " &fđã chọn &e" + victim.getName()));
							Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.death").replaceAll("%player%", name).replaceAll("%role%", role)));
							Bukkit.getPlayer(all).sendMessage(ChatUtils.color(Game.m.getConfig().getString("msg.death").replaceAll("%player%", p.getName()).replaceAll("%role%", "Thợ săn")));
						}
						a.thosanChon.add(p.getName());
						ArenaManager.am.addSpec(victim);
						ArenaManager.am.addSpec(p);
						
					} else if(a.getAlives().contains(p.getName()) && a.getState() == GameState.DAY && a.getRole() == Role.DANLANG) {
						if(a.voteDan.containsKey(p.getName())) {
							if(a.voteDan.get(p.getName()).contains(victim.getName())) {
								p.sendMessage(ChatUtils.color("&cBạn đã chọn &e" + victim.getName() + " &crồi!"));
							} else {
								a.victimdan.put(a.voteDan.get(p.getName()), a.victimdan.get(a.voteDan.get(p.getName())) - 1);
								if(!a.victimdan.containsKey(victim.getName())) {
									a.victimdan.put(victim.getName(), 1);
									for(String soi : a.getPlayers()) {
										Bukkit.getPlayer(soi).sendMessage(ChatUtils.color("&c" + p.getName() + " &fđã chọn &e" + victim.getName() + " &7(1)"));
									}
								} else {
									a.victimdan.put(victim.getName(), a.victimdan.get(victim.getName()) + 1);
									for(String soi : a.getPlayers()) {
										Bukkit.getPlayer(soi).sendMessage(ChatUtils.color("&c" + p.getName() + " &fđã chọn &e" + victim.getName() + " &7(" + a.victimdan.get(victim.getName()) + ")"));
									}
								}
								a.voteDan.put(p.getName(), victim.getName());
							}
						} else {
							a.voteDan.put(p.getName(), victim.getName());
							if(!a.victimdan.containsKey(victim.getName())) {
								a.victimdan.put(victim.getName(), 1);
								for(String soi : a.getPlayers()) {
									Bukkit.getPlayer(soi).sendMessage(ChatUtils.color("&c" + p.getName() + " &fđã chọn &e" + victim.getName() + " &7(1)"));
								}
							} else {
								a.victimdan.put(victim.getName(), a.victimdan.get(victim.getName()) + 1);
								for(String soi : a.getPlayers()) {
									Bukkit.getPlayer(soi).sendMessage(ChatUtils.color("&c" + p.getName() + " &fđã chọn &e" + victim.getName() + " &7(" + a.victimdan.get(victim.getName()) + ")"));
								}
							}
						}
					}
			        p.closeInventory();
				}
				if (i.getItemMeta().getLore().contains("§eChuột phải để cứu") && e.getClick() == ClickType.RIGHT) {
					Player victim = Bukkit.getPlayer(ChatColor.stripColor(name));
					if(a.getPhuthuy().contains(p.getName()) && a.victimphuthuycuu.isEmpty() && a.lockedPhuthuy.isEmpty() && a.getState() == GameState.NIGHT && a.getRole() == Role.PHUTHUY) {
						if(a.hasCuu.isEmpty()) {
							if(a.victimsoi.containsKey(victim.getName())) {
								a.victimphuthuycuu.add(victim.getName());
								a.lockedPhuthuy.add(p.getName());
								a.hasCuu.add(victim.getName());
								FBar.sendTitleBar(p, "", ChatUtils.color("&aBạn đã cứu " + victim.getName()));
							} else {
								p.sendMessage(ChatUtils.color("&e" + victim.getName() + "&f không bị chết!"));
							}
						} else {
							p.sendMessage(ChatUtils.color("&cBạn đã hết bình cứu!"));
						}
					}
					p.closeInventory();
				}
				
			}
			catch (Exception ex) {ex.printStackTrace();}
		
		}
	}
	
	
}
