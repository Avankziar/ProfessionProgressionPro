package me.avankziar.ppp.spigot.listener;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.PlayerData;
import me.avankziar.ppp.general.objects.Profession;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.ProfessionHandler;

public class JoinLeaveListener implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
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
				if(pd != null)
				{
					if(!pd.getName().equals(name))
					{
						pd.setName(name);
						plugin.getMysqlHandler().updateData(pd, "`player_uuid` = ?", uuid.toString());
					}
				} else
				{
					pd = new PlayerData(0, uuid, name, 0, 0, 0);
					plugin.getMysqlHandler().create(pd);
				}
				ArrayList<Profession> pa = plugin.getMysqlHandler().getFullList(new Profession(), 
						"`id` ASC", "`player_uuid` = ? AND `is_active` = ?", uuid.toString(), true);
				pa.forEach(x ->
				{
					ProfessionHandler.addProfession(x);
				});
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
