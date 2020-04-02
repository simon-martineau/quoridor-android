package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import simon.app.quoridor.Core.GParent;
import simon.app.quoridor.WindowViews.WindowView;

public class GLine extends GView{

	private int mX2;
	private int mY2;
	private Paint mPaint = new Paint();

	public GLine(GParent gParent, int x1, int y1, int x2, int y2, int width, boolean register) {
		super(gParent, x1, y1, register);
		mX2 = x2;
		mY2 = y2;

		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(width);
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
		canvas.drawLine(getLeft(), getTop(), mX2, mY2, mPaint);
	}
}
