package simon.app.quoridor.WindowViews;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import simon.app.quoridor.Core.AppView;
import simon.app.quoridor.CustomViews.GTitleView;

public class StatisticsView extends WindowView {
	SharedPreferences mStatistics;

	// =============================================================================================
	// View members
	// =============================================================================================

	GTitleView mTitleView;


	// =============================================================================================
	// Constructors
	// =============================================================================================
	/**
	 * Constructor holding a reference to the AppView
	 *
	 * @param appView The AppView from which the WindowView is created
	 */
	public StatisticsView(AppView appView) {
		super(appView);
		mStatistics = getAppView().getSharedPreferences(AppView.DATA_STATISTICS);
	}

	// =============================================================================================
	// Setup methods
	// =============================================================================================

	private void setUpViews() {
		mGViews.clear();

		mTitleView = new GTitleView(this, 0, 100, "Statistics", Color.GREEN, 128);
		mTitleView.setCenterHorizontal();



	}

	// =============================================================================================
	// Override methods
	// =============================================================================================

	/**
	 * Draws the window on the canvas
	 * @param canvas The canvas to draw on
	 */
	@Override
	public void draw(Canvas canvas) {
		for (int i = mGViews.size() - 1; i >= 0; i--) {
			mGViews.get(i).draw(canvas);
		}
	}

	/**
	 * Touch event routing
	 * @param event The touch event routed from the AppView
	 * @return true
	 */
	@SuppressWarnings("SwitchStatementWithTooFewBranches")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();


		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				dispatchTouchToViews(x, y);
				break;
		}
		return true;
	}

	/**
	 * Called from the AppView when surface gets changed.
	 * @see WindowView#surfaceChanged(SurfaceHolder, int, int, int)
 	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setUpViews();
	}

	// =============================================================================================
	// Other methods
	// =============================================================================================

	private void setPrefInt(String key, int stat) {
		SharedPreferences.Editor editor = mStatistics.edit();
		editor.putInt(key, stat);
		editor.apply();
	}
}
