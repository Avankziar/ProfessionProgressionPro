package me.avankziar.ppp.spigot.handler;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import me.avankziar.ppp.general.objects.Compensation;
import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.general.objects.Profession;
import me.avankziar.ppp.general.objects.ProfessionFile;

public class CompensationHandler 
{
	public static boolean dropItems(UUID uuid, EventType eventType, Material mat)
	{
		ArrayList<Profession> pa = ProfessionHandler.getActiveProfession(uuid);
		for(Profession p : pa)
		{
			ProfessionFile pf = ProfessionHandler.getProfession(p.getProfessionTitle());
			if(pf == null)
			{
				continue;
			}
			Optional<Compensation> oc = pf.getCompensation().stream()
			.filter(x -> x.getEventType() == eventType)
			.filter(x -> x.getMaterial() == mat)
			.filter(x -> !x.isItemDrops())
			.findAny();
			if(oc.isPresent())
			{
				if(!oc.get().isItemDrops())
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean dropItems(UUID uuid, EventType eventType, EntityType et)
	{
		ArrayList<Profession> pa = ProfessionHandler.getActiveProfession(uuid);
		for(Profession p : pa)
		{
			ProfessionFile pf = ProfessionHandler.getProfession(p.getProfessionTitle());
			if(pf == null)
			{
				continue;
			}
			Optional<Compensation> oc = pf.getCompensation().stream()
			.filter(x -> x.getEventType() == eventType)
			.filter(x -> x.getEntityType() == et)
			.filter(x -> !x.isItemDrops())
			.findAny();
			if(oc.isPresent())
			{
				if(!oc.get().isItemDrops())
				{
					return false;
				}
			}
		}
		return true;
	}
}