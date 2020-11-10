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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.signavio.bdm.testlab.exchange.DefaultParameter;
import com.signavio.bdm.testlab.exchange.TestCase;

@JsonPropertyOrder({ "success", "testCase" })
public class TestResult {
	
	@JsonProperty
	private final TestCase testCase;
	
	@JsonProperty
	private final boolean success;
	
	
	public TestResult(TestCase testCase, boolean success) {
		this.testCase = testCase;
		this.success = success;
	}
	
	
	public static TestResult success(TestCase testCase) {
		return new TestResult(testCase, true);
	}
	
	
	public static TestResult failure(TestCase testCase) {
		return new TestResult(testCase, false);
	}
	
	
	public abstract static class TestCaseMixin {
		
		@JsonIgnore
		public abstract List<DefaultParameter> getAllExpectedParametersInternal();
		
	}
	
}
