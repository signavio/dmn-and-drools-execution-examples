package com.signavio.examples;

import com.signavio.examples.dmn.SignavioDmnExample;
import com.signavio.examples.drl.SimpleDrlExample;
import com.signavio.examples.drl.OwnTypesDrlExample;

public class SignavioExamples {
	
	public static void main(String[] args) {
		System.out.println("\n\n=== DMN EXECUTION ===\n\n");
		new SignavioDmnExample().execute();
		
		System.out.println("\n\n=== DROOLS EXECUTION ===\n\n");
		new SimpleDrlExample().execute();
		
		System.out.println("\n\n=== DROOLS EXECUTION WITH OWN TYPE ===\n\n");
		new OwnTypesDrlExample().execute();
	}
	
	
}
