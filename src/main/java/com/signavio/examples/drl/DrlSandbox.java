package com.signavio.examples.drl;

import java.math.BigDecimal;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;

public class DrlSandbox extends AbstractDrlExample {
	
	private static final String PACKAGE_NAME = "com.signavio.examples.drl.sandbox";
	
	@Override
	public void execute() {
		
		KieSession ksession = newKieSession("SignavioExampleDroolsSandboxKS");
		
		// creating input object
		Object input = createInput(
				PACKAGE_NAME,
				ImmutablePair.of("aNumber", new BigDecimal(5)));
		
		// setting input values
		ksession.insert(input);
		
		// executing decision logic
		ksession.fireAllRules();
		
		// retrieving execution results
		ksession.getObjects().forEach(System.out::println);
		
		// cleaning up
		ksession.dispose();
		
	}
	
}
