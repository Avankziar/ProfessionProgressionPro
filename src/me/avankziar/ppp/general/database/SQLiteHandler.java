package me.avankziar.ppp.general.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

public class SQLiteHandler
{	
	/*
	 * Alle Mysql Reihen, welche durch den Betrieb aufkommen.
	 */
	public static long startRecordTime = System.currentTimeMillis();
	public static int inserts = 0;
	public static int updates = 0;
	public static int deletes = 0;
	public static int reads = 0;
	
	public static void addRows(QueryType type, int amount)
	{
		switch(type)
		{
		case DELETE:
			deletes += amount;
			break;
		case INSERT:
			inserts += amount;
		case READ:
			reads += amount;
			break;
		case UPDATE:
			updates += amount;
			break;
		}
	}
	
	public static void resetsRows()
	{
		inserts = 0;
		updates = 0;
		reads = 0;
		deletes = 0;
	}
	
	@Nullable
	private static Logger logger;
	private SQLiteSetup sqliteSetup;
	
	public SQLiteHandler(Logger logger, SQLiteSetup mysqlSetup) 
	{
		SQLiteHandler.logger = logger;
		this.sqliteSetup = mysqlSetup;
	}
	@Nullable
	public static Logger getLogger()
	{
		return SQLiteHandler.logger;
	}
	
	private PreparedStatement getPreparedStatement(Connection conn, String sql, int count, Object... whereObject) throws SQLException
	{
		PreparedStatement ps = conn.prepareStatement(sql);
		int i = count;
        for(Object o : whereObject)
        {
        	ps.setObject(i, o);
        	i++;
        }
        return ps;
	}
	
