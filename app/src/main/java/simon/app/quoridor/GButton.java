package simon.app.quoridor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;


public class GButton {
	private Rect mRect;
	private Paint mPaint;
	private Paint mTextPaint;
	private String mText;
	private boolean visible = true;

	public onClickAction mOnClickAction;

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

	public void setVisible(boolean isVisible) {
		visible = isVisible;
	}

	public void setText(String text) {
		mText = text;
	}
	public void setTextColor(int color) {
		mTextPaint.setColor(color);
	}
	public void setBackgroundColor(int color) {
		mPaint.setColor(color);
	}

	public String getText() {
		return mText;
	}

	public interface onClickAction {
		void onClick(GameView gameView);
	}

	public void setOnClickAction(onClickAction action) {
		mOnClickAction = action;
	}

	public void draw(Canvas canvas)
	{
		if (visible) {
			canvas.drawRect(mRect, mPaint);
			canvas.drawText(mText, posX + mWidth / 2.0f, posY + mHeight / 2.0f - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
		}
	}



	public boolean isInRect(int x, int y) {
		// Does not consume the event if button is visible
		return (mRect.left < x && x < mRect.right && mRect.top < y && y < mRect.bottom && visible);
	}

}
