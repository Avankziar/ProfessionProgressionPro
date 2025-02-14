package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class GrindstoneListener implements Listener
{
	final private static EventType GR = EventType.GRINDING;
	@EventHandler
	public void onGrindstone(InventoryClickEvent event)
	{
		if(event.isCancelled()
				|| event.getCurrentItem() == null
				|| event.getClickedInventory() == null
				|| event.getClickedInventory().getType() != InventoryType.GRINDSTONE
				|| !(event.getClickedInventory() instanceof GrindstoneInventory)
				|| !(event.getWhoClicked() instanceof Player)
				|| event.getWhoClicked().getGameMode() == GameMode.CREATIVE
				|| event.getWhoClicked().getGameMode() == GameMode.SPECTATOR
				|| event.getSlotType() != SlotType.RESULT)
		{
			return;
		}
		final Player player = (Player) event.getWhoClicked();
		final UUID uuid = player.getUniqueId();
		final Location loc = event.getClickedInventory().getLocation();
		final Material mat = event.getCurrentItem().getType();
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, GR, mat);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, GR, mat);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), GR, mat, 1, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());	
	}
}