package com.atompacman.configuana;

import java.util.Arrays;
import java.util.List;

public class FlagInfo {

    //======================================= FIELDS =============================================\\

    private String       consoleName;
    private String       fullName;
    private int          numArgs;
    private String       description;
    private List<String> defaultValues;



    //======================================= METHODS ============================================\\

    //---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

    public FlagInfo(String    consoleName, 
                    String    fullName, 
                    int       numArgs, 
                    String    description, 
                    String... defaultValues) {

        this.consoleName   = consoleName;
        this.fullName      = fullName;
        this.description   = description;
        this.numArgs       = numArgs;
        this.defaultValues = Arrays.asList(defaultValues);
    }


    //--------------------------------------- GETTERS --------------------------------------------\\

    String consoleName() {
        return consoleName;
    }

    String fullName() {
        return fullName;
    }

    int numArgs() {
        return numArgs;
    }

    String description() {
        return description;
    }

    List<String> defaultValues() {
        return defaultValues;
    }
}