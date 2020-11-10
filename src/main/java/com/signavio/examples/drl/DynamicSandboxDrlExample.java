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
package com.signavio.examples.drl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.signavio.examples.AbstractSignavioExample;
import com.signavio.examples.KieExecutionWrapper;
import org.apache.commons.io.IOUtils;

public class DynamicSandboxDrlExample extends AbstractSignavioExample {
	
	private static final String DESCRIPTION = "Example to construct knowledgebase dynamically";
	
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	@SuppressWarnings({ "unchecked", "ConstantConditions" })
	public void execute() {
		
		// passing a string
		String drlString = readDrlString();
		
		List<BigDecimal> result = (List<BigDecimal>) new KieExecutionWrapper()
				.drlString(drlString)
				.insert("aNumber", new BigDecimal(5))
				.execute("someCalculation");
		
		System.out.println("Calculation result (string): " + result);
		
		// passing a file path
		String drlFilePath =
				getClass().getClassLoader().getResource("com/signavio/examples/drl/dynamic/Sandbox.drl").getPath();
		
		result = (List<BigDecimal>) new KieExecutionWrapper()
				.drlFilePath(drlFilePath)
				.insert("aNumber", new BigDecimal(5))
				.execute("someCalculation");
		
		System.out.println("Calculation result (file path): " + result);
	}
	
	
	private String readDrlString() {
		try {
			return IOUtils
					.toString(getClass().getClassLoader().getResource("com/signavio/examples/drl/dynamic/Sandbox.drl"),
							StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
