package simon.app.quoridor.WindowViews;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Button;

import simon.app.quoridor.Core.AppView;
import simon.app.quoridor.CustomViews.Colors.ColorPickerView;
import simon.app.quoridor.CustomViews.Colors.ColorView;
import simon.app.quoridor.CustomViews.GButton;
import simon.app.quoridor.CustomViews.GTitleView;
import simon.app.quoridor.CustomViews.GView;
import simon.app.quoridor.Utils.MoreColors;

public class SettingsView extends WindowView {
	//==============================================================================================
	// Default preferences
	//==============================================================================================

	public static final int DEFAULT_PAWN_COLOR = Color.BLUE;
	public static final int DEFAULT_ENEMY_PAWN_COLOR = Color.RED;
	public static final int DEFAULT_WALL_COLOR = Color.GREEN;


	//==============================================================================================
	// Constants
	//==============================================================================================

	private static final int OPTION_LABELS_LEFT_FROM_LEFT = 100;
	private static final int OPTION_BUTTON_RIGHT_FROM_RIGHT = 100;
	private static final int OPTION_DESCRIPTOR_LEFT_FROM_LABEL_RIGHT = 50;

	private static final int OPTION_1_MIDDLE_Y = 500;
	private static final int OPTION_2_MIDDLE_Y = 700;
	private static final int OPTION_3_MIDDLE_Y = 900;


	//==============================================================================================
	// GViews
	//==============================================================================================

	GTitleView mTitleView;

	// Option 1
	GTitleView mPawnColorSettingLabel;
	ColorView mPawnColorSettingColorPreview;
	GButton mPawnColorSettingChangeButton;
	ColorPickerView mPawnColorSettingColorPicker;

	// Option 2
	GTitleView mEnemyPawnColorSettingLabel;
	ColorView mEnemyPawnColorSettingColorPreview;
	GButton mEnemyPawnColorSettingChangeButton;
	ColorPickerView mEnemyPawnColorSettingColorPicker;

	// Option 2
	GTitleView mWallColorSettingLabel;
	ColorView mWallColorSettingColorPreview;
	GButton mWallColorSettingChangeButton;
	ColorPickerView mWallColorSettingColorPicker;


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

		// =========================================================================================
		// Option 1
		// =========================================================================================

		mPawnColorSettingLabel = new GTitleView(this, OPTION_LABELS_LEFT_FROM_LEFT, 0, "Pawn color:", Color.WHITE, 64);
		mPawnColorSettingLabel.setYFromViewCenter(OPTION_1_MIDDLE_Y);

		mPawnColorSettingColorPreview = new ColorView(this, mPawnColorSettingLabel.getRight() + OPTION_DESCRIPTOR_LEFT_FROM_LABEL_RIGHT, 0, 100, 100,
				getAppView().getSharedPreferences().getInt("pawn_color", DEFAULT_PAWN_COLOR), true);
		mPawnColorSettingColorPreview.setYFromViewCenter(OPTION_1_MIDDLE_Y);

