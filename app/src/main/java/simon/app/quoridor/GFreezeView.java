package simon.app.quoridor;

import android.graphics.Canvas;
import android.graphics.Paint;

public class GFreezeView extends GView {
	private int mWidth;
	private int mHeight;
	private Paint mPaint;

	public GFreezeView(int x, int y, int width, int height, int color, int alpha) {
		super(x, y);
		mWidth = width;
		mHeight = height;

		mPaint = new Paint();
		mPaint.setColor(color);
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

	}
}
