package simon.app.quoridor.WindowViews;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import simon.app.quoridor.Core.AppView;
import simon.app.quoridor.Core.Quoridor;
import simon.app.quoridor.Core.QuoridorException;
import simon.app.quoridor.Utils.Annotations.Asynchronous;
import simon.app.quoridor.CustomViews.GButton;
import simon.app.quoridor.CustomViews.GTitleView;
import simon.app.quoridor.CustomViews.GView;
import simon.app.quoridor.CustomViews.GModalView;
import simon.app.quoridor.CustomViews.GQuoridorView;
import simon.app.quoridor.R;
import simon.app.quoridor.WindowViews.WindowView;

public class GameView extends WindowView {
	//==============================================================================================
	// Constants
	//==============================================================================================

	/**
	 * Tag for logging
	 */
	private static final String TAG = "GameView";

	/**
	 * Base URL for the quoridor APi. Used by the OKHttp client
	 */
	private static final String API_BASE_URL = "https://python.gel.ulaval.ca/quoridor/api/";

	/**
	 * Suffix to the base url to play a move
	 */
	private static final String API_MAKE_MOVE_SUFFIX = "jouer/";

	/**
	 * Suffix to the base url to begin a game
	 */
	private static final String API_BEGIN_GAME_SUFFIX = "débuter/";

	/**
	 * Used as identification for the quoridor api
	 */
	private static final String IDUL = "simar86";

	/**
	 * In a wall placement, how far the finger has to move on the screen to move the wall by one
	 * unit
	 */
	private static final float WALL_UPDATE_STEP = 100;

	/**
	 * The default color to use for button background
	 */
	public static int DEFAULT_BUTTON_BACKGROUND_COLOR = Color.rgb(40, 40, 40);

	/**
	 * Default typeface used in drawText calls
	 */
	public static Typeface DEFAULT_TYPEFACE;

	/**
	 * Client used for server requests
	 */
	private final OkHttpClient httpClient = new OkHttpClient();

	/**
	 * Logical implementation of Quoridor. The game that is drawn to the canvas
	 */
	public Quoridor mGame;

	//==============================================================================================
	// Preferences
	//==============================================================================================
	private int mPawnColorPref;
	private int mEnemyPawnColorPref;
	private int mWallColorPref;
	private boolean mDrawPathPref;



	//==============================================================================================
	// Wall placement logic
	//==============================================================================================
	/**
	 * Wall placement: the last x coordinate of the finger in an ACTION_MOVE touch event.
	 */
	private float mLastTouchX;

	/**
	 * Wall placement: the last y coordinate of the finger in an ACTION_MOVE touch event.
	 */
	private float mLastTouchY;

	/**
	 * Wall placement: the current type of wall (HORIZONTAL or VERTICAL) to draw in the preview by
	 * the QuoridorView.
	 */
	private int mWallPreviewType;

	//==============================================================================================
	// Views
	//==============================================================================================

	// Quoridor
	/**
	 * View in charge of drawing the Quoridor game on the canvas. Also handles some logic regarding
	 * the user placing a wall. Has to be linked to mGame.
	 * @see GQuoridorView#linkQuoridorGame(Quoridor quoridor)
	 */
	public GQuoridorView mGQuoridorView;

	// Buttons
	/**
	 * Button to toggle between vertical/horizontal wall type during a wall placement
	 */
	GButton mToggleWallTypeButton;

	/**
	 * Button to begin a wall placement maneuver. Changes to a Cancel button during the wall placement
	 */
	GButton mPlaceWallButton;

	/**
	 * Button to confirm a wall placement maneuver
	 */
	GButton mConfirmWallButton;

	/**
	 * Button to begin a new game
	 */
	GButton mNewGameButton;

	/**
	 * Button to abandon the current match
	 */
	GButton mAbandonButton;

	/**
	 * Button to open settings menu
	 */
	GButton mSettingsButton;

	// Modal views
	/**
	 * Prompt to confirm match forfeiting
	 */
	GModalView mRestartConfirmModalView;

	// Title view
	/**
	 * Name of the app at the top of the screen
	 */
	GTitleView mGTitleView;


	//==============================================================================================
	// Audio
	//==============================================================================================

	/**
	 * Media player for the main track
	 */
	private MediaPlayer mBackgroundMusicPlayer;

