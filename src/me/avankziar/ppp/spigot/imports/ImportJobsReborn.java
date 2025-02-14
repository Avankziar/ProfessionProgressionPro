package me.avankziar.ppp.spigot.imports;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.avankziar.ifh.general.math.MathFormulaParser;
import me.avankziar.ppp.general.objects.PlayerData;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.MessageHandler;

public class ImportJobsReborn
{
	private static PPP plugin = PPP.getPlugin();
	
	public static void importJobsReborn(Player player, PlayerData pd)
	{
		int juserid = getJobsRebornUserID(player.getUniqueId());
		if(juserid == 0)
		{
			return;
		}
		LinkedHashMap<String, LinkedHashMap<Integer, Integer>> map = getJobsRebornData(juserid);
		if(map == null)
		{
			return;
		}
		LinkedHashMap<String, String> formulamap = getFormulas();
		double pppexp = convertJobInExp(map, formulamap);
		pd.setFreeProfessionExperience(pd.getFreeProfessionExperience() + pppexp);
		pd.setTotalProfessionExperience(pd.getTotalProfessionExperience() + pppexp);
		plugin.getMysqlHandler().updateData(pd, "`player_uuid` = ?", player.getUniqueId().toString());
		deleteJobsRebornUserID(player.getUniqueId());
		deleteJobsRebornData(juserid);
		MessageHandler.sendMessage(player, PPP.getPlugin().getYamlHandler().getLang().getString("ImportFromJobsReborn")
				.replace("%pexp%", String.valueOf(pppexp)));
	}
	
	private static int getJobsRebornUserID(UUID uuid)
	{
		int juserid = 0;
		PreparedStatement ps = null;
		ResultSet result = null;
		Connection conn = null;
		try
		{
			conn = plugin.getMysqlSetup().getConnection();
		} catch (SQLException e)
		{
			return 0;
		}
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT * FROM `jobs_users` WHERE `player_uuid` = ?";
		        ps = conn.prepareStatement(sql);
		        ps.setString(1, uuid.toString());
		        result = ps.executeQuery();
		        while (result.next()) 
		        {
		        	juserid = result.getInt("id");
		        }
		        return juserid;
		    } catch (SQLException e) 
			{
				  PPP.logger.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	try 
		    	{
		    		if (result != null) 
		    		{
		    			result.close();
		    		}
		    		if (ps != null) 
		    		{
		    			ps.close();
		    		}
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
		}
		return juserid;
	}
	
