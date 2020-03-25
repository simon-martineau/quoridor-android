package simon.app.quoridor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;


public class GButton {
	private Rect mRect;
	private Paint mPaint;
	private Paint mTextPaint;
	private String mText;

	int mWidth, mHeight;
	int posX, posY;

	public GButton(String text, int width, int height, int x, int y, int backgroundColor, int foreGroundColor)
	{
		mText = text;
		mWidth = width;
		mHeight = height;
		posX = x;
		posY = y;

		mPaint = new Paint();
		mPaint.setColor(backgroundColor);

		mTextPaint = new Paint();
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setTextSize(48);
		mTextPaint.setColor(foreGroundColor);

		mRect = new Rect(x, y, x + width, y + height);
	}

	public void setPosition(int x, int y)
	{
		posX = x;
		posY = y;
		mRect.set(new Rect(x, y, x + mWidth, y + mHeight));
	}

	public void draw(Canvas canvas)
	{
		canvas.drawRect(mRect, mPaint);
		canvas.drawText(mText, posX + mWidth/2.0f, posY + mHeight/2.0f - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
	}
}
