package com.almundo.callcenter.callcenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;

import com.almundo.callcenter.entities.Director;
import com.almundo.callcenter.entities.Employee;
import com.almundo.callcenter.entities.Operator;
import com.almundo.callcenter.entities.Supervisor;
import com.almundo.callcenter.util.LoggerUtil;

/**
 * Hello world!
 *
 */
public class CallCenterExercise
{
    
	private static List<Employee> poolEmployees;
	
	public static void main( String[] args ) 
    {	
    	
    	Logger logger = LoggerUtil.getLogger(CallCenterExercise.class);
    	initEmployees();
    	
    	// Prints the pool of employees initiated 
    	poolEmployees.stream()
    	  			 .map(emp -> emp.getClass().getSimpleName() + " Id:"+emp.getEmployeeId())
    	  			 .forEach(logger::info);
    	
    	Dispatcher callDispatcher = new Dispatcher(poolEmployees);
    	
    	//On this "for" we go to simulate n inbound calls .
    	for(int i=0;i<100;i++) {
    		callDispatcher.dispatchCall(new Call());
    	}
    	
    	callDispatcher.stop();
    	
    }

	
	/**
	 * Here we initialize the pool of employees available to answer calls
	 * we are assuming 5 operators, 3 supervisors and 2 directors.
	 */
	public static void initEmployees() {	
		LoggerUtil.getLogger(CallCenterExercise.class).info("Initializing a pool with the employees available to answer calls {} ","");		
		poolEmployees= Collections.synchronizedList(new ArrayList<>());		
		synchronized (poolEmployees) {
			for(int i=1; i<=10; i++) {
				if (i<=5) {
					poolEmployees.add(new Operator(i));
				}else if (i>5 && i<=8) {
					poolEmployees.add(new Supervisor(i));
				}else {
					poolEmployees.add(new Director(i));
				}
			}			
		}	
	}
}
