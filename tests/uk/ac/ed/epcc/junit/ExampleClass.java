/*******************************************************************************
 * Copyright (c) - The Univeristy of Edinburgh 2010
 *******************************************************************************/
package uk.ac.ed.epcc.junit;

public class ExampleClass implements ExampleInterface {

	public int doAdd(int a, int b) {
		return a + b;
	}
	public int doSubtract(int a,int  b){
		return a - b;
	}
	public void doThrow(boolean doit) throws Exception {
		if(doit){
			throw new Exception("You asked for it");
		}
		
	}

}
