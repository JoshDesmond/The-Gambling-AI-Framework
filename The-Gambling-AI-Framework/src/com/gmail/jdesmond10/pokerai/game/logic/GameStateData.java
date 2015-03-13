package com.gmail.jdesmond10.pokerai.game.logic;

import com.gmail.jdesmond10.pokerai.main.Main;

/**
 * An object representing the state of a poker game. For limited view game
 * states, the value of playerOnecard (or playerTwoCard) is kept as Card.Unkown.
 * 
 * @author Josh
 *
 */
public final class GameStateData {

	/**
	 * Simple nested enum for use in passing parameters between endHand() and
	 * determine Winner.
	 * 
	 * @author Josh
	 */
	private enum Winner {
		playerOne, playerTwo, tie
	}

	/**
	 * Generates an initial GameStateData for use at the beginning of the game.
	 * Player One/Zero is small blind and first to act.
	 * 
	 * @return Initial game state data object.
	 */
	public static GameStateData getInitialGameStateData() {
		return new GameStateData(Main.STARTING_CHIPS - Main.SMALL_BLIND,
				Main.STARTING_CHIPS - Main.BIG_BLIND, Card.getRandomCard(),
				Card.getRandomCard(), Main.SMALL_BLIND, Main.BIG_BLIND, true,
				true, Main.SMALL_BLIND + Main.BIG_BLIND, null, false);
	}

	public final int playerOneStack;
	public final int playerTwoStack;
	public final Card playerOneCard;
	public final Card playerTwoCard;
	public final int playerOneAmountBetSoFar;
	public final int playerTwoAmountBetSoFar;
	public final boolean playerOneHasButton;
	public final boolean playerOneToBet;
	public final int potSize;
	public final BettingAction lastAction;
	/** True if the game is over, and this state is only there to show cards */
	public final boolean isHandOver;

	/**
	 * Private Constructor of new gameStates.
	 * 
	 * @param playerOneStack
	 * @param playerTwoStack
	 * @param playerOneCard
	 * @param playerTwoCard
	 * @param playerOneAmountBetSoFar
	 * @param playerTwoAmountBetSoFar
	 * @param playerOneHasButton
	 * @param playerOneToBet
	 * @param potSize
	 * @param lastAction
	 */
	private GameStateData(final int playerOneStack, final int playerTwoStack,
			final Card playerOneCard, final Card playerTwoCard,
			final int playerOneAmountBetSoFar,
			final int playerTwoAmountBetSoFar,
			final boolean playerOneHasButton, final boolean playerOneToBet,
			final int potSize, final BettingAction lastAction,
			final boolean isHandOver) {
		super();
		this.playerOneStack = playerOneStack;
		this.playerTwoStack = playerTwoStack;
		this.playerOneCard = playerOneCard;
		this.playerTwoCard = playerTwoCard;
		this.playerOneAmountBetSoFar = playerOneAmountBetSoFar;
		this.playerTwoAmountBetSoFar = playerTwoAmountBetSoFar;
		this.playerOneHasButton = playerOneHasButton;
		this.playerOneToBet = playerOneToBet;
		this.potSize = potSize;
		this.lastAction = lastAction;
		this.isHandOver = isHandOver;

		// Validate the number of total chips in the game.
		if (playerOneStack + playerTwoStack + potSize != Main.STARTING_CHIPS * 2) {
			Main.fail("Illegal gameStateData creation where total chips != what it should");
		}
	}

