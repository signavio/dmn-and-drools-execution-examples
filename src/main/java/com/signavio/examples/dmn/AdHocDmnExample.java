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
package com.signavio.examples.dmn;

public class AdHocDmnExample extends DmnTestableExample {
	
	private static final String DESCRIPTION = "Adhoc Example";
	
	
	public AdHocDmnExample() {
		super("AdHocDmnKB", "adhoc_test.json", DESCRIPTION);
	}
	
	
	public static void main(String[] args) {
		new AdHocDmnExample().execute();
	}
	
	
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}
	
	
}
