package com.atompacman.configuana;

public class CmdInfo {
	
	//======================================= FIELDS =============================================\\

	private String 		 consoleName;
	private String 		 fullName;
	private int	   		 numMainArgs;
	private String 		 description;
	
	
	
	//======================================= METHODS ============================================\\

	//---------------------------------- PUBLIC CONSTRUCTOR --------------------------------------\\

	public CmdInfo(String consoleName, 
					 String fullName, 
					 int numMainArgs, 
					 String description) {
		
		this.consoleName = consoleName;
		this.fullName = fullName;
		this.description = description;
		this.numMainArgs = numMainArgs;
	}

	
	//--------------------------------------- GETTERS --------------------------------------------\\

	String consoleName() { 
		return consoleName; 	
	}
	
	String fullName() { 
		return fullName;		
	}
	
	int numMainArgs() { 
		return numMainArgs;		
	}
	
	String description() { 
		return description; 	
	}
}