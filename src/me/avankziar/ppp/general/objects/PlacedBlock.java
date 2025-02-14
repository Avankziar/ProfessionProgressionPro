package me.avankziar.ppp.general.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Location;

import me.avankziar.ppp.general.database.QueryType;
import me.avankziar.ppp.general.database.SQLiteHandable;
import me.avankziar.ppp.general.database.SQLiteHandler;
import me.avankziar.ppp.general.database.SQLiteSetup;
import me.avankziar.ppp.general.database.ServerType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.database.MysqlHandler;

public class PlacedBlock implements SQLiteHandable<PlacedBlock>
{
	private int id;
	private String world;
	private int x;
	private int y;
	private int z;
	private long expirationDate;
	
	public PlacedBlock(){}
	
	public PlacedBlock(int id, String world, int x, int y, int z, long expirationDate)
	{
		setId(id);
		setWorld(world);
		setX(x);
		setY(y);
		setZ(z);
		setExpirationDate(expirationDate);
	}
	
	public ServerType getServerType()
	{
		return ServerType.ALL;
	}
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getWorld()
	{
		return world;
	}

	public void setWorld(String world)
	{
		this.world = world;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getZ()
	{
		return z;
	}

	public void setZ(int z)
	{
		this.z = z;
	}

	public long getExpirationDate()
	{
		return expirationDate;
	}

	public void setExpirationDate(long expirationDate)
	{
		this.expirationDate = expirationDate;
	}
	
	public static boolean wasPlaced(Location loc)
	{
		return PPP.getPlugin().getSQLiteHandler().exist(new PlacedBlock(),
				"`world` = ? AND `x` = ? AND `y` = ? AND `z` = ?",
				loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public static void delete(Location loc)
	{
		PPP.getPlugin().getSQLiteHandler().deleteData(new PlacedBlock(),
				"`world` = ? AND `x` = ? AND `y` = ? AND `z` = ?",
				loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	@Override
	public String getMysqlTableName() 
	{
		return "PPPPlacedBlock";
	}
	
	public boolean setupMysql(SQLiteSetup mysqlSetup, ServerType serverType)
	{
		StringBuilder sql = new StringBuilder();
		sql.append( "CREATE TABLE IF NOT EXISTS `" + getMysqlTableName()
		+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
		+ " world text,"
		+ " x int,"
		+ " y int,"
		+ " z int,"
		+ " expiration_date BIGINT);");
		return mysqlSetup.baseSetup(sql.toString());
	}

	@Override
	public boolean create(Connection conn)
	{
		try
		{
			String sql = "INSERT INTO `" + getMysqlTableName()
					+ "`(`world`, `x`, `y`, `z`, `expiration_date`) " 
					+ "VALUES(?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getWorld());
	        ps.setInt(2, getX());
	        ps.setInt(3, getY());
	        ps.setInt(4, getZ());
	        ps.setLong(5, getExpirationDate());
	        int i = ps.executeUpdate();
	        MysqlHandler.addRows(QueryType.INSERT, i);
	        return true;
		} catch (SQLException e)
		{
			this.log(SQLiteHandler.getLogger(), Level.WARNING, "SQLException! Could not create a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public boolean update(Connection conn, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "UPDATE `" + getMysqlTableName()
				+ "` SET `world` = ?, `x` = ?, `y` = ?, `z` = ?,"
				+ " `expiration_date` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getWorld());
		    ps.setInt(2, getX());
		    ps.setInt(3, getY());
		    ps.setInt(4, getZ());
		    ps.setLong(5, getExpirationDate());
			int i = 6;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}			
			int u = ps.executeUpdate();
			MysqlHandler.addRows(QueryType.UPDATE, u);
			return true;
		} catch (SQLException e)
		{
			this.log(SQLiteHandler.getLogger(), Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<PlacedBlock> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "SELECT * FROM `" + getMysqlTableName()
				+ "` WHERE "+whereColumn+" ORDER BY "+orderby+limit;
			PreparedStatement ps = conn.prepareStatement(sql);
			int i = 1;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}
			
			ResultSet rs = ps.executeQuery();
			MysqlHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
			ArrayList<PlacedBlock> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new PlacedBlock(rs.getInt("id"),
						rs.getString("world"),
						rs.getInt("x"),
						rs.getInt("y"),
						rs.getInt("z"),
						rs.getLong("expiration_date")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(SQLiteHandler.getLogger(), Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
}