	/**
	 * Switches button, causes players to pay their blinds, then deals out cards
	 * and properly sets all variables. This should be called after
	 * {@link #endHand()}.
	 * 
	 * @return The new instance of a GameStateData after the hand has been
	 *         dealt.
	 */
	protected GameStateData dealNextHand() {
		// Handle easy variables:
		final boolean p1Button = !playerOneHasButton;
		final boolean p1ToBet = p1Button;
		// Declare variables for use in constructor call
		final int p1Stack, p2Stack, p1Bet, p2Bet, pSize;

		// Now lets handle the normal case where the blinds aren't more than a
		// players stack size
		if (playerOneStack >= Main.BIG_BLIND
				&& playerTwoStack >= Main.BIG_BLIND) {

			pSize = Main.BIG_BLIND + Main.SMALL_BLIND;

			// Handle the bets
			if (!p1Button) {
				p1Bet = Main.BIG_BLIND;
				p2Bet = Main.SMALL_BLIND;
			} else {
				p1Bet = Main.SMALL_BLIND;
				p2Bet = Main.BIG_BLIND;
			}

			// Subtract from stacks
			p1Stack = playerOneStack - p1Bet;
			p2Stack = playerTwoStack - p2Bet;
		}

		// However, in the case that a player has only one chip, we must only
		// bet small blinds.
		else {
			pSize = Main.SMALL_BLIND + Main.SMALL_BLIND;

			// Handle the bets
			if (!p1Button) {
				p1Bet = Main.SMALL_BLIND;
				p2Bet = Main.SMALL_BLIND;
			} else {
				p1Bet = Main.SMALL_BLIND;
				p2Bet = Main.SMALL_BLIND;
			}

			// Subtract from stacks
			p1Stack = playerOneStack - p1Bet;
			p2Stack = playerTwoStack - p2Bet;
		}

		return new GameStateData(p1Stack, p2Stack, Card.getRandomCard(),
				Card.getRandomCard(), p1Bet, p2Bet, p1Button, p1ToBet, pSize,
				null, false);
	}

	/**
	 * 
	 * @return Winner of current hand in the game, regardless of whether the
	 *         betting round is complete or not.
	 */
	private Winner determineWinner() {
		// Validate current state of game
		if (playerOneCard == Card.Unkown || playerTwoCard == Card.Unkown) {
			Main.fail("Improper usage of #determineWinner()");
		}

		// Handle folds SUGGESTION Ensure this is still necessary if we change
		// how GameState handles folding.
		if (playerOneCard == Card.Fold || playerTwoCard == Card.Winner)
			return Winner.playerTwo;
		else if (playerOneCard == Card.Winner || playerTwoCard == Card.Fold)
			return Winner.playerOne;

		// Determine winner based off value of cards.
		if (playerOneCard.value > playerTwoCard.value)
			return Winner.playerOne;
		if (playerOneCard.value == playerTwoCard.value)
			return Winner.tie;
		else
			return Winner.playerTwo;

	}

	/**
	 * Determines winner and transfers the chips over to the winner.
	 * 
	 * @return
	 */
	protected GameStateData endHand() {
		/*
		 * The first step of this method is to determine the winning hand (or if
		 * there is a tie). After that, the chips must be transfered from the
		 * pot to the winning player (or split).
		 */

		final Winner winner = this.determineWinner();
		Main.systemPrint(String.format(
				"The winner was %s! The winning player(s) will"
						+ " recieve the pot of %s chips", winner, potSize));

		// Variables that will be used in the creation of a new GameStateData
		int p1Stack;
		int p2Stack;

		if (winner == Winner.tie) {
			if (potSize % 2 == 1) {
				// PlayerOne gets the extra chip in odd sized pots.
				p1Stack = playerOneStack + (potSize / 2 + 1);
				p2Stack = playerTwoStack + (potSize / 2);
			} else {
				p1Stack = playerOneStack + (potSize / 2);
				p2Stack = playerTwoStack + (potSize / 2);
			}
		} else if (winner == Winner.playerOne) {
			p1Stack = playerOneStack + potSize;
			p2Stack = playerTwoStack;
		} else {
			p1Stack = playerOneStack;
			p2Stack = playerTwoStack + potSize;
		}

		return new GameStateData(p1Stack, p2Stack, playerOneCard,
				playerTwoCard, playerOneAmountBetSoFar,
				playerTwoAmountBetSoFar, playerOneHasButton, playerOneToBet, 0,
				lastAction, true);
	}

	/**
	 * Generates a new GameStateData object for a given player, which hides the
	 * opponents cards. (Note that GameStateData is an immutable class, so it is
	 * also safe for agents to store an instance of this created object knowing
	 * it won't change.)
	 * 
	 * SUGGESTION Card value is not obfuscated, but instead, the code to
	 * generate obfuscated data takes into account lastAction being a fold.
	 * 
	 * @param isPlayerOnePerspective
	 *            True if the obfuscated data should be from the perspective of
	 *            player one. False if for the perspective of the second player.
	 * @return GameStateData with Card.Unkown in place of the opponents card.
	 */
	protected GameStateData generateObfuscatedData(
			final boolean isPlayerOnePerspective) {
		if (isPlayerOnePerspective)
			return new GameStateData(playerOneStack, playerTwoStack,
					playerOneCard, Card.Unkown, playerOneAmountBetSoFar,
					playerTwoAmountBetSoFar, playerOneHasButton,
					playerOneToBet, potSize, lastAction, isHandOver);
		else
			return new GameStateData(playerOneStack, playerTwoStack,
					Card.Unkown, playerTwoCard, playerOneAmountBetSoFar,
					playerTwoAmountBetSoFar, playerOneHasButton,
					playerOneToBet, potSize, lastAction, isHandOver);
	}

