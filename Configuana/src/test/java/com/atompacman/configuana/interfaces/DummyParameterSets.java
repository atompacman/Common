package com.atompacman.configuana.interfaces;

import com.atompacman.configuana.Param;
import com.atompacman.configuana.StrictParam;

public class DummyParameterSets {

    //------------------------------- ERRONEOUS PARAMETER SET ------------------------------------\\

    public enum NotAParameterSet {
        SALUT,
        SALOT;
    }

    public enum ImplementsMultipleParameterSetClasses 
    implements Param, StrictParam {
        ITEM_1 ("123",   false, false),
        ITEM_2 ("5.98", true,  true);

        private Object defaultValue;
        private boolean isMandatory;
        private boolean isModifiable;
        private ImplementsMultipleParameterSetClasses(Object defaultValue, 
                boolean isMandatory, boolean isModifiable) { 
            this.defaultValue = defaultValue; 
            this.isMandatory = isMandatory; 
            this.isModifiable = isModifiable; }
        public Object defaultValue() { return defaultValue;	}
        public boolean isMandatory() { return isMandatory; }
        public boolean isModifiable() {	return isModifiable; }
    }

    public class NotAnEnum implements StrictParam {

    }


    //-------------------------------- CORRECT PARAMETER SET -------------------------------------\\

    public enum A_S_L$L_L_A$$A$ implements Param {
        ;

        public enum S_L$ implements StrictParam {
            ITEM_1,
            ITEM_2;

            public enum L implements Param {
                ITEM_1 ("Salut"),
                ITEM_2 ("seg");

                private Object defaultValue;
                private L(Object defaultValue) { this.defaultValue = defaultValue; }
                public Object defaultValue() { return defaultValue;	}
            }
        }

        public enum L_L_A$$ implements Param {
            ITEM_1 ("335"),
            ITEM_2 ("5.3");

            public enum L_A$ implements Param {
                ITEM_1 ("wfsef"),
                ITEM_2 ("aaaaa");

                public enum A implements Param {
                    ITEM_1 ("123",  false, false),
                    ITEM_2 ("5.98", true,  true);

                    private Object defaultValue;
                    private boolean isMandatory;
                    private boolean isModifiable;
                    private A(Object defaultValue, boolean isMandatory, boolean isModifiable) { 
                        this.defaultValue = defaultValue; 
                        this.isMandatory = isMandatory; 
                        this.isModifiable = isModifiable; }
                    public Object defaultValue() { return defaultValue;	}
                    public boolean isMandatory() { return isMandatory; }
                    public boolean isModifiable() {	return isModifiable; }
                }

                private Object defaultValue;
                private L_A$(Object defaultValue) { this.defaultValue = defaultValue; }
                public Object defaultValue() { return defaultValue;	}
            }

            private Object defaultValue;
            private L_L_A$$(Object defaultValue) { this.defaultValue = defaultValue; }
            public Object defaultValue() { return defaultValue;	}
        }

        public enum A implements Param {
            ITEM_1 ("false", false, false),
            ITEM_2 ("2", 	 true,  true);

            private Object defaultValue;
            private boolean isMandatory;
            private boolean isModifiable;
            private A(Object defaultValue, boolean isMandatory, boolean isModifiable) { 
                this.defaultValue = defaultValue; 
                this.isMandatory = isMandatory; 
                this.isModifiable = isModifiable; }
            public Object defaultValue() { return defaultValue;	}
            public boolean isMandatory() { return isMandatory; }
            public boolean isModifiable() {	return isModifiable; }
        }

        public Object defaultValue() {
            return null;
        }

        public boolean isMandatory() {
            return false;
        }

        public boolean isModifiable() {
            return false;
        }
    }
}
