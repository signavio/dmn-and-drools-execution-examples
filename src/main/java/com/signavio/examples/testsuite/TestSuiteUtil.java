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
package com.signavio.examples.testsuite;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.signavio.bdm.testlab.exchange.DefaultParameter;
import com.signavio.bdm.testlab.exchange.ParameterDefinition;
import com.signavio.bdm.testlab.exchange.TestCase;
import com.signavio.bdm.testlab.exchange.TestSuite;
import com.signavio.bdm.testlab.exchange.types.BooleanParameter;
import com.signavio.bdm.testlab.exchange.types.ComplexParameter;
import com.signavio.bdm.testlab.exchange.types.ComplexSlotParameter;
import com.signavio.bdm.testlab.exchange.types.DateParameter;
import com.signavio.bdm.testlab.exchange.types.DateTimeParameter;
import com.signavio.bdm.testlab.exchange.types.EnumerationParameter;
import com.signavio.bdm.testlab.exchange.types.ListParameter;
import com.signavio.bdm.testlab.exchange.types.NumberParameter;
import com.signavio.bdm.testlab.exchange.types.TextParameter;
import com.signavio.bdm.testlab.exchange.types.TimeParameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.range;
import static org.apache.commons.lang3.text.WordUtils.capitalize;

@Slf4j
public class TestSuiteUtil {
	
	private TestSuiteUtil() {
	}
	
	
	public static List<String> getInputNames(TestSuite testSuite) {
		return testSuite.getInputParameterDefinitions().stream()
				.map(ParameterDefinition::getRequirementName)
				.map(TestSuiteUtil::toCamelCase)
				.collect(toList());
	}
	
	
	public static List<Pair<String, Object>> getInputs(TestCase testCase, List<String> orderedInputNames) {
		List<Object> inputValues = testCase.getInputParameters().stream()
				.map(TestSuiteUtil::getParameterValue)
				.collect(toList());
		
		return range(0, orderedInputNames.size())
				.mapToObj(index -> ImmutablePair.of(orderedInputNames.get(index), inputValues.get(index)))
				.collect(toList());
	}
	
	
	public static String getOutputName(TestSuite testSuite) {
		String requirementName = testSuite.getOutputParameterDefinition().getRequirementName();
		return toCamelCase(requirementName);
	}
	
	
	public static Object getParameterValue(DefaultParameter parameter) {
		if (parameter == null) {
			return null;
		}
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
			case LIST:
				return ((ListParameter) parameter).getElements().stream()
						.map(TestSuiteUtil::getParameterValue)
						.collect(toList());
			case COMPLEX:
				return ((ComplexParameter) parameter).getSlots().stream()
						.collect(toMap(ComplexSlotParameter::getName, s -> getParameterValue(s.getValue())));
			default:
				throw new RuntimeException("Not supported in this example: " + parameter.getType());
		}
	}
	
	
	public static Comparable getParameterValueComparator(DefaultParameter parameter) {
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
			
			case HIERARCHY:
				return o -> {
					log.error("Should implement HIERARCHY comparator");
					throw new IllegalStateException(
							"Should compare HIERARCHY: " + getParameterValue(parameter) + " with " + o);
				};
			
			case LIST:
				return o -> {
					log.error("Should implement LIST comparator");
					throw new IllegalStateException(
							"Should compare LIST: " + getParameterValue(parameter) + " with " + o);
				};
			
			case COMPLEX:
				return o -> {
					log.error("Should implement COMPLEX comparator");
					throw new IllegalStateException(
							"Should compare COMPLEX: " + getParameterValue(parameter) + " with " + o);
				};
			
			default:
				throw new RuntimeException("Not supported in this example: " + parameter.getType());
		}
	}
	
	
	public static TestSuite readTestSuite(String name) {
		InputStream testSuiteStream =
				TestSuiteUtil.class.getResourceAsStream(name);
		return new TestSuiteReader().parse(testSuiteStream);
	}
	
	
	public static String toCamelCase(String original) {
		return UPPER_CAMEL.to(
				LOWER_CAMEL,
				capitalize(original).replaceAll("\\s", "")
		);
	}
	
	
}
