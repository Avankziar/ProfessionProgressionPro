package me.avankziar.ppp.general.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import me.avankziar.ppp.general.database.MysqlBaseHandler;
import me.avankziar.ppp.general.database.MysqlBaseSetup;
import me.avankziar.ppp.general.database.MysqlTable;
import me.avankziar.ppp.general.database.QueryType;
import me.avankziar.ppp.general.database.ServerType;

public class ProfessionCompensationLog implements MysqlTable<ProfessionCompensationLog>
{
	private long id;
	private UUID uuid;
	private String professionCategory;
	private long timeStamp;
	private EventType eventType;
	private Material material;
	private EntityType entityType;
	private int amount;
	private double collectedMoney;
	private String currency;
	private double collectedExperience;
	
	public ProfessionCompensationLog(long id, UUID uuid, String professionCategory, long timeStamp,
			EventType eventType, Material material, EntityType entityType,
			int amount, double collectedMoney, String currency, double collectedExperience)
	{
		setId(id);
		setUUID(uuid);
		setProfessionCategory(professionCategory);
		setTimeStamp(timeStamp);
		setEventType(eventType);
		setMaterial(material);
		setEntityType(entityType);
		setAmount(amount);
		setCollectedMoney(collectedMoney);
		setCurrency(currency);
		setCollectedExperience(collectedExperience);
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

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public EventType getEventType() 
	{
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public Material getMaterial()
	{
		return material;
	}

	public void setMaterial(Material material) 
	{
		this.material = material;
	}

	public EntityType getEntityType() 
	{
		return entityType;
	}

	public void setEntityType(EntityType entityType) 
	{
		this.entityType = entityType;
	}

	public int getAmount() 
	{
		return amount;
	}

	public void setAmount(int amount) 
	{
		this.amount = amount;
	}

	public double getCollectedMoney() 
	{
		return collectedMoney;
	}

	public void setCollectedMoney(double collectedMoney) 
	{
		this.collectedMoney = collectedMoney;
	}

	public String getCurrency() 
	{
		return currency;
	}

	public void setCurrency(String currency) 
	{
		this.currency = currency;
	}

	public double getCollectedExperience() 
	{
		return collectedExperience;
	}

	public void setCollectedExperience(double collectedExperience) 
	{
		this.collectedExperience = collectedExperience;
	}
	
	public String getMysqlTableName()
	{
		return "pppProfessionCompensationLog";
	}
	
	public boolean setupMysql(MysqlBaseSetup mysqlSetup, ServerType serverType)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE IF NOT EXISTS `"+getMysqlTableName()
				+ "` (id bigint AUTO_INCREMENT PRIMARY KEY,"
				+ " player_uuid char(36) NOT NULL,"
				+ " profession_category text,"
				+ " time_stamp bigint,"
				+ " event_type text,"
				+ " material text,"
				+ " entity_type text,"
				+ " amount int,"
				+ " collected_money double,"
				+ " currency text,"
				+ " collected_profession_experience double;");
		return mysqlSetup.baseSetup(sql.toString());
	}

	@Override
	public boolean create(Connection conn)
	{
		try
		{
			String sql = "INSERT INTO `" + getMysqlTableName()
					+ "`(`player_uuid`, `profession_category`, `time_stamp`, `event_type`, `material`, `entity_type`,"
					+ " `amount`, `collected_money`, `currency`, `collected_profession_experience`) " 
					+ "VALUES(?, ?, ?, ?, ?, ?,"
					+ " ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, getUUID().toString());
	        ps.setString(2, getProfessionCategory());
	        ps.setLong(3, getTimeStamp());
	        ps.setString(4, getEventType().toString());
	        ps.setString(5, getMaterial() != null ? getMaterial().toString() : "null");
	        ps.setString(6, getEntityType() != null ? getEntityType().toString() : "null");
	        ps.setInt(7, getAmount());
	        ps.setDouble(8, getCollectedMoney());
	        ps.setString(9, getCurrency());
	        ps.setDouble(10, getCollectedExperience());
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
				+ "` SET `player_uuid` = ?, `profession_category` = ?, `time_stamp` = ?,"
				+ " `event_type` = ?, `material` = ?, `entity_type` = ?,"
				+ " `amount` = ?, `collected_money` = ?, `currency` = ?, `collected_profession_experience` = ?" 
				+ " WHERE "+whereColumn;
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, getUUID().toString());
	        ps.setString(2, getProfessionCategory());
	        ps.setLong(3, getTimeStamp());
	        ps.setString(4, getEventType().toString());
	        ps.setString(5, getMaterial() != null ? getMaterial().toString() : "null");
	        ps.setString(6, getEntityType() != null ? getEntityType().toString() : "null");
	        ps.setInt(7, getAmount());
	        ps.setDouble(8, getCollectedMoney());
	        ps.setString(9, getCurrency());
	        ps.setDouble(10, getCollectedExperience());
			int i = 11;
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
	public ArrayList<ProfessionCompensationLog> get(Connection conn, String orderby, String limit, String whereColumn, Object... whereObject)
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
			ArrayList<ProfessionCompensationLog> al = new ArrayList<>();
			String m = rs.getString("event_type");
			String e = rs.getString("entity_type");
			while (rs.next()) 
			{
				al.add(new ProfessionCompensationLog(rs.getInt("id"),
						UUID.fromString(rs.getString("player_uuid")),
						rs.getString("profession_category"),
						rs.getLong("time_stamp"),
						EventType.valueOf(rs.getString("event_type")),
						m.equals("null") ? null : Material.valueOf(m),
						e.equals("null") ? null : EntityType.valueOf(e),
						rs.getInt("amount"),
						rs.getDouble("collected_money"),
						rs.getString("currency"),
						rs.getDouble("collected_profession_experience")
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