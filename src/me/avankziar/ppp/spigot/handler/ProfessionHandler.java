package me.avankziar.ppp.spigot.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import me.avankziar.ppp.general.objects.Profession;
import me.avankziar.ppp.general.objects.ProfessionFile;

public class ProfessionHandler 
{
	private static LinkedHashMap<UUID, ArrayList<Profession>> activeProfession = new LinkedHashMap<>();
	
	public static LinkedHashMap<UUID, ArrayList<Profession>> getActiveProfession()
	{
		return activeProfession;
	}
	
	public static ArrayList<Profession> getActiveProfession(UUID uuid)
	{
		return activeProfession.get(uuid);
	}
	
	public static void addProfession(Profession profession)
	{
		if(profession == null)
		{
			return;
		}
		ArrayList<Profession> pa = getActiveProfession(profession.getUUID());
		for(Profession p : pa)
		{
			if(p.getProfessionCategory().equals(profession.getProfessionCategory()))
			{
				return;
			}
		}
		pa.add(profession);
		activeProfession.put(profession.getUUID(), pa);
	}
	
	public static void removePlayer(UUID uuid)
	{
		activeProfession.remove(uuid);
	}
	
	private static LinkedHashMap<String, ProfessionFile> allProfessions = new LinkedHashMap<>(); //Key professionTilte
	
	public static ProfessionFile getProfession(String professionTitle)
	{
		return allProfessions.get(professionTitle);
	}
}