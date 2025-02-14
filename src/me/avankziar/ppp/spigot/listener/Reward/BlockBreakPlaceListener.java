package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.assistance.TimeHandler;
import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.general.objects.PlacedBlock;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.CompensationHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class BlockBreakPlaceListener implements Listener
{
	private static EventType BB = EventType.BREAKING;
	private static EventType BP = EventType.PLACING;
	private static boolean TRACK_PLACED_BLOCKS = PPP.getPlugin().getYamlHandler()
			.getConfig().getBoolean("Reward.TrackPlacedBlocks", true);
	private static boolean REWARD_IF_MANUALLY_PLACED_BEFORE = PPP.getPlugin().getYamlHandler()
			.getConfig().getBoolean("Reward.IfPlacedBlocksManually", false);
	private static long EXPIRATION_DATE = TimeHandler.getTiming(PPP.getPlugin().getYamlHandler()
			.getConfig().getString("Reward.TimeWhenPlacedBlockStatusExpire"));
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		if(event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR
				|| (PPP.getWorldGuard() ? 
						WorldGuardHook.compensationDeactive(event.getPlayer(), event.getBlock().getLocation())
						: false)
				)
		{
			return;
		}
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final Material mat = event.getBlock().getType();
		final Location loc = event.getBlock().getLocation();
		if(CompensationHandler.dropItems(uuid, BB, mat))
		{
			event.setDropItems(false);
		}
		new BukkitRunnable() 
		{	
			@Override
			public void run() 
			{
				if(TRACK_PLACED_BLOCKS)
				{
					PlacedBlock.delete(loc);
					if(PlacedBlock.wasPlaced(loc) && !REWARD_IF_MANUALLY_PLACED_BEFORE)
					{
						return;
					}
				}
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, BB, mat);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, BB, mat);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), BB, mat, 1, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if(event.isCancelled()
				|| !event.canBuild()
				|| event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR
				|| (PPP.getWorldGuard() ? 
						WorldGuardHook.compensationDeactive(event.getPlayer(), event.getBlock().getLocation())
						: false))
		{
			return;
		}
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final Material mat = event.getBlock().getType();
		final Location loc = event.getBlock().getLocation();
		double typeamount = 1;
		new BukkitRunnable() 
		{	
			@Override
			public void run() 
			{
				if(TRACK_PLACED_BLOCKS)
				{
					PPP.getPlugin().getSQLiteHandler().create(new PlacedBlock(0,
									loc.getWorld().getName(),
									loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(),
									System.currentTimeMillis()+EXPIRATION_DATE));
				}
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, BP, mat);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, BP, mat);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), BP, mat, typeamount, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
}