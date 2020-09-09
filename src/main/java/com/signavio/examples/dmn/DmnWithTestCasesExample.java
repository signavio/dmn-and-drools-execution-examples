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

public class DmnWithTestCasesExample extends AbstractDmnExample {
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	
	
	@Override
	public void execute() {
		printTestSuite(readTestSuite());
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
	
}
