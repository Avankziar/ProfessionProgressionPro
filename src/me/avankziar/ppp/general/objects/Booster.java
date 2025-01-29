package me.avankziar.ppp.general.objects;

public class Booster 
{
	private String permission;
	private double multiplicatorMoney;
	private double multiplicatorExperience;
	
	public Booster(String permission, double multiplicatorMoney, double multiplicatorExperience)
	{
		setPermission(permission);
		setMultiplicatorMoney(multiplicatorMoney);
		setMultiplicatorExperience(multiplicatorExperience);
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
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