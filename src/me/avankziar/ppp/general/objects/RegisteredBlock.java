package me.avankziar.ppp.general.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import me.avankziar.ppp.general.database.MysqlBaseHandler;
import me.avankziar.ppp.general.database.MysqlBaseSetup;
import me.avankziar.ppp.general.database.MysqlTable;
import me.avankziar.ppp.general.database.QueryType;
import me.avankziar.ppp.general.database.ServerType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.database.MysqlHandler;
import me.avankziar.ppp.spigot.handler.BlockHandler.BlockType;

public class RegisteredBlock implements MysqlTable<RegisteredBlock>
{
	private int id;
	private UUID playerUUID;
	private BlockType blockType;
	private String server;
	private String world;
	private int blockX;
	private int blockY;
	private int blockZ;
	
	public RegisteredBlock(){}
	
	public RegisteredBlock(int id, UUID playerUUID, BlockType blockType, String server, String world, int blockX, int blockY, int blockZ)
	{
		setId(id);
		setPlayerUUID(playerUUID);
		setBlockType(blockType);
		setServer(server);
		setWorld(world);
		setBlockX(blockX);
		setBlockY(blockY);
		setBlockZ(blockZ);
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

	public UUID getPlayerUUID()
	{
		return playerUUID;
	}

	public void setPlayerUUID(UUID playerUUID)
	{
		this.playerUUID = playerUUID;
	}

	public BlockType getBlockType()
	{
		return blockType;
	}

	public void setBlockType(BlockType blockType)
	{
		this.blockType = blockType;
	}

	public String getServer()
	{
		return server;
	}

	public void setServer(String server)
	{
		this.server = server;
	}

	public String getWorld()
	{
		return world;
	}

	public void setWorld(String world)
	{
		this.world = world;
	}

	public int getBlockX()
	{
		return blockX;
	}

	public void setBlockX(int blockX)
	{
		this.blockX = blockX;
	}

	public int getBlockY()
	{
		return blockY;
	}

	public void setBlockY(int blockY)
	{
		this.blockY = blockY;
	}

	public int getBlockZ()
	{
		return blockZ;
	}

	public void setBlockZ(int blockZ)
	{
		this.blockZ = blockZ;
	}
	
	public Location getLocation()
	{
		if(!PPP.getPlugin().getServername().equals(server))
		{
			return null;
		}
		World world = Bukkit.getWorld(this.world);
		if(world == null)
		{
			return null;
		}
		return new Location(world, blockX, blockY, blockZ);
	}
	
	public String getMysqlTableName()
	{
		return "pppPlayerData";
	}
	
	public boolean setupMysql(MysqlBaseSetup mysqlSetup, ServerType serverType)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS `"+getMysqlTableName()
			+ "` (id int AUTO_INCREMENT PRIMARY KEY,"
			+ " player_uuid char(36) NOT NULL,"
			+ " block_type text,"
			+ " server text NOT NULL,"
			+ " world text NOT NULL,"
			+ " block_x int,"
			+ " block_y int,"
			+ " block_z int);");
		return mysqlSetup.baseSetup(sql.toString());
	}

	@Override
	public boolean create(Connection conn)
	{
		try
		{
			String sql = "INSERT INTO `" + getMysqlTableName()
					+ "`(`player_uuid`, `block_type`, `server`, `world`,"
					+ " `block_x`, `block_y`, `block_z`) " 
					+ "VALUES(?, ?, ?, ?,"
					+ " ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getPlayerUUID().toString());
	        ps.setString(2, getBlockType().toString());
	        ps.setString(3, getServer());
	        ps.setString(4, getWorld());
	        ps.setInt(5, getBlockX());
	        ps.setInt(6, getBlockY());
	        ps.setInt(7, getBlockZ());
	        int i = ps.executeUpdate();
	        MysqlHandler.addRows(QueryType.INSERT, i);
	        return true;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not create a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public boolean update(Connection conn, String whereColumn, Object... whereObject)
	{
		try
		{
			String sql = "UPDATE `" + getMysqlTableName()
				+ "` SET `player_uuid` = ?, `block_type` = ?, `server` = ?,"
				+ " `world` = ?, `block_x` = ?, `block_y` = ?, `block_z` = ?"
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getPlayerUUID().toString());
	        ps.setString(2, getBlockType().toString());
	        ps.setString(3, getServer());
	        ps.setString(4, getWorld());
	        ps.setInt(5, getBlockX());
	        ps.setInt(6, getBlockY());
	        ps.setInt(7, getBlockZ());
			int i = 8;
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
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<RegisteredBlock> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject)
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
			ArrayList<RegisteredBlock> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new RegisteredBlock(rs.getInt("id"),
						UUID.fromString(rs.getString("player_uuid")),
						BlockType.valueOf(rs.getString("block_type")),
						rs.getString("server"),
						rs.getString("world"),
						rs.getInt("block_x"),
						rs.getInt("block_y"),
						rs.getInt("block_z")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
}