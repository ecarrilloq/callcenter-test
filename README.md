# Callcenter-test
Java application test for senior back end developer on almundo.com

## The Problem
This application try to simulate a call dispatcher on a call center with 3 kinds of employees, operator, supervisor and director.
The supervisor only can answer if all the operators are busy, and the directors only can answer a call if all the operators and supervisors are busy.

## Technologies
* Java 1.8
* Maven
* JUnit
* slf4j

## The Solution

The dispatcher uses an executor service with a pool of 10 thread to support 10 simultaneous calls.

When the dispatcher receives a call put it on a queue, then it look in the pool of employees and using streams search the first operator avalibale, if there are not operators try to find the first supervisor available and finalily tray to find a director and dispatch the call.

If the dispatcher receives a call and all the employees are busy the call remains on the queue until one employee is available.

When an employee ends a call and is available using an observer pattern it notify the dispatcher about the change on his state and the dispatcher tray to dispatchs one of the calls on the queue.



