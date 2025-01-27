package me.avankziar.base.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;

import me.avankziar.base.velocity.BM;

public class JoinLeaveListener
{
	private BM plugin;
	
	public JoinLeaveListener(BM plugin)
	{
		this.plugin = plugin;
	}
	
	@Subscribe
	public void onPlayerJoin(PlayerChooseInitialServerEvent event)
	{
		
	}
	
	@Subscribe
	public void onPlayerQuit(DisconnectEvent event)
	{
		
	}
}