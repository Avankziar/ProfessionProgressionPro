package me.avankziar.ppp.general.objects;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class Reward
{
	private String professionCategory;
	private EventType eventType;
	private Material material;
	private EntityType entityType;
	private double amount;
	private double totalExp;
	private double totalMoney;
	
	public Reward(String professionCategory, EventType eventType, Material material, 
			double amount, double totalExp, double totalMoney)
	{
		setProfessionCategory(professionCategory);
		setEventType(eventType);
		setMaterial(material);
		setAmount(amount);
		setTotalExp(totalExp);
		setTotalMoney(totalMoney);
	}
	
	public Reward(String professionCategory, EventType eventType, EntityType entityType, double amount, 
			double totalExp, double totalMoney)
	{
		setProfessionCategory(professionCategory);
		setEventType(eventType);
		setEntityType(entityType);
		setAmount(amount);
		setTotalExp(totalExp);
		setTotalMoney(totalMoney);
	}

	public String getProfessionCategory() {
		return professionCategory;
	}

	public void setProfessionCategory(String professionCategory) {
		this.professionCategory = professionCategory;
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

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getTotalExp() {
		return totalExp;
	}

	public void setTotalExp(double totalExp) {
		this.totalExp = totalExp;
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}
	
	public void add(double amount, double exp, double money)
	{
		setAmount(getAmount() + amount);
		setTotalExp(getTotalExp() + exp);
		setTotalMoney(getTotalMoney() + money);
	}
}