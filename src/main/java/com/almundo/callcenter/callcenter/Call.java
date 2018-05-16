package com.almundo.callcenter.callcenter;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.almundo.callcenter.util.LoggerUtil;

public class Call implements Callable<Integer>{	
	
	private Long callId;

	public Call() {
		
	}	
	
	public Call(Long callId) {
		super();
		this.callId = callId;
	}

	public Long getCallId() {
		return callId;
	}		

	public void setCallId(Long callId) {
		this.callId = callId;
	}

	@Override
	public Integer call() {
		
			try {
				int callDuration = ThreadLocalRandom.current().nextInt(5, 10 + 1);
				TimeUnit.SECONDS.sleep(callDuration);
				return callDuration;
			} catch (InterruptedException e) {
				LoggerUtil.getLogger(this.getClass()).error(e.getMessage());
				Thread.currentThread().interrupt();
				throw new IllegalStateException("call interrupted", e);
			}	        			
	}
	
}
