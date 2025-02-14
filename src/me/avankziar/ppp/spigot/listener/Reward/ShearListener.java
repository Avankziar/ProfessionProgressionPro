package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class ShearListener implements Listener
{
	final private static EventType SH = EventType.SHEARING;
	
	@EventHandler
	public void onShearing(PlayerShearEntityEvent event)
	{
		if(event.isCancelled()
				|| event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR)
		{
			return;
		}
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final Location loc = event.getEntity().getLocation();
		final EntityType ent = event.getEntity().getType();
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, SH, ent);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, SH, ent);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), SH, ent, 1, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
}