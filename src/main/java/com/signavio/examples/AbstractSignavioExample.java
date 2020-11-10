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
package com.signavio.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.signavio.bdm.testlab.exchange.TestCase;
import com.signavio.examples.testsuite.TestResult;

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
