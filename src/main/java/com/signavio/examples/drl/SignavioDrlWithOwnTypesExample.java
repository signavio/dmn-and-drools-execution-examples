package com.signavio.examples.drl;

import java.math.BigDecimal;
import java.util.function.Consumer;

import com.signavio.examples.owntypes.CustomerData;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static java.util.Arrays.stream;

public class SignavioDrlWithOwnTypesExample {
	
	public void execute() {
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieClasspathContainer = kieServices.getKieClasspathContainer();
		KieSession ksession = kieClasspathContainer.newKieSession("SignavioExampleDroolsSimpleKS");
		
		// creating input object
		Object input = createInput(
				kieClasspathContainer.getKieBase("SignavioExampleDroolsSimpleKB"),
				ImmutablePair.of("customerData", new CustomerData("Silver", new BigDecimal(15)))
		);
		
		// setting input values
		ksession.insert(input);
		
		// executing decision logic
		ksession.fireAllRules();
		
		// retrieving execution results
		ksession.getObjects().forEach(object -> System.out.println(object.getClass().getSimpleName() + ": " + object));
		
		// cleaning up
		ksession.dispose();
	}
	
	
	/**
	 * Creates a new Input object that contains information about the input values that should be used during the
	 * execution of the drl file.
	 */
	private Object createInput(KieBase kieBase, Pair<String, Object>... fieldNamesToValues) {
		try {
			// creating input object defined in the .drl file
			FactType inputType = kieBase.getFactType("com.signavio.examples.drl.owntypes", "Input");
			Object input = inputType.newInstance();
			
			// setting all given values to there respective fields
			Consumer<Pair<String, Object>> inputSetter = pair -> inputType.set(input, pair.getKey(), pair.getValue());
			stream(fieldNamesToValues).forEach(inputSetter);
			
			return input;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
}
