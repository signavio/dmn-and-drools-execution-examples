package com.signavio.examples.dmn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;

public class DmnSandbox extends AbstractDmnExample {
	
	private static final String DESCRIPTION = "Sandbox";
	
	
	public DmnSandbox() {
		super("SignavioExampleDMNSandboxKB");
	}
	
	
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	
	@Override
	public void execute() {
		// parsing model from .dmn
		DMNModel model = getDmnRuntime().getModels().get(0); // assuming there is only one model in the KieBase
		
		// setting input data
		DMNContext dmnContext = createDmnContext(
				ImmutablePair.of("age", 32)
		);
		
		// executing decision logic
		DMNResult topLevelResult = getDmnRuntime().evaluateAll(model, dmnContext);
		
		// retrieving execution results
		System.out.println("Results:");
		topLevelResult.getDecisionResults().stream()
				.map(DMNDecisionResult::getResult)
				.forEach(this::printAsJson);
		
	}
}
