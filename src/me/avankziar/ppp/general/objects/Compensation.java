package me.avankziar.ppp.general.objects;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class Compensation 
{
	private EventType eventType;
	private Material material;
	private EntityType entityType;
	private double compensationMoney;
	private double compensationExperience;
	private boolean itemDrops;
	
	public Compensation(EventType eventType, Material material, EntityType entityType,
			double compensationMoney, double compensationExperience, boolean itemDrops)
	{
		setEventType(eventType);
		setMaterial(material);
		setEntityType(entityType);
		setCompensationMoney(compensationMoney);
		setCompensationExperience(compensationExperience);
		setItemDrops(itemDrops);
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

	public double getCompensationMoney() {
		return compensationMoney;
	}

	public void setCompensationMoney(double compensationMoney) {
		this.compensationMoney = compensationMoney;
	}

	public double getCompensationExperience() {
		return compensationExperience;
	}

	public void setCompensationExperience(double compensationExperience) {
		this.compensationExperience = compensationExperience;
	}

	public boolean isItemDrops() {
		return itemDrops;
	}

	public void setItemDrops(boolean itemDrops) {
		this.itemDrops = itemDrops;
	}
}