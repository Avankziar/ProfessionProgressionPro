package me.avankziar.ppp.general.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.Material;

import me.avankziar.ppp.general.database.Language.ISO639_2B;
import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.general.objects.PlayerData.RewardMessageType;
import me.avankziar.ppp.general.objects.ProfessionFile.CompensationType;
import me.avankziar.ppp.general.objects.ProfessionFile.WorldStatus;
import me.avankziar.ppp.spigot.ModifierValueEntry.Bypass;

public class YamlManager
{	
	public enum Type
	{
		BUNGEE, SPIGOT, VELO;
	}
	
	private ISO639_2B languageType = ISO639_2B.GER;
	//The default language of your plugin. Mine is german.
	private ISO639_2B defaultLanguageType = ISO639_2B.GER;
	private Type type;
	
	//Per Flatfile a linkedhashmap.
	private static LinkedHashMap<String, Language> configKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> commandsKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> languageKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> mvelanguageKeys = new LinkedHashMap<>();
	/*
	 * Here are mutiplefiles in one "double" map. The first String key is the filename
	 * So all filename muss be predefine. For example in the config.
	 */
	private static LinkedHashMap<String, LinkedHashMap<String, Language>> guisKeys = new LinkedHashMap<>();
	
	private static LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Language>>>
					professionKeys = new LinkedHashMap<>(); //folder, file, path, sectioncontent
	
	public YamlManager(Type type)
	{
		this.type = type;
		initConfig();
		initCommands();
		initLanguage();
		initModifierValueEntryLanguage();
		initProfession();
	}
	
	public ISO639_2B getLanguageType()
	{
		return languageType;
	}

	public void setLanguageType(ISO639_2B languageType)
	{
		this.languageType = languageType;
	}
	
	public ISO639_2B getDefaultLanguageType()
	{
		return defaultLanguageType;
	}
	
	public LinkedHashMap<String, Language> getConfigKey()
	{
		return configKeys;
	}
	
	public LinkedHashMap<String, Language> getCommandsKey()
	{
		return commandsKeys;
	}
	
	public LinkedHashMap<String, Language> getLanguageKey()
	{
		return languageKeys;
	}
	
