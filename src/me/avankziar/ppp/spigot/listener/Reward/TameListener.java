package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class TameListener implements Listener
{
	final private static EventType TA = EventType.TAMING;
	@EventHandler
	public void onEntityTame(EntityTameEvent event)
	{
		if(!(event.getOwner() instanceof Player)
				|| event.getEntity().isDead()
				|| ((Player) event.getOwner()).getGameMode() == GameMode.CREATIVE
				|| ((Player) event.getOwner()).getGameMode() == GameMode.SPECTATOR)
		{
			return;
		}
		final Player player = (Player) event.getOwner();
		final UUID uuid = player.getUniqueId();
		final Location loc = event.getEntity().getLocation();
		final EntityType ent = event.getEntityType();
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, TA, ent);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, TA, ent);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), TA, ent, 1, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
}