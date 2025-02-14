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
	private double freeProfessionExperience;
	private double totalProfessionCompensationMoney;
	
	public enum RewardMessageType
	{
		NONE, CHAT, ACTIONBAR;
	}
	
	private RewardMessageType processRewardMessageType;
	private boolean scoreboardActive;
	
	public PlayerData(){}
	
	public PlayerData(int id, UUID uuid, String name, long lastProfessionChange,
			double totalProfessionExperience, double freeProfessionExperience, double totalProfessionCompensationMoney,
			RewardMessageType processRewardMessageType, boolean scoreboardActive)
	{
		setId(id);
		setUUID(uuid);
		setName(name);
		setLastProfessionChange(lastProfessionChange);
		setTotalProfessionExperience(totalProfessionExperience);
		setFreeProfessionExperience(freeProfessionExperience);
		setTotalProfessionCompensationMoney(totalProfessionCompensationMoney);
		setProcessRewardMessageType(processRewardMessageType);
		setScoreboardActive(scoreboardActive);
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

	public double getFreeProfessionExperience()
	{
		return freeProfessionExperience;
	}

	public void setFreeProfessionExperience(double freeProfessionExperience)
	{
		this.freeProfessionExperience = freeProfessionExperience;
	}

	public double getTotalProfessionCompensationMoney() {
		return totalProfessionCompensationMoney;
	}

	public void setTotalProfessionCompensationMoney(double totalProfessionCompensationMoney) {
		this.totalProfessionCompensationMoney = totalProfessionCompensationMoney;
	}

	public RewardMessageType getProcessRewardMessageType() {
		return processRewardMessageType;
	}

	public void setProcessRewardMessageType(RewardMessageType processRewardMessageType) {
		this.processRewardMessageType = processRewardMessageType;
	}

	public boolean isScoreboardActive()
	{
		return scoreboardActive;
	}

	public void setScoreboardActive(boolean scoreboardActive)
	{
		this.scoreboardActive = scoreboardActive;
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
				+ " total_profession_experience double,"
				+ " free_profession_experience double,"
				+ " total_profession_compensation_money double,"
				+ " process_reward_message_type text,"
				+ " scoreboard_active boolean);");
		return mysqlSetup.baseSetup(sql.toString());
	}

	@Override
	public boolean create(Connection conn)
	{
		try
		{
			String sql = "INSERT INTO `" + getMysqlTableName()
					+ "`(`player_uuid`, `player_name`, `last_prefossion_change`,"
					+ " `total_profession_experience`, `free_profession_experience`, `total_profession_compensation_money`,"
					+ " `process_reward_message_type`, `scoreboard_active`) " 
					+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
			ps.setString(2, getName());
			ps.setLong(3, getLastProfessionChange());
	        ps.setDouble(4, getTotalProfessionExperience());
	        ps.setDouble(5, getFreeProfessionExperience());
	        ps.setDouble(6, getTotalProfessionCompensationMoney());
	        ps.setString(7, getProcessRewardMessageType().toString());
	        ps.setBoolean(8, isScoreboardActive());
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
				+ " `total_profession_experience` = ?, `free_profession_experience` = ?, `total_profession_compensation_money` = ?,"
				+ " `process_reward_message_type` = ?, `scoreboard_active` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
			ps.setString(2, getName());
			ps.setLong(3, getLastProfessionChange());
	        ps.setDouble(4, getTotalProfessionExperience());
	        ps.setDouble(5, getFreeProfessionExperience());
	        ps.setDouble(6, getTotalProfessionCompensationMoney());
	        ps.setString(7, getProcessRewardMessageType().toString());
	        ps.setBoolean(8, isScoreboardActive());
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
						rs.getDouble("free_profession_experience"),
						rs.getDouble("total_profession_compensation_money"),
						RewardMessageType.valueOf(rs.getString("process_reward_message_type")),
						rs.getBoolean("scoreboard_active")));
			}
			return al;
		} catch (SQLException e)
		{
			this.log(MysqlBaseHandler.getLogger(), Level.WARNING, "SQLException! Could not get a "+this.getClass().getSimpleName()+" Object!", e);
		}
		return new ArrayList<>();
	}
}