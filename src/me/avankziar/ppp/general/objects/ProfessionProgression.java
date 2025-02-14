package me.avankziar.ppp.general.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import me.avankziar.ppp.general.database.MysqlBaseHandler;
import me.avankziar.ppp.general.database.MysqlBaseSetup;
import me.avankziar.ppp.general.database.MysqlTable;
import me.avankziar.ppp.general.database.QueryType;
import me.avankziar.ppp.general.database.ServerType;

public class ProfessionProgression implements MysqlTable<ProfessionProgression>
{
	private long id;
	private UUID uuid;
	private String professionCategory;
	private String professionTitle;
	private long professionReceived; //Wann man diese Profession erlangt hat.
	private double moneyTotal; //Erwirtschaftet Geld in diesem Beruf.
	
	public ProfessionProgression()
	{
		
	}
	
	public ProfessionProgression(long id, UUID uuid, String professionCategory, String professionTitle, long professionReceived, double moneyTotal)
	{
		setId(id);
		setUUID(uuid);
		setProfessionCategory(professionCategory);
		setProfessionTitle(professionTitle);
		setProfessionReceived(professionReceived);
		setMoneyTotal(moneyTotal);
	}
	
	public ServerType getServerType()
	{
		return ServerType.ALL;
	}

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public UUID getUUID()
	{
		return uuid;
	}

	public void setUUID(UUID uuid)
	{
		this.uuid = uuid;
	}

	public String getProfessionCategory()
	{
		return professionCategory;
	}

	public void setProfessionCategory(String professionCategory)
	{
		this.professionCategory = professionCategory;
	}

	public String getProfessionTitle()
	{
		return professionTitle;
	}

	public void setProfessionTitle(String professionTitle)
	{
		this.professionTitle = professionTitle;
	}

	public long getProfessionReceived()
	{
		return professionReceived;
	}

	public void setProfessionReceived(long professionReceived)
	{
		this.professionReceived = professionReceived;
	}

	public double getMoneyTotal()
	{
		return moneyTotal;
	}

	public void setMoneyTotal(double moneyTotal)
	{
		this.moneyTotal = moneyTotal;
	}
	
	@Override
	public String getMysqlTableName()
	{
		return "pppProfessionProgression";
	}
	
	public boolean setupMysql(MysqlBaseSetup mysqlSetup, ServerType serverType)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS `"+getMysqlTableName()
				+ "` (id bigint AUTO_INCREMENT PRIMARY KEY,"
				+ " player_uuid char(36) NOT NULL,"
				+ " profession_category text,"
				+ " profession_title text,"
				+ " profession_recieved bigint,"
				+ " money_total_received double;");
		return mysqlSetup.baseSetup(sql.toString());
	}

	@Override
	public boolean create(Connection conn)
	{
		try
		{
			String sql = "INSERT INTO `" + getMysqlTableName()
					+ "`(`player_uuid`, `profession_category`, `profession_title`,"
					+ " `profession_recieved`, `money_total_received`) " 
					+ "VALUES(?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUUID().toString());
	        ps.setString(2, getProfessionCategory());
	        ps.setString(3, getProfessionTitle());
	        ps.setLong(4, getProfessionReceived());
	        ps.setDouble(5, getMoneyTotal());
	        int i = ps.executeUpdate();
	        MysqlBaseHandler.addRows(QueryType.INSERT, i);
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
				+ "` SET `player_uuid` = ?, `profession_category` = ?, `profession_title` = ?,"
				+ " `profession_recieved` = ?, `money_total_received` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
		    ps.setString(2, getProfessionCategory());
		    ps.setString(3, getProfessionTitle());
		    ps.setLong(4, getProfessionReceived());
		    ps.setDouble(5, getMoneyTotal());
			int i = 6;
			for(Object o : whereObject)
			{
				ps.setObject(i, o);
				i++;
			}			
			int u = ps.executeUpdate();
			MysqlBaseHandler.addRows(QueryType.UPDATE, u);
			return true;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not update a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return false;
	}

	@Override
	public ArrayList<ProfessionProgression> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject)
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
			MysqlBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
			ArrayList<ProfessionProgression> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new ProfessionProgression(rs.getLong("id"),
						UUID.fromString(rs.getString("player_uuid")),
						rs.getString("profession_category"),
						rs.getString("profession_title"),
						rs.getLong("profession_recieved"),
						rs.getDouble("money_total_received")
						));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
}