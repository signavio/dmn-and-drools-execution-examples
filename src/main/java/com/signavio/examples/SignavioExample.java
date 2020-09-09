package com.signavio.examples;

import java.math.BigDecimal;
import java.util.function.Consumer;

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

import static java.util.Arrays.stream;

public class SignavioExample {
	
	public static void main(String[] args) throws IllegalAccessException, InstantiationException {
		System.out.println("\n\n=== DMN EXECUTION ===\n\n");
		executeDmnXml();
		
		System.out.println("\n\n=== DROOLS EXECUTION ===\n\n");
		executeDrools();
	}
	
	
	private static void executeDmnXml() {
		// parsing model from .dmn
		DMNRuntime dmnRuntime = createDmnRuntime();
		DMNModel model = dmnRuntime.getModels().get(0); // assuming there is only one model in the KieBase
		
		// setting input data
		DMNContext dmnContext = createDmnContext(dmnRuntime);
		
		// executing decision logic
		DMNResult topLevelResult = dmnRuntime.evaluateAll(model, dmnContext);
		
		// retrieving execution results
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
		KieSession ksession = kieClasspathContainer.newKieSession("SignavioExampleDroolsSimpleKS");
		
		// creating input object
		Object input = createInput(
				kieClasspathContainer.getKieBase("SignavioExampleDroolsSimpleKB"),
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
	
	
	/**
	 * Creates a new Input object that contains information about the input values that should be used during the
	 * execution of the drl file.
	 */
	private static Object createInput(KieBase kieBase, Pair<String, Object>... fieldNamesToValues)
			throws InstantiationException, IllegalAccessException {
		// creating input object defined in the .drl file
		FactType inputType = kieBase.getFactType("com.signavio.examples.drl.simple", "Input");
		Object input = inputType.newInstance();
		
		// setting all given values to there respective fields
		Consumer<Pair<String, Object>> inputSetter = pair -> inputType.set(input, pair.getKey(), pair.getValue());
		stream(fieldNamesToValues).forEach(inputSetter);
		
		return input;
	}
	
}
