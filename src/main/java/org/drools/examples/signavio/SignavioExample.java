package org.drools.examples.signavio;

import java.math.BigDecimal;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;

public class SignavioExample {
	
	public static void main(String[] args) {
		KieContainer kieClasspathContainer = KieServices.Factory.get()
				.getKieClasspathContainer();
		
		KieSession ksession = kieClasspathContainer
				.newKieSession("SignavioExampleKS");
		
//		DMNRuntime dmnRuntime =
//				kieClasspathContainer.newKieSession("SignavioDrools").getKieRuntime(DMNRuntime.class);
//		List<DMNModel> models = dmnRuntime.getModels();

		ksession.insert(new Input(17));
		
//		ksession.startProcess("Example");
		ksession.fireAllRules();
		
		
		ksession.getObjects().forEach(System.out::println);

		ksession.dispose();
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
