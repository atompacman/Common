package com.atompacman.toolkat.misc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestStringHelper {

	@Test
	public void testSplitCamelCase() {
		assertEquals("Salut Mon Gars", StringHelper.splitCamelCase("SalutMonGars"));
		assertEquals("I B M", StringHelper.splitCamelCase("IBM"));
		assertEquals("I B M", StringHelper.splitCamelCase("IBM", 1));
		assertEquals("IBM", StringHelper.splitCamelCase("IBM", 2));
		assertEquals("Ayo Yo Yo", StringHelper.splitCamelCase("AyoYoYo", 2));
	}
}
