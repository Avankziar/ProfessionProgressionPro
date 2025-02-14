package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class StatisticIncrementListener implements Listener
{
	@EventHandler
	public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event)
	{
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR
				|| (PPP.getWorldGuard() ? 
						WorldGuardHook.compensationDeactive(event.getPlayer(), event.getPlayer().getLocation())
						: false)
				)
		{
			return;
		}
		EventType et;
		switch(event.getStatistic())
		{
		default: return;
		case CLIMB_ONE_CM: et = EventType.CLIMBING; break;
		case CROUCH_ONE_CM: et = EventType.CROUCHING; break;
		case FALL_ONE_CM: et = EventType.FALLING; break;
		case FLY_ONE_CM: et = EventType.FLYING; break;
		case JUMP: et = EventType.JUMPING; break;
		case WALK_ONE_CM: et = EventType.WALKING_ON_EARTH; break;
		case WALK_ON_WATER_ONE_CM: et = EventType.WALKING_ON_WATER; break;
		case WALK_UNDER_WATER_ONE_CM: et = EventType.WALKING_UNDER_WATER; break;
		}
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final Material mat = Material.AIR;
		final Location loc = event.getPlayer().getLocation();
		double typeamount = event.getNewValue()-event.getPreviousValue();
		new BukkitRunnable() 
		{	
			@Override
			public void run() 
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, et, mat);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, et, mat);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), et, mat, typeamount, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
}