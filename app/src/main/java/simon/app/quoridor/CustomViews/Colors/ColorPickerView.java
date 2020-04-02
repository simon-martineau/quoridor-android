package simon.app.quoridor.CustomViews.Colors;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Button;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import simon.app.quoridor.Core.GParent;
import simon.app.quoridor.CustomViews.GView;
import simon.app.quoridor.Utils.MoreColors;
import simon.app.quoridor.WindowViews.WindowView;

import static android.content.ContentValues.TAG;

public class ColorPickerView extends GView {
	// =============================================================================================
	// Constants
	// =============================================================================================
	private static final int PADDING = 50;
	private static final int SPACE_BETWEEN_COLORS = 24;
	private static final int COLOR_SIZE = 150;
	private static final List<Integer> COLOR_POOL = new ArrayList<>(Arrays.asList(
			MoreColors.ORANGE, MoreColors.YELLOW, MoreColors.GREEN, MoreColors.CYAN,
			MoreColors.BLUE, MoreColors.VIOLET, MoreColors.PINK, MoreColors.RED
	));


	// =============================================================================================
	// Views
	// =============================================================================================
	private ColorView mColorView0;
	private ColorView mColorView1;
	private ColorView mColorView2;
	private ColorView mColorView3;
	private ColorView mColorView4;
	private ColorView mColorView5;
	private ColorView mColorView6;
	private ColorView mColorView7;
	private boolean mIsColorPicked = false;

	// =============================================================================================
	// Others
	// =============================================================================================
	private Paint mBackgroundPaint = new Paint();
	private Paint mBorderPaint = new Paint();
	private int mSelectedColor = -1;
	private ColorPickCallBack mColorPickCallBack;


	public ColorPickerView(GParent gParent, int x, int y) {
		super(gParent, x, y, true);
		setZIndex(50);

		isParent = true;
		setOnClickAction(new onClickAction() {
			@Override
			public void onClick(int x, int y) {
				if (mIsColorPicked) {
					mColorPickCallBack.onColorPick(mSelectedColor);
				} else {
					mColorPickCallBack.onDismiss();
				}
			}
		});

		setUpPaints();
		setUpViews();
	}

	private void setUpPaints() {
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setColor(Color.WHITE);

		mBackgroundPaint.setColor(Color.BLACK);
	}

	private void setUpViews() {
		// TODO: Find a way to not hard-code this (maybe?)
		{
			mColorView0 = new ColorView(this,
					PADDING,
					PADDING,
					COLOR_SIZE, COLOR_SIZE, COLOR_POOL.get(0), true);
			mColorView0.setOnClickAction(new onClickAction() {
				@Override
				public void onClick(int x, int y) {
					mSelectedColor = mColorView0.getColor();
					mIsColorPicked = true;
				}
			});

			mColorView1 = new ColorView(this,
					PADDING,
					PADDING + (SPACE_BETWEEN_COLORS + COLOR_SIZE),
					COLOR_SIZE, COLOR_SIZE, COLOR_POOL.get(1), true);
			mColorView1.setOnClickAction(new onClickAction() {
				@Override
				public void onClick(int x, int y) {
					mSelectedColor = mColorView1.getColor();
					mIsColorPicked = true;
				}
			});

			mColorView2 = new ColorView(this,
					PADDING + (SPACE_BETWEEN_COLORS + COLOR_SIZE),
					PADDING,
					COLOR_SIZE, COLOR_SIZE, COLOR_POOL.get(2), true);
			mColorView2.setOnClickAction(new onClickAction() {
				@Override
				public void onClick(int x, int y) {
					mSelectedColor = mColorView2.getColor();
					mIsColorPicked = true;
				}
			});

			mColorView3 = new ColorView(this,
					PADDING + (SPACE_BETWEEN_COLORS + COLOR_SIZE),
					PADDING + (SPACE_BETWEEN_COLORS + COLOR_SIZE),
					COLOR_SIZE, COLOR_SIZE, COLOR_POOL.get(3), true);
			mColorView3.setOnClickAction(new onClickAction() {
				@Override
				public void onClick(int x, int y) {
					mSelectedColor = mColorView3.getColor();
					mIsColorPicked = true;
				}
			});

			mColorView4 = new ColorView(this,
					PADDING + 2 * (SPACE_BETWEEN_COLORS + COLOR_SIZE),
					PADDING,
					COLOR_SIZE, COLOR_SIZE, COLOR_POOL.get(4), true);
			mColorView4.setOnClickAction(new onClickAction() {
				@Override
				public void onClick(int x, int y) {
					mSelectedColor = mColorView4.getColor();
					mIsColorPicked = true;
				}
			});

			mColorView5 = new ColorView(this,
					PADDING + 2 * (SPACE_BETWEEN_COLORS + COLOR_SIZE),
					PADDING + (SPACE_BETWEEN_COLORS + COLOR_SIZE),
					COLOR_SIZE, COLOR_SIZE, COLOR_POOL.get(5), true);
			mColorView5.setOnClickAction(new onClickAction() {
				@Override
				public void onClick(int x, int y) {
					mSelectedColor = mColorView5.getColor();
					mIsColorPicked = true;
				}
			});

			mColorView6 = new ColorView(this,
					PADDING + 3 * (SPACE_BETWEEN_COLORS + COLOR_SIZE),
					PADDING,
					COLOR_SIZE, COLOR_SIZE, COLOR_POOL.get(6), true);
			mColorView6.setOnClickAction(new onClickAction() {
				@Override
				public void onClick(int x, int y) {
					mSelectedColor = mColorView6.getColor();
					mIsColorPicked = true;
				}
			});

			mColorView7 = new ColorView(this,
					PADDING + 3 * (SPACE_BETWEEN_COLORS + COLOR_SIZE),
					PADDING + (SPACE_BETWEEN_COLORS + COLOR_SIZE),
					COLOR_SIZE, COLOR_SIZE, COLOR_POOL.get(7), true);
			mColorView7.setOnClickAction(new onClickAction() {
				@Override
				public void onClick(int x, int y) {
					mSelectedColor = mColorView7.getColor();
					mIsColorPicked = true;
					Log.i(TAG, "onClick: white");
				}
			});

		}
	}


	public interface ColorPickCallBack {
		void onColorPick(int color);
		void onDismiss();
	}

	public void setColorPickCallBack(ColorPickCallBack colorPickCallBack) {
		mColorPickCallBack = colorPickCallBack;
	}


	@Override
	public int getWidth() {
		return PADDING*2 + SPACE_BETWEEN_COLORS*3 + COLOR_SIZE*4;
	}

	@Override
	public int getHeight() {
		return PADDING*2 + SPACE_BETWEEN_COLORS + COLOR_SIZE*2;
	}

	@Override
	public void draw(Canvas canvas) {
		if (isVisible()) {
			// Draw background
			canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), mBackgroundPaint);

			// Draw border
			canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), mBorderPaint);

			// Draw children
			drawChildren(canvas);
		}
	}

	@Override
	public void performClick(int x, int y) {
		Log.i(TAG, "performClick: color picker perform click");
		if (isParent) {
			int relX = x - getLeft();
			int relY = y - getTop();
			for (GView gView : mGViews) {
				if (gView.isInRect(relX, relY)) {
					gView.performClick(relX, relY);
					break;
				}
			}
		}
		if (hasOnClick) {
			mOnClickAction.onClick(x, y);
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		mIsColorPicked = false;
	}

	@Override
	public boolean isInRect(int x, int y) {
		return isVisible();
	}
}
