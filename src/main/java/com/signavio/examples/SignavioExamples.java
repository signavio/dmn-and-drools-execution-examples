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

import com.signavio.examples.dmn.DmnSandbox;
import com.signavio.examples.dmn.DmnWithTestCasesExample;
import com.signavio.examples.dmn.OwnTypesDmnExample;
import com.signavio.examples.dmn.SimpleDmnExample;
import com.signavio.examples.dmn.TestExample;
import com.signavio.examples.drl.DrlSandbox;
import com.signavio.examples.drl.DrlWithTestCasesExample;
import com.signavio.examples.drl.DynamicSandboxDrlExample;
import com.signavio.examples.drl.OwnTypesDrlExample;
import com.signavio.examples.drl.SimpleDrlExample;

import static java.lang.System.out;

public class SignavioExamples {
	
	private static void executeDmnExamples() {
		out.println("=== DMN EXECUTION ===");
		executeExample(new SimpleDmnExample());
		executeExample(new DmnWithTestCasesExample());
		executeExample(new DmnSandbox());
		executeExample(new OwnTypesDmnExample());
	}
	
	
	private static void executeDroolsExamples() {
		out.println("=== DROOLS EXECUTION ===");
		executeExample(new SimpleDrlExample());
		executeExample(new OwnTypesDrlExample());
		executeExample(new DrlWithTestCasesExample());
		executeExample(new DynamicSandboxDrlExample());
		executeExample(new DrlSandbox());
	}
	
	
	private static void executeExample(AbstractSignavioExample example) {
		out.println("= " + example.getDescription() + " =");
		example.execute();
		out.println();
	}
	
	
	public static void main(String[] args) {
//		executeExample(new TestExample());

		executeDmnExamples();
		executeDroolsExamples();
	}
	
}
