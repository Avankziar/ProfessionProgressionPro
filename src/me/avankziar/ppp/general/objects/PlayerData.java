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

public class PlayerData implements MysqlTable<PlayerData>
{
	private int id;
	private UUID uuid;
	private String name;
	private long lastProfessionChange;
	private double totalProfessionExperience;
	private double totalProfessionCompensationMoney;
	
	public PlayerData(){}
	
	public PlayerData(int id, UUID uuid, String name,
			long lastProfessionChange, double totalProfessionExperience, double totalProfessionCompensationMoney)
	{
		setId(id);
		setUUID(uuid);
		setName(name);
		setLastProfessionChange(lastProfessionChange);
		setTotalProfessionExperience(totalProfessionExperience);
		setTotalProfessionCompensationMoney(totalProfessionCompensationMoney);
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

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public long getLastProfessionChange() {
		return lastProfessionChange;
	}

	public void setLastProfessionChange(long lastProfessionChange) {
		this.lastProfessionChange = lastProfessionChange;
	}

	public double getTotalProfessionExperience() {
		return totalProfessionExperience;
	}

	public void setTotalProfessionExperience(double totalProfessionExperience) {
		this.totalProfessionExperience = totalProfessionExperience;
	}

	public double getTotalProfessionCompensationMoney() {
		return totalProfessionCompensationMoney;
	}

	public void setTotalProfessionCompensationMoney(double totalProfessionCompensationMoney) {
		this.totalProfessionCompensationMoney = totalProfessionCompensationMoney;
	}

	public String getMysqlTableName()
	{
		return "pppPlayerData";
	}
	
	public boolean setupMysql(MysqlBaseSetup mysqlSetup, ServerType serverType)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS `"+getMysqlTableName()
				+ "` (id bigint AUTO_INCREMENT PRIMARY KEY,"
				+ " player_uuid char(36) NOT NULL UNIQUE,"
				+ " player_name varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,"
				+ " last_prefossion_change bigint,"
				+ " total_profession_experience double"
				+ " total_profession_compensation_money double);");
		return mysqlSetup.baseSetup(sql.toString());
	}

	@Override
	public boolean create(Connection conn)
	{
		try
		{
			String sql = "INSERT INTO `" + getMysqlTableName()
					+ "`(`player_uuid`, `player_name`,"
					+ " `last_prefossion_change`, `total_profession_experience`, `total_profession_compensation_money`) " 
					+ "VALUES(?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUUID().toString());
	        ps.setString(2, getName());
	        ps.setLong(3, getLastProfessionChange());
	        ps.setDouble(4, getTotalProfessionExperience());
	        ps.setDouble(5, getTotalProfessionCompensationMoney());
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
				+ "` SET `player_uuid` = ?, `player_name` = ?, `last_prefossion_change` = ?,"
				+ " `total_profession_experience` = ?, `total_profession_compensation_money` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
			ps.setString(2, getName());
			ps.setLong(3, getLastProfessionChange());
	        ps.setDouble(4, getTotalProfessionExperience());
	        ps.setDouble(5, getTotalProfessionCompensationMoney());
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
	public ArrayList<PlayerData> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject)
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
			ArrayList<PlayerData> al = new ArrayList<>();
			while (rs.next()) 
			{
				al.add(new PlayerData(rs.getInt("id"),
						UUID.fromString(rs.getString("player_uuid")),
						rs.getString("player_name"),
						rs.getLong("last_prefossion_change"),
						rs.getDouble("total_profession_experience"),
						rs.getDouble("total_profession_compensation_money")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
}