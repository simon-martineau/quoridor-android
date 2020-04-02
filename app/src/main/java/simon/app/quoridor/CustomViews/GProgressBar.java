package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Paint;

import simon.app.quoridor.Core.GParent;
import simon.app.quoridor.WindowViews.WindowView;

public class GProgressBar extends GView {
	float mProgress = 0.0f;

	int mPadding = 5;
	int mWidth;
	int mHeight;

	Paint mBorderPaint = new Paint();
	Paint mBackgroundPaint = new Paint();
	Paint mForegroundPaint = new Paint();


	public GProgressBar(GParent gParent, int x, int y, int width, int height) {
		super(gParent, x, y, true);

		mWidth = width;
		mHeight = height;

		mBorderPaint.setStyle(Paint.Style.STROKE);
	}

	public void setBorderWidth(int width) {
		mBorderPaint.setStrokeWidth(width);
	}

	public void setBorderColor(int color) {
		mBorderPaint.setColor(color);
	}

	public void setBackgroundColor(int color) {
		mBackgroundPaint.setColor(color);
	}

	public void setForegroundColor(int color) {
		mForegroundPaint.setColor(color);
	}


	public void setPadding(int padding) {
		mPadding = padding;
	}

	public int getPadding() {
		return mPadding;
	}

	public void setProgress(float progress) {
		if (progress > 1) progress = 1f;
		if (progress < 0) progress = 0f;
		mProgress = progress;
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
		// Draw background
		canvas.drawRect(getLeft(), getTop(),
				getRight(), getBottom(), mBackgroundPaint);

		// Draw border
		canvas.drawRect(
				getLeft(), getTop(),
				getRight(), getBottom(),
				mBorderPaint
		);

		// Draw bar
		canvas.drawRect(
				getLeft() + getPadding(),
				getTop() + getPadding(),
				getLeft() + getPadding() + mProgress*(getWidth() - 2*getPadding()),
				getBottom() - getPadding(), mForegroundPaint
		);
	}
}
