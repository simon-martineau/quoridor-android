package simon.app.quoridor;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;


import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	// Threading
	public GameThread mGameThread;



	public GameView(Context context) {
		super(context);

		getHolder().addCallback(this);

		mGameThread = new GameThread(getHolder(), this);
		setFocusable(true);
	}


	public void update() {

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mGameThread.setRunning(true);
		mGameThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		while (retry) {
			try {
				mGameThread.setRunning(false);
				mGameThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			retry = false;
		}
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return super.onTouchEvent(event);
	}



	public static Bitmap scaleToHeight(Bitmap bmp, int height) {
		float aspectRatio = bmp.getWidth() / (float) bmp.getHeight();

		return Bitmap.createScaledBitmap(bmp, (int) (height * aspectRatio), height, true);
	}

}
