package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class ItemBreakListener implements Listener
{	
	final private static EventType IB = EventType.ITEM_BREAKING;
	@EventHandler
	public void onItemBreak(PlayerItemBreakEvent event)
	{
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR)
		{
			return;
		}
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final Location loc = player.getLocation();
		final Material mat = event.getBrokenItem().getType();
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, IB, mat);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, IB, mat);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), IB, mat, 1, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
}