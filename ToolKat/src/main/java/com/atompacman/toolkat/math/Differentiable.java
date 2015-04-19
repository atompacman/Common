package com.atompacman.toolkat.math;

public interface Differentiable<T extends Differentiable<T>> {

    Norm differenceWith(T other);
}