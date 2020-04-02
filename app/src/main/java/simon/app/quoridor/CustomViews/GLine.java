package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;

import simon.app.quoridor.WindowViews.WindowView;

public class GLine extends GView{
	private int mX2;
	private int mY2;

	public GLine(GView gView, int x, int y, boolean register) {
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
