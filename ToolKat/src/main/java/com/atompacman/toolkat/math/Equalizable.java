package com.atompacman.toolkat.math;

public interface Equalizable<T extends Equalizable<T>> {

    boolean isEqualTo(T other);
}
