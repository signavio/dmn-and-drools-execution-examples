package com.signavio.examples;

import com.signavio.examples.dmn.DmnWithTestCasesExample;
import com.signavio.examples.dmn.SimpleDmnExample;
import com.signavio.examples.drl.DrlSandbox;
import com.signavio.examples.drl.DrlWithTestCasesExample;
import com.signavio.examples.drl.OwnTypesDrlExample;
import com.signavio.examples.drl.SimpleDrlExample;
import com.signavio.examples.drl.DynamicSandboxDrlExample;

public class SignavioExamples {
	
	public static void main(String[] args) throws Exception {
		System.out.println("\n\n=== DYNAMIC DROOLS EXECUTION ===\n\n");
		new DynamicSandboxDrlExample().execute();
		
		System.out.println("\n\n=== DMN EXECUTION ===\n\n");
		new SimpleDmnExample().execute();
		
		System.out.println("\n\n=== DMN EXECUTION WITH TESTCASES ===\n\n");
		new DmnWithTestCasesExample().execute();
		
		System.out.println("\n\n=== DROOLS EXECUTION ===\n\n");
		new SimpleDrlExample().execute();
		
		System.out.println("\n\n=== DROOLS EXECUTION WITH OWN TYPE ===\n\n");
		new OwnTypesDrlExample().execute();
		
		System.out.println("\n\n=== DROOLS EXECUTION WITH TEST CASES ===\n\n");
		new DrlWithTestCasesExample().execute();
		
		System.out.println("\n\n=== DROOLS EXECUTION SANDBOX ===\n\n");
		new DrlSandbox().execute();
	}
	
	
}
