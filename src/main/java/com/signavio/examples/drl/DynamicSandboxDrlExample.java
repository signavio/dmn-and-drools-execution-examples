package com.signavio.examples.drl;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.signavio.examples.KieExecutionWrapper;
import org.apache.commons.io.IOUtils;

public class DynamicSandboxDrlExample {
	
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	public void execute() throws Exception {
		
		// passing a string
		String drlString = IOUtils.toString(getClass().getClassLoader().getResource("Sandbox.drl"), StandardCharsets.UTF_8);
		
		List<BigDecimal> result = (List<BigDecimal>) new KieExecutionWrapper()
				.drlString(drlString)
				.insert("aNumber", new BigDecimal(5))
				.execute("someCalculation");
		
		System.out.println("Calculation result (string): " + result);
		
		// passing a file path
		String drlFilePath = getClass().getClassLoader().getResource("Sandbox.drl").getPath();
		
		result = (List<BigDecimal>) new KieExecutionWrapper()
				.drlFilePath(drlFilePath)
				.insert("aNumber", new BigDecimal(5))
				.execute("someCalculation");
		
		System.out.println("Calculation result (file path): " + result);
	}
}
