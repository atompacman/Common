package com.atompacman.toolkat.math;

public interface Differentiable<T extends Differentiable<T>> {

    UNorm differenceWith(T other);
}