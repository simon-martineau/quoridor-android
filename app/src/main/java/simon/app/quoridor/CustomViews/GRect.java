package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import simon.app.quoridor.Core.GParent;

public class GRect extends GView {

	private Paint mPaint = new Paint();
	private Paint mBorderPaint = new Paint();

	private boolean mHasBorder = false;

	private int mWidth;
	private int mHeight;

	/**
	 * Constructor for the GView in a ModalView
	 *
	 * @param gParent  The parent containing this
	 * @param x        The x coordinate (in pixels) for the view
	 * @param y        The y coordinate (in pixels) for the view
	 * @param register Whether or not to implicitly register this in the parent's children list
	 */
	public GRect(GParent gParent, int x, int y, int width, int height, boolean register) {
		super(gParent, x, y, register);


		mWidth = width;
		mHeight = height;

		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.FILL);
		mBorderPaint.setColor(Color.WHITE);
		mBorderPaint.setStyle(Paint.Style.STROKE);
	}

	public void setBorder(boolean border) {
		mHasBorder = border;
	}

	public void setBorderWidth(int width) {
		mBorderPaint.setStrokeWidth(width);
	}

	public void setBorderColor(int color) {
		mBorderPaint.setColor(color);
	}

	public void setBorderAlpha(int alpha) {
		mBorderPaint.setAlpha(alpha);
	}


	public void setColor(int color) {
		mPaint.setColor(color);
	}

	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	@Override
	public int getWidth() {
		return mWidth;
	}

	@Override
	public int getHeight() {
		return mHeight;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), mPaint);

		if (mHasBorder) {
			canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), mBorderPaint);
		}
	}
}
