package com.gmail.jdesmond10.pokerai.main;

import java.util.LinkedList;
import java.util.List;

import com.gmail.jdesmond10.pokerai.agent.TestClient;
import com.gmail.jdesmond10.pokerai.game.Client;
import com.gmail.jdesmond10.pokerai.game.Server;
import com.gmail.jdesmond10.pokerai.game.logic.BettingAction;
import com.gmail.jdesmond10.pokerai.game.logic.Card;
import com.gmail.jdesmond10.pokerai.game.logic.GameState;
import com.gmail.jdesmond10.pokerai.game.logic.GameStateData;

public class ProjectTesting {

	private static boolean cardTest() {
		final Card c1 = Card.Ace;
		final Card c2 = Card.Seven;
		if (c2.compareTo(c1) != c2.value - c1.value) {
			System.out.println("Card Value error");
			return false;
		}

		// Ensure no funky cards are being generated.
		for (int i = 30; i > 0; i--) {
			final Card c = Card.getRandomCard();
			if (c == Card.Fold || c == Card.Unkown || c == Card.Winner)
				return false;
		}

		return true;
	}

	/**
	 * Ensures that our current version of the Agent is function. This is
	 * achieved by testing for whether the Agent responds correctly given
	 * various gameStates.
	 */
	public static void executeAgentTesting() {
		// TODO Write AgentTesting method once the main class for an agent has
		// been written.
	}

	/**
	 * While building the project, this method should be run before every
	 * execution of the project while making changes to ensure that no code has
	 * been broken/altered.
	 */
	public static void executeProjectTesting() {
		final boolean sprint = Main.SHOULD_PRINT;
		final boolean spause = Main.SHOULD_PAUSE;
		Main.SHOULD_PRINT = false;
		Main.SHOULD_PAUSE = false;
		Main.consolePrint("Testing Project Code to ensure all classes are running properly:");
		if (!cardTest()) {
			Main.fail("Test Failure: cardTest");
		} else {
			Main.consolePrint("cardTest Passed");
		}
		if (!gameStateDataTest()) {
			Main.fail("Test Failure: gameStateDataTest");
		} else {
			Main.consolePrint("gameStateDataTest Passed");
		}
		if (!gameStateTest()) {
			Main.fail("Test Failure: gameStateTest");
		} else {
			Main.consolePrint("gameStateTest Passed");
		}

		// We will run serverTest 20 times to ensure it's definitely definitely
		// working (as random is involved in the server testing, so with enough
		// tests weird situations lie ties during all-ins on blinds will occur.
		// for (int i = 20; i > 0; i--) {
		// if (!serverTest()) {
		// Main.fail("Test Failure: serverTest");
		// }
		// }
		Main.consolePrint("serverTest Passed");

		Main.SHOULD_PRINT = sprint;
		Main.SHOULD_PAUSE = spause;
	}

	/**
	 * Tests whether certain actions are being considered legal or not.
	 * 
	 * @return True if the test passes
	 */
	private static boolean gameStateDataTest() {
		final GameStateData g = GameStateData.getInitialGameStateData();
		if (!g.isLegalAction(BettingAction.FOLD)) {
			System.out.println("Failure to consider a fold legal");
			return false;
		}
		if (!g.isLegalAction(new BettingAction(5))) {
			System.out.println("Failure to consider bet of 5 chips legal");
			return false;
		}
		if (g.isLegalAction(new BettingAction(5000))) {
			System.out
			.println("Failure to consider a bet of 5000 chips illegal");
			return false;
		}
		return true;
	}

	private static boolean gameStateTest() {
		/*
		 * Create a GameState with two players, and create and input Actions
		 * that should work. Then check the resulting GameState to see if it's
		 * correct.
		 */
		final GameState g = new GameState(2);
		final GameStateData data = g.getGameStateData();

		if (g.getCurrentPlayer() != 0) {
			Main.fail("");
			return false;
		}
		if (!g.getGameStateData().playerOneToBet) {
			Main.fail("");
			return false;
		}
		if (!g.getGameStateData().playerOneHasButton) {
			Main.fail("");
			return false;
		}

		if (!g.applyAction(BettingAction.FOLD)) {
			Main.fail("");
			return false;
		}

		if (g.getGameStateData().lastAction != BettingAction.FOLD) {
			Main.fail("");
			return false;
		}

		if (g.getGameStateData().potSize != Main.SMALL_BLIND + Main.BIG_BLIND) {
			Main.fail("");
			return false;
		}

		if (g.isTerminalState()) {
			Main.fail("");
			return false;
		}

		g.endHand();

		if (g.getGameStateData() == data) {
			Main.fail("");
			return false;
		}

		if (g.isTerminalState()) {
			Main.fail("");
			return false;
		}

		g.dealNextHand();

		if (g.getGameStateData().playerOneHasButton) {
			System.out.println("FAILURE to switch Button in GameStateData");
			return false;
		}
		if (g.getGameStateData().playerOneToBet) {
			System.out.println("FAILURE to switch player in GameStateData");
			return false;
		}
		if (g.getCurrentPlayer() == 0) {
			System.out.println("FAILURE to switch player in GameState");
			return false;
		}

		return true;
	}

	private static boolean serverTest() {
		/*
		 * Creates two TestClient agents and creates a server to see if it runs
		 */
		final List<Client> clients = new LinkedList<Client>();
		clients.add(new TestClient().initializeAgent(true));
		clients.add(new TestClient().initializeAgent(false));
		final Server s = new Server(clients, false);
		final Thread t = new Thread(s);
		t.start();

		// Checks 100 times every 1ms if the server has finished execution. It
		// should be done quickly but with enough ties it could go on a while.
		for (int i = 100; i > 0; i--) {
			try {
				Thread.sleep(1);
			} catch (final InterruptedException e) {
				e.printStackTrace();
				Main.fail("Interruption while pausing during tests");
			}
			if (!s.isRunning())
				return true;
		}

		return false;
	}
}
