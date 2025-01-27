package me.avankziar.base.spigot.database;

import me.avankziar.base.general.database.MysqlBaseHandler;
import me.avankziar.base.spigot.BM;

public class MysqlHandler extends MysqlBaseHandler
{	
	public MysqlHandler(BM plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}
