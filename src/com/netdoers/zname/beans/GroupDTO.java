package com.netdoers.zname.beans;

public class GroupDTO {

	private String groupId;
	private String groupName;
	private String groupDp;

	public GroupDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GroupDTO(String groupId, String groupName, String groupDp) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.groupDp = groupDp;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupDp() {
		return groupDp;
	}

	public void setGroupDp(String groupDp) {
		this.groupDp = groupDp;
	}
}
