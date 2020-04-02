package simon.app.quoridor.CustomViews.Colors;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import simon.app.quoridor.CustomViews.GView;
import simon.app.quoridor.WindowViews.WindowView;

public class ColorView extends GView {
	private int mBorderRadius = 10;
	private int mWidth;
	private int mHeight;
	private int mColor;

	private Paint mPaint = new Paint();
	private Paint mBorderPaint = new Paint();

	public ColorView(GView gView, int x, int y, int width, int height, int color, boolean register) {
		super(gView, x, y, register);

		mWidth = width;
		mHeight = height;
		mColor = color;
		mPaint.setColor(color);
		setUpPaints();

	}

	public ColorView(WindowView windowView, int x, int y, int width, int height, int color) {
		super(windowView, x, y);

		mWidth = width;
		mHeight = height;
		mColor = color;
		mPaint.setColor(color);
		setUpPaints();
	}

	public int getColor() {
		return mColor;
	}

	private void setUpPaints() {
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setStrokeWidth(2);
		mBorderPaint.setColor(Color.WHITE);
	}



	public void setBorderColor(int color) {
		mBorderPaint.setColor(color);
	}

	public void setColor(int color) {
		mColor = color;
		mPaint.setColor(color);
	}

	public void setBorderRadius(int radius) {
		mBorderRadius = radius;
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
		canvas.drawRoundRect(getLeft(), getTop(), getRight(), getBottom(),
				mBorderRadius, mBorderRadius, mPaint);

		canvas.drawRoundRect(getLeft(), getTop(), getRight(), getBottom(),
				mBorderRadius, mBorderRadius, mBorderPaint);
	}
}
