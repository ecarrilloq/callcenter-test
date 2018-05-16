package com.almundo.callcenter.callcenter;

import java.util.ArrayList;
import java.util.Collections;

import com.almundo.callcenter.entities.Director;
import com.almundo.callcenter.entities.Employee;
import com.almundo.callcenter.entities.Operator;
import com.almundo.callcenter.entities.Supervisor;

import junit.framework.TestCase;

public class DispatcherTest extends TestCase {


	public void testDispatchCall() {
		
		ArrayList<Employee> poolEmployees;
		poolEmployees = new ArrayList();	
		for(int i=1; i<=10; i++) {
			if (i<=5) {
				poolEmployees.add(new Operator(i));
			}else if (i>5 && i<=8) {
				poolEmployees.add(new Supervisor(i));
			}else {
				poolEmployees.add(new Director(i));
			}
		}
		
		Dispatcher dispatchertested = new Dispatcher(poolEmployees);
		//The initial pool must be 10
		assertEquals(10,dispatchertested.getPool().size());
		
		//The initial value of call processed must be 0
		assertEquals(0,dispatchertested.getCallsCounter());
		
    	for(int i=0;i<10;i++) {
    		dispatchertested.dispatchCall(new Call());    		
    		//while the dispatcher process a call, the queue could not be empty
    		assertTrue(!dispatchertested.getCallsQueue().isEmpty());
    	}
		
    	//The final value of call processed must be 10
    	assertEquals(10,dispatchertested.getCallsCounter());
    	
    	//Wait while dispatcher process all the calls
    	try {
			Thread.sleep(10100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//At the end the queue must be empty
    	assertTrue(dispatchertested.getCallsQueue().isEmpty());
	}

}
