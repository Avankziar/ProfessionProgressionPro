package me.avankziar.ppp.spigot.handler;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.avankziar.ifh.general.economy.account.AccountCategory;
import me.avankziar.ifh.general.economy.action.OrdererType;
import me.avankziar.ifh.general.economy.currency.CurrencyType;
import me.avankziar.ifh.spigot.economy.account.Account;
import me.avankziar.ifh.spigot.economy.currency.EconomyCurrency;
import me.avankziar.ppp.general.database.Language.ISO639_2B;
import me.avankziar.ppp.spigot.PPP;

public class EconomyHandler 
{
	public static String format(double d)
	{
		if(PPP.getPlugin().getIFHEco() != null)
		{
			EconomyCurrency ec = PPP.getPlugin().getIFHEco().getDefaultCurrency(CurrencyType.DIGITAL);
			return PPP.getPlugin().getIFHEco().format(d, ec);
		}
		if(PPP.getPlugin().getVaultEco() != null)
		{
			DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
			if(PPP.getPlugin().getYamlManager().getLanguageType() == ISO639_2B.GER)
			{
				formatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMAN);
			}
			formatter.setMaximumFractionDigits(2);
			formatter.setMinimumFractionDigits(0);
			return String.valueOf(d) + " " + PPP.getPlugin().getVaultEco().currencyNamePlural();
		}
		return "MISSING ECONOMY";
	}
	
	public static boolean hasBalance(UUID uuid, double d)
	{
		if(PPP.getPlugin().getIFHEco() != null)
		{
			Account ac = PPP.getPlugin().getIFHEco().getDefaultAccount(uuid, AccountCategory.MAIN,
					PPP.getPlugin().getIFHEco().getDefaultCurrency(CurrencyType.DIGITAL));
			return ac != null ? ac.getBalance() >= d : false;
		}
		if(PPP.getPlugin().getVaultEco() != null)
		{
			return PPP.getPlugin().getVaultEco().has(Bukkit.getOfflinePlayer(uuid), d);
		}
		return false;
	}
	
	public static void withdraw(UUID uuid, double d, String category, String comment)
	{
		if(PPP.getPlugin().getIFHEco() != null)
		{
			Account ac = PPP.getPlugin().getIFHEco().getDefaultAccount(uuid, AccountCategory.MAIN,
					PPP.getPlugin().getIFHEco().getDefaultCurrency(CurrencyType.DIGITAL));
			PPP.getPlugin().getIFHEco().withdraw(ac, d, OrdererType.PLAYER, uuid.toString(),
					category, comment);
		}
		if(PPP.getPlugin().getVaultEco() != null)
		{
			PPP.getPlugin().getVaultEco().withdrawPlayer(Bukkit.getPlayer(uuid), d);
		}
	}
	
	public static void deposit(UUID uuid, double d, String category, String comment)
	{
		if(PPP.getPlugin().getIFHEco() != null)
		{
			Account ac = PPP.getPlugin().getIFHEco().getDefaultAccount(uuid, AccountCategory.MAIN,
					PPP.getPlugin().getIFHEco().getDefaultCurrency(CurrencyType.DIGITAL));
			PPP.getPlugin().getIFHEco().deposit(ac, d, OrdererType.PLAYER, uuid.toString(),
					category, comment);
		}
		if(PPP.getPlugin().getVaultEco() != null)
		{
			PPP.getPlugin().getVaultEco().depositPlayer(Bukkit.getPlayer(uuid), d);
		}
	}
}