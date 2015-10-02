package com.everyting.server.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanShell {
	
	public static void main2(String[] args) {
		Interpreter i = new Interpreter();
		StringBuilder requestBuilder = new StringBuilder();
		Object responseData = new Object();
		try {
				i.set("responseData", responseData);
				i.source("/home/venkatesh_vasam/Downloads/TestBeanShell.java");
				System.out.println(responseData);
		} catch (EvalError e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println(requestBuilder);
	}

}
