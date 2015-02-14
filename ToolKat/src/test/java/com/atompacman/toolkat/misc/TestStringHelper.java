package com.atompacman.toolkat.misc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestStringHelper {

	@Test
	public void testSplitCamelCase() {
		assertEquals("Salut Mon Gars", StringHelper.splitCamelCase("SalutMonGars"));
		assertEquals("Salut Mon Gars", StringHelper.splitCamelCase("SalutMonGars", false));
		assertEquals("I B M", StringHelper.splitCamelCase("IBM"));
		assertEquals("IBM", StringHelper.splitCamelCase("IBM", false));
	}
}
