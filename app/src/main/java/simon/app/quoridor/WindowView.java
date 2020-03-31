package simon.app.quoridor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract class representing a Window, child of AppView
 */
public abstract class WindowView {

	//==============================================================================================
	// Views
	//==============================================================================================

	/**
	 * The views contained in the WindowView
	 */
	protected List<GView> mGViews = new ArrayList<>();

	/**
	 * Called by a GView created with a reference to the WindowView. Registers the review in order
	 * to draw it and to dispatch touch events
	 * @param gView The GView to register
	 */
	public void registerGView(GView gView) {
		mGViews.add(gView);
		sortViews();
	}

	/**
	 * Sorts GViews by zIndex. Called by children GViews when zIndex gets changed.
	 */
	public void sortViews() {
		Collections.sort(mGViews);
		Collections.reverse(mGViews);
	}

	//==============================================================================================
	// Drawing and touch event routing
	//==============================================================================================

	/**
	 * Draws the views contained in the window. Can be overrode to customize the drawing process.
	 * @param canvas The canvas to draw on
	 */
	public void draw(Canvas canvas) {

	}

	/**
	 * Children can override this to handle touch events
	 * @param event The touch event routed from the AppView
	 * @return True, the event is always considered to be handled
	 */
	public boolean onTouchEvent(MotionEvent event) {
		dispatchTouchToViews((int) event.getX(), (int) event.getY());
		return true;
	}

	/**
	 * Dispatch the touch event to the children views until it is handled
	 * @param x The x coordinate (pixels) of the event
	 * @param y The y coordinate (pixels) of the event
	 */
	private void dispatchTouchToViews(int x, int y) {
		for (GView gView : mGViews) {

			if (gView.isInRect(x, y)) {
				gView.performClick(this, x, y); // TODO: make GViews accept WindowView as argument
				return;
			}
		}
	}

	//==============================================================================================
	// Abstract methods linked to SurfaceHolder
	//==============================================================================================

	/**
	 * Actions to perform when surfaceChanged gets called in the AppView.
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(SurfaceHolder, int, int, int)
	 */
	public abstract void surfaceChanged(SurfaceHolder holder, int format, int width, int height);

	/**
	 * Actions to perform when surfaceCreated gets called in the AppView.
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(SurfaceHolder)
	 */
	public abstract void surfaceCreated(SurfaceHolder holder);

	/**
	 * Actions to perform when surfaceDestroyed gets called in the AppView. The implementation
	 * should free non-necessary resources
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(SurfaceHolder)
	 */
	public abstract void surfaceDestroyed(SurfaceHolder holder);
}