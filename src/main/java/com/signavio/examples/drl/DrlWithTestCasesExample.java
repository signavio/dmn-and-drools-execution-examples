package com.signavio.examples.drl;

import java.util.List;

import com.signavio.bdm.testlab.exchange.TestCase;
import com.signavio.bdm.testlab.exchange.TestSuite;
import com.signavio.examples.testsuite.TestResult;
import com.signavio.examples.testsuite.TestSuiteUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.runtime.KieSession;

import static com.signavio.examples.testsuite.TestResult.failure;
import static com.signavio.examples.testsuite.TestResult.success;
import static com.signavio.examples.testsuite.TestSuiteUtil.getInputNames;
import static com.signavio.examples.testsuite.TestSuiteUtil.getOutputName;
import static com.signavio.examples.testsuite.TestSuiteUtil.getParameterValue;
import static java.util.stream.Collectors.toList;

public class DrlWithTestCasesExample extends AbstractDrlExample {
	
	private static final String DESCRIPTION = "Example with testcases";
	
	private static final String PACKAGE_NAME = "com.signavio.examples.drl.simple";
	private static final String SESSION_ID = "SignavioExampleDroolsSimpleKS";
	
	
	public DrlWithTestCasesExample() {
		super(SESSION_ID, PACKAGE_NAME);
	}
	
	
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	
	@Override
	public void execute() {
		TestSuite testSuite = TestSuiteUtil.readTestSuite();
		List<TestResult> testResults = executeTestCases(testSuite);
		testResults.forEach(this::printAsJson);
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
		KieSession ksession = newKieSession();
		
		List<Pair<String, Object>> inputValues = TestSuiteUtil.getInputs(testCase, inputs);
		
		Object input = createInput(
				inputValues
		);
		
		// setting input values
		ksession.insert(input);
		
		// executing decision logic
		ksession.fireAllRules();
		
		// retrieving execution results
		Comparable expectedOutput = getParameterValue(testCase.getExpectedParameter());
		Object actualOutput = getOutput(output);
		
		// cleaning up
		ksession.dispose();
		
		return (expectedOutput.compareTo(actualOutput) == 0) ? success(testCase) : failure(testCase);
	}
	
}
