package com.atompacman.configuana;

public interface Cmd<A extends App, F extends Flag> {
	
	//====================================== CONSTANTS ===========================================\\

	public static final int UNLIMITED_ARGS = -1;
	
	
	
	//======================================= METHODS ============================================\\

	//--------------------------------------- EXECUTE --------------------------------------------\\

	void execute(A app, CmdArgs<F> args);

	
	//--------------------------------------- GETTERS --------------------------------------------\\

	CmdInfo info();
}
