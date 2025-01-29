package me.avankziar.ppp.general.objects;

public enum EventType 
{
	BREAKING, 
	BREEDING,
	BREWING,
	BUCKET_EMPTYING,
	BUCKET_FILLING,
	COLD_FORGING,
	COMPOSTING, //Use the composter
	COOKING,
	CRAFTING,
	CREATE_PATH, //Dirtpath
	DEBARKING, //Debarking woodlog
	DRYING,
	DYING,
	ENCHANTING,
	EXPLODING,
	FERTILIZING,
	FISHING,
	GRINDING,
	HARMING,
	HARVESTING,
	IGNITING,
	ITEM_BREAKING,
	ITEM_CONSUME,
	INTERACT, //To interact with f.e. CraftingTable, Furnace etc.
	KILLING,
	MELTING,
	MILKING, //Cows
	PLACING,
	//PREPARE_ITEMCRAFT, //Not Needed? //Do not can be used to reward a player, only used in permit the player access to the event
	//PREPARE_SMITHING, //Not Needed? //Do not can be used to reward a player, only used in permit the player access to the event
	RENAMING,
	SHEARING,
	SHEEP_DYE,
	SMELTING,
	SMITHING,
	SMOKING,
	STONECUTTING,
	TAMING;
	
	//ADDME Possible new EventTypes
	/*
	 * HOOKING > PlayerFishEvent > event.getCaught() !instanceof Item, aka Player Hook a seeable Entity
	 * EQUIP_ARMOR > PlayerInteractEvent > Possible New RewardType = Reward.UnlockableEquipment
	 * EQUIP_WEAPON > PlayerItemHeldEvent > Possible New RewardType = Reward.UnlockableEquipment
	 * EQUIP_TOOLS > PlayerItemHeldEvent > Possible New RewardType = Reward.UnlockableEquipment
	 * TRADING > InventoryClickEvent
	 * 		Info: It muss be prevent the event.getAction NOTHING/PLACE_ONE/PLACE_ALL/PLACE_SOME
	 * 		InventoryType must be MERCHANT, clicked SlotType must be RESULT
	 * 		ItemStack resultStack = event.getClickedInventory().getItem(2);
	 */
}