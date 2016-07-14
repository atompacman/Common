package com.fxguild.common.pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.fxguild.common.JSONUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

public final class PatternTree<E> {

    //
    //  ~  NESTED TYPES  ~  //
    //

    private final class Node<T> {
        
        //
        //  ~  FIELDS  ~  //
        //

        private final T             elem;
        private final List<Node<T>> children;
        private       boolean       hasPattern;
        

        //
        //  ~  INIT  ~  //
        //
        
        public Node(T elem) {
            this.children   = new ArrayList<>();
            this.elem       = elem;
            this.hasPattern = false;
        }


        //
        //  ~  ADD PATTERN  ~  //
        //

        public void addPattern(Pattern<T> pattern, int depth) {
            T elem = pattern.getSequence().get(depth);

            Optional<Node<T>> opChild = children.stream().filter(
                    node -> node.elem.equals(elem)).findFirst();
            
            Node<T> child;
            
            if (opChild.isPresent()) {
                child = opChild.get();
            } else {
                child = new Node<>(elem);
                children.add(child);
            }
            
            if (depth == pattern.getSequence().size() - 1) {
                checkState(!child.hasPattern, "Pattern %s was already added to the tree", pattern);
                child.hasPattern = true;
            } else {
                child.addPattern(pattern, depth + 1);
            }
        }


        //
        //  ~  CONTAINS  ~  //
        //
        
        public boolean contains(ImmutableList<T> sequence, int depth) {
            Optional<Node<T>> opChild = children.stream().filter(node 
                    -> node.elem.equals(sequence.get(depth))).findFirst();
            
            if (opChild.isPresent()) {
                if (depth == sequence.size() - 1) {
                    return opChild.get().hasPattern;
                } else {
                    return opChild.get().contains(sequence, depth + 1);
                }
            } else {
                return false;
            }
        }
    }


    //
    //  ~  FIELDS  ~  //
    //

    private final ImmutableList<E>       seq;
    private final Node<E>                root;
    private final List<List<Pattern<E>>> patternsBySize;


    //
    //  ~  INIT  ~  //
    //

    PatternTree(ImmutableList<E> sequence) {
        checkArgument(!checkNotNull(sequence).isEmpty(), "Sequence must not be empty");

        this.seq            = sequence;
        this.root           = new Node<>(seq.get(0));
        this.patternsBySize = new ArrayList<>();
        
        for (int i = 0; i < seq.size() / 2; ++i) {
            patternsBySize.add(new LinkedList<>());
        }
    }
    

    //
    //  ~  ADD PATTERN  ~  //
    //

    void addPattern(Pattern<E> pattern) {
        root.addPattern(pattern, 0);
        patternsBySize.get(pattern.getSequence().size() - 1).add(pattern);
    }


    //
    //  ~  GETTERS  ~  //
    //

    public ImmutableList<E> getSequence() {
        return seq;
    }
    
    public ImmutableList<Pattern<E>> getAllPatterns() {
        List<Pattern<E>> output = new ArrayList<>();
        for (List<Pattern<E>> lengthPat : patternsBySize) {
            output.addAll(lengthPat);
        }
        return ImmutableList.copyOf(output);
    }

    public ImmutableList<ImmutableList<Pattern<E>>> getPatternsByLength() {
        List<ImmutableList<Pattern<E>>> output = new ArrayList<>();
        for (List<Pattern<E>> lengthPat : patternsBySize) {
            output.add(ImmutableList.copyOf(lengthPat));
        }
        return ImmutableList.copyOf(output);
    }

    public ImmutableList<Pattern<E>> getPatternOfLength(int length) {
        if (length > patternsBySize.size() || length <= 0) {
            return ImmutableList.of();
        } else {
            return ImmutableList.copyOf(patternsBySize.get(length - 1));
        }
    }

    public ImmutableListMultimap<Integer, Pattern<E>> getPatternsByNumOccurrences() {
        ListMultimap<Integer, Pattern<E>> output = ArrayListMultimap.create();
        for (List<Pattern<E>> lengthPat : patternsBySize) {
            for (Pattern<E> pat : lengthPat) {
                output.put(pat.numAppearances(), pat);
            }
        }
        return ImmutableListMultimap.copyOf(output);
    }

    
    //
    //  ~  STATE  ~  //
    //

    public int getNumPatterns() {
        int num = 0;
        for (List<Pattern<E>> lengthPat : patternsBySize) {
            num += lengthPat.size();
        }
        return num;
    }

    public boolean isEmpty() {
        return getNumPatterns() == 0;
    }

    public boolean contains(ImmutableList<E> sequence) {
        if (sequence.isEmpty() || sequence.size() > seq.size() / 2) {
            return false;
        } else {
            return root.contains(sequence, 0);
        }
    }


    //
    //  ~  TO STRING  ~  //
    //
    
    public String toString() {
        return JSONUtils.toQuietPrettyJSONString(Pattern.of(seq, new HashSet<>(), this));
    }
}