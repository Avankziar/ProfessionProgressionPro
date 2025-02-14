package me.avankziar.ppp.spigot.listener.Reward;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Breedable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class EntityInteractListener implements Listener
{	
	@EventHandler
	public void onInteractWithEntity(PlayerInteractEntityEvent event)
	{
		final EventType et = getEventPerEntity(event.getPlayer().getInventory().getItemInMainHand() != null
				? event.getPlayer().getInventory().getItemInMainHand().getType()
				: null, event.getRightClicked().getType());
		if(et == null 
				|| event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR)
		{
			return;
		}
		if(event.getRightClicked() instanceof Breedable)
		{
			Breedable br = (Breedable) event.getRightClicked();
			if(et == EventType.BREEDING && !br.canBreed())
			{
				event.setCancelled(true);
				return;
			}
		}
		final Player player = event.getPlayer();
		final UUID uuid = player.getUniqueId();
		final Location loc = event.getRightClicked().getLocation();
		final EntityType ent = event.getRightClicked().getType();
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, et, ent);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, et, ent);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), et, ent, 1, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
	
	public EventType getEventPerEntity(Material toolmat, EntityType ent)
	{
		if(toolmat == null)
		{
			return EventType.INTERACT;
		}
		switch(ent)
		{
		default:
			return null;
		case AXOLOTL:
			switch(toolmat)
			{
			default:
				return null;
			case TROPICAL_FISH_BUCKET:
				return EventType.BREEDING;
			}
		case BEE:
			switch(toolmat)
			{
			default:
				return null;
			case DANDELION:
			case POPPY:
			case BLUE_ORCHID:
			case AZURE_BLUET:
			case ORANGE_TULIP:
			case WHITE_TULIP:
			case PINK_TULIP:
			case OXEYE_DAISY:
			case CORNFLOWER:
			case LILY_OF_THE_VALLEY:
			case WITHER_ROSE:
			case TORCHFLOWER:
			case SUNFLOWER:
			case LILAC:
			case ROSE_BUSH:
			case PEONY:
			case PITCHER_PLANT:
				return EventType.BREEDING;
			}
		case CAMEL:
			switch(toolmat)
			{
			default:
				return null;
			case CACTUS:
				return EventType.BREEDING;
			}
		case FROG:
			switch(toolmat)
			{
			default:
				return null;
			case SLIME_BALL:
				return EventType.BREEDING;
			}
		case FOX:
			switch(toolmat)
			{
			default:
				return null;
			case SWEET_BERRIES:
			case GLOW_BERRIES:
				return EventType.BREEDING;
			}
		case HOGLIN:
			switch(toolmat)
			{
			default:
				return null;
			case CRIMSON_FUNGUS:
				return EventType.BREEDING;
			}
		case CHICKEN:
			switch(toolmat)
			{
			default:
				return null;
			case WHEAT_SEEDS:
			case MELON_SEEDS:
			case PUMPKIN_SEEDS:
			case BEETROOT_SEEDS:
				return EventType.BREEDING;
			}
		case WOLF:
			switch(toolmat)
			{
			default:
				return null;
			case PORKCHOP:
			case COOKED_PORKCHOP:
			case CHICKEN:
			case COOKED_CHICKEN:
			case BEEF:
			case COOKED_BEEF:
			case MUTTON:
			case COOKED_MUTTON:
			case RABBIT:
			case COOKED_RABBIT:
			case COD:
			case COOKED_COD:
			case ROTTEN_FLESH:
				return EventType.BREEDING;
			}
		case PANDA:
			switch(toolmat)
			{
			default:
				return null;
			case BAMBOO:
				return EventType.BREEDING;
			}
		case RABBIT:
			switch(toolmat)
			{
			default:
				return null;
			case CARROT:
			case DANDELION:
			case GOLDEN_CARROT:
				return EventType.BREEDING;
			}
		case CAT:
			switch(toolmat)
			{
			default:
				return null;
			case COD:
			case SALMON:
			case TROPICAL_FISH:
			case PUFFERFISH:
				return EventType.BREEDING;
			}
		case COW:
		case MOOSHROOM:
		case GOAT:
			switch(toolmat)
			{
			default:
				return null;
			case BUCKET:
				return EventType.MILKING;
			case WHEAT:
				return EventType.BREEDING;
			}
		case SHEEP:
			switch(toolmat)
			{
			default:
				return null;
			case WHEAT:
				return EventType.BREEDING;
			}
		case LLAMA:
			switch(toolmat)
			{
			default:
				return null;
			case WHEAT:
			case HAY_BLOCK:
				return EventType.BREEDING;
			}
		case HORSE:
		case MULE:
			switch(toolmat)
			{
			default:
				return null;
			case WHEAT:
			case APPLE:
			case SUGAR:
			case HAY_BLOCK:
			case GOLDEN_CARROT:
			case GOLDEN_APPLE:
			case ENCHANTED_GOLDEN_APPLE:
				return EventType.BREEDING;
			}
		case TURTLE:
			switch(toolmat)
			{
			default:
				return null;
			case SEAGRASS:
				return EventType.BREEDING;
			}
		case SNIFFER:
			switch(toolmat)
			{
			default:
				return null;
			case TORCHFLOWER_SEEDS:
				return EventType.BREEDING;
			}
		case STRIDER:
			switch(toolmat)
			{
			default:
				return null;
			case WARPED_FUNGUS:
				return EventType.BREEDING;
			}
		case PIG:
			switch(toolmat)
			{
			default:
				return null;
			case CARROT:
				return EventType.BREEDING;
			}
		}
	}
}
