package me.avankziar.base.velocity.assistance;

import java.util.concurrent.TimeUnit;

import me.avankziar.base.velocity.BM;

public class BackgroundTask 
{
	private BM plugin;
	
	public BackgroundTask(BM plugin)
	{
		this.plugin = plugin;
		runTask();
	}
	
	private void runTask()
	{
		plugin.getServer().getScheduler().buildTask(plugin, (task) ->
		{
			//Do something
		}).delay(1L, TimeUnit.MILLISECONDS).repeat(15L, TimeUnit.MILLISECONDS).schedule();
	}
	
	
}
