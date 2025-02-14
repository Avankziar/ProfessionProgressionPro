package me.avankziar.ppp.spigot.cmd;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.avankziar.ppp.general.assistance.ChatApi;
import me.avankziar.ppp.general.cmdtree.ArgumentConstructor;
import me.avankziar.ppp.general.cmdtree.CommandConstructor;
import me.avankziar.ppp.general.cmdtree.CommandSuggest;
import me.avankziar.ppp.general.cmdtree.CommandSuggest.Type;
import me.avankziar.ppp.general.objects.Profession;
import me.avankziar.ppp.general.objects.ProfessionFile;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.ModifierValueEntry.ModifierValueEntry;
import me.avankziar.ppp.spigot.cmdtree.ArgumentModule;
import me.avankziar.ppp.spigot.handler.MessageHandler;
import me.avankziar.ppp.spigot.handler.ProfessionHandler;

public class ProfessionCommandExecutor implements CommandExecutor
{
	private PPP plugin;
	private static CommandConstructor cc;
	
	public ProfessionCommandExecutor(PPP plugin, CommandConstructor cc)
	{
		this.plugin = plugin;
		ProfessionCommandExecutor.cc = cc;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) 
	{
		if(cc == null)
		{
			return false;
		}
		if(args.length == 0)
		{
			if (!(sender instanceof Player)) 
			{
				plugin.getLogger().info("Cmd is only for Player!");
				return false;
			}
			Player player = (Player) sender;
			if(!ModifierValueEntry.hasPermission(player, cc))
			{
				///Du hast dafür keine Rechte!
				player.spigot().sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
				return false;
			}
			baseCommands(player); //Base and Info Command
			return true;
		}
		int length = args.length-1;
		ArrayList<ArgumentConstructor> aclist = cc.subcommands;
		for(int i = 0; i <= length; i++)
		{
			for(ArgumentConstructor ac : aclist)
			{
				if(args[i].equalsIgnoreCase(ac.getName()))
				{
					if(length >= ac.minArgsConstructor && length <= ac.maxArgsConstructor)
					{
						if (sender instanceof Player)
						{
							Player player = (Player) sender;
							if(ModifierValueEntry.hasPermission(player, ac))
							{
								ArgumentModule am = plugin.getArgumentMap().get(ac.getPath());
								if(am != null)
								{
									try
									{
										am.run(sender, args);
									} catch (IOException e)
									{
										e.printStackTrace();
									}
								} else
								{
									plugin.getLogger().info("ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
											.replace("%ac%", ac.getName()));
									player.spigot().sendMessage(ChatApi.tl(
											"ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
											.replace("%ac%", ac.getName())));
									return false;
								}
								return false;
							} else
							{
								player.spigot().sendMessage(ChatApi.tl(plugin.getYamlHandler().getLang().getString("NoPermission")));
								return false;
							}
						} else
						{
							ArgumentModule am = plugin.getArgumentMap().get(ac.getPath());
							if(am != null)
							{
								try
								{
									am.run(sender, args);
								} catch (IOException e)
								{
									e.printStackTrace();
								}
							} else
							{
								plugin.getLogger().info("ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
										.replace("%ac%", ac.getName()));
								sender.spigot().sendMessage(ChatApi.tl(
										"ArgumentModule from ArgumentConstructor %ac% not found! ERROR!"
										.replace("%ac%", ac.getName())));
								return false;
							}
							return false;
						}
					} else
					{
						aclist = ac.subargument;
						break;
					}
				}
			}
		}
		sender.spigot().sendMessage(ChatApi.tl(ChatApi.click(plugin.getYamlHandler().getLang().getString("InputIsWrong"),
				"RUN_COMMAND", CommandSuggest.getCmdString(CommandSuggest.Type.PROFESSION))));
		return false;
	}
	
	private void baseCommands(Player player)
	{
		ArrayList<String> msg = new ArrayList<>();
		msg.add(plugin.getYamlHandler().getLang().getString("Profession.Base.Headline"));
		for(Profession prof : ProfessionHandler.getActiveProfession(player.getUniqueId()))
		{
			ProfessionFile pf = ProfessionHandler.getProfession(prof.getProfessionTitle());
			msg.add(ChatApi.clickHover(plugin.getYamlHandler().getLang().getString("Profession.Base.Info")
					.replace("%titel%", prof.getProfessionTitle())
					.replace("%actualexp%", String.valueOf(prof.getActualProfessionExperience()))
					.replace("%maxpexp%", String.valueOf(pf.getPromotionNeededProfessionExperience())),
					"RUN_COMMAND", CommandSuggest.get(Type.PROFESSION_INFO)+pf.getProfessionCategory(),
					"SHOW_TEXT", plugin.getYamlHandler().getLang().getString("GeneralHover")
					));
		}
		msg.add(plugin.getYamlHandler().getLang().getString("Profession.Base.Bottomline"));
		msg.forEach(x -> MessageHandler.sendMessage(player, x));
	}
}