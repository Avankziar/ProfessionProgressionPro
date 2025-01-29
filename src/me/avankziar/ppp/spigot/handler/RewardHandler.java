package me.avankziar.ppp.spigot.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.general.objects.Reward;

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
		if(ra == null || ra.isEmpty())
		{
			r = new Reward(eventType, material, amount, amount * factorExp, amount * factorMoney);
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
				r = new Reward(eventType, material, amount, amount * factorExp, amount * factorMoney);
				ra.add(r);
			}
			toProcessRewards.put(uuid, ra);
		}
	}
	
	public static void addReward(UUID uuid, EventType eventType, EntityType entityType, 
			double amount, double factorExp, double factorMoney)
	{
		Reward r = null;
		ArrayList<Reward> ra = getReward(uuid);
		if(ra == null || ra.isEmpty())
		{
			r = new Reward(eventType, entityType, amount, amount * factorExp, amount * factorMoney);
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
				r = new Reward(eventType, entityType, amount, amount * factorExp, amount * factorMoney);
				ra.add(r);
			}
			toProcessRewards.put(uuid, ra);
		}
	}
	
	public static void init()
	{
		
	}
	
	public static void processReward()
	{
		final LinkedHashMap<UUID, ArrayList<Reward>> rewards = toProcessRewards;
		toProcessRewards.clear();
		for(Entry<UUID, ArrayList<Reward>> e : rewards.entrySet())
		{
			
		}
	}
	
	public static void logrecordReward()
	{
		
	}
}