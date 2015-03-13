package com.gmail.jdesmond10.pokerai.agent;

import com.gmail.jdesmond10.pokerai.game.logic.BettingAction;

/**
 * The Fish never raises, but always try to see cards. The Fish is the most
 * passive type of player there is.
 *
 * @author Josh Desmond
 */
public class TheFish extends SimplePokerAgent {
	@Override
	public BettingAction prompt() {
		if (getCurrentCard().value > 12)
			return call();
		else if (getCurrentCard().value > 8) {
			if (getGameState().getCallAmount() < 20)
				return call();
			else
				return BettingAction.FOLD;
		} else if (getCurrentCard().value > 4) {
			if (getGameState().getCallAmount() < 3)
				return call();
			else
				return BettingAction.FOLD;
		} else
			return checkFold();
	}
}
