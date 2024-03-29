// SPDX-FileCopyrightText: 2023 SAP-Signavio
//
// SPDX-License-Identifier: Apache-2.0

package com.signavio.examples.dmn;

import java.util.Collection;

import com.signavio.examples.AbstractSignavioExample;
import org.apache.commons.lang3.tuple.Pair;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNRuntime;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public abstract class AbstractDmnExample extends AbstractSignavioExample {
	
	private DMNRuntime dmnRuntime;
	
	
	public AbstractDmnExample(String knowledgeBaseId) {
		dmnRuntime = createDmnRuntime(knowledgeBaseId);
	}
	
	
	/**
	 * Creates a DMNRuntime based on the configuration given in kmodule.xml.
	 * <p>
	 * The runtime contains information about all dmn models that where parsed from .dmn files and that are available
	 * for execution.
	 */
	private DMNRuntime createDmnRuntime(String knowledgeBaseId) {
		KieContainer kieClasspathContainer = KieServices.Factory.get().getKieClasspathContainer();
		return kieClasspathContainer
				.getKieBase(knowledgeBaseId)
				.newKieSession()
				.getKieRuntime(DMNRuntime.class);
	}
	
	
	protected DMNRuntime getDmnRuntime() {
		return dmnRuntime;
	}
	
	
	/**
	 * Creates a new DmnContext that contains information about the input values that should be used during the
	 * execution of the dmn model.
	 */
	protected DMNContext createDmnContext(Pair<String, Object>... inputs) {
		return createDmnContext(stream(inputs).collect(toList()));
	}
	
	
	/**
	 * Creates a new DmnContext that contains information about the input values that should be used during the
	 * execution of the dmn model.
	 */
	protected DMNContext createDmnContext(Collection<Pair<String, Object>> inputs) {
		DMNContext dmnContext = getDmnRuntime().newContext();
		
		// setting values for inputs
		inputs.forEach(inputValue -> dmnContext.set(inputValue.getKey(), inputValue.getValue()));
		
		return dmnContext;
	}
	
}
