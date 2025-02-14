package me.avankziar.ppp.spigot.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.avankziar.ppp.general.objects.Compensation;
import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.general.objects.Profession;
import me.avankziar.ppp.general.objects.ProfessionFile;
import me.avankziar.ppp.general.objects.ProfessionFile.CompensationType;
import me.avankziar.ppp.general.objects.ProfessionFile.WorldStatus;
import me.avankziar.ppp.spigot.PPP;

public class ProfessionHandler 
{
	private static LinkedHashMap<UUID, ArrayList<Profession>> activeProfession = new LinkedHashMap<>();
	
	public static LinkedHashMap<UUID, ArrayList<Profession>> getActiveProfession()
	{
		return activeProfession;
	}
	
	public static ArrayList<Profession> getActiveProfession(UUID uuid)
	{
		return activeProfession.containsKey(uuid) ?  activeProfession.get(uuid) : new ArrayList<>();
	}
	
	public static void addProfession(Profession profession)
	{
		if(profession == null)
		{
			return;
		}
		ArrayList<Profession> pa = getActiveProfession(profession.getUUID());
		int i = 0;
		for(Iterator<Profession> iter = pa.iterator(); iter.hasNext();)
		{
			Profession p = iter.next();
			if(p.getProfessionCategory().equals(profession.getProfessionCategory()))
			{
				pa.remove(i);
			}
			i++;
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
	
	private static LinkedHashMap<String, ProfessionFile> startProfessions = new LinkedHashMap<>(); //Key professionTilte
	
	public static LinkedHashMap<String, ProfessionFile> getStartProfession()
	{
		return startProfessions;
	}
	
	public static ProfessionFile getStartProfession(String professionTitle)
	{
		return startProfessions.get(professionTitle);
	}
	
	public static void initProfession()
	{
		for(YamlDocument y : PPP.getPlugin().getYamlHandler().getProfessions())
		{
			if(!y.contains("ProfessionCategory") || !y.contains("ProfessionTitle") || !y.contains("Compensation"))
			{
				PPP.logger.warning("");
				continue;
			}
			String professionCategory = y.getString("ProfessionCategory");
			String professionTitle = y.getString("ProfessionTitle");
			ArrayList<Compensation> compensation = new ArrayList<>();
			if(y.contains("Compensation"))
			{
				for(String s : y.getStringList("Compensation"))
				{
					String[] sa = s.split(";");
					if(sa.length != 7)
					{
						PPP.logger.info("Profession "+professionTitle+" has a problem at compensation! | "+s);
						continue;
					}
					try
					{
						String cprofessionCategory = sa[0];
						EventType eventType = EventType.valueOf(sa[1]);
						Material material = sa[2].equals("null") ? null : Material.valueOf(sa[2]);
						EntityType entityType = sa[3].equals("null") ? null : EntityType.valueOf(sa[3]);
						double compensationMoney = Double.valueOf(sa[4]);
						double compensationExperience = Double.valueOf(sa[5]);
						boolean itemDrops = Boolean.valueOf(sa[6]);
						compensation.add(new Compensation(cprofessionCategory, eventType, material, entityType,
								compensationMoney, compensationExperience, itemDrops));
					} catch(Exception e)
					{
						PPP.logger.info("Profession "+professionTitle+" has a problem at compensation! | "+s);
						continue;
					}
				}
			} else
			{
				PPP.logger.info("Profession "+professionTitle+" has a problem at compensation!");
				continue;
			}
			
			WorldStatus worldStatus = WorldStatus.valueOf(y.getString("CompensationStatusForWorlds", WorldStatus.DONT_CARE.toString()));
			
			LinkedHashMap<String, CompensationType> worldList = new LinkedHashMap<>();
			if(y.contains("CompensationPerWorld"))
			{
				for(String s : y.getStringList("CompensationPerWorld"))
				{
					String[] w = s.split(";");
					if(w.length != 2)
					{
						PPP.logger.info("Profession "+professionTitle+" has a problem at CompensationPerWorld! It should be one ; | "+s);
						continue;
					}
					try
					{
						String worldname = w[0];
						CompensationType ct = CompensationType.valueOf(w[1]);
						worldList.put(worldname, ct);
					} catch(Exception e)
					{
						PPP.logger.info("Profession "+professionTitle+" has a problem at CompensationPerWorld! | "+s);
						continue;
					}
				}
			}
			
			boolean isStartProfession = y.getBoolean("IsStartProfession", false);
			boolean degrationOnProfessionChange = y.getBoolean("Degradation.OnProfessionChange", false);
			String degradationProfessionTitle = y.getString("Degradation.ProfessionTitle").equals("null") 
					? null : y.getString("Degradation.ProfessionTitle");
			double degrationTransferOfWorkExperienceInPercent = y.getDouble("Degration.TransferOfWorkExperienceInPercent");
			
			String promotionProfessionTitle = y.getString("Promotion.ProfessionTitle").equals("null") 
					? null : y.getString("Promotion.ProfessionTitle");
			String promotionPermissionToAcquire = y.getString("Promotion.PermissionToAcquire").equals("null")
					? null : y.getString("Promotion.PermissionToAcquire");
			double promotionTeachingFee = y.getDouble("Promotion.TeachingFee");
			double promotionNeededProfessionExperience = y.getDouble("Promotion.NeededProfessionExperience");
			ArrayList<String> promotionExecuteConsoleCommands = new ArrayList<>();
			if(y.contains("Promotion.ExecuteConsoleCommands"))
			{
				for(String s : y.getStringList("Promotion.ExecuteConsoleCommands"))
				{
					promotionExecuteConsoleCommands.add(s);
				}
			}
			double promotionTransferOfWorkExperienceInPercent = y.getDouble("Promotion.TransferOfWorkExperienceInPercent");
			ProfessionFile pf = new ProfessionFile(professionCategory, professionTitle, compensation, worldList, worldStatus, isStartProfession,
					degrationOnProfessionChange,
					degradationProfessionTitle, degrationTransferOfWorkExperienceInPercent, 
					promotionProfessionTitle, promotionPermissionToAcquire, promotionTeachingFee,
					promotionNeededProfessionExperience, promotionExecuteConsoleCommands, promotionTransferOfWorkExperienceInPercent);
			allProfessions.put(professionTitle, pf);
			if(isStartProfession)
			{
				startProfessions.put(professionTitle, pf);
			}
		}
	}
}