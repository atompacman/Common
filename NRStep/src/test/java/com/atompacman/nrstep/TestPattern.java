package com.atompacman.nrstep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

public class TestPattern {

	//================================== FUNCTIONNAL TESTS =======================================\\

	@Test
	public void assertCorrectJSONSerialization() {
		Pattern occ = new Pattern(new Sequence(Letter.valueOf("seuilsuvbd")));
		occ.addOccurrences(Arrays.asList(1, 42, 252));
		assertEquals("{\"POS\":[1,42,252],\"SEQ\":[\"s\",\"e\",\"u\",\"i\",\"l\","
				+ "\"s\",\"u\",\"v\",\"b\",\"d\"]}", occ.toJSON().toString());
	}
	
	@Test
	public void assertCorrectJSONDeserialization() throws JSONException {
		JSONArray array = new JSONArray("[\"a\",\"d\",\"a\",\"d\",\"j\",\"a\"]");
		Sequence seq = Sequence.fromJSON(array, Letter.class);
		assertTrue(((Letter) seq.get(4)).isEqualTo(new Letter('j')));
	}
}
