package me.avankziar.ppp.spigot.cmd.profession;

import java.io.IOException;
import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.assistance.MatchApi;
import me.avankziar.ppp.general.cmdtree.ArgumentConstructor;
import me.avankziar.ppp.general.objects.Profession;
import me.avankziar.ppp.general.objects.ProfessionFile;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.cmdtree.ArgumentModule;
import me.avankziar.ppp.spigot.handler.MessageHandler;
import me.avankziar.ppp.spigot.handler.ProfessionHandler;

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
		
	}
	
	private void withCategory(Player player, int page, String profcat)
	{
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
	}
}