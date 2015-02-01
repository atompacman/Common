package com.atompacman.nrstep;

import com.atompacman.toolkat.json.JSONSerializable;
import com.atompacman.toolkat.math.Equalizable;

public interface PatternElement<T extends PatternElement<T>> 
extends JSONSerializable, Equalizable<T> {

}
