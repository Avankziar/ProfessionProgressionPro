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

public class Profession implements MysqlTable<Profession>
{
	private int id;
	private UUID uuid;
	private boolean isActive;
	private String professionCategory;
	private String professionTitle;
	private double actualProfessionExperience;
	private double totalProfessionCompensationMoney;
	private long timeWhenProfessionWasAcceptFirstTime;
	private long timeWhenProfessionWasSwitchTo;
	
	public Profession() {}
	
	public Profession(int id, UUID uuid, boolean isActive, String professionCategory, String professionTitle,
			double actualProfessionExperience, double totalProfessionCompensationMoney,
			long timeWhenProfessionWasAcceptFirstTime, long timeWhenProfessionWasSwitchTo)
	{
		setId(id);
		setUUID(uuid);
		setActive(isActive);
		setProfessionCategory(professionCategory);
		setProfessionTitle(professionTitle);
		setActualProfessionExperience(actualProfessionExperience);
		setTotalProfessionCompensationMoney(totalProfessionCompensationMoney);
		setTimeWhenProfessionWasAcceptFirstTime(timeWhenProfessionWasAcceptFirstTime);
		setTimeWhenProfessionWasSwitchTo(timeWhenProfessionWasSwitchTo);
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

	public UUID getUUID() 
	{
		return uuid;
	}

	public void setUUID(UUID uuid) 
	{
		this.uuid = uuid;
	}

	public boolean isActive() 
	{
		return isActive;
	}

	public void setActive(boolean isActive) 
	{
		this.isActive = isActive;
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

	public double getActualProfessionExperience() 
	{
		return actualProfessionExperience;
	}

	public void setActualProfessionExperience(double actualProfessionExperience) 
	{
		this.actualProfessionExperience = actualProfessionExperience;
	}

	public double getTotalProfessionCompensationMoney() 
	{
		return totalProfessionCompensationMoney;
	}

	public void setTotalProfessionCompensationMoney(double totalProfessionCompensationMoney) 
	{
		this.totalProfessionCompensationMoney = totalProfessionCompensationMoney;
	}

	public long getTimeWhenProfessionWasAcceptFirstTime() 
	{
		return timeWhenProfessionWasAcceptFirstTime;
	}

	public void setTimeWhenProfessionWasAcceptFirstTime(long timeWhenProfessionWasAcceptFirstTime) 
	{
		this.timeWhenProfessionWasAcceptFirstTime = timeWhenProfessionWasAcceptFirstTime;
	}

	public long getTimeWhenProfessionWasSwitchTo() 
	{
		return timeWhenProfessionWasSwitchTo;
	}

	public void setTimeWhenProfessionWasSwitchTo(long timeWhenProfessionWasSwitchTo) 
	{
		this.timeWhenProfessionWasSwitchTo = timeWhenProfessionWasSwitchTo;
	}
	
	public String getMysqlTableName()
	{
		return "pppProfession";
	}
	
	public boolean setupMysql(MysqlBaseSetup mysqlSetup, ServerType serverType)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS `"+getMysqlTableName()
				+ "` (id bigint AUTO_INCREMENT PRIMARY KEY,"
				+ " player_uuid char(36) NOT NULL"
				+ " is_active boolean,"
				+ " profession_category text,"
				+ " profession_title text,"
				+ " actual_profession_experience double,"
				+ " total_profession_compensation_money double,"
				+ " time_when_profession_was_accept_first_time long,"
				+ " time_when_profession_was_switch_to long;");
		return mysqlSetup.baseSetup(sql.toString());
	}

	@Override
	public boolean create(Connection conn)
	{
		try
		{
			String sql = "INSERT INTO `" + getMysqlTableName()
					+ "`(`player_uuid`, `is_active`, `profession_category`, `profession_title`,"
					+ " `actual_profession_experience`, `total_profession_compensation_money`,"
					+ " `time_when_profession_was_accept_first_time`, `time_when_profession_was_switch_to`) " 
					+ "VALUES(?, ?, ?, ?, "
					+ "?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUUID().toString());
	        ps.setBoolean(2, isActive());
	        ps.setString(3, getProfessionCategory());
	        ps.setString(4, getProfessionTitle());
	        ps.setDouble(5, getActualProfessionExperience());
	        ps.setDouble(6, getTotalProfessionCompensationMoney());
	        ps.setLong(7, getTimeWhenProfessionWasAcceptFirstTime());
	        ps.setLong(8, getTimeWhenProfessionWasSwitchTo());
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
				+ "` SET `player_uuid` = ?, `is_active` = ?, `profession_category` = ?, `profession_title` = ?,"
				+ " `actual_profession_experience` = ?, `total_profession_compensation_money` = ?,"
				+ " `time_when_profession_was_accept_first_time` = ?, `time_when_profession_was_switch_to` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
			ps.setBoolean(2, isActive());
	        ps.setString(3, getProfessionCategory());
	        ps.setString(4, getProfessionTitle());
	        ps.setDouble(5, getActualProfessionExperience());
	        ps.setDouble(6, getTotalProfessionCompensationMoney());
	        ps.setLong(7, getTimeWhenProfessionWasAcceptFirstTime());
	        ps.setLong(8, getTimeWhenProfessionWasSwitchTo());
			int i = 9;
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
	public ArrayList<Profession> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject)
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
			ArrayList<Profession> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new Profession(rs.getInt("id"),
						UUID.fromString(rs.getString("player_uuid")),
						rs.getBoolean("is_active"),
						rs.getString("profession_category"),
						rs.getString("profession_title"),
						rs.getDouble("actual_profession_experience"),
						rs.getDouble("total_profession_compensation_money"),
						rs.getLong("time_when_profession_was_accept_first_time"),
						rs.getLong("time_when_profession_was_switch_to")
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