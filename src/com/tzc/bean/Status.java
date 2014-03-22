package com.tzc.bean;


public class Status {
	int submitId;
	String ojName;
	String submitTime;
	String judgeStatus;
	int  problemId;
	String  language;
	int runTime;
	int runMemory;
	String userName;
	String errorInfo;
	public Status(){
		judgeStatus = "System Error";
	}
	
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}
	public int getSubmitId() {
		return submitId;
	}
	public void setSubmitId(int submitId) {
		this.submitId = submitId;
	}
	public String getOjName() {
		return ojName;
	}
	public void setOjName(String ojName) {
		this.ojName = ojName;
	}
	public String getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}
	public String getJudgeStatus() {
		return judgeStatus;
	}
	public void setJudgeStatus(String judgeStatus) {
		this.judgeStatus = judgeStatus;
	}
	public int getProblemId() {
		return problemId;
	}
	public void setProblemId(int problemId) {
		this.problemId = problemId;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public int getRunTime() {
		return runTime;
	}
	public void setRunTime(int runTime) {
		this.runTime = runTime;
	}
	public int getRunMemory() {
		return runMemory;
	}
	public void setRunMemory(int runMemory) {
		this.runMemory = runMemory;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public String toString() {
		return "Submit ID : " + submitId +
		  	   "\n<br>Submit Time : " + submitTime + 
		  	   "\n<br>Judge Status : " + judgeStatus + 
		  	   "\n<br>Error Info : " + errorInfo + 
		  	   "\n<br>Problem ID : " + problemId + 
		  	   "\n<br>Language : " + language + 
		  	   "\n<br>Run Time : " + runTime + 
		  	   "\n<br>Run Memory : " + runMemory + 
		  	   "\n<br>OJ : " + ojName + 
		  	   "\n<br>User Name ：" + userName;
	}
}
