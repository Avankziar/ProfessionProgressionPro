package me.avankziar.ppp.spigot.listener.Reward;

import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BlockHandler;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class DryingListener implements Listener
{
	private static LinkedHashMap<String, String> placedSponge = new LinkedHashMap<>();
	final private static EventType DR = EventType.DRYING;
	
	@EventHandler(priority = EventPriority.LOW)
	public void onSponePlace(BlockPlaceEvent event)
	{
		if(event.isCancelled()
				|| event.getBlock().getType() != Material.SPONGE
				|| event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR)
		{
			return;
		}
		placedSponge.put(BlockHandler.getLocationText(event.getBlock().getLocation()), event.getPlayer().getUniqueId().toString());
	}
	
	@EventHandler
	public void onSponeAbsorb(SpongeAbsorbEvent event)
	{
		if(event.isCancelled())
		{
			return;
		}
		final Location loc = event.getBlock().getLocation();
		final int size = event.getBlocks().size();
		final Material mat = Material.SPONGE;
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				final UUID uuid = UUID.fromString(placedSponge.get(BlockHandler.getLocationText(loc)));
				if(uuid == null)
				{
					return;
				}
				Player player = Bukkit.getPlayer(uuid);
				if(player == null
						|| player.getGameMode() == GameMode.CREATIVE
						|| player.getGameMode() == GameMode.SPECTATOR)
				{
					return;
				}
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, DR, mat);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, DR, mat);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), DR, mat, size, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
}