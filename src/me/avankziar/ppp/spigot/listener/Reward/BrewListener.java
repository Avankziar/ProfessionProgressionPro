package me.avankziar.ppp.spigot.listener.Reward;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BlockHandler;
import me.avankziar.ppp.spigot.handler.BlockHandler.BlockType;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class BrewListener implements Listener
{
	final private static EventType BR = EventType.BREWING;
	
	@EventHandler
	public void onBrewingFinish(BrewEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		final UUID uuid = BlockHandler.getRegisterBlockOwner(BlockType.BREWING_STAND, event.getBlock().getLocation());
		if(uuid == null)
		{
			return;
		}
		final Player player = Bukkit.getPlayer(uuid);
		final Location loc = event.getBlock().getLocation();
		HashSet<ItemStack> set = new HashSet<>();
		for(ItemStack is : event.getContents().getContents())
		{
			if(is != null)
			{
				if(is.getType() != Material.POTION && is.getType() != Material.LINGERING_POTION
						&& is.getType() != Material.SPLASH_POTION)
				{
					continue;
				}
				set.add(is);
			}
		}
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				for(ItemStack is : set)
				{
					double expfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierPEXP(player, loc) 
									: 1) *
							BoosterHandler.getBoosterExperience(player, BR, is.getType());
					double moneyfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierMoney(player, loc) 
									: 1) *
							BoosterHandler.getBoosterMoney(player, BR, is.getType());
					RewardHandler.addReward(uuid, loc.getWorld().getName(), BR, is.getType(), is.getAmount(), moneyfactor, expfactor);
				}
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
}