package simon.app.quoridor;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public abstract class GViewGroup {

	public abstract boolean	onTouchEvent(MotionEvent event);
	public abstract void draw(Canvas canvas);
	public abstract void update();
	public abstract void viewGroupDestroyed(SurfaceHolder holder);
	public abstract void viewGroupChanged()
}
