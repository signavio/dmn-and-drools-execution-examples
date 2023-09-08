// SPDX-FileCopyrightText: 2023 SAP-Signavio
//
// SPDX-License-Identifier: Apache-2.0

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
