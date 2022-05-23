package Snykkk.api;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;

public class FBar_1_12 {

	static void sendActionBar(Player p, String message) {
        CraftPlayer cp = (CraftPlayer) p;
        IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, ChatMessageType.GAME_INFO);
        cp.getHandle().playerConnection.sendPacket(ppoc);
    }
	
	static void sendTitleBar(Player p, String title, String subtitle) {
		CraftPlayer cp = (CraftPlayer) p;
        PacketPlayOutTitle packettitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + title + "\"}"), 0, 12, 0);
        cp.getHandle().playerConnection.sendPacket(packettitle);
        
        PacketPlayOutTitle packetsubtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"" + subtitle + "\"}"), 0, 12, 0);
        cp.getHandle().playerConnection.sendPacket(packetsubtitle);
	}
}
