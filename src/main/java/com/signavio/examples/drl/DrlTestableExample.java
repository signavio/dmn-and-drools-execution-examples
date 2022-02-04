package com.signavio.examples.drl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.signavio.bdm.testlab.exchange.DefaultParameter;
import com.signavio.bdm.testlab.exchange.ParameterDefinition;
import com.signavio.bdm.testlab.exchange.TestCase;
import com.signavio.bdm.testlab.exchange.TestSuite;
import com.signavio.examples.testsuite.TestResult;
import com.signavio.examples.testsuite.TestSuiteUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import static com.signavio.examples.testsuite.TestResult.failure;
import static com.signavio.examples.testsuite.TestResult.success;
import static com.signavio.examples.testsuite.TestSuiteUtil.getOutputName;
import static com.signavio.examples.testsuite.TestSuiteUtil.getParameterValue;
import static com.signavio.examples.testsuite.TestSuiteUtil.getParameterValueComparator;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

@Slf4j
public class DrlTestableExample extends AbstractDrlExample {
	
	private final String testSuiteFileName;
	private final String description;
	
	
	public DrlTestableExample(String kieSessionId, String packageName, String testSuiteFileName, String description) {
		super(kieSessionId, packageName);
		this.testSuiteFileName = testSuiteFileName;
		this.description = description;
	}
	
	
	@Override
	public String getDescription() {
		return description;
	}
	
	
	public List<TestResult> executeTestCases(TestSuite testSuite) {
		String output = getOutputName(testSuite);
		
		return testSuite.getTestCases().stream()
				.map(testCase -> executeTestCase(testSuite, testCase, output))
				.collect(toList());
	}
	
	
	@SneakyThrows
	public TestResult executeTestCase(TestSuite testSuite, TestCase testCase, String output) {
		KieSession kieSession = newKieSession();
		
		FactType inputFactType = this.kieSession.getKieBase().getFactType(packageName, "Input");
		
		Map<String, FactField> inputFactFieldsByName = inputFactType.getFields()
				.stream()
				.collect(Collectors.toMap(FactField::getName, f -> f));
		Set<FactField> unsetInputFactFields = new HashSet<>(inputFactFieldsByName.values());
		
		Object inputBean = inputFactType.newInstance();
		
		range(0, testSuite.getInputParameterDefinitions().size())
				.forEach(i -> {
					ParameterDefinition parameterDefinition = testSuite.getInputParameterDefinitions().get(i);
					String requirementName = TestSuiteUtil.toCamelCase(parameterDefinition.getRequirementName());
					
					FactField factField = inputFactFieldsByName.get(requirementName);
					if (factField == null) {
						log.warn("No input found for test case by name {}", requirementName);
// Many mismatches
//						new IllegalStateException(
//								"Could not find input in model for test suite input parameter definition id="
//										+ parameterDefinition.getId() + ", requirementName=" + parameterDefinition.getRequirementName());
						
						return;
					}
					
					DefaultParameter defaultParameter = testCase.getInputParameters().get(i);
					Object inputValue = getParameterValue(defaultParameter);
					log.debug("Setting test case input name={}, value={}", requirementName, inputValue);
					unsetInputFactFields.remove(factField);
					
					if (inputValue instanceof Map) {
						onMapInputValue(inputFactType, inputBean, factField, (Map<String, Object>)inputValue);
					} else {
						onSingleInputValue(inputFactType, inputBean, factField, inputValue);
					}
				});
		
		
		if (!unsetInputFactFields.isEmpty()) {
			log.warn("Some input facts are not set facts={}", unsetInputFactFields);
		}
		
		kieSession.insert(inputBean);
		
		kieSession.fireAllRules();
		
		// retrieving execution results
		Comparable expectedOutput = getParameterValueComparator(testCase.getExpectedParameter());
		Object actualOutput = getOutput(output);
		
		kieSession.dispose();
		
		return (expectedOutput.compareTo(actualOutput) == 0) ? success(testCase) : failure(testCase);
	}
	
	
	private void onSingleInputValue(FactType inputFactType, Object inputBean, FactField factField, Object inputValue) {
		inputFactType.set(inputBean, factField.getName(), inputValue);
	}
	
	
	public void onMapInputValue(FactType inputFactType, Object inputBean, FactField factField, Map<String, Object> inputValue) {
		onSingleInputValue(inputFactType, inputBean, factField, inputValue);
	}
	
	
	public void toBeanPopulatingMap(Object source, Map<String, Object> target, String prefix) {
		if (source instanceof Map) {
			((Map<String, Object>) source)
					.forEach((k, v) -> toBeanPopulatingMap(v, target, prefix + "." + TestSuiteUtil.toCamelCase(k)));
		} else if (source instanceof List) {
			List l = (List) source;
			range(0, l.size()).forEach(i -> toBeanPopulatingMap(l.get(i), target, prefix + "[" + i + "]"));
		} else {
			target.put(prefix, source);
		}
	}
	
	
	@Override
	public void execute() {
		TestSuite testSuite = TestSuiteUtil.readTestSuite(testSuiteFileName);
		executeTestCases(testSuite).forEach(this::printAsJson);
	}
	
	
}
