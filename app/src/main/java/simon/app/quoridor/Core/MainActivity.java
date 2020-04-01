package simon.app.quoridor.Core;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;

import simon.app.quoridor.R;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	public AppView mAppView;

	@SuppressLint("SourceLockedOrientationActivity")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Window window = getWindow();
		window.setStatusBarColor(Color.BLACK); // Redundant

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);

		LinearLayout surface = findViewById(R.id.surfaceViewContainer);
		mAppView = new AppView(this);
		surface.addView(mAppView);

	}
}

// TODO: "Allow" GViews to contain other GViews and simplify ModalView
// TODO: Make GViews draw relative to parent coordinates
// TODO: Make a color picker view for picking colors in settings

// TODO: Add loading bar sound effect

/* TODO: Implement stats tracking:
	- Walls placed
	- Distance moved
	- Wins/Losses, Win %
	- Play time

	Reset Stats
	*/







