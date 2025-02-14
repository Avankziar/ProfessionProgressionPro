package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class CraftItemListener implements Listener
{
	final private static EventType CR = EventType.CRAFTING;
	
	@EventHandler //InventoryClickEvent
	public void onCrafItem(CraftItemEvent event)
	{
		if(event.isCancelled()
				|| !(event.getWhoClicked() instanceof Player)
				|| event.getSlotType() != SlotType.RESULT
				|| event.getClickedInventory() == null
				|| (event.getClickedInventory().getType() != InventoryType.WORKBENCH && event.getClickedInventory().getType() != InventoryType.CRAFTING)
				|| event.getWhoClicked().getGameMode() == GameMode.CREATIVE
				|| event.getWhoClicked().getGameMode() == GameMode.SPECTATOR
				|| event.getCurrentItem() == null)
		{
			return;
		}
		final ItemStack result = event.getCurrentItem().clone();
		if(result == null) //If the result item is null, deny all and return.
		{
			event.setResult(Result.DENY);
			event.setCurrentItem(null);
			event.setCancelled(true);
			return;
		}
		final int amount = result.getAmount();
		final Material mat = result.getType();
		final Player player = (Player) event.getWhoClicked();
		final UUID uuid = player.getUniqueId();
		final Location loc = event.getClickedInventory().getLocation();
		if (event.getClick() == ClickType.SHIFT_LEFT 
				|| event.getClick() == ClickType.SHIFT_RIGHT 
				|| event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY)
		{
			postDetectionAll(player, uuid, result, mat, result.getItemMeta(), loc);
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
							BoosterHandler.getBoosterExperience(player, CR, mat);
					double moneyfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierMoney(player, loc) 
									: 1) *
							BoosterHandler.getBoosterMoney(player, CR, mat);
					RewardHandler.addReward(uuid, loc.getWorld().getName(), CR, mat, amount, moneyfactor, expfactor);
				}
			}.runTaskAsynchronously(PPP.getPlugin());
		}
	}
	
	public static void postDetectionAll(final Player player, final UUID uuid,
			final ItemStack pr, final Material premat, final ItemMeta premeta, Location loc)
	{ //Da final ItemStack pr null zurücklierfert, holen wir die nicht null Material und ItemMeta.
		
		final ItemStack[] preInv = player.getInventory().getContents();
		for (int i = 0; i < preInv.length; i++) //als Sicherung erstellen wir vom PreInv eine Kopie
		{
			preInv[i] = preInv[i] != null ? preInv[i].clone() : null;
		}
		int preInvcount = 0;
		for(ItemStack is : preInv)
		{
			if(isSameItem(premat, premeta, is))
			{
				preInvcount = preInvcount + is.getAmount();
			}
		}
		final int preCount = preInvcount;
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				final ItemStack[] postInv = player.getInventory().getContents();
				int newCount = 0;
				
				for (ItemStack post : postInv)
				{
					if(isSameItem(premat, premeta, post))
					{
						newCount = newCount + post.getAmount();
					}
				}
				int newItemsCount = newCount-preCount;
				if (newItemsCount < 0)
				{
			        return;
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
								BoosterHandler.getBoosterExperience(player, CR, premat);
						double moneyfactor = 1 * 
								(PPP.getWorldGuard() 
										? WorldGuardHook.getMultiplierMoney(player, loc) 
										: 1) *
								BoosterHandler.getBoosterMoney(player, CR, premat);
						RewardHandler.addReward(uuid, loc.getWorld().getName(), CR, premat, newItemsCount, moneyfactor, expfactor);
					}
				}.runTaskAsynchronously(PPP.getPlugin());
			}
		}.runTaskLater(PPP.getPlugin(), 1L);
	}
	
	public static boolean isSameItem(Material mat, ItemMeta meta, ItemStack other)
	{
        if(other == null
        		|| mat != other.getType())
        {
        	return false;
        }
        if((other.getItemMeta() == null && meta != null)
        		|| other.getItemMeta() != null && meta == null)
        {
        	return false;
        }
        if(other.getItemMeta() != null && (meta != null))
        {
        	if(!meta.toString().equals(other.getItemMeta().toString()))
            {
            	return false;
            }
        }
        return true;
    }
}