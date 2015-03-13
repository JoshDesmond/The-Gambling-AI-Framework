package com.gmail.jdesmond10.pokerai.agent;

import com.gmail.jdesmond10.pokerai.game.logic.BettingAction;

/**
 * The Maniac goes all in no matter what. It is the most loose and most
 * aggressive player there is.
 * 
 * @author Josh Desmond
 */
public class TheManiac extends SimplePokerAgent {

	@Override
	public BettingAction prompt() {
		// Fold the trash
		if (getCurrentCard().value < 5)
			return checkFold();

		if (getCurrentCard().value < 9)
			return call();

		if (getCurrentCard().value > 11)
			return new BettingAction(getGameState().getMaxBetAmount());

		return betHigh();
	}

}
