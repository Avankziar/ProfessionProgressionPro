package me.avankziar.ppp.spigot.database;

import me.avankziar.ppp.general.database.MysqlBaseHandler;
import me.avankziar.ppp.spigot.PPP;

public class MysqlHandler extends MysqlBaseHandler
{	
	public MysqlHandler(PPP plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}
