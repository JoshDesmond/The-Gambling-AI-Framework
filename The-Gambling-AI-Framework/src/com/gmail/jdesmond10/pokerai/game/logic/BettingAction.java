package com.gmail.jdesmond10.pokerai.game.logic;

import com.gmail.jdesmond10.pokerai.main.Main;

/**
 * An action represents the action of a player. An action can be one of the four
 * things: Folding, Calling (Betting the minimum amount required to stay in the
 * hand), Raising (Betting more than the minimum amount required to stay in the
 * hand), or checking (betting 0).
 * 
 * @author Josh
 *
 */
public final class BettingAction {

	private final int amount;
	private static final int FOLD_BET = -2;
	public static final BettingAction FOLD = new BettingAction(FOLD_BET);

	/**
	 * Note: use BettingAction.FOLD to create a fold, don't bet -1.
	 * 
	 * @param betAmount
	 */
	public BettingAction(final int betAmount) {
		// Validate betAmount
		if (betAmount == -1) {
			Main.fail("Illegal Bet Amount, " + betAmount);
		}

		amount = betAmount;
	}

	/**
	 * 
	 * @return True if the bettingAction was a fold.
	 */
	public final boolean isFold() {
		return (amount == FOLD_BET);
	}

	/**
	 * 
	 * @return True if the bettingAction is a check (a bet of 0)
	 */
	public final boolean isCheck() {
		return (amount == 0);
	}

	/**
	 * Note: You probably shouldn't be calling this until you know the
	 * BettingAction is not a fold. Check if it is a fold via isFold first.
	 * 
	 * @return the amount bet
	 */
	public int getAmount() {
		// Validate status of BettingAction.
		if (amount == FOLD_BET) {
			Main.systemPrint("You are asking for the amount"
					+ " bet of a fold. Try using the method isFold()"
					+ " to handle folds, and then dealing with the "
					+ "amount bet.");
		}

		return amount;
	}

	@Override
	public final String toString() {
		if (isFold())
			return "folded";
		else
			return "bet " + amount + " chips";
	}
}
