package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Paint;

import simon.app.quoridor.WindowViews.WindowView;


public class GButton extends GView {

	private Paint mPaint;
	private Paint mTextPaint;
	private String mText;

	private int mWidth, mHeight;

	public GButton(WindowView windowView, String text, int width, int height, int x, int y, int backgroundColor, int foreGroundColor)
	{
		super(windowView, x, y);

		mText = text;
		mWidth = width;
		mHeight = height;


		mPaint = new Paint();
		mPaint.setColor(backgroundColor);

		mTextPaint = new Paint();
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(48);
		mTextPaint.setColor(foreGroundColor);

	}

	public GButton(GModalView gModalView, String text, int width, int height, int x, int y, int backgroundColor, int foreGroundColor, boolean register)
	{
		super(gModalView, x, y, register);

		mText = text;
		mWidth = width;
		mHeight = height;


		mPaint = new Paint();
		mPaint.setColor(backgroundColor);

		mTextPaint = new Paint();
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(48);
		mTextPaint.setColor(foreGroundColor);

	}



	public void setText(String text) {
		mText = text;
	}

	public void setTextSize(int textSize) {mTextPaint.setTextSize(textSize);}

	public void setTextColor(int color) {
		mTextPaint.setColor(color);
	}

	public void setBackgroundColor(int color) {
		mPaint.setColor(color);
	}

	public String getText() {
		return mText;
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
	public void draw(Canvas canvas)
	{
		if (isVisible()) {
			canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), mPaint);
			canvas.drawText(mText, getLeft() + mWidth / 2.0f, getTop() + mHeight / 2.0f - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
		}
	}


}
