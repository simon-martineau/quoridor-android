package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Paint;

import simon.app.quoridor.Core.GParent;
import simon.app.quoridor.WindowViews.WindowView;

public class GNumberStream extends GView {
	int mXStep = 0;
	int mYStep = 0;

	int mStreamSize = 200;
	int mEraserIndex = -100;
	int mLastNumberIndex = 0;

	private boolean doDraw = true;

	Paint mPaint = new Paint();


	public GNumberStream(GParent gParent, int x, int y, int textSize, int color) {
		super(gParent, x, y, true);
		mPaint.setTextSize(textSize);
		mPaint.setColor(color);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setAlpha(175);

		mYStep = textSize;
		setZIndex(-99);
	}

	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	public void setStreamSize(int size) {
		mStreamSize = size;
	}

	public void setEraserIndex(int index) {
		mEraserIndex = index;
	}

	public int getXStep() {
		return mXStep;
	}

	public int getYStep() {
		return mYStep;
	}

	public void setXStep(int step) {
		mXStep = step;
	}

	public void setYStep(int step) {
		mYStep = step;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void draw(Canvas canvas) {

		if (doDraw) {
			int i = 0;
			if (mEraserIndex > i) i = mEraserIndex;

			while (i < mLastNumberIndex && i < mStreamSize - 1) {
				canvas.drawText(String.valueOf((int) (Math.random() * 9)),
						getLeft() + mXStep*i, getTop() + mYStep * i, mPaint);

				i++;
			}

			canvas.drawText("█", getLeft() + mXStep * mLastNumberIndex,
					getTop() + mYStep * mLastNumberIndex, mPaint);

			mEraserIndex++;
			mLastNumberIndex++;

			if (mLastNumberIndex > mStreamSize - 1)  mLastNumberIndex = mStreamSize - 1;
			if (mEraserIndex >= mStreamSize) {
				doDraw = false;
				setVisible(false);
			}
		}
	}

	@Override
	public boolean isInRect(int x, int y) {
		return false;
	}

}
