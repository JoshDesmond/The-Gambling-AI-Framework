package com.gmail.jdesmond10.pokerai.main;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.gmail.jdesmond10.pokerai.agent.ProjectPokerPlayer;
import com.gmail.jdesmond10.pokerai.agent.TheFish;
import com.gmail.jdesmond10.pokerai.agent.TheManiac;
import com.gmail.jdesmond10.pokerai.agent.TheRock;
import com.gmail.jdesmond10.pokerai.game.Client;
import com.gmail.jdesmond10.pokerai.game.Server;
import com.gmail.jdesmond10.pokerai.game.logic.GameState;

/**
 * This is the intended Main class for when our project is finished. Main will
 * oversee the initialization of the GUI, and any simulations the user inputs.
 * 
 * 
 * @author Josh
 */
public class Main {

	/**
	 * Set Should_Print to false if you want to disable System.out.println()
	 * commands.
	 */
	protected static boolean SHOULD_PRINT = false;

	protected static boolean SHOULD_PRINT_SOURCE = false;

	/** True if Server should pause between loops */
	protected static boolean SHOULD_PAUSE = true;

	// SUGGESTION Instead of public final variables in Main, write a public
	// static getter in Server, so that way the creation of a new server can
	// change the big and small blind values.
	public static final int MAX_PLAYERS = 2;

	public static final int BIG_BLIND = 2;

	public static final int SMALL_BLIND = 1;

	public static final int STARTING_CHIPS = 100;

	/**
	 * Time in Milliseconds that Server will pause between each action. Change
	 * this number to slow/speed up execution of a hand. XXX
	 **/
	public static final int PAUSE_TIME = 100;

	private static Semaphore serverCompletedSemaphore;

