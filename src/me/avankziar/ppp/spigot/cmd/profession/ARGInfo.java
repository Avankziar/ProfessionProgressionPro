package me.avankziar.ppp.spigot.cmd.profession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.assistance.ChatApi;
import me.avankziar.ppp.general.cmdtree.ArgumentConstructor;
import me.avankziar.ppp.general.cmdtree.CommandSuggest;
import me.avankziar.ppp.general.cmdtree.CommandSuggest.Type;
import me.avankziar.ppp.general.objects.Profession;
import me.avankziar.ppp.general.objects.ProfessionFile;
import me.avankziar.ppp.general.objects.ProfessionProgression;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.cmdtree.ArgumentModule;
import me.avankziar.ppp.spigot.handler.EconomyHandler;
import me.avankziar.ppp.spigot.handler.MessageHandler;
import me.avankziar.ppp.spigot.handler.ProfessionHandler;

public class ARGInfo extends ArgumentModule
{
	private PPP plugin;
	
	public ARGInfo(ArgumentConstructor ac)
	{
		super(ac);
		this.plugin = PPP.getPlugin();
	}

	/**
	 * => /profession info [professioncategory]
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
		ArrayList<String> msg = new ArrayList<>();
		if(args.length == 1)
		{
			msg.add(plugin.getYamlHandler().getLang().getString("Profession.Base.Headline"));
			ArrayList<Profession> pa = ProfessionHandler.getActiveProfession(player.getUniqueId());
			plugin.getMysqlHandler().getFullList(new Profession(), 
					"`id` ASC", "`player_uuid` = ? AND `is_active` = ?", player.getUniqueId().toString(), false)
			.forEach(x -> pa.add(x));
			for(Profession prof : pa)
			{
				ProfessionFile pf = ProfessionHandler.getProfession(prof.getProfessionTitle());
				if(prof.isActive())
				{
					msg.add(ChatApi.clickHover(plugin.getYamlHandler().getLang().getString("Profession.Base.Info")
							.replace("%titel%", prof.getProfessionTitle())
							.replace("%actualexp%", String.valueOf(prof.getActualProfessionExperience()))
							.replace("%maxpexp%", String.valueOf(pf.getPromotionNeededProfessionExperience())),
							"RUN_COMMAND", CommandSuggest.get(Type.PROFESSION_INFO)+pf.getProfessionCategory(),
							"SHOW_TEXT", plugin.getYamlHandler().getLang().getString("GeneralHover")
							));
				} else
				{
					msg.add(ChatApi.clickHover(plugin.getYamlHandler().getLang().getString("Profession.Info.Info")
							.replace("%titel%", prof.getProfessionTitle())
							.replace("%actualexp%", String.valueOf(prof.getActualProfessionExperience()))
							.replace("%maxpexp%", String.valueOf(pf.getPromotionNeededProfessionExperience())),
							"RUN_COMMAND", CommandSuggest.get(Type.PROFESSION_INFO)+pf.getProfessionCategory(),
							"SHOW_TEXT", plugin.getYamlHandler().getLang().getString("GeneralHover")
							));
				}
			}
			msg.add(plugin.getYamlHandler().getLang().getString("Profession.Base.Bottomline"));
			msg.forEach(x -> MessageHandler.sendMessage(player, x));
			return;
		}
		String profcat = args[1];
		Profession prof = plugin.getMysqlHandler().getData(new Profession(), 
				"`id` ASC", "`player_uuid` = ? AND `profession_category` = ?", player.getUniqueId().toString(), profcat);
		Optional<ProfessionFile> opf = null;
		if(prof != null)
		{
			opf = ProfessionHandler.getStartProfession().values().stream()
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
		double moneySum = plugin.getMysqlHandler().getSum(new ProfessionProgression(), 
				"`money_total_received` > ? `player_uuid` = ? AND `profession_category` = ?", 0.0, player.getUniqueId().toString(), profcat);
		msg.add(plugin.getYamlHandler().getLang().getString("Profession.Info.Headline").replace("%profcat%", profcat));
		msg.add(plugin.getYamlHandler().getLang().getString("Profession.Info.ProfessionTitle").replace("%proftitle%", pf.getProfessionTitle()));
		if(prof != null)
		{
			msg.add(plugin.getYamlHandler().getLang().getString("Profession.Info.ProfessionExperience")
					.replace("%actualexp%", String.valueOf(prof.getActualProfessionExperience()))
					.replace("%maxexp%", String.valueOf(pf.getPromotionNeededProfessionExperience())));
			if(prof.getActualProfessionExperience() >= pf.getPromotionNeededProfessionExperience())
			{
				msg.add(ChatApi.clickHover(plugin.getYamlHandler().getLang().getString("Profession.Info.YouCanBePromote"),
						"RUN_COMMAND", CommandSuggest.get(Type.PROFESSION_PROMOTE)+pf.getProfessionCategory(),
						"SHOW_TEXT", plugin.getYamlHandler().getLang().getString("GeneralHover")));
			}
			msg.add(plugin.getYamlHandler().getLang().getString("Profession.Info.MoneyPerTitle")
					.replace("%money%", EconomyHandler.format(prof.getTotalProfessionCompensationMoney())));
		}
		if(moneySum >= 0)
		{
			msg.add(plugin.getYamlHandler().getLang().getString("Profession.Info.MoneyTotal")
					.replace("%money%", EconomyHandler.format(moneySum)));
		}
		if(!pf.getCompensation().isEmpty())
		{
			msg.add(ChatApi.clickHover(plugin.getYamlHandler().getLang().getString("Profession.Info.Compensation"),
					"RUN_COMMAND", CommandSuggest.get(Type.PROFESSION_COMPENSATION)+pf.getProfessionCategory(),
					"SHOW_TEXT", plugin.getYamlHandler().getLang().getString("GeneralHover")));
		}
		msg.add(plugin.getYamlHandler().getLang().getString("Profession.Info.Bottomline"));
		msg.forEach(x -> MessageHandler.sendMessage(player, x));
	}
}