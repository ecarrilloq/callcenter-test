package com.almundo.callcenter.util;

public enum EmployeeStatus {
	FREE("Waiting for a call"),
	ON_CALL("Attenng a call");
	
	private String statusMessage;

	private EmployeeStatus(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	public String statusMessage() {return statusMessage;}
	
}
