// SPDX-FileCopyrightText: 2023 SAP-Signavio
//
// SPDX-License-Identifier: Apache-2.0

package com.signavio.examples.dmn;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;

public class SimpleDmnExample extends AbstractDmnExample {
	
	private static final String DESCRIPTION = "Simple Example";
	
	
	public SimpleDmnExample() {
		super("SignavioExampleDMNSimpleKB");
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
				ImmutablePair.of("customerLevel", "Silver"),
				ImmutablePair.of("customerYears", 15)
		);
		
		// executing decision logic
		DMNResult topLevelResult = getDmnRuntime().evaluateAll(model, dmnContext);
		
		// retrieving execution results
		System.out.println("--- results of evaluating all ---");
		topLevelResult.getDecisionResults().forEach(this::printAsJson);
		
		// evaluating the decision logic partially
		System.out.println("--- results of evauluating partially ---");
		DMNResult lowerLevelResult = getDmnRuntime().evaluateByName(model, dmnContext, "calculateDiscountBasedOnYears");
		lowerLevelResult.getDecisionResults().forEach(this::printAsJson);
	}
	
}
