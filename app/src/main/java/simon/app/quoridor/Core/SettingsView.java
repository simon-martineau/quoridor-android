package simon.app.quoridor.Core;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import simon.app.quoridor.CustomViews.GButton;
import simon.app.quoridor.CustomViews.GTitleView;
import simon.app.quoridor.CustomViews.GView;
import simon.app.quoridor.Utils.MoreColors;

public class SettingsView extends WindowView {
	//==============================================================================================
	// GViews
	//==============================================================================================

	GTitleView mTitleView;
	GTitleView mPawnColorSettingLabel;
	GButton mPawnColorSettingButtonGreen;
	GButton mPawnColorSettingButtonWhite;
	GButton mPawnColorSettingButtonBlue;
	GButton mPawnColorSettingButtonYellow;

	GButton mBackButton;

	/**
	 * Constructor holding a reference to the AppView
	 *
	 * @param appView The AppView from which the WindowView is created
	 */
	public SettingsView(AppView appView) {
		super(appView);
	}


	private void setUpViews() {
		mGViews.clear();

		mTitleView = new GTitleView(this, 0, 100, "Settings", Color.GREEN, 128);
		mTitleView.setCenterHorizontal();

		mPawnColorSettingLabel = new GTitleView(this, 50, 400, "Pawn color", Color.GREEN, 64);

		mPawnColorSettingButtonBlue = new GButton(this, "Blue", 200, 100, 300, 400, GameView.DEFAULT_BUTTON_BACKGROUND_COLOR, Color.BLUE);
		mPawnColorSettingButtonBlue.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(WindowView windowView, int x, int y) {
				setPawnColor(Color.BLUE);
			}
		});

		mPawnColorSettingButtonGreen = new GButton(this, "Orange", 200, 100, 550, 400, GameView.DEFAULT_BUTTON_BACKGROUND_COLOR, MoreColors.ORANGE);
		mPawnColorSettingButtonGreen.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(WindowView windowView, int x, int y) {
				setPawnColor(MoreColors.ORANGE);
			}
		});

		mPawnColorSettingButtonYellow = new GButton(this, "Yellow", 200, 100, 800, 400, GameView.DEFAULT_BUTTON_BACKGROUND_COLOR, Color.YELLOW);
		mPawnColorSettingButtonYellow.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(WindowView windowView, int x, int y) {
				setPawnColor(Color.YELLOW);
			}
		});

		mPawnColorSettingButtonWhite = new GButton(this, "White", 200, 100, 1050, 400, GameView.DEFAULT_BUTTON_BACKGROUND_COLOR, Color.WHITE);
		mPawnColorSettingButtonWhite.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(WindowView windowView, int x, int y) {
				setPawnColor(Color.WHITE);
			}
		});

		mBackButton = new GButton(this, "Back", 300, 150, 150 ,getHeight() - 450, GameView.DEFAULT_BUTTON_BACKGROUND_COLOR, Color.WHITE);
		mBackButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(WindowView windowView, int x, int y) {
				getAppView().swapToMainMenuView();
			}
		});

	}

	private void setPawnColor(int color) {
		SharedPreferences.Editor editor = mAppView.getSharedPreferences().edit();
		editor.putInt("pawn_color", color);
		editor.apply();
	}

	@Override
	public void draw(Canvas canvas) {
		for (int i = mGViews.size() - 1; i >= 0; i--) {
			mGViews.get(i).draw(canvas);
		}
	}

	@SuppressWarnings("SwitchStatementWithTooFewBranches")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();


		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				dispatchTouchToViews(x, y);
				break;
		}

		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setUpViews();
	}
}
