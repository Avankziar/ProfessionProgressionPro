package me.avankziar.ppp.general.objects;

import java.util.ArrayList;

public class ProfessionFile 
{
	private String professionCategory;
	private String professionTitle;
	private ArrayList<Compensation> compensation;
	
	public ProfessionFile(String professionCategory, String professionTitle,
			ArrayList<Compensation> compensation)
	{
		setProfessionCategory(professionCategory);
		setProfessionTitle(professionTitle);
		setCompensation(compensation);
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
}