	/**
	 * Sound pool used to play all the sound effects (excluding the main track)
	 * @see	#setUpAudio()
	 */
	private SoundPool mSoundPool;

	/**
	 * Sound played when the player wins
	 */
	private int mWinSoundId;

	/**
	 * Sound played when the player loses
	 */
	private int mLoseSoundId;

	/**
	 * Sound played when the moves his pawn
	 */
	private int mPawnMoveSoundId;

	/**
	 * Sound played the wall preview changes coordinates during a wall placement
	 */
	private int mWallMoveSoundId;

	/**
	 * Sound played when the wall is placed on the grid after a wall placement
	 */
	private int mWallPlaceSoundId;

	/**
	 * Sound played when toggling horizontal/vertical wall placement
	 */
	private int mSwitchWallTypeSoundId;

	/**
	 * Sound played when the "Place wall" button is pressed
	 */
	private int mBeginPlaceWallSoundId;

	/**
	 * Sound played when the abandon button is pressed
	 */
	private int mAbandonButtonSoundId;

	/**
	 * Sound played when the user tries to place a wall at an invalid location during wall placement
	 */
	private int mInvalidWallSoundId;


	//==============================================================================================
	// State logic
	//==============================================================================================
	/**
	 * Whether or not a wall placement is occuring
	 */
	public boolean placingWall = false;

	/**
	 * Whether or not the game is "paused". Used while waiting for server response.
	 */
	public boolean gamePaused = false;

	private long timePaused;
	private long MINIMUM_PAUSE_DELAY = 750;


	//==============================================================================================
	// Constructors
	//==============================================================================================
	public GameView(AppView appView) {
		super(appView);

		DEFAULT_TYPEFACE = Typeface.createFromAsset(appView.getContext().getAssets(), "fonts/8_bit_style.ttf");

		mGame = new Quoridor();
		fetchNewGameFromServer(API_BASE_URL + API_BEGIN_GAME_SUFFIX, IDUL);

		setUpAudio();
	}

	//==============================================================================================
	// Setup Methods
	//==============================================================================================

	/**
	 * Sets up audio required to run the window
	 */
	private void setUpAudio() {
		mBackgroundMusicPlayer = MediaPlayer.create(mAppView.getContext(), R.raw.game_track);
		mBackgroundMusicPlayer.setLooping(true);

		mSoundPool = new SoundPool.Builder()
				.setMaxStreams(10)
				.build();
		mWinSoundId = mSoundPool.load(mAppView.getContext(), R.raw.win, 1);
		mLoseSoundId = mSoundPool.load(mAppView.getContext(), R.raw.lose, 1);
		mPawnMoveSoundId = mSoundPool.load(mAppView.getContext(), R.raw.pawn_move, 1);
		mWallMoveSoundId = mSoundPool.load(mAppView.getContext(), R.raw.move_wall, 1);
		mWallPlaceSoundId = mSoundPool.load(mAppView.getContext(), R.raw.place_wall, 1);
		mSwitchWallTypeSoundId = mSoundPool.load(mAppView.getContext(), R.raw.switch_wall_type, 1);
		mBeginPlaceWallSoundId = mSoundPool.load(mAppView.getContext(), R.raw.begin_place_wall, 1);
		mAbandonButtonSoundId = mSoundPool.load(mAppView.getContext(), R.raw.abandon_button_sound, 1);
		mInvalidWallSoundId = mSoundPool.load(mAppView.getContext(), R.raw.invalid_wall, 1);
	}

