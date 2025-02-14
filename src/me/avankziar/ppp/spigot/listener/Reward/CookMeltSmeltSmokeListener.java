package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BlockHandler;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class CookMeltSmeltSmokeListener implements Listener
{	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onFurnaceSmelt(BlockCookEvent event)
	{
		EventType et = BlockHandler.getEventType(event.getBlock().getType());
		if(event.isCancelled() 
				|| event.getBlock() == null 
				|| event.getResult() == null
				|| et == null)
		{
			return;
		}
		final UUID uuid = BlockHandler.getRegisterBlockOwner(BlockHandler.getBlockType(event.getBlock().getType()), event.getBlock().getLocation());
		if(uuid == null)
		{
			return;
		}
		final Player player = Bukkit.getPlayer(uuid);
		final Location loc = event.getBlock().getLocation();
		final Material mat = event.getResult().getType();
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
				RewardHandler.addReward(uuid, loc.getWorld().getName(), et, mat, event.getResult().getAmount(), moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
}