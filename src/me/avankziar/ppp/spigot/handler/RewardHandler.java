package me.avankziar.ppp.spigot.handler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.assistance.MatchApi;
import me.avankziar.ppp.general.objects.Compensation;
import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.general.objects.PlayerData;
import me.avankziar.ppp.general.objects.PlayerData.RewardMessageType;
import me.avankziar.ppp.general.objects.ProfessionCompensationLog;
import me.avankziar.ppp.general.objects.Reward;
import me.avankziar.ppp.spigot.PPP;

public class RewardHandler 
{
	private static LinkedHashMap<UUID, ArrayList<Reward>> toProcessRewards = new LinkedHashMap<>();
	private static LinkedHashMap<UUID, ArrayList<Reward>> toLogRecordRewards = new LinkedHashMap<>();
	
	
	public static ArrayList<Reward> getReward(UUID uuid)
	{
		return toProcessRewards.get(uuid);
	}
	
	public static void addReward(UUID uuid, EventType eventType, Material material, 
			double amount, double factorExp, double factorMoney)
	{
		Reward r = null;
		ArrayList<Reward> ra = getReward(uuid);
		ArrayList<Compensation> com = CompensationHandler.getCompensation(uuid, eventType, material);
		if(com.isEmpty())
		{
			return;
		}
		for(Compensation c : com)
		{
			if(ra == null || ra.isEmpty())
			{
				r = new Reward(c.getProfessionCategory(), eventType, material, amount,
						amount * factorExp * c.getCompensationExperience(),
						amount * factorMoney * c.getCompensationMoney());
				ra = new ArrayList<>();
				ra.add(r);
				toProcessRewards.put(uuid, ra);
			} else
			{
				int index = 0;
				for(Reward rw : ra)
				{
					if(rw.getEventType() == eventType
							&& rw.getMaterial() == material)
					{
						r = rw;
						break;
					}
					index++;
				}
				if(r != null)
				{
					r.add(amount, amount * factorExp, amount * factorMoney);
					ra.set(index, r);
				} else
				{
					r = new Reward(c.getProfessionCategory(), eventType, material, amount,
							amount * factorExp * c.getCompensationExperience(),
							amount * factorMoney * c.getCompensationMoney());
					ra.add(r);
				}
				toProcessRewards.put(uuid, ra);
			}
		}		
	}
	
	public static void addReward(UUID uuid, EventType eventType, EntityType entityType, 
			double amount, double factorExp, double factorMoney)
	{
		Reward r = null;
		ArrayList<Reward> ra = getReward(uuid);
		ArrayList<Compensation> com = CompensationHandler.getCompensation(uuid, eventType, entityType);
		if(com.isEmpty())
		{
			return;
		}
		for(Compensation c : com)
		{
			if(ra == null || ra.isEmpty())
			{
				r = new Reward(c.getProfessionCategory(), eventType, entityType, amount,
						amount * factorExp * c.getCompensationExperience(),
						amount * factorMoney * c.getCompensationMoney());
				ra = new ArrayList<>();
				ra.add(r);
				toProcessRewards.put(uuid, ra);
			} else
			{
				int index = 0;
				for(Reward rw : ra)
				{
					if(rw.getEventType() == eventType
							&& rw.getEntityType() == entityType)
					{
						r = rw;
						break;
					}
					index++;
				}
				if(r != null)
				{
					r.add(amount, amount * factorExp, amount * factorMoney);
					ra.set(index, r);
				} else
				{
					r = new Reward(c.getProfessionCategory(), eventType, entityType, amount,
							amount * factorExp * c.getCompensationExperience(),
							amount * factorMoney * c.getCompensationMoney());
					ra.add(r);
				}
				toProcessRewards.put(uuid, ra);
			}
		}		
	}
	
