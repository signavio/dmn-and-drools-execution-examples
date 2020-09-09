package com.signavio.examples.owntypes;

import java.math.BigDecimal;

public class CustomerData {
	
	private final String level;
	private final BigDecimal years;
	
	
	public CustomerData(String level, BigDecimal years) {
		this.level = level;
		this.years = years;
	}
	
	
	public String getLevel() {
		return level;
	}
	
	
	public BigDecimal getYears() {
		return years;
	}
}
