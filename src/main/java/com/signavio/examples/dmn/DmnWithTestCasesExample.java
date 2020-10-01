package com.signavio.examples.dmn;

import java.util.List;

import com.signavio.bdm.testlab.exchange.TestCase;
import com.signavio.bdm.testlab.exchange.TestSuite;
import com.signavio.examples.testsuite.TestSuiteUtil;
import com.signavio.examples.testsuite.TestResult;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;

import static com.signavio.examples.testsuite.TestSuiteUtil.getInputNames;
import static com.signavio.examples.testsuite.TestSuiteUtil.getOutputName;
import static com.signavio.examples.testsuite.TestSuiteUtil.getParameterValue;
import static com.signavio.examples.testsuite.TestSuiteUtil.readTestSuite;
import static com.signavio.examples.testsuite.TestResult.failure;
import static com.signavio.examples.testsuite.TestResult.success;
import static java.util.stream.Collectors.toList;

public class DmnWithTestCasesExample extends AbstractDmnExample {
	
	private static final String DESCRIPTION = "Example with testcases";
	
	
	public DmnWithTestCasesExample() {
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
		
		// parsing the testsuite
		TestSuite testSuite = readTestSuite();
		
		// executing tests
		List<TestResult> testResults = executeTestCases(testSuite, model);
		testResults.forEach(this::printAsJson);
	}
	
	
	private List<TestResult> executeTestCases(TestSuite testSuite, DMNModel model) {
		List<String> inputs = getInputNames(testSuite);
		String output = getOutputName(testSuite);
		
		return testSuite.getTestCases().stream()
				.map(testCase -> executeTestCase(testCase, inputs, output, model))
				.collect(toList());
	}
	
	
	private TestResult executeTestCase(TestCase testCase, List<String> inputs, String output, DMNModel model) {
		DMNContext dmnContext = createDmnContextFromTestCase(testCase, inputs);
		
		DMNResult dmnResult = getDmnRuntime().evaluateAll(model, dmnContext);
		
		Comparable expectedOutput = getParameterValue(testCase.getExpectedParameter());
		Object actualOutput = getDecisionOutput(output, dmnResult);
		
		return (expectedOutput.compareTo(actualOutput) == 0) ? success(testCase) : failure(testCase);
	}
	
	
	private Object getDecisionOutput(String output, DMNResult dmnResult) {
		return dmnResult.getDecisionResults().stream()
				.filter(result -> output.equals(result.getDecisionName()))
				.map(DMNDecisionResult::getResult)
				.findFirst().orElse(null);
	}
	
	
	private DMNContext createDmnContextFromTestCase(TestCase testCase, List<String> inputNames) {
		List<Pair<String, Object>> inputs = TestSuiteUtil.getInputs(
				testCase,
				inputNames
		);
		return createDmnContext(inputs);
	}
	
}
