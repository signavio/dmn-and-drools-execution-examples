package org.drools.examples.signavio;

import java.math.BigDecimal;

import org.drools.core.ClassObjectFilter;
import org.kie.api.KieServices;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;

public class SignavioExample {
	
	public static void main(String[] args) throws IllegalAccessException, InstantiationException {
		KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
		
		KieSession ksession = kieClasspathContainer.newKieSession("SignavioExampleKS");
		KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
		
		createInput(17, ksession);
		ksession.fireAllRules();
		
		ksession.getObjects().forEach(System.out::println);
		
		ksession.dispose();
	}
	
	//		DMNRuntime dmnRuntime =
	//				kieClasspathContainer.newKieSession("SignavioDrools").getKieRuntime(DMNRuntime.class);
	//		List<DMNModel> models = dmnRuntime.getModels();
	
	private static void createInput(int age, KieSession ksession) throws InstantiationException, IllegalAccessException {
		FactType inputType = ksession.getKieBase().getFactType("org.drools.examples.signavio.drl", "Input");
		Object input = inputType.newInstance();
		inputType.set(input, "age", new BigDecimal(age));
		ksession.insert(input);
	}
	
	
	private static class Input {
		
		BigDecimal age;
		
		
		public Input(int age) {
			this.age = new BigDecimal(age);
		}
		
		
		public BigDecimal getAge() {
			return age;
		}
		
		
		public void setAge(BigDecimal age) {
			this.age = age;
		}
	}
}
