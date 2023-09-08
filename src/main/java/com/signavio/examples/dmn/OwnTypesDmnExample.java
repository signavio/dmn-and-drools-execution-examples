// SPDX-FileCopyrightText: 2023 SAP-Signavio
//
// SPDX-License-Identifier: Apache-2.0

package com.signavio.examples.dmn;

import java.math.BigDecimal;

import com.signavio.examples.owntypes.CustomerData;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;

public class OwnTypesDmnExample extends AbstractDmnExample {
	
	private static final String DESCRIPTION = "Example using an own type";
	
	private static final String KNOWLEDGE_BASE_ID = "SignavioExampleDMNOwnTypesKB";
	
	
	public OwnTypesDmnExample() {
		super(KNOWLEDGE_BASE_ID);
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
				ImmutablePair.of("customerData", new CustomerData("Silver", new BigDecimal(15)))
		);
		
		// executing decision logic
		DMNResult topLevelResult = getDmnRuntime().evaluateAll(model, dmnContext);
		
		// retrieving execution results
		System.out.println("--- results of evaluating all ---");
		topLevelResult.getDecisionResults().forEach(this::printAsJson);
	}
	
}
