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

import java.util.List;

import com.signavio.bdm.testlab.exchange.TestSuite;
import com.signavio.examples.testsuite.TestResult;
import org.kie.dmn.api.core.DMNModel;

import static com.signavio.examples.testsuite.TestSuiteUtil.readTestSuite;

public class TestExample extends AbstractDmnTestableExample {
	
	private static final String DESCRIPTION = "Test Example";
	
	
	public TestExample() {
		super("SignavioExampleDMNTestKB");
	}
	
	
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	
	@Override
	public void execute() {
		// parsing model from .dmn
		DMNModel model = getDmnRuntime().getModels().get(0); // assuming there is only one model in the KieBase
		
		// parsing the testsuite
		TestSuite testSuite = readTestSuite("tests.json");
		
		// executing tests
		List<TestResult> testResults = executeTestCases(testSuite, model);
		testResults.forEach(this::printAsJson);
	}
	
}
