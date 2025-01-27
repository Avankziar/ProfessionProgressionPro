package me.avankziar.base.velocity.database;

import me.avankziar.base.general.database.MysqlBaseHandler;
import me.avankziar.base.velocity.BM;

public class MysqlHandler extends MysqlBaseHandler
{	
	public MysqlHandler(BM plugin)
	{
		super(plugin.getLogger(), plugin.getMysqlSetup());
	}
}
