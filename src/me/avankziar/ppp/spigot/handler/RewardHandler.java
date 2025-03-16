package me.avankziar.ppp.spigot.handler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
import me.avankziar.ppp.general.objects.Profession;
import me.avankziar.ppp.general.objects.ProfessionCompensationLog;
import me.avankziar.ppp.general.objects.ProfessionFile;
import me.avankziar.ppp.general.objects.ProfessionFile.CompensationType;
import me.avankziar.ppp.general.objects.ProfessionFile.WorldStatus;
import me.avankziar.ppp.general.objects.Reward;
import me.avankziar.ppp.spigot.PPP;

public class RewardHandler 
{
	private static ConcurrentHashMap<UUID, ArrayList<Reward>> toProcessRewards = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<UUID, ArrayList<Reward>> toLogRecordRewards = new ConcurrentHashMap<>();
	
	public static ArrayList<Reward> getReward(UUID uuid)
	{
		return toProcessRewards.get(uuid);
	}
	
	public static void addReward(UUID uuid, String worldname, EventType eventType, Material material, 
			double amount, double factorExp, double factorMoney)
	{
		Reward r = null;
		ArrayList<Reward> ra = getReward(uuid);
		ArrayList<Profession> pa = ProfessionHandler.getActiveProfession(uuid);
		for(Profession p : pa)
		{
			ProfessionFile pf = ProfessionHandler.getProfession(p.getProfessionTitle());
			if(pf == null)
			{
				continue;
			}
			for(Compensation c : pf.getCompensation())
			{
				if(c.getEventType() == eventType
						&& c.getMaterial() == material)
				{
					CompensationType ct = CompensationType.BOTH;
					if(pf.getWorldStatus() != WorldStatus.DONT_CARE)
					{
						switch(pf.getWorldStatus())
						{
						case DONT_CARE: break;
						case BLACKLIST:
							if(!pf.getWorldList().containsKey(worldname))
							{
								break;
							}
							break;
						case WHITELIST:
							if(pf.getWorldList().containsKey(worldname))
							{
								ct = pf.getWorldList().get(worldname);
							}
						}
					}
					if(ct == CompensationType.NOTHING)
					{
						continue;
					}
					if(ra == null || ra.isEmpty())
					{
						r = new Reward(c.getProfessionCategory(), eventType, material, amount,
								ct == CompensationType.BOTH || ct == CompensationType.ONLY_EXP 
								? amount * factorExp * c.getCompensationExperience() : 0.0,
								ct == CompensationType.BOTH || ct == CompensationType.ONLY_MONEY 
								? amount * factorMoney * c.getCompensationMoney() : 0.0);
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
									ct == CompensationType.BOTH || ct == CompensationType.ONLY_EXP 
									? amount * factorExp * c.getCompensationExperience() : 0.0,
									ct == CompensationType.BOTH || ct == CompensationType.ONLY_MONEY 
									? amount * factorMoney * c.getCompensationMoney() : 0.0);
							ra.add(r);
						}
						toProcessRewards.put(uuid, ra);
					}
					sendActionbar(uuid, eventType, material, null, r.getTotalMoney(), r.getTotalExp());
				}
			}
		}		
	}
	
	public static void addReward(UUID uuid, String worldname, EventType eventType, EntityType entityType, 
			double amount, double factorExp, double factorMoney)
	{
		Reward r = null;
		ArrayList<Reward> ra = getReward(uuid);
		ArrayList<Profession> pa = ProfessionHandler.getActiveProfession(uuid);
		for(Profession p : pa)
		{
			ProfessionFile pf = ProfessionHandler.getProfession(p.getProfessionTitle());
			if(pf == null)
			{
				continue;
			}
			for(Compensation c : pf.getCompensation())
			{
				if(c.getEventType() == eventType
						&& c.getEntityType() == entityType)
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
					sendActionbar(uuid, eventType, null, entityType, r.getTotalMoney(), r.getTotalExp());
				}
			}
		}
	}
	
	public static void init()
	{
		SENDACTIONBAR = PPP.getPlugin().getYamlHandler().getConfig().getBoolean("Default.SendActionBarByAction");
		ACTIONBARMESSAGE = PPP.getPlugin().getYamlHandler().getConfig().getString("Default.SendActionBarByActionMessage");
		long runProcessRewardInSeconds = PPP.getPlugin().getYamlHandler().getConfig().getLong("Reward.Task.Process");
		new BukkitRunnable() 
		{	
			@Override
			public void run() 
			{
				if(PPP.SHUTDOWN)
				{
					return;
				}
				new BukkitRunnable() 
				{
					@Override
					public void run() 
					{
						final ConcurrentHashMap<UUID, ArrayList<Reward>> rewards = toProcessRewards;
						toProcessRewards.clear();
						for(Entry<UUID, ArrayList<Reward>> e : rewards.entrySet())
						{
							processReward(e.getKey(), e.getValue());
						}
					}
				}.runTaskAsynchronously(PPP.getPlugin());
			}
		}.runTaskTimer(PPP.getPlugin(), 0, runProcessRewardInSeconds * 20L);
		
		List<String> logReward = PPP.getPlugin().getYamlHandler().getConfig().getStringList("Reward.Task.Log");
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
				if(PPP.SHUTDOWN)
				{
					return;
				}
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
	
	public static void shutdown()
	{
		final ConcurrentHashMap<UUID, ArrayList<Reward>> rewards = toProcessRewards;
		toProcessRewards.clear();
		for(Entry<UUID, ArrayList<Reward>> e : rewards.entrySet())
		{
			processReward(e.getKey(), e.getValue());
		}
		logrecordReward();
	}
	
	public static void processReward(UUID uuid, ArrayList<Reward> ra)
	{
		ArrayList<Profession> pa = ProfessionHandler.getActiveProfession(uuid);
		toLogRecordRewards.put(uuid, ra);
		Player player = Bukkit.getPlayer(uuid);
		PlayerData pd = PPP.getPlugin().getMysqlHandler().getData(new PlayerData(),
				"`player_uuid` = ?", uuid.toString());
		int actions = 0;
		double money = 0.0;
		double pxp = 0.0;
		for(Reward r : ra)
		{
			Optional<Profession> op = pa.stream()
					.filter(x -> x.getProfessionCategory().equals(r.getProfessionCategory()))
					.findFirst();
			if(op.isEmpty())
			{
				continue;
			}
			Profession p = op.get();
			p.setActualProfessionExperience(p.getActualProfessionExperience()+r.getTotalExp());
			p.setTotalProfessionCompensationMoney(p.getTotalProfessionCompensationMoney()+r.getTotalMoney());
			String category = PPP.getPlugin().getYamlHandler().getLang().getString("EconomyCategory");
			String comment = PPP.getPlugin().getYamlHandler().getLang().getString("EconomyComment")
					.replace("%eventtype%", r.getEventType().toString())
					.replace("%whattype%", r.getMaterial() != null 
					? r.getMaterial().toString() : r.getEntityType().toString())
					.replace("%amount%", String.valueOf(r.getAmount()));
			EconomyHandler.deposit(uuid, r.getTotalMoney(), category, comment);
		}
		pa.stream().forEach(x ->
		{
			ProfessionHandler.addProfession(x);
			PPP.getPlugin().getMysqlHandler().updateData(x, "`id` = ?", x.getId());
		});
		if(player != null && pd.getProcessRewardMessageType() != RewardMessageType.NONE && !PPP.SHUTDOWN)
		{
			String msg = PPP.getPlugin().getYamlHandler().getLang().getString("Log.InfoMessage")
					.replace("%amount%", String.valueOf(actions))
					.replace("%money%", EconomyHandler.format(money))
					.replace("%pexp%", String.valueOf(pxp));
			switch(pd.getProcessRewardMessageType())
			{
			case NONE: break;
			case CHAT:
				MessageHandler.sendMessage(uuid, msg); break;
			case ACTIONBAR:
				MessageHandler.sendActionBar(uuid, msg); break;
			}
		}
		
	}
	
	public static void logrecordReward()
	{
		final ConcurrentHashMap<UUID, ArrayList<Reward>> rewards = toLogRecordRewards;
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
	
	private static boolean SENDACTIONBAR;
	private static String ACTIONBARMESSAGE;
	
	public static void sendActionbar(UUID uuid, EventType evt, Material mat, EntityType ent, double money, double pexp)
	{
		if(!SENDACTIONBAR)
		{
			return;
		}
		MessageHandler.sendActionBar(uuid, ACTIONBARMESSAGE
				.replace("%eventtype%", evt.toString())
				.replace("%whattype%", mat != null
				? mat.toString() : ent.toString())
				.replace("%money%", EconomyHandler.format(money))
				.replace("%exp%", EconomyHandler.formatDouble(pexp))
				);
	}
}