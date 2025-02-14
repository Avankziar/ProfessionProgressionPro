package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecutterInventory;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class StoneCutterListener implements Listener
{
	final private static EventType SC = EventType.STONECUTTING;

	@EventHandler
	public void onStoneCutter(InventoryClickEvent event)
	{
		if(event.getClickedInventory() == null
				|| event.getCurrentItem() == null
				|| !(event.getClickedInventory() instanceof StonecutterInventory)
				|| event.getSlotType() != SlotType.RESULT)
		{
			return;
		}
		final Material mat = event.getCurrentItem().getType();
		final ItemStack result = event.getCurrentItem().clone();
		final Player player = (Player) event.getWhoClicked();
		final UUID uuid = player.getUniqueId();
		final Location loc = event.getClickedInventory().getLocation();
		final int amount = result.getAmount();
		if (event.getClick() == ClickType.SHIFT_LEFT 
				|| event.getClick() == ClickType.SHIFT_RIGHT 
				|| event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
		{
			CraftItemListener.postDetectionAll(player, uuid, result, mat, result.getItemMeta(), loc);
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
							BoosterHandler.getBoosterExperience(player, SC, mat);
					double moneyfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierMoney(player, loc) 
									: 1) *
							BoosterHandler.getBoosterMoney(player, SC, mat);
					RewardHandler.addReward(uuid, loc.getWorld().getName(), SC, mat, amount, moneyfactor, expfactor);
				}
			}.runTaskAsynchronously(PPP.getPlugin());
		}
	}
}