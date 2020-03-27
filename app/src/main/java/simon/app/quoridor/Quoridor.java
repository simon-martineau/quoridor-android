package simon.app.quoridor;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.JsonReader;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
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

	public Quoridor() {

	}



	public Quoridor(String gameID, JSONObject initialGameState) {
		mGameID = gameID;
		putGameState(initialGameState);
	}

	public void setGameID(String gameID) {
		mGameID = gameID;
	}

	public void movePlayer(int playerNumber, int x, int y) {
		if (playerNumber == 1)
			mPlayerOnePosition = new int[]{x, y};
		else
			mPlayerTwoPosition = new int[]{x, y};
		mLastMoveType = "D";
		mLastMoveCoordinates = "(" + x + ", " + y + ")";
	}

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
				horizontalWalls.put(currentPos);
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

	private void assignPlayerPosition(int player, int x, int y) {
		if (x < 1 || x > 9 || y < 1 || y > 9) throw new AssertionError("Invalid coordinates");
		if (player < 1 || player > 2) throw new AssertionError("Invalid player number");
		if (player == 1) {
			mPlayerOnePosition[0] = x;
			mPlayerOnePosition[1] = y;
		} else {
			mPlayerTwoPosition[0] = x;
			mPlayerTwoPosition[1] = y;
		}
	}

	public void requestPlayerMovement(int playerNumber, int x, int y) throws QuoridorException {
		int[] coordinates = new int[]{x, y};
		List<int[]> possibleNextCoordinates = getPossibleNextCoordinates(playerNumber, false, null);
		if (positionIncluded(coordinates, possibleNextCoordinates)) {
			movePlayer(playerNumber, x, y);
		} else {
			throw new QuoridorException("Invalid movement request at (" + x + ", " + y + ")");
		}

	}

	public void requestWallPlacement(int playerNumber, int wallType, int x, int y) throws QuoridorException {
		int[] coordinates = new int[]{x, y};
		List<int[]> invalidWallCoordinates = getInvalidWallCoordinates(wallType);

		if (!positionIncluded(coordinates, invalidWallCoordinates)) {
			placeWall(playerNumber, wallType, x, y);
		} else {
			throw new QuoridorException("Invalid wall placement request");
		}
	}

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


	private boolean isHorizontalWall(int x, int y) {
		boolean isWall = false;
		for (int[] wall : mHorizontalWalls) {
			if (wall[0] == x && wall[1] == y) isWall = true;
		}
		return isWall;
	}

	private boolean isVerticalWall(int x, int y) {
		boolean isWall = false;
		for (int[] wall : mVerticalWalls) {
			if (wall[0] == x && wall[1] == y) isWall = true;
		}
		return isWall;
	}

	private static boolean positionIncluded(int[] position, List<int[]> positionArrayList) {
		boolean included = false;

		for (int[] positionInArray : positionArrayList) {
			if (position[0] == positionInArray[0] && position[1] == positionInArray[1]) included = true;
		}

		return included;
	}

	public int getWinnerPlayerNumberOrZero() {
		if (mPlayerOnePosition[1] == 9) return 1;
		if (mPlayerTwoPosition[1] == 1) return 2;
		return 0;
	}

	private class Node {
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
	}

	public List<int[]> getShortestPathToVictory(int playerNumber) { // null if none
		ArrayList<Node> openNodes = new ArrayList<>();
		ArrayList<Node> closedNodes = new ArrayList<>();

		Node baseNode = new Node(null, getPlayerPosition(playerNumber));
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

				if (aStarListContainsNodePositionWithLessF(successor, openNodes)) {
					// Do nothing
				}

				else if (!aStarListContainsNodePositionWithLessF(successor, closedNodes)) {
					openNodes.add(successor);
				}

			}

			closedNodes.add(q);

		}

		return null;
	}

	private List<int[]> getPathToNode(Node n) {
		List<int[]> path = new ArrayList<>();
		path.add(0, n.position);
		Node currentNode = n;
		while (currentNode.parent != null) {
			path.add(0, currentNode.parent.position);
			currentNode = currentNode.parent;
		}
		return path;
	}

	private boolean aStarListContainsNodePositionWithLessF(Node node, List<Node> list) {
		for (Node n : list) {
			if (n.position == node.position && n.getF() <= node.getF()) return true;
		}
		return false;
	}

	private int aStarEvaluateF(int playerNumber, int[] position) {
		if (playerNumber == 1) {
			return 9 - position[1];
		} else {
			return Math.abs(1 - position[1]);
		}
	}

	public int[] getPlayerPosition(int playerNumber) {
		if (playerNumber == 1)
			return mPlayerOnePosition;
		else
			return mPlayerTwoPosition;
	}


}
