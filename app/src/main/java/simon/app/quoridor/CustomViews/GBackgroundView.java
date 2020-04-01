package simon.app.quoridor.CustomViews;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import simon.app.quoridor.Core.WindowView;

public class GBackgroundView extends GView {
	private Bitmap mBitmap;

	public GBackgroundView(WindowView windowView, int x, int y, Bitmap bitmap) {
		super(windowView, x, y);

		mBitmap = bitmap;
		setZIndex(-100);
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
		canvas.drawBitmap(mBitmap, 0, 0, null);
	}
}
