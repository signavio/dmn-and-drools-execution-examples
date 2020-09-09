package com.signavio.examples.drl;

import java.math.BigDecimal;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.kie.api.runtime.KieSession;

public class SimpleDrlExample extends AbstractDrlExample {
	
	private static final String PACKAGE_NAME = "com.signavio.examples.drl.simple";
	
	
	@Override
	public void execute() {
		KieSession ksession = newKieSession();
		
		// creating input object
		Object input = createInput(
				PACKAGE_NAME,
				ImmutablePair.of("customerLevel", "Silver"),
				ImmutablePair.of("customerYears", new BigDecimal(15)));
		
		// setting input values
		ksession.insert(input);
		
		// executing decision logic
		ksession.fireAllRules();
		
		// retrieving execution results
		ksession.getObjects().forEach(object -> System.out.println(object.getClass().getSimpleName() + ": " + object));
		
		// cleaning up
		ksession.dispose();
	}
	
}
