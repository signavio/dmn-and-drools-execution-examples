package com.signavio.examples.dmn.testsuite;

import com.signavio.bdm.testlab.exchange.TestCase;

public class TestResult {
	
	private TestCase testCase;
	private boolean success;
	
	
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
	
	
	@Override
	public String toString() {
		return String.valueOf(success) + " -> " + testCase.toString();
	}
}