	public LinkedHashMap<String, Language> getModifierValueEntryLanguageKey()
	{
		return mvelanguageKeys;
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, Language>> getGUIKey()
	{
		return guisKeys;
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Language>>> getProfessionKey()
	{
		return professionKeys;
	}
	
	public void setFileInput(dev.dejvokep.boostedyaml.YamlDocument yml,
			LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType) throws org.spongepowered.configurate.serialize.SerializationException
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(key.startsWith("#"))
		{
			//Comments
			String k = key.replace("#", "");
			if(yml.get(k) == null)
			{
				//return because no actual key are present
				return;
			}
			if(yml.getBlock(k) == null)
			{
				return;
			}
			if(yml.getBlock(k).getComments() != null && !yml.getBlock(k).getComments().isEmpty())
			{
				//Return, because the comments are already present, and there could be modified. F.e. could be comments from a admin.
				return;
			}
			if(keyMap.get(key).languageValues.get(languageType).length == 1)
			{
				if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
				{
					String s = ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", "");
					yml.getBlock(k).setComments(Arrays.asList(s));
				}
			} else
			{
				List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
				ArrayList<String> stringList = new ArrayList<>();
				if(list instanceof List<?>)
				{
					for(Object o : list)
					{
						if(o instanceof String)
						{
							stringList.add(((String) o).replace("\r\n", ""));
						}
					}
				}
				yml.getBlock(k).setComments((List<String>) stringList);
			}
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", ""));
			} else
			{
				yml.set(key, keyMap.get(key).languageValues.get(languageType)[0]);
			}
		} else
		{
			List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
			ArrayList<String> stringList = new ArrayList<>();
			if(list instanceof List<?>)
			{
				for(Object o : list)
				{
					if(o instanceof String)
					{
						stringList.add(((String) o).replace("\r\n", ""));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	
	
	private void addComments(LinkedHashMap<String, Language> mapKeys, String path, Object[] o)
	{
		mapKeys.put(path, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, o));
	}
	
	private void addConfig(String path, Object[] c, Object[] o)
	{
		configKeys.put(path, new Language(new ISO639_2B[] {ISO639_2B.GER}, c));
		addComments(configKeys, "#"+path, o);
	}
	
	public void initConfig() //INFO:Config
	{
		addConfig("useIFHAdministration",
				new Object[] {
				true},
				new Object[] {
				"Boolean um auf das IFH Interface Administration zugreifen soll.",
				"Wenn 'true' eingegeben ist, aber IFH Administration ist nicht vorhanden, so werden automatisch die eigenen Configwerte genommen.",
				"Boolean to access the IFH Interface Administration.",
				"If 'true' is entered, but IFH Administration is not available, the own config values are automatically used."});
		addConfig("IFHAdministrationPath", 
				new Object[] {
				"bm"},
				new Object[] {
				"",
				"Diese Funktion sorgt dafür, dass das Plugin auf das IFH Interface Administration zugreifen kann.",
				"Das IFH Interface Administration ist eine Zentrale für die Daten von Sprache, Servername und Mysqldaten.",
				"Diese Zentralisierung erlaubt für einfache Änderung/Anpassungen genau dieser Daten.",
				"Sollte das Plugin darauf zugreifen, werden die Werte in der eigenen Config dafür ignoriert.",
				"",
				"This function ensures that the plugin can access the IFH Interface Administration.",
				"The IFH Interface Administration is a central point for the language, server name and mysql data.",
				"This centralization allows for simple changes/adjustments to precisely this data.",
				"If the plugin accesses it, the values in its own config are ignored."});
		addConfig("ServerName",
				new Object[] {
				"hub"},
				new Object[] {
				"",
				"Der Server steht für den Namen des Spigotservers, wie er in BungeeCord/Waterfall/Velocity config.yml unter dem Pfad 'servers' angegeben ist.",
				"Sollte kein BungeeCord/Waterfall oder andere Proxys vorhanden sein oder du nutzt IFH Administration, so kannst du diesen Bereich ignorieren.",
				"",
				"The server stands for the name of the spigot server as specified in BungeeCord/Waterfall/Velocity config.yml under the path 'servers'.",
				"If no BungeeCord/Waterfall or other proxies are available or you are using IFH Administration, you can ignore this area."});
		addConfig("Language",
				new Object[] {
				"ENG"},
				new Object[] {
				"",
				"Die eingestellte Sprache. Von Haus aus sind 'ENG=Englisch' und 'GER=Deutsch' mit dabei.",
				"Falls andere Sprachen gewünsch sind, kann man unter den folgenden Links nachschauen, welchs Kürzel für welche Sprache gedacht ist.",
				"Siehe hier nach, sowie den Link, welche dort auch für Wikipedia steht.",
				"https://github.com/Avankziar/RootAdministration/blob/main/src/main/java/me/avankziar/roota/general/Language.java",
				"",
				"The set language. By default, ENG=English and GER=German are included.",
				"If other languages are required, you can check the following links to see which abbreviation is intended for which language.",
				"See here, as well as the link, which is also there for Wikipedia.",
				"https://github.com/Avankziar/RootAdministration/blob/main/src/main/java/me/avankziar/roota/general/Language.java"});
		addConfig("Mysql.Status",
				new Object[] {
				false},
				new Object[] {
				"",
				"'Status' ist ein simple Sicherheitsfunktion, damit nicht unnötige Fehler in der Konsole geworfen werden.",
				"Stelle diesen Wert auf 'true', wenn alle Daten korrekt eingetragen wurden.",
				"",
				"'Status' is a simple security function so that unnecessary errors are not thrown in the console.",
				"Set this value to 'true' if all data has been entered correctly."});
		addComments(configKeys, "#Mysql", 
				new Object[] {
				"",
				"Mysql ist ein relationales Open-Source-SQL-Databaseverwaltungssystem, das von Oracle entwickelt und unterstützt wird.",
				"'My' ist ein Namenkürzel und 'SQL' steht für Structured Query Language. Eine Programmsprache mit der man Daten auf einer relationalen Datenbank zugreifen und diese verwalten kann.",
				"Link https://www.mysql.com/de/",
				"Wenn du IFH Administration nutzt, kann du diesen Bereich ignorieren.",
				"",
				"Mysql is an open source relational SQL database management system developed and supported by Oracle.",
				"'My' is a name abbreviation and 'SQL' stands for Structured Query Language. A program language that can be used to access and manage data in a relational database.",
				"Link https://www.mysql.com",
				"If you use IFH Administration, you can ignore this section."});
		addConfig("Mysql.Host",
				new Object[] {
				"127.0.0.1"},
				new Object[] {
				"",
				"Der Host, oder auch die IP. Sie kann aus einer Zahlenkombination oder aus einer Adresse bestehen.",
				"Für den Lokalhost, ist es möglich entweder 127.0.0.1 oder 'localhost' einzugeben. Bedenke, manchmal kann es vorkommen,",
				"das bei gehosteten Server die ServerIp oder Lokalhost möglich ist.",
				"",
				"The host, or IP. It can consist of a number combination or an address.",
				"For the local host, it is possible to enter either 127.0.0.1 or >localhost<.",
				"Please note that sometimes the serverIp or localhost is possible for hosted servers."});
		addConfig("Mysql.Port",
				new Object[] {
				3306},
				new Object[] {
				"",
				"Ein Port oder eine Portnummer ist in Rechnernetzen eine Netzwerkadresse,",
				"mit der das Betriebssystem die Datenpakete eines Transportprotokolls zu einem Prozess zuordnet.",
				"Ein Port für Mysql ist standart gemäß 3306.",
				"",
				"In computer networks, a port or port number ",
				"is a network address with which the operating system assigns the data packets of a transport protocol to a process.",
				"A port for Mysql is standard according to 3306."});
		addConfig("Mysql.DatabaseName",
				new Object[] {
				"mydatabase"},
				new Object[] {
				"",
				"Name der Datenbank in Mysql.",
				"",
				"Name of the database in Mysql."});
		addConfig("Mysql.SSLEnabled",
				new Object[] {
				false},
				new Object[] {
				"",
				"SSL ist einer der drei Möglichkeiten, welcher, solang man nicht weiß, was es ist, es so lassen sollte wie es ist.",
				"",
				"SSL is one of the three options which, as long as you don't know what it is, you should leave it as it is."});
		addConfig("Mysql.AutoReconnect",
				new Object[] {
				true},
				new Object[] {
				"",
				"AutoReconnect ist einer der drei Möglichkeiten, welcher, solang man nicht weiß, was es ist, es so lassen sollte wie es ist.",
				"",
				"AutoReconnect is one of the three options which, as long as you don't know what it is, you should leave it as it is."});
		addConfig("Mysql.VerifyServerCertificate",
				new Object[] {
				false},
				new Object[] {
				"",
				"VerifyServerCertificate ist einer der drei Möglichkeiten, welcher, solang man nicht weiß, was es ist, es so lassen sollte wie es ist.",
				"",
				"VerifyServerCertificate is one of the three options which, as long as you don't know what it is, you should leave it as it is."});
		addConfig("Mysql.User",
				new Object[] {
				"admin"},
				new Object[] {
				"",
				"Der User, welcher auf die Mysql zugreifen soll.",
				"",
				"The user who should access the Mysql."});
		addConfig("Mysql.Password",
				new Object[] {
				"not_0123456789"},
				new Object[] {
				"",
				"Das Passwort des Users, womit er Zugang zu Mysql bekommt.",
				"",
				"The user's password, with which he gets access to Mysql."});
		
		addConfig("EnableMechanic.Modifier",
				new Object[] {
				true},
				new Object[] {
				"",
				"Ermöglicht TT die Benutzung von IFH Interface Modifier.",
				"Es erlaubt, dass externe Plugins oder per Befehl Zahlenmodifikatoren in bestimmte Werten einfließen.",
				"Bspw. könnte es dazu führen, dass die Spieler mehr regestrierte Öfen besitzen dürfen.",
				"",
				"Enables TT to use IFH interface modifiers.",
				"It allows external plugins or by command to include number modifiers in certain values.",
				"For example, it could lead to players being allowed to own more registered furnace."});
		addConfig("EnableMechanic.ValueEntry",
				new Object[] {
				true},
				new Object[] {
				"",
				"Ermöglicht TT die Benutzung von IFH Interface ValueEntry.",
				"Es erlaubt, dass externe Plugins oder per Befehl Werteeinträge vornehmen.",
				"Bspw. könnte man dadurch bestimmte Befehle oder Technologien für Spieler freischalten.",
				"",
				"Enables TT to use the IFH interface ValueEntry.",
				"It allows external plugins or commands to make value entries.",
				"For example, it could be used to unlock certain commands or technologies for players."});		
		addConfig("ValueEntry.OverrulePermission",
				new Object[] {
				false},
				new Object[] {
				"",
				"Sollte ValueEntry eingeschalten und installiert sein, so wird bei fast allen Permissionabfragen ValueEntry mit abgefragt.",
				"Fall 1: ValueEntry ist nicht vorhanden oder nicht eingschaltet. So wird die Permission normal abgefragt.",
				"Für alle weitern Fälle ist ValueEntry vorhanden und eingeschaltet.",
				"Fall 2: Der Werteeintrag für den Spieler für diesen abgefragten Wert ist nicht vorhanden,",
				"so wird wenn 'OverrulePermission'=true immer 'false' zurückgegeben.",
				"Ist 'OverrulePermission'=false wird eine normale Permissionabfrage gemacht.",
				"Fall 3: Der Werteeintrag für den Spieler für diesen abgefragten Wert ist vorhanden,",
				"so wird wenn 'OverrulePermission'=true der hinterlegte Werteeintrag zurückgegebn.",
				"Wenn 'OverrulePermission'=false ist, wird 'true' zurückgegeben wenn der hinterlegte Werteeintrag ODER die Permissionabfrage 'true' ist.",
				"Sollten beide 'false' sein, wird 'false' zurückgegeben.",
				"",
				"If ValueEntry is switched on and installed, ValueEntry is also queried for almost all permission queries.",
				"Case 1: ValueEntry is not present or not switched on. The permission is queried normally.",
				"For all other cases, ValueEntry is present and switched on.",
				"Case 2: The value entry for the player for this queried value is not available,",
				"so if 'OverrulePermission'=true, 'false' is always returned.",
				"If 'OverrulePermission'=false, a normal permission query is made.",
				"Case 3: The value entry for the player for this queried value exists,",
				"so if 'OverrulePermission'=true the stored value entry is returned.",
				"If 'OverrulePermission'=false, 'true' is returned if the stored value entry OR the permission query is 'true'.",
				"If both are 'false', 'false' is returned."});
		addConfig("Import.JobsReborn.Active",
				new Object[] {
				false},
				new Object[] {
				"",
				"Wenn 'true', dann wird das Plugin nach Daten vom Plugin JobsReborn in der Mysql suchen.",
				"Sollten Spieler dann joinen wird versucht, diesee JobExp vom Plugin JobsReborn als freie Berufserfahrung gutzuschreiben.",
				"Danach wird der Spieler aus den Daten von JobsReborn gelöscht.",
				"",
				"If 'true', then the plugin will search for data from the JobsReborn plugin in the mysql.",
				"If players then join, an attempt is made to credit this JobExp from the JobsReborn plugin as professionexperience.",
				"The player is then deleted from the JobsReborn data."});
		addConfig("Import.JobsReborn.MaxJobsPerPlayer",
				new Object[] {
				3},
				new Object[] {
				"",
				"Um die Mathematischen Formel von JobsReborn korrekt zu berechnen ist hier die maximale Anzahl von aktiven Jobs angegeben,",
				"welche die Spieler früher in JobsReborn zu Verfügung hatten.",
				"",
				"In order to correctly calculate the JobsReborn mathematical formula, the maximum number of active jobs is given here",
				"which the players used to have available in JobsReborn."});
		addConfig("Default.ProcessRewardMessageType",
				new Object[] {
				RewardMessageType.ACTIONBAR.toString()},
				new Object[] {
				"",
				"Standart RewardMessageType für neue Spieler.",
				"Definiert wie sie eine Nachricht bekommen sollen, wenn der Auswertprozess für die Gewinne vollzogen wird.",
				"Möglich sind NONE, ACTIONBAR & CHAT.",
				"",
				"Standard RewardMessageType for new players.",
				"Defines how they should receive a message when the evaluation process for the profits is completed.",
				"NONE, ACTIONBAR & CHAT are possible."});
		addConfig("Default.SendActionBarByAction",
				new Object[] {
				true},
				new Object[] {
				"",
				"Soll dem Spieler, nachdem er etwas getan hat um Geld und oder Berufserfahrung verdient hat, eine Nachricht über die ActionBar gesendet werden.",
				"",
				"Should a message be sent to the player via the ActionBar after he has done something to earn money or professional experience."});
		addConfig("Default.SendActionBarByActionMessage",
				new Object[] {
				"<red>%eventtype% <yellow>%whattype% <yellow>+%money% <gray>+%expPXP"},
				new Object[] {
				"",
				"Die Nachricht die bei einer Aktion gesendet wird.",
				"",
				"The message that is sent during an action."});
		addConfig("Default.Scoreboard.Board",
				new Object[] {
				"",
				""},
				new Object[] {
				"",
				"Das Scoreboard, was Spieler über einen Befehl aktivieren können.",
				"Mögliche Replacer:",
				"%playername% : Spielername",
				"%onlineplayer% : Online Spieler",
				"%maxplayer% : Maximal Spieler, welche dem Spieler gleichzeit joinen können.",
				"%server% : Server wo der Spieler sich befindet.",
				"%world% : Welt wo der Spieler sich befindet.",
				"%position% : X Y Z Blockkoordinaten als Ganzzahl",
				"%madeaction% : Zusammengezählte Aktion von allen Profession.",
				"%mademoney% : Zusammengezähltes Geld, welches in der vergangenen Zeit vom letzten Verrechnungsprozess registriert wurde. Reward.Task.Process",
				"%madepexp% : Gleiches wie %mademoney% nur für Berufserfahrung.",
				"%balance% : Geldstand des Spielers",
				"Folgende Replacer haben <Zahl>. Bedeutet dort wird 1 bis 9 erwartet. Dies steht für die Berufe welche in der geschehenen Zeit aufsteigend am meisten Aktionen durchgeführt haben.",
				"%profession_<Zahl>% : Berufkategorie, worin die Aktionen ausgeführt wurden.",
				"%amount_<Zahl>% : Anzahl an Aktionen die ausgeführt wurden.",
				"%money_<Zahl>% : Geld, welches durch die Aktion eingenommen wurde.",
				"%pexp_<Zahl>% : Berufserfahrung, welches durch die Aktionen eingenommen wurde.",
				"",
				"The scoreboard, which players can activate via a command.",
				"Mögliche Replacer:",
				"%playername% : Player name",
				"%onlineplayer% : Online player",
				"%maxplayer% : Maximum number of players who can join the player at the same time.",
				"%server% : Server where the player is located.",
				"%world% : World where the player is located.",
				"%position% : X Y Z Block coordinates as integer",
				"%madeaction% : Added action of all profession.",
				"%mademoney% : Added up money that was registered in the past from the last clearing process. Reward.Task.Process",
				"%madepexp% : Same as %mademoney% only for work experience.",
				"%balance% : Money balance of the player",
				"The following replacers have <number>. Means 1 to 9 is expected there. This stands for the occupations which have carried out the most actions in ascending order in the time elapsed.",
				"%profession_<number>% : Profession category in which the actions were performed.",
				"%amount_<number>% : Number of actions that have been executed.",
				"%money_<number>% : Money raised by the campaign.",
				"%pexp_<number>% : Professional experience gained through the actions."});
		addConfig("Default.Scoreboard.Task",
				new Object[] {
				30},
				new Object[] {
				"",
				"Zeit in Sekunden, wann das Scoreboard aktualisiert werden soll.",
				"",
				"Time in seconds when the scoreboard should be updated."});
		addConfig("Reward.Task.Process",
				new Object[] {
				600},
				new Object[] {
				"",
				"Anzahl in Sekunden, wann die Belohnung berechnet werden und den Spieler gutgeschrieben werden.",
				"",
				"Number of seconds when the reward is calculated and credited to the player."});
		addConfig("Reward.Task.Log",
				new Object[] {
				"0",
				"30"},
				new Object[] {
				"",
				"Minutenzahl in einer Stunde, wann der Berufslog geschrieben wird.",
				"",
				"Number of minutes in an hour when the job log is written."});
		addConfig("Reward.TrackPlacedBlocks",
				new Object[] {
				true},
				new Object[] {
				"",
				"Wenn 'true', dann werden gesetzte Blöcke per SQLite getrackt.",
				"",
				"If 'true', then set blocks are tracked via SQLite."});
		addConfig("Reward.IfPlacedBlocksManually",
				new Object[] {
				false},
				new Object[] {
				"",
				"Wenn `true`, werden Blöcke die vorher gesetzt und geträckt wurden auch Geld und Berufserfahrung gezahlt.",
				"",
				"If 'true', blocks that were previously set and trimmed are also paid money and work experience."});
		addConfig("Reward.TimeWhenPlacedBlockStatusExpire",
				new Object[] {
				"1y-0d-0H-0m-0s"},
				new Object[] {
				"",
				"Anzahl an Zeit, wann der Status des 'gesetzten' Blocks verschwindet um man wieder Geld fürs Abbauen bekommen kann.",
				"",
				"Number of time when the status of the 'set' block disappears so that you can get money for dismantling it again."});
	}
	
	@SuppressWarnings("unused") //INFO:Commands
	public void initCommands()
	{
		comBypass();
		String path = "";
		commandsInput("ppp", "ppp", "ppp.cmd.ppp", 
				"/ppp [pagenumber]", "/ppp ", false,
				"<red>/ppp <white>| Infoseite für alle Befehle.",
				"<red>/ppp <white>| Info page for all commands.",
				"<aqua>Befehlsrecht für <white>/ppp",
				"<aqua>Commandright for <white>/ppp",
				"<yellow>Basisbefehl für das ProfessionProgressionPro Plugin.",
				"<yellow>Groundcommand for the ProfessionProgressionPro Plugin.");
		commandsInput("profession", "profession", "profession.cmd.profession", 
				"/profession [pagenumber]", "/profession ", false,
				"<red>/profession <white>| Infoseite für aktiv Berufe.",
				"<red>/profession <white>| Info page for active professions.",
				"<aqua>Befehlsrecht für <white>/profession",
				"<aqua>Commandright for <white>/profession",
				"<yellow>Infoseite für aktiv Berufe.",
				"<yellow>Info page for active professions.");
		String basePermission = "profession.cmd";
		argumentInput("profession_info", "info", basePermission,
				"/profession info [professioncategory]", "/profession info ", false,
				"<red>/profession info [professioncategory] <white>| Zeigt alle aktive & deaktive Berufe an. Optional detailierte Einsicht in den jeweiligen Beruf.",
				"<red>/profession info [professioncategory] <white>| Zeigt alle aktive & deaktive Berufe an. Optional detailierte Einsicht in den jeweiligen Beruf.",
				"<aqua>Befehlsrecht für <white>/profession info",
				"<aqua>Commandright for <white>/profession info",
				"<yellow>Zeigt alle aktive & deaktive Berufe an. Optional detailierte Einsicht in den jeweiligen Beruf.",
				"<yellow>Shows all active & active professions. Optional detailed insight into the respective profession.");
	}
	
	private void comBypass() //INFO:ComBypass
	{
		List<Bypass.Permission> list = new ArrayList<Bypass.Permission>(EnumSet.allOf(Bypass.Permission.class));
		for(Bypass.Permission ept : list)
		{
			commandsKeys.put("Bypass."+ept.toString().replace("_", ".")
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"base."+ept.toString().toLowerCase().replace("_", ".")}));
		}
		
		List<Bypass.Counter> list2 = new ArrayList<Bypass.Counter>(EnumSet.allOf(Bypass.Counter.class));
		for(Bypass.Counter ept : list2)
		{
			if(!ept.forPermission())
			{
				continue;
			}
			commandsKeys.put("Count."+ept.toString().replace("_", ".")
					, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
					"base."+ept.toString().toLowerCase().replace("_", ".")}));
		}
	}
	
