package simon.app.quoridor;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private String mGameKey = null;
	private String mGameState = null;
	public AppView mAppView;

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

		LinearLayout surface = findViewById(R.id.surfaceViewContainer);
		mAppView = new AppView(this);
		surface.addView(mAppView);
		Log.i(TAG, "onCreate: view added");
	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.i(TAG, "onStart: called");
		if (mAppView == null) Log.i(TAG, "onStart: mAppView is null");

	}

	@Override
	protected void onStop() {
		super.onStop();
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


// TODO: Add home screen
// TODO: Cancel wall placement on abandon match







