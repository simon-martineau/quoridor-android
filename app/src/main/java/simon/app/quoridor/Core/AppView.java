package simon.app.quoridor.Core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.HashMap;
import java.util.Map;



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
		setActiveWindowView("loading screen");

		setFocusable(true);
	}

	//==============================================================================================
	// WindowView methods
	//==============================================================================================

	private void setUpWindowViews() {

		mWindowViews.put("game", new GameView(this));
		mWindowViews.put("main menu", new MainMenuView(this));
		mWindowViews.put("loading screen", new LoadingView(this));
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

		activateCurrentWindow();
	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		deactivateCurrentWindow();

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

	//==============================================================================================
	// Methods called from WindowViews
	//==============================================================================================
	public void swapToGameView() {
		setActiveWindowView("game");
	}
	public void swapToMainMenuView() {
		setActiveWindowView("main menu");
	}
}
