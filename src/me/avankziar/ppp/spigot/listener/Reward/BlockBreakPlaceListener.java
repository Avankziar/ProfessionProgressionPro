package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.CompensationHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class BlockBreakPlaceListener implements Listener
{
	private static EventType BB = EventType.BREAKING;
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR
				|| (PPP.getWorldGuard() ? 
						WorldGuardHook.compensationDeactive(event.getPlayer(), event.getBlock().getLocation())
						: false)
				)
		{
			return;
		}
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final Material mat = event.getBlock().getType();
		final Location loc = event.getBlock().getLocation();
		double typeamount = 1;
		if(CompensationHandler.dropItems(uuid, BB, mat))
		{
			event.setDropItems(false);
		}
		new BukkitRunnable() 
		{	
			@Override
			public void run() 
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, BB, mat);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, BB, mat);
				RewardHandler.addReward(uuid, BB, mat, typeamount, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
}