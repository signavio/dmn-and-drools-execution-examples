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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;

@Slf4j
public class AdHocDrlExample extends DrlTestableExample {
	
	// Test case modified
	// _508 => newElement8
	// Rest of test inputs are null so it should not matter
	public AdHocDrlExample() {
		super("AdHocDrlKs", "com.signavio.examples.drl.adhoc", "adhoc_test_drl_mod.json", "Adhoc DRL");
	}
	
	
	public static void main(String[] args) {
		new AdHocDrlExample().execute();
	}
	
	
	@Override
	public void onMapInputValue(FactType inputFactType, Object inputBean, FactField factField,
			Map<String, Object> inputValue) {
		switch (factField.getName()) {
			case "newElement8":
				
				Map<String, Object> flattened = new HashMap<>();
				String memberName = factField.getName();
				
				// Experiment/Hack to get complex type set
				toBeanPopulatingMap(inputValue, flattened, memberName);
				try {
					Object element8Instance =
							ConstructorUtils.invokeConstructor(PropertyUtils.getPropertyType(inputBean, memberName));
					BeanUtils.setProperty(inputBean, memberName, element8Instance);
					
					Class<?> onboardingClientRequestedAccountProfileTechValueClass =
							inputBean.getClass().getClassLoader()
									.loadClass(
											"com.signavio.examples.drl.adhoc.OnboardingClientRequestedAccountProfileTechValue");
					
					ArrayList<Object> list = new ArrayList<>();
					list.add(ConstructorUtils.invokeConstructor(onboardingClientRequestedAccountProfileTechValueClass));
					list.add(ConstructorUtils.invokeConstructor(onboardingClientRequestedAccountProfileTechValueClass));
					BeanUtils.setProperty(element8Instance, "onboardingClientRequestedAccountProfileTechValue", list);
					BeanUtils.populate(inputBean, flattened);


//					list.add(new HashMap<>());
//					list.add(new HashMap<>());
					// Does not work because of the list...
//					inputFactType.set(inputBean, memberName, element8Instance);
//					inputFactType.setFromMap(element8Instance, inputValue);
				} catch (Exception e) {
					throw new IllegalArgumentException(
							"Could not set properties into input bean " + inputBean + ", props=" + flattened,
							e);
					
				}
				break;
			default:
				super.onMapInputValue(inputFactType, inputBean, factField, inputValue);
		}
	}
	
	
}
