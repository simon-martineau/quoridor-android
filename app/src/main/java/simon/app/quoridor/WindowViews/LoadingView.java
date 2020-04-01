package simon.app.quoridor.WindowViews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import simon.app.quoridor.Core.AppView;
import simon.app.quoridor.CustomViews.GProgressBar;

public class LoadingView extends WindowView {
	public static final int LOAD_TIME = 30;

	int mCountdown = LOAD_TIME;

	GProgressBar mProgressBar;




	/**
	 * Constructor holding a reference to the AppView
	 *
	 * @param appView The AppView from which the WindowView is created
	 */
	public LoadingView(AppView appView) {
		super(appView);
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
	}

	@Override
	public void draw(Canvas canvas) {
		for (int i = mGViews.size() - 1; i >= 0; i--) {
			mGViews.get(i).draw(canvas);
		}

		updateProgressBar();
	}

	private void updateProgressBar() {
		if (mCountdown <= 0) {
			mAppView.swapToMainMenuView();
		}
		mProgressBar.setProgress((LOAD_TIME - mCountdown) / (float) LOAD_TIME);
		mCountdown--;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setUpViews();
	}
}
