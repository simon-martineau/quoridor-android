package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Typeface;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import simon.app.quoridor.WindowViews.GameView;
import simon.app.quoridor.WindowViews.WindowView;

/**
 * Base class for custom views
 */
public abstract class GView implements Comparable<GView> {

	protected List<GView> mGViews = null;
	protected boolean isParent = false;

	/**
	 * X coordinate of the GView in relation to its parent
	 */
	private int mX;

	/**
	 * Y coordinate of the GView in relation to its parent
	 */
	private int mY;

	protected WindowView mWindowView;
	protected GModalView mGModalView;

	/**
	 * Callback for click event
	 */
	private onClickAction mOnClickAction;
	private boolean hasOnClick = false;


	/**
	 * If the GView is visible. If not, it does not get drawn, and it does not consume click events
	 */
	private boolean mIsVisible = true;

	/**
	 * Default typeface
	 */
	protected static Typeface mTypeFace = GameView.DEFAULT_TYPEFACE;

	/**
	 * The index used for drawing GViews and handling events. Higher zIndex views are drawn on top
	 * and have priority on click events
	 */
	private int mZIndex;


	/**
	 * Constructor for the GView in a GameView
	 * @param x The x coordinate (in pixels) for the view
	 * @param y The y coordinate (in pixels) for the view
	 */
	public GView(WindowView windowView, int x, int y) {
		mX = x;
		mY = y;
		mWindowView = windowView;
		registerView(windowView);
	}

	/**
	 * Constructor for the GView in another GView
	 */

	/**
	 * Constructor for the GView in a ModalView
	 * @param x The x coordinate (in pixels) for the view
	 * @param y The y coordinate (in pixels) for the view
	 */
	public GView(GModalView gModalView, int x, int y, boolean register) {
		mX = x;
		mY = y;
		mGModalView = gModalView;
		if (register) {
			registerViewModal(gModalView);
		}
	}

	/**
	 * Centers the view horizontally in its parent WindowView
	 */
	public void setCenterHorizontal() {
		setX(mWindowView.getWidth() / 2 - getWidth() / 2);
	}

	/**
	 * Centers the view vertically in its parent WindowView
	 */
	public void setCenterVertical() {
		setY(mWindowView.getHeight() / 2 - getHeight() / 2);
	}

	/**
	 * @return The view's zIndex
	 */
	public int getZIndex() {
		return mZIndex;
	}

	/**
	 * @param index The zIndex to set the member to
	 */
	public void setZIndex(int index) {
		mZIndex = index;
		if (mWindowView != null) {
			mWindowView.sortViews();
		} else if (mGModalView != null) {
			mGModalView.sortViews();
		}
	}

	/**
	 * Register the view in the parent WindowView
	 * @param windowView The parent WindowView
	 */
	private void registerView(WindowView windowView) {
		windowView.registerGView(this);
	}

	/**
	 * Register the view in the parent GModalView
	 * @param gModalView The parent GModalView
	 */
	private void registerViewModal(GModalView gModalView) {
		gModalView.registerGView(this);
	}


	private void registerViewModal(GView gView) {
		// TODO: Merge this with ModalView
	}


	/**
	 * Perform the view's method associated with its mOnClickAction interface
	 * @param x The x coordinate of the touch event
	 * @param y The y coordinate of the touch event
	 */
	public void performClick(int x, int y) {
		if (hasOnClick) {
			mOnClickAction.onClick(x, y);
		}
	}

	/**
	 * Sets the onClickAction (interface), which is the interface used to handle touch events
	 * @param action An implementation of onClickAction
	 */
	public void setOnClickAction(onClickAction action) {
		mOnClickAction = action;
		hasOnClick = true;
	}

	/**
	 * Interface used to handle touch events. The method onClick is called when a touch event gets
	 * routed to the view
	 */
	public interface onClickAction {
		void onClick(int x, int y);
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

	/**
	 * CompareTo implementation. Comparison is based on the zIndex
	 * @param o The other object
	 * @return The difference in zIndex
	 */
	@Override
	public int compareTo(@NotNull GView o) {
		return this.getZIndex() - o.getZIndex();
	}
}
