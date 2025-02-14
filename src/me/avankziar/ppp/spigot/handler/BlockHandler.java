package me.avankziar.ppp.spigot.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.general.objects.RegisteredBlock;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.ModifierValueEntry.Bypass;
import me.avankziar.ppp.spigot.ModifierValueEntry.ModifierValueEntry;

public class BlockHandler
{
	public enum BlockType
	{
		UNKNOW,
		BLASTFURNACE, CAMPFIRE, FURNACE, SMOKER, BREWING_STAND, //Registerable Blocks
		//CRAFTING_TABLE, ENCHANTING_TABLE, ANVIL, CARTOGRAPHY_TABLE, GRINDSTONE, STONECUTTER, SMITHING_TABLE, COMPOSTER; //not registerable blocks
	}
	
	private static PPP plugin = PPP.getPlugin();
	
	//Blöcke die man gerade angeklickt hat., BlockType, Loc als String
	public static LinkedHashMap<UUID, String> atTheMomentAccessBlockMap = new LinkedHashMap<>();
	
	//Location als string und dann das Key des Recipe
	public static LinkedHashMap<String, String> recipeSmeltAtFurnace = new LinkedHashMap<>();
	//Location als string und dann das Key des Recipe
	public static LinkedHashMap<String, String> recipeBrewAtBrewingStand = new LinkedHashMap<>();
	
	public static LinkedHashMap<UUID, LinkedHashMap<BlockType, ArrayList<String>>> registeredBlocks = new LinkedHashMap<>();//UUID, BlockType, Location as Text
	
	public static BlockType getBlockType(Material mat)
	{
		BlockType bt = null;
		switch(mat)
		{
		default:
			bt = BlockType.UNKNOW;
			break;			
		case BREWING_STAND:
			bt = BlockType.BREWING_STAND;
			break;
		case FURNACE:
		case FURNACE_MINECART:
			bt = BlockType.FURNACE;
			break;
		case BLAST_FURNACE:
			bt = BlockType.BLASTFURNACE;
			break;
		case SMOKER:
			bt = BlockType.SMOKER;
			break;
		case CAMPFIRE:
		case SOUL_CAMPFIRE:
			bt = BlockType.CAMPFIRE;
			break;
		}
		return bt;
	}
	
	public static EventType getEventType(Material mat)
	{
		EventType bt = null;
		switch(mat)
		{
		default:
			break;
		case BREWING_STAND:
			bt = EventType.BREWING;
			break;
		case CRAFTING_TABLE:
			bt = EventType.CRAFTING;
			break;
		case ENCHANTING_TABLE:
			bt = EventType.ENCHANTING;
			break;
		case FURNACE:
		case FURNACE_MINECART:
			bt = EventType.MELTING;
			break;
		case BLAST_FURNACE:
			bt = EventType.SMELTING;
			break;
		case SMOKER:
			bt = EventType.SMOKING;
			break;
		case CAMPFIRE:
		case SOUL_CAMPFIRE:
			bt = EventType.COOKING;
			break;
		}
		return bt;
	}
	
	public static String getLocationText(Location l)
	{
		return l.getWorld().getName()
				+":"+l.getBlockX()
				+":"+l.getBlockY()
				+":"+l.getBlockZ();
	}
	
