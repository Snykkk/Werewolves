package Snykkk.api;

import java.lang.reflect.Field;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class FSkull {
	
	@SuppressWarnings("deprecation")
	public static ItemStack byName(String skullname) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		
		SkullMeta sm = (SkullMeta) head.getItemMeta();
		
		sm.setOwner(skullname);
		
		head.setItemMeta(sm);
		
		return head;
	}

	public static ItemStack byURL(String skinURL) {
		
		if (!skinURL.contains("http")) {
			skinURL = "http://textures.minecraft.net/texture/" + skinURL;
		}
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		
		if (skinURL.isEmpty()) {return head;}

		ItemMeta headMeta = head.getItemMeta();
		
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		
		byte[] encodedData = 
				Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", skinURL).getBytes());
		
		profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
		
		Field profileField = null;
		
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
		}
		catch (NoSuchFieldException | SecurityException ex) {}
		profileField.setAccessible(true);
		try {profileField.set(headMeta, profile);
		
		}
		catch (IllegalArgumentException | IllegalAccessException ex) {}
		
		head.setItemMeta(headMeta);
		
		return head;
	}
	
	public static ItemStack byValue(String skinValue) {
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		
		if (skinValue.isEmpty()) {return head;}

		ItemMeta headMeta = head.getItemMeta();
		
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		
		profile.getProperties().put("textures", new Property("textures", skinValue));
		
		Field profileField = null;
		
		try {
			profileField = headMeta.getClass().getDeclaredField("profile");
		}
		catch (NoSuchFieldException | SecurityException ex) {}
		profileField.setAccessible(true);
		try {profileField.set(headMeta, profile);
		
		}
		catch (IllegalArgumentException | IllegalAccessException ex) {}
		
		head.setItemMeta(headMeta);
		
		return head;
	}
}
