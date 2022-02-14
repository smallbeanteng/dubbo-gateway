package com.atommiddleware.cloud.sample.api.user.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank(message = "用户名称不能为空")
	@Length(min = 1, max = 200, message = "用户名称请输入1-200个英文字符或者1-100个汉字")
	private String userName;
	
	private String password;

	private Integer age;

	private Short gender;
	
	private Date dt;

	private WorkHistory workHistory = new WorkHistory();

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

	public Date getDt() {
		return dt;
	}

	public void setDt(Date dt) {
		this.dt = dt;
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

	public class WorkHistory implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String[] workDescriptions;

		private String companyName;

		public String getCompanyName() {
			return companyName;
		}

		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}

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
