package simon.app.quoridor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcelable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;


import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
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
	public QuoridorDrawer mQuoridorDrawer;

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

	// Temp
	String message = "";




	public GameView(Context context) {
		super(context);

		getHolder().addCallback(this);
		try {
			fetchNewGameFromServer(apiBaseUrl + "débuter/", "simar86");
		} catch (IOException e) {
			e.printStackTrace();
		}

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

		setFocusable(true);
	}


	public void update() {

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		if (gamePaused) mQuoridorDrawer.setBlink(false);
		else mQuoridorDrawer.setBlink(true);

		mQuoridorDrawer.draw(canvas, mGame);

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
		mSurfaceWidth = width;
		mSurfaceHeight = height;

		mQuoridorDrawer = new QuoridorDrawer(mSurfaceWidth);
		mQuoridorDrawer.hoverCells(mGame.getPossibleNextCoordinates(1, false));

		mPlaceWallButton = new GButton("Place a wall", 300, 150, 100, mQuoridorDrawer.getBottom() + 64, mButtonBackgroundColor, Color.GREEN);
		mPlaceWallButton.setOnClickAction(new GButton.onClickAction() {
			@Override
			public void onClick(GameView gameView) {
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
		});

		mToggleWallTypeButton = new GButton("Horizontal", 300, 150, 475, mQuoridorDrawer.getBottom() + 64, mButtonBackgroundColor, Color.WHITE);
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


		mConfirmWallButton = new GButton("Confirm", 300, 150, 850, mQuoridorDrawer.getBottom() + 64, mButtonBackgroundColor, Color.GREEN);
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



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();


		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// Check if button is pressed
				if (mPlaceWallButton.isInRect(x, y) && !gamePaused) {
					mPlaceWallButton.mOnClickAction.onClick(this);
				} else if (mToggleWallTypeButton.isInRect(x, y)) {
					mToggleWallTypeButton.mOnClickAction.onClick(this);
				} else if (mConfirmWallButton.isInRect(x, y)) {
					mConfirmWallButton.mOnClickAction.onClick(this);
				} else if (mNewGameButton.isInRect(x, y)) {
					mNewGameButton.mOnClickAction.onClick(this);
				} else {
					if (!placingWall && !gamePaused) {
						int[] possibleCellCoordinates;
						possibleCellCoordinates = mQuoridorDrawer.getCellCorrespondingToTouch(x, y);
						if (possibleCellCoordinates != null)
							tryToMovePlayer(1, possibleCellCoordinates[0], possibleCellCoordinates[1]);
					} else {
						mLastTouchX = event.getX();
						mLastTouchY = event.getY();
						message = "snap";
						break;
					}
				}
				break;

			case MotionEvent.ACTION_MOVE: {
				float posX = event.getX();
				float posY = event.getY();

				if (posX - mLastTouchX > wallUpdateStep) {
					mQuoridorDrawer.offsetWallPreview(mWallPreviewType, 1, 0);
					mLastTouchX = posX - 10;

				} else if (posX - mLastTouchX < wallUpdateStep*-1) {
					mQuoridorDrawer.offsetWallPreview(mWallPreviewType, -1, 0);
					mLastTouchX = posX + 10;
				}
				if (posY - mLastTouchY > wallUpdateStep) {
					mQuoridorDrawer.offsetWallPreview(mWallPreviewType, 0, -1);
					mLastTouchY = posY - 10;
				} else if (posY - mLastTouchY < wallUpdateStep*-1) {
					mQuoridorDrawer.offsetWallPreview(mWallPreviewType, 0, 1);
					mLastTouchY = posY + 10;
				}
				message = null;
				break;
			}

		}
		return true;
	}

	public void tryToMovePlayer(int playerNumber, int x, int y) {
		// Logic
		try {
			mGame.requestPlayerMovement(playerNumber, x, y);
			gamePaused = true;
			postMoveAndGetNewState(apiBaseUrl + "jouer/", mGame.mGameID, mGame.mLastMoveType, mGame.mLastMoveCoordinates);
		} catch (QuoridorException | IOException e) {
			message = e.getMessage();
			return;
		}
	}

	public void refreshHover() {
		mQuoridorDrawer.resetHoverPositions();
		mQuoridorDrawer.hoverCells(mGame.getPossibleNextCoordinates(1, false));
	}

	public void initiateWallPlacement(int wallType) {
		placingWall = true;
		mWallPreviewType = wallType;
		mQuoridorDrawer.setWallPreview(wallType, 5, 5);
	}

	public void finalizeWallPlacement() {

		int[] coordinates = mQuoridorDrawer.getWallPreviewCoordinates();

		try {
			tryToPlaceWall(1, mWallPreviewType,coordinates[0], coordinates[1]);
			gamePaused = true;
			postMoveAndGetNewState(apiBaseUrl + "jouer/", mGame.mGameID, mGame.mLastMoveType, mGame.mLastMoveCoordinates);
		} catch (QuoridorException | IOException e) {
			message = "Could not place wall";
		}

		cancelWallPlacement();
	}

	public void tryToPlaceWall(int playerNumber, int wallType, int x, int y) throws QuoridorException {

		mGame.requestWallPlacement(playerNumber, wallType, x, y);

		mQuoridorDrawer.resetHoverPositions();
		mQuoridorDrawer.hoverCells(mGame.getPossibleNextCoordinates(1, false));

	}

	public void cancelWallPlacement() {
		placingWall = false;
		mWallPreviewType = 0;
		mQuoridorDrawer.clearWallPreview();
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

	public void fetchNewGameFromServer(String targetURL, String idul) throws IOException {

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
					public void onFailure(Call call, IOException e) {
						e.printStackTrace();
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							throw new IOException("Error : " + response);
						}
						String data =  response.body().string();
						setNewGame(data);
						gamePaused = false;
						refreshHover();
					}
				});
	}

	public void postMoveAndGetNewState(String targetURL, String gameID, String moveType, String position) throws IOException{

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
					public void onFailure(Call call, IOException e) {
						e.printStackTrace();
					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							throw new IOException("Error : " + response);
						}
						String data =  response.body().string();
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
		mQuoridorDrawer.setConsoleMessageColor(Color.RED);
		mQuoridorDrawer.setConsoleMessage("YOU LOST!!");
		mNewGameButton.setVisible(true);

	}

	public void initGameWin() {
		gamePaused = true;
		mQuoridorDrawer.setConsoleMessageColor(Color.GREEN);
		mQuoridorDrawer.setConsoleMessage("YOU WON!");
		mNewGameButton.setVisible(true);
	}

	public void startNewGame() {
		try {
			fetchNewGameFromServer(apiBaseUrl + "débuter/", "simar86");
		} catch (IOException e) {
			e.printStackTrace();
		}
		mQuoridorDrawer.setConsoleMessage("");
	}

	public String getGameStateJSONString() {
		return mGame.getGameStateJSON().toString();
	}


}
