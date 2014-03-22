package com.tzc.bean;

import java.util.Date;

public class Contest {
	private int id;
	private String oj;
	private String link;
	private String name;
	private Date start_time;
	private String week;
	private String access;
	public  Contest(){	
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOj() {
		return oj;
	}

	public void setOj(String oj) {
		this.oj = oj;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStart_time() {
		return start_time;
	}

	public void setStart_time(Date startTime) {
		start_time = startTime;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}
}
