package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class EnchantListener implements Listener
{
	final private static EventType EN = EventType.ENCHANTING;
	
	@EventHandler
	public void onEnchant(EnchantItemEvent event)
	{
		if(event.isCancelled()
				|| event.getEnchanter().getGameMode() == GameMode.CREATIVE
				|| event.getEnchanter().getGameMode() == GameMode.SPECTATOR
				|| event.getItem() == null)
		{
			return;
		}
		final Player player = event.getEnchanter();
		final UUID uuid = player.getUniqueId();
		final Location loc = event.getEnchantBlock().getLocation();
		final Material mat = event.getItem().getType();
		new BukkitRunnable()
		{		
			@Override
			public void run()
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, EN, mat);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, EN, mat);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), EN, mat, 1, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
}