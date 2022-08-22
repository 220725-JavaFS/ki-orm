package com.revature.models;

import java.util.Objects;

import com.revature.orm.PrimaryKey;

public class User implements PrimaryKey<Integer> {

	private int user_id;
	private String first_name;
	private String last_name;
	private String user_role;
	private String pword;
	
	public Integer pKey() {
		return this.user_id;
	}
	
	public String pKeyFieldName() {
		return "user_id";
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUser_role() {
		return user_role;
	}

	public void setUser_role(String role) {
		this.user_role = role;
	}

	public String getPword() {
		return pword;
	}

	public void setPword(String pword) {
		this.pword = pword;
	}

	@Override
	public int hashCode() {
		return Objects.hash(first_name, last_name, pword, user_role, user_id);
	}

	@Override
	public String toString() {
		return "User [user_id=" + user_id + ", first_name=" + first_name + ", last_name=" + last_name + ", user_role="
				+ user_role + ", pword=" + pword + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(first_name, other.first_name) && Objects.equals(last_name, other.last_name)
				&& Objects.equals(pword, other.pword) && Objects.equals(user_role, other.user_role) && user_id == other.user_id;
	}

	public User(int user_id, String first_name, String last_name, String user_role, String pword) {
		super();
		this.user_id = user_id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.user_role = user_role;
		this.pword = pword;
	}
	
	public User() {
		super();
	}
}
