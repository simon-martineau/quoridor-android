package simon.app.quoridor;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class GTitleView extends GView {
	private String mText;
	private Paint mPaint;

	// Constructor for centered text
	public GTitleView(String text, int color, float textSize, int parentWidth) {
		super(0, 0);
		mText = text;

		mPaint = new Paint();
		mPaint.setColor(color);
		mPaint.setTextSize(textSize);
		mPaint.setTextAlign(Paint.Align.CENTER);

		setX(parentWidth / 2 - getTextWidth() / 2);
	}

	public GTitleView(int x, int y, String text, int color, float textSize) {
		super(x, y);
		mText = text;

		mPaint = new Paint();
		mPaint.setColor(color);
		mPaint.setTextSize(textSize);
	}

	public void setTypeFace(Typeface typeFace) {
		mPaint.setTypeface(typeFace);
	}

	@Override
	public int getWidth() {
		return getTextWidth();
	}

	@Override
	public int getHeight() {
		return getTextHeight();
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawText(mText, getLeft() + getWidth() / 2f, getBottom(), mPaint);
	}

	private int getTextWidth() {
		Rect bounds = new Rect();
		mPaint.getTextBounds(mText, 0, mText.length(), bounds);

		return bounds.right - bounds.left;
	}

	private int getTextHeight() {
		Rect bounds = new Rect();
		mPaint.getTextBounds(mText, 0, mText.length(), bounds);

		return bounds.bottom - bounds.top;
	}
}