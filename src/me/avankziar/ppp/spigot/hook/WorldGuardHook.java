package me.avankziar.ppp.spigot.hook;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class WorldGuardHook
{
	public static StateFlag PPP_COMPENSATION_MONEY;
	public static StateFlag PPP_COMPENSATION_PEXP;
	public static StateFlag PPP_BOOST_MONEY;
	public static StateFlag PPP_BOOST_PEXP;
	public static StateFlag PPP_MULTIPLIER_MONEY_X1_5;
	public static StateFlag PPP_MULTIPLIER_PEXP_X1_5;
	public static StateFlag PPP_MULTIPLIER_MONEY_X2;
	public static StateFlag PPP_MULTIPLIER_PEXP_X2;
	public static StateFlag PPP_MULTIPLIER_MONEY_X3;
	public static StateFlag PPP_MULTIPLIER_PEXP_X3;
	public static StateFlag PPP_MULTIPLIER_MONEY_X4;
	public static StateFlag PPP_MULTIPLIER_PEXP_X4;
	
	public static boolean init()
	{
		FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		try 
		{
			StateFlag ppp_c_m = new StateFlag("ppp-compensation-money", true);
	        registry.register(ppp_c_m);
	        PPP_COMPENSATION_MONEY = ppp_c_m;
	        StateFlag ppp_c_pexp = new StateFlag("ppp-compensation-pexp", true);
	        registry.register(ppp_c_pexp);
	        PPP_COMPENSATION_PEXP = ppp_c_pexp;
	        
	        StateFlag ppp_b_m = new StateFlag("ppp-boost-money", true);
	        registry.register(ppp_b_m);
	        PPP_BOOST_MONEY = ppp_b_m;
	        StateFlag ppp_b_pexp = new StateFlag("ppp-boost-pexp", true);
	        registry.register(ppp_b_pexp);
	        PPP_BOOST_PEXP = ppp_b_pexp;
	        
	        StateFlag ppp_m_m_x1_5 = new StateFlag("ppp-multiplier-money-x1-5", true);
	        registry.register(ppp_m_m_x1_5);
	        PPP_MULTIPLIER_MONEY_X1_5 = ppp_m_m_x1_5;
	        StateFlag ppp_m_pexp_x1_5 = new StateFlag("ppp-multiplier-pexp-x1-5", true);
	        registry.register(ppp_m_pexp_x1_5);
	        PPP_MULTIPLIER_PEXP_X1_5 = ppp_m_pexp_x1_5;
	        
	        StateFlag ppp_m_m_x2 = new StateFlag("ppp-multiplier-money-x2", true);
	        registry.register(ppp_m_m_x2);
	        PPP_MULTIPLIER_MONEY_X2 = ppp_m_m_x2;
	        StateFlag ppp_m_pexp_x2 = new StateFlag("ppp-multiplier-pexp-x2", true);
	        registry.register(ppp_m_pexp_x2);
	        PPP_MULTIPLIER_PEXP_X2 = ppp_m_pexp_x2;
	        
	        StateFlag ppp_m_m_x3 = new StateFlag("ppp-multiplier-money-x3", true);
	        registry.register(ppp_m_m_x3);
	        PPP_MULTIPLIER_MONEY_X3 = ppp_m_m_x3;
	        StateFlag ppp_m_pexp_x3 = new StateFlag("ppp-multiplier-pexp-x3", true);
	        registry.register(ppp_m_pexp_x3);
	        PPP_MULTIPLIER_PEXP_X3 = ppp_m_pexp_x3;
	        
	        StateFlag ppp_m_m_x4 = new StateFlag("ppp-multiplier-money-x4", true);
	        registry.register(ppp_m_m_x4);
	        PPP_MULTIPLIER_MONEY_X4 = ppp_m_m_x4;
	        StateFlag ppp_m_pexp_x4 = new StateFlag("ppp-multiplier-pexp-x4", true);
	        registry.register(ppp_m_pexp_x4);
	        PPP_MULTIPLIER_PEXP_X4 = ppp_m_pexp_x4;
	    } catch (FlagConflictException e) 
		{
	        return false;
	    }
		return true;
	}
	
	public static boolean compensationDeactive(Player player, Location pointOne)
	{
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_COMPENSATION_MONEY)
        		&& query.testState(BukkitAdapter.adapt(pointOne), 
                		WorldGuardPlugin.inst().wrapPlayer(player), PPP_COMPENSATION_PEXP);
	}
	
	public static boolean canCompensationMoney(Player player, Location pointOne)
	{
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_COMPENSATION_MONEY);
	}
	
	public static boolean canCompensationPEXP(Player player, Location pointOne)
	{
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_COMPENSATION_PEXP);
	}
	
	public static boolean canBoostMoney(Player player, Location pointOne)
	{
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_BOOST_MONEY);
	}
	
	public static boolean canBoostPEXP(Player player, Location pointOne)
	{
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        return query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_BOOST_PEXP);
	}
	
	public static double getMultiplierMoney(Player player, Location pointOne)
	{
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
		boolean x1_5 = query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_MULTIPLIER_MONEY_X1_5);
		boolean x2 = query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_MULTIPLIER_MONEY_X1_5);
		boolean x3 = query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_MULTIPLIER_MONEY_X1_5);
		boolean x4= query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_MULTIPLIER_MONEY_X1_5);
		return (x1_5 ? 1.5 : 1) *
				(x2 ? 2 : 1) *
				(x3 ? 3 : 1) *
				(x4 ? 4 : 1);
	}
	
	public static double getMultiplierPEXP(Player player, Location pointOne)
	{
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
		boolean x1_5 = query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_MULTIPLIER_PEXP_X1_5);
		boolean x2 = query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_MULTIPLIER_PEXP_X1_5);
		boolean x3 = query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_MULTIPLIER_PEXP_X1_5);
		boolean x4= query.testState(BukkitAdapter.adapt(pointOne), 
        		WorldGuardPlugin.inst().wrapPlayer(player), PPP_MULTIPLIER_PEXP_X1_5);
		return (x1_5 ? 1.5 : 1) *
				(x2 ? 2 : 1) *
				(x3 ? 3 : 1) *
				(x4 ? 4 : 1);
	}
}