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


	public Quoridor(@Nullable String gameID) {
		mGameID = gameID;
		assignPlayerPosition(1, 5, 1);
		assignPlayerPosition(2, 5, 9);
	}

	public Quoridor(@Nullable String gameID, JSONObject initialGameState) {
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
		List<int[]> possibleNextCoordinates = getPossibleNextCoordinates(playerNumber, false);
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

	public List<int[]> getPossibleNextCoordinates(int playerNumber, boolean ignoreOtherPlayerJump) {
		List<int[]> possibleNextCoordinates = new ArrayList<>();
		int playerX;
		int playerY;
		int otherPlayerX;
		int otherPlayerY;
		int otherPlayerNumber;

		if (playerNumber == 1) {
			playerX = mPlayerOnePosition[0];
			playerY = mPlayerOnePosition[1];
			otherPlayerX = mPlayerTwoPosition[0];
			otherPlayerY = mPlayerTwoPosition[1];
			otherPlayerNumber = 2;

		} else {
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
						possibleNextCoordinates.addAll(getPossibleNextCoordinates(otherPlayerNumber, true));
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
						possibleNextCoordinates.addAll(getPossibleNextCoordinates(otherPlayerNumber, true));
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
						possibleNextCoordinates.addAll(getPossibleNextCoordinates(otherPlayerNumber, true));
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
						possibleNextCoordinates.addAll(getPossibleNextCoordinates(otherPlayerNumber, true));
					} // Else do nothing
				} else {
					// Other player is not there, add position
					possibleNextCoordinates.add(new int[]{playerX + 1, playerY});
				}
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

}
