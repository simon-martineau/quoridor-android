package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import simon.app.quoridor.WindowViews.WindowView;

public class GTitleView extends GView {
	private String mText;
	private Paint mPaint;

	// Constructor for centered text
	public GTitleView(WindowView windowView, String text, int color, float textSize, int parentWidth) {
		super(windowView, 0, 0);
		mText = text;

		mPaint = new Paint();
		mPaint.setColor(color);
		mPaint.setTextSize(textSize);
		mPaint.setTextAlign(Paint.Align.CENTER);
		mPaint.setTypeface(mTypeFace);

		setX(parentWidth / 2 - getTextWidth() / 2);
	}

	public GTitleView(WindowView windowView, int x, int y, String text, int color, float textSize) {
		super(windowView, x, y);
		mText = text;

		mPaint = new Paint();
		mPaint.setColor(color);
		mPaint.setTextSize(textSize);
		mPaint.setTextAlign(Paint.Align.CENTER);

	}

//	@Override
//	public void setCenterHorizontal() {
//		setX(mWindowView.getWidth());
//	}

	public void setTypeFace(Typeface typeFace) {
		mTypeFace = typeFace;
		mPaint.setTypeface(mTypeFace);
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
