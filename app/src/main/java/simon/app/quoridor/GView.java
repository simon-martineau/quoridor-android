package simon.app.quoridor;

import android.graphics.Canvas;

public abstract class GView {
	private int mX;
	private int mY;
	private onClickAction mOnClickAction;
	private boolean mIsVisible = true;

	// Constructor
	public GView(int x, int y) {
		mX = x;
		mY = y;
	}

	// Universal click event
	public void performClick(GameView gameView, int x, int y) {
		mOnClickAction.onClick(gameView, x, y);
	}

	public void setOnClickAction(onClickAction action) {
		mOnClickAction = action;
	}

	public interface onClickAction {
		void onClick(GameView gameView, int x, int y);
	}

	// Abstract methods
	public abstract int getWidth();
	public abstract int getHeight();
	public abstract void draw(Canvas canvas);


	// Dimensions
	public int getTop() {
		return mY;
	}

	public int getBottom() {
		return mY + getHeight();
	}

	public int getLeft() {
		return mX;
	}

	public int getRight() {
		return mX + getWidth();
	}

	public void setX(int x) {
		mX = x;
	}

	public void setY(int y) {
		mY = y;
	}

	public void setPosition(int x, int y) {
		mX = x;
		mY = y;
	}

	// Visibility
	public boolean isInRect(int x, int y) {
		return (getLeft() < x && x < getRight() && getTop() < y && y < getBottom() && mIsVisible);
	}

	public boolean isVisible() {
		return mIsVisible;
	}

	public void setVisible(boolean visible) {
		mIsVisible = visible;
	}

}
