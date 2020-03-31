package simon.app.quoridor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
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

	// Threading
	/**
	 * Thread from which to run the game
	 */
	public GameThread mGameThread;

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

	/**
	 * This is where all the views are registered. TODO: Automatically register the views
	 */
	List<GView> mGViews = new ArrayList<>();

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

	/**
	 * Whether or not a ModalView is active (visible)
	 */
	public boolean modal = false;


	//==============================================================================================
	// Constructors
	//==============================================================================================

	public GameView(Context context) {
		super(context);

		getHolder().addCallback(this);
		fetchNewGameFromServer(API_BASE_URL + API_BEGIN_GAME_SUFFIX, IDUL);

		DEFAULT_TYPEFACE = Typeface.createFromAsset(getContext().getAssets(), "fonts/8_bit_style.ttf");

		setUpAudio();

		mGame = new Quoridor();
		setFocusable(true);
	}

	public GameView(Context context, String id, String state) {
		super(context);

		getHolder().addCallback(this);

		JSONObject stateJSON;
		try {
			stateJSON = new JSONObject(state);
			mGame = new Quoridor(id, stateJSON);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		setUpAudio();

		setFocusable(true);
	}

	//==============================================================================================
	// Methods
	//==============================================================================================

	private void playSound(int soundId, float intensity) {
		mSoundPool.play(soundId, intensity, intensity, 1, 0, 1f);
	}

	private void setUpAudio() {
		mBackgroundMusicPlayer = MediaPlayer.create(getContext(), R.raw.game_track);
		mBackgroundMusicPlayer.setLooping(true);

		mSoundPool = new SoundPool.Builder()
				.setMaxStreams(10)
				.build();
		mWinSoundId = mSoundPool.load(getContext(), R.raw.win, 1);
		mLoseSoundId = mSoundPool.load(getContext(), R.raw.lose, 1);
		mPawnMoveSoundId = mSoundPool.load(getContext(), R.raw.pawn_move, 1);
		mWallMoveSoundId = mSoundPool.load(getContext(), R.raw.move_wall, 1);
		mWallPlaceSoundId = mSoundPool.load(getContext(), R.raw.place_wall, 1);
		mSwitchWallTypeSoundId = mSoundPool.load(getContext(), R.raw.switch_wall_type, 1);
		mBeginPlaceWallSoundId = mSoundPool.load(getContext(), R.raw.begin_place_wall, 1);
		mAbandonButtonSoundId = mSoundPool.load(getContext(), R.raw.abandon_button_sound, 1);
		mInvalidWallSoundId = mSoundPool.load(getContext(), R.raw.invalid_wall, 1);

	}


	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		for (GView gView : mGViews) {
			gView.draw(canvas);
		}

		if (gamePaused)
			mQuoridorView.setBlink(false);
		else
			mQuoridorView.setBlink(true);


	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mBackgroundMusicPlayer.start();

		mGTitleView = new GTitleView("8 bit Quoridor", Color.GREEN, 184f, getWidth());
		mGTitleView.setY(128);

		mQuoridorView = new QuoridorView(mGame, 50, mGTitleView.getBottom() + 128, width);
		mQuoridorView.setOnClickAction(new QuoridorView.onClickAction() {
			@Override
			public void onClick(GameView gameView, int x, int y) {
				if (!gamePaused && !placingWall) {
					int[] possibleCellCoordinates;
					possibleCellCoordinates = mQuoridorView.getCellCorrespondingToTouch(x, y);
					if (possibleCellCoordinates != null)
						tryToMovePlayer(1, possibleCellCoordinates[0], possibleCellCoordinates[1]);
				}
			}
		});
		mQuoridorView.hoverCells(mGame.getPossibleNextCoordinates(1, false, null));

		mPlaceWallButton = new GButton("Place a wall", 300, 150, 100, mQuoridorView.getBottom() + 64, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.GREEN);
		mPlaceWallButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(GameView gameView, int x, int y) {
				if (!gamePaused) {
					if (!gameView.placingWall) {
						playSound(mBeginPlaceWallSoundId, 0.5f);
						mToggleWallTypeButton.setVisible(true);
						mConfirmWallButton.setVisible(true);
						gameView.initiateWallPlacement(Quoridor.HORIZONTAL);
						mPlaceWallButton.setText("Cancel");
						mPlaceWallButton.setTextColor(Color.RED);
					} else {
						mToggleWallTypeButton.setVisible(false);
						mConfirmWallButton.setVisible(false);
						gameView.cancelWallPlacement();
						mPlaceWallButton.setTextColor(Color.GREEN);
						mPlaceWallButton.setText("Place a wall");
						mToggleWallTypeButton.setText("Horizontal");
					}
				}
			}
		});

		mToggleWallTypeButton = new GButton("Horizontal", 300, 150, 475, mQuoridorView.getBottom() + 64, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.WHITE);
		mToggleWallTypeButton.setOnClickAction(new GView.onClickAction() {

			@Override
			public void onClick(GameView gameView, int x, int y) {
				gameView.cancelWallPlacement();
				playSound(mSwitchWallTypeSoundId, 0.3f);
				if (mToggleWallTypeButton.getText().equals("Horizontal"))
				{
					mToggleWallTypeButton.setText("Vertical");
					gameView.initiateWallPlacement(Quoridor.VERTICAL);
				} else if (mToggleWallTypeButton.getText().equals("Vertical")) {
					mToggleWallTypeButton.setText("Horizontal");
					gameView.initiateWallPlacement(Quoridor.HORIZONTAL);
				}
			}
		});
		mToggleWallTypeButton.setVisible(false);


		mConfirmWallButton = new GButton("Confirm", 300, 150, 850, mQuoridorView.getBottom() + 64, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.GREEN);
		mConfirmWallButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(GameView gameView, int x, int y) {
				try {
					gameView.finalizeWallPlacement();
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

		mAbandonButton = new GButton("Abandon", 300, 150, getWidth() - 450, getHeight() - 300, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.RED);
		mAbandonButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(GameView gameView, int x, int y) {
				playSound(mAbandonButtonSoundId, 0.5f);
				mRestartConfirmModalView.setVisible(true);
				modal = true;
			}
		});

		mNewGameButton = new GButton("New Game", 300, 150, getWidth() - 450, getHeight() - 300, DEFAULT_BUTTON_BACKGROUND_COLOR, Color.GREEN);
		mNewGameButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(GameView gameView, int x, int y) {
				gameView.startNewGame();
				mNewGameButton.setVisible(false);
			}
		});
		mNewGameButton.setVisible(false);

		mRestartConfirmModalView = new ModalView("Confirm match forfeit?", 100, 600);
		mRestartConfirmModalView.setFreezeView(new GFreezeView(0, 0, getWidth(), getHeight(), Color.WHITE, 50));
		mRestartConfirmModalView.addGButton(Color.RED, DEFAULT_BUTTON_BACKGROUND_COLOR, 400, 150, "Yes",
				new GView.onClickAction() {
					@Override
					public void onClick(GameView gameView, int x, int y) {
						playSound(mLoseSoundId, 0.5f);
						startNewGame();
						modal = false;
						mRestartConfirmModalView.setVisible(false);
					}
				});

		mRestartConfirmModalView.addGButton(Color.GREEN, DEFAULT_BUTTON_BACKGROUND_COLOR, 400, 150, "No",
				new GView.onClickAction() {
					@Override
					public void onClick(GameView gameView, int x, int y) {
						playSound(mWallMoveSoundId, 0.5f);
						modal = false;
						mRestartConfirmModalView.setVisible(false);
					}
				});
		mRestartConfirmModalView.setX(getWidth() / 2 - mRestartConfirmModalView.getWidth() / 2);
		mRestartConfirmModalView.setY(getHeight() / 2 - mRestartConfirmModalView.getHeight() / 2 - 200);
		mRestartConfirmModalView.setVisible(false);



		// Register views
		mGViews.add(mGTitleView);
		mGViews.add(mPlaceWallButton);
		mGViews.add(mToggleWallTypeButton);
		mGViews.add(mConfirmWallButton);
		mGViews.add(mNewGameButton);
		mGViews.add(mAbandonButton);
		mGViews.add(mQuoridorView);
		mGViews.add(mRestartConfirmModalView);

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mGameThread = new GameThread(getHolder(), this);
		mGameThread.setRunning(true);
		mGameThread.start();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mBackgroundMusicPlayer.pause();
		boolean retry = true;
		while (retry) {
			try {
				mGameThread.setRunning(false);
				mGameThread.join();
				retry = false;
				Log.i("GameThread", "surfaceDestroyed: thread killed");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void dispatchTouchToViews(int x, int y) {
		for (GView gView : mGViews) {

			if ((gView.isInRect(x, y) && !modal) || (gView instanceof ModalView)) {
				gView.performClick(this, x, y);
				return;
			}
		}
	}

	@SuppressLint("ClickableViewAccessibility")
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
