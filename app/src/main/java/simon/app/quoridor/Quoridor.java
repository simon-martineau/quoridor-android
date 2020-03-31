package simon.app.quoridor;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.JsonReader;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Quoridor {
	// Debug
	private static final String TAG = "QUORIDOR";

	// Constant
	public static final int HORIZONTAL = 1;
	public static final int VERTICAL = 2;

	// Logic & api
	String mGameID = "";
	int[] mPlayerOnePosition = new int[2];
	int mPlayerOneWallsLeft = 10;
	String mPlayerOneName = "";
	int[] mPlayerTwoPosition = new int[2];
	int mPlayerTwoWallsLeft = 10;
	String mPlayerTwoName = "";
	List<int[]> mHorizontalWalls = new ArrayList<>();
	List<int[]> mVerticalWalls = new ArrayList<>();
	String mLastMoveType = "";
	String mLastMoveCoordinates = "";



	/**
	 * Copy constructor
	 * @param quoridor The quoridor game to copy from
	 */
	public Quoridor( Quoridor quoridor) {
		putGameState(quoridor.getGameStateJSON());
	}

	/**
	 * Con
	 * @param gameID The ID of the game
	 * @param initialGameState The JSONObject corresponding to the game state
	 */
	public Quoridor(String gameID, JSONObject initialGameState) {
		mGameID = gameID;
		putGameState(initialGameState);
	}

	/**
	 * Sets the game ID
	 * @param gameID String corresponding to the game ID
	 */
	public void setGameID(String gameID) {
		mGameID = gameID;
	}

	/**
	 * Changes coordinates of a player, and updates mLastMoveType and mLastMoveCoordinates
	 * accordingly
	 * @param playerNumber The number of the player to change position (1 or 2)
	 * @param x The new x coordinate
	 * @param y The new y coordinate
	 */
	public void movePlayer(int playerNumber, int x, int y) {
		if (playerNumber == 1)
			mPlayerOnePosition = new int[]{x, y};
		else
			mPlayerTwoPosition = new int[]{x, y};
		mLastMoveType = "D";
		mLastMoveCoordinates = "(" + x + ", " + y + ")";
	}

	/**
	 * Adds a wall to the horizontal or vertical wall list and subtract 1 to the number of walls left
	 * for the player who places the wall
	 * @param player The player who places the wall (1 or 2)
	 * @param type The type of wall to place (constant HORIZONTAL or VERTICAL)
	 * @param x The x coordinate of the wall
	 * @param y The y coordinate of the wall
	 */
	public void placeWall(int player, int type, int x, int y) {
		if (type == HORIZONTAL) {
			int[] wallCoordinates = new int[2];
			wallCoordinates[0] = x;
			wallCoordinates[1] = y;
			mHorizontalWalls.add(wallCoordinates);
		} else if (type == VERTICAL) {
			int[] wallCoordinates = new int[2];
			wallCoordinates[0] = x;
			wallCoordinates[1] = y;
			mVerticalWalls.add(wallCoordinates);
		}
		if (player == 1) {
			mPlayerOneWallsLeft--;
		} else if (player == 2) {
			mPlayerTwoWallsLeft--;
		}
		if (type == HORIZONTAL)
			mLastMoveType = "MH";
		else
			mLastMoveType = "MV";
		mLastMoveCoordinates = "(" + x + ", " + y + ")";

	}

	/**
	 * @return JSONObject corresponding to the current game state
	 */
	public JSONObject getGameStateJSON() {

		try {
			JSONObject gameStateJSON = new JSONObject();

			// Players
			JSONArray players = new JSONArray();

			JSONObject player1 = new JSONObject();

			JSONArray player1Position = new JSONArray();
			player1Position.put(mPlayerOnePosition[0]);
			player1Position.put(mPlayerOnePosition[1]);

			player1.put("nom", mPlayerOneName);
			player1.put("murs", mPlayerOneWallsLeft);
			player1.put("pos", player1Position);

			JSONObject player2 = new JSONObject();

			JSONArray player2Position = new JSONArray();
			player2Position.put(mPlayerTwoPosition[0]);
			player2Position.put(mPlayerTwoPosition[1]);

			player2.put("nom", mPlayerTwoName);
			player2.put("murs", mPlayerTwoWallsLeft);
			player2.put("pos", player2Position);

			players.put(player1);
			players.put(player2);

			// Walls

			JSONObject walls = new JSONObject();

			JSONArray horizontalWalls = new JSONArray();
			for (int i = 0; i < mHorizontalWalls.size(); i++) {
				JSONArray currentPos = new JSONArray();
				currentPos.put(mHorizontalWalls.get(i)[0]);
				currentPos.put(mHorizontalWalls.get(i)[1]);
				horizontalWalls.put(currentPos);
			}

			JSONArray verticalWalls = new JSONArray();
			for (int i = 0; i < mVerticalWalls.size(); i++) {
				JSONArray currentPos = new JSONArray();
				currentPos.put(mVerticalWalls.get(i)[0]);
				currentPos.put(mVerticalWalls.get(i)[1]);
				verticalWalls.put(currentPos);
			}

			walls.put("horizontaux", horizontalWalls);
			walls.put("verticaux", verticalWalls);

			// Total

			gameStateJSON.put("joueurs", players);
			gameStateJSON.put("murs", walls);

			return gameStateJSON;

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Parses game state and injects modifies the object to match that game state.
	 * @param gameState A JSONObject to be injected
	 */
	public void putGameState(JSONObject gameState) {

		// Clear containers
		mHorizontalWalls.clear();
		mVerticalWalls.clear();

		// Players data
		try {
			// Get players list and iterate through it
			JSONArray players = gameState.getJSONArray("joueurs");
			JSONObject currentPlayer;
			for (int i = 0; i < players.length(); i++) {

				currentPlayer = (JSONObject) players.get(i);
				int[] position = new int[2];
				int wallsLeft;
				String name;

				JSONArray positionJSON = currentPlayer.getJSONArray("pos");
				for (int j = 0; j < positionJSON.length(); j++) {
					position[j] = positionJSON.getInt(j);
				}

				wallsLeft = currentPlayer.getInt("murs");
				name = currentPlayer.getString("nom");

				if (i == 0) {
					mPlayerOnePosition = position;
					mPlayerOneWallsLeft = wallsLeft;
					mPlayerOneName = name;
				} else {
					mPlayerTwoPosition = position;
					mPlayerTwoWallsLeft = wallsLeft;
					mPlayerTwoName = name;
				}
			}

		} catch	(Exception e) {
			Log.e(TAG, "parseJSON: Error parsing players. Object: " + gameState.toString());
			e.printStackTrace();
		}

		// Walls
		try {
			JSONObject walls = gameState.getJSONObject("murs");
			JSONArray currentCoordinates;

			// Horizontal
			JSONArray horizontalWalls = walls.getJSONArray("horizontaux");
			for (int i = 0; i < horizontalWalls.length(); i++) {

				int[] position = new int[2];

				currentCoordinates = horizontalWalls.getJSONArray(i);
				for (int j = 0; j < currentCoordinates.length(); j++) {
					position[j] = currentCoordinates.getInt(j);
				}

				mHorizontalWalls.add(position);
			}

			// Vertical
			JSONArray verticalWalls = walls.getJSONArray("verticaux");
			for (int i = 0; i < verticalWalls.length(); i++) {

				int[] position = new int[2];

				currentCoordinates = verticalWalls.getJSONArray(i);
				for (int j = 0; j < currentCoordinates.length(); j++) {
					position[j] = currentCoordinates.getInt(j);
				}

				mVerticalWalls.add(position);
			}

		} catch	(Exception e) {
			Log.e(TAG, "parseJSON: Error parsing walls");
			e.printStackTrace();
		}
	}

	/**
	 * Moves player at specified position if the move is valid and legal, else throws a QuoridorException
	 * @param playerNumber The player to move to the position (1 or 2)
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @throws QuoridorException if the move is invalid
	 */
	public void requestPlayerMovement(int playerNumber, int x, int y) throws QuoridorException {
		int[] coordinates = new int[]{x, y};
		List<int[]> possibleNextCoordinates = getPossibleNextCoordinates(playerNumber, false, null);
		if (positionIncluded(coordinates, possibleNextCoordinates)) {
			movePlayer(playerNumber, x, y);
		} else {
			throw new QuoridorException("Invalid movement request at (" + x + ", " + y + ")");
		}

	}

	/**
	 * Places wall at specified position if it is valid, else throws a QuoridorException.
	 * @param playerNumber The player placing the wall (1 or 2)
	 * @param wallType The type of wall to place (constant HORIZONTAL or VERTICAL)
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @throws QuoridorException if the move is invalid
	 */
	public void requestWallPlacement(int playerNumber, int wallType, int x, int y) throws QuoridorException {
		int[] coordinates = new int[]{x, y};
		List<int[]> invalidWallCoordinates = getInvalidWallCoordinates(wallType);

		if (!positionIncluded(coordinates, invalidWallCoordinates)) {
			placeWall(playerNumber, wallType, x, y);
		} else {
			throw new QuoridorException("Invalid wall placement request");
		}
	}

	/**
	 * Retrieves a list of invalid wall coordinates depending on the current game state for the specified wall type
	 * @param wallType The wall type for which to get the list of invalid positions (HORIZONTAL or VERTICAL)
	 * @return A list containing arrays of size 2 (x, y), the invalid coordinates for the wall placement
	 */
	public List<int[]> getInvalidWallCoordinates(int wallType) {
		List<int[]> invalidWallCoordinates = new ArrayList<>();

		if (wallType == HORIZONTAL) {
			for (int[] coordinates : mHorizontalWalls) {
				invalidWallCoordinates.add(new int[]{coordinates[0] - 1, coordinates[1]});
				invalidWallCoordinates.add(new int[]{coordinates[0], coordinates[1]});
				invalidWallCoordinates.add(new int[]{coordinates[0] + 1, coordinates[1]});
			}
			for (int[] coordinates : mVerticalWalls) {
				invalidWallCoordinates.add(new int[]{coordinates[0] - 1, coordinates[1] + 1});
			}
		} else {
			for (int[] coordinates : mVerticalWalls) {
				invalidWallCoordinates.add(new int[]{coordinates[0], coordinates[1] - 1});
				invalidWallCoordinates.add(new int[]{coordinates[0], coordinates[1]});
				invalidWallCoordinates.add(new int[]{coordinates[0], coordinates[1] + 1});
			}
			for (int[] coordinates : mHorizontalWalls) {
				invalidWallCoordinates.add(new int[]{coordinates[0] + 1, coordinates[1] - 1});
			}
		}

		return invalidWallCoordinates;
	}

	/**
	 * Retrieves the possible coordinates a player can move to
	 * @param playerNumber The player for which to retrieve the next possible coordinates
	 * @param ignoreOtherPlayerJump Whether to ignore the option to jump over the other player
	 *                              (used in recursion to analyze the jump over the other player).
	 *                           	Should be false if called from outside the method
	 * @param virtualPosition If not null, the player is considered to be at that position (x, y).
	 *                        Used for analysing different scenarios
	 * @return A list containing all the coordinates the player can move to (size-2 arrays)
	 */
	public List<int[]> getPossibleNextCoordinates(int playerNumber, boolean ignoreOtherPlayerJump, @Nullable int[] virtualPosition) {
		List<int[]> possibleNextCoordinates = new ArrayList<>();


		int playerX;
		int playerY;

		int otherPlayerX;
		int otherPlayerY;
		int otherPlayerNumber;

		int[] savedPlayerPosition = new int[2];


		if (playerNumber == 1) {
			if (virtualPosition != null) {
				savedPlayerPosition = new int[]{mPlayerOnePosition[0], mPlayerOnePosition[1]};
				mPlayerOnePosition = virtualPosition;
			}
			playerX = mPlayerOnePosition[0];
			playerY = mPlayerOnePosition[1];
			otherPlayerX = mPlayerTwoPosition[0];
			otherPlayerY = mPlayerTwoPosition[1];
			otherPlayerNumber = 2;


		} else {
			if (virtualPosition != null) {
				savedPlayerPosition = new int[]{mPlayerTwoPosition[0], mPlayerTwoPosition[1]};
				mPlayerTwoPosition = virtualPosition;
			}
			playerX = mPlayerTwoPosition[0];
			playerY = mPlayerTwoPosition[1];
			otherPlayerX = mPlayerOnePosition[0];
			otherPlayerY = mPlayerOnePosition[1];
			otherPlayerNumber = 1;
		}

		// Up
		if (playerY < 9) {
			if (!(isHorizontalWall(playerX - 1, playerY + 1) || isHorizontalWall(playerX, playerY + 1))) {
				// There is no wall
				if (playerX == otherPlayerX && playerY + 1 == otherPlayerY) {
					// Other player is there
					if (!ignoreOtherPlayerJump) {
						List<int[]> otherPlayerMoves = getPossibleNextCoordinates(otherPlayerNumber, true, null);
						if (positionIncluded(new int[]{otherPlayerX, otherPlayerY + 1}, otherPlayerMoves)) {
							possibleNextCoordinates.add(new int[]{otherPlayerX, otherPlayerY + 1});
						} else {
							possibleNextCoordinates.addAll(otherPlayerMoves);
						}
					} // Else do nothing

				} else {
					// Other player is not there, add position
					possibleNextCoordinates.add(new int[]{playerX, playerY + 1});
				}
			}
		}

		// Down
		if (playerY > 1) {
			if (!(isHorizontalWall(playerX - 1, playerY) || isHorizontalWall(playerX, playerY))) {
				// There is no wall
				if (playerX == otherPlayerX && playerY - 1 == otherPlayerY) {
					// Other player is there
					if (!ignoreOtherPlayerJump) {
						List<int[]> otherPlayerMoves = getPossibleNextCoordinates(otherPlayerNumber, true, null);
						if (positionIncluded(new int[]{otherPlayerX, otherPlayerY - 1}, otherPlayerMoves)) {
							possibleNextCoordinates.add(new int[]{otherPlayerX, otherPlayerY - 1});
						} else {
							possibleNextCoordinates.addAll(otherPlayerMoves);
						}
					} // Else do nothing
				} else {
					// Other player is not there, add position
					possibleNextCoordinates.add(new int[]{playerX, playerY - 1});
				}
			}
		}

		// Left
		if (playerX > 1) {
			if (!(isVerticalWall(playerX, playerY) || isVerticalWall(playerX, playerY - 1))) {
				// There is no wall
				if (playerY == otherPlayerY && playerX - 1 == otherPlayerX) {
					// Other player is there
					if (!ignoreOtherPlayerJump) {
						List<int[]> otherPlayerMoves = getPossibleNextCoordinates(otherPlayerNumber, true, null);
						if (positionIncluded(new int[]{otherPlayerX - 1, otherPlayerY}, otherPlayerMoves)) {
							possibleNextCoordinates.add(new int[]{otherPlayerX - 1, otherPlayerY});
						} else {
							possibleNextCoordinates.addAll(otherPlayerMoves);
						}
					} // Else do nothing
				} else {
					// Other player is not there, add position
					possibleNextCoordinates.add(new int[]{playerX - 1, playerY});
				}
			}
		}

		// Right
		if (playerX < 9) {
			if (!(isVerticalWall(playerX + 1, playerY) || isVerticalWall(playerX + 1, playerY - 1))) {
				// There is no wall
				if (playerY == otherPlayerY && playerX + 1 == otherPlayerX) {
					// Other player is there
					if (!ignoreOtherPlayerJump) {
						List<int[]> otherPlayerMoves = getPossibleNextCoordinates(otherPlayerNumber, true, null);
						if (positionIncluded(new int[]{otherPlayerX + 1, otherPlayerY}, otherPlayerMoves)) {
							possibleNextCoordinates.add(new int[]{otherPlayerX + 1, otherPlayerY});
						} else {
							possibleNextCoordinates.addAll(otherPlayerMoves);
						}
					} // Else do nothing
				} else {
					// Other player is not there, add position
					possibleNextCoordinates.add(new int[]{playerX + 1, playerY});
				}
			}
		}

		if (virtualPosition != null) {
			if (playerNumber == 1) {
				mPlayerOnePosition = savedPlayerPosition;
			} else {
				mPlayerTwoPosition = savedPlayerPosition;
			}
		}

		return possibleNextCoordinates;
	}

	/**
	 * Finds out if there is a horizontal wall at the specified location in the current game state
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return True if there is a wall, false if not
	 */
	private boolean isHorizontalWall(int x, int y) {
		boolean isWall = false;
		for (int[] wall : mHorizontalWalls) {
			if (wall[0] == x && wall[1] == y) {
				isWall = true;
				break;
			}
		}
		return isWall;
	}

	/**
	 * Finds out if there is a vertical wall at the specified location in the current game state
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @return True if there is a wall, false if not
	 */
	private boolean isVerticalWall(int x, int y) {
		boolean isWall = false;
		for (int[] wall : mVerticalWalls) {
			if (wall[0] == x && wall[1] == y) {
				isWall = true;
				break;
			}
		}
		return isWall;
	}

	/**
	 * Utility method to check if a position is included in a List. The function assumes that the
	 * positions are arrays of size 2, both in the list and for the first parameter
	 * @param position An array of size 2 corresponding to a position (x, y)
	 * @param positionArrayList The list to search in
	 * @return True if the position is found in the list, else false
	 */
	static boolean positionIncluded(int[] position, List<int[]> positionArrayList) {
		boolean included = false;

		for (int[] positionInArray : positionArrayList) {
			if (position[0] == positionInArray[0] && position[1] == positionInArray[1]) {
				included = true;
				break;
			}
		}

		return included;
	}

	/**
	 * Check if a player has won the game
	 * @return The number corresponding to the winner (1 or 2) if someone has won, else 0
	 */
	public int getWinnerPlayerNumberOrZero() {
		if (mPlayerOnePosition[1] == 9) return 1;
		if (mPlayerTwoPosition[1] == 1) return 2;
		return 0;
	}

	/**
	 * Class used for the A star pathfinder algorithm
	 */
	private static class Node {
		int g = 0;
		int h = 0;

		int[] position;
		Node parent;
		List<Node> successors = new ArrayList<>();

		public Node(Node parent, int[] position) {
			this.position = position;
			this.parent = parent;
		}

		public void addSuccessor(Node node) {
			successors.add(node);
		}

		public int getF() {
			return g + h;
		}

		@NotNull
		@Override
		public String toString() {
			return Arrays.toString(position);
		}
	}

	/**
	 * Finds the shortest path a player has to get to the victory line
	 * @param playerNumber The player number (1 or 2) for which to find the path
	 * @return A list containing coordinates (arrays of size 2) to the victory or null if not path
	 * is possible. Does not include the starting position, but includes the ending position (since
	 * there are several possible ending positions). The first element of the list is the first
	 * position the player has to move to.
	 */
	public List<int[]> getShortestPathToVictory(int playerNumber) { // null if none
		ArrayList<Node> openNodes = new ArrayList<>();
		ArrayList<Node> closedNodes = new ArrayList<>();

		Node baseNode = new Node(null, getPlayerPosition(playerNumber));
		baseNode.h = aStarEvaluateF(playerNumber, baseNode.position);
		openNodes.add(baseNode);

		while (!openNodes.isEmpty()) {
			// Find node with the least f
			int leastFNodeIndex = 0;
			int leastF = 100;
			for (int i = 0; i < openNodes.size(); i++) {
				if (openNodes.get(i).getF() < leastF) {
					leastF = openNodes.get(i).getF();
					leastFNodeIndex = i;
				}
			}

			Node q = openNodes.remove(leastFNodeIndex);
			for (int[] position : getPossibleNextCoordinates(playerNumber, false, q.position)) {
				q.addSuccessor(new Node(q, position));
			}

			// Loop through successors
			for (Node successor : q.successors) {

				// Set successor h
				successor.h = aStarEvaluateF(playerNumber, successor.position);

				// If it is goal
				if (successor.getF() == 0) {
					return getPathToNode(successor);
				}

				else if (!aStarListContainsNodePositionWithLessF(successor, closedNodes)) {
					openNodes.add(successor);
				}

			}

			closedNodes.add(q);

		}

		return null;
	}

	/**
	 * Utility function for the A star algorithm. Generates the path to victory by accessing parent
	 * nodes from the winner node
	 * @param node The node to retrace the path to
	 * @return A list containing positions representing the path to that node
	 */
	private static List<int[]> getPathToNode(Node node) {
		List<int[]> path = new ArrayList<>();
		path.add(0, node.position);
		Node currentNode = node;
		while (currentNode.parent != null) {
			path.add(0, currentNode.parent.position);
			currentNode = currentNode.parent;
		}
		return path;
	}

	/**
	 * Utility function for the A star algorithm. Checks if a list contains a node with an F lower
	 * than the node passed in argument
	 * @param node The node with which to compare f's
	 * @param list The list in which to search for a node with a lower f
	 * @return True if such node is found in the list, false otherwise
	 */
	private static boolean aStarListContainsNodePositionWithLessF(Node node, List<Node> list) {
		for (Node n : list) {
			if (n.position[0] == node.position[0] && n.position[1] == node.position[1] && n.getF() <= node.getF()) return true;
		}
		return false;
	}

	/**
	 * Utility function for the A star algorithm. Evaluates the f (game state score) associated
	 * with the position passed as argument for the player specified
	 * @param playerNumber The player the position if for
	 * @param position The (x, y) coordinates of the position
	 * @return The f-value for that position and player
	 */
	private static int aStarEvaluateF(int playerNumber, int[] position) {
		if (playerNumber == 1) {
			return 9 - position[1];
		} else {
			return Math.abs(1 - position[1]);
		}
	}

	/**
	 * @param playerNumber The number of the player for which to get the position
	 * @return A 2-sized array containing the player's position
	 */
	public int[] getPlayerPosition(int playerNumber) {
		if (playerNumber == 1)
			return mPlayerOnePosition;
		else
			return mPlayerTwoPosition;
	}


}

