package simon.app.quoridor.WindowViews;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import simon.app.quoridor.Core.AppView;
import simon.app.quoridor.CustomViews.GProgressBar;
import simon.app.quoridor.CustomViews.GTitleView;
import simon.app.quoridor.R;

public class LoadingView extends WindowView {

	private static final long LOADING_TIME = 1800;
	private static final long DING_TIME_STAMP = 1000;

	private long mStartTime;
	private boolean mPlayedSound;

	private MediaPlayer mLoadingSoundPlayer;

	private boolean mSoundEffectsPref;


	GTitleView mCompleteTitleView;
	GProgressBar mProgressBar;


	/**
	 * Retrieves the user preferences and updates members accordingly
	 */
	private void retrievePreferences() {
		SharedPreferences prefs = getAppView().getSharedPreferences(AppView.DATA_SETTINGS);
		mSoundEffectsPref = prefs.getBoolean("sound_effects", SettingsView.DEFAULT_SOUND_EFFECTS);
	}


	/**
	 * Constructor holding a reference to the AppView
	 *
	 * @param appView The AppView from which the WindowView is created
	 */
	public LoadingView(AppView appView) {
		super(appView);
	}

	private void setUpAudio() {
		mLoadingSoundPlayer = MediaPlayer.create(mAppView.getContext(), R.raw.loading_sound);
	}


	private void setUpViews() {
		mGViews.clear();

		mProgressBar = new GProgressBar(this, 0, 0, 600, 100);
		mProgressBar.setBackgroundColor(Color.BLACK);
		mProgressBar.setForegroundColor(Color.GREEN);
		mProgressBar.setBorderWidth(5);
		mProgressBar.setBorderColor(Color.WHITE);
		mProgressBar.setPadding(10);
		mProgressBar.setCenterHorizontal();
		mProgressBar.setCenterVertical();

		mCompleteTitleView = new GTitleView(this, 0, 0, "COMPLETE", Color.GREEN, 172);
		mCompleteTitleView.setTypeFace(GameView.DEFAULT_TYPEFACE);
		mCompleteTitleView.setCenterHorizontal();
		mCompleteTitleView.setBottom(mProgressBar.getTop() - 70);
		mCompleteTitleView.setVisible(false);

	}

	@Override
	public void draw(Canvas canvas) {
		for (int i = mGViews.size() - 1; i >= 0; i--) {
			mGViews.get(i).draw(canvas);
		}

		if (!mPlayedSound) {
			mStartTime = System.currentTimeMillis();
			if (mSoundEffectsPref) {
				if (!mLoadingSoundPlayer.isPlaying())
					mLoadingSoundPlayer.start();
			}
			mPlayedSound = true;
		}

		updateProgressBar();
	}

	private void updateProgressBar() {
		if (System.currentTimeMillis() - mStartTime > LOADING_TIME) {
			mAppView.swapToMainMenuView();
		}
		mProgressBar.setProgress((System.currentTimeMillis() - mStartTime) / (float) DING_TIME_STAMP);
		if (System.currentTimeMillis() - mStartTime >= DING_TIME_STAMP) {
			mProgressBar.setBorderColor(Color.GREEN);
			mCompleteTitleView.setVisible(true);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	@Override
	public void onActivate() {
		super.onActivate();
		setUpAudio();
		retrievePreferences();
	}

	@Override
	public void onDeactivate() {
		super.onDeactivate();
		mLoadingSoundPlayer.stop();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setUpViews();
	}
}
