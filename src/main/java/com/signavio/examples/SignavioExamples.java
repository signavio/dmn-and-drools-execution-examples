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
		executeExample(new SimpleDmnExample());
		executeExample(new DmnWithTestCasesExample());
		executeExample(new DmnSandbox());
	}
	
	
	private static void executeDroolsExamples() {
		out.println("=== DROOLS EXECUTION ===");
		executeExample(new SimpleDrlExample());
		executeExample(new OwnTypesDrlExample());
		executeExample(new DrlWithTestCasesExample());
		executeExample(new DynamicSandboxDrlExample());
		executeExample(new DrlSandbox());
	}
	
	
	private static void executeExample(AbstractSignavioExample example) {
		out.println("= " + example.getDescription() + " =");
		example.execute();
		out.println();
	}
	
}
