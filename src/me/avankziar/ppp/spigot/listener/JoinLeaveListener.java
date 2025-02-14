package me.avankziar.ppp.spigot.listener;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.PlayerData;
import me.avankziar.ppp.general.objects.PlayerData.RewardMessageType;
import me.avankziar.ppp.general.objects.Profession;
import me.avankziar.ppp.general.objects.RegisteredBlock;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BlockHandler;
import me.avankziar.ppp.spigot.handler.ProfessionHandler;
import me.avankziar.ppp.spigot.handler.ScoreboardHandler;
import me.avankziar.ppp.spigot.imports.ImportJobsReborn;

public class JoinLeaveListener implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		final Player player = event.getPlayer();
		final UUID uuid = event.getPlayer().getUniqueId();
		final String name = event.getPlayer().getName();
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				PPP plugin = PPP.getPlugin();
				PlayerData pd = plugin.getMysqlHandler().getData(
						new PlayerData(), "`player_uuid` = ?", uuid.toString());
				boolean scoreboard = false;
				if(pd != null)
				{
					scoreboard = pd.isScoreboardActive();
					if(!pd.getName().equals(name))
					{
						pd.setName(name);
						plugin.getMysqlHandler().updateData(pd, "`player_uuid` = ?", uuid.toString());
					}
				} else
				{
					pd = new PlayerData(0, uuid, name, 0, 0, 0, 0,
							RewardMessageType.valueOf(PPP.getPlugin().getYamlHandler()
									.getConfig().getString("Default.ProcessRewardMessageType")),
							false);
					plugin.getMysqlHandler().create(pd);
				}
				if(plugin.getYamlHandler().getConfig().getBoolean("Import.JobsReborn.Active", false))
				{
					ImportJobsReborn.importJobsReborn(player, pd);
				}
				for(RegisteredBlock rg : plugin.getMysqlHandler().getFullList(new RegisteredBlock(), "`id` ASC",
						"`player_uuid` = ? AND `server` = ?", uuid.toString(), plugin.getServername()))
				{
					BlockHandler.registerBlock(player, rg.getBlockType(), rg.getLocation(), false);
				}
				ArrayList<Profession> pa = plugin.getMysqlHandler().getFullList(new Profession(), 
						"`id` ASC", "`player_uuid` = ? AND `is_active` = ?", uuid.toString(), true);
				pa.forEach(x ->
				{
					ProfessionHandler.addProfession(x);
				});
				if(scoreboard)
				{
					new BukkitRunnable()
					{
						@Override
						public void run()
						{
							ScoreboardHandler.setScoreBoard(event.getPlayer());
						}
					}.runTask(plugin);
				}
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		final UUID uuid = event.getPlayer().getUniqueId();
		ProfessionHandler.removePlayer(uuid);
	}
}
