package simon.app.quoridor.Core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import simon.app.quoridor.CustomViews.GView;

public abstract class GParent {

	protected List<GView> mGViews = new ArrayList<>();
	protected boolean isParent = false;

	public void registerGView(GView gView) {
		mGViews.add(gView);
		sortViews();
	}

	public void sortViews() {
		Collections.sort(mGViews);
		Collections.reverse(mGViews);
	}

	public abstract int getWidth();
	public abstract int getHeight();
}
