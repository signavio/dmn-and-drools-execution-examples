package org.drools.examples.signavio;

import java.math.BigDecimal;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;

public class SignavioExample {
	
	public static void main(String[] args) throws IllegalAccessException, InstantiationException {
		executeDmnXml();
	}
	
	
	private static void executeDmnXml() throws IllegalAccessException, InstantiationException {
		KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
		
		DMNRuntime dmnRuntime =
				kieClasspathContainer.newKieSession("SignavioExampleDmnXmlKS").getKieRuntime(DMNRuntime.class);
		List<DMNModel> models = dmnRuntime.getModels();
		
		DMNContext dmnContext = dmnRuntime.newContext();
		
		dmnContext.set("age", 19);
		DMNResult dmnResult = dmnRuntime.evaluateAll(models.get(0), dmnContext);
		for (DMNDecisionResult dr : dmnResult.getDecisionResults()) {
			System.out.println("Age " + 19 + " Decision '" + dr.getDecisionName() + "' : " + dr.getResult());
		}
		
	}
	
	
	private static void executeDrools() throws InstantiationException, IllegalAccessException {
		KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
		
		KieSession ksession = kieClasspathContainer.newKieSession("SignavioExampleDroolsKS");
		KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
		
		createInput(19, ksession);
		ksession.fireAllRules();
		
		ksession.getObjects().forEach(System.out::println);
		
		ksession.dispose();
	}
	
	
	private static void createInput(int age, KieSession ksession)
			throws InstantiationException, IllegalAccessException {
		FactType inputType = ksession.getKieBase().getFactType("org.drools.examples.signavio.drl", "Input");
		Object input = inputType.newInstance();
		inputType.set(input, "age", new BigDecimal(age));
		ksession.insert(input);
	}
	
}
