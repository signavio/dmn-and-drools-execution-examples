package com.signavio.examples.dmn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

public class SimpleDmnExample extends AbstractDmnExample {
	
	@Override
	public void execute() {
		// parsing model from .dmn
		DMNRuntime dmnRuntime = createDmnRuntime();
		DMNModel model = dmnRuntime.getModels().get(0); // assuming there is only one model in the KieBase
		
		// setting input data
		DMNContext dmnContext = createDmnContext(
				dmnRuntime,
				ImmutablePair.of("customerLevel", "Silver"),
				ImmutablePair.of("customerYears", 15)
		);
		
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
	
}