	/**
	 * Sets up the all the window's views
	 */
	private void setUpViews() {
		Log.i(TAG, "setUpViews: mAppView.getWidth() = " + mAppView.getWidth());
		Log.i(TAG, "setUpViews: getWidth() = " + getWidth());
		retrievePreferences();

		mGViews.clear();

		mGTitleView = new GTitleView(this, "8 bit Quoridor", Color.GREEN, 184f, mAppView.getWidth());
		mGTitleView.setY(128);

		mGQuoridorView = new GQuoridorView(this, mGame, 50, mGTitleView.getBottom() + 128, getWidth());
		mGQuoridorView.setPlayerColor(1, mPawnColorPref);
		mGQuoridorView.setPlayerColor(2, mEnemyPawnColorPref);
		mGQuoridorView.setDrawPath(mDrawPathPref);
		mGQuoridorView.setWallColor(mWallColorPref);
		mGQuoridorView.setOnClickAction(new GQuoridorView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				if (!gamePaused && !placingWall) {
					int[] possibleCellCoordinates;
					possibleCellCoordinates = mGQuoridorView.getCellCorrespondingToTouch(x, y);
					if (possibleCellCoordinates != null)
						tryToMovePlayer(1, possibleCellCoordinates[0], possibleCellCoordinates[1]);
				}
			}
		});
		mGQuoridorView.linkQuoridorGame(mGame);

		mPlaceWallButton = new GButton(this, "Place a wall", 300, 150, 150, mGQuoridorView.getBottom() + 64, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.GREEN, true);
		mPlaceWallButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				if (!gamePaused) {
					if (!placingWall) {
						playSound(mBeginPlaceWallSoundId, 0.5f);
						mToggleWallTypeButton.setVisible(true);
						mConfirmWallButton.setVisible(true);
						mPlaceWallButton.setText("Cancel");
						mPlaceWallButton.setTextColor(Color.RED);
						Log.i(TAG, "onClick: placeWallButton visible:" + mPlaceWallButton.isVisible());
						initiateWallPlacement(Quoridor.HORIZONTAL);
					} else {
						mToggleWallTypeButton.setVisible(false);
						mConfirmWallButton.setVisible(false);
						cancelWallPlacement();
						mPlaceWallButton.setTextColor(Color.GREEN);
						mPlaceWallButton.setText("Place a wall");
						mToggleWallTypeButton.setText("Horizontal");
					}
				}
			}
		});

		mToggleWallTypeButton = new GButton(this, "Horizontal", 300, 150, 525, mGQuoridorView.getBottom() + 64, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.WHITE, true);
		mToggleWallTypeButton.setOnClickAction(new GView.onClickAction() {

			@Override
			public void onClick(int x, int y) {
				cancelWallPlacement();
				playSound(mSwitchWallTypeSoundId, 0.3f);
				if (mToggleWallTypeButton.getText().equals("Horizontal"))
				{
					mToggleWallTypeButton.setText("Vertical");
					initiateWallPlacement(Quoridor.VERTICAL);
				} else if (mToggleWallTypeButton.getText().equals("Vertical")) {
					mToggleWallTypeButton.setText("Horizontal");
					initiateWallPlacement(Quoridor.HORIZONTAL);
				}
			}
		});
		mToggleWallTypeButton.setVisible(false);


		mConfirmWallButton = new GButton(this, "Confirm", 300, 150, 900, mGQuoridorView.getBottom() + 64, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.GREEN, true);
		mConfirmWallButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				try {
					finalizeWallPlacement();
					mToggleWallTypeButton.setVisible(false);
					mConfirmWallButton.setVisible(false);
					mPlaceWallButton.setTextColor(Color.GREEN);
					mPlaceWallButton.setText("Place a wall");
					mToggleWallTypeButton.setText("Horizontal");
				} catch (QuoridorException e) {
					mGQuoridorView.setCustomMessageBlink(e.getMessage(), Color.RED, 64, 5, 3);
					playSound(mInvalidWallSoundId, 0.5f);
				}


			}
		});
		mConfirmWallButton.setVisible(false);

		mAbandonButton = new GButton(this, "Abandon", 300, 150, mAppView.getWidth() - 450, mAppView.getHeight() - 300, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.RED, true);
		mAbandonButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				playSound(mAbandonButtonSoundId, 0.5f);
				mRestartConfirmModalView.setVisible(true);
			}
		});

		mNewGameButton = new GButton(this, "New Game", 300, 150, mAppView.getWidth() - 450, mAppView.getHeight() - 300, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.GREEN, true);
		mNewGameButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				startNewGame();
				mNewGameButton.setVisible(false);
			}
		});
		mNewGameButton.setVisible(false);

		mRestartConfirmModalView = new GModalView(this, "Confirm match forfeit?", 100, 600);
		mRestartConfirmModalView.addGButton(Color.RED, DEFAULT_BUTTON_BACKGROUND_COLOR, 400, 150, "Yes",
				new GView.onClickAction() {
					@Override
					public void onClick(int x, int y) {
						playSound(mLoseSoundId, 0.5f);
						if (placingWall) mPlaceWallButton.performClick(x, y);
						startNewGame();
						mRestartConfirmModalView.setVisible(false);
					}
				});

		mRestartConfirmModalView.addGButton(Color.GREEN, DEFAULT_BUTTON_BACKGROUND_COLOR, 400, 150, "No",
				new GView.onClickAction() {
					@Override
					public void onClick(int x, int y) {
						playSound(mWallMoveSoundId, 0.5f);
						mRestartConfirmModalView.setVisible(false);
					}
				});
		mRestartConfirmModalView.setX(mAppView.getWidth() / 2 - mRestartConfirmModalView.getWidth() / 2);
		mRestartConfirmModalView.setY(mAppView.getHeight() / 2 - mRestartConfirmModalView.getHeight() / 2 - 200);
		mRestartConfirmModalView.setVisible(false);

		mSettingsButton = new GButton(this, "Main menu", 300, 150, 150, getHeight() - 300, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.WHITE, true);
		mSettingsButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				getAppView().swapToMainMenuView();
			}
		});

		refreshHover();

	}

	/**
	 * Retrieves the user preferences and updates members accordingly
	 */
	private void retrievePreferences() {
		SharedPreferences prefs = getAppView().getSharedPreferences();
		mPawnColorPref = prefs.getInt("pawn_color", SettingsView.DEFAULT_PAWN_COLOR);
		mEnemyPawnColorPref = prefs.getInt("enemy_pawn_color", SettingsView.DEFAULT_ENEMY_PAWN_COLOR);
		mWallColorPref = prefs.getInt("wall_color", SettingsView.DEFAULT_WALL_COLOR);
		mDrawPathPref = prefs.getBoolean("draw_path", SettingsView.DEFAULT_DRAW_PATH);
	}

	//==============================================================================================
	// Override Methods
	//==============================================================================================

	/**
	 * Draws the window to the canvas
	 * @param canvas The canvas to draw on
	 */
	@Override
	public void draw(Canvas canvas) {

		for (int i = mGViews.size() - 1; i >= 0; i--) {
			mGViews.get(i).draw(canvas);
		}

		if (gamePaused)
			mGQuoridorView.setBlink(false);
		else
			mGQuoridorView.setBlink(true);
	}

	/**
	 * Called when the window is activated by the AppView.
	 */
	@Override
	public void onActivate() {
		super.onActivate();
		setUpViews();
		mBackgroundMusicPlayer.start();
	}

	/**
	 * Called when the window is deactivated by the AppView.
	 */
	@Override
	public void onDeactivate() {
		super.onDeactivate();
		mBackgroundMusicPlayer.pause();
	}

	/**
	 * Routed here by the AppView on a surfaceChanged
	 * @see AppView#surfaceChanged(SurfaceHolder, int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setUpViews();
	}

	/**
	 * Routed here by the AppView on a touch event if this window is activated
	 * @param event The touch event routed from the AppView
	 * @return true
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();


		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				dispatchTouchToViews(x, y);
				mLastTouchX = x;
				mLastTouchY = y;
				break;

			case MotionEvent.ACTION_MOVE: {
				if (placingWall) {
					float posX = event.getX();
					float posY = event.getY();

					if (posX - mLastTouchX > WALL_UPDATE_STEP) {
						mGQuoridorView.offsetWallPreview(mWallPreviewType, 1, 0);
						mLastTouchX = posX - 10;
						playSound(mWallMoveSoundId, 0.3f);

					} else if (posX - mLastTouchX < WALL_UPDATE_STEP * -1) {
						mGQuoridorView.offsetWallPreview(mWallPreviewType, -1, 0);
						mLastTouchX = posX + 10;
						playSound(mWallMoveSoundId, 0.3f);
					}
					if (posY - mLastTouchY > WALL_UPDATE_STEP) {
						mGQuoridorView.offsetWallPreview(mWallPreviewType, 0, -1);
						mLastTouchY = posY - 10;
						playSound(mWallMoveSoundId, 0.3f);
					} else if (posY - mLastTouchY < WALL_UPDATE_STEP * -1) {
						mGQuoridorView.offsetWallPreview(mWallPreviewType, 0, 1);
						mLastTouchY = posY + 10;
						playSound(mWallMoveSoundId, 0.3f);
					}
				}
				break;
			}

		}

		return true;
	}

	//==============================================================================================
	// Game flow methods
	//==============================================================================================

	/**
	 * Moves player in mGame if coordinates are a valid move. Does nothing otherwise.
	 * @param playerNumber The player to move (1 or 2)
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	public void tryToMovePlayer(int playerNumber, int x, int y) {
		try {
			mGame.requestPlayerMovement(playerNumber, x, y);
			playSound(mPawnMoveSoundId, 0.5f);
			gamePaused = true;
			postMoveAndGetNewState(API_BASE_URL + API_MAKE_MOVE_SUFFIX, mGame.mGameID, mGame.mLastMoveType, mGame.mLastMoveCoordinates);
		} catch (QuoridorException e) {
			Log.i(TAG, "tryToMovePlayer: QuoridorException");
		}
	}

	/**
	 * Places wall at specified position in mGame if the wall placement is invalid.
	 * @param playerNumber The player that places the wall (1 or 2)
	 * @param wallType The type of wall to place (HORIZONTAL or VERTICAL)
	 * @param x The x coordinate
	 * @param y The y coordinate
	 * @throws QuoridorException If the wall placement is invalid. mGame will therefore not be modified.
	 */
	public void tryToPlaceWall(int playerNumber, int wallType, int x, int y) throws QuoridorException {

		mGame.requestWallPlacement(playerNumber, wallType, x, y);

		mGQuoridorView.resetHoverPositions();
		mGQuoridorView.hoverCells(mGame.getPossibleNextCoordinates(1, false, null));
	}

	/**
	 * Begins a wall placement state for the GameView.
	 * @param wallType The type of wall to place (HORIZONTAL or VERTICAL)
	 */
	public void initiateWallPlacement(int wallType) {
		placingWall = true;
		mWallPreviewType = wallType;
		mGQuoridorView.setWallPreview(wallType, 5, 5);
	}

	/**
	 * Attempts to place a wall accordingly to the wall placement state. If the wall placement is valid,
	 * mGame will be modified and the wall placement state will end.
	 * @throws QuoridorException if the wall placement is invalid. mGame will not be modified and the
	 * wall placement state will remain active.
	 */
	public void finalizeWallPlacement() throws QuoridorException {
		if (mGQuoridorView.isWallPreviewInvalid()) {
			throw new QuoridorException("Illegal wall placement!");
		}

		int[] coordinates = mGQuoridorView.getWallPreviewCoordinates();

		// TODO: Make this concise and avoid state check redundancy
		try {
			tryToPlaceWall(1, mWallPreviewType,coordinates[0], coordinates[1]);
			playSound(mWallPlaceSoundId, 0.8f);
			gamePaused = true;
			postMoveAndGetNewState(API_BASE_URL + API_MAKE_MOVE_SUFFIX, mGame.mGameID, mGame.mLastMoveType, mGame.mLastMoveCoordinates);
		} catch (QuoridorException e) {
			throw new QuoridorException("Could not place wall!");
		}

		cancelWallPlacement();
	}

	/**
	 * Ends the wall placement state.
	 */
	public void cancelWallPlacement() {
		placingWall = false;
		mWallPreviewType = 0;
		mGQuoridorView.clearWallPreview();
	}

	/**
	 * Creates new game according to the server response passed as argument
	 * @param serverResponse The string containing JSON of the game state and gameID
	 */
	private void setNewGame(String serverResponse) {
		JSONObject serverResponseJSON;
		JSONObject state;
		String gameID;
		try {
			serverResponseJSON = new JSONObject(serverResponse);
			gameID = serverResponseJSON.getString("id");
			state = serverResponseJSON.getJSONObject("état");
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		mGame = new Quoridor(gameID, state);
		mGQuoridorView.linkQuoridorGame(mGame);
	}

	/**
	 * Changes mGame according to the game sate passed as argument
	 * @param serverResponse The string containing JSON of the game state
	 */
	private void setGameState(String serverResponse) {
		JSONObject serverResponseJSON;
		JSONObject state;
		try {
			serverResponseJSON = new JSONObject(serverResponse);
			state = serverResponseJSON.getJSONObject("état");
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		mGame.putGameState(state);
	}

	/**
	 * Initiates game winning sequence if a player has won, else does nothing
	 */
	public void checkForWin() {
		int possibleWinner = mGame.getWinnerPlayerNumberOrZero();
		if (possibleWinner == 1) initGameWin();
		else if (possibleWinner == 2) initGameLoss();
	}

	/**
	 * Modifies the GUI for a game loss
	 */
	public void initGameLoss() {
		gamePaused = true;
		playSound(mLoseSoundId, 0.6f);
		mGQuoridorView.setConsoleMessageColor(Color.RED);
		mGQuoridorView.setConsoleMessage("YOU LOST!");
		mGQuoridorView.setBorderBlink(Color.RED, 8, 5);
		mAbandonButton.setVisible(false);
		mNewGameButton.setVisible(true);

	}

	/**
	 * Modifies the GUI for a game win
	 */
	public void initGameWin() {
		gamePaused = true;
		playSound(mWinSoundId, 0.6f);
		mGQuoridorView.setConsoleMessageColor(Color.GREEN);
		mGQuoridorView.setConsoleMessage("YOU WON!");
		mGQuoridorView.setBorderBlink(Color.GREEN, 8, 3);
		mAbandonButton.setVisible(false);
		mNewGameButton.setVisible(true);
	}

	/**
	 * Fetches a new game from the server and modifies the UI accordingly
	 */
	public void startNewGame() {

		fetchNewGameFromServer(API_BASE_URL + API_BEGIN_GAME_SUFFIX, IDUL);
		mAbandonButton.setVisible(true);
		mGQuoridorView.setConsoleMessage("");
	}

	//==============================================================================================
	// Server request methods
	//==============================================================================================

	/**
	 * Queues a post request to the server to start a new game.
	 * @param targetURL The URL to make the request to
	 * @param idul The identifier used for the server
	 * @callback Starts a new game
	 */
	@Asynchronous
	public void fetchNewGameFromServer(String targetURL, String idul) {

		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("idul", idul)
				.build();

		final Request request = new Request.Builder()
				.url(targetURL)
				.method("POST", body)
				.build();


		httpClient.newCall(request)
				.enqueue(new Callback() {
					@Override
					public void onFailure(@NotNull Call call, @NotNull IOException e) {
						e.printStackTrace();
					}

					@Override
					public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
						if (!response.isSuccessful()) {
							throw new IOException("Error : " + response);
						}
						String data =  Objects.requireNonNull(response.body()).string();
						// Wait for mGQuoridorView to be initialised
						try {
							while (true) {
								if (mGQuoridorView != null) {
									setNewGame(data);
									break;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						gamePaused = false;
						refreshHover();
					}
				});
	}


	/**
	 * Queues a post request to the server to make a move and get a new state
	 * @param targetURL The URL to make the request to
	 * @param gameID The gameID associated with the current game
	 * @param moveType The type of move ('D' for move, 'MH' for horizontal wall, 'MV' for vertical wall)
	 * @param position The position (x, y) of the move.
	 * @callback Updates mGame with the new game state
	 */
	@Asynchronous
	public void postMoveAndGetNewState(String targetURL, String gameID, String moveType, String position) {
		// Temp
		mGQuoridorView.setDrawPath(false);

		RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
				.addFormDataPart("id", gameID)
				.addFormDataPart("type", moveType)
				.addFormDataPart("pos", position)
				.build();
		Request request = new Request.Builder()
				.url(targetURL)
				.method("POST", body)
				.build();

		httpClient.newCall(request)
				.enqueue(new Callback() {
					@Override
					public void onFailure(@NotNull Call call, @NotNull IOException e) {
						e.printStackTrace();
					}

					@Override
					public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
						if (!response.isSuccessful()) {
							throw new IOException("Error : " + response);
						}

						String data =  Objects.requireNonNull(response.body()).string();
						setGameState(data);
						gamePaused = false;
						mGQuoridorView.setDrawPath(mDrawPathPref);
						refreshHover();
						checkForWin();
					}
				});
	}

	//==============================================================================================
	// Graphic/Audio methods
	//==============================================================================================

	/**
	 * Plays a sound using mSoundPool
	 * @param soundId The SoundPool ID of the sound to play
	 * @param intensity The intensity of the sound
	 */
	private void playSound(int soundId, float intensity) {
		mSoundPool.play(soundId, intensity, intensity, 1, 0, 1f);
	}

	/**
	 * Refreshes the blinking cells representing possible moves according to the current game state
	 */
	public void refreshHover() {
		mGQuoridorView.resetHoverPositions();
		mGQuoridorView.hoverCells(mGame.getPossibleNextCoordinates(1, false, null));
	}

}
