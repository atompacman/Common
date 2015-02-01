package com.atompacman.nrstep;

import java.util.ArrayList;
import java.util.List;

import com.atompacman.toolkat.exception.Throw;

public class PatternDetector {

	//===================================== INNER TYPES ==========================================\\

	private class Interval {
		public int beg;
		public int end;
	}

	
	
	//======================================= FIELDS =============================================\\

	/** Processed sequence */
	private Sequence seq;
	
	/** Processed sequence length */
	private int len;
	
	/** Accepted patterns tree */
	private PatternTree apt;
	
	/** Covered patterns tree */
	private PatternTree cpt;
	
	/** Accepted patterns intervals */
	private List<Interval> interv;

	/** Current minimum pattern length */
	private int minPatLen;
	
	
	
	//======================================= METHODS ============================================\\

	//--------------------------------------- DETECT ---------------------------------------------\\

	public PatternTree detect(Sequence sequence) {
		return detect(sequence, 1);
	}

	public PatternTree detect(Sequence sequence, int minPatternLength) {
		seq = sequence;
		len = sequence.size();
		apt = new PatternTree(seq);
		cpt = new PatternTree(seq);
		interv = new ArrayList<Interval>();
		minPatLen = minPatternLength;
		
		try {
			if (minPatternLength < 1) {
				Throw.aRuntime(NRStepException.class, "Must be positive");
			}
			if (minPatternLength > len / 2 && len > 1) {
				Throw.aRuntime(NRStepException.class, "Must not be "
						+ "longer than the half of the sequence");
			}
		} catch (NRStepException e) {
			Throw.aRuntime(NRStepException.class, "Invalid minimum pattern length", e);
		}

		for (int i = len / 2; i >= minPatLen; --i) {
			detect(i);
		}

		return apt;
	}

	private void detect(int subSeqLen) {
		for (int refSeqBeg = 0; refSeqBeg < len - 2 * subSeqLen + 1; ++refSeqBeg) {
			int refSeqEnd = refSeqBeg + subSeqLen;
			Sequence refSeq = seq.subSequence(refSeqBeg, refSeqEnd);

			if (cpt.contains(refSeq)) {
				continue;
			}
			cpt.addOccurrence(refSeq, 0);

			List<Integer> matches = findMatches(refSeq, refSeqEnd);

			if (matches.size() < 2) {
				continue;
			}

			matches = keepValidMatches(matches, subSeqLen);

			if (matches.size() < 2) {
				continue;
			}

			boolean patternsAreAllCovered = true;
			for (int patBeg : matches) {
				if (!isACoveredPattern(patBeg, patBeg + subSeqLen)) {
					patternsAreAllCovered = false;
					break;
				}
			}
			if (!patternsAreAllCovered) {
				addOccurances(refSeq, matches);
			}
		}
	}

	private List<Integer> findMatches(Sequence refSeq, int refSeqEnd) {
		List<Integer> matches = new ArrayList<Integer>();
		int subSeqLen = refSeq.size();

		for (int cmpSeqBeg = refSeqEnd; cmpSeqBeg < len - subSeqLen + 1; ++cmpSeqBeg) {
			Sequence cmpSeq = seq.subSequence(cmpSeqBeg, cmpSeqBeg + subSeqLen);
			if (areEqual(refSeq, cmpSeq)) {
				matches.add(cmpSeqBeg);
				cmpSeqBeg += subSeqLen - 1;
			}
		}

		if (!matches.isEmpty()) {
			matches.add(0, refSeqEnd - subSeqLen);
		}

		return matches;
	}

	private List<Integer> keepValidMatches(List<Integer> matches, int subSeqLen) {
		List<Integer> validMatches = new ArrayList<Integer>();

		for (int i = 0; i < matches.size() - 2; ++i) {
			Integer seqBegA = matches.get(i);
			Integer seqBegB = matches.get(i + 1);
			if (!areLinkedByAShorterPattern(seqBegA, seqBegB, subSeqLen)) {
				validMatches.add(seqBegA);
			}
		}
		Integer seqBegA = matches.get(matches.size() - 2);
		Integer seqBegB = matches.get(matches.size() - 1);
		if (!areLinkedByAShorterPattern(seqBegA, seqBegB, subSeqLen)) {
			validMatches.add(seqBegA);
			validMatches.add(seqBegB);
		}

		return validMatches;
	}

	private boolean areLinkedByAShorterPattern(int seqBegA, int seqBegB, int subSeqLen) {
		for (int patLen = 1; patLen < subSeqLen; ++patLen) {
			int patSeqEnd = seqBegA + patLen;
			Sequence patSeq = seq.subSequence(seqBegA, patSeqEnd);
			int altSeqBeg = patSeqEnd;
			Sequence altSeq = seq.subSequence(altSeqBeg, altSeqBeg + patLen);

			while (areEqual(patSeq, altSeq) && altSeqBeg < seqBegB) {
				altSeqBeg += patLen;
				altSeq = seq.subSequence(altSeqBeg, altSeqBeg + patLen);
			}
			if (altSeqBeg >= seqBegB) {
				return true;
			}
		}
		return false;
	}

	private boolean isACoveredPattern(int patBeg, int patEnd) {
		boolean[] covered = new boolean[patEnd - patBeg];
		for (Interval inter : interv) {
			if (inter.end <= patBeg || inter.beg >= patEnd) {
				continue;
			}
			for (int i = Math.max(inter.beg, patBeg); i < Math.min(inter.end, patEnd); ++i) {
				covered[i - patBeg] = true;
			}
			boolean allCovered = true;
			for (boolean b : covered) {
				if (!b) {
					allCovered = false;
					break;
				}
			}
			if (allCovered) {
				return true;
			}
		}
		return false;
	}

	private void addOccurances(Sequence subSeq,	List<Integer> matches) {
		apt.addOccurrences(subSeq, matches, new PatternDetector().detect(subSeq, minPatLen));
		int seqLen = subSeq.size();
		for (int matchBeg : matches) {
			Interval interval = new Interval();
			interval.beg = matchBeg;
			interval.end = matchBeg + seqLen;
			interv.add(interval);
		}
	}

	
	
	//==================================== STATIC METHODS ========================================\\

	//-------------------------------------- ARE EQUAL -------------------------------------------\\

	static boolean areEqual(Sequence a, Sequence b) {
		if (a.size() != b.size()) {
			throw new IllegalArgumentException("Cannot compare sub-sequences of different length.");
		}
		for (int i = 0; i < a.size(); ++i) {
			if (!areEqual(a.get(i), b.get(i))) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	static <T extends PatternElement<T>> boolean areEqual(PatternElement<?> a, PatternElement<?> b){
		return a.getClass() == b.getClass() && ((T) a).isEqualTo((T) b);
	}
}