	/**
	 * Determines the amount of chips needed to call for the current player to
	 * act.
	 * 
	 * @return a number greater than or equal to zero specifying amount required
	 *         to call (or check)
	 */
	public int getCallAmount() {

		int callAmount;

		if (playerOneToBet) {
			callAmount = playerTwoAmountBetSoFar - playerOneAmountBetSoFar;

			// Check for all-in cases.
			if (callAmount > playerOneStack) {
				callAmount = playerOneStack;
			}

			if (callAmount > playerTwoStack) {

				callAmount = playerTwoStack;
			}

			// Validate callAmount before returning.
			if (callAmount < 0 || callAmount > Main.STARTING_CHIPS) {
				Main.fail(String
						.format("Illegal CallAmount of %s, where player one is "
								+ "up to act and player one has bet %s and player"
								+ " two has bet %s."
								+ "\nThe current state is:\n	%s", callAmount,
								playerOneAmountBetSoFar,
								playerTwoAmountBetSoFar, this));
			}
		} else {
			callAmount = playerOneAmountBetSoFar - playerTwoAmountBetSoFar;

			// Check for all-in cases.
			if (callAmount > playerTwoStack) {
				callAmount = playerTwoStack;
			}

			if (callAmount < 0 || callAmount > Main.STARTING_CHIPS) {
				Main.fail(String
						.format("Illegal CallAmount of %s, where player two is "
								+ "up to act and player one has bet %s and player"
								+ " two has bet %s."
								+ "\nThe current state is:\n	%s", callAmount,
								playerOneAmountBetSoFar,
								playerTwoAmountBetSoFar, this));
			}
		}

		return callAmount;
	}

	/**
	 * Determines the maximum amount you are allowed to bet. If the opponent has
	 * fewer chips than you, then you are not allowed to bet more than them.
	 * Thus AI agents and the GUI should use this method to determine how much
	 * is "all-in"
	 * 
	 * @return The maximum bet allowed for the current player to act.
	 */
	public int getMaxBetAmount() {

		// Get the smallest of playerOneStack and playerTwoStack
		int maxBet = playerOneStack;
		if (playerTwoStack < maxBet) {
			maxBet = playerTwoStack;
		}

		if (maxBet < getCallAmount()) {
			maxBet = getCallAmount();
		}

		// Validate return value.
		if (maxBet < 0) {
			Main.fail(String
					.format("Illegal Return value from MaxBetAmount of %s in the gameState %s",
							maxBet, this.toString()));
		} else if (maxBet > Main.STARTING_CHIPS) {
			Main.fail("Max bet should never be more than the starting amount of chips.");
		}

		return maxBet;
	}

