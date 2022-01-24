package com.atommiddleware.cloud.sample.api.user.domain;

import java.io.Serializable;
import java.util.Arrays;

public class User implements Serializable{

	private static final long serialVersionUID = 1L;

	private String userName;
	
	private String password;
	
	private Integer age;
	
	private Short gender;
	
	private WorkHistory workHistory;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Short getGender() {
		return gender;
	}

	public void setGender(Short gender) {
		this.gender = gender;
	}

	public WorkHistory getWorkHistory() {
		return workHistory;
	}

	public void setWorkHistory(WorkHistory workHistory) {
		this.workHistory = workHistory;
	}

	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [userName=");
		builder.append(userName);
		builder.append(", password=");
		builder.append(password);
		builder.append(", age=");
		builder.append(age);
		builder.append(", gender=");
		builder.append(gender);
		builder.append(", workHistory=");
		builder.append(workHistory);
		builder.append("]");
		return builder.toString();
	}


	public class WorkHistory implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String[] workDescriptions;

		public String[] getWorkDescriptions() {
			return workDescriptions;
		}

		public void setWorkDescriptions(String[] workDescriptions) {
			this.workDescriptions = workDescriptions;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("WorkHistory [workDescriptions=");
			builder.append(Arrays.toString(workDescriptions));
			builder.append("]");
			return builder.toString();
		}
	}
}
