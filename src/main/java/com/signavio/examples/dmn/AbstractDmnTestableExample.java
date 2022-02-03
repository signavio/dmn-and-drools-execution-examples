package com.signavio.examples.dmn;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import com.signavio.bdm.testlab.exchange.DefaultParameter;
import com.signavio.bdm.testlab.exchange.ParameterDefinition;
import com.signavio.bdm.testlab.exchange.TestCase;
import com.signavio.bdm.testlab.exchange.TestSuite;
import com.signavio.examples.testsuite.TestResult;
import com.signavio.examples.testsuite.TestSuiteUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.InputDataNodeImpl;

import static com.github.javaparser.utils.Utils.toCamelCase;
import static com.signavio.examples.testsuite.TestResult.failure;
import static com.signavio.examples.testsuite.TestResult.success;
import static com.signavio.examples.testsuite.TestSuiteUtil.getInputNames;
import static com.signavio.examples.testsuite.TestSuiteUtil.getOutputName;
import static com.signavio.examples.testsuite.TestSuiteUtil.getParameterValue;
import static com.signavio.examples.testsuite.TestSuiteUtil.getParameterValueComparator;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public abstract class AbstractDmnTestableExample extends AbstractDmnExample {
	
	
	public static final QName QNAME_SHAPE_ID = new QName("http://www.signavio.com/schema/dmn/1.2/", "shapeId");
	
	
	public AbstractDmnTestableExample(String knowledgeBaseId) {
		super(knowledgeBaseId);
	}
	
	
	public List<TestResult> executeTestCases(TestSuite testSuite, DMNModel model) {
		String output = getOutputName(testSuite);
		List<String> inputs = getInputNames(testSuite);
		
		return testSuite.getTestCases().stream()
				.map(testCase -> executeTestCase(testCase, inputs, output, model, testSuite))
				.collect(toList());
	}
	
	public TestResult executeTestCase(TestCase testCase, List<String> inputsNOUSE, String output, DMNModel model,
			TestSuite testSuite) {
		
		List<Pair<InputDataNode, Object>> inputMapping = range(0, testSuite.getInputParameterDefinitions().size())
				.mapToObj(i -> {
					ParameterDefinition parameterDefinition = testSuite.getInputParameterDefinitions().get(i);
					// Should match on both
					String definitionDiagramId = StringUtils.substringBefore(parameterDefinition.getId(), "/");
					String definitionShapeId = StringUtils.substringAfter(parameterDefinition.getId(), "/");
					String requirementName = TestSuiteUtil.toCamelCase(parameterDefinition.getRequirementName());

					InputDataNode matchingInputNode = model.getInputs()
							.stream()
							.map(n -> (InputDataNodeImpl) n)
							.filter(n -> {
								String name = n.getName();
								if (requirementName.equals(TestSuiteUtil.toCamelCase(name))) {
									return true; // match on name
								}
								
//								String diagramId = n.getInputData().getAdditionalAttributes()
//										.get("{http://www.signavio.com/schema/dmn/1.2/}diagramId");
								String shapeId = n.getInputData().getAdditionalAttributes()
										.get(QNAME_SHAPE_ID);
								if (definitionShapeId.equals(shapeId)) {
									return true; // match on shapeId
								}
								
								return false;
							})
							.findFirst()
							.orElseThrow(() -> new IllegalStateException(
									"Could not find input in model for test suite input parameter definition id="
											+ parameterDefinition.getId() + ", requirementName="+ parameterDefinition.getRequirementName()));
					
					DefaultParameter defaultParameter = testCase.getInputParameters().get(i);
					Object inputValue = getParameterValue(defaultParameter);
					return Pair.of(matchingInputNode, inputValue);
				})
				.collect(toList());
		
		DMNContext dmnContext = getDmnRuntime().newContext();
		
		inputMapping.forEach(p -> dmnContext.set(p.getKey().getName(), p.getValue()));
		
		DMNResult dmnResult = getDmnRuntime().evaluateAll(model, dmnContext);
		
		Object actualOutput = getDecisionOutput(output, dmnResult);
		
		Comparable expectedOutput = getParameterValueComparator(testCase.getExpectedParameter());
		return (expectedOutput.compareTo(actualOutput) == 0) ? success(testCase) : failure(testCase);
	}
	
	
	public Object getDecisionOutput(String output, DMNResult dmnResult) {
		List<DMNDecisionResult> decisionResults = dmnResult.getDecisionResults().stream()
				.filter(result -> output.equals(result.getDecisionName()))
				.collect(Collectors.toList());

		
		
		return decisionResults.stream()
				.map(DMNDecisionResult::getResult)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}
	
	
}
