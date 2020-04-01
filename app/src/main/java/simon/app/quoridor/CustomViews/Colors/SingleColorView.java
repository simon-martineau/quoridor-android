package simon.app.quoridor.CustomViews.Colors;

import android.graphics.Canvas;

import simon.app.quoridor.CustomViews.GView;
import simon.app.quoridor.WindowViews.WindowView;

public class SingleColorView extends GView {

	public SingleColorView(WindowView windowView, int x, int y) {
		super(windowView, x, y);
	}

	public SingleColorView(GView gView, int x, int y, boolean register) {
		super(gView, x, y, register);
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public void draw(Canvas canvas) {

	}
}
