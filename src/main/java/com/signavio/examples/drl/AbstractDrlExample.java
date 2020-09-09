package com.signavio.examples.drl;

import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static java.util.Arrays.stream;

public abstract class AbstractDrlExample {
	
	private final KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
	
	
	public abstract void execute();
	
	
	/**
	 * Creates a new kie session based on the configuration in kmodules.xml
	 */
	protected KieSession newKieSession() {
		return kieClasspathContainer.newKieSession("SignavioExampleDroolsSimpleKS");
	}
	
	
	/**
	 * Creates a new Input object that contains information about the input values that should be used during the
	 * execution of the drl file.
	 */
	protected Object createInput(String packageName, Pair<String, Object>... fieldNamesToValues) {
		try {
			// creating input object defined in the .drl file
			FactType inputType = getInputFactType(packageName);
			Object input = inputType.newInstance();
			
			// setting all given values to there respective fields
			Consumer<Pair<String, Object>> inputSetter = pair -> inputType.set(input, pair.getKey(), pair.getValue());
			stream(fieldNamesToValues).forEach(inputSetter);
			
			return input;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private FactType getInputFactType(String packageName) {
		return kieClasspathContainer.getKieBase("SignavioExampleDroolsSimpleKB")
				.getFactType(packageName, "Input");
	}
	
}
