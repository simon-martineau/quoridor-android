package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Paint;

import simon.app.quoridor.Core.GParent;
import simon.app.quoridor.WindowViews.GameView;

public class GFreezeView extends GView {
	private int mWidth;
	private int mHeight;
	private Paint mPaint;

	public GFreezeView(GParent gParent, int x, int y, int width, int height, int color, int alpha, boolean register) {
		super(gParent, x, y, register);
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
