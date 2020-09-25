package com.signavio.examples.dmn;

import java.util.Collection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public abstract class AbstractDmnExample {
	
	private DMNRuntime dmnRuntime;
	
	
	public AbstractDmnExample(String knowledgeBaseId) {
		dmnRuntime = createDmnRuntime(knowledgeBaseId);
	}
	
	
	public abstract void execute();
	
	
	/**
	 * Creates a DMNRuntime based on the configuration given in kmodule.xml.
	 * <p>
	 * The runtime contains information about all dmn models that where parsed from .dmn files and that are available
	 * for execution.
	 */
	private DMNRuntime createDmnRuntime(String knowledgeBaseId) {
		KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
		return kieClasspathContainer
				.getKieBase(knowledgeBaseId)
				.newKieSession()
				.getKieRuntime(DMNRuntime.class);
	}
	
	
	protected DMNRuntime getDmnRuntime() {
		return dmnRuntime;
	}
	
	
	/**
	 * Creates a new DmnContext that contains information about the input values that should be used during the
	 * execution of the dmn model.
	 */
	protected DMNContext createDmnContext(Pair<String, Object>... inputs) {
		return createDmnContext(stream(inputs).collect(toList()));
	}
	
	
	/**
	 * Creates a new DmnContext that contains information about the input values that should be used during the
	 * execution of the dmn model.
	 */
	protected DMNContext createDmnContext(Collection<Pair<String, Object>> inputs) {
		DMNContext dmnContext = getDmnRuntime().newContext();
		
		// setting values for inputs
		inputs.forEach(inputValue -> dmnContext.set(inputValue.getKey(), inputValue.getValue()));
		
		return dmnContext;
	}
	
	
	/**
	 * Handles the result to use the data collected during the dmn execution.
	 */
	protected void handleResult(DMNResult decisionResult) {
		decisionResult.getDecisionResults().forEach(this::printResult);
		System.out.println();
	}
	
	
	private void printResult(DMNDecisionResult decisionResult) {
		System.out.println("Decision '" + decisionResult.getDecisionName() + "' : " + decisionResult.getResult());
	}
	
	protected void printAsJson(Object result) {
		try {
			System.out.println(new ObjectMapper().writeValueAsString(result));
		} catch (JsonProcessingException e) {
			System.err.println(result);
		}
	}
	
}
