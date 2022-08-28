package com.revature.orm.utils;

public class Conf {
	
	private String url;
	private String uname;
	private String pass;
	
	public String getUrl() {
		return url;
	}
	
	public String getUname() {
		return uname;
	}
	
	public String getPass() {
		return pass;
	}
	
	public Conf(String url, String uname, String pass) {
		super();
		this.url = url;
		this.uname = uname;
		this.pass = pass;
	}
	
	public Conf() {
		super();
	}

}
