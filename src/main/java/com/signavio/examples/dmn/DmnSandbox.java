/*
	Copyright 2020 Signavio GmbH
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
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
