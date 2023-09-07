// SPDX-FileCopyrightText: 2023 SAP-Signavio
//
// SPDX-License-Identifier: Apache-2.0

package com.signavio.examples.testsuite;

import java.io.InputStream;
import java.util.List;

import com.signavio.bdm.testlab.exchange.DefaultParameter;
import com.signavio.bdm.testlab.exchange.ParameterDefinition;
import com.signavio.bdm.testlab.exchange.TestCase;
import com.signavio.bdm.testlab.exchange.TestSuite;
import com.signavio.bdm.testlab.exchange.types.BooleanParameter;
import com.signavio.bdm.testlab.exchange.types.DateParameter;
import com.signavio.bdm.testlab.exchange.types.DateTimeParameter;
import com.signavio.bdm.testlab.exchange.types.EnumerationParameter;
import com.signavio.bdm.testlab.exchange.types.NumberParameter;
import com.signavio.bdm.testlab.exchange.types.TextParameter;
import com.signavio.bdm.testlab.exchange.types.TimeParameter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

public class TestSuiteUtil {
	
	private TestSuiteUtil() {
	}
	
	
	public static TestSuite readTestSuite() {
		InputStream testSuiteStream =
				TestSuiteUtil.class.getResourceAsStream("Simple-TestLab.json");
		return new TestSuiteReader().parse(testSuiteStream);
	}
	
	
	public static List<String> getInputNames(TestSuite testSuite) {
		return testSuite.getInputParameterDefinitions().stream()
				.map(ParameterDefinition::getRequirementName)
				.map(TestSuiteUtil::toCamelCase)
				.collect(toList());
	}
	
	
	public static String getOutputName(TestSuite testSuite) {
		String requirementName = testSuite.getOutputParameterDefinition().getRequirementName();
		return toCamelCase(requirementName);
	}
	
	
	public static Comparable getParameterValue(DefaultParameter parameter) {
		switch (parameter.getType()) {
			case TEXT:
				return ((TextParameter) parameter).getValue();
			case NUMBER:
				return ((NumberParameter) parameter).getValue();
			case BOOLEAN:
				return ((BooleanParameter) parameter).getValue();
			case DATE:
				return ((DateParameter) parameter).getValue();
			case TIME:
				return ((TimeParameter) parameter).getValue();
			case DATETIME:
				return ((DateTimeParameter) parameter).getValue();
			case ENUMERATION:
				return ((EnumerationParameter) parameter).getItemName();
			default:
				throw new RuntimeException("Not supported in this example.");
		}
	}
	
	
	public static List<Pair<String, Object>> getInputs(TestCase testCase, List<String> orderedInputNames) {
		List<Object> inputValues = getInputValues(testCase);
		
		return range(0, orderedInputNames.size())
				.mapToObj(index -> ImmutablePair.of(orderedInputNames.get(index), inputValues.get(index)))
				.collect(toList());
	}
	
	
	private static List<Object> getInputValues(TestCase testCase) {
		return testCase.getInputParameters().stream()
				.map(TestSuiteUtil::getParameterValue)
				.collect(toList());
	}
	
	
	private static String toCamelCase(String original) {
		return UPPER_CAMEL.to(
				LOWER_CAMEL,
				capitalize(original).replaceAll("\\s", "")
		);
	}
	
}
