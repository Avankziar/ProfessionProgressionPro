package me.avankziar.ppp.spigot.cmd.profession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.assistance.ChatApi;
import me.avankziar.ppp.general.assistance.MatchApi;
import me.avankziar.ppp.general.cmdtree.ArgumentConstructor;
import me.avankziar.ppp.general.cmdtree.CommandSuggest;
import me.avankziar.ppp.general.cmdtree.CommandSuggest.Type;
import me.avankziar.ppp.general.objects.Compensation;
import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.general.objects.Profession;
import me.avankziar.ppp.general.objects.ProfessionFile;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.cmdtree.ArgumentModule;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.EconomyHandler;
import me.avankziar.ppp.spigot.handler.MessageHandler;
import me.avankziar.ppp.spigot.handler.ProfessionHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class ARGCompensation extends ArgumentModule
{
	private PPP plugin;
	//private ArgumentConstructor ac;
	
	public ARGCompensation(ArgumentConstructor ac)
	{
		super(ac);
		//this.ac = ac;
		this.plugin = PPP.getPlugin();
	}

	/**
	 * => /profession compensation [page] [professioncategory] 
	 */
	@Override
	public void run(CommandSender sender, String[] args) throws IOException 
	{
		Player player = (Player) sender;
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				task(player, args);
			}
		}.runTaskAsynchronously(plugin);
	}
	
	private void task(Player player, String[] args)
	{
		int page = 0;
		String profcat = null;
		if(args.length >= 2)
		{
			if(MatchApi.isInteger(args[1]))
			{
				page = Integer.valueOf(args[1]);
			}
		}
		if(args.length >= 3)
		{
			profcat = args[1];
		}
		if(profcat == null)
		{
			withoutCategory(player, page);
		} else
		{
			withCategory(player, page, profcat);
		}
		
	}
	
	private void withoutCategory(Player player, int page)
	{
		ArrayList<Profession> activeProfession = ProfessionHandler.getActiveProfession(player.getUniqueId());
		ArrayList<Compensation> com = new ArrayList<>();
		for(Profession prof : activeProfession)
		{
			Optional<ProfessionFile> opf = ProfessionHandler.getProfession().values().stream()
					.filter(x -> x.getProfessionCategory().equals(prof.getProfessionCategory()))
					.filter(x -> x.getProfessionTitle().equals(prof.getProfessionTitle()))
					.findFirst();
			if(opf.isEmpty())
			{
				continue;
			}
			ProfessionFile pf = opf.get();
			for(Compensation c : pf.getCompensation())
			{
				Optional<Compensation> existingComp = com.stream()
                        .filter(x -> x.getEventType() == c.getEventType())
                        .filter(x -> Objects.equals(x.getMaterial(), c.getMaterial()))
                        .filter(x -> Objects.equals(x.getEntityType(), c.getEntityType()))
                        .findFirst();
				if (existingComp.isPresent()) 
				{
				    Compensation ec = existingComp.get();
				    Compensation newc = new Compensation(
				            c.getProfessionCategory(),
				            c.getEventType(),
				            c.getMaterial(),
				            c.getEntityType(),
				            c.getCompensationMoney() + ec.getCompensationMoney(),
				            c.getCompensationExperience() + ec.getCompensationExperience(),
				            c.isItemDrops());
				    com.remove(ec);
				    com.add(newc);
				} else 
				{
				    com.add(c);
				}
			}
		}
		Collections.sort(com, 
				Comparator.comparing(Compensation::getEventType)
				.thenComparing(Compensation::getCompensationMoney, Comparator.reverseOrder())
				.thenComparing(Compensation::getCompensationExperience, Comparator.reverseOrder())
				);
		end(player, page, null, com);
	}
	
	private void withCategory(Player player, int page, String profcat)
	{
		Profession prof = plugin.getMysqlHandler().getData(new Profession(), 
				"`id` ASC", "`player_uuid` = ? AND `profession_category` = ?", player.getUniqueId().toString(), profcat);
		Optional<ProfessionFile> opf = null;
		if(prof != null)
		{
			opf = ProfessionHandler.getProfession().values().stream()
			.filter(x -> x.getProfessionCategory().equals(profcat))
			.filter(x -> x.getProfessionTitle().equals(prof.getProfessionTitle()))
			.findFirst();
		} else
		{
			ProfessionHandler.getStartProfession().values().stream()
			.filter(x -> x.getProfessionCategory().equals(profcat))
			.findFirst();
		}
		if(opf.isEmpty())
		{
			MessageHandler.sendMessage(player, plugin.getYamlHandler().getLang().getString("Profession.Info.ProfessionCategoryDontExist"));
			return;
		}
		ProfessionFile pf = opf.get();
		ArrayList<Compensation> com = pf.getCompensation();
		Collections.sort(com, 
				Comparator.comparing(Compensation::getEventType)
				.thenComparing(Compensation::getCompensationMoney, Comparator.reverseOrder())
				.thenComparing(Compensation::getCompensationExperience, Comparator.reverseOrder())
				);
		end(player, page, profcat, com);
		
	}
	
	private void end(Player player, int page, String profcat, ArrayList<Compensation> com)
	{
		ArrayList<String> msg = new ArrayList<>();
		int have = 0;
		int index = 0;
		int quantity = 20;
		int start = page * quantity;
		if(start > com.size())
		{
			start = 0;
		}
		msg.add(plugin.getYamlHandler().getLang().getString("Profession.Compensation.Headline"));
		EventType actual = null;
		for(Compensation c : com)
		{
			if(have >= quantity)
			{
				break;
			}
			if(index < start)
			{
				index++;
				continue;
			}
			if(actual == null || actual != c.getEventType())
			{
				actual = c.getEventType();
				msg.add(plugin.getYamlHandler().getLang().getString("Profession.Compensation.EventType")
						.replace("%eventtype%", c.getEventType().toString()));
			}
			double expWGFac = PPP.getWorldGuard() 
					? WorldGuardHook.getMultiplierPEXP(player, player.getLocation()) 
					: 1;
			double moneyWGFac = PPP.getWorldGuard() 
					? WorldGuardHook.getMultiplierMoney(player, player.getLocation()) 
					: 1;
			String matOREntity = null;
			String money = null;
			String exp = null;
			if(c.getMaterial() != null)
			{
				matOREntity = plugin.getEnumTransaltion() != null ? plugin.getEnumTransaltion().getLocalization(c.getMaterial()) : c.getMaterial().toString();
				double moneyBFac = BoosterHandler.getBoosterMoney(player, c.getEventType(), c.getMaterial());
				double mres = c.getCompensationMoney()*moneyWGFac*moneyBFac;
				money = EconomyHandler.formatDouble(c.getCompensationMoney())+"*"+EconomyHandler.formatDouble(moneyWGFac)+"*"+EconomyHandler.formatDouble(moneyBFac)+"="+EconomyHandler.format(mres);
				double expBFac = BoosterHandler.getBoosterExperience(player, c.getEventType(), c.getMaterial());
				double eres = c.getCompensationExperience()*expWGFac*expBFac;
				exp = EconomyHandler.formatDouble(c.getCompensationExperience())+"*"+EconomyHandler.formatDouble(expWGFac)+"*"+EconomyHandler.formatDouble(expBFac)+"="+EconomyHandler.formatDouble(eres);
				
			} else
			{
				matOREntity = plugin.getEnumTransaltion() != null ? plugin.getEnumTransaltion().getLocalization(c.getEntityType()) : c.getEntityType().toString();
				double moneyBFac = BoosterHandler.getBoosterMoney(player, c.getEventType(), c.getEntityType());
				double mres = c.getCompensationMoney()*moneyWGFac*moneyBFac;
				money = EconomyHandler.formatDouble(c.getCompensationMoney())+"*"+EconomyHandler.formatDouble(moneyWGFac)+"*"+EconomyHandler.formatDouble(moneyBFac)+"="+EconomyHandler.format(mres);
				double expBFac = BoosterHandler.getBoosterExperience(player, c.getEventType(), c.getEntityType());
				double eres = c.getCompensationExperience()*expWGFac*expBFac;
				exp = EconomyHandler.formatDouble(c.getCompensationExperience())+"*"+EconomyHandler.formatDouble(expWGFac)+"*"+EconomyHandler.formatDouble(expBFac)+"="+EconomyHandler.formatDouble(eres);
			}
			msg.add(plugin.getYamlHandler().getLang().getString("Profession.Compensation.Line")
					.replace("%matORentity%", matOREntity)
					.replace("%money%", money)
					.replace("%exp%", exp)
					);
			have++;
		}
		String pastNext = "";
		if(profcat != null)
		{
			if(page > 0)
			{
				pastNext += ChatApi.click(plugin.getYamlHandler().getLang().getString("Past"), 
						"RUN_COMMAND", 
						CommandSuggest.getCmdString(Type.PROFESSION_COMPENSATION).strip()+" "+(page-1)+" "+profcat);
			}
			if(page > 0 && start+quantity < com.size())
			{
				pastNext += " <white>| ";
			}
			if(start+quantity < com.size())
			{
				pastNext += ChatApi.click(plugin.getYamlHandler().getLang().getString("Next"), 
						"RUN_COMMAND", 
						CommandSuggest.getCmdString(Type.PROFESSION_COMPENSATION).strip()+" "+(page+1));
			}
		} else
		{
			if(page > 0)
			{
				pastNext += ChatApi.click(plugin.getYamlHandler().getLang().getString("Past"), 
						"RUN_COMMAND", 
						CommandSuggest.getCmdString(Type.PROFESSION_COMPENSATION).strip()+" "+(page-1));
			}
			if(page > 0 && start+quantity < com.size())
			{
				pastNext += " <white>| ";
			}
			if(start+quantity < com.size())
			{
				pastNext += ChatApi.click(plugin.getYamlHandler().getLang().getString("Next"), 
						"RUN_COMMAND", 
						CommandSuggest.getCmdString(Type.PROFESSION_COMPENSATION).strip()+" "+(page+1));
			}
		}
		if(!pastNext.isEmpty())
		{
			msg.add(pastNext);
		}
		MessageHandler.sendMessage(player.getUniqueId(), msg.toArray(msg.toArray(new String[msg.size()])));
	}
}