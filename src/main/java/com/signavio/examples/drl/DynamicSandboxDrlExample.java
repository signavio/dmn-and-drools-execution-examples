// SPDX-FileCopyrightText: 2023 SAP-Signavio
//
// SPDX-License-Identifier: Apache-2.0

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
