package com.signavio.examples;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
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
		KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
		DMNRuntime dmnRuntime = kieClasspathContainer
				.newKieSession("SignavioExampleDMNSimpleKS")
				.getKieRuntime(DMNRuntime.class);
		List<DMNModel> models = dmnRuntime.getModels();
		DMNModel model = models.get(0); // assuming there is only one model in the KieBase
		
		DMNContext dmnContext = dmnRuntime.newContext();
		dmnContext.set("customerLevel", "Silver");
		dmnContext.set("customerYears", 15);
		
		DMNResult topLevelResult = dmnRuntime.evaluateAll(model, dmnContext);
		DMNResult lowerLevelResult =
				dmnRuntime.evaluateByName(model, dmnContext, "calculateDiscountBasedOnYears");
		for (DMNDecisionResult dr : topLevelResult.getDecisionResults()) {
			System.out.println("Decision '" + dr.getDecisionName() + "' : " + dr.getResult());
		}
		for (DMNDecisionResult dr : lowerLevelResult.getDecisionResults()) {
			System.out.println("Decision '" + dr.getDecisionName() + "' : " + dr.getResult());
		}
		
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
	
	
	private static Object createInput(KieBase kieBase, Pair<String, Object>...fieldNamesToValues)
			throws InstantiationException, IllegalAccessException {
		FactType inputType = kieBase.getFactType("com.signavio.examples.drl.simple", "Input");
		Object input = inputType.newInstance();
		for (Pair<String, Object> fieldNameToValue : fieldNamesToValues) {
			inputType.set(input, fieldNameToValue.getLeft(), fieldNameToValue.getRight());
		}
		return input;
	}
	
}