	private void commandsInput(String path, String name, String basePermission, 
			String suggestion, String commandString, boolean putUpCmdPermToBonusMalusSystem,
			String helpInfoGerman, String helpInfoEnglish,
			String dnGerman, String dnEnglish,
			String exGerman, String exEnglish)
	{
		commandsKeys.put(path+".Name"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				name}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".PutUpCommandPermToBonusMalusSystem"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				putUpCmdPermToBonusMalusSystem}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
		commandsKeys.put(path+".Displayname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				dnGerman,
				dnEnglish}));
		commandsKeys.put(path+".Explanation"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				exGerman,
				exEnglish}));
	}
	
	private void argumentInput(String path, String argument, String basePermission, 
			String suggestion, String commandString, boolean putUpCmdPermToBonusMalusSystem,
			String helpInfoGerman, String helpInfoEnglish,
			String dnGerman, String dnEnglish,
			String exGerman, String exEnglish)
	{
		commandsKeys.put(path+".Argument"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				argument}));
		commandsKeys.put(path+".Permission"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				basePermission+"."+argument}));
		commandsKeys.put(path+".Suggestion"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				suggestion}));
		commandsKeys.put(path+".PutUpCommandPermToBonusMalusSystem"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				putUpCmdPermToBonusMalusSystem}));
		commandsKeys.put(path+".CommandString"
				, new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
				commandString}));
		commandsKeys.put(path+".HelpInfo"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				helpInfoGerman,
				helpInfoEnglish}));
		commandsKeys.put(path+".Displayname"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				dnGerman,
				dnEnglish}));
		commandsKeys.put(path+".Explanation"
				, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
				exGerman,
				exEnglish}));
	}
	
	public void initLanguage() //INFO:Languages
	{
		languageKeys.put("InputIsWrong",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Deine Eingabe ist fehlerhaft! Klicke hier auf den Text, um weitere Infos zu bekommen!",
						"<red>Your input is incorrect! Click here on the text to get more information!"}));
		languageKeys.put("NoPermission",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Du hast dafür keine Rechte!",
						"<red>You dont not have the rights!"}));
		languageKeys.put("NoPlayerExist",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Der Spieler existiert nicht!",
						"<red>The player does not exist!"}));
		languageKeys.put("NoNumber",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Das Argument <white>%value% <red>muss eine ganze Zahl sein.",
						"<red>The argument <white>%value% <red>must be an integer."}));
		languageKeys.put("NoDouble",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Das Argument <white>%value% <red>muss eine Gleitpunktzahl sein!",
						"<red>The argument <white>%value% <red>must be a floating point number!"}));
		languageKeys.put("IsNegativ",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Das Argument <white>%value% <red>muss eine positive Zahl sein!",
						"<red>The argument <white>%value% <red>must be a positive number!"}));
		languageKeys.put("GeneralHover",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<yellow>Klick mich!",
						"<yellow>Click me!"}));
		languageKeys.put("Next", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<yellow><underlined>nächste Seite <yellow>==>",
						"<yellow><underlined>next page <yellow>==>"}));
		languageKeys.put("Past", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<yellow><== <underlined>vorherige Seite",
						"<yellow><== <underlined>previous page"}));
		languageKeys.put("IsTrue", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<green>✔",
						"<green>✔"}));
		languageKeys.put("IsFalse", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>✖",
						"<red>✖"}));
		languageKeys.put("EconomyCategory", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Berufverdienst",
						"Professionreward"}));
		languageKeys.put("EconomyComment", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"%eventtype%/%whattype%: x%amount%",
						"%eventtype%/%whattype%: x%amount%"}));
		languageKeys.put("Log.InfoMessage", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<aqua>Du hast %amount% Aktionen geleistet. Dein gesamter Verdienst: <yellow>+%money% <gray>+%pexp%",
						"<aqua>You have performed %amount% actions. Your total earnings: <yellow>+%money% <gray>+%pexp%"}));
		languageKeys.put("ImportFromJobsReborn", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gray>Durch die Jobs von JobsReborn wurde dir <white>%pexp% <gray>freie Berufserfahrung gutgeschrieben!",
						"<gray>JobsReborn jobs have credited you with <white>%pexp% <gray>free professionexperience!"}));
		initCommandLanguage();
	}
	
	private void initCommandLanguage()
	{
		languageKeys.put("Profession.Base.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gray>=====<white>Aktive Berufe<gray>=====",
						"<gray>=====<white>Active Profession<gray>====="}));
		languageKeys.put("Profession.Base.Bottomline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gray>===================================",
						"<gray>==================================="}));
		languageKeys.put("Profession.Base.Info", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>%titel%<gray>: <white>%actualexp%<gray>/<white>%maxpexp% <gray>PExp",
						"<white>%titel%<gray>: <white>%actualexp%<gray>/<white>%maxpexp% <gray>PExp"}));
		languageKeys.put("Profession.Info.Info", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>%titel%<gray>: <white>%actualexp%<gray>/<white>%maxpexp% <gray>PExp",
						"<red>%titel%<gray>: <white>%actualexp%<gray>/<white>%maxpexp% <gray>PExp"}));
		languageKeys.put("Profession.Info.ProfessionCategoryDontExist", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Die Berufskategorie existiert nicht!",
						""}));
		languageKeys.put("Profession.Info.Headline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gray>=====<white>%profcat%<gray>=====",
						"<gray>=====<white>%profcat%<gray>====="}));
		languageKeys.put("Profession.Info.Bottomline", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gray>===================================",
						"<gray>==================================="}));
		languageKeys.put("Profession.Info.ProfessionTitle", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Berufstitle: <white>%proftitle%",
						""}));
		languageKeys.put("Profession.Info.ProfessionExperience", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<red>Berufserfahrung: <white>%actualexp%<gray>/<white>%maxexp%",
						""}));
		languageKeys.put("Profession.Info.YouCanBePromote", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gold>Du hast mehr als genug Berufserfahrung für eine Beförderung! Klicke <red>{hier}<gold>!",
						""}));
		languageKeys.put("Profession.Info.MoneyPerTitle", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>%money% <red>per aktuellen Berufstitel.",
						""}));
		languageKeys.put("Profession.Info.MoneyTotal", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<white>%money% <red>insgesamt in der Berufskategorie erwirtschaftet.",
						""}));
		languageKeys.put("Profession.Info.Compensation", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gray>Um die Vergütung einzusehen, klicke <red>{hier}<gray>!",
						""}));
	}
	
	private void initModifierValueEntryLanguage() //INFO:BonusMalusLanguages
	{
		mvelanguageKeys.put(Bypass.Permission.BASE.toString()+".Displayname",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<yellow>Byasspermission für",
						"<yellow>Bypasspermission for"}));
		mvelanguageKeys.put(Bypass.Permission.BASE.toString()+".Explanation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<yellow>Byasspermission für",
						"<yellow>das Plugin BaseTemplate",
						"<yellow>Bypasspermission for",
						"<yellow>the plugin BaseTemplate"}));
		mvelanguageKeys.put(Bypass.Counter.REGISTER_BLOCK_.toString()+".Displayname",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<yellow>Zählpermission für Registrierte Blöcke.",
						"<yellow>Countpermission for registered blocks."}));
		mvelanguageKeys.put(Bypass.Counter.REGISTER_BLOCK_.toString()+".Explanation",
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<yellow>Zählpermission für",
						"<yellow>das Plugin ProfessionProgressionPro.",
						"<yellow>Countpermission for",
						"<yellow>the plugin ProfessionProgressionPro."}));
	}
	
	private void initProfession()
	{
		getProfessionKey().put("miner", initMiner());
	}
	
	private LinkedHashMap<String, LinkedHashMap<String, Language>> initMiner()
	{
		Object[] o = new Object[] {"dummy %player%","dummy %random%"};
		Object[] worldList = new Object[] {
				"Worldnamedummy;"+CompensationType.BOTH,
				"Worldnamedummy2;"+CompensationType.NOTHING,
				"Worldnamedummy3;"+CompensationType.ONLY_EXP,
				"Worldnamedummy4;"+CompensationType.ONLY_MONEY};
		LinkedHashMap<String, LinkedHashMap<String, Language>> files = new LinkedHashMap<>();
		files.put("Miner Helpassistant 1. Grade Junior".replace(" ", "_"),
				getProfessionFile("Miner", 
						"Bergmann Hilfsassistant 1. Grad Junior",
						"Miner Helpassistant 1. Grade Junior",
						true,
						"null",
						"null",
						100.0, true,
						"Bergmann Hilfsassistant 1. Grad",
						"Miner Helpassistant 1. Grade",
						"null", 0.0, 0.0, 100.0, o, new Object[] {
						EventType.BREAKING.toString()+";"+Material.STONE.toString()+";"+"null;"+"0.01;0.01;true",
						EventType.BREAKING.toString()+";"+Material.COBBLESTONE.toString()+";"+"null;"+"0.01;0.01;true",
						EventType.BREAKING.toString()+";"+Material.ANDESITE.toString()+";"+"null;"+"0.015;0.02;true",
						EventType.BREAKING.toString()+";"+Material.GRANITE.toString()+";"+"null;"+"0.015;0.02;true",
						EventType.BREAKING.toString()+";"+Material.DIORITE.toString()+";"+"null;"+"0.015;0.02;true"},
						WorldStatus.DONT_CARE, worldList));
		files.put("Miner Helpassistant 1. Grad".replace(" ", "_"),
				getProfessionFile("Miner", 
						"Bergmann Hilfsassistant 1. Grad",
						"Miner Helpassistant 1. Grad",
						false,
						"Bergmann Hilfsassistant 1. Grad Junior",
						"Miner Helpassistant 1. Grad Junior",
						100.0, true,
						"Bergmann Hilfsassistant 1. Grad Senior",
						"Miner Helpassistant 1. Grad Senior",
						"null", 0.0, 100.0, 100.0, o, new Object[] {
						EventType.BREAKING.toString()+";"+Material.STONE.toString()+";"+"null;"+"0.011;0.011;true",
						EventType.BREAKING.toString()+";"+Material.COBBLESTONE.toString()+";"+"null;"+"0.011;0.011;true",
						EventType.BREAKING.toString()+";"+Material.ANDESITE.toString()+";"+"null;"+"0.0151;0.021;true",
						EventType.BREAKING.toString()+";"+Material.GRANITE.toString()+";"+"null;"+"0.0151;0.021;true",
						EventType.BREAKING.toString()+";"+Material.DIORITE.toString()+";"+"null;"+"0.0151;0.021;true"},
						WorldStatus.DONT_CARE, worldList));
		files.put("Miner Helpassistant 1. Grade Senior".replace(" ", "_"),
				getProfessionFile("Miner", 
						"Bergmann Hilfsassistant 1. Grad Senior",
						"Miner Helpassistant 1. Grade Senior",
						false,
						"Bergmann Hilfsassistant 1. Grad Junior",
						"Miner Helpassistant 1. Grade Junior",
						100.0, true,
						"Bergmann Hilfsassistant 2. Grad Junior",
						"Miner Helpassistant 2. Grade Junior",
						"null", 0.0, 150.0, 100.0, o, new Object[] {
						EventType.BREAKING.toString()+";"+Material.STONE.toString()+";"+"null;"+"0.012;0.012;true",
						EventType.BREAKING.toString()+";"+Material.COBBLESTONE.toString()+";"+"null;"+"0.012;0.012;true",
						EventType.BREAKING.toString()+";"+Material.ANDESITE.toString()+";"+"null;"+"0.0152;0.022;true",
						EventType.BREAKING.toString()+";"+Material.GRANITE.toString()+";"+"null;"+"0.0152;0.022;true",
						EventType.BREAKING.toString()+";"+Material.DIORITE.toString()+";"+"null;"+"0.0152;0.022;true"},
						WorldStatus.DONT_CARE, worldList));
		//------------------
		files.put("Miner Helpassistant 2. Grade Junior".replace(" ", "_"),
				getProfessionFile("Miner", 
						"Bergmann Hilfsassistant 2. Grad Junior",
						"Miner Helpassistant 2. Grade Junior",
						true,
						"null",
						"null",
						100.0, true,
						"Bergmann Hilfsassistant 2. Grad",
						"Miner Helpassistant 2. Grade",
						"null", 0.0, 0.0, 100.0, o, new Object[] {
						EventType.BREAKING.toString()+";"+Material.STONE.toString()+";"+"null;"+"0.01;0.01;true",
						EventType.BREAKING.toString()+";"+Material.COBBLESTONE.toString()+";"+"null;"+"0.01;0.01;true",
						EventType.BREAKING.toString()+";"+Material.ANDESITE.toString()+";"+"null;"+"0.015;0.02;true",
						EventType.BREAKING.toString()+";"+Material.GRANITE.toString()+";"+"null;"+"0.015;0.02;true",
						EventType.BREAKING.toString()+";"+Material.DIORITE.toString()+";"+"null;"+"0.015;0.02;true"},
						WorldStatus.DONT_CARE, worldList));
		files.put("Miner Helpassistant 2. Grade".replace(" ", "_"),
				getProfessionFile("Miner", 
						"Bergmann Hilfsassistant 2. Grad",
						"Miner Helpassistant 2. Grade",
						false,
						"Bergmann Hilfsassistant 2. Grad Junior",
						"Miner Helpassistant 2. Grade Junior",
						100.0, true,
						"Bergmann Hilfsassistant 2. Grad Senior",
						"Miner Helpassistant 2. Grade Senior",
						"null", 0.0, 100.0, 100.0, o, new Object[] {
						EventType.BREAKING.toString()+";"+Material.STONE.toString()+";"+"null;"+"0.011;0.011;true",
						EventType.BREAKING.toString()+";"+Material.COBBLESTONE.toString()+";"+"null;"+"0.011;0.011;true",
						EventType.BREAKING.toString()+";"+Material.ANDESITE.toString()+";"+"null;"+"0.0151;0.021;true",
						EventType.BREAKING.toString()+";"+Material.GRANITE.toString()+";"+"null;"+"0.0151;0.021;true",
						EventType.BREAKING.toString()+";"+Material.DIORITE.toString()+";"+"null;"+"0.0151;0.021;true"},
						WorldStatus.DONT_CARE, worldList));
		files.put("Miner Helpassistant 2. Grade Senior".replace(" ", "_"),
				getProfessionFile("Miner", 
						"Bergmann Hilfsassistant 2. Grad Senior",
						"Miner Helpassistant 2. Grade Senior",
						false,
						"Bergmann Hilfsassistant 2. Grad Junior",
						"Miner Helpassistant 2. Grade Junior",
						100.0, true,
						"Bergmann Hilfsassistant 2. Grad Junior",
						"Miner Helpassistant 2. Grade Junior",
						"null", 0.0, 150.0, 100.0, o, new Object[] {
						EventType.BREAKING.toString()+";"+Material.STONE.toString()+";"+"null;"+"0.012;0.012;true",
						EventType.BREAKING.toString()+";"+Material.COBBLESTONE.toString()+";"+"null;"+"0.012;0.012;true",
						EventType.BREAKING.toString()+";"+Material.ANDESITE.toString()+";"+"null;"+"0.0152;0.022;true",
						EventType.BREAKING.toString()+";"+Material.GRANITE.toString()+";"+"null;"+"0.0152;0.022;true",
						EventType.BREAKING.toString()+";"+Material.DIORITE.toString()+";"+"null;"+"0.0152;0.022;true"},
						WorldStatus.DONT_CARE, worldList));
		return files;
	}
	
	private LinkedHashMap<String, Language> getProfessionFile(String pcat, String ptitleGER, String ptitleENG, boolean isStartProfession,
			String degradationProfessionTitleGER,
			String degradationProfessionTitleENG,
			double degrationTransferOfWorkExperienceInPercent, boolean degrationOnProfessionChange,
			String promotionProfessionTitleGER, String promotionProfessionTitleENG, String promotionPermissionToAcquire, double promotionTeachingFee,
			double promotionNeededProfessionExperience, double promotionTransferOfWorkExperienceInPercent,
			Object[] promotionExecuteConsoleCommands,
			Object[] com,
			WorldStatus worldStatus,
			Object[] worldList)
	{
		LinkedHashMap<String, Language> key = new LinkedHashMap<>();
		key.put("ProfessionCategory", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						pcat}));
		key.put("#ProfessionCategory", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"Die ProfessionCategory (Berufskategorie), ist der Oberbegriff für alle Berufstitel. Sie legen eine Zusammengehörigkeit fest.",
						"Bspw. kann das ein Bergmann oder Holzfäller sein etc.",
						"The ProfessionCategory is the umbrella term for all job titles. They define a grouping.",
						"For example, it could be a miner or lumberjack, etc."}));
		key.put("ProfessionTitle", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						ptitleGER,
						ptitleENG}));
		key.put("#ProfessionTitle", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Der ProfessionTitle (Berufstitel) ist der eigentliche Beruf den man dann ausgewählt hat. Dies ist auch der Teil,",
						"den man 'leveln' bzw. befördern kann.",
						"",
						"The ProfessionTitle is the actual profession that you have chosen.",
						"This is also the part that you can 'level up' or promote."}));
		key.put("CompensationStatusForWorlds", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						worldStatus.toString()}));
		key.put("#CompensationStatusForWorlds", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Setzt den globalen Status für die Handhabung der Welten.",
						"Möglich ist:",
						"DONT_CARE, ignoriert die CompensationPerWorld",
						"WHITELIST setzt die CompensationPerWorld auf Whitelist, somit gelten die Berufsvergütung nur für Welten die draufstehen.",
						"  Bedeutet, solange WHITELIST an ist, werden NUR die Welten die draufstehen eine Berufsvergüten erhalten, sofern diese das definieren.",
						"BLACKLIST setzt die CompensationPerWorld auf Blacklist, somit gelten die Berufsvergütung nur für Welten die nicht draufstehen.",
						"  Bedeutet, solange BLACKLIST an ist, werden alle Welten die draufstehen automatische KEINE Vergütung ausgeben.",
						"",
						"Sets the global status for handling worlds.",
						"It is possible:",
						"DONT_CARE, ignores the CompensationPerWorld",
						"WHITELIST puts the CompensationPerWorld on the whitelist, so the professional compensation only applies to worlds that are on it.",
						"  This means that as long as WHITELIST is on, ONLY the worlds listed on it will receive professional compensation, provided they define it.",
						"BLACKLIST puts the CompensationPerWorld on the blacklist, so the professional compensation only applies to worlds that are not on the blacklist.",
						"  This means that as long as BLACKLIST is on, all worlds on it will automatically NOT pay out any compensation."}));
		key.put("CompensationPerWorld", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, 
						worldList));
		key.put("#CompensationPerWorld", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Eine Auflistung der Welten. Dies ist interessant, wenn man Welten ausschließen will von der Berufsvergütung.",
						"Dabei muss unterschieden werden was man erreichen will.",
						"Will man, dass alle AUßER bestimmte Welten eine Vergütung in Geld & Berufserfahrung haben möchte, so muss es auf BLACKLIST stellen.",
						"Will man, dass NUR bestimmte Welten eine Vergütung haben möchten, setzt man es auf WHITELIST UND gibt an welche Vergütung sein soll.",
						"Dabei wird zuerst der Weltennamen genannt und dann der CompensationsType. Es gibt folgende Werte:",
						"ONLY_MONEY gibt nur Geld als Vergütung aus.",
						"ONLY_EXP gibt nur Berufserfahrung aus. (Es ist nicht die VanillaExp gemeint!)",
						"NOTHING, gibt garnichts aus.",
						"BOTH gibt sowohl Geld sowie auch Berufserfahrung als Vergütung aus.",
						"",
						"A list of worlds. This is interesting if you want to exclude worlds from the professional remuneration.",
						"It is important to differentiate what you want to achieve.",
						"If you want everyone EXCEPT certain worlds to be compensated in money & professional experience, you have to put it on the BLACKLIST.",
						"If you want ONLY certain worlds to have a compensation, set it to WHITELIST AND specify what compensation should be.",
						"The world name is mentioned first and then the compensation type. The following values ​​are available:",
						"ONLY_MONEY only gives out money as compensation.",
						"ONLY_EXP only outputs professional experience. (This does not mean VanillaExp!)",
						"NOTHING, outputs nothing.",
						"BOTH offers both money and professional experience as compensation."}));
		key.put("Compensation", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, 
						com));
		key.put("#Compensation", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Compensationen (Vergütung) legt die Belohnung für eine Tätigkeit fest.",
						"Es ist aufgeteilt in:",
						"EventType:",
						"  BREAKING, Blöcke abbauen.",
						"  BREEDING, Tiere paaren.",
						"  BREWING",
						"  BUCKET_EMPTYING",
						"  BUCKET_FILLING",
						"  COLD_FORGING, im Amboss craften.",
						"  COMPOSTING, Komposter nutzen.",
						"  COOKING",
						"  CRAFTING",
						"  CREATE_PATH, mit einer Schaufel Erdpfade erstellen.",
						"  DEBARKING, Holzstämme entrinden.",
						"  DRYING",
						"  DYING",
						"  ENCHANTING",
						"  EXPLODING",
						"  FERTILIZING",
						"  FISHING",
						"  GRINDING",
						"  HARMING",
						"  HARVESTING",
						"  IGNITING",
						"  ITEM_BREAKING",
						"  ITEM_CONSUME",
						"  INTERACT, mit Blöcken interagieren.",
						"  KILLING",
						"  LOOTING, wird genutzt wenn man Kisten öffnet und Loot generiert wird.",
						"  MELTING",
						"  MILKING",
						"  PLACING",
						"  RENAMING",
						"  SHEARING",
						"  SHEEP_DYE",
						"  SMELTING",
						"  SMITHING",
						"  SMOKING",
						"  STONECUTTING",
						"  TAMING",
						"  CLIMBING, für alle Bewegungseventstype nutze Material AIR.",
						"  CROUCHING",
						"  FALLING",
						"  FLYING",
						"  JUMPING",
						"  WALKING_ON_EARTH",
						"  WALKING_ON_WATER",
						"  WALKING_UNDER_WATER",
						"Material, kann 'null' sein, wenn ein Enitity (Mob) gemeint ist.",
						"EntityType, kann 'null' sein, wenn ein Material/Block gemeint ist.",
						"Vergütung des Geld als Gleitkommazahl (bspw. 1.059) [Achtung mit Punkt nicht mit Komma]",
						"Vergütung der Berufserfahrung als Gleitkommazahl",
						"ItemDrops als Boolean 'true/false' um zu definieren ob items droppen sollen.",
						"",
						"Compensation determines the reward for an activity.",
						"It is divided into:",
						"EventType:",
						"  BREAKING, mining blocks.",
						"  BREEDING, animals mate.",
						"  BREWING",
						"  BUCKET_EMPTYING",
						"  BUCKET_FILLING",
						"  COLD_FORGING, crafting in the anvil.",
						"  COMPOSTING, Use composters.",
						"  COOKING",
						"  CRAFTING",
						"  CREATE_PATH, create dirt paths with a shovel.",
						"  DEBARKING, debarking logs.",
						"  DRYING",
						"  DYING",
						"  ENCHANTING",
						"  EXPLODING",
						"  FERTILIZING",
						"  FISHING",
						"  GRINDING",
						"  HARMING",
						"  HARVESTING",
						"  IGNITING",
						"  ITEM_BREAKING",
						"  ITEM_CONSUME",
						"  INTERACT, interact with blocks.",
						"  KILLING",
						"  MELTING",
						"  MILKING",
						"  PLACING",
						"  CLIMBING",
						"  CROUCHING",
						"  FALLING",
						"  FLYING",
						"  JUMPING",
						"  WALKING_ON_EARTH",
						"  WALKING_ON_WATER",
						"  WALKING_UNDER_WATER",
						"Material, can be 'null' if an entity (mob) is meant.",
						"EntityType, can be 'null' if a material/block is meant.",
						"Remuneration of money as a floating point number (e.g. 1.059)",
						"Compensation for professional experience as a floating point number",
						"ItemDrops as Boolean 'true/false' to define whether items should drop."}));
		key.put("IsStartProfession", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						isStartProfession}));
		key.put("#IsStartProfession", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Gibt an als Boolean 'true/false' ob dies ein Startberuf ist und somit ohne andere vorherige Berufstitel angenommen werden kann.",
						"",
						"Specifies as Boolean 'true/false' whether this is a starting profession and can therefore be accepted without any other previous job titles."}));
		key.put("Degradation.OnProfessionChange", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						 degrationOnProfessionChange}));
		key.put("#Degradation.OnProfessionChange", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Gibt an als Boolean 'true/false' ob der Spieler degradiert wird, wenn er seinen aktuellen Beruf wechselt.",
						"",
						"Returns as Boolean 'true/false' whether the player will be demoted when he changes his current profession."}));
		key.put("Degradation.ProfessionTitle", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						degradationProfessionTitleGER,
						degradationProfessionTitleENG}));
		key.put("#Degradation.ProfessionTitle", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Der eigentliche Berufstitel zu dem man degradiert wird. Man sollte sicherstellen, dass beide Berufe in der gleichen Berufskategorie drin sind.",
						"",
						"The actual job title you are being demoted to. You should make sure that both jobs are in the same job category."}));
		key.put("Degration.TransferOfWorkExperienceInPercent", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						degrationTransferOfWorkExperienceInPercent}));
		key.put("#Degration.TransferOfWorkExperienceInPercent", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Gibt an als Gleitkommazahl, wieviel Erfahrung man noch vom Beruf, auf den man degradiert wird hat.",
						"",
						"Indicates, as a floating point number, how much experience you still have in the job to which you are being demoted."}));
		key.put("Promotion.ProfessionTitle", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						promotionProfessionTitleGER,
						promotionProfessionTitleENG}));
		key.put("#Promotion.ProfessionTitle", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Der Berufstitel zu welchem man befördert werden kann.",
						"",
						"The job title to which one can be promoted."}));
		key.put("Promotion.PermissionToAcquire", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						promotionPermissionToAcquire}));
		key.put("#Promotion.PermissionToAcquire", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Wenn von Beförderung spricht, gelten ALLE Werte für die Situation, wenn der Spieler von diesem Beruf zu dem nächsten möchten.",
						"Aka wenn er zu dem anderen ProfessionTitle sich beförden möchte.",
						"Eine Permission die man einbringen kann, damit der Spieler die Beförderung überhaupt annehmen kann. 'null' zum deaktivieren.",
						"",
						"When talking about promotion, ALL values ​​apply to the situation when the player wants to move from this job to the next one.",
						"Aka if he wants to promote himself to the other ProfessionTitle.",
						"A permission that can be entered so that the player can accept the promotion. 'null' to deactivate."}));
		key.put("Promotion.TeachingFee", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						promotionTeachingFee}));
		key.put("#Promotion.TeachingFee", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Ein Lehrgeld was gezahlt werden muss, wenn man die Beförderung annehmen will.",
						"",
						"A lesson that must be paid if one wants to accept the promotion."}));
		key.put("Promotion.NeededProfessionExperience", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						promotionNeededProfessionExperience}));
		key.put("#Promotion.NeededProfessionExperience", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Die Berufserfahrung die man für die Beförderung benötigt.",
						"",
						"The professional experience required for the promotion."}));
		key.put("Promotion.ExecuteConsoleCommands", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, 
						promotionExecuteConsoleCommands));
		key.put("#Promotion.ExecuteConsoleCommands", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Die Befehle, die per Console ausgeführt werden, wenn man von diesem Beruftitel befördert wird. %player% wird replaced.",
						"",
						"The commands executed via console when promoted from this profession title. %player% is replaced."}));
		key.put("Promotion.TransferOfWorkExperienceInPercent", 
				new Language(new ISO639_2B[] {ISO639_2B.GER}, new Object[] {
						promotionTransferOfWorkExperienceInPercent}));
		key.put("#Promotion.TransferOfWorkExperienceInPercent", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"",
						"Wieviel man an den Berufserfahrung mitnehmen darf in Prozent, wenn man mehr Erfahrung erwirtschaftet hat, als man zu beförderung braucht.",
						"",
						"How much of your professional experience you can take with you in percent if you have gained more experience than you need to get promoted."}));
		return key;
	}
}