package simon.app.quoridor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Collections;

public class ModalView extends GView {
	private static final int LEFT_MARGIN = 100;
	private static final int RIGHT_MARGIN = 100;
	private static final int TOP_MARGIN = 100;
	private static final int BOTTOM_MARGIN = 100;
	private static final int TEXT_TO_GVIEW_SEPARATION = 50;
	private static final int GVIEW_TO_GVIEW_SEPARATION = 50;
	private static final int MESSAGE_SECTION_HEIGHT = 150; // TODO: make this flexible
	private static final float TEXT_SIZE = 64f;

	private static final int BORDER_COLOR = Color.WHITE;
	private static final int BACKGROUND_COLOR = Color.BLACK;
	private static final int BORDER_SIZE = 3;
	private static final int TEXT_COLOR = Color.GREEN;

	private GFreezeView mGFreezeView;
	private ArrayList<GView> mGViews = new ArrayList<>();
	private String mPromptString;
	private final GameView mGameView;

	// TODO: Implement offset button coordinates instead of absolute

	public ModalView(GameView gameView, String message, int x, int y) {
		super(gameView, x, y);

		setZIndex(100);

		mPromptString = message;

		setOnClickAction(new onClickAction() {
			@Override
			public void onClick(GameView gameView, int x, int y) {
				for (GView gView: mGViews) {
					if (gView.isInRect(x, y)) {
						gView.performClick(gameView, x, y);
						break;
					}
				}
			}
		});

		// Default FreezeView
		setFreezeView(new GFreezeView(this, 0, 0, gameView.getWidth(), gameView.getHeight(), Color.WHITE, 50, false));

		mGameView = gameView;
	}

	public void setFreezeView(GFreezeView gFreezeView) {
		mGFreezeView = gFreezeView;
		mGFreezeView.setZIndex(-1);
	}

	public void addGButton(int foreGroundColor, int backgroundColor, int width, int height, String text, onClickAction action) {
		GButton newButton = new GButton(this, text, width, height, getNextButtonX(),
				getTop() + TOP_MARGIN + MESSAGE_SECTION_HEIGHT + TEXT_TO_GVIEW_SEPARATION, backgroundColor, foreGroundColor, true);
		newButton.setOnClickAction(action);
	}

	/**
	 * Registers a view in mGViews. GViews is sorted by zIndex after each call
	 * @param gView The view to register
	 */
	public void registerGView(GView gView) {
		mGViews.add(gView);
		sortViews();
	}

	public void sortViews() {
		Collections.sort(mGViews);
		Collections.reverse(mGViews);
	}

	private int getHighestButtonBottomValue() {
		int highestValue = TOP_MARGIN + MESSAGE_SECTION_HEIGHT + TEXT_TO_GVIEW_SEPARATION;
		for (GView gView: mGViews) {
			if (gView.getBottom() > highestValue) highestValue = gView.getBottom();
		}
		return highestValue;
	}

	private int getNextButtonX() {
		if (mGViews.isEmpty()) {
			return (getLeft() + LEFT_MARGIN);
		} else {
			return (mGViews.get(0).getRight() + GVIEW_TO_GVIEW_SEPARATION);
		}
	}

	private int getTextWidth() {
		final Paint testPaint = new Paint();

		// Get the bounds of the text, using our testTextSize.
		testPaint.setTextSize(TEXT_SIZE);
		Rect bounds = new Rect();
		testPaint.getTextBounds(mPromptString, 0, mPromptString.length(), bounds);

		// Calculate the desired size as a proportion of our testTextSize.
		return bounds.right - bounds.left;
	}

	@Override
	public int getWidth() {

		if (getTextWidth() < getNextButtonX() - GVIEW_TO_GVIEW_SEPARATION - LEFT_MARGIN - getLeft()) {
			return getNextButtonX() - GVIEW_TO_GVIEW_SEPARATION - getLeft() + RIGHT_MARGIN;
		} else {
			return LEFT_MARGIN + getTextWidth() + RIGHT_MARGIN;
		}
	}

	@Override
	public int getHeight() {
		return getHighestButtonBottomValue() + BOTTOM_MARGIN - getTop();

	}

	@Override
	public void draw(Canvas canvas) {
		if (isVisible()) {
			Rect viewRect = new Rect(getLeft(), getTop(), getRight(), getBottom());

			// Draw freeze view if any
			if (mGFreezeView != null) mGFreezeView.draw(canvas);

			// Draw background
			Paint backgroundPaint = new Paint();
			backgroundPaint.setColor(BACKGROUND_COLOR);
			canvas.drawRect(viewRect, backgroundPaint);


			// Draw border
			Paint borderPaint = new Paint();
			borderPaint.setStrokeWidth(BORDER_SIZE);
			borderPaint.setStyle(Paint.Style.STROKE);
			borderPaint.setColor(BORDER_COLOR);
			canvas.drawRect(viewRect, borderPaint);

			// Draw text
			Paint textPaint = new Paint();
			textPaint.setColor(TEXT_COLOR);
			textPaint.setTextSize(TEXT_SIZE);
			textPaint.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(mPromptString, getLeft() + getWidth() / 2f,
					getTop() + TOP_MARGIN + MESSAGE_SECTION_HEIGHT / 2f -  ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);

			// Draw the buttons
			for (GView gView : mGViews) {
				gView.draw(canvas);
			}
		}
	}

	/**
	 * Override of the isInRect method from GView
	 * @see GView#isInRect(int x, int y)
	 * @param x The x coordinate (pixels)
	 * @param y The y coordinate (pixels)
	 * @return True if the modal view is set to visible, false otherwise
	 */
	@Override
	public boolean isInRect(int x, int y) {
		return isVisible();
	}

	@Override
	public void setX(int x) {
		int offset = x - getLeft();
		for (GView gView : mGViews) {
			gView.setX(gView.getLeft() + offset);
		}
		super.setX(x);
	}

	@Override
	public void setY(int y) {
		int offset = y - getTop();
		for (GView gView : mGViews) {
			gView.setY(gView.getTop() + offset);
		}
		super.setY(y);
	}
}
