package me.avankziar.ppp.spigot.listener.Reward;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import me.avankziar.ppp.general.objects.EventType;
import me.avankziar.ppp.spigot.PPP;
import me.avankziar.ppp.spigot.handler.BoosterHandler;
import me.avankziar.ppp.spigot.handler.RewardHandler;
import me.avankziar.ppp.spigot.hook.WorldGuardHook;

public class DyingHarmingKillingListener implements Listener
{
	private static LinkedHashMap<UUID, LinkedHashMap<UUID, Double>> damageMap = new LinkedHashMap<>();
	final private static EventType HA = EventType.HARMING;
	final private static EventType KI = EventType.KILLING;
	final private static EventType DY = EventType.DYING;
	final private static String SPAWNER = "IS_FROM_SPAWNER";
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) 
	{
	    if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER 
	    		|| event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)
	    {
	    	event.getEntity().setMetadata(SPAWNER, (MetadataValue) new FixedMetadataValue(PPP.getPlugin(), Boolean.valueOf(true))); 
	    }
	 }
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if(!(event.getDamager() instanceof Player)
				|| event.getEntity().hasMetadata(SPAWNER)
				|| ((Player) event.getDamager()).getGameMode() == GameMode.CREATIVE
				|| ((Player) event.getDamager()).getGameMode() == GameMode.SPECTATOR)
		{
			return;
		}
		onHarm(event.getEntity(), (Player) event.getDamager(), event.getDamage());
	}
	
	@EventHandler
	public void onEntityDamageByProjectile(EntityDamageByEntityEvent event)
	{
		if(event.getEntity().hasMetadata(SPAWNER))
		{
			return;
		}
		if(event.getDamager() instanceof Projectile)
		{
			if(((Projectile)event.getDamager()).getShooter() != null
					&& ((Projectile)event.getDamager()).getShooter() instanceof Player)
			{
				final Player damager = (Player) ((Projectile)event.getDamager()).getShooter();
				if(damager.getGameMode() == GameMode.CREATIVE
						|| damager.getGameMode() == GameMode.SPECTATOR)
				{
					return;
				}
				onHarm(event.getEntity(), damager, event.getDamage());
			}
		}		
	}
	
	private static void onHarm(final Entity entity, final Player player, double damage)
	{
		if(returnIfEntityFromSpawner(entity))
		{
			return;
		}
		LinkedHashMap<UUID, Double> mapI = new LinkedHashMap<>();
		if(damageMap.containsKey(entity.getUniqueId()))
		{
			mapI = damageMap.get(entity.getUniqueId());
		}
		double dam = 0;
		if(mapI.containsKey(player.getUniqueId()))
		{
			dam = mapI.get(player.getUniqueId());
		}
		dam = dam + damage;
		mapI.put(player.getUniqueId(), dam);
		damageMap.put(entity.getUniqueId(), mapI);
		final double d = dam;
		final Location loc = entity.getLocation();
		final UUID uuid = player.getUniqueId();
		final EntityType ent = entity.getType();
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, HA, ent);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, HA, ent);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), HA, ent, d, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if(event.getEntity().getKiller() != null)
		{
			if(event.getEntity().getKiller().getGameMode() != GameMode.CREATIVE
					|| event.getEntity().getKiller().getGameMode() != GameMode.SPECTATOR)
			{
				if(damageMap.containsKey(event.getEntity().getUniqueId()))
				{
					double totaldamage = 0;
					for(double d : damageMap.get(event.getEntity().getUniqueId()).values())
					{
						totaldamage = totaldamage + d;
					}
					final double td = totaldamage;
					final UUID uuid = event.getEntity().getUniqueId();
					final Player player = event.getEntity().getKiller();
					final Location loc = player.getLocation();
					final EntityType ent = EntityType.PLAYER;
					new BukkitRunnable()
					{
						@Override
						public void run()
						{
							for(Entry<UUID, Double> entry : damageMap.get(uuid).entrySet())
							{
								double percent = entry.getValue()/td;
								double expfactor = 1 * 
										(PPP.getWorldGuard() 
												? WorldGuardHook.getMultiplierPEXP(player, loc) 
												: 1) *
										BoosterHandler.getBoosterExperience(player, KI, ent);
								double moneyfactor = 1 * 
										(PPP.getWorldGuard() 
												? WorldGuardHook.getMultiplierMoney(player, loc) 
												: 1) *
										BoosterHandler.getBoosterMoney(player, KI, ent);
								RewardHandler.addReward(uuid, loc.getWorld().getName(), KI, ent, percent, moneyfactor, expfactor);
							}
						}
					}.runTaskAsynchronously(PPP.getPlugin());
				} else
				{
					final Player player = event.getEntity().getKiller();
					final UUID uuid = player.getUniqueId();
					final Location loc = event.getEntity().getLastDeathLocation();
					final EntityType ent = EntityType.PLAYER;
					new BukkitRunnable()
					{
						@Override
						public void run()
						{
							double expfactor = 1 * 
									(PPP.getWorldGuard() 
											? WorldGuardHook.getMultiplierPEXP(player, loc) 
											: 1) *
									BoosterHandler.getBoosterExperience(player, KI, ent);
							double moneyfactor = 1 * 
									(PPP.getWorldGuard() 
											? WorldGuardHook.getMultiplierMoney(player, loc) 
											: 1) *
									BoosterHandler.getBoosterMoney(player, KI, ent);
							RewardHandler.addReward(uuid, loc.getWorld().getName(), KI, ent, 1, moneyfactor, expfactor);
						}
					}.runTaskAsynchronously(PPP.getPlugin());
				}			
			}
		}
		if(event.getEntity().getGameMode() == GameMode.CREATIVE
				|| event.getEntity().getGameMode() == GameMode.SPECTATOR)
		{
			return;
		}
		final Player player = event.getEntity();
		final Location loc = player.getLocation();
		final UUID uuid = player.getUniqueId();
		final EntityType ent = EntityType.PLAYER;
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				double expfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierPEXP(player, loc) 
								: 1) *
						BoosterHandler.getBoosterExperience(player, DY, ent);
				double moneyfactor = 1 * 
						(PPP.getWorldGuard() 
								? WorldGuardHook.getMultiplierMoney(player, loc) 
								: 1) *
						BoosterHandler.getBoosterMoney(player, DY, ent);
				RewardHandler.addReward(uuid, loc.getWorld().getName(), DY, ent, 1, moneyfactor, expfactor);
			}
		}.runTaskAsynchronously(PPP.getPlugin());
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(event.getEntity().getKiller() == null
				|| event.getEntity().getKiller().getGameMode() == GameMode.CREATIVE
				|| event.getEntity().getKiller().getGameMode() == GameMode.SPECTATOR)
		{
			return;
		}
		if(returnIfEntityFromSpawner(event.getEntity()))
		{
			return;
		}
		if(damageMap.containsKey(event.getEntity().getUniqueId()))
		{
			double totaldamage = 0;
			for(double d : damageMap.get(event.getEntity().getUniqueId()).values())
			{
				totaldamage = totaldamage + d;
			}
			final double td = totaldamage;
			final UUID uuid = event.getEntity().getUniqueId();
			final Player player = event.getEntity().getKiller();
			final EntityType ent = event.getEntityType();
			final Location loc = event.getEntity().getLocation();
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					for(Entry<UUID, Double> entry : damageMap.get(uuid).entrySet())
					{
						double percent = entry.getValue()/td;
						double expfactor = 1 * 
								(PPP.getWorldGuard() 
										? WorldGuardHook.getMultiplierPEXP(player, loc) 
										: 1) *
								BoosterHandler.getBoosterExperience(player, DY, ent);
						double moneyfactor = 1 * 
								(PPP.getWorldGuard() 
										? WorldGuardHook.getMultiplierMoney(player, loc) 
										: 1) *
								BoosterHandler.getBoosterMoney(player, DY, ent);
						RewardHandler.addReward(uuid, loc.getWorld().getName(), DY, ent, percent, moneyfactor, expfactor);
					}
				}
			}.runTaskAsynchronously(PPP.getPlugin());
		} else
		{
			final Player player = event.getEntity().getKiller();
			final UUID uuid = player.getUniqueId();
			final Location loc = event.getEntity().getLocation();
			final EntityType ent = event.getEntityType();
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					double expfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierPEXP(player, loc) 
									: 1) *
							BoosterHandler.getBoosterExperience(player, DY, ent);
					double moneyfactor = 1 * 
							(PPP.getWorldGuard() 
									? WorldGuardHook.getMultiplierMoney(player, loc) 
									: 1) *
							BoosterHandler.getBoosterMoney(player, DY, ent);
					RewardHandler.addReward(uuid, loc.getWorld().getName(), DY, ent, 1, moneyfactor, expfactor);
				}
			}.runTaskAsynchronously(PPP.getPlugin());
		}
	}
	
	private static boolean returnIfEntityFromSpawner(Entity entity)
	{
		return entity.hasMetadata(SPAWNER);
	}
}