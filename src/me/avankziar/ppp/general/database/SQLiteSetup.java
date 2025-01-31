package me.avankziar.ppp.general.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import me.avankziar.ppp.spigot.PPP;

public class SQLiteSetup
{
	private String database = "technologytree";
	
	public SQLiteSetup()
	{
		loadSQLitelSetup();
	}
	
	public boolean connectToDatabase() 
	{
		PPP.logger.info("Connecting to the SQLite database...");
		try
		{
			getConnection();
			PPP.logger.info("SQLite Database connection successful!");
			return true;
		} catch(Exception e) 
		{
			PPP.logger.log(Level.WARNING, "Could not connect to SQLite Database!", e);
			return false;
		}		
	}
	
	public Connection getConnection() throws SQLException
	{
		return reConnect();
	}
	
	private Connection reConnect() throws SQLException
	{
		File directory = new File(PPP.getPlugin().getDataFolder()+"/SQLite/");
		if(!directory.exists())
		{
			directory.mkdir();
		}
		File db = new File(directory.getPath(), database+".db");
		if(!db.exists())
		{
			try
			{
				db.createNewFile();
			} catch (IOException e)
			{
				PPP.logger.log(Level.WARNING, "Could not build db file!", e);
				e.printStackTrace();
			}
		}
		boolean bool = false;
	    try
	    {
	    	// Load new Drivers for papermc
	    	Class.forName("com.mysql.cj.jdbc.Driver");
	    	bool = true;
	    } catch (Exception e)
	    {
	    	bool = false;
	    } 
	    if (bool == false)
    	{
    		// Load old Drivers for spigot
    		try
    		{
    			Class.forName("com.mysql.jdbc.Driver");
    		}  catch (Exception e) {}
    	}
        //Connect to database
        return DriverManager.getConnection("jdbc:sqlite:" + db);
	}
	
	public boolean baseSetup(String data) 
	{
		try (Connection conn = getConnection(); PreparedStatement query = conn.prepareStatement(data))
		{
			query.execute();
		} catch (SQLException e) 
		{
			PPP.logger.log(Level.WARNING, "Could not build SQLite data source. Or connection is null", e);
		}
		return true;
	}
	
	public boolean loadSQLitelSetup()
	{
		if(!connectToDatabase())
		{
			return false;
		}
		return true;
	}
}
