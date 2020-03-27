package simon.app.quoridor;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private String mGameKey = null;
	private String mGameState = null;
	public GameView mGameView;

	@SuppressLint("SourceLockedOrientationActivity")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate: called");
		super.onCreate(savedInstanceState);

		Window window = getWindow();
		window.setStatusBarColor(Color.BLACK); // Redundant

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		LinearLayout surface = (LinearLayout) findViewById(R.id.surfaceViewContainer);
		mGameView = new GameView(this);
		surface.addView(mGameView);
		Log.i(TAG, "onCreate: view added");
	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.i(TAG, "onStart: called");
		if (mGameView == null) Log.i(TAG, "onStart: mGameView is null");

		if (mGameKey != null) {
			mGameView = new GameView(this, mGameKey, mGameState);
		}

	}

	@Override
	protected void onStop() {
//		Log.i(TAG, "onStop: called");
		super.onStop();
//		mGameKey = mGameView.mGame.mGameID;
//		mGameState = mGameView.getGameStateJSONString();
	}

	@Override
	protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}


	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}


// TODO: Change wall preview color depending on if position is good or not
// TODO: Add z-axis for drawing order
// TODO: Add home screen
// TODO: Cancel wall placement on abandon match






