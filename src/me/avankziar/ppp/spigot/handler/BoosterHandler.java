package me.avankziar.ppp.spigot.handler;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import me.avankziar.ppp.general.objects.Booster;

public class BoosterHandler 
{
	private static ArrayList<Booster> booster = new ArrayList<>();
	
	public static ArrayList<Booster> getBooster()
	{
		return booster;
	}
	
	public static double getBoosterMoney(Player player)
	{
		double a = 1.0;
		for(Booster x : booster)
		{
			if(x == null)
			{
				continue;
			}
			if(player.hasPermission(x.getPermission()))
			{
				a = a * x.getMultiplicatorMoney();
			}
		}
		return a;
	}
	
	public static double getBoosterExperience(Player player)
	{
		double a = 1.0;
		for(Booster x : booster)
		{
			if(x == null)
			{
				continue;
			}
			if(player.hasPermission(x.getPermission()))
			{
				a = a * x.getMultiplicatorExperience();
			}
		}
		return a;
	}
	
	public static void init()
	{
		//ADDME
	}
}