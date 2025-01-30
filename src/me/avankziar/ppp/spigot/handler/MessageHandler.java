package me.avankziar.ppp.spigot.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.avankziar.ppp.general.assistance.ChatApi;
import me.avankziar.ppp.spigot.PPP;
import net.md_5.bungee.api.ChatMessageType;

public class MessageHandler 
{
	public static void sendMessage(CommandSender sender, String...array)
	{
		Arrays.asList(array).stream().forEach(x -> sender.spigot().sendMessage(ChatApi.tl(x)));
	}
	
	public static void sendMessage(UUID uuid, String...array)
	{
		Player player = Bukkit.getPlayer(uuid);
		if(player != null)
		{
			Arrays.asList(array).stream().forEach(x -> player.spigot().sendMessage(ChatApi.tl(x)));
			return;
		}
		if(PPP.getPlugin().getMtV() == null)
		{
			return;
		}
		PPP.getPlugin().getMtV().sendMessage(uuid, array);
	}
	
	public static void sendMessage(Collection<UUID> uuids, String...array)
	{
		uuids.stream().forEach(x -> sendMessage(x, array));
	}
	
	public static void sendMessage(String...array)
	{
		if(PPP.getPlugin().getMtV() != null)
		{
			PPP.getPlugin().getMtV().sendMessage(array);
			return;
		} else
		{
			Bukkit.getOnlinePlayers().stream().forEach(x -> sendMessage(x.getUniqueId(), array));
			return;
		}
	}
	
	public static void sendActionBar(UUID uuid, String...array)
	{
		Player player = Bukkit.getPlayer(uuid);
		if(player != null)
		{
			Arrays.asList(array).stream().forEach(x -> player.spigot().sendMessage(ChatMessageType.ACTION_BAR, ChatApi.tl(x)));
			return;
		}
	}
}