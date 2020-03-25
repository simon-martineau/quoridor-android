package simon.app.quoridor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.JsonReader;
import android.util.Log;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Quoridor {
	// Debug
	private static final String TAG = "QUORIDOR";

	// Appearance
	private int mTop, mLeft;
	private int mCellSize;
	private Color mGridColor;

	// Logic
	private int[] mPlayerOnePosition = new int[2];
	private int mPlayerOneWallsLeft = 10;
	private int[] mPlayerTwoPosition = new int[2];
	private int mPlayerTwoWallsLeft = 10;
	private List<int[]> mHorizontalWalls = new ArrayList<>();
	private List<int[]> mVerticalWalls = new ArrayList<>();



	private JSONObject mGameState;


	public Quoridor(int x, int y, int cellSize, Color gridColor, JSONObject initialGameState) {
		mTop = x;
		mLeft = y;
		mCellSize = cellSize;
		mGridColor = gridColor;
	}

	public void draw(Canvas canvas, JSONObject gameJSON) {

	}


	private void putGameState(JSONObject gameState) {

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

				JSONArray positionJSON = currentPlayer.getJSONArray("pos");
				for (int j = 0; j < positionJSON.length(); j++) {
					position[j] = positionJSON.getInt(j);
				}

				wallsLeft = currentPlayer.getInt("murs");

				if (i == 0) {
					mPlayerOnePosition = position;
					mPlayerOneWallsLeft = wallsLeft;
				} else {
					mPlayerTwoPosition = position;
					mPlayerTwoWallsLeft = wallsLeft;
				}
			}

		} catch	(Exception e) {
			Log.e(TAG, "parseJSON: Error parsing players");
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


}
