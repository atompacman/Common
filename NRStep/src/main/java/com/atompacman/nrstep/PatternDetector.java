package com.atompacman.nrstep;

import java.util.ArrayList;
import java.util.List;

import com.atompacman.toolkat.exception.Throw;
import com.atompacman.toolkat.math.Interval;
import com.atompacman.toolkat.misc.Log;

public class PatternDetector<T> {

    //======================================= FIELDS =============================================\\

    /** Processed sequence */
    private Sequence<T> seq;

    /** Processed sequence length */
    private int len;

    /** Accepted patterns tree */
    private PatternTree<T> apt;

    /** Covered patterns tree */
    private PatternTree<T> cpt;

    /** Accepted patterns intervals */
    private List<Interval<Integer>> interv;

    /** Current minimum pattern length */
    private int minPatLen;



    //======================================= METHODS ============================================\\

    //--------------------------------------- DETECT ---------------------------------------------\\

    public PatternTree<T> detect(Sequence<T> sequence) {
        return detect(sequence, 1);
    }

    public PatternTree<T> detect(Sequence<T> sequence, int minPatternLength) {
        Log.info("Detecting patterns in sequence \"%s\".", sequence);

        seq = sequence;
        len = sequence.size();
        apt = new PatternTree<>(seq);
        cpt = new PatternTree<>(seq);
        interv = new ArrayList<Interval<Integer>>();
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
            Throw.aRuntime(NRStepException.class, "Invalid minimum pattern length.", e);
        }

        for (int i = len / 2; i >= minPatLen; --i) {
            Log.debug("Looking for patterns of size %d. Patterns so far: %d.", 
                    i, apt.getAllPatterns().size());
            detect(i);
        }

        Log.info("Done detecting patterns. %s in total.", apt.getAllPatterns().size());

        return apt;
    }

    private void detect(int subSeqLen) {
        for (int refSeqBeg = 0; refSeqBeg < len - 2 * subSeqLen + 1; ++refSeqBeg) {
            int refSeqEnd = refSeqBeg + subSeqLen;
            Sequence<T> refSeq = seq.subSequence(refSeqBeg, refSeqEnd);

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

    private List<Integer> findMatches(Sequence<T> refSeq, int refSeqEnd) {
        List<Integer> matches = new ArrayList<Integer>();
        int subSeqLen = refSeq.size();

        for (int cmpSeqBeg = refSeqEnd; cmpSeqBeg < len - subSeqLen + 1; ++cmpSeqBeg) {
            Sequence<T> cmpSeq = seq.subSequence(cmpSeqBeg, cmpSeqBeg + subSeqLen);
            if (refSeq.equals(cmpSeq)) {
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
            Sequence<T> patSeq = seq.subSequence(seqBegA, patSeqEnd);
            int altSeqBeg = patSeqEnd;
            Sequence<T> altSeq = seq.subSequence(altSeqBeg, altSeqBeg + patLen);

            while (patSeq.equals(altSeq) && altSeqBeg < seqBegB) {
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
        for (Interval<Integer> inter : interv) {
            if (inter.end() <= patBeg || inter.beg() >= patEnd) {
                continue;
            }
            for (int i = Math.max(inter.beg(), patBeg); i < Math.min(inter.end(), patEnd); ++i) {
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

    private void addOccurances(Sequence<T> subSeq,	List<Integer> matches) {
        apt.addOccurrences(subSeq, matches, new PatternDetector<T>().detect(subSeq, minPatLen));
        int seqLen = subSeq.size();
        for (int matchBeg : matches) {
            interv.add(new Interval<Integer>(matchBeg, matchBeg + seqLen));
        }
    }
}
