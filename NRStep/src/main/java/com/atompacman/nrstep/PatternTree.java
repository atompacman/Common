package com.atompacman.nrstep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.atompacman.toolkat.exception.Throw;
import com.atompacman.toolkat.json.JSONDeserializationException;
import com.atompacman.toolkat.json.JSONSerializable;

public class PatternTree implements JSONSerializable {

    //===================================== INNER TYPES ==========================================\\

    private class Node {

        //===================================== FIELDS ===========================================\\

        private PatternElement<?> elem;
        private List<Node>        children;
        private Pattern           pat;



        //===================================== METHODS ==========================================\\

        //-------------------------------- PUBLIC CONSTRUCTOR ------------------------------------\\

        public Node(Sequence sequence) {
            this.children = new ArrayList<Node>();
            if (!sequence.isEmpty()) {
                this.elem = sequence.get(sequence.size() - 1);
                this.pat = new Pattern(sequence);
            }
        }


        //--------------------------------- ADD OCCURRENCE ---------------------------------------\\

        public void addOccurrences(Sequence sequence, List<Integer> startPos, 
                PatternTree subPat, int depth) {

            if (depth == sequence.size()) {
                if (pat.getNumAppearances() == 0) {
                    PatternTree.this.addPattern(pat);
                }
                pat.addOccurrences(startPos);
                pat.setSubPatterns(subPat);
            } else {
                PatternElement<?> elem = sequence.get(depth);
                Node next = null;

                for (Node child : children) {
                    if (PatternDetector.areEqual(child.elem, elem)) {
                        next = child;
                        break;
                    }
                }
                if (next == null) {
                    next = new Node(sequence.subSequence(0, depth + 1));
                    children.add(next);
                }
                next.addOccurrences(sequence, startPos, subPat, depth + 1);
            }
        }


        //------------------------------------ CONTAINS ------------------------------------------\\

        public boolean contains(Sequence sequence, int depth) {
            PatternElement<?> elem = sequence.get(depth);

            for (Node child : children) {
                if (PatternDetector.areEqual(child.elem, elem)) {
                    if (depth == sequence.size() - 1) {
                        return child.pat.getNumAppearances() != 0;
                    } else {
                        return child.contains(sequence, depth + 1);
                    }
                }
            }
            return false;
        }
    }



    //======================================= FIELDS =============================================\\

    private final Sequence        seq;
    private final int             seqLength;
    private final Node            root;
    private final List<Pattern>[] patterns;



    //======================================= METHODS ============================================\\

    //---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

    @SuppressWarnings("unchecked")
    PatternTree(Sequence sequence) {
        this.seq = sequence;
        this.seqLength = sequence.size();
        this.root = new Node(new Sequence());
        this.patterns = new List[seqLength / 2];
        for (int i = 0; i < seqLength / 2; ++i) {
            patterns[i] = new ArrayList<Pattern>();
        }
    }


    //-------------------------------- JSON STATIC CONSTRUCTOR -----------------------------------\\

    static PatternTree fromJSON(JSONObject obj, Class<? extends PatternElement<?>> elementClass) {
        PatternTree og = null;
        try {
            JSONArray array = (JSONArray) obj.get(Pattern.JSON_SEQUENCE_ATTRIBUTE);
            og = new PatternTree(Sequence.fromJSON(array, elementClass));
            array = (JSONArray) obj.get(Pattern.JSON_INNER_SEQUENCES_ATTRIBUTE);
            for (int i = 0; i < array.length(); ++i) {
                og.addPattern(Pattern.fromJSON(array.getJSONObject(i), elementClass));
            }
        } catch (Exception e) {
            JSONDeserializationException.causedBy(obj, PatternTree.class, e);
        }
        return og;
    }

    @SuppressWarnings("unchecked")
    static PatternTree fromJSON(JSONArray sub, Sequence seq) {
        PatternTree og = null;
        try {
            og = new PatternTree(seq);
            for (int i = 0; i < sub.length(); ++i) {
                og.addPattern(Pattern.fromJSON(sub.getJSONObject(i),
                        (Class<? extends PatternElement<?>>) seq.get(0).getClass()));
            }
        } catch (Exception e) {
            JSONDeserializationException.causedBy(sub, PatternTree.class, e);
        }
        return og;
    }


