package simon.app.quoridor;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
	private final SurfaceHolder mSurfaceHolder;
	private GameView mGameView;
	private boolean running;
	private static Canvas mCanvas;


	public GameThread(SurfaceHolder holder, GameView gameView) {
		mSurfaceHolder = holder;
		mGameView = gameView;
	}

	@Override
	public void run() {

		while (running) {

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


		}
	}

	public void setRunning(boolean isRunning) {
		running = isRunning;
	}
}
