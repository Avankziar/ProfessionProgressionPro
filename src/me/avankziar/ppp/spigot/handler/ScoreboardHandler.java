package me.avankziar.ppp.spigot.handler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import me.avankziar.ppp.general.assistance.ChatApi;
import me.avankziar.ppp.general.objects.PlayerData;
import me.avankziar.ppp.general.objects.Reward;
import me.avankziar.ppp.spigot.PPP;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ScoreboardHandler
{
	public static void setScoreBoard(Player player) 
	{
	    Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();        
	    Objective obj = board.registerNewObjective("ppp", Criteria.DUMMY, "Test Server");
	    obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	    scoreboard(player, board, obj);
	    new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if(player == null)
				{
					cancel();
					return;
				}
				try
				{
					updateScoreboard(player);
				} catch(Exception e) {}
			}
		}.runTaskTimer(PPP.getPlugin(), PPP.getPlugin().getYamlHandler().getConfig().getInt("Default.Scoreboard.Task") * 20L,
				PPP.getPlugin().getYamlHandler().getConfig().getInt("Default.Scoreboard.Task") * 20L);
	}
	
	public static void updateScoreboard(Player player)
	{
		Scoreboard board = player.getScoreboard();
		Objective obj = board.getObjective("ppp");
		scoreboard(player, board, obj);
	}
	
	private static void scoreboard(Player player, Scoreboard board, Objective obj)
	{
		PlayerData pd = PPP.getPlugin().getMysqlHandler().getData(new PlayerData(), "`player_uuid` = ?", player.getUniqueId().toString());
	    List<String> l = PPP.getPlugin().getYamlHandler().getConfig().getStringList("Default.Scoreboard.Board");
	    LegacyComponentSerializer lcs = LegacyComponentSerializer.builder().build();
	    double action = 0.0;
	    double money = 0.0;
	    double pexp = 0.0;
	    final ArrayList<Reward> ar = RewardHandler.getReward(player.getUniqueId());
	    LinkedHashMap<String, Double[]> profession = new LinkedHashMap<>();
	    for(Reward r : ar)
	    {
	    	action = action + r.getAmount();
	    	money = money + r.getTotalMoney();
	    	pexp = pexp + r.getTotalExp();
	    	if(profession.containsKey(r.getProfessionCategory()))
	    	{
	    		Double[] d = profession.get(r.getProfessionCategory());
	    		Double[] e = new Double[] {d[0]+r.getAmount(), d[1]+r.getTotalMoney(), d[2]+r.getTotalExp()};
	    		profession.put(r.getProfessionCategory(), e);
	    	} else
	    	{
	    		Double[] d = new Double[] {r.getAmount(), r.getTotalMoney(), r.getTotalExp()};
	    		profession.put(r.getProfessionCategory(), d);
	    	}
	    }
	    for(int i = 0; i < l.size(); i++)
	    {
	    	String s = replace(player, pd, action, money, pexp, sortByFirstValue(profession), l.get(15-i));
	    	Score sc = obj.getScore(lcs.serialize(ChatApi.all.deserialize(s.substring(0, 32))));
	    	sc.setScore(15-i);
	    }    
	    player.setScoreboard(board);
	}
	
	private static String replace(Player player, PlayerData pd, double action, double money, double pexp,
			LinkedHashMap<String, Double[]> profession, String s)
	{
		String r = s.replace("%playername%", player.getName())
				.replace("%onlineplayer%", String.valueOf(Bukkit.getOnlinePlayers().size()))
				.replace("%maxplayer%", String.valueOf(Bukkit.getMaxPlayers()))
				.replace("%server%", PPP.getPlugin().getServername())
				.replace("%world%", player.getWorld().getName())
				.replace("%position%", player.getLocation().getBlockX()+" "+player.getLocation().getBlockY()+" "+player.getLocation().getBlockZ())
				.replace("%madeaction%", EconomyHandler.formatDouble(action))
				.replace("%mademoney%", EconomyHandler.format(money))
				.replace("%madepexp%", EconomyHandler.formatDouble(pexp))
				.replace("%balance%", EconomyHandler.getBalance(player))
				;
		int i = 1;
		for(Iterator<Entry<String, Double[]>> iter = profession.entrySet().iterator(); iter.hasNext();)
		{
			Entry<String, Double[]> e = iter.next();
			r = r.replace("%profession_"+i+"%", e.getKey())
				.replace("%amount_"+i+"%", EconomyHandler.formatDouble(e.getValue()[0]))
				.replace("%money_"+i+"%", EconomyHandler.format(e.getValue()[1]))
				.replace("%pexp_"+i+"%", EconomyHandler.formatDouble(e.getValue()[2]));
			i++;
		}
		while(i < 10)
		{
			r = r.replace("%amount_"+i+"%", "0.0")
					.replace("%money_"+i+"%", EconomyHandler.format(0.0))
					.replace("%pexp_"+i+"%", "0.0");
			i++;
		}
		return r;
	}
	
	public static LinkedHashMap<String, Double[]> sortByFirstValue(LinkedHashMap<String, Double[]> map) 
	{
        List<Map.Entry<String, Double[]>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Comparator.comparingDouble(e -> e.getValue()[0]));
        LinkedHashMap<String, Double[]> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double[]> entry : entries) 
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }
}