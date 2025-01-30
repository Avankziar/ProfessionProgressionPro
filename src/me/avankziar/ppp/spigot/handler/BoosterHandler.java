package me.avankziar.ppp.spigot.handler;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.avankziar.ppp.general.objects.Booster;
import me.avankziar.ppp.general.objects.EventType;

public class BoosterHandler 
{
	private static ArrayList<Booster> booster = new ArrayList<>();
	
	public static ArrayList<Booster> getBooster()
	{
		return booster;
	}
	
	public static double getBoosterMoney(Player player, EventType eventType, Material material)
	{
		double a = 1.0;
		for(Booster b : booster)
		{
			if(b == null)
			{
				continue;
			}
			if(player.hasPermission(b.getPermission()))
			{
				if(b.getEventType() != null && b.getEventType() != eventType)
				{
					continue;
				}
				if(b.getMaterial() != null && b.getMaterial() != material)
				{
					continue;
				}
				a = a + b.getMultiplicatorMoney();
			}
		}
		return a;
	}
	
	public static double getBoosterMoney(Player player, EventType eventType, EntityType entityType)
	{
		double a = 1.0;
		for(Booster b : booster)
		{
			if(b == null)
			{
				continue;
			}
			if(player.hasPermission(b.getPermission()))
			{
				if(b.getEventType() != null && b.getEventType() != eventType)
				{
					continue;
				}
				if(b.getEntityType() != null && b.getEntityType() != entityType)
				{
					continue;
				}
				a = a + b.getMultiplicatorMoney();
			}
		}
		return a;
	}
	
	public static double getBoosterExperience(Player player, EventType eventType, Material material)
	{
		double a = 1.0;
		for(Booster b : booster)
		{
			if(b == null)
			{
				continue;
			}
			if(player.hasPermission(b.getPermission()))
			{
				if(b.getEventType() != null && b.getEventType() != eventType)
				{
					continue;
				}
				if(b.getMaterial() != null && b.getMaterial() != material)
				{
					continue;
				}
				a = a + b.getMultiplicatorExperience();
			}
		}
		return a;
	}
	
	public static double getBoosterExperience(Player player, EventType eventType, EntityType entityType)
	{
		double a = 1.0;
		for(Booster b : booster)
		{
			if(b == null)
			{
				continue;
			}
			if(player.hasPermission(b.getPermission()))
			{
				if(b.getEventType() != null && b.getEventType() != eventType)
				{
					continue;
				}
				if(b.getEntityType() != null && b.getEntityType() != entityType)
				{
					continue;
				}
				a = a + b.getMultiplicatorExperience();
			}
		}
		return a;
	}
	
	public static void init()
	{
		//ADDME ConfigLoad
	}
}