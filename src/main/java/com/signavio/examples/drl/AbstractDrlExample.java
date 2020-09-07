package com.signavio.examples.drl;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private final String kieSessionId;
	private final String packageName;
	private KieSession kieSession;
	
	
	public AbstractDrlExample(String kieSessionId, String packageName) {
		this.kieSessionId = kieSessionId;
		this.packageName = packageName;
	}
	
	
	public abstract void execute();
	
	
	/**
	 * Creates a new kie session based on the configuration in kmodules.xml
	 */
	protected KieSession newKieSession() {
		kieSession = kieClasspathContainer.newKieSession(kieSessionId);
		return kieSession;
	}
	
	
	/**
	 * Creates a new Input object that contains information about the input values that should be used during the
	 * execution of the drl file.
	 */
	protected Object createInput(Pair<String, Object>... inputs) {
		return createInput(stream(inputs).collect(toList()));
	}
	
	
	/**
	 * Creates a new Input object that contains information about the input values that should be used during the
	 * execution of the drl file.
	 */
	protected Object createInput(List<Pair<String, Object>> inputs) {
		try {
			// creating input object defined in the .drl file
			FactType inputType = getInputFactType();
			Object input = inputType.newInstance();
			
			// setting all given values to there respective fields
			inputs.forEach(pair -> inputType.set(input, pair.getKey(), pair.getValue()));
			
			return input;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	protected Object getOutput(String outputName) {
		FactType outputType = getOutputFactType(StringUtils.capitalize(outputName));
		Collection<?> outputs = kieSession.getObjects(new ClassObjectFilter(outputType.getFactClass()));
		return outputs.stream()
				.map(output -> outputType.get(output, outputName))
				.findFirst().orElse(null);
	}
	
	
	private FactType getInputFactType() {
		return kieSession.getKieBase()
				.getFactType(packageName, "Input");
	}
	
	
	private FactType getOutputFactType(String outputName) {
		return kieSession.getKieBase()
				.getFactType(packageName, outputName + "_Output");
	}
	
	protected void printAsJson(Object result) {
		try {
			System.out.println(new ObjectMapper().writeValueAsString(result));
		} catch (JsonProcessingException e) {
			System.err.println(result);
		}
	}
	
}
