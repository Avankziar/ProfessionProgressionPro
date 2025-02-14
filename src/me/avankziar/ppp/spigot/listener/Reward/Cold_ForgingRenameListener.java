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
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class Cold_ForgingRenameListener implements Listener
{
	final private static EventType RN = EventType.RENAMING;
	final private static EventType CF = EventType.COLD_FORGING;
	@EventHandler
	public void onAnvil(InventoryClickEvent event)
	{
		if(event.isCancelled()
				|| event.getClickedInventory() == null
				|| event.getCurrentItem() == null
				|| !(event.getClickedInventory() instanceof AnvilInventory)
				|| event.getClickedInventory().getType() != InventoryType.ANVIL
				|| event.getSlotType() != SlotType.RESULT
				|| !(event.getWhoClicked() instanceof Player)
				|| event.getWhoClicked().getGameMode() == GameMode.CREATIVE
				|| event.getWhoClicked().getGameMode() == GameMode.SPECTATOR
				|| !(event.getView() instanceof AnvilView)
				)
		{
			return;
		}
		final Player player = (Player) event.getWhoClicked();
		final UUID uuid = player.getUniqueId();
		AnvilInventory ai = (AnvilInventory) event.getClickedInventory();
		AnvilView av = (AnvilView) event.getView();
		ItemStack base = ai.getContents()[0];
		ItemStack add = ai.getContents()[1];
		ItemStack result = event.getCurrentItem().clone();
		final Material mat = result.getType();
		final Location loc = event.getClickedInventory().getLocation();
		if(add == null
				&& base.hasItemMeta()
				&& (	(base.getItemMeta().hasDisplayName()
						&& !base.getItemMeta().getDisplayName().equals(av.getRenameText())
						)
						|| !base.getItemMeta().hasDisplayName())
				)
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{

					double expfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierPEXP(player, loc) 
									: 1) *
							BoosterHandler.getBoosterExperience(player, RN, mat);
					double moneyfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierMoney(player, loc) 
									: 1) *
							BoosterHandler.getBoosterMoney(player, RN, mat);
					RewardHandler.addReward(uuid, loc.getWorld().getName(), RN, mat, result.getAmount(), moneyfactor, expfactor);
				}
			}.runTaskAsynchronously(PPP.getPlugin());
		} else
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					double expfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierPEXP(player, loc) 
									: 1) *
							BoosterHandler.getBoosterExperience(player, CF, mat);
					double moneyfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierMoney(player, loc) 
									: 1) *
							BoosterHandler.getBoosterMoney(player, CF, mat);
					RewardHandler.addReward(uuid, loc.getWorld().getName(), CF, mat, 1, moneyfactor, expfactor);
				}
			}.runTaskAsynchronously(PPP.getPlugin());
		}
	}
}