    //----------------------------------- ADD OCCURRENCE -----------------------------------------\\

    public void addOccurrence(Sequence sequence, int startPos) {
        addOccurrences(sequence, Arrays.asList(startPos), null);
    }

    public void addOccurrences(Sequence sequence, List<Integer> startPos) {
        addOccurrences(sequence, startPos, null);
    }

    public void addOccurrences(Sequence sequence, List<Integer> startPos, 
            PatternTree subPatterns) {

        try {
            if (sequence.isEmpty()) {
                Throw.aRuntime(NRStepException.class, "Cannot add an empty sequence");
            }
            for (int start : startPos) {
                if (start < 0) {
                    Throw.aRuntime(NRStepException.class, "Starting positions must be positive");
                }
                if (start > seqLength - sequence.size() + 1) {
                    Throw.aRuntime(NRStepException.class, "The end of a sequence "
                            + "exceeds end of the global sequence");
                }
            }
        } catch (NRStepException e) {
            Throw.aRuntime(NRStepException.class, "Could not add occurrences \"" + 
                    sequence.toString()	+ "\" (pos: " + startPos + ") to pattern tree", e);
        }

        root.addOccurrences(sequence, startPos, subPatterns, 0);
    }

    private void addPattern(Pattern pattern) {
        int seqLength = pattern.getSequence().size();
        patterns[seqLength - 1].add(pattern);
    }


    //----------------------------------- GET PATTERNS -------------------------------------------\\

    public List<Pattern> getAllPatterns() {
        List<Pattern> output = new ArrayList<Pattern>();
        for (List<Pattern> lengthPat : patterns) {
            output.addAll(lengthPat);
        }
        return output;
    }

    public List<List<Pattern>> getPatternsByLength() {
        return Arrays.asList(patterns);
    }

    public List<Pattern> getPatternOfLength(int length) {
        if (length > patterns.length + 1 || length <= 0) {
            return new ArrayList<Pattern>();
        } else {
            return patterns[length - 1];
        }
    }

    public Map<Integer, List<Pattern>> getPatternsByNumOccurrences() {
        Map<Integer, List<Pattern>> output = new HashMap<Integer, List<Pattern>>();
        for (List<Pattern> lengthPat : patterns) {
            for (Pattern pat : lengthPat) {
                int numApp = pat.getNumAppearances();
                List<Pattern> patList = output.get(numApp);
                if (patList == null) {
                    patList = new ArrayList<Pattern>();
                    output.put(numApp, patList);
                }
                patList.add(pat);
            }
        }
        return output;
    }

    public List<Pattern> getPatternsThatAppearedNTimes(int n) {
        List<Pattern> output = new ArrayList<Pattern>();
        for (List<Pattern> lengthPat : patterns) {
            for (Pattern pat : lengthPat) {
                if (pat.getNumAppearances() == n) {
                    output.add(pat);
                }
            }
        }
        return output;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    public int getGlobalSequenceLength() {
        return seqLength;
    }

    public boolean isEmpty() {
        for (List<Pattern> pat : patterns) {
            if (!pat.isEmpty()) {
                return false;
            }
        }
        return true;
    }


    //-------------------------------------- CONTAINS --------------------------------------------\\

    public boolean contains(Sequence sequence) {
        if (sequence.isEmpty() || sequence.size() > seqLength) {
            return false;
        } else {
            return root.contains(sequence, 0);
        }
    }


    //---------------------------------------- JSON ----------------------------------------------\\

    public JSONObject toJSON() {
        Map<String, Object> content = new HashMap<>();
        content.put(Pattern.JSON_SEQUENCE_ATTRIBUTE, seq.toJSON());
        List<JSONObject> pat = new ArrayList<>();
        for (Pattern pattern : getAllPatterns()) {
            pat.add(pattern.toJSON());
        }
        if (!pat.isEmpty()) {
            content.put(Pattern.JSON_INNER_SEQUENCES_ATTRIBUTE, pat);
        }
        return new JSONObject(content);
    }


    //------------------------------------- TO STRING --------------------------------------------\\

    public String toString() {
        return toJSON().toString();
    }
}