	public static Location getLocation(String l)
	{
		String[] s = l.split(":");
		return new Location(Bukkit.getWorld(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]));
	}
	
	public static int getMaxRegisteredBlocks(Player player, BlockType bt)
	{
		return ModifierValueEntry.getResult(player, 0.0, Bypass.Counter.REGISTER_BLOCK_, bt.toString()+".");
	}
	
	public static boolean isAlreadyRegisteredBlock(BlockType bt, Location l)
	{
		if(plugin.getMysqlHandler().exist(new RegisteredBlock(),
				"`block_type` = ? AND `server` = ? AND `world` = ? AND `block_x` = ? AND `block_y` = ? AND `block_z` = ?",
				bt.toString(), plugin.getServername(), l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ()))
			
		{
			return true;
		}
		return false;
	}
	
	public static boolean isAlreadyRegisteredBlock(UUID uuid, BlockType bt, Location l)
	{
		String loc = BlockHandler.getLocationText(l);
		if(!registeredBlocks.containsKey(uuid))
		{
			return false;
		}
		LinkedHashMap<BlockHandler.BlockType, ArrayList<String>> mapI = registeredBlocks.get(uuid);
		if(!mapI.containsKey(bt))
		{
			return false;
		}
		ArrayList<String> listI = mapI.get(bt);
		if(!listI.contains(loc))
		{
			return false;
		}
		return true;
	}
	
	public static boolean canRegisterBlock(Player player, BlockType bt)
	{
		int already = plugin.getMysqlHandler().getCount(new RegisteredBlock(),
				"`player_uuid` = ? AND `block_type` = ?", player.getUniqueId().toString(), bt.toString());
		int canPossible = ModifierValueEntry.getResult(player, 0.0, Bypass.Counter.REGISTER_BLOCK_, bt.toString()+".");
		if(already >= canPossible)
		{
			return false;
		}
		return true;
	}
	
	public static UUID getRegisterBlockOwner(BlockType bt, Location l)
	{
		UUID uuid = null;
		for(Entry<UUID, LinkedHashMap<BlockType, ArrayList<String>>> entry : registeredBlocks.entrySet())
		{
			if(!entry.getValue().containsKey(bt))
			{
				continue;
			}
			if(!entry.getValue().get(bt).contains(BlockHandler.getLocationText(l)))
			{
				continue;
			}
			uuid = entry.getKey();
			break;
		}
		if(uuid == null)
		{
			RegisteredBlock rg = (RegisteredBlock) plugin.getMysqlHandler().getData(new RegisteredBlock(),
					"`block_type` = ? AND `server` = ? AND `world` = ? AND `block_x` = ? AND `block_y` = ? AND `block_z` = ?",
					bt.toString(), plugin.getServername(), l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
			if(rg != null)
			{
				uuid = rg.getPlayerUUID();
			}
		}
		return uuid;
	}
	
	public static void registerBlock(Player player, BlockType bt, Location l, boolean mysql)
	{
		UUID uuid = player.getUniqueId();
		LinkedHashMap<BlockType, ArrayList<String>> mapI = new LinkedHashMap<>();
		if(registeredBlocks.containsKey(uuid))
		{
			mapI = registeredBlocks.get(uuid);
		}
		ArrayList<String> list = new ArrayList<>();
		if(mapI.containsKey(bt))
		{
			list = mapI.get(bt);
		}
		if(!list.contains(BlockHandler.getLocationText(l)))
		{
			list.add(BlockHandler.getLocationText(l));
		}
		mapI.put(bt, list);
		registeredBlocks.put(uuid, mapI);
		if(mysql)
		{
			if(plugin.getMysqlHandler().exist(new RegisteredBlock(),
					"`player_uuid` = ? AND `block_type` = ? AND `server` = ? AND `world` = ? AND `block_x` = ? AND `block_y` = ? AND `block_z` = ?",
					uuid.toString(), bt.toString(), plugin.getServername(), l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ()))
				
			{
				return;
			}
			RegisteredBlock rg = new RegisteredBlock(0, uuid, bt,
					plugin.getServername(), l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
			plugin.getMysqlHandler().create(rg);
		}
	}
	
	public static void deRegisterBlock(BlockType bt, Location l, boolean mysql)
	{
		for(Entry<UUID, LinkedHashMap<BlockType, ArrayList<String>>> entry : registeredBlocks.entrySet())
		{
			LinkedHashMap<BlockType, ArrayList<String>> mapI = entry.getValue();
			if(!mapI.containsKey(bt))
			{
				continue;
			}
			ArrayList<String> list = mapI.get(bt);
			if(list.contains(BlockHandler.getLocationText(l)))
			{
				list.remove(BlockHandler.getLocationText(l));
				mapI.put(bt, list);
				registeredBlocks.put(entry.getKey(), mapI);
				break;
			}			
		}
		if(mysql)
		{
			plugin.getMysqlHandler().deleteData(new RegisteredBlock(), 
					"`block_type` = ? AND `server` = ? AND `world` = ? AND `block_x` = ? AND `block_y` = ? AND `block_z` = ?",
					bt.toString(), plugin.getServername(), l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
		}
	}
}