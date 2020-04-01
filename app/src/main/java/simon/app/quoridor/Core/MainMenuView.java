package simon.app.quoridor.Core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import simon.app.quoridor.CustomViews.GButton;
import simon.app.quoridor.CustomViews.GTitleView;
import simon.app.quoridor.CustomViews.GView;

public class MainMenuView extends WindowView {
	private static Typeface DEFAULT_TYPEFACE;

	//==============================================================================================
	// GViews
	//==============================================================================================
	/**
	 * The default color to use for button background
	 */
	private static int DEFAULT_BUTTON_BACKGROUND_COLOR = Color.rgb(40, 40, 40);

	//==============================================================================================
	// GViews
	//==============================================================================================
	/**
	 * App title header
	 */
	GTitleView mAppTitleView;

	/**
	 * Button to start game
	 */
	GButton mStartGameButton;

	//==============================================================================================
	// Constructors
	//==============================================================================================
	/**
	 * Constructor holding a reference to the AppView
	 *
	 * @param appView The AppView from which the WindowView is created
	 */
	public MainMenuView(AppView appView) {
		super(appView);

		DEFAULT_TYPEFACE = Typeface.createFromAsset(appView.getContext().getAssets(), "fonts/8_bit_style.ttf");
	}

	//==============================================================================================
	// Setup methods
	//==============================================================================================
	private void setUpViews() {

		mStartGameButton = new GButton(this, "Start game", 400, 200, 0, getHeight() / 2 + 100,
				DEFAULT_BUTTON_BACKGROUND_COLOR, Color.GREEN);
		mStartGameButton.setTextSize(64);
		mStartGameButton.setCenterHorizontal();
		mStartGameButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(WindowView windowView, int x, int y) {
				mAppView.swapToGameView();
			}
		});

		mAppTitleView = new GTitleView(this, 0, getHeight() / 3 - 100, "8 BIT QUORIDOR",
				Color.GREEN, 192);
		mAppTitleView.setTypeFace(DEFAULT_TYPEFACE);
		mAppTitleView.setCenterHorizontal();

	}

	//==============================================================================================
	// Overridden methods
	//==============================================================================================
	/**
	 * Called when the window is activated by the AppView.
	 */
	@Override
	public void onActivate() {
		super.onActivate();
	}

	/**
	 * Called when the window is deactivated by the AppView.
	 */
	@Override
	public void onDeactivate() {
		super.onDeactivate();
	}

	/**
	 * Draws the window to the canvas
	 * @param canvas The canvas to draw on
	 */
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
