package simon.app.quoridor.Core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.HashMap;
import java.util.Map;

import simon.app.quoridor.R;
import simon.app.quoridor.WindowViews.GameView;
import simon.app.quoridor.WindowViews.LoadingView;
import simon.app.quoridor.WindowViews.MainMenuView;
import simon.app.quoridor.WindowViews.SettingsView;
import simon.app.quoridor.WindowViews.WindowView;


/**
 * Mother class containing all the views for the App. // TODO: finish this description
 */
public class AppView extends SurfaceView implements SurfaceHolder.Callback {
	//==============================================================================================
	// Constants
	//==============================================================================================

	/**
	 * Tag for logging
	 */
	private static final String TAG = "AppView";

	// Threading
	/**
	 * Thread from which to run the game
	 */
	public GameThread mGameThread;

	/**
	 * For accessing SharedPreferences
	 */
	public static final String APP_NAME = "";

	//==============================================================================================
	// Preferences
	//==============================================================================================
	public static final String SETTINGS_KEY = "Quoridor settings";
	public static final String STATISTICS_KEY = "Quoridor statistics";

	private SharedPreferences mSettings = getContext().getApplicationContext()
			.getSharedPreferences(SETTINGS_KEY, Context.MODE_PRIVATE);

	private SharedPreferences mStatistics = getContext().getApplicationContext()
			.getSharedPreferences(STATISTICS_KEY, Context.MODE_PRIVATE);


	//==============================================================================================
	// MediaPlayers
	//==============================================================================================
	private MediaPlayer mMenuSongPlayer;


	//==============================================================================================
	// WindowViews
	//==============================================================================================

	/**
	 * Width of the SurfaceView
	 */
	private int mWidth;

	/**
	 * Height of the SurfaceView
	 */
	private int mHeight;

	//==============================================================================================
	// WindowViews
	//==============================================================================================

	private HashMap<String, WindowView> mWindowViews = new HashMap<>();
	private String mActiveWindowView = null;

	//==============================================================================================
	// Constructors
	//==============================================================================================

	/**
	 * Constructor for the AppView.
	 * @param context The activity context
	 */
	public AppView(Context context) {
		super(context);
		getHolder().addCallback(this);

		setUpWindowViews();
		setUpAudio();
		setActiveWindowView("loading screen");

		setFocusable(true);
	}

	//==============================================================================================
	// WindowView methods
	//==============================================================================================


	private void setUpAudio() {
		mMenuSongPlayer = MediaPlayer.create(getContext(), R.raw.menu_song);
		mMenuSongPlayer.setVolume(0.5f, 0.5f);
		mMenuSongPlayer.setLooping(true);
	}

	public void startMainMenuMusic() {
		if (!mMenuSongPlayer.isPlaying() && getSharedPreferences(DATA_SETTINGS).getBoolean("music", SettingsView.DEFAULT_MUSIC)) {
			mMenuSongPlayer.start();
		}
	}

	public void stopMainMenuMusic() {
		if (mMenuSongPlayer.isPlaying()) {
			mMenuSongPlayer.pause();
		}
	}

	private void setUpWindowViews() {

		mWindowViews.put("game", new GameView(this));
		mWindowViews.put("main menu", new MainMenuView(this));
		mWindowViews.put("loading screen", new LoadingView(this));
		mWindowViews.put("settings view", new SettingsView(this));
	}

	private void deactivateCurrentWindow() {
		WindowView currentWindow = mWindowViews.get(mActiveWindowView);
		if (currentWindow != null) currentWindow.onDeactivate();
	}

	private void activateCurrentWindow() {
		WindowView currentWindow = mWindowViews.get(mActiveWindowView);
		if (currentWindow != null) currentWindow.onActivate();
	}

	private void setActiveWindowView(String key) {
		if (mActiveWindowView != null) {
			mWindowViews.get(mActiveWindowView).onDeactivate();
		}

		mWindowViews.get(key).onActivate();
		if (key.equals("main menu") || key.equals("settings view")) {
			startMainMenuMusic();
		} else {
			stopMainMenuMusic();
		}

		mActiveWindowView = key;
	}

	//==============================================================================================
	// Overridden methods
	//==============================================================================================
	/**
	 * Draws the app on the canvas
	 * @param canvas The canvas to draw on
	 */
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		WindowView currentWindowView = mWindowViews.get(mActiveWindowView);
		if (currentWindowView != null) {
			currentWindowView.draw(canvas);
		} else {
			Log.e(TAG, "draw: ActiveWindowView not found");
		}
	}

	/**
	 * Touch event routing
	 * @see SurfaceView#onTouchEvent(MotionEvent)
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		WindowView currentWindowView = mWindowViews.get(mActiveWindowView);
		if (currentWindowView != null) {
			currentWindowView.onTouchEvent(event);
		} else {
			Log.e(TAG, "onTouchEvent: ActiveWindowView not found");
		}

		return true;
	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(SurfaceHolder, int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mWidth = width;
		mHeight = height;
		for (Map.Entry<String, WindowView> pair: mWindowViews.entrySet()) {
			pair.getValue().surfaceChanged(holder, format, width, height);
		}
	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mGameThread = new GameThread(getHolder(), this);
		mGameThread.setRunning(true);
		mGameThread.start();
	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		deactivateCurrentWindow();

		stopMainMenuMusic();

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


	public void onStop() {
		stopMainMenuMusic();
	}

	public void onRestart() {
		startMainMenuMusic();
	}

	//==============================================================================================
	// Methods called from WindowViews
	//==============================================================================================
	public void swapToGameView() {
		setActiveWindowView("game");
	}
	public void swapToMainMenuView() {
		setActiveWindowView("main menu");
	}
	public void swapToSettingsView() { setActiveWindowView("settings view"); }


	public static final int DATA_SETTINGS = 1;
	public static final int DATA_STATISTICS = 2;

	public SharedPreferences getSharedPreferences(int group) {
		switch (group) {
			case DATA_SETTINGS:
				return mSettings;
			case DATA_STATISTICS:
				return mStatistics;
			default:
				return null;
		}

	}
}
