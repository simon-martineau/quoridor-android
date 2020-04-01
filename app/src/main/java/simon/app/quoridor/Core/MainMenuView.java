package simon.app.quoridor.Core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import simon.app.quoridor.CustomViews.GBackgroundView;
import simon.app.quoridor.CustomViews.GButton;
import simon.app.quoridor.CustomViews.GNumberStream;
import simon.app.quoridor.CustomViews.GTitleView;
import simon.app.quoridor.CustomViews.GView;
import simon.app.quoridor.R;

public class MainMenuView extends WindowView {
	private static Typeface DEFAULT_TYPEFACE;

	//==============================================================================================
	// Constants
	//==============================================================================================
	/**
	 * The default color to use for button background
	 */
	private final static int DEFAULT_BUTTON_BACKGROUND_COLOR = Color.rgb(40, 40, 40);

	/**
	 * Rate at which the GNumberStream are generated on average. Lower means more are generated.
	 */
	private final static int NUMBER_STREAM_GENERATING_RATE = 20;

	/**
	 * Frequency at which numberStreams are generated horizontally. The lower, the more there are.
	 */
	private final static int HORIZONTAL_STREAM_RATE = 200;


	/**
	 * Color pool for the numberStream randomizer
	 */
	private final static List<Integer> NUMBER_STREAM_COLOR_POOL = new ArrayList<>(Arrays.asList(
			Color.GREEN, Color.BLUE, Color.RED));
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

	/**
	 * Background
	 */
	GBackgroundView mBackground;

	/**
	 *
	 */
	GNumberStream mMyNumberStream;
	//==============================================================================================
	// Bitmaps
	//==============================================================================================

	/**
	 * Background Bitmap
	 */
	Bitmap mBackgroundBitmap;


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

//		mBackgroundBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(mAppView.getResources(), R.drawable.main_menu_background),
//				getWidth(), getHeight(), false);
//		mBackground = new GBackgroundView(this, 0, 0, mBackgroundBitmap);
//		mBackground.setAlphaPulsate(0, 255, 180);

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
		generateRandomNumberStream();
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

	/**
	 * Chance to generate a random GNumberStream for the main menu WindowView
	 */
	public void generateRandomNumberStream() {
		int matchAttempt = (int) (Math.random()*NUMBER_STREAM_GENERATING_RATE);

		if (matchAttempt == NUMBER_STREAM_GENERATING_RATE - 1) {
			Random rand = new Random();

			int x = rand.nextInt(getWidth());
			int y = rand.nextInt(getHeight() + 600) - 600;
			int size = 48;
			int color = NUMBER_STREAM_COLOR_POOL.get(rand.nextInt(NUMBER_STREAM_COLOR_POOL.size()));
			int eraserIndex = - rand.nextInt(50);

			GNumberStream gNumberStream = new GNumberStream(this, x, y, size, color);
			gNumberStream.setEraserIndex(eraserIndex);
			if (rand.nextInt(HORIZONTAL_STREAM_RATE) == HORIZONTAL_STREAM_RATE - 1) {
				gNumberStream.setXStep(gNumberStream.getYStep() - 8);
				gNumberStream.setYStep(0);
			}
		}


	}
}
