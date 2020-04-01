package simon.app.quoridor.Core;

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
import java.util.Collections;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import simon.app.quoridor.CustomViews.GButton;
import simon.app.quoridor.CustomViews.GTitleView;
import simon.app.quoridor.CustomViews.GView;
import simon.app.quoridor.CustomViews.ModalView;
import simon.app.quoridor.CustomViews.QuoridorView;
import simon.app.quoridor.R;

public class GameView extends WindowView{
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
	private static int DEFAULT_BUTTON_BACKGROUND_COLOR = Color.rgb(40, 40, 40);

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
	 * @see QuoridorView#linkQuoridorGame(Quoridor quoridor)
	 */
	public QuoridorView mQuoridorView;

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

	// Modal views
	/**
	 * Prompt to confirm match forfeiting
	 */
	ModalView mRestartConfirmModalView;

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

	public GameView(AppView appView, String id, String state) {
		super(appView);

		DEFAULT_TYPEFACE = Typeface.createFromAsset(appView.getContext().getAssets(), "fonts/8_bit_style.ttf");

		JSONObject stateJSON;
		try {
			stateJSON = new JSONObject(state);
			mGame = new Quoridor(id, stateJSON);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		setUpAudio();
	}


	//==============================================================================================
	// Methods
	//==============================================================================================

	private void playSound(int soundId, float intensity) {
		mSoundPool.play(soundId, intensity, intensity, 1, 0, 1f);
	}



	/**
	 * Registers a view in mGViews. GViews is sorted by zIndex after each call
	 * @param gView The view to register
	 */
	public void registerGView(GView gView) {
		mGViews.add(gView);
		sortViews();
	}

	public void sortViews() {
		Collections.sort(mGViews);
		Collections.reverse(mGViews);
	}

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

	private void setUpViews() {
		Log.i(TAG, "setUpViews: mAppView.getWidth() = " + mAppView.getWidth());
		Log.i(TAG, "setUpViews: getWidth() = " + getWidth());

		mGTitleView = new GTitleView(this, "8 bit Quoridor", Color.GREEN, 184f, mAppView.getWidth());
		mGTitleView.setY(128);

		mQuoridorView = new QuoridorView(this, mGame, 50, mGTitleView.getBottom() + 128, getWidth());
		mQuoridorView.setOnClickAction(new QuoridorView.onClickAction() {
			@Override
			public void onClick(WindowView windowView, int x, int y) {
				if (!gamePaused && !placingWall) {
					int[] possibleCellCoordinates;
					possibleCellCoordinates = mQuoridorView.getCellCorrespondingToTouch(x, y);
					if (possibleCellCoordinates != null)
						tryToMovePlayer(1, possibleCellCoordinates[0], possibleCellCoordinates[1]);
				}
			}
		});
		mQuoridorView.linkQuoridorGame(mGame);

		mPlaceWallButton = new GButton(this, "Place a wall", 300, 150, 100, mQuoridorView.getBottom() + 64, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.GREEN);
		mPlaceWallButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(WindowView windowView, int x, int y) {
				if (!gamePaused) {
					if (!placingWall) {
						playSound(mBeginPlaceWallSoundId, 0.5f);
						mToggleWallTypeButton.setVisible(true);
						mConfirmWallButton.setVisible(true);
						initiateWallPlacement(Quoridor.HORIZONTAL);
						mPlaceWallButton.setText("Cancel");
						mPlaceWallButton.setTextColor(Color.RED);
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

		mToggleWallTypeButton = new GButton(this, "Horizontal", 300, 150, 475, mQuoridorView.getBottom() + 64, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.WHITE);
		mToggleWallTypeButton.setOnClickAction(new GView.onClickAction() {

			@Override
			public void onClick(WindowView windowView, int x, int y) {
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


		mConfirmWallButton = new GButton(this, "Confirm", 300, 150, 850, mQuoridorView.getBottom() + 64, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.GREEN);
		mConfirmWallButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(WindowView windowView, int x, int y) {
				try {
					finalizeWallPlacement();
					mToggleWallTypeButton.setVisible(false);
					mConfirmWallButton.setVisible(false);
					mPlaceWallButton.setTextColor(Color.GREEN);
					mPlaceWallButton.setText("Place a wall");
					mToggleWallTypeButton.setText("Horizontal");
				} catch (QuoridorException e) {
					mQuoridorView.setCustomMessageBlink(e.getMessage(), Color.RED, 64, 5, 3);
					playSound(mInvalidWallSoundId, 0.5f);
				}


			}
		});
		mConfirmWallButton.setVisible(false);

		mAbandonButton = new GButton(this, "Abandon", 300, 150, mAppView.getWidth() - 450, mAppView.getHeight() - 300, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.RED);
		mAbandonButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(WindowView windowView, int x, int y) {
				playSound(mAbandonButtonSoundId, 0.5f);
				mRestartConfirmModalView.setVisible(true);
			}
		});

		mNewGameButton = new GButton(this, "New Game", 300, 150, mAppView.getWidth() - 450, mAppView.getHeight() - 300, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.GREEN);
		mNewGameButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(WindowView windowView, int x, int y) {
				startNewGame();
				mNewGameButton.setVisible(false);
			}
		});
		mNewGameButton.setVisible(false);

		mRestartConfirmModalView = new ModalView(this, "Confirm match forfeit?", 100, 600);
		mRestartConfirmModalView.addGButton(Color.RED, DEFAULT_BUTTON_BACKGROUND_COLOR, 400, 150, "Yes",
				new GView.onClickAction() {
					@Override
					public void onClick(WindowView windowView, int x, int y) {
						playSound(mLoseSoundId, 0.5f);
						startNewGame();
						mRestartConfirmModalView.setVisible(false);
					}
				});

		mRestartConfirmModalView.addGButton(Color.GREEN, DEFAULT_BUTTON_BACKGROUND_COLOR, 400, 150, "No",
				new GView.onClickAction() {
					@Override
					public void onClick(WindowView windowView, int x, int y) {
						playSound(mWallMoveSoundId, 0.5f);
						mRestartConfirmModalView.setVisible(false);
					}
				});
		mRestartConfirmModalView.setX(mAppView.getWidth() / 2 - mRestartConfirmModalView.getWidth() / 2);
		mRestartConfirmModalView.setY(mAppView.getHeight() / 2 - mRestartConfirmModalView.getHeight() / 2 - 200);
		mRestartConfirmModalView.setVisible(false);

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		for (int i = mGViews.size() - 1; i >= 0; i--) {
			mGViews.get(i).draw(canvas);
		}

		if (gamePaused)
			mQuoridorView.setBlink(false);
		else
			mQuoridorView.setBlink(true);
	}

	@Override
	public void onActivate() {
		super.onActivate();
		mBackgroundMusicPlayer.start();
	}

	@Override
	public void onDeactivate() {
		super.onDeactivate();
		mBackgroundMusicPlayer.pause();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setUpViews();
	}

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
						mQuoridorView.offsetWallPreview(mWallPreviewType, 1, 0);
						mLastTouchX = posX - 10;
						playSound(mWallMoveSoundId, 0.3f);

					} else if (posX - mLastTouchX < WALL_UPDATE_STEP * -1) {
						mQuoridorView.offsetWallPreview(mWallPreviewType, -1, 0);
						mLastTouchX = posX + 10;
						playSound(mWallMoveSoundId, 0.3f);
					}
					if (posY - mLastTouchY > WALL_UPDATE_STEP) {
						mQuoridorView.offsetWallPreview(mWallPreviewType, 0, -1);
						mLastTouchY = posY - 10;
						playSound(mWallMoveSoundId, 0.3f);
					} else if (posY - mLastTouchY < WALL_UPDATE_STEP * -1) {
						mQuoridorView.offsetWallPreview(mWallPreviewType, 0, 1);
						mLastTouchY = posY + 10;
						playSound(mWallMoveSoundId, 0.3f);
					}
				}
				break;
			}

		}

		return true;
	}

	public void tryToMovePlayer(int playerNumber, int x, int y) {
		// Logic
		try {
			mGame.requestPlayerMovement(playerNumber, x, y);
			playSound(mPawnMoveSoundId, 0.5f);
			gamePaused = true;
			postMoveAndGetNewState(API_BASE_URL + API_MAKE_MOVE_SUFFIX, mGame.mGameID, mGame.mLastMoveType, mGame.mLastMoveCoordinates);
		} catch (QuoridorException e) {
			Log.i(TAG, "tryToMovePlayer: QuoridorException");
		}
	}

	public void refreshHover() {
		mQuoridorView.resetHoverPositions();
		// mQuoridorView.hoverCells(mGame.getShortestPathToVictory(1));
		mQuoridorView.hoverCells(mGame.getPossibleNextCoordinates(1, false, null));
	}

	public void initiateWallPlacement(int wallType) {
		placingWall = true;
		mWallPreviewType = wallType;
		mQuoridorView.setWallPreview(wallType, 5, 5);

	}

	public void finalizeWallPlacement() throws QuoridorException {
		if (mQuoridorView.isWallPreviewInvalid()) {
			throw new QuoridorException("Illegal wall placement!");
		}

		int[] coordinates = mQuoridorView.getWallPreviewCoordinates();

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

	public void tryToPlaceWall(int playerNumber, int wallType, int x, int y) throws QuoridorException {

		mGame.requestWallPlacement(playerNumber, wallType, x, y);

		mQuoridorView.resetHoverPositions();
		mQuoridorView.hoverCells(mGame.getPossibleNextCoordinates(1, false, null));

	}

	public void cancelWallPlacement() {
		placingWall = false;
		mWallPreviewType = 0;
		mQuoridorView.clearWallPreview();
	}

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
		mQuoridorView.linkQuoridorGame(mGame);
	}

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
						setNewGame(data);
						gamePaused = false;
						refreshHover();
					}
				});
	}

	public void postMoveAndGetNewState(String targetURL, String gameID, String moveType, String position) {

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
						refreshHover();
						checkForWin();
					}
				});
	}

	public void checkForWin() {
		int possibleWinner = mGame.getWinnerPlayerNumberOrZero();
		if (possibleWinner == 1) initGameWin();
		else if (possibleWinner == 2) initGameLoss();
	}

	public void initGameLoss() {
		gamePaused = true;
		playSound(mLoseSoundId, 0.6f);
		mQuoridorView.setConsoleMessageColor(Color.RED);
		mQuoridorView.setConsoleMessage("YOU LOST!");
		mQuoridorView.setBorderBlink(Color.RED, 8, 5);
		mAbandonButton.setVisible(false);
		mNewGameButton.setVisible(true);

	}

	public void initGameWin() {
		gamePaused = true;
		playSound(mWinSoundId, 0.6f);
		mQuoridorView.setConsoleMessageColor(Color.GREEN);
		mQuoridorView.setConsoleMessage("YOU WON!");
		mQuoridorView.setBorderBlink(Color.GREEN, 8, 3);
		mAbandonButton.setVisible(false);
		mNewGameButton.setVisible(true);
	}

	public void startNewGame() {

		fetchNewGameFromServer(API_BASE_URL + API_BEGIN_GAME_SUFFIX, IDUL);
		mAbandonButton.setVisible(true);
		mQuoridorView.setConsoleMessage("");
	}

}