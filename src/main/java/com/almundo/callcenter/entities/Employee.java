package com.almundo.callcenter.entities;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.almundo.callcenter.callcenter.Call;
import com.almundo.callcenter.listener.EmployeeFreeListener;
import com.almundo.callcenter.util.EmployeeStatus;
import com.almundo.callcenter.util.LoggerUtil;

public class Employee {
	
	private EmployeeStatus status;	
	private int employeeId;	
	private EmployeeFreeListener listener;
	
	public Employee( int employeeId) {
		super();
		this.status = EmployeeStatus.FREE;
		this.employeeId = employeeId;
	}
	
	public void registerListener(EmployeeFreeListener listener) {
		if (this.listener == null) {
			this.listener = listener;
		}		
	}

	public EmployeeStatus getStatus() {
		return status;
	}
		
	public void setStatus(EmployeeStatus status) {
		this.status = status;
	}

	public int getEmployeeId() {
		return employeeId;
	}
	
	/**
	 * On this method, the call is attended on the same thread created by the dispatcher.
	 * The method just prints who are attending the call and the call duration.
	 * 
	 * @param inboundCall The inbound call
	 */
	public void answerCallv1(Call inboundCall) {
		status = EmployeeStatus.ON_CALL;
		LoggerUtil.getLogger(this.getClass()).info("The employee {} start to attend the call {}",employeeId, inboundCall.getCallId());		
		int callTime = inboundCall.call();
		LoggerUtil.getLogger(this.getClass()).info("The employee {} ends to attend the call {},"
				+ " the call duartion was:{}",employeeId, inboundCall.getCallId(), callTime);		
		status = EmployeeStatus.FREE;
		notifyListener();
	}
	
	/**
	 * This second version of the method answer call, on this method, the employee has his own thread to attend the call,
	 * this is more near to the simulation case reality, for that it is used on the simulation
	 * 
	 * The method just prints who are attending the call and the call duration.
	 * 
	 * @param inboundCall The inbound call
	 */
	public void answerCall(Call inboundCall) {
		status = EmployeeStatus.ON_CALL;
		LoggerUtil.getLogger(this.getClass()).info("The employee {} start to attend the call {}",employeeId, inboundCall.getCallId());		
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<Integer> future =  executor.submit(inboundCall);		
			try {
				LoggerUtil.getLogger(this.getClass()).info("The employee {} ends to attend the call {},"
							+ " the call duartion was:{}",employeeId, inboundCall.getCallId(), future.get());
				status = EmployeeStatus.FREE;
				
			} catch (InterruptedException | ExecutionException e) {
				LoggerUtil.getLogger(this.getClass()).error(e.getMessage(), e);
			}
		executor.shutdown();
		//The listener notifies the dispatcher to inform that this employee ends to attend a call and is free.
		notifyListener();
	}
	
	public void notifyListener() {
		this.listener.onEmployeeFree(this);
	}
	
}
