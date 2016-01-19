package com.atompacman.nrstep;

import java.util.ArrayList;
import java.util.List;

import com.atompacman.toolkat.exception.Throw;
import com.atompacman.toolkat.misc.Log;

public class PatternDetector<T> {

    //======================================= FIELDS =============================================\\

    /** Processed sequence */
    private Sequence<T> seq;

    /** Processed sequence length */
    private int seqLen;

    /** Accepted patterns tree */
    private PatternTree<T> acceptedPatTree;

    /** Covered patterns tree */
    private PatternTree<T> coveredPatTree;

    /** Accepted patterns coverage */
    private boolean[] acceptedPatCoverage;

    /** Current minimum pattern length */
    private int minPatLen;



    //======================================= METHODS ============================================\\

    //--------------------------------------- DETECT ---------------------------------------------\\

    public PatternTree<T> detect(Sequence<T> sequence) {
        return detect(sequence, 1);
    }

    public PatternTree<T> detect(Sequence<T> sequence, int minPatternLength) {
        Log.info("Detecting patterns in sequence \"%s\".", sequence);

        seq                 = sequence;
        seqLen              = sequence.size();
        acceptedPatTree     = new PatternTree<>(seq);
        coveredPatTree      = new PatternTree<>(seq);
        acceptedPatCoverage = new boolean[seqLen];
        minPatLen           = minPatternLength;

        // Check that minimum pattern length is valid
        if (minPatLen < 1) {
            Throw.aRuntime(NRStepException.class, "Minimum pattern length must be positive");
        }
        if (minPatLen > seqLen / 2 && seqLen > 1) {
            Throw.aRuntime(NRStepException.class, "Minimum pattern length "
                    + "must not be longer than the half of the sequence");
        }

        // For every possible subsequence length in decreasing order
        for (int subSeqLen = seqLen / 2; subSeqLen >= minPatLen; --subSeqLen) {
            Log.debug("Looking for patterns of size %d. Patterns so far: "
                    + "%d.", subSeqLen, acceptedPatTree.getNumPatterns());
            // For every reference subsequence starting position
            for (int refSeqBeg = 0; refSeqBeg < seqLen - 2 * subSeqLen + 1; ++refSeqBeg) {
                detect(refSeqBeg, subSeqLen);
            }
        }

        Log.info("Done detecting patterns. %s in total.", acceptedPatTree.getNumPatterns());

        return acceptedPatTree;
    }

    private void detect(int refSeqBeg, int subSeqLen) {
        // Extract current reference subsequence
        Sequence<T> refSeq = seq.subSequence(refSeqBeg, refSeqBeg + subSeqLen);

        // Return if the pattern was already added to the covered pattern tree
        if (coveredPatTree.contains(refSeq)) {
            return;
        }
        
        // Add pattern to covered pattern tree
        coveredPatTree.addOccurrence(refSeq, 0);

        // Find subsequences matching reference subsequence
        List<Integer> matches = findMatches(refSeq, refSeqBeg);

        // Return if no match was found
        if (matches.isEmpty()) {
            return;
        }

        // Keep matches that are not linked by shorter patterns, example:
        //    [ABAB]AB[ABAB] -> not valid match
        //    [ABAB]BB[ABAB] -> valid match
        matches = keepValidMatches(matches, subSeqLen);

        // Return if no pair is valid
        if (matches.size() < 2) {
            return;
        }

        // Check if matches were all already covered (if they were all covered, then current pattern 
        // is a sub pattern of an existing one and is not added)
        boolean patternsAreAllCovered = true;
        for (int patBeg : matches) {
            for (int i = patBeg; i < patBeg + subSeqLen; ++i) {
                patternsAreAllCovered &= acceptedPatCoverage[i];
            }
            if (!patternsAreAllCovered) {
                break;
            }
        }
        if (patternsAreAllCovered) {
            return;
        }
        
        // Recursively detect patterns within current reference sub sequence
        PatternTree<T> subPatterns = new PatternDetector<T>().detect(refSeq, minPatLen);
        
        // Add current pattern and its sub patterns to the accepted pattern tree
        acceptedPatTree.addOccurrences(refSeq, matches, subPatterns);
        
        // Update accepted pattern coverage
        for (int matchBeg : matches) {
            for (int i = matchBeg; i < matchBeg + subSeqLen; ++i) {
                acceptedPatCoverage[i] = true;
            }
        }
    }

    /**
     * Finds subsequences matching a reference subsequence ({@link refSeq}) that starts at a certain 
     * position ({@link refSeqBeg}).
     * 
     * @param refSeq    Reference subsequence
     * @param refSeqBeg Starting position of reference subsequence
     * @return          List of starting positions of matching sequences, including the reference 
     */
    private List<Integer> findMatches(Sequence<T> refSeq, int refSeqBeg) {
        List<Integer> matches = new ArrayList<Integer>();
        int subSeqLen = refSeq.size();

        // Check for a matching pattern at each starting position after reference sequence's end 
        for (int cmpSeqBeg = refSeqBeg + subSeqLen; cmpSeqBeg < seqLen - subSeqLen + 1;++cmpSeqBeg){
            // Extract subsequence to compare
            Sequence<T> cmpSeq = seq.subSequence(cmpSeqBeg, cmpSeqBeg + subSeqLen);
            
            // Check if every elements of both sequence are equal
            if (refSeq.equals(cmpSeq)) {
                // Add the position of the match
                matches.add(cmpSeqBeg);
                // Jump directly to next possible subsequence start position
                cmpSeqBeg += subSeqLen - 1;
            }
        }

        // If there's at least one match, add reference sequence to the match list
        if (!matches.isEmpty()) {
            matches.add(0, refSeqBeg);
        }

        return matches;
    }

    private List<Integer> keepValidMatches(List<Integer> matches, int subSeqLen) {
        List<Integer> validMatches = new ArrayList<Integer>();

        // For every consecutive match pair
        for (int i = 0; i < matches.size() - 2; ++i) {
            Integer seqBegA = matches.get(i);
            Integer seqBegB = matches.get(i + 1);
            // Pair is valid if there is no shorter pattern linking them
            if (!areLinkedByAShorterPattern(seqBegA, seqBegB, subSeqLen)) {
                validMatches.add(seqBegA);
            }
        }
        // Same thing for the last pair
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
            Sequence<T> patSeq = seq.subSequence(seqBegA, seqBegA + patLen);
            int altSeqBeg = seqBegA + patLen;
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
}