	/**
	 * Generates a GameStateData after applying the bet. Only legal bets/actions
	 * are allowed. If a player folds, the values of the cards will become
	 * obfuscated automatically to {@code Card.Fold} and {@code Card.Winner},
	 * because no information should be gained after a fold.
	 * 
	 * SUGGESTION Card value is not obfuscated, but instead, the code to
	 * generate obfuscated data takes into account lastAction being a fold.
	 * 
	 * @param bet
	 *            Any legal betting action.
	 * @return new Instance of GameStateData after applying the bet.
	 */
	protected GameStateData getResultingGameStateData(final BettingAction bet) {

		if (!isLegalAction(bet)) {
			Main.fail(String.format(
					"Illegal action %s applied to the gameState %s",
					bet.toString(), this.toString()));
		}

		boolean isOver = false;

		// Determine if the hand is over
		if (playerOneToBet && playerOneStack == 0) {
			isOver = true;
		} else if (!playerOneToBet && playerTwoStack == 0) {
			isOver = true;
		}

		// folding always ends the betting.
		else if (bet == BettingAction.FOLD) {
			isOver = true;
		}

		// If the action was a call, but isn't the first call of the game
		// (determined by pot size), then the betting is over.
		else if (bet.getAmount() <= getCallAmount()
				&& !(potSize <= (Main.BIG_BLIND + Main.SMALL_BLIND))) {
			// Big Blind plus small blind is the starting pot size- it's an easy
			// way of knowing if it's the first action of the game.
			isOver = true;
		} else if (bet.isCheck()) {
			isOver = true;
		} else {
			isOver = false;
		}

		if (playerOneToBet) {
			if (bet.isFold())
				return new GameStateData(playerOneStack, playerTwoStack,
						Card.Fold, Card.Winner, playerOneAmountBetSoFar,
						playerTwoAmountBetSoFar, playerOneHasButton,
						playerOneToBet, potSize, bet, isOver);

			return new GameStateData(playerOneStack - bet.getAmount(),
					playerTwoStack, playerOneCard, playerTwoCard,
					playerOneAmountBetSoFar + bet.getAmount(),
					playerTwoAmountBetSoFar, playerOneHasButton,
					!playerOneToBet, potSize + bet.getAmount(), bet, isOver);
		} else {
			if (bet.isFold())
				return new GameStateData(playerOneStack, playerTwoStack,
						Card.Winner, Card.Fold, playerOneAmountBetSoFar,
						playerTwoAmountBetSoFar, playerOneHasButton,
						playerOneToBet, potSize, bet, isOver);

			return new GameStateData(playerOneStack, playerTwoStack
					- bet.getAmount(), playerOneCard, playerTwoCard,
					playerOneAmountBetSoFar, playerTwoAmountBetSoFar
							+ bet.getAmount(), playerOneHasButton,
					!playerOneToBet, potSize + bet.getAmount(), bet, isOver);
		}
	}

	/**
	 * Note: This is only public for the GUI really. AI's shouldn't be using
	 * this as a core part of their logic (I don't think); I think it's best to
	 * use this in an AI as just a test to make sure it never generates an
	 * illegal move.
	 * 
	 * @param action
	 * 
	 * @return True if the action is legal
	 */
	public boolean isLegalAction(final BettingAction action) {
		if (action.isFold())
			return true;

		/*
		 * Something would be illegal if a bet was less the amount to call. It
		 * would also be illegal if a player was betting more chips than they
		 * had. Furthermore, it's illegal if it's more than the max amount you
		 * are allowed to bet (an all-in can not exceed your opponents stack
		 * size.)
		 */
		if (playerOneToBet) {
			// First check if Player One has the chips to do so
			if (action.getAmount() > playerOneStack)
				return false;

			// Now check if it's greater than or equal to the amount to call.
			if (action.getAmount() < getCallAmount())
				return false;
		} else {
			// First check if Player Two has the chips to do so
			if (action.getAmount() > playerTwoStack)
				return false;

			// Now check if it's greater than or equal to the amount to call.
			if (action.getAmount() < getCallAmount())
				return false;
		}

		// If the checks didn't fail, then it's a legal action.
		return true;
	}

	/**
	 * 
	 * @return True if the entire sit n' go is over and a winner has been
	 *         determined.
	 */
	protected boolean isTerminalState() {
		if ((playerOneStack <= 0 || playerTwoStack <= 0) && potSize == 0)
			return true;
		if (playerOneStack < 0 || playerTwoStack < 0)
			return true;
		if (playerOneStack == Main.STARTING_CHIPS * 2
				|| playerTwoStack == Main.STARTING_CHIPS * 2)
			return true;

		return false;
	}

	@Override
	public String toString() {
		return "GameStateData [playerOneStack=" + playerOneStack
				+ ", playerTwoStack=" + playerTwoStack + ", playerOneCard="
				+ playerOneCard + ", playerTwoCard=" + playerTwoCard
				+ ", playerOneAmountBetSoFar=" + playerOneAmountBetSoFar
				+ ", playerTwoAmountBetSoFar=" + playerTwoAmountBetSoFar
				+ ", playerOneHasButton=" + playerOneHasButton
				+ ", playerOneToBet=" + playerOneToBet + ", potSize=" + potSize
				+ ", lastAction=" + lastAction + "]";
	}
}
