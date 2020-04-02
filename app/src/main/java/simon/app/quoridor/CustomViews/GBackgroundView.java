package simon.app.quoridor.CustomViews;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import simon.app.quoridor.WindowViews.WindowView;

public class GBackgroundView extends GView {
	private Bitmap mBitmap;

	private int mBaseAlpha;
	private int mCycleDuration;
	private int mAlphaAmplitude;
	private long mPulsateCounter = 0;
	private boolean mIsAlphaPulsate = false;

	public GBackgroundView(WindowView windowView, int x, int y, Bitmap bitmap) {
		super(windowView, x, y, true);

		mBitmap = bitmap;
		setZIndex(-100);
	}

	public void setAlphaPulsate(int minAlpha, int maxAlpha, int cycleDuration) {
			mBaseAlpha = (maxAlpha + minAlpha) / 2;
			mAlphaAmplitude = (maxAlpha - minAlpha) / 2;
			mCycleDuration = cycleDuration;
			mIsAlphaPulsate = true;
	}

	@Override
	public int getWidth() {
		return mBitmap.getWidth();
	}

	@Override
	public int getHeight() {
		return mBitmap.getHeight();
	}

	@Override
	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAlpha(255);

		if (mIsAlphaPulsate) {
			int alpha = (int) (mBaseAlpha + mAlphaAmplitude * Math.sin(2*Math.PI*((mPulsateCounter % mCycleDuration) / (float) mCycleDuration)));
			paint.setAlpha(alpha);
			mPulsateCounter++;
		}

		canvas.drawBitmap(mBitmap, getLeft(), getTop(), paint);
	}
}
