package Snykkk.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;


public class FItem {
	
	ItemStack i = null;
	Material type = Material.AIR;
	int typeid = 0;
	String name = "";
	List<String> lore = new LinkedList<String>();
	byte data = 0;
	int amount = 1;
	Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();

	boolean glow = false;
	
	boolean hideenchant = false;
	boolean hideattributes = false;
	boolean unbreak = false;
	boolean hideall = false;
	
	Color color = null;
	boolean leather_armor = false;
	boolean firework_charge =false;

	@SuppressWarnings("deprecation")
	public FItem(Material type) {

		this.type = type;
		this.typeid = type.getId();
	}
	@SuppressWarnings("deprecation")
	public FItem(int typeid) {
		this.typeid = typeid;
		this.type = Material.getMaterial(typeid);

	}
	@SuppressWarnings("deprecation")
	public FItem(ItemStack i) {

		this.i = i;
		this.type = i.getType();
		this.data = i.getData().getData();
		this.amount = i.getAmount();
		
		if (i.hasItemMeta()) {
			ItemMeta im = i.getItemMeta();
			
			if (im.hasDisplayName()) {name = im.getDisplayName();}
			if (im.hasLore()) {lore = im.getLore();}
			if (im.hasEnchants()) {enchantments = im.getEnchants();}
		}
	}
	
	public FItem setType(Material a) {this.type = a; return this;}
	public FItem setType(boolean condition, Material a) {if (condition) {this.type = a;} return this;}
	
	public FItem setName(String s) {this.name = s;return this;}
	public FItem setName(boolean condition, String s) {if (condition) {this.name = s;} return this;}

	public FItem setData(int s) {this.data = (byte) s; return this;}
	public FItem setData(boolean condition, int s) {if (condition) {this.data = (byte) s;} return this;}
	
	public FItem setAmount(int s) {this.amount = s; return this;}
	public FItem setAmount(boolean condition, int s) {if (condition) {this.amount = s;} return this;}
	
	public FItem addLore(String s) {this.lore.add(s); return this;}
	public FItem addLore(boolean add, String s) {if (add) {this.lore.add(s);} return this;}
	
	public FItem addLore(double s) {this.lore.add(s + ""); return this;}
	
	
	public FItem insertLore(int index, String s) {this.lore.add(index, s); return this;}
	public FItem insertLore(boolean add, int index, String s) {if (add) {this.lore.add(index, s);} return this;}

	public FItem removeLore(String s) {this.lore.remove(s); return this;}
	public FItem removeLore(boolean condition, String s) {if (condition) {this.lore.remove(s);} return this;}
	
	public FItem setLore(List<String> s) {this.lore = s; return this;}
	public FItem setLore(boolean condition, List<String> s) {if (condition) {this.lore = s;} return this;}
	
	public FItem replaceLore(String from, String to) {
		List<String> newlore = new LinkedList<String>();
		for (String s : this.lore) {
			s = s.replaceAll(from, to);
			newlore.add(s);
		}
		this.lore = newlore;
		return this;
	}
	
	public String getName() {return this.name;}
	
	public List<String> getLore() {return this.lore;}
	
	public FItem addEnchant(Enchantment s, int lv) {enchantments.put(s, lv); return this;}
	public FItem addEnchant(boolean condition, Enchantment s, int lv) {if (condition) {enchantments.put(s, lv);} return this;}
	
	public FItem removeEnchant(Enchantment s) {enchantments.remove(s); return this;}
	public FItem removeEnchant(boolean condition, Enchantment s) {if (condition) {enchantments.remove(s);} return this;}
	
	public FItem setEnchant(Map<Enchantment, Integer> s) {this.enchantments = s; return this;}
	public FItem setEnchant(boolean condition, Map<Enchantment, Integer> s) {if (condition) {this.enchantments = s;} return this;}
	
	
	public FItem hideEnchant(boolean a) {hideenchant = a; return this;}
	public FItem hideAttributes(boolean a) {hideattributes = a; return this;}
	public FItem hideAll(boolean a) {hideall = a; return this;}
	public FItem setUnbreak(boolean a) {unbreak = a; return this;}
	
	public boolean hasEnchant() {return !this.enchantments.isEmpty();}
	
	public FItem glow(boolean a) {glow = a; return this;}
	
	public FItem setColor(Color c) {this.color = c; return this;}
	
	public FItem isLeatherArmor(boolean b) {this.leather_armor = b; return this;}
	public FItem isFireworkCharge(boolean b) {this.firework_charge = b; return this;}

	
	
	
	
	@SuppressWarnings("deprecation")
	public ItemStack toItemStack() {
		if (i == null) {i = new ItemStack(type, amount, data);}
		
		if (leather_armor && type.toString().startsWith("LEATHER_")) {
			LeatherArmorMeta im = (LeatherArmorMeta) i.getItemMeta();
			
			im.setColor(color);
			
			i.setItemMeta(im);
		}
		
		if (firework_charge && type == Material.FIREWORK_CHARGE) {
			FireworkEffectMeta im = (FireworkEffectMeta) i.getItemMeta();
			
			FireworkEffect aa = FireworkEffect.builder().withColor(color).build();
			
			im.setEffect(aa);

			i.setItemMeta(im);
		}
		
		ItemMeta im = i.getItemMeta();
		
		if (unbreak) {im.spigot().setUnbreakable(true);}

		if (hideenchant) im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		if (hideattributes) im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		if (glow) im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		if (hideall) {
			im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			im.addItemFlags(ItemFlag.HIDE_DESTROYS);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			im.addItemFlags(ItemFlag.HIDE_PLACED_ON);
			im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			im.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		

		
		if (!name.equals("")) {im.setDisplayName(name);}
		im.setLore(lore);
		
		i.setItemMeta(im);
		

		i.addUnsafeEnchantments(enchantments);
		
		
		if (glow) {i.addUnsafeEnchantment(Enchantment.DURABILITY, 1);}
		
		i.setAmount(amount);
		

		return i;
	}
	
	public FItem clone() {
		FItem fi = new FItem(this.type);
		fi.setAmount(this.amount);
		fi.setData(this.data);
		fi.setEnchant(this.enchantments);
		fi.setLore(this.lore);
		fi.setName(this.name);
		fi.setUnbreak(this.unbreak);
		fi.glow(this.glow);
		fi.hideAttributes(this.hideattributes);
		fi.hideEnchant(this.hideenchant);
		
		return fi;
	}
	
	
	public void onClick (InventoryClickEvent e) {
		
	}
}