package com.almundo.callcenter.callcenter;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.almundo.callcenter.entities.Director;
import com.almundo.callcenter.entities.Employee;
import com.almundo.callcenter.entities.Operator;
import com.almundo.callcenter.entities.Supervisor;
import com.almundo.callcenter.listener.EmployeeFreeListener;
import com.almundo.callcenter.util.EmployeeStatus;
import com.almundo.callcenter.util.LoggerUtil;


public class Dispatcher {
	
	private List<Employee> pool;
	private static final int NUMBER_OF_THREADS = 10;
	
	//The dispatcher is started with and ExecutorService with a pool of ten threads, 
	//so it can process ten simultaneous calls
	private ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
	
	//The dispatcher has an Queue for dispatch the calls in order and 
	private ConcurrentLinkedQueue<Call> callsQueue = new ConcurrentLinkedQueue();
	
	// The dispatcher has an callsCounter that is used to assign an id to every inbound call in the order that are processed
	private long callsCounter = 0;
	
	
	public Dispatcher(List<Employee> pool) {
		super();
		this.pool = pool;
	}
	
	

	public List<Employee> getPool() {
		return pool;
	}

	public Queue<Call> getCallsQueue() {
		return callsQueue;
	}

	public long getCallsCounter() {
		return callsCounter;
	}


	/**
	 * This method receive and inbound call as a parameter and dispatch it to the first available employee according
	 * to the business rules.
	 * 
	 * If the inbound call is null the method was requested by the listener and it is trying to process the reminder 
	 * calls on the queue
	 * 
	 * @param inboundCall
	 */
	public synchronized void dispatchCall(Call inboundCall) {		
				
		//All the inbound calls are added to a queue, and the dispatcher sends the head call to the first 
		//available employee.
		if(inboundCall !=null) {
			callsCounter++;
			inboundCall.setCallId(callsCounter);		
			callsQueue.add(inboundCall);
		}

		Employee attendantEmployee;		
		
		//Select the first operator free
		attendantEmployee= pool.stream()
				.filter(s ->  (s instanceof Operator &&  s.getStatus().equals(EmployeeStatus.FREE)) )
				.findFirst().orElse(null);

		//If there is not a free operator try to find the first free supervisor
		if(attendantEmployee ==null) {
			attendantEmployee= pool.stream()
					.filter(s ->  (s instanceof Supervisor &&  s.getStatus().equals(EmployeeStatus.FREE)) )
					.findFirst().orElse(null);			
		}
		//Finally if there are not a free operator and a free supervisor find a free director
		if(attendantEmployee ==null) {
			attendantEmployee= pool.stream()
					.filter(s ->  (s instanceof Director &&  s.getStatus().equals(EmployeeStatus.FREE)) )
					.findFirst().orElse(null);			
		}		
		// On this point the dispatcher send the call on one new thread from the executor to the first free employee found
		if (attendantEmployee != null) {
			final Employee currEmployee = attendantEmployee;
			currEmployee.registerListener(new DispatchCalltoFreeEmployeeListener());
			if(!callsQueue.isEmpty()) {
				currEmployee.setStatus(EmployeeStatus.ON_CALL);
				executor.submit(()->currEmployee.answerCall(callsQueue.poll()));
			}else {
				LoggerUtil.getLogger(this.getClass()).info("The calls queue is empty {}","");
			}							
		}else {
			//If there are not employees availables to answer the call it will remind on the queue to be attended later
			LoggerUtil.getLogger(this.getClass()).warn("No employees available to answer call {}",callsQueue.peek().getCallId());
			//When all the threads of the executor service pool are busy the main thread sleeps 
			//for a moment waiting while one of the threads is available. 
			try {
				while(!callsQueue.isEmpty())
				wait(100);
			} catch (InterruptedException e) { 
				LoggerUtil.getLogger(this.getClass()).error(e.getMessage());
				Thread.currentThread().interrupt();
			}
		}
		
	}	
	
	public void stop() {
		executor.shutdown();
	}
	
	/**
	 * Implementation of the Interface EmployeeFreeListener used to receive a notification from an employee 
	 * when ends to attend a call.
	 * 
	 * When the event happen, the method dispatchCall is called to process the next call in the queue 
	 * 
	 * @author ecarrillo
	 *
	 */
	public class DispatchCalltoFreeEmployeeListener implements EmployeeFreeListener{

		@Override
		public synchronized void onEmployeeFree(Employee employeeFree) {
			employeeFree.setStatus(EmployeeStatus.FREE);
			LoggerUtil.getLogger(this.getClass()).info("The employee {} is with status FREE",employeeFree.getEmployeeId());
			dispatchCall(null);
		}
		
	}
	
}