	public static void init()
	{
		long runProcessRewardInSeconds = PPP.getPlugin().getYamlHandler().getConfig().getLong(""); //FIXME Config
		new BukkitRunnable() 
		{	
			@Override
			public void run() 
			{
				new BukkitRunnable() 
				{
					@Override
					public void run() 
					{
						processReward();
					}
				}.runTaskAsynchronously(PPP.getPlugin());
			}
		}.runTaskTimer(PPP.getPlugin(), 0, runProcessRewardInSeconds * 20L);
		
		List<String> logReward = PPP.getPlugin().getYamlHandler().getConfig().getStringList(""); //FIXME Config
		HashSet<Integer> logRewardStartMinutes = new HashSet<>();
		logReward.forEach(x ->
		{
			if(MatchApi.isInteger(x))
			{
				logRewardStartMinutes.add(Integer.valueOf(x));
			}
		});
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				LocalTime lt = LocalTime.now();
				if(logRewardStartMinutes.contains(lt.getMinute()))
				{
					new BukkitRunnable() 
					{
						@Override
						public void run() 
						{
							logrecordReward();
						}
					}.runTaskAsynchronously(PPP.getPlugin());
				}
			}
		}.runTaskTimer(PPP.getPlugin(), 0, 60 * 20L);
	}
	
	public static void processReward()
	{
		final LinkedHashMap<UUID, ArrayList<Reward>> rewards = toProcessRewards;
		toProcessRewards.clear();
		for(Entry<UUID, ArrayList<Reward>> e : rewards.entrySet())
		{
			toLogRecordRewards.put(e.getKey(), e.getValue());
			Player player = Bukkit.getPlayer(e.getKey());
			PlayerData pd = PPP.getPlugin().getMysqlHandler().getData(new PlayerData(),
					"`player_uuid` = ?", e.getKey().toString());
			for(Reward r : e.getValue())
			{
				String category = PPP.getPlugin().getYamlHandler().getLang().getString(""); //FIXME Config
				String comment = PPP.getPlugin().getYamlHandler().getLang().getString("") //FIXME Config
						.replace("%eventtype%", r.getEventType().toString())
						.replace("%whattype%", r.getMaterial() != null 
						? r.getMaterial().toString() : r.getEntityType().toString())
						.replace("%amount%", String.valueOf(r.getAmount()));
				EconomyHandler.deposit(e.getKey(), r.getTotalMoney(), category, comment);
				if(player != null && pd.getRewardMessageType() != RewardMessageType.NONE)
				{
					switch(pd.getRewardMessageType())
					{
					case NONE: break;
					case CHAT:
						MessageHandler.sendMessage(e.getKey(), 
								PPP.getPlugin().getYamlHandler().getLang().getString("") //FIXME Config
								.replace("%eventtype%", r.getEventType().toString())
								.replace("%whattype%", r.getMaterial() != null
								? r.getMaterial().toString() : r.getEntityType().toString())
								.replace("%amount%", String.valueOf(r.getAmount()))
								.replace("%money%", EconomyHandler.format(r.getTotalMoney()))
								.replace("%exp%", String.valueOf(r.getTotalExp()))
								);
						break;
					case ACTIONBAR:
						MessageHandler.sendActionBar(e.getKey(), 
								PPP.getPlugin().getYamlHandler().getLang().getString("") //FIXME Config
								.replace("%eventtype%", r.getEventType().toString())
								.replace("%whattype%", r.getMaterial() != null
								? r.getMaterial().toString() : r.getEntityType().toString())
								.replace("%amount%", String.valueOf(r.getAmount()))
								.replace("%money%", EconomyHandler.format(r.getTotalMoney()))
								.replace("%exp%", String.valueOf(r.getTotalExp()))
								);
						break;
					}
				}
			}
		}
	}
	
	public static void logrecordReward()
	{
		final LinkedHashMap<UUID, ArrayList<Reward>> rewards = toLogRecordRewards;
		toLogRecordRewards.clear();
		for(Entry<UUID, ArrayList<Reward>> e : rewards.entrySet())
		{
			for(Reward r : e.getValue())
			{
				long now = System.currentTimeMillis();
				ProfessionCompensationLog pcl = new ProfessionCompensationLog(0, e.getKey(),
					r.getProfessionCategory(), now, r.getEventType(), r.getMaterial(), r.getEntityType(), 
					(int) r.getAmount(), r.getTotalMoney(), EconomyHandler.getCurrency(), r.getTotalExp());
				PPP.getPlugin().getMysqlHandler().create(pcl);
			}
		}
	}
}