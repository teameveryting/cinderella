package com.everyting.server.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.everyting.server.vendor.SQLConstructor;
import com.everyting.server.vendor.VendorManager;

import bsh.EvalError;
import bsh.Interpreter;

public class BeanShell {
	
	public static void main(String[] args) {
		Interpreter i = new Interpreter();
		StringBuilder requestBuilder = new StringBuilder();
		SQLConstructor sqlConstructor = VendorManager.getSQLConstructor(); 
		try {
				i.set("requestBuilder", requestBuilder);
				i.set("sqlConstructor", sqlConstructor);
				requestBuilder.append("Before shell\n");
				i.source("/home/venkatesh_vasam/test.java");
				
				requestBuilder.append("after shell");
		
		} catch (EvalError e) {
			System.out.println(e.getMessage());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		System.out.println(requestBuilder);
	}

}
