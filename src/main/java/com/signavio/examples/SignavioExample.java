package com.signavio.examples;

import java.math.BigDecimal;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

public class SignavioExample {
	
	public static void main(String[] args) throws IllegalAccessException, InstantiationException {
		System.out.println("\n\n=== DMN EXECUTION ===\n\n");
		executeDmnXml();
		System.out.println("\n\n=== DROOLS EXECUTION ===\n\n");
		executeDrools();
	}
	
	
	private static void executeDmnXml() {
		// parsing the model from .dmn
		DMNRuntime dmnRuntime = createDmnRuntime();
		DMNModel model = dmnRuntime.getModels().get(0); // assuming there is only one model in the KieBase
		
		// setting input data
		DMNContext dmnContext = createDmnContext(dmnRuntime);
		
		// executing the decision logic
		DMNResult topLevelResult = dmnRuntime.evaluateAll(model, dmnContext);
		
		// retrieving the execution results
		System.out.println("--- top level results ---");
		handleResult(topLevelResult);
		
		// retrieve intermediate results
		System.out.println("--- intermediate results ---");
		DMNResult lowerLevelResult = dmnRuntime.evaluateByName(model, dmnContext, "calculateDiscountBasedOnYears");
		handleResult(lowerLevelResult);
	}
	
	
	/**
	 * Handles the result to use the data collected during the dmn execution.
	 */
	private static void handleResult(DMNResult decisionResult) {
		decisionResult.getDecisionResults().forEach(SignavioExample::printResult);
		System.out.println();
	}
	
	
	private static void printResult(DMNDecisionResult decisionResult) {
		System.out.println("Decision '" + decisionResult.getDecisionName() + "' : " + decisionResult.getResult());
	}
	
	
	/**
	 * Creates a DMNRuntime based on the configuration given in kmodule.xml.
	 * <p>
	 * The runtime contains information about all dmn models that where parsed from .dmn files and that are available
	 * for execution.
	 */
	private static DMNRuntime createDmnRuntime() {
		KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
		return kieClasspathContainer
				.newKieSession("SignavioExampleDMNSimpleKS")
				.getKieRuntime(DMNRuntime.class);
	}
	
	
	/**
	 * Creates a new DmnContext that contains information about the input values that should be used during the
	 * execution of the dmn model.
	 */
	private static DMNContext createDmnContext(DMNRuntime dmnRuntime) {
		DMNContext dmnContext = dmnRuntime.newContext();
		
		// setting values for inputs
		dmnContext.set("customerLevel", "Silver");
		dmnContext.set("customerYears", 15);
		
		return dmnContext;
	}
	
	
	private static void executeDrools() throws InstantiationException, IllegalAccessException {
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kieClasspathContainer = kieServices.getKieClasspathContainer();
		
		Object input = createInput(
				kieClasspathContainer.getKieBase("SignavioExampleDroolsSimpleKB"),
				ImmutablePair.of("customerLevel", "Silver"),
				ImmutablePair.of("customerYears", new BigDecimal(15)));
		
		KieSession ksession = kieClasspathContainer.newKieSession("SignavioExampleDroolsSimpleKS");
		ksession.insert(input);
		ksession.fireAllRules();
		ksession.getObjects().forEach(object -> System.out.println(object.getClass().getSimpleName() + ": " + object));
		ksession.dispose();
	}
	
	
	private static Object createInput(KieBase kieBase, Pair<String, Object>... fieldNamesToValues)
			throws InstantiationException, IllegalAccessException {
		FactType inputType = kieBase.getFactType("com.signavio.examples.drl.simple", "Input");
		Object input = inputType.newInstance();
		for (Pair<String, Object> fieldNameToValue : fieldNamesToValues) {
			inputType.set(input, fieldNameToValue.getLeft(), fieldNameToValue.getRight());
		}
		return input;
	}
	
}
