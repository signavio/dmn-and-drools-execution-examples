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
package com.signavio.examples.drl;

import java.math.BigDecimal;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.kie.api.runtime.KieSession;

public class SimpleDrlExample extends AbstractDrlExample {
	
	private static final String DESCRIPTION = "Simple Example";
	
	private static final String PACKAGE_NAME = "com.signavio.examples.drl.simple";
	private static final String SESSION_ID = "SignavioExampleDroolsSimpleKS";
	
	
	public SimpleDrlExample() {
		super(SESSION_ID, PACKAGE_NAME);
	}
	
	
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	
	@Override
	public void execute() {
		KieSession ksession = newKieSession();
		
		// creating input object
		Object input = createInput(
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
	
}
