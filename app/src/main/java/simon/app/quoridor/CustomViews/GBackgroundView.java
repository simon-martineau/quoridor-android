package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;

import simon.app.quoridor.Core.WindowView;

public class GBackgroundView extends GView {
	public GBackgroundView(WindowView windowView, int x, int y) {
		super(windowView, x, y);
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
