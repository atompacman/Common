package com.atompacman.nrstep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atompacman.toolkat.exception.Throw;
import com.atompacman.toolkat.misc.JSONUtils;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PatternTree<E> {

    //===================================== INNER TYPES ==========================================\\

    private class Node<T> {

        //===================================== FIELDS ===========================================\\

        private T             elem;
        private List<Node<T>> children;
        private Pattern<T>    pat;



        //===================================== METHODS ==========================================\\

        //-------------------------------- PUBLIC CONSTRUCTOR ------------------------------------\\

        public Node(Sequence<T> sequence) {
            this.children = new ArrayList<>();
            if (!sequence.isEmpty()) {
                this.elem = sequence.get(sequence.size() - 1);
                this.pat = new Pattern<>(sequence);
            }
        }


        //--------------------------------- ADD OCCURRENCE ---------------------------------------\\

        @SuppressWarnings("unchecked")
        public void addOccurrences(Sequence<T> sequence, List<Integer> startPos, 
                                   PatternTree<T> subPat, int depth) {

            if (depth == sequence.size()) {
                if (pat.numAppearances() == 0) {
                    PatternTree.this.addPattern((Pattern<E>) pat);
                }
                pat.addOccurrences(startPos);
                pat.setSubPatterns(subPat);
            } else {
                T elem = sequence.get(depth);
                Node<T> next = null;

                for (Node<T> child : children) {
                    if (child.elem.equals(elem)) {
                        next = child;
                        break;
                    }
                }
                if (next == null) {
                    next = new Node<>(sequence.subSequence(0, depth + 1));
                    children.add(next);
                }
                next.addOccurrences(sequence, startPos, subPat, depth + 1);
            }
        }


        //------------------------------------ CONTAINS ------------------------------------------\\

        public boolean contains(Sequence<T> sequence, int depth) {
            T elem = sequence.get(depth);

            for (Node<T> child : children) {
                if (child.elem.equals(elem)) {
                    if (depth == sequence.size() - 1) {
                        return child.pat.numAppearances() != 0;
                    } else {
                        return child.contains(sequence, depth + 1);
                    }
                }
            }
            return false;
        }
    }



    //======================================= FIELDS =============================================\\

    private final Sequence<E>        seq;
    private final int                seqLength;
    private final Node<E>            root;
    private final List<Pattern<E>>[] patterns;
    
    private int numPatterns;
    


    //======================================= METHODS ============================================\\

    //---------------------------------- PACKAGE CONSTRUCTOR -------------------------------------\\

    @SuppressWarnings("unchecked")
    PatternTree(Sequence<E> seq) {
        this.seq       = seq;
        this.seqLength = seq.size();
        this.root      = new Node<>(new Sequence<E>());
        this.patterns  = new List[seqLength / 2];
        
        for (int i = 0; i < seqLength / 2; ++i) {
            patterns[i] = new ArrayList<>();
        }
        
        this.numPatterns = 0;
    }
    

    //----------------------------------- ADD OCCURRENCE -----------------------------------------\\

    public void addOccurrence(Sequence<E> sequence, int startPos) {
        addOccurrences(sequence, Arrays.asList(startPos), null);
    }

    public void addOccurrences(Sequence<E> sequence, List<Integer> startPos) {
        addOccurrences(sequence, startPos, null);
    }

    public void addOccurrences(Sequence<E> sequence, List<Integer> startPos, 
                               PatternTree<E> subPatterns) {

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

    private void addPattern(Pattern<E> pattern) {
        ++numPatterns;
        patterns[pattern.getSequence().size() - 1].add(pattern);
    }


    //----------------------------------- GET PATTERNS -------------------------------------------\\

    @JsonIgnore
    public List<Pattern<E>> getAllPatterns() {
        List<Pattern<E>> output = new ArrayList<>();
        for (List<Pattern<E>> lengthPat : patterns) {
            output.addAll(lengthPat);
        }
        return output;
    }

    @JsonIgnore
    public List<List<Pattern<E>>> getPatternsByLength() {
        return Arrays.asList(patterns);
    }

    @JsonIgnore
    public List<Pattern<E>> getPatternOfLength(int length) {
        if (length > patterns.length + 1 || length <= 0) {
            return new ArrayList<Pattern<E>>();
        } else {
            return patterns[length - 1];
        }
    }

    @JsonIgnore
    public Map<Integer, List<Pattern<E>>> getPatternsByNumOccurrences() {
        Map<Integer, List<Pattern<E>>> output = new HashMap<>();
        for (List<Pattern<E>> lengthPat : patterns) {
            for (Pattern<E> pat : lengthPat) {
                int numApp = pat.numAppearances();
                List<Pattern<E>> patList = output.get(numApp);
                if (patList == null) {
                    patList = new ArrayList<>();
                    output.put(numApp, patList);
                }
                patList.add(pat);
            }
        }
        return output;
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    @JsonIgnore
    public Sequence<E> getSequence() {
        return seq;
    }
    
    @JsonIgnore
    public int getNumPatterns() {
        return numPatterns;
    }
    
    @JsonIgnore
    public int getGlobalSequenceLength() {
        return seqLength;
    }

    @JsonIgnore
    public boolean isEmpty() {
        for (List<Pattern<E>> pat : patterns) {
            if (!pat.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a Pattern wrapper around this PatternTree in order to have a correct JSON 
     * serialization.
     * 
     * @return A pojo for the JSON serializer
     */
    @JsonGetter("pattern_tree")
    private Pattern<E> getJSONPojo() {
        Pattern<E> pojo = new Pattern<>(seq);
        pojo.addOccurrences(Arrays.asList(0));
        pojo.setSubPatterns(this);
        return pojo;
    }

    
    //-------------------------------------- CONTAINS --------------------------------------------\\

    public boolean contains(Sequence<E> sequence) {
        if (sequence.isEmpty() || sequence.size() > seqLength) {
            return false;
        } else {
            return root.contains(sequence, 0);
        }
    }


    //------------------------------------- TO STRING --------------------------------------------\\

    public String toString() {
        return JSONUtils.toRobustJSONString(this);
    }
}