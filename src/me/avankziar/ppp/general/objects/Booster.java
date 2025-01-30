package me.avankziar.ppp.general.objects;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class Booster 
{
	private String permission;
	private EventType eventType;
	private Material material;
	private EntityType entityType;
	private double multiplicatorMoney;
	private double multiplicatorExperience;
	
	public Booster(String permission, EventType eventType, Material material, 
			double multiplicatorMoney, double multiplicatorExperience)
	{
		setPermission(permission);
		setEventType(eventType);
		setMaterial(material);
		setMultiplicatorMoney(multiplicatorMoney);
		setMultiplicatorExperience(multiplicatorExperience);
	}
	
	public Booster(String permission, EventType eventType, EntityType entityType, 
			double multiplicatorMoney, double multiplicatorExperience)
	{
		setPermission(permission);
		setEventType(eventType);
		setEntityType(entityType);
		setMultiplicatorMoney(multiplicatorMoney);
		setMultiplicatorExperience(multiplicatorExperience);
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public double getMultiplicatorMoney() {
		return multiplicatorMoney;
	}

	public void setMultiplicatorMoney(double multiplicatorMoney) {
		this.multiplicatorMoney = multiplicatorMoney;
	}

	public double getMultiplicatorExperience() {
		return multiplicatorExperience;
	}

	public void setMultiplicatorExperience(double multiplicatorExperience) {
		this.multiplicatorExperience = multiplicatorExperience;
	}
}