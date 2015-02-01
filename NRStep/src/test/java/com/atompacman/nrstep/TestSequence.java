package com.atompacman.nrstep;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

public class TestSequence {

	//================================== FUNCTIONNAL TESTS =======================================\\

	@Test
	public void assertCorrectJSONSerialization() {
		Sequence seq = new Sequence(Letter.valueOf("adadja"));
		assertEquals("[a, d, a, d, j, a]", seq.toJSON().toString());
		
		seq = new Sequence(Letter.valueOf(""));
		assertEquals("[]", seq.toJSON().toString());
	}
	
	@Test
	public void assertCorrectJSONDeserialization() throws JSONException {
		JSONArray array = new JSONArray("[a,d,a,d,j,a]");
		Sequence seq = Sequence.fromJSON(array, Letter.class);
		assertTrue(((Letter) seq.get(4)).isEqualTo(new Letter('j')));
	}
}