	private static void deleteJobsRebornUserID(UUID uuid)
	{
		PreparedStatement ps = null;
		ResultSet result = null;
		Connection conn = null;
		try
		{
			conn = plugin.getMysqlSetup().getConnection();
		} catch (SQLException e)
		{
			return;
		}
		if (conn != null) 
		{
			try 
			{			
				String sql = "Delete FROM `jobs_users` WHERE `player_uuid` = ?";
		        ps = conn.prepareStatement(sql);
		        ps.setString(1, uuid.toString());
		        ps.executeUpdate();
		    } catch (SQLException e) 
			{
				  PPP.logger.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	try 
		    	{
		    		if (result != null) 
		    		{
		    			result.close();
		    		}
		    		if (ps != null) 
		    		{
		    			ps.close();
		    		}
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
		}
		return;
	}
	
	private static LinkedHashMap<String, LinkedHashMap<Integer, Integer>> getJobsRebornData(int userid)
	{
		LinkedHashMap<String, LinkedHashMap<Integer, Integer>> map = new LinkedHashMap<>();
		PreparedStatement ps = null;
		ResultSet result = null;
		Connection conn = null;
		try
		{
			conn = plugin.getMysqlSetup().getConnection();
		} catch (SQLException e)
		{
			return null;
		}
		if (conn != null) 
		{
			try 
			{			
				String sql = "SELECT * FROM `jobs_jobs` WHERE `userid` = ?";
		        ps = conn.prepareStatement(sql);
		        ps.setInt(1, userid);
		        result = ps.executeQuery();
		        while (result.next()) 
		        {
		        	String job = result.getString("job");
		        	int level = result.getInt("level");
		        	int expprogression = result.getInt("experience");
		        	LinkedHashMap<Integer, Integer> m = new LinkedHashMap<>();
		        	m.put(level, expprogression);
		        	map.put(job, m);
		        }
		    } catch (SQLException e) 
			{
				  PPP.logger.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	try 
		    	{
		    		if (result != null) 
		    		{
		    			result.close();
		    		}
		    		if (ps != null) 
		    		{
		    			ps.close();
		    		}
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
		}
		return map;
	}
	
	private static LinkedHashMap<String, String> getFormulas()
	{
		LinkedHashMap<String, String> formulamap = new LinkedHashMap<>();
		File jobsDir = new File("plugins/Jobs/jobs");
		File[] listOfFiles = jobsDir.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) 
		{
			if(!listOfFiles[i].isFile()) 
			{
				continue;
			}
			YamlConfiguration y = new YamlConfiguration();
			try 
			{
				y.load(listOfFiles[i]);
			} catch (IOException | InvalidConfigurationException e) 
			{
				return null;
			}
			String path = null;
			for(String s : y.getKeys(false))
			{
				if(y.get(s+".fullname") != null)
				{
					path = s;
					break;
				}
			}
			String name = FilenameUtils.removeExtension(listOfFiles[i].getName()) + ";"+y.getString(path+".fullname");
			if(!formulamap.containsKey(name))
			{
				formulamap.put(name, y.getString(path+".leveling-progression-equation"));
			}
		}
		return formulamap;
	}
	
	private static void deleteJobsRebornData(int userid)
	{
		PreparedStatement ps = null;
		ResultSet result = null;
		Connection conn = null;
		try
		{
			conn = plugin.getMysqlSetup().getConnection();
		} catch (SQLException e)
		{
			return;
		}
		if (conn != null) 
		{
			try 
			{			
				String sql = "Delete FROM `jobs_jobs` WHERE `userid` = ?";
		        ps = conn.prepareStatement(sql);
		        ps.setInt(1, userid);
		        ps.executeUpdate();
		    } catch (SQLException e) 
			{
				  PPP.logger.warning("Error: " + e.getMessage());
				  e.printStackTrace();
		    } finally 
			{
		    	try 
		    	{
		    		if (result != null) 
		    		{
		    			result.close();
		    		}
		    		if (ps != null) 
		    		{
		    			ps.close();
		    		}
		    	} catch (Exception e) {
		    		e.printStackTrace();
		    	}
		    }
		}
		return;
	}
	
	private static double convertJobInExp(LinkedHashMap<String, LinkedHashMap<Integer, Integer>> m, LinkedHashMap<String, String> formulamap)
	{
		double ttexp = 0;
		int numjobs = m.size();
		int maxjobs = plugin.getYamlHandler().getConfig().getInt("Import.JobsReborn.MaxJobsPerPlayer", 3);
		for(Entry<String, LinkedHashMap<Integer, Integer>> v : m.entrySet())
		{
			String j = v.getKey();
			String formula = null;
			for(String f : formulamap.keySet())
			{
				if(f.contains(j))
				{
					formula = formulamap.get(f);
					break;
				}
			}
			for(Entry<Integer, Integer> e : v.getValue().entrySet())
			{
				int level = e.getKey();
				int exp = e.getValue();
				for(int i = 1; i <= level; i++)
				{
					HashMap<String, Double> map = new HashMap<>();
					map.put("numjobs" , Integer.valueOf(numjobs).doubleValue());
					map.put("maxjobs" , Integer.valueOf(maxjobs).doubleValue());
					map.put("joblevel" , Integer.valueOf(level).doubleValue());
					ttexp = ttexp + new MathFormulaParser().parse(formula, map);
				}
				ttexp = ttexp + Integer.valueOf(exp).doubleValue();
			}
		}
		return ttexp;
	}
}