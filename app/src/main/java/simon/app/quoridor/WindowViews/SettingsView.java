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
import simon.app.quoridor.CustomViews.GLine;
import simon.app.quoridor.CustomViews.GSectionFrame;
import simon.app.quoridor.CustomViews.GTitleView;
import simon.app.quoridor.CustomViews.GView;
import simon.app.quoridor.Utils.MoreColors;

public class SettingsView extends WindowView {
	SharedPreferences mSettings;

	//==============================================================================================
	// Default preferences
	//==============================================================================================

	public static final int DEFAULT_PAWN_COLOR = Color.BLUE;
	public static final int DEFAULT_ENEMY_PAWN_COLOR = Color.RED;
	public static final int DEFAULT_WALL_COLOR = Color.GREEN;
	public static final boolean DEFAULT_DRAW_PATH = false;
	public static final boolean DEFAULT_MUSIC = true;
	public static final boolean DEFAULT_SOUND_EFFECTS = true;

	//==============================================================================================
	// Constants
	//==============================================================================================

	private static final int OPTION_FRAMES_LEFT = 100;
	private static final int OPTION_FRAMES_RIGHT = 100;
	private static final int OPTION_FRAMES_MARGIN = 50;

	// Group 1
	private static final int OPTION_SECTION_1_FRAME_TOP = 350;
	private static final int OPTION_1_BOTTOM_Y = 500;
	private static final int OPTION_2_BOTTOM_Y = 650;
	private static final int OPTION_3_BOTTOM_Y = 800;
	private static final int OPTION_4_BOTTOM_Y = 950;
	private static final int OPTION_SECTION_1_FRAME_BOTTOM = 1000;

	private static final int OPTION_SECTION_2_FRAME_TOP = 1150;
	private static final int OPTION_5_BOTTOM_Y = 1300;
	private static final int OPTION_6_BOTTOM_Y = 1450;
	private static final int OPTION_SECTION_2_FRAME_BOTTOM = 1500;


	//==============================================================================================
	// GViews
	//==============================================================================================

	GTitleView mTitleView;
	GButton mBackButton;

	// Group 1 =====================================================================================
	GSectionFrame mSectionFrame1;

	// Option 1
	GTitleView mPawnColorSettingLabel;
	ColorView mPawnColorSettingColorPreview;
	ColorPickerView mPawnColorSettingColorPicker;
	GLine mPawnColorSettingLine;

	// Option 2
	GTitleView mEnemyPawnColorSettingLabel;
	ColorView mEnemyPawnColorSettingColorPreview;
	ColorPickerView mEnemyPawnColorSettingColorPicker;
	GLine mEnemyPawnColorSettingLine;

	// Option 2
	GTitleView mWallColorSettingLabel;
	ColorView mWallColorSettingColorPreview;
	ColorPickerView mWallColorSettingColorPicker;
	GLine mWallColorSettingLine;

	// Option 4
	GTitleView mDrawPathSettingLabel;
	GButton mDrawPathSettingButton;
	GLine mDrawPathSettingLine;

	// Group 2 =====================================================================================

	// Option 5
	GTitleView mMusicSettingLabel;
	GButton mMusicSettingButton;
	GLine mMusicSettingLine;

	// Option 6
	GTitleView mSoundEffectsSettingLabel;
	GButton mSoundEffectsSettingButton;
	GLine mSoundEffectsSettingLine;

	/**
	 * Constructor holding a reference to the AppView
	 *
	 * @param appView The AppView from which the WindowView is created
	 */
	public SettingsView(AppView appView) {
		super(appView);
		mSettings = getAppView().getSharedPreferences(AppView.DATA_SETTINGS);
	}


	private void setUpViews() {
		mGViews.clear();

		mTitleView = new GTitleView(this, 0, 100, "Settings", Color.GREEN, 128);
		mTitleView.setCenterHorizontal();

		// =========================================================================================
		// Section frame 1
		// =========================================================================================

		mSectionFrame1 = new GSectionFrame(this, OPTION_FRAMES_LEFT, OPTION_SECTION_1_FRAME_TOP,
				getRight() - OPTION_FRAMES_RIGHT, OPTION_SECTION_1_FRAME_BOTTOM, 5, true);
		mSectionFrame1.setCaption("Appearance", 64, Color.WHITE);

		// Option 1 ================================================================================

		mPawnColorSettingLabel = new GTitleView(this, OPTION_FRAMES_LEFT + OPTION_FRAMES_MARGIN, 0, "Pawn color:", Color.WHITE, 48);
		mPawnColorSettingLabel.setBottom(OPTION_1_BOTTOM_Y);

		mPawnColorSettingColorPreview = new ColorView(this, 0, 0, 100, 100,
				mSettings.getInt("pawn_color", DEFAULT_PAWN_COLOR), true);
		mPawnColorSettingColorPreview.setBottom(OPTION_1_BOTTOM_Y);
		mPawnColorSettingColorPreview.setRight(getWidth() - OPTION_FRAMES_LEFT - OPTION_FRAMES_MARGIN);
		mPawnColorSettingColorPreview.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				mPawnColorSettingColorPicker.setVisible(true);
			}
		});

		mPawnColorSettingLine = new GLine(this,
				mPawnColorSettingLabel.getRight() + 20, OPTION_1_BOTTOM_Y,
				mPawnColorSettingColorPreview.getLeft() - 20, OPTION_1_BOTTOM_Y, 3, true);


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
		mPawnColorSettingColorPicker.setYFromViewCenter(mPawnColorSettingColorPreview.getCenterY());
		mPawnColorSettingColorPicker.setCenterHorizontal();
		mPawnColorSettingColorPicker.setVisible(false);

		// Option 2 ================================================================================

		mEnemyPawnColorSettingLabel = new GTitleView(this, OPTION_FRAMES_LEFT + OPTION_FRAMES_MARGIN, 0, "Enemy pawn color:", Color.WHITE, 48);
		mEnemyPawnColorSettingLabel.setBottom(OPTION_2_BOTTOM_Y);

		mEnemyPawnColorSettingColorPreview = new ColorView(this, 0, 0, 100, 100,
				mSettings.getInt("enemy_pawn_color", DEFAULT_ENEMY_PAWN_COLOR), true);
		mEnemyPawnColorSettingColorPreview.setRight(getWidth() - OPTION_FRAMES_LEFT - OPTION_FRAMES_MARGIN);
		mEnemyPawnColorSettingColorPreview.setBottom(OPTION_2_BOTTOM_Y);
		mEnemyPawnColorSettingColorPreview.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				mEnemyPawnColorSettingColorPicker.setVisible(true);
			}
		});

		mEnemyPawnColorSettingLine = new GLine(this,
				mEnemyPawnColorSettingLabel.getRight() + 20, OPTION_2_BOTTOM_Y,
				mEnemyPawnColorSettingColorPreview.getLeft() - 20, OPTION_2_BOTTOM_Y, 3, true);

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
		mEnemyPawnColorSettingColorPicker.setYFromViewCenter(mEnemyPawnColorSettingColorPreview.getCenterY());
		mEnemyPawnColorSettingColorPicker.setCenterHorizontal();
		mEnemyPawnColorSettingColorPicker.setVisible(false);

		// Option 3 ================================================================================

		mWallColorSettingLabel = new GTitleView(this, OPTION_FRAMES_LEFT + OPTION_FRAMES_MARGIN, 0, "Wall color:", Color.WHITE, 48);
		mWallColorSettingLabel.setBottom(OPTION_3_BOTTOM_Y);

		mWallColorSettingColorPreview = new ColorView(this, 0, 0, 100, 100,
				mSettings.getInt("wall_color", DEFAULT_WALL_COLOR), true);
		mWallColorSettingColorPreview.setRight(getWidth() - OPTION_FRAMES_LEFT - OPTION_FRAMES_MARGIN);
		mWallColorSettingColorPreview.setBottom(OPTION_3_BOTTOM_Y);
		mWallColorSettingColorPreview.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				mWallColorSettingColorPicker.setVisible(true);
			}
		});

		mWallColorSettingLine = new GLine(this,
				mWallColorSettingLabel.getRight() + 20, OPTION_3_BOTTOM_Y,
				mWallColorSettingColorPreview.getLeft() - 20, OPTION_3_BOTTOM_Y, 3, true);

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
		mWallColorSettingColorPicker.setYFromViewCenter(mWallColorSettingColorPreview.getCenterY());
		mWallColorSettingColorPicker.setCenterHorizontal();
		mWallColorSettingColorPicker.setVisible(false);

		// Option 4 ================================================================================
		mDrawPathSettingLabel = new GTitleView(this, OPTION_FRAMES_LEFT + OPTION_FRAMES_MARGIN, 0, "Draw path to victory:", Color.WHITE, 48);
		mDrawPathSettingLabel.setBottom(OPTION_4_BOTTOM_Y);

		boolean drawPath = mSettings.getBoolean("draw_path", DEFAULT_DRAW_PATH);
		int textColor = drawPath ? Color.GREEN : Color.RED;
		String text = drawPath ? "Yes" : "No";
		mDrawPathSettingButton = new GButton(this, text, 200, 110, 0, 0, Color.BLACK, textColor, true);
		mDrawPathSettingButton.setBorder(true);
		mDrawPathSettingButton.setRight(getWidth() - OPTION_FRAMES_LEFT - OPTION_FRAMES_MARGIN);
		mDrawPathSettingButton.setBottom(OPTION_4_BOTTOM_Y);
		mDrawPathSettingButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				if (mDrawPathSettingButton.getText().equals("Yes")) {
					mDrawPathSettingButton.setText("No");
					mDrawPathSettingButton.setTextColor(Color.RED);
					setPrefBoolean("draw_path", false);

				} else {
					mDrawPathSettingButton.setText("Yes");
					mDrawPathSettingButton.setTextColor(Color.GREEN);
					setPrefBoolean("draw_path", true);
				}
			}
		});

		mDrawPathSettingLine = new GLine(this,
				mDrawPathSettingLabel.getRight() + 20, OPTION_4_BOTTOM_Y,
				mDrawPathSettingButton.getLeft() - 20, OPTION_4_BOTTOM_Y, 3, true);

		// =========================================================================================
		// Section frame 2 (Sounds)
		// =========================================================================================

		mSectionFrame1 = new GSectionFrame(this, OPTION_FRAMES_LEFT, OPTION_SECTION_2_FRAME_TOP,
				getRight() - OPTION_FRAMES_RIGHT, OPTION_SECTION_2_FRAME_BOTTOM, 5, true);
		mSectionFrame1.setCaption("Sound", 64, Color.WHITE);

		// Option 5 ================================================================================

		mMusicSettingLabel = new GTitleView(this, OPTION_FRAMES_LEFT + OPTION_FRAMES_MARGIN, 0, "Music:", Color.WHITE, 48);
		mMusicSettingLabel.setBottom(OPTION_5_BOTTOM_Y);

		boolean musicOn = mSettings.getBoolean("music", DEFAULT_MUSIC);
		int musicTextColor = musicOn ? Color.GREEN : Color.RED;
		String musicText = musicOn ? "On" : "Off";
		mMusicSettingButton = new GButton(this, musicText, 200, 110, 0, 0, Color.BLACK, musicTextColor, true);
		mMusicSettingButton.setBorder(true);
		mMusicSettingButton.setRight(getWidth() - OPTION_FRAMES_LEFT - OPTION_FRAMES_MARGIN);
		mMusicSettingButton.setBottom(OPTION_5_BOTTOM_Y);
		mMusicSettingButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				if (mMusicSettingButton.getText().equals("On")) {
					mMusicSettingButton.setText("Off");
					mMusicSettingButton.setTextColor(Color.RED);
					getAppView().stopMainMenuMusic();
					setPrefBoolean("music", false);

				} else {
					mMusicSettingButton.setText("On");
					mMusicSettingButton.setTextColor(Color.GREEN);
					getAppView().startMainMenuMusic();
					setPrefBoolean("music", true);
				}
			}
		});

		mMusicSettingLine = new GLine(this,
				mMusicSettingLabel.getRight() + 20, OPTION_5_BOTTOM_Y,
				mMusicSettingButton.getLeft() - 20, OPTION_5_BOTTOM_Y, 3, true);

		// Option 5 ================================================================================

		mSoundEffectsSettingLabel = new GTitleView(this, OPTION_FRAMES_LEFT + OPTION_FRAMES_MARGIN, 0, "Sound effects:", Color.WHITE, 48);
		mSoundEffectsSettingLabel.setBottom(OPTION_6_BOTTOM_Y);

		boolean soundEffectsOn = mSettings.getBoolean("sound_effects", DEFAULT_MUSIC);
		int soundEffectsTextColor = soundEffectsOn ? Color.GREEN : Color.RED;
		String soundEffectsText = soundEffectsOn ? "On" : "Off";
		mSoundEffectsSettingButton = new GButton(this, soundEffectsText, 200, 110, 0, 0, Color.BLACK, soundEffectsTextColor, true);
		mSoundEffectsSettingButton.setBorder(true);
		mSoundEffectsSettingButton.setRight(getWidth() - OPTION_FRAMES_LEFT - OPTION_FRAMES_MARGIN);
		mSoundEffectsSettingButton.setBottom(OPTION_6_BOTTOM_Y);
		mSoundEffectsSettingButton.setOnClickAction(new GView.onClickAction() {
			@Override
			public void onClick(int x, int y) {
				if (mSoundEffectsSettingButton.getText().equals("On")) {
					mSoundEffectsSettingButton.setText("Off");
					mSoundEffectsSettingButton.setTextColor(Color.RED);
					setPrefBoolean("sound_effects", false);

				} else {
					mSoundEffectsSettingButton.setText("On");
					mSoundEffectsSettingButton.setTextColor(Color.GREEN);
					setPrefBoolean("sound_effects", true);
				}
			}
		});

		mSoundEffectsSettingLine = new GLine(this,
				mSoundEffectsSettingLabel.getRight() + 20, OPTION_6_BOTTOM_Y,
				mSoundEffectsSettingButton.getLeft() - 20, OPTION_6_BOTTOM_Y, 3, true);



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

	private void setPrefBoolean(String key, boolean pref) {
		SharedPreferences.Editor editor = mSettings.edit();
		editor.putBoolean(key, pref);
		editor.apply();
	}

	private void setPrefInt(String key, int pref) {
		SharedPreferences.Editor editor = mSettings.edit();
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
