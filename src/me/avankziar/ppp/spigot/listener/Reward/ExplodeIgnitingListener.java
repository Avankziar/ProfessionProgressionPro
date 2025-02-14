package me.avankziar.ppp.spigot.listener.Reward;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BlockHandler;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class ExplodeIgnitingListener implements Listener
{
	private static LinkedHashMap<UUID, String> ignitingMap = new LinkedHashMap<>();
	final private static EventType IG = EventType.IGNITING;
	final private static EventType EX = EventType.EXPLODING;
	
	//Do not tracks TNT
	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent event)
	{
		if(event.isCancelled()
				|| event.getPlayer() == null
				|| event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR)
		{
			return;
		}
		ignitingMap.put(event.getPlayer().getUniqueId(), BlockHandler.getLocationText(event.getBlock().getLocation()));
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final Location loc = event.getBlock().getLocation();
		final Material mat = event.getBlock().getType();
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, IG, mat);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, IG, mat);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), IG, mat, 1, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
	
	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event)
	{
		if(event.isCancelled()
				|| !ignitingMap.containsValue(BlockHandler.getLocationText(event.getBlock().getLocation())))
		{
			return;
		}
		UUID uuids = null;
		for(Entry<UUID, String> entry : ignitingMap.entrySet())
		{
			if(entry.getValue().equals(BlockHandler.getLocationText(event.getBlock().getLocation())))
			{
				continue;
			}
			uuids = entry.getKey();
			break;
		}
		if(uuids == null)
		{
			return;
		}
		ignitingMap.remove(uuids);
		final Player player = Bukkit.getPlayer(uuids);
		final UUID uuid = player.getUniqueId();
		final Location loc = event.getBlock().getLocation();
		final Material mat = event.getBlock().getType();
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, EX, mat);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, EX, mat);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), EX, mat, 1, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
		
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if(event.isCancelled()
				|| event.getEntityType() != EntityType.TNT)
		{
			return;
		}
		UUID uuids = null;
		double d = 999_999_999_999.0;
		for(Entry<UUID, String> entry : ignitingMap.entrySet())
		{
			Location l = BlockHandler.getLocation(entry.getValue());
			if(!event.getLocation().getWorld().getName().equals(l.getWorld().getName()))
			{
				continue;
			}
			double xyz = Math.max(event.getLocation().getBlockX(), l.getBlockX()) - Math.min(event.getLocation().getBlockX(), l.getBlockX())
					+ Math.max(event.getLocation().getBlockY(), l.getBlockY()) - Math.min(event.getLocation().getBlockY(), l.getBlockY())
					+ Math.max(event.getLocation().getBlockZ(), l.getBlockZ()) - Math.min(event.getLocation().getBlockZ(), l.getBlockZ());
			if(xyz < 10 && xyz < d)
			{
				d = xyz;
				uuids = entry.getKey();
			}
		}
		if(uuids == null)
		{
			return;
		}
		ignitingMap.remove(uuids);
		final Player player = Bukkit.getPlayer(uuids);
		final UUID uuid = uuids;
		for(Block b : event.blockList())
		{
			final Material mat = b.getType();
			final Location loc = b.getLocation();
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					if(player == null)
					{
						return;
					}
					double expfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierPEXP(player, loc) 
									: 1) *
							BoosterHandler.getBoosterExperience(player, EX, mat);
					double moneyfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierMoney(player, loc) 
									: 1) *
							BoosterHandler.getBoosterMoney(player, EX, mat);
					RewardHandler.addReward(uuid, loc.getWorld().getName(), EX, mat, 1, moneyfactor, expfactor);
				}
			}.runTaskAsynchronously(PPP.getPlugin());
		}
	}
}