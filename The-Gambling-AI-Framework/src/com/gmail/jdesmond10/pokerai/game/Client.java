package com.gmail.jdesmond10.pokerai.game;

import com.gmail.jdesmond10.pokerai.agent.PokerAgent;
import com.gmail.jdesmond10.pokerai.game.logic.BettingAction;
import com.gmail.jdesmond10.pokerai.game.logic.GameStateData;
import com.gmail.jdesmond10.pokerai.main.SwingGUI;

/**
 * A client is a poker player.
 * 
 * @see Server
 * @see PokerAgent
 * @see SwingGUI
 * @author Josh
 */
public interface Client {

	/**
	 * Informs the Client of the updated state of the game. This will be called
	 * after every step in the game. This is primarily for the benefit of a GUI
	 * and user visibility of a game; that is, it is okay to do nothing but
	 * update the current state of the game. A human/GUI Agent will want to do
	 * something with this information, however.
	 * 
	 * @param currentState
	 */
	public void inform(GameStateData currentState);

	/**
	 * Calculates the next move of the Client. There is no server-regulated time
	 * limit on how long you are allowed to take, but for the sake of
	 * practicality, assume a time-limit of ~4-5 seconds.
	 * 
	 * @return the Action of this Client.
	 */
	public BettingAction prompt();

	/**
	 * In order to differentiate who you are playing against, and who the player
	 * is and the AI, this method will be used once at the start of a GUI/Server
	 * runtime to display the name.
	 * 
	 * @return A String representing the name of Client.
	 */
	public String getName();
}
