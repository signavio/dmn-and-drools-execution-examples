package com.signavio.examples.dmn;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.signavio.bdm.testlab.exchange.TestSuite;
import org.json.JSONObject;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;

public class SignavioDmnExample {
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	
	public void execute() {
		// parsing model from .dmn
		DMNRuntime dmnRuntime = createDmnRuntime();
		DMNModel model = dmnRuntime.getModels().get(0); // assuming there is only one model in the KieBase
		
		// setting input data
		DMNContext dmnContext = createDmnContext(dmnRuntime);
		
		// executing decision logic
		DMNResult topLevelResult = dmnRuntime.evaluateAll(model, dmnContext);
		
		// retrieving execution results
		System.out.println("--- top level results ---");
		handleResult(topLevelResult);
		
		// retrieve intermediate results
		System.out.println("--- intermediate results ---");
		DMNResult lowerLevelResult = dmnRuntime.evaluateByName(model, dmnContext, "calculateDiscountBasedOnYears");
		handleResult(lowerLevelResult);
		
		
		// ############ execute testcases
		TestSuite testSuite = readTestSuite();
		printTestSuite(testSuite);
	}
	
	
	// TODO: TestSuite.class is part of the bdm-test-suite-api, clarify if Signavio is publishing it
	private TestSuite readTestSuite() {
		try {
			JsonReader reader = createTestSuiteReader();
			JsonObject testSuiteJson = GSON.fromJson(reader, JsonObject.class);
			
			// TODO: just for this we need a whole bunch of other dependencies
			return TestSuite.fromJson(new JSONObject(testSuiteJson.toString()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private void printTestSuite(TestSuite testSuite) {
		System.out.println(GSON.toJson(testSuite));
	}
	
	
	private JsonReader createTestSuiteReader() {
		InputStream testLabFile = this.getClass().getResourceAsStream("simple/Simple-TestLab.json");
		return new JsonReader(new InputStreamReader(testLabFile));
	}
	
	
	/**
	 * Handles the result to use the data collected during the dmn execution.
	 */
	private void handleResult(DMNResult decisionResult) {
		decisionResult.getDecisionResults().forEach(this::printResult);
		System.out.println();
	}
	
	
	private void printResult(DMNDecisionResult decisionResult) {
		System.out.println("Decision '" + decisionResult.getDecisionName() + "' : " + decisionResult.getResult());
	}
	
	
	/**
	 * Creates a DMNRuntime based on the configuration given in kmodule.xml.
	 * <p>
	 * The runtime contains information about all dmn models that where parsed from .dmn files and that are available
	 * for execution.
	 */
	private DMNRuntime createDmnRuntime() {
		KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
		return kieClasspathContainer
				.newKieSession("SignavioExampleDMNSimpleKS")
				.getKieRuntime(DMNRuntime.class);
	}
	
	
	/**
	 * Creates a new DmnContext that contains information about the input values that should be used during the
	 * execution of the dmn model.
	 */
	private DMNContext createDmnContext(DMNRuntime dmnRuntime) {
		DMNContext dmnContext = dmnRuntime.newContext();
		
		// setting values for inputs
		dmnContext.set("customerLevel", "Silver");
		dmnContext.set("customerYears", 15);
		
		return dmnContext;
	}
	
}