	/**
	 * Exits the program and displays the given error message. The printed error
	 * will be in the form of "Error: " + error.
	 * 
	 * @param error
	 *            A string summarizing why an error is being through.
	 */
	public static void fail(final String error) {
		System.err.println("Error: " + error);
		System.err.println(Thread.currentThread().getStackTrace()[2]);
		consolePrint("Error: " + error);
		consolePrint(Thread.currentThread().getStackTrace()[2].toString());
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		/*
		 * If you want to run a server where one of the players is SwingGUI, set
		 * the boolean in the constructor of SwingGUI to true, then in the
		 * linked list of agents, instead of TheFish(), add g.
		 */
		final SwingGUI g = new SwingGUI(false);
		ProjectTesting.executeProjectTesting();
		ProjectTesting.executeAgentTesting();
		serverCompletedSemaphore = new Semaphore(0);

		consolePrint("Testing was succesful!\n Now testing ProjectPokerPlayer against Simple Agents.");

		// Test PPP v TheFish
		LinkedList<Client> clients = new LinkedList<Client>();
		clients.add(new TheFish().initializeAgent(true));
		clients.add(new ProjectPokerPlayer().initializeAgent(true));
		consolePrint(String.format("Running %s Vs. %s", clients.get(0)
				.getName(), clients.get(1).getName()));
		Server testServer = new Server(clients, SHOULD_PAUSE);
		setNames(clients);
		final Thread s1 = new Thread(testServer);
		s1.start();

		try {
			serverCompletedSemaphore.acquire(1);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		serverCompletedSemaphore.drainPermits();

		// Test PPP v TheRock
		clients = new LinkedList<Client>();
		clients.add(new TheRock().initializeAgent(true));
		clients.add(new ProjectPokerPlayer().initializeAgent(false));
		testServer = new Server(clients, SHOULD_PAUSE);
		consolePrint(String.format("Running %s Vs. %s", clients.get(0)
				.getName(), clients.get(1).getName()));
		setNames(clients);
		final Thread s2 = new Thread(testServer);
		s2.start();

		try {
			serverCompletedSemaphore.acquire(1);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		serverCompletedSemaphore.drainPermits();

		// Test PPP v TheManiac
		clients = new LinkedList<Client>();
		clients.add(new TheManiac().initializeAgent(true));
		clients.add(new ProjectPokerPlayer().initializeAgent(false));
		consolePrint(String.format("Running %s Vs. %s", clients.get(0)
				.getName(), clients.get(1).getName()));
		testServer = new Server(clients, SHOULD_PAUSE);
		setNames(clients);
		final Thread s3 = new Thread(testServer);
		s3.start();

		// Test PPP v PPP
		clients = new LinkedList<Client>();
		clients.add(new ProjectPokerPlayer().initializeAgent(true));
		clients.add(new ProjectPokerPlayer().initializeAgent(false));
		consolePrint(String.format("Running %s Vs. %s", clients.get(0)
				.getName(), clients.get(1).getName()));
		testServer = new Server(clients, SHOULD_PAUSE);
		setNames(clients);
		final Thread s4 = new Thread(testServer);
		s4.start();

		try {
			serverCompletedSemaphore.acquire(1);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		serverCompletedSemaphore.drainPermits();

		consolePrint("Testing has completed");
		consolePrint("Now letting you play against ProjectPokerPlayer. Good luck!");

		g.setClient(true); // Set the GUI to be a client.

		// Keep testing PPP vs. GUI forever.
		while (true) {
			clients = new LinkedList<Client>();
			clients.add(new ProjectPokerPlayer().initializeAgent(true));
			clients.add(g);
			testServer = new Server(clients, SHOULD_PAUSE);
			setNames(clients);
			final Thread s5 = new Thread(testServer);
			s5.start();

			try {
				serverCompletedSemaphore.acquire(1);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			serverCompletedSemaphore.drainPermits();
			consolePrint("Now letting you play against ProjectPokerPlayer. Good luck!");
		}

	}

	public static boolean shouldPause() {
		return SHOULD_PAUSE;
	}

	/**
	 * Allows global printing of System.out.println() commands during execution
	 * of the program.
	 * 
	 * @return true if System.out.println() commands should be executed
	 */
	public static boolean shouldPrint() {
		return SHOULD_PRINT;
	}

	/**
	 * Prints a message with a link to the current class and line.
	 * 
	 * @param message
	 */
	public static void systemPrint(final String message) {
		if (SHOULD_PRINT) {
			StackTraceElement methodLocation;
			if (SHOULD_PRINT_SOURCE) {
				methodLocation = Thread.currentThread().getStackTrace()[2];

				// The source for messages coming from importantSystemPrint
				// should be stack element #3
				if (methodLocation.getMethodName() == "importantSystemPrint") {
					methodLocation = Thread.currentThread().getStackTrace()[3];
				}
				System.out.println("	--" + message + "\n" + methodLocation);
			} else {
				System.out.println(message);
			}

		}
	}

	/**
	 * Prints a message with a link to the current class and line. This will
	 * override the setting of SHOULD_PRINT, and will print regardless of
	 * whether it should or shouldn't.
	 * 
	 * @param message
	 */
	public static void importantSystemPrint(final String message) {
		final boolean temp = SHOULD_PRINT;
		final boolean temp2 = SHOULD_PRINT_SOURCE;
		SHOULD_PRINT = true;
		SHOULD_PRINT_SOURCE = true;
		systemPrint(message);
		SHOULD_PRINT = temp;
		SHOULD_PRINT_SOURCE = temp2;
	}

	/**
	 * Updates the GUI's current {@link GameState}
	 * 
	 * @param state
	 *            the current {@link GameState}
	 */
	public static void updateState(final GameState state) {

		SwingGUI.getInstance().updateState(state);
	}

	public static void gameOver() {
		serverCompletedSemaphore.release();
	}

	/**
	 * Sets the names of the GUI clients.
	 * 
	 */
	private static void setNames(final List<Client> clients) {
		SwingGUI.getInstance().setPlayerOneName(clients.get(0).getName());
		SwingGUI.getInstance().setPlayerTwoName(clients.get(1).getName());
	}

	/**
	 * Prints a message in the GUI's console.
	 * 
	 * @param string
	 *            message to printed in the GUI's console.
	 */
	public static void consolePrint(final String message) {
		SwingGUI.getInstance().printToConsole(message);
	}
}
