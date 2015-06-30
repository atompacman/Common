package com.atompacman.toolkat.misc;

import java.util.Collection;

public class Collections2 {

    public static int sum(Collection<Integer> vector) {
        int sum = 0;
        for (int component : vector) {
            sum += component;
        }
        return sum;
    }
}
