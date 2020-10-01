package com.signavio.examples.dmn.testsuite;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.signavio.bdm.testlab.exchange.TestSuite;

public class TestSuiteReader {
	
	public TestSuite parse(InputStream testSuite) {
		try {
			return new ObjectMapper()
					.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
					.readValue(testSuite, TestSuite.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
