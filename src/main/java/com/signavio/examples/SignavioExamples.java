package com.signavio.examples;

import com.signavio.examples.dmn.SignavioDmnExample;
import com.signavio.examples.drl.SignavioDrlExample;

public class SignavioExamples {
	
	public static void main(String[] args) {
		System.out.println("\n\n=== DMN EXECUTION ===\n\n");
		new SignavioDmnExample().execute();
		
		System.out.println("\n\n=== DROOLS EXECUTION ===\n\n");
		new SignavioDrlExample().execute();
	}
	
	
}