		mPawnColorSettingChangeButton = new GButton(this, "Change", 300, 150, getWidth() - 300 - OPTION_BUTTON_RIGHT_FROM_RIGHT, 0,
				GameView.DEFAULT_BUTTON_BACKGROUND_COLOR, Color.WHITE, true);
		mPawnColorSettingChangeButton.setYFromViewCenter(OPTION_1_MIDDLE_Y);
		mPawnColorSettingChangeButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				mPawnColorSettingColorPicker.setVisible(true);
			}
		});

		mPawnColorSettingColorPicker = new ColorPickerView(this, 400, 200);
		mPawnColorSettingColorPicker.setColorPickCallBack(new ColorPickerView.ColorPickCallBack() {
			@Override
			public void onColorPick(int color) {
				setPrefInt("pawn_color", color);
				mPawnColorSettingColorPreview.setColor(color);
				mPawnColorSettingColorPicker.setVisible(false);
			}

			@Override
			public void onDismiss() {
				mPawnColorSettingColorPicker.setVisible(false);
			}
		});
		mPawnColorSettingColorPicker.setY(mPawnColorSettingColorPreview.getBottom() + 25);
		mPawnColorSettingColorPicker.setX(OPTION_LABELS_LEFT_FROM_LEFT);
		mPawnColorSettingColorPicker.setVisible(false);

		// =========================================================================================
		// Option 2
		// =========================================================================================

		mEnemyPawnColorSettingLabel = new GTitleView(this, OPTION_LABELS_LEFT_FROM_LEFT, 0, "Enemy pawn color:", Color.WHITE, 64);
		mEnemyPawnColorSettingLabel.setYFromViewCenter(OPTION_2_MIDDLE_Y);

		mEnemyPawnColorSettingColorPreview = new ColorView(this, mEnemyPawnColorSettingLabel.getRight() + OPTION_DESCRIPTOR_LEFT_FROM_LABEL_RIGHT, 0, 100, 100,
				getAppView().getSharedPreferences().getInt("enemy_pawn_color", DEFAULT_ENEMY_PAWN_COLOR), true);
		mEnemyPawnColorSettingColorPreview.setYFromViewCenter(OPTION_2_MIDDLE_Y);

		mEnemyPawnColorSettingChangeButton = new GButton(this, "Change", 300, 150, getWidth() - 300 - OPTION_BUTTON_RIGHT_FROM_RIGHT, 0,
				GameView.DEFAULT_BUTTON_BACKGROUND_COLOR, Color.WHITE, true);
		mEnemyPawnColorSettingChangeButton.setYFromViewCenter(OPTION_2_MIDDLE_Y);
		mEnemyPawnColorSettingChangeButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				mEnemyPawnColorSettingColorPicker.setVisible(true);
			}
		});

		mEnemyPawnColorSettingColorPicker = new ColorPickerView(this, 400, 200);
		mEnemyPawnColorSettingColorPicker.setColorPickCallBack(new ColorPickerView.ColorPickCallBack() {
			@Override
			public void onColorPick(int color) {
				setPrefInt("enemy_pawn_color", color);
				mEnemyPawnColorSettingColorPreview.setColor(color);
				mEnemyPawnColorSettingColorPicker.setVisible(false);
			}

			@Override
			public void onDismiss() {
				mEnemyPawnColorSettingColorPicker.setVisible(false);
			}
		});
		mEnemyPawnColorSettingColorPicker.setY(mEnemyPawnColorSettingColorPreview.getBottom() + 25);
		mEnemyPawnColorSettingColorPicker.setX(OPTION_LABELS_LEFT_FROM_LEFT);
		mEnemyPawnColorSettingColorPicker.setVisible(false);

		// =========================================================================================
		// Option 3
		// =========================================================================================

		mWallColorSettingLabel = new GTitleView(this, OPTION_LABELS_LEFT_FROM_LEFT, 0, "Wall color:", Color.WHITE, 64);
		mWallColorSettingLabel.setYFromViewCenter(OPTION_3_MIDDLE_Y);

		mWallColorSettingColorPreview = new ColorView(this, mWallColorSettingLabel.getRight() + OPTION_DESCRIPTOR_LEFT_FROM_LABEL_RIGHT, 0, 100, 100,
				getAppView().getSharedPreferences().getInt("wall_color", DEFAULT_WALL_COLOR), true);
		mWallColorSettingColorPreview.setYFromViewCenter(OPTION_3_MIDDLE_Y);

		mWallColorSettingChangeButton = new GButton(this, "Change", 300, 150, getWidth() - 300 - OPTION_BUTTON_RIGHT_FROM_RIGHT, 0,
				GameView.DEFAULT_BUTTON_BACKGROUND_COLOR, Color.WHITE, true);
		mWallColorSettingChangeButton.setYFromViewCenter(OPTION_3_MIDDLE_Y);
		mWallColorSettingChangeButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				mWallColorSettingColorPicker.setVisible(true);
			}
		});

		mWallColorSettingColorPicker = new ColorPickerView(this, 400, 200);
		mWallColorSettingColorPicker.setColorPickCallBack(new ColorPickerView.ColorPickCallBack() {
			@Override
			public void onColorPick(int color) {
				setPrefInt("wall_color", color);
				mWallColorSettingColorPreview.setColor(color);
				mWallColorSettingColorPicker.setVisible(false);
			}

			@Override
			public void onDismiss() {
				mWallColorSettingColorPicker.setVisible(false);
			}
		});
		mWallColorSettingColorPicker.setY(mWallColorSettingColorPreview.getBottom() + 25);
		mWallColorSettingColorPicker.setX(OPTION_LABELS_LEFT_FROM_LEFT);
		mWallColorSettingColorPicker.setVisible(false);

		// =========================================================================================
		// Others
		// =========================================================================================

		mBackButton = new GButton(this, "Back", 300, 150, 150 ,getHeight() - 450, GameView.DEFAULT_BUTTON_BACKGROUND_COLOR, Color.WHITE, true);
		mBackButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				getAppView().swapToMainMenuView();
			}
		});

	}

	private void setPrefInt(String key, int pref) {
		SharedPreferences.Editor editor = mAppView.getSharedPreferences().edit();
		editor.putInt(key, pref);
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
