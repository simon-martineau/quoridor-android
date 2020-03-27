package simon.app.quoridor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
	private static final String TAG = "GameView";
	// Network
	private final OkHttpClient httpClient = new OkHttpClient();
	public static final String apiBaseUrl = "https://python.gel.ulaval.ca/quoridor/api/";

	// Threading
	public GameThread mGameThread;

	// Logic
	public Quoridor mGame;

	// For wall placement
	private final float wallUpdateStep = 100;
	private float mLastTouchX;
	private float mLastTouchY;
	private int mWallPreviewType;


	// Graphics
	private int mSurfaceWidth, mSurfaceHeight;
	public QuoridorView mQuoridorView;

	// Colors
	private int mButtonBackgroundColor = Color.rgb(40, 40, 40);

	// Buttons
	GButton mToggleWallTypeButton;
	GButton mPlaceWallButton;
	GButton mConfirmWallButton;
	GButton mNewGameButton;
	List<GButton> mGButtons = new ArrayList<>();

	// State
	public boolean placingWall = false;
	public boolean gamePaused = false;

	// Audio
	private SoundPool mSoundPool;
	private int mWinSoundId;
	private int mLoseSoundId;
	private int mPawnMoveSoundId;
	private int mWallMoveSoundId;
	private int mWallPlaceSoundId;

	private MediaPlayer mBackgroundMusicPlayer;

	// Temp
	String message = "";




	public GameView(Context context) {
		super(context);

		getHolder().addCallback(this);
		fetchNewGameFromServer(apiBaseUrl + "débuter/", "simar86");


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

	}

	public void update() {

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		if (gamePaused) mQuoridorView.setBlink(false);
		else mQuoridorView.setBlink(true);

		mQuoridorView.draw(canvas, mGame);

		// Temp
		Paint textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTextSize(48);
		// canvas.drawText("DEBUG: gamePause = " + gamePaused, 400, mSurfaceHeight - 150, textPaint);
		// canvas.drawText("DEBUG: message = " + message, 400, mSurfaceHeight - 50, textPaint);

		for (GButton gButton : mGButtons) {
			gButton.draw(canvas);
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mBackgroundMusicPlayer.start();

		mSurfaceWidth = width;
		mSurfaceHeight = height;

		mQuoridorView = new QuoridorView(mSurfaceWidth);
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

		mQuoridorView.hoverCells(mGame.getPossibleNextCoordinates(1, false));

		mPlaceWallButton = new GButton("Place a wall", 300, 150, 100, mQuoridorView.getBottom() + 64, mButtonBackgroundColor, Color.GREEN);
		mPlaceWallButton.setOnClickAction(new GButton.onClickAction() {
			@Override
			public void onClick(GameView gameView) {
				if (!gamePaused) {
					if (!gameView.placingWall) {
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

		mToggleWallTypeButton = new GButton("Horizontal", 300, 150, 475, mQuoridorView.getBottom() + 64, mButtonBackgroundColor, Color.WHITE);
		mToggleWallTypeButton.setOnClickAction(new GButton.onClickAction() {
			@Override
			public void onClick(GameView gameView) {
				gameView.cancelWallPlacement();
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


		mConfirmWallButton = new GButton("Confirm", 300, 150, 850, mQuoridorView.getBottom() + 64, mButtonBackgroundColor, Color.GREEN);
		mConfirmWallButton.setOnClickAction(new GButton.onClickAction() {
			@Override
			public void onClick(GameView gameView) {
				gameView.finalizeWallPlacement();
				mToggleWallTypeButton.setVisible(false);
				mConfirmWallButton.setVisible(false);
				mPlaceWallButton.setTextColor(Color.GREEN);
				mPlaceWallButton.setText("Place a wall");
				mToggleWallTypeButton.setText("Horizontal");
			}
		});
		mConfirmWallButton.setVisible(false);

		mNewGameButton = new GButton("New Game", 300, 150, mSurfaceWidth - 450, mSurfaceHeight - 300, mButtonBackgroundColor, Color.GREEN);
		mNewGameButton.setOnClickAction(new GButton.onClickAction() {
			@Override
			public void onClick(GameView gameView) {
				gameView.startNewGame();
				mNewGameButton.setVisible(false);
			}
		});
		mNewGameButton.setVisible(false);


		// Register buttons
		mGButtons.add(mPlaceWallButton);
		mGButtons.add(mToggleWallTypeButton);
		mGButtons.add(mConfirmWallButton);
		mGButtons.add(mNewGameButton);

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
		for (GButton b : mGButtons) {
			if (b.isInRect(x, y)) {
				b.mOnClickAction.onClick(this);
				return;
			}
		}
		if (mQuoridorView.isInRect(x, y)) {
			mQuoridorView.mOnClickAction.onClick(this, x, y);
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

					if (posX - mLastTouchX > wallUpdateStep) {
						mQuoridorView.offsetWallPreview(mWallPreviewType, 1, 0);
						mLastTouchX = posX - 10;
						playSound(mWallMoveSoundId, 0.3f);

					} else if (posX - mLastTouchX < wallUpdateStep * -1) {
						mQuoridorView.offsetWallPreview(mWallPreviewType, -1, 0);
						mLastTouchX = posX + 10;
						playSound(mWallMoveSoundId, 0.3f);
					}
					if (posY - mLastTouchY > wallUpdateStep) {
						mQuoridorView.offsetWallPreview(mWallPreviewType, 0, -1);
						mLastTouchY = posY - 10;
						playSound(mWallMoveSoundId, 0.3f);
					} else if (posY - mLastTouchY < wallUpdateStep * -1) {
						mQuoridorView.offsetWallPreview(mWallPreviewType, 0, 1);
						mLastTouchY = posY + 10;
						playSound(mWallMoveSoundId, 0.3f);
					}
					message = null;
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
			postMoveAndGetNewState(apiBaseUrl + "jouer/", mGame.mGameID, mGame.mLastMoveType, mGame.mLastMoveCoordinates);
		} catch (QuoridorException e) {
			message = e.getMessage();
		}
	}

	public void refreshHover() {
		mQuoridorView.resetHoverPositions();
		mQuoridorView.hoverCells(mGame.getPossibleNextCoordinates(1, false));
	}

	public void initiateWallPlacement(int wallType) {
		placingWall = true;
		mWallPreviewType = wallType;
		mQuoridorView.setWallPreview(wallType, 5, 5);
	}

	public void finalizeWallPlacement() {

		int[] coordinates = mQuoridorView.getWallPreviewCoordinates();

		try {
			tryToPlaceWall(1, mWallPreviewType,coordinates[0], coordinates[1]);
			playSound(mWallPlaceSoundId, 0.5f);
			gamePaused = true;
			postMoveAndGetNewState(apiBaseUrl + "jouer/", mGame.mGameID, mGame.mLastMoveType, mGame.mLastMoveCoordinates);
		} catch (QuoridorException e) {
			message = "Could not place wall";
		}

		cancelWallPlacement();
	}

	public void tryToPlaceWall(int playerNumber, int wallType, int x, int y) throws QuoridorException {

		mGame.requestWallPlacement(playerNumber, wallType, x, y);

		mQuoridorView.resetHoverPositions();
		mQuoridorView.hoverCells(mGame.getPossibleNextCoordinates(1, false));

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
		mQuoridorView.setConsoleMessage("YOU LOST!!");
		mNewGameButton.setVisible(true);

	}

	public void initGameWin() {
		gamePaused = true;
		playSound(mWinSoundId, 0.6f);
		mQuoridorView.setConsoleMessageColor(Color.GREEN);
		mQuoridorView.setConsoleMessage("YOU WON!");
		mNewGameButton.setVisible(true);
	}

	public void startNewGame() {

		fetchNewGameFromServer(apiBaseUrl + "débuter/", "simar86");

		mQuoridorView.setConsoleMessage("");
	}

}
