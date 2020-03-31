package simon.app.quoridor;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
	private final SurfaceHolder mSurfaceHolder;
	private GameView mGameView;
	private boolean running;
	private static Canvas mCanvas;

	double averageFPS;
	int mTargetFPS = 30;


	public GameThread(SurfaceHolder holder, GameView gameView) {
		super();
		mSurfaceHolder = holder;
		mGameView = gameView;
	}

	@Override
	public void run() {
		long startTime;
		long timeMillis;
		long waitTime;
		long totalTime = 0;
		int frameCount = 0;
		long targetTime = 1000 / mTargetFPS;

		while (running) {

			startTime = System.nanoTime();

			mCanvas = null;
			try {
				mCanvas = this.mSurfaceHolder.lockCanvas();
				synchronized (mSurfaceHolder) {
					this.mGameView.draw(mCanvas);
					this.mGameView.update();
				}
			} catch (Exception e) { e.printStackTrace(); } finally {
				if (mCanvas != null) {
					try {
						mSurfaceHolder.unlockCanvasAndPost(mCanvas);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}




			timeMillis = (System.nanoTime() - startTime) / 1000000;
			waitTime = targetTime - timeMillis;

			try {
				sleep(waitTime);
			} catch (Exception e) { }

			totalTime += System.nanoTime() - startTime;
			frameCount++;
			if (frameCount == mTargetFPS) {
				averageFPS = 1000 / ((totalTime / (float) frameCount) / 1000000f);
				frameCount = 0;
				totalTime = 0;
			}


		}
	}

	public void setRunning(boolean isRunning) {
		running = isRunning;
	}
}
