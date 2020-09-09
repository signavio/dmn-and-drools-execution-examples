package com.signavio.examples.drl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

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
	protected Object createInput(String packageName, Pair<String, Object>... inputs) {
		return createInput(packageName, stream(inputs).collect(toList()));
	}
	
	
	/**
	 * Creates a new Input object that contains information about the input values that should be used during the
	 * execution of the drl file.
	 */
	protected Object createInput(String packageName, List<Pair<String, Object>> inputs) {
		try {
			// creating input object defined in the .drl file
			FactType inputType = getInputFactType(packageName);
			Object input = inputType.newInstance();
			
			// setting all given values to there respective fields
			inputs.forEach(pair -> inputType.set(input, pair.getKey(), pair.getValue()));
			
			return input;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	protected Object getOutput(KieSession ksession, String packageName, String outputName) {
		FactType outputType = getOutputFactType(packageName, StringUtils.capitalize(outputName));
		Collection<?> outputs = ksession.getObjects(new ClassObjectFilter(outputType.getFactClass()));
		return outputs.stream()
				.map(output -> outputType.get(output, outputName))
				.findFirst().orElse(null);
	}
	
	
	private FactType getInputFactType(String packageName) {
		return kieClasspathContainer.getKieBase("SignavioExampleDroolsSimpleKB")
				.getFactType(packageName, "Input");
	}
	
	
	private FactType getOutputFactType(String packageName, String outputName) {
		return kieClasspathContainer.getKieBase("SignavioExampleDroolsSimpleKB")
				.getFactType(packageName, outputName + "_Output");
	}
	
}
