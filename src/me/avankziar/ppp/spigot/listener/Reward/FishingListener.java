package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class FishingListener implements Listener
{	
	final private static EventType FI = EventType.FISHING;
	@EventHandler
	public void onFishing(PlayerFishEvent event)
	{
		if(event.isCancelled()
				|| event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR
				|| event.getState() != State.CAUGHT_FISH)
		{
			return;
		}
		ItemStack ismat = null;
		Entity e = event.getCaught();
		if(e != null && e instanceof Item)
		{
			Item it = (Item) e;
			ismat = it.getItemStack();
		} else
		{
			return;
		}
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final Location loc = event.getCaught().getLocation();
		final Material mat = ismat.getType();
		final int amount = ismat.getAmount();
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, FI, mat);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, FI, mat);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), FI, mat, amount, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
}