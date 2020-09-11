package com.signavio.examples;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class KieExecutionWrapper {
	
	public KieSessionWrapper drlFilePath(String drlFilePath) {
		try {
			return new KieSessionWrapper(newKieSession(new FileReader(drlFilePath)));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public KieSessionWrapper drlString(String drlString) {
		return new KieSessionWrapper(newKieSession(new StringReader(drlString)));
	}
	
	private KieSession newKieSession(Reader drlReader) {
		InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		knowledgeBuilder.add(ResourceFactory.newReaderResource(drlReader), ResourceType.DRL);
		knowledgeBase.addPackages(knowledgeBuilder.getKnowledgePackages());
		return knowledgeBase.newKieSession();
	}
	
	public static class KieSessionWrapper {
		
		private final KieSession kieSession;
		private final Map<String, Object> inputNamesToValues = new HashMap<>();
		
		
		private KieSessionWrapper(KieSession kieSession) {
			this.kieSession = kieSession;
		}
		
		public KieSessionWrapper insert(String inputName, Object value) {
			inputNamesToValues.put(inputName, value);
			return this;
		}
		
		public Collection<?> execute() {
			kieSession.insert(createInput());
			kieSession.fireAllRules();
			Collection<?> objects = kieSession.getObjects();
			// TODO dispose session?
			return objects;
		}
		
		public Object execute(String outputName) {
			kieSession.insert(createInput());
			kieSession.fireAllRules();
			FactType outputType = getOutputFactType(StringUtils.capitalize(outputName));
			Collection<?> objects = kieSession.getObjects(new ClassObjectFilter(outputType.getFactClass()));
			// TODO dispose session?
			return objects.stream()
					.map(object -> outputType.get(object, outputName))
					.findFirst().orElse(null);
		}
		
		private Object createInput() {
			try {
				// creating input object defined in the .drl file
				FactType inputType = getInputFactType();
				Object input = inputType.newInstance();
				
				// setting all given values to there respective fields
				inputNamesToValues.forEach((inputName, value) -> inputType.set(input, inputName, value));
				
				return input;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		
		private FactType getInputFactType() {
			return kieSession.getKieBase().getFactType(getPackageName(), "Input");
		}
		
		
		private FactType getOutputFactType(String outputName) {
			return kieSession.getKieBase().getFactType(getPackageName(), outputName + "_Output");
		}
		
		
		private String getPackageName() {
			String packageName = kieSession.getKieBase().getKiePackages().stream()
					// TODO use package as defined in signavio export
					.filter(kiePackage -> kiePackage.getName().contains("com.signavio.example"))
					.map(KiePackage::getName)
					.findFirst().orElse(null);
			return packageName;
		}
	}
}
