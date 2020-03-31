package simon.app.quoridor;

import android.graphics.Canvas;

/**
 * Base class for custom views
 */
public abstract class GView {
	private int mX;
	private int mY;
	private onClickAction mOnClickAction;
	private boolean mIsVisible = true;

	/**
	 * Constructor for the GView
	 * @param x The x coordinate (in pixels) for the view
	 * @param y The y coordinate (in pixels) for the view
	 */
	public GView(int x, int y) {
		mX = x;
		mY = y;
	}

	/**
	 * Perform the view's method associated with its mOnClickAction interface
	 * @param gameView A reference to the GameView in which the view resides
	 * @param x The x coordinate of the touch event
	 * @param y The y coordinate of the touch event
	 */
	public void performClick(GameView gameView, int x, int y) {
		mOnClickAction.onClick(gameView, x, y);
	}

	/**
	 * Sets the onClickAction (interface), which is the interface used to handle touch events
	 * @param action An implementation of onClickAction
	 */
	public void setOnClickAction(onClickAction action) {
		mOnClickAction = action;
	}

	/**
	 * Interface used to handle touch events. The method onClick is called when a touch event gets
	 * routed to the view
	 */
	public interface onClickAction {
		void onClick(GameView gameView, int x, int y);
	}

	// Abstract methods

	/**
	 * Get the width (pixels) of the view. Children of this class have to implement it correctly for
	 * other methods to work.
	 * @return The width of the view in pixels
	 */
	public abstract int getWidth();

	/**
	 * Get the height (pixels) of the view. Children of this class have to implement it correctly for
	 * other methods to work.
	 * @return The width of the view in pixels
	 */
	public abstract int getHeight();

	/**
	 * Draw the view on the canvas. Each children has its own implementation.
	 * @param canvas The canvas on which to draw the view on
	 */
	public abstract void draw(Canvas canvas);


	// Dimensions

	/**
	 * @return The x coordinate (pixels) of the top edge of the view
	 */
	public int getTop() {
		return mY;
	}

	/**
	 * @return The x coordinate (pixels) of the bottom edge of the view
	 */
	public int getBottom() {
		return mY + getHeight();
	}

	/**
	 * @return The x coordinate (pixels) of the left edge of the view
	 */
	public int getLeft() {
		return mX;
	}

	/**
	 * @return The x coordinate (pixels) of the right edge of the view
	 */
	public int getRight() {
		return mX + getWidth();
	}

	/**
	 * @param x The x coordinate (pixels) to set the X to
	 */
	public void setX(int x) {
		mX = x;
	}

	/**
	 * @param y The y coordinate (pixels) to set the Y to
	 */
	public void setY(int y) {
		mY = y;
	}

	/**
	 * Sets both x and y at the same time
	 * @param x The x coordinate (pixels)
	 * @param y The y coordinate (pixels)
	 */
	public void setPosition(int x, int y) {
		mX = x;
		mY = y;
	}

	// Visibility

	/**
	 * Checks if the position is included in the view's rectangle. If mIsVisible is set to false,
	 * will always return false
	 * @param x The x coordinate (pixels)
	 * @param y The y coordinate (pixels)
	 * @return True if the coordinates are included in the rectangle AND the view is visible, false
	 * otherwise
	 */
	public boolean isInRect(int x, int y) {
		return (getLeft() < x && x < getRight() && getTop() < y && y < getBottom() && mIsVisible);
	}

	/**
	 * @return True if the view is visible, false otherwise
	 */
	public boolean isVisible() {
		return mIsVisible;
	}

	/**
	 * @param visible The visibility to set the view to
	 */
	public void setVisible(boolean visible) {
		mIsVisible = visible;
	}

}
