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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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
	// Views
	//==============================================================================================




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

		setFocusable(true);
	}

	/**
	 * Draws the app to the canvas
	 * @param canvas The canvas to draw on
	 */
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(SurfaceHolder, int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// mGameThread = new GameThread(getHolder(), this); // TODO: Make GameThread control AppView
		mGameThread.setRunning(true);
		mGameThread.start();

	}

	/**
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(SurfaceHolder)
	 */
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

	/**
	 * Touch event routing
	 * @see SurfaceView#onTouchEvent(MotionEvent)
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {


		return true;
	}

}
