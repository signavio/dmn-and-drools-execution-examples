// SPDX-FileCopyrightText: 2023 SAP-Signavio
//
// SPDX-License-Identifier: Apache-2.0

package com.signavio.examples.testsuite;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.signavio.bdm.testlab.exchange.TestSuite;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

public class TestSuiteReader {
	
	public TestSuite parse(InputStream testSuite) {
		try {
			return new ObjectMapper()
					.setVisibility(FIELD, ANY)
					.readValue(testSuite, TestSuite.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