	public <T extends SQLiteHandable<T>> boolean exist(T t, String whereColumn, Object... whereObject)
	{
		//All Object which leaves the try-block, will be closed. So conn and ps is closed after the methode
		//No finally needed.
		//So much as possible in async methode use
		try (Connection conn = sqliteSetup.getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"SELECT `id` FROM `" + t.getMysqlTableName() + "` WHERE "+whereColumn+" LIMIT 1",
					1,
					whereObject);
	        ResultSet rs = ps.executeQuery();
	        MysqlBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return true;
	        }
	    } catch (SQLException e) 
		{
			t.log(logger, Level.WARNING, "Could not check "+t.getClass().getName()+" Object if it exist!", e);
		}
		return false;
	}
	
	public <T extends SQLiteHandable<T>> boolean create(T t)
	{
		try (Connection conn = sqliteSetup.getConnection();)
		{
			t.create(conn);
			return true;
		} catch (Exception e)
		{
			t.log(logger, Level.WARNING, "Could not create "+t.getClass().getName()+" Object!", e);
		}
		return false;
	}
	
	public <T extends SQLiteHandable<T>> boolean updateData(T t, String whereColumn, Object... whereObject)
	{
		try (Connection conn = sqliteSetup.getConnection();)
		{
			t.update(conn, whereColumn, whereObject);
			return true;
		} catch (Exception e)
		{
			t.log(logger, Level.WARNING, "Could not create "+t.getClass().getName()+" Object!", e);
		}
		return false;
	}
	
	public <T extends SQLiteHandable<T>> T getData(T t, String whereColumn, Object... whereObject)
	{
		try (Connection conn = sqliteSetup.getConnection();)
		{
			ArrayList<T> list = t.get(conn, "`id` ASC", " Limit 1", whereColumn, whereObject);
			if(!list.isEmpty())
			{
				return list.get(0);
			}
		} catch (Exception e)
		{
			t.log(logger, Level.WARNING, "Could not create "+t.getClass().getName()+" Object!", e);
		}
		return null;
	}
	
	public <T extends SQLiteHandable<T>> int deleteData(T t, String whereColumn, Object... whereObject)
	{
		try (Connection conn = sqliteSetup.getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"DELETE FROM `" + t.getMysqlTableName() + "` WHERE "+whereColumn,
					1,
					whereObject);
	        int d = ps.executeUpdate();
			MysqlBaseHandler.addRows(QueryType.DELETE, d);
			return d;
	    } catch (SQLException e) 
		{
	    	t.log(logger, Level.WARNING, "Could not delete "+t.getClass().getName()+" Object!", e);
		}
		return 0;
	}
	
	public <T extends SQLiteHandable<T>> int truncate(T t)
	{
		try (Connection conn = sqliteSetup.getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"TRUNCATE TABLE `" + t.getMysqlTableName() + "`", 0, new Object[]{});
	        int d = ps.executeUpdate();
	        //SQLLiteHandler.addRows(QueryType.DELETE, d);
			return d;
	    } catch (SQLException e)
		{
	    	t.log(logger, Level.WARNING, "Could not truncate "+t.getClass().getName()+" Object table!", e);
		}
		return 0;
	}
	
	public <T extends SQLiteHandable<T>> int lastID(T t)
	{
		try (Connection conn = sqliteSetup.getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"SELECT `id` FROM `" + t.getMysqlTableName() + "` ORDER BY `id` DESC LIMIT 1",
					1);
	        ResultSet rs = ps.executeQuery();
	        MysqlBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return rs.getInt("id");
	        }
	    } catch (SQLException e) 
		{
			t.log(logger, Level.WARNING, "Could not get last id from "+t.getClass().getName()+" Object table!", e);
		}
		return 0;
	}
	
	public <T extends SQLiteHandable<T>> int getCount(T t, String whereColumn, Object... whereObject)
	{
		try (Connection conn = sqliteSetup.getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					" SELECT count(*) FROM `" + t.getMysqlTableName() + "` WHERE "+whereColumn,
					1,
					whereObject);
	        ResultSet rs = ps.executeQuery();
	        MysqlBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return rs.getInt(1);
	        }
	    } catch (SQLException e) 
		{
	    	t.log(logger, Level.WARNING, "Could not count "+t.getClass().getName()+" Object!", e);
		}
		return 0;
	}
	
	public <T extends SQLiteHandable<T>> double getSum(T t, String whereColumn, Object... whereObject)
	{
		try (Connection conn = sqliteSetup.getConnection();)
		{
			PreparedStatement ps = getPreparedStatement(conn,
					"SELECT sum("+whereColumn+") FROM `" + t.getMysqlTableName() + "` WHERE 1",
					1,
					whereObject);
	        ResultSet rs = ps.executeQuery();
	        MysqlBaseHandler.addRows(QueryType.READ, rs.getMetaData().getColumnCount());
	        while (rs.next()) 
	        {
	        	return rs.getInt(1);
	        }
	    } catch (SQLException e) 
		{
	    	t.log(logger, Level.WARNING, "Could not summarized "+t.getClass().getName()+" Object!", e);
		}
		return 0;
	}
	
	public <T extends SQLiteHandable<T>> ArrayList<T> getList(T t, String orderByColumn, int start, int quantity, String whereColumn, Object...whereObject)
	{
		try (Connection conn = sqliteSetup.getConnection();)
		{
			ArrayList<T> list = t.get(conn, orderByColumn, " Limit "+start+", "+quantity, whereColumn, whereObject);
			if(!list.isEmpty())
			{
				return list;
			}
		} catch (Exception e)
		{
			t.log(logger, Level.WARNING, "Could not create "+t.getClass().getName()+" Object!", e);
		}
		return new ArrayList<>();
	}
	
	public <T extends SQLiteHandable<T>> ArrayList<T> getFullList(T t, String orderByColumn,
			String whereColumn, Object...whereObject)
	{
		try (Connection conn = sqliteSetup.getConnection();)
		{
			ArrayList<T> list = t.get(conn, orderByColumn, " ", whereColumn, whereObject);
			if(!list.isEmpty())
			{
				return list;
			}
		} catch (Exception e)
		{
			t.log(logger, Level.WARNING, "Could not create "+t.getClass().getName()+" Object!", e);
		}
		return new ArrayList<>();
	}
}