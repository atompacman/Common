package com.atompacman.nrstep;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.atompacman.toolkat.exception.Throw;
import com.atompacman.toolkat.json.JSONDeserializationException;
import com.atompacman.toolkat.json.JSONSerializable;

public class Pattern implements JSONSerializable {

	//====================================== CONSTANTS ===========================================\\

	static final String JSON_SEQUENCE_ATTRIBUTE 		= "SEQ";
	static final String JSON_START_POSITIONS_ATTRIBUTE 	= "POS";
	static final String JSON_INNER_SEQUENCES_ATTRIBUTE 	= "SUB";



	//======================================= FIELDS =============================================\\

	private Sequence 		seq;
	private Set<Integer> 	startPos;
	private PatternTree 	subPat;



	//======================================= METHODS ============================================\\

	//---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

	Pattern(Sequence seq) {
		if (seq.isEmpty()) {
			Throw.aRuntime(NRStepException.class, "Cannot create a pattern from an empty sequence");
		}
		this.seq = seq;
		this.startPos = new LinkedHashSet<Integer>();
	}


	//-------------------------------- JSON STATIC CONSTRUCTOR -----------------------------------\\

	static Pattern fromJSON(JSONObject obj, Class<? extends PatternElement<?>> elementClass) {
		Pattern occ = null;
		try {
			JSONArray array = (JSONArray) obj.get(JSON_SEQUENCE_ATTRIBUTE);
			Sequence seq = Sequence.fromJSON(array, elementClass);
			occ = new Pattern(seq);
			array = (JSONArray) obj.get(JSON_START_POSITIONS_ATTRIBUTE);
			List<Integer> startPos = new ArrayList<>();
			for (int i = 0; i < array.length(); ++i) {
				startPos.add(array.getInt(i));
			}
			occ.addOccurrences(startPos);
			if (obj.has(JSON_INNER_SEQUENCES_ATTRIBUTE)) {
				JSONArray iog = (JSONArray) obj.get(JSON_INNER_SEQUENCES_ATTRIBUTE);
				occ.setSubPatterns(PatternTree.fromJSON(iog, seq));
			}
		} catch (Exception e) {
			JSONDeserializationException.causedBy(obj, Pattern.class, e);
		}
		return occ;
	}


	//------------------------------------ ADD OCCURRENCE ----------------------------------------\\

	void addOccurrences(List<Integer> startPos) {
		for (Integer start : startPos) {
			if (!this.startPos.add(start)) {
				Throw.aRuntime(NRStepException.class, "The occurrence of "
						+ "the pattern \"" + seq.toString() + "\" at position "
						+ "\"" + startPos + "\" has already been counted.");
			}	
		}
	}


	//--------------------------------- SET SUB OCCURRENCES --------------------------------------\\

	public void setSubPatterns(PatternTree subPat) {
		this.subPat = subPat;
	}


	//--------------------------------------- GETTERS --------------------------------------------\\

	public Sequence getSequence() {
		return seq;
	}

	public Set<Integer> getStartingPositions() {
		return startPos;
	}

	public PatternTree getSubPatterns() {
		return subPat;
	}

	public int getNumAppearances() {
		return startPos.size();
	}

	
	//---------------------------------------- JSON ----------------------------------------------\\

	public JSONObject toJSON() {
		JSONObject obj = new JSONObject();
		try {
			obj.put(JSON_SEQUENCE_ATTRIBUTE, seq.toJSON());
			obj.put(JSON_START_POSITIONS_ATTRIBUTE, startPos);
			if (subPat != null && !subPat.isEmpty()) {
				JSONArray array = new JSONArray();
				for (Pattern occ : subPat.getAllPatterns()) {
					array.put(occ.toJSON());
				}
				obj.put(JSON_INNER_SEQUENCES_ATTRIBUTE, array);
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return obj;
	}


	//-------------------------------------- TO STRING -------------------------------------------\\

	public String toString() {
		return toJSON().toString();
	}
}
