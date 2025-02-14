package me.avankziar.ppp.general.objects;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ProfessionFile 
{
	private String professionCategory;
	private String professionTitle;
	private ArrayList<Compensation> compensation;
	private LinkedHashMap<String, CompensationType> worldList;
	private WorldStatus worldStatus;
	
	public enum CompensationType
	{
		ONLY_MONEY, ONLY_EXP, NOTHING, BOTH;
	}
	
	public enum WorldStatus
	{
		DONT_CARE, WHITELIST, BLACKLIST;
	}
	
	private boolean isStartProfession;
	private boolean degrationOnProfessionChange;
	private String degradationProfessionTitle;
	private double degrationTransferOfWorkExperienceInPercent;
	
	private String promotionProfessionTitle;	
	private String promotionPermissionToAcquire; //ThisProfessionTitle
	private double promotionTeachingFee; //Lehrgeld
	private double promotionNeededProfessionExperience;
	private ArrayList<String> promotionExecuteConsoleCommands;
	private double promotionTransferOfWorkExperienceInPercent;
		
	
	public ProfessionFile(String professionCategory, String professionTitle,
			ArrayList<Compensation> compensation, LinkedHashMap<String, CompensationType> worldList, WorldStatus worldStatus,
			boolean isStartProfession, boolean degrationOnProfessionChange,
			String degradationProfessionTitle, double degrationTransferOfWorkExperienceInPercent,
			String promotionProfessionTitle, String promotionPermissionToAcquire, double promotionTeachingFee,
			double promotionNeededProfessionExperience, ArrayList<String> promotionExecuteConsoleCommands, 
			double promotionTransferOfWorkExperienceInPercent)
	{
		setProfessionCategory(professionCategory);
		setProfessionTitle(professionTitle);
		setCompensation(compensation);
		setWorldList(worldList);
		setWorldStatus(worldStatus);
		setStartProfession(isStartProfession);
		
		setDegrationOnProfessionChange(degrationOnProfessionChange);
		setDegradationProfessionTitle(degradationProfessionTitle);
		setDegrationTransferOfWorkExperienceInPercent(degrationTransferOfWorkExperienceInPercent);
		
		setPromotionProfessionTitle(promotionProfessionTitle);
		setPromotionPermissionToAcquire(promotionPermissionToAcquire);
		setPromotionTeachingFee(promotionTeachingFee);
		setPromotionNeededProfessionExperience(promotionNeededProfessionExperience);
		setPromotionExecuteConsoleCommands(promotionExecuteConsoleCommands);
		setPromotionTransferOfWorkExperienceInPercent(promotionTransferOfWorkExperienceInPercent);
	}

	public String getProfessionCategory() {
		return professionCategory;
	}

	public void setProfessionCategory(String professionCategory) {
		this.professionCategory = professionCategory;
	}

	public String getProfessionTitle() {
		return professionTitle;
	}

	public void setProfessionTitle(String professionTitle) {
		this.professionTitle = professionTitle;
	}

	public ArrayList<Compensation> getCompensation() {
		return compensation;
	}

	public void setCompensation(ArrayList<Compensation> compensation) {
		this.compensation = compensation;
	}

	public LinkedHashMap<String, CompensationType> getWorldList()
	{
		return worldList;
	}

	public void setWorldList(LinkedHashMap<String, CompensationType> worldList)
	{
		this.worldList = worldList;
	}

	public WorldStatus getWorldStatus()
	{
		return worldStatus;
	}

	public void setWorldStatus(WorldStatus worldStatus)
	{
		this.worldStatus = worldStatus;
	}

	public boolean isStartProfession()
	{
		return isStartProfession;
	}

	public void setStartProfession(boolean isStartProfession)
	{
		this.isStartProfession = isStartProfession;
	}

	public boolean isDegrationOnProfessionChange()
	{
		return degrationOnProfessionChange;
	}

	public void setDegrationOnProfessionChange(boolean degrationOnProfessionChange)
	{
		this.degrationOnProfessionChange = degrationOnProfessionChange;
	}

	public String getDegradationProfessionTitle()
	{
		return degradationProfessionTitle;
	}

	public void setDegradationProfessionTitle(String degradationProfessionTitle)
	{
		this.degradationProfessionTitle = degradationProfessionTitle;
	}

	public double getDegrationTransferOfWorkExperienceInPercent()
	{
		return degrationTransferOfWorkExperienceInPercent;
	}

	public void setDegrationTransferOfWorkExperienceInPercent(double degrationTransferOfWorkExperienceInPercent)
	{
		this.degrationTransferOfWorkExperienceInPercent = degrationTransferOfWorkExperienceInPercent;
	}

	public String getPromotionProfessionTitle()
	{
		return promotionProfessionTitle;
	}

	public void setPromotionProfessionTitle(String promotionProfessionTitle)
	{
		this.promotionProfessionTitle = promotionProfessionTitle;
	}

	public String getPromotionPermissionToAcquire()
	{
		return promotionPermissionToAcquire;
	}

	public void setPromotionPermissionToAcquire(String promotionPermissionToAcquire)
	{
		this.promotionPermissionToAcquire = promotionPermissionToAcquire;
	}

	public double getPromotionTeachingFee()
	{
		return promotionTeachingFee;
	}

	public void setPromotionTeachingFee(double promotionTeachingFee)
	{
		this.promotionTeachingFee = promotionTeachingFee;
	}

	public double getPromotionNeededProfessionExperience()
	{
		return promotionNeededProfessionExperience;
	}

	public void setPromotionNeededProfessionExperience(double promotionNeededProfessionExperience)
	{
		this.promotionNeededProfessionExperience = promotionNeededProfessionExperience;
	}

	public ArrayList<String> getPromotionExecuteConsoleCommands()
	{
		return promotionExecuteConsoleCommands;
	}

	public void setPromotionExecuteConsoleCommands(ArrayList<String> promotionExecuteConsoleCommands)
	{
		this.promotionExecuteConsoleCommands = promotionExecuteConsoleCommands;
	}

	public double getPromotionTransferOfWorkExperienceInPercent()
	{
		return promotionTransferOfWorkExperienceInPercent;
	}

	public void setPromotionTransferOfWorkExperienceInPercent(double promotionTransferOfWorkExperienceInPercent)
	{
		this.promotionTransferOfWorkExperienceInPercent = promotionTransferOfWorkExperienceInPercent;
	}
}
