package com.signavio.examples.drl;

import java.math.BigDecimal;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.kie.api.runtime.KieSession;

public class DrlSandbox extends AbstractDrlExample {
	
	private static final String DESCRIPTION = "Sandbox";
	
	private static final String PACKAGE_NAME = "com.signavio.examples.drl.sandbox";
	private static final String SESSION_ID = "SignavioExampleDroolsSandboxKS";
	
	
	public DrlSandbox() {
		super(SESSION_ID, PACKAGE_NAME);
	}
	
	
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	
	@Override
	public void execute() {
		
		KieSession ksession = newKieSession();
		
		// creating input object
		Object input = createInput(
				ImmutablePair.of("age", new BigDecimal(32))
		);
		
		// defining the desired output
		String outputName = "isLegalAge";
		
		// setting input values
		ksession.insert(input);
		
		// executing decision logic
		ksession.fireAllRules();
		
		// retrieving execution results
		System.out.println("Results:");
		printAsJson(getOutput(outputName));
		
		// cleaning up
		ksession.dispose();
		
	}
	
}
