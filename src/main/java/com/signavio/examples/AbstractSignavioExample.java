package com.signavio.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.signavio.bdm.testlab.exchange.TestCase;
import com.signavio.examples.dmn.testsuite.TestResult;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static java.lang.System.err;
import static java.lang.System.out;

public abstract class AbstractSignavioExample {
	
	private final ObjectMapper mapper = new ObjectMapper()
			.addMixIn(TestCase.class, TestResult.TestCaseMixin.class)
			.enable(INDENT_OUTPUT);
	
	
	public abstract void execute();
	
	public abstract String getDescription();
	
	
	protected void printAsJson(Object result) {
		try {
			out.println(mapper.writeValueAsString(result));
		} catch (JsonProcessingException e) {
			err.println(result);
		}
	}
	
}
