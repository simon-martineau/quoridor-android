package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import simon.app.quoridor.WindowViews.GameView;
import simon.app.quoridor.WindowViews.WindowView;

/**
 * Base class for custom views
 */
public abstract class GView implements Comparable<GView> {

	/**
	 * X coordinate of the GView in relation to its parent
	 */
	private int mX;

	/**
	 * Y coordinate of the GView in relation to its parent
	 */
	private int mY;

	protected WindowView mWindowView;
	protected GView mParentGView; // TODO: Merge modalView into this
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
	protected Typeface mTypeFace = GameView.DEFAULT_TYPEFACE;

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
	public GView(WindowView windowView, int x, int y, boolean register) {
		mX = x;
		mY = y;
		mWindowView = windowView;
		if (register) registerView(windowView);
	}

	/**
	 * Constructor for the GView in another GView
	 */

	/**
	 * Constructor for the GView in a ModalView
	 * @param x The x coordinate (in pixels) for the view
	 * @param y The y coordinate (in pixels) for the view
	 */
	public GView(GView gView, int x, int y) {
		mX = x;
		mY = y;
		mParentGView = gView;
		registerView(gView);
	}

	/**
	 * Centers the view horizontally in its parent WindowView
	 */
	public void setCenterHorizontal() {
		setX(getParentWidth() / 2 - getWidth() / 2);
	}

	/**
	 * Centers the view vertically in its parent WindowView
	 */
	public void setCenterVertical() {
		setY(getParentHeight() / 2 - getHeight() / 2);
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

	private void registerView(GView gView) {
		gView.registerGView(this);
	}


	/**
	 * Perform the view's method associated with its mOnClickAction interface
	 * @param x The x coordinate of the touch event
	 * @param y The y coordinate of the touch event
	 */
	public void performClick(int x, int y) {
		if (isParent) {
			int relX = x - getLeft();
			int relY = y - getTop();
			for (GView gView : mGViews) {
				if (gView.isInRect(relX, relY)) {
					gView.performClick(relX, relY);
					return;
				}
			}
		}
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
	 * IMPORTANT: Children need to draw relative to the parent.
	 * @param canvas The canvas on which to draw the view on
	 */
	public abstract void draw(Canvas canvas);


	protected void drawChildren(Canvas canvas) {
		Matrix translateMatrix = new Matrix();
		translateMatrix.setTranslate(getLeft(), getTop());

		canvas.save();
		canvas.concat(translateMatrix);
		for (GView gView : mGViews) {
			gView.draw(canvas);
		}
		canvas.restore();
	}

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
	 * will always return false.
	 * @param x The x coordinate (pixels)
	 * @param y The y coordinate (pixels)
	 * @return True if the coordinates are included in the rectangle AND the view is visible, false
	 * otherwise
	 */
	public boolean isInRect(int x, int y) {
		if (isParent) {
			// First, translate in coordinates relative to this
			int relX = x - getLeft();
			int relY = y - getTop();
			for (GView gView : mGViews) {
				if (gView.isInRect(relX, relY)) return true;
			}
		}
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

	/**
	 * Returns the width of the parent (WindowView or GView)
	 * @return The width in pixels
	 */
	protected int getParentWidth() {
		if (mGModalView != null) return mGModalView.getWidth();
		if (mParentGView != null) return mParentGView.getWidth();
		if (mWindowView != null) return mWindowView.getWidth();

		return -1;
	}

	/**
	 * Returns the height of the parent (WindowView or GView)
	 * @return The height in pixels
	 */
	protected int getParentHeight() {
		if (mGModalView != null) return mGModalView.getHeight();
		if (mParentGView != null) return mParentGView.getHeight();
		if (mWindowView != null) return mWindowView.getHeight();

		return -1;
	}


	protected int getParentLeft() {
		if (mGModalView != null) return mGModalView.getLeft();
		if (mParentGView != null) return mParentGView.getLeft();
		if (mWindowView != null) return mWindowView.getLeft();

		return -1;
	}

	protected int getParentTop() {
		if (mGModalView != null) return mGModalView.getTop();
		if (mParentGView != null) return mParentGView.getTop();
		if (mWindowView != null) return mWindowView.getTop();

		return -1;
	}

	protected int getParentRight() {
		if (mGModalView != null) return mGModalView.getRight();
		if (mParentGView != null) return mParentGView.getRight();
		if (mWindowView != null) return mWindowView.getRight();

		return -1;
	}

	protected int getParentBottom() {
		if (mGModalView != null) return mGModalView.getBottom();
		if (mParentGView != null) return mParentGView.getBottom();
		if (mWindowView != null) return mWindowView.getBottom();

		return -1;
	}


	// =============================================================================================
	// Parenting
	// =============================================================================================

	protected List<GView> mGViews = new ArrayList<>();
	protected boolean isParent = false;

	public void registerGView(GView gView) {
		mGViews.add(gView);
		sortViews();
	}

	public void sortViews() {
		Collections.sort(mGViews);
		Collections.reverse(mGViews);
	}
}


// TODO: Draw relative to parent in the following views:
// TODO: GFreezeView**
// TODO: GModalView**
// TODO: GQuoridorView***


