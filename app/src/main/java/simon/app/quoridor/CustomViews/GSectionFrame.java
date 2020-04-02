package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import simon.app.quoridor.Core.GParent;

public class GSectionFrame extends GView {

	private int mX2;
	private int mY2;
	private int mBorderRadius = 0;
	private Paint mPaint = new Paint();

	private GTitleView mCaption;

	/**
	 * Constructor for the GSectionFrame
	 *
	 * @param gParent  The parent containing the view
	 * @param x1       The x coordinate for the top-left of the frame
	 * @param y1       The y coordinate for the top-left of the frame
	 * @param x2       The x coordinate for the top-right of the frame
	 * @param y2       The y coordinate for the top-right of the frame
	 * @param register Whether or not to implicitly register this view in the parent's view.
	 *                 False when the parent handles drawing and touch event routing manually.
	 */
	public GSectionFrame(GParent gParent, int x1, int y1, int x2, int y2, int width, boolean register) {
		super(gParent, x1, y1, register);
		isParent = true;

		mX2 = x2;
		mY2 = y2;
		mPaint.setStrokeWidth(width);
		mPaint.setColor(Color.WHITE);

	}

	public void setCaption(String caption, int textSize, int textColor) {
		mGViews.clear();

		mCaption = new GTitleView(this, 0, 0, caption, textColor, textSize);
		mCaption.setZIndex(1);
		mCaption.setYFromViewCenter(0);
		mCaption.setX(50);
	}

	public void setBorderRadius(int borderRadius) {
		mBorderRadius = borderRadius;
	}

	public void setStrokeWidth(int width) {
		mPaint.setStrokeWidth(width);
	}

	public void setColor(int color) {
		mPaint.setColor(color);
	}

	@Override
	public int getWidth() {
		return mX2 - getLeft();
	}

	@Override
	public int getHeight() {
		return mY2 - getTop();
	}

	@Override
	public void draw(Canvas canvas) {
		// Draw frame
		canvas.drawRoundRect(getLeft(), getTop(), mX2, mY2, mBorderRadius, mBorderRadius, mPaint);

		drawChildren(canvas);
	}
}
