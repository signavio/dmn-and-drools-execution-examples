package com.signavio.examples;

import com.signavio.examples.dmn.DmnSandbox;
import com.signavio.examples.dmn.DmnWithTestCasesExample;
import com.signavio.examples.dmn.SimpleDmnExample;
import com.signavio.examples.drl.DrlSandbox;
import com.signavio.examples.drl.DrlWithTestCasesExample;
import com.signavio.examples.drl.DynamicSandboxDrlExample;
import com.signavio.examples.drl.OwnTypesDrlExample;
import com.signavio.examples.drl.SimpleDrlExample;

import static java.lang.System.out;

public class SignavioExamples {
	
	public static void main(String[] args) {
		executeDmnExamples();
		executeDroolsExamples();
	}
	
	
	private static void executeDmnExamples() {
		out.println("=== DMN EXECUTION ===");
		executeWithHeadline(new SimpleDmnExample());
		executeWithHeadline(new DmnWithTestCasesExample());
		executeWithHeadline(new DmnSandbox());
	}
	
	
	private static void executeDroolsExamples() {
		out.println("=== DROOLS EXECUTION ===");
		executeWithHeadline(new SimpleDrlExample());
		executeWithHeadline(new OwnTypesDrlExample());
		executeWithHeadline(new DrlWithTestCasesExample());
		executeWithHeadline(new DynamicSandboxDrlExample());
		executeWithHeadline(new DrlSandbox());
	}
	
	
	private static void executeWithHeadline(AbstractSignavioExample example) {
		out.println("= " + example.getDescription() + " =");
		example.execute();
		out.println();
	}
	
}
