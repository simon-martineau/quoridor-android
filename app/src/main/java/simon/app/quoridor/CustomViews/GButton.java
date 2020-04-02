package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import simon.app.quoridor.Core.GParent;
import simon.app.quoridor.WindowViews.WindowView;


public class GButton extends GView {

	private Paint mPaint;
	private Paint mTextPaint;
	private Paint mBorderPaint;
	private boolean mHasBorder;
	private String mText;

	private int mWidth, mHeight;


	public GButton(GParent gParent, String text, int width, int height, int x, int y, int backgroundColor, int foreGroundColor, boolean register)
	{
		super(gParent, x, y, register);

		mText = text;
		mWidth = width;
		mHeight = height;


		mPaint = new Paint();
		mPaint.setColor(backgroundColor);

		mBorderPaint = new Paint();
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setStrokeWidth(2);
		mBorderPaint.setColor(Color.WHITE);

		mTextPaint = new Paint();
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(48);
		mTextPaint.setColor(foreGroundColor);

	}

	public void setBorderColor(int color) {
		mBorderPaint.setColor(color);
	}

	public void setBorderWidth(int width) {
		mBorderPaint.setStrokeWidth(width);
	}

	public void setBorder(boolean border) {
		mHasBorder = border;
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

			if (mHasBorder) {
				canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), mBorderPaint);
			}
		}
	}


}
