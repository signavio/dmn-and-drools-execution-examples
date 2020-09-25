package com.signavio.examples;

import com.signavio.examples.dmn.DmnSandbox;
import com.signavio.examples.dmn.DmnWithTestCasesExample;
import com.signavio.examples.dmn.SimpleDmnExample;
import com.signavio.examples.drl.DrlSandbox;
import com.signavio.examples.drl.DrlWithTestCasesExample;
import com.signavio.examples.drl.OwnTypesDrlExample;
import com.signavio.examples.drl.SimpleDrlExample;
import com.signavio.examples.drl.DynamicSandboxDrlExample;

public class SignavioExamples {
	
	public static void main(String[] args) throws Exception {
		System.out.println("\n\n=== DYNAMIC DROOLS EXECUTION ===");
		new DynamicSandboxDrlExample().execute();

		System.out.println("\n\n=== DMN EXECUTION ===");
		new SimpleDmnExample().execute();

		System.out.println("\n\n=== DMN EXECUTION WITH TESTCASES ===");
		new DmnWithTestCasesExample().execute();

		System.out.println("\n\n=== DROOLS EXECUTION ===");
		new SimpleDrlExample().execute();

		System.out.println("\n\n=== DROOLS EXECUTION WITH OWN TYPE ===");
		new OwnTypesDrlExample().execute();

		System.out.println("\n\n=== DROOLS EXECUTION WITH TEST CASES ===");
		new DrlWithTestCasesExample().execute();

		System.out.println("\n\n=== DROOLS EXECUTION SANDBOX ===");
		new DrlSandbox().execute();
		
		System.out.println("\n\n=== DMN EXECUTION SANDBOX ===");
		new DmnSandbox().execute();
	}
	
	
}
