package com.signavio.examples.dmn;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.namespace.QName;

import com.signavio.bdm.testlab.exchange.DefaultParameter;
import com.signavio.bdm.testlab.exchange.ParameterDefinition;
import com.signavio.bdm.testlab.exchange.TestCase;
import com.signavio.bdm.testlab.exchange.TestSuite;
import com.signavio.examples.testsuite.TestResult;
import com.signavio.examples.testsuite.TestSuiteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.core.ast.InputDataNodeImpl;

import static com.signavio.examples.testsuite.TestResult.failure;
import static com.signavio.examples.testsuite.TestResult.success;
import static com.signavio.examples.testsuite.TestSuiteUtil.getOutputName;
import static com.signavio.examples.testsuite.TestSuiteUtil.getParameterValue;
import static com.signavio.examples.testsuite.TestSuiteUtil.getParameterValueComparator;
import static com.signavio.examples.testsuite.TestSuiteUtil.readTestSuite;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

@Slf4j
public class DmnTestableExample extends AbstractDmnExample {
	
	
	public static final QName QNAME_SHAPE_ID = new QName("http://www.signavio.com/schema/dmn/1.2/", "shapeId");
	
	private final String testSuiteFileName;
	private final String description;
	
	
	public DmnTestableExample(String knowledgeBaseId, String testSuiteFileName, String description) {
		super(knowledgeBaseId);
		this.testSuiteFileName = testSuiteFileName;
		this.description = description;
	}
	
	
	@Override
	public String getDescription() {
		return description;
	}
	
	
	public List<TestResult> executeTestCases(TestSuite testSuite, DMNModel model) {
		return testSuite.getTestCases().stream()
				.map(testCase -> executeTestCase(testSuite, model, testCase))
				.collect(toList());
	}
	
	
	public TestResult executeTestCase(TestSuite testSuite, DMNModel model, TestCase testCase) {
		
		DMNContext dmnContext = getDmnRuntime().newContext();
		
		Set<InputDataNode> unsetInputNodes = new HashSet<>(model.getInputs());
		
		range(0, testSuite.getInputParameterDefinitions().size())
				.forEach(i -> {
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
								return definitionShapeId.equals(shapeId); // match on shapeId
							})
							.findFirst()
							.orElseThrow(() -> new IllegalStateException(
									"Could not find input in model for test suite input parameter definition id="
											+ parameterDefinition.getId() + ", requirementName=" + parameterDefinition
											.getRequirementName()));
					
					DefaultParameter defaultParameter = testCase.getInputParameters().get(i);
					Object inputValue = getParameterValue(defaultParameter);
					unsetInputNodes.remove(matchingInputNode);
					dmnContext.set(matchingInputNode.getName(), inputValue);
				});
		
		
		if (!unsetInputNodes.isEmpty()) {
			log.warn("Some input nodes are not set nodes={}", unsetInputNodes);
		}
		
		DMNResult dmnResult = getDmnRuntime().evaluateAll(model, dmnContext);
		
		Object dmnOutputValue = getDecisionOutput(getOutputName(testSuite), dmnResult);
		
		return getParameterValueComparator(testCase.getExpectedParameter())
				.compareTo(dmnOutputValue) == 0 ? success(testCase) : failure(testCase);
	}
	
	
	public Object getDecisionOutput(String decisionName, DMNResult dmnResult) {
		List<DMNDecisionResult> decisionResults = dmnResult.getDecisionResults().stream()
				.filter(result -> decisionName.equals(result.getDecisionName()))
				.collect(Collectors.toList());
		
		return decisionResults.stream()
				.map(DMNDecisionResult::getResult)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}
	
	
	@Override
	public void execute() {
		// parsing model from .dmn
		DMNModel model = getDmnRuntime().getModels().get(0); // assuming there is only one model in the KieBase
		
		// parsing the testsuite
		TestSuite testSuite = readTestSuite(testSuiteFileName);
		
		// executing tests
		List<TestResult> testResults = executeTestCases(testSuite, model);
		testResults.forEach(this::printAsJson);
	}
}
