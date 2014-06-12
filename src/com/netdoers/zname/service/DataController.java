/* HISTORY
 * CATEGORY 		:- SERVICE
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- 
 * DESCRIPTION 		:- 
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * --------------------------------------------------------------------------------------------------------------------
 */
package com.netdoers.zname.service;

import java.util.ArrayList;

public class DataController implements IServiceListener{

	ArrayList<String> location;
	ArrayList<String> startTime;
	ArrayList<String> level;
	ArrayList<String> procedure;
	ArrayList<String> ward;
	ArrayList<String> teamMember;
	ArrayList<String> type;
	ArrayList<String> referredBy;
	ArrayList<String> paymentMode;
	ArrayList<String> expenseCategory;
	
	public ArrayList<String> getLocation() {
		location = new ArrayList<String>();
		location.add("Location 1");
		location.add("Location 2");
		location.add("Location 3");
		location.add("Location 4");
		location.add("Location 5");
		return location;
	}
	
	public void addToLocation(String location)
	{
		getLocation().add(location);
	}


	public void setLocation(ArrayList<String> location) {
		this.location = location;
	}


	/*public ArrayList<String> getStartTime() {
		
		startTime = new ArrayList<String>();
		startTime.add("StartTime 1");
		startTime.add("StartTime 2");
		startTime.add("StartTime 3");
		startTime.add("StartTime 4");
		startTime.add("StartTime 5");
		return startTime;
	}*/

	/*public void addStartTime(String time)
	{
		getStartTime().add(time);
	}*/

	public void setStartTime(ArrayList<String> startTime) {
		this.startTime = startTime;
	}


	public ArrayList<String> getLevel() {
		level = new ArrayList<String>();
		level.add("Level 1");
		level.add("Level 2");
		level.add("Level 3");
		level.add("Level 4");
		level.add("Level 5");
		return level;
	}
	public void addLevel(String level)
	{
		getLevel().add(level);
	}


	public void setLevel(ArrayList<String> level) {
		this.level = level;
	}


	public ArrayList<String> getProcedure() {
		procedure = new ArrayList<String>();
		procedure.add("Procedure 1");
		procedure.add("Procedure 2");
		procedure.add("Procedure 3");
		procedure.add("Procedure 4");
		procedure.add("Procedure 5");
		return procedure;
	}
	public void addProcedure(String level)
	{
		getProcedure().add(level);
	}


	public void setProcedure(ArrayList<String> procedure) {
		this.procedure = procedure;
	}


	public ArrayList<String> getWard() {
		ward = new ArrayList<String>();
		ward.add("Ward 1");
		ward.add("Ward 2");
		ward.add("Ward 3");
		ward.add("Ward 4");
		ward.add("Ward 5");
		return ward;
	}
	
	public void addWard(String level)
	{
		getWard().add(level);
	}


	public void setWard(ArrayList<String> ward) {
		this.ward = ward;
	}


	public ArrayList<String> getTeamMember() {
		teamMember = new ArrayList<String>();
		teamMember.add("TeamMember 1");
		teamMember.add("TeamMember 2");
		teamMember.add("TeamMember 3");
		teamMember.add("TeamMember 4");
		teamMember.add("TeamMember 5");
		return teamMember;
	}
	
	public void addTeamMember(String level)
	{
		getTeamMember().add(level);
	}


	public void setTeamMember(ArrayList<String> teamMember) {
		this.teamMember = teamMember;
	}


	public ArrayList<String> getType() {
		type = new ArrayList<String>();
		type.add("SX");
		type.add("IPD");
		type.add("OPD");
		return type;
	}
	public void addType(String level)
	{
		getType().add(level);
	}

	public void setType(ArrayList<String> type) {
		this.type = type;
	}
	
	public ArrayList<String> getReferredBy() {
		referredBy = new ArrayList<String>();
		referredBy.add("referredBy 1");
		referredBy.add("referredBy 2");
		referredBy.add("referredBy 3");
		referredBy.add("referredBy 4");
		referredBy.add("referredBy 5");
		return referredBy;
	}
	public void addReferredBy(String level)
	{
		getReferredBy().add(level);
	}

	public void setReferredBy(ArrayList<String> referredBy) {
		this.referredBy = referredBy;
	}
	
	public ArrayList<String> getPaymentMode() {
		paymentMode = new ArrayList<String>();
		paymentMode.add("paymentMode 1");
		paymentMode.add("paymentMode 2");
		paymentMode.add("paymentMode 3");
		paymentMode.add("paymentMode 4");
		paymentMode.add("paymentMode 5");
		return paymentMode;
	}
	public void addPaymentMode(String level)
	{
		getPaymentMode().add(level);
	}

	public void setPaymentMode(ArrayList<String> referredBy) {
		this.paymentMode = referredBy;
	}
	
	public ArrayList<String> getExpenseCategory() {
		expenseCategory = new ArrayList<String>();
		expenseCategory.add("expenseCategory 1");
		expenseCategory.add("expenseCategory 2");
		expenseCategory.add("expenseCategory 3");
		expenseCategory.add("expenseCategory 4");
		expenseCategory.add("expenseCategory 5");
		return expenseCategory;
	}
	public void addExpenseCategory(String level)
	{
		getExpenseCategory().add(level);
	}

	public void setExpenseCategory(ArrayList<String> referredBy) {
		this.expenseCategory = referredBy;
	}


	@Override
	public void onErrorReceived(String statusCode, String errorMessage) {
		// TODO Auto-generated method stub	
	}
}
