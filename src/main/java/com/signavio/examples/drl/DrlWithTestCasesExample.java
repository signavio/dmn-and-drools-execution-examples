package com.signavio.examples.drl;

import java.util.List;

import com.signavio.bdm.testlab.exchange.TestCase;
import com.signavio.bdm.testlab.exchange.TestSuite;
import com.signavio.examples.TestSuiteUtil;
import com.signavio.examples.dmn.testsuite.TestResult;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.runtime.KieSession;

import static com.signavio.examples.TestSuiteUtil.getInputNames;
import static com.signavio.examples.TestSuiteUtil.getOutputName;
import static com.signavio.examples.TestSuiteUtil.getParameterValue;
import static com.signavio.examples.dmn.testsuite.TestResult.failure;
import static com.signavio.examples.dmn.testsuite.TestResult.success;
import static java.util.stream.Collectors.toList;

public class DrlWithTestCasesExample extends AbstractDrlExample {
	
	private static final String PACKAGE_NAME = "com.signavio.examples.drl.simple";
	
	
	@Override
	public void execute() {
		TestSuite testSuite = TestSuiteUtil.readTestSuite("drl");
		List<TestResult> testResults = executeTestCases(testSuite);
		testResults.forEach(System.out::println);
	}
	
	
	private List<TestResult> executeTestCases(TestSuite testSuite) {
		List<String> inputs = getInputNames(testSuite);
		String output = getOutputName(testSuite);
		
		return testSuite.getTestCases().stream()
				.map(testCase -> executeTestCase(testCase, inputs, output))
				.collect(toList());
	}
	
	
	private TestResult executeTestCase(TestCase testCase, List<String> inputs, String output) {
		// creating input object
		List<Pair<String, Object>> inputValues = TestSuiteUtil.getInputs(testCase, inputs);
		Object input = createInput(
				PACKAGE_NAME,
				inputValues
		);
		
		KieSession ksession = newKieSession();
		
		// setting input values
		ksession.insert(input);
		
		// executing decision logic
		ksession.fireAllRules();
		
		// retrieving execution results
		Comparable expectedOutput = getParameterValue(testCase.getExpectedParameter());
		Object actualOutput = getOutput(ksession, PACKAGE_NAME, output);
		
		// cleaning up
		ksession.dispose();
		
		return (expectedOutput.compareTo(actualOutput) == 0) ? success(testCase) : failure(testCase);
	}
	
}
