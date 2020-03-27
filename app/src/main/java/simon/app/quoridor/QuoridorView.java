package simon.app.quoridor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class QuoridorView {

	//// Logic
	// Hover cells and blinking
	private List<int[]> hoverPositions = new ArrayList<>();
	private boolean drawHover = false; // If true, hover is drawn (used for blinking)
	private final int blinkDelay = 30;
	private int blinkTimer = 0;
	private boolean blink = true;

	// Wall preview (wall placement)
	public int[] verticalWallPreview = null;
	public int[] horizontalWallPreview = null;
	private int wallBlinkTimer = 0;
	private boolean drawWallPreview = false;
	private final int wallBlinkDelay = 8;

	// Console
	private String mConsoleMessage = "";
	private boolean isConsoleMessageTimed = false;
	private int consoleMessageTimer = 0;

	// Action
	public onClickAction mOnClickAction;

	// Visible
	public boolean visible = true;


	//// Colors
	public int consoleMessageColor = Color.GREEN;
	public int wallPreviewColor = Color.GREEN;
	public int hoverColor = 0x3f001eff;
	public int wrapperColor = Color.WHITE;
	public int backgroundColor = Color.BLACK;
	public int gridColor = Color.rgb(50, 50, 50);
	public int gridBorderColor = Color.rgb(25, 85, 25);
	public int wallColor = Color.GREEN;
	public int playerOneColor = Color.BLUE;
	public int playerTwoColor = Color.RED;

	//// Dimensions
	// Margins
	public int gridMargin = 64;

	public int headerHeight = 250;

	public int cellSize;
	public int cellBorderWidth = 12;
	public int wrapperWidth = 5;
	public float mX = 50;
	public float mY = 50;

	public QuoridorView(int width) {
		cellSize = (int) ((width - (gridMargin*2 + wrapperWidth*2 + cellBorderWidth*10)) / 9.0);
	}

	public int getWidth() {
		return cellSize * 9 + gridMargin * 2 + wrapperWidth * 2;
	}

	public int getHeight() {
		return cellSize * 9 + gridMargin * 2 + wrapperWidth * 2 + headerHeight;
	}

	public void draw(Canvas canvas, Quoridor quoridorGame) {
		// Background
		Paint backgroundPaint = new Paint();
		backgroundPaint.setColor(backgroundColor);
		canvas.drawRect(mX, mY, mX + getWidth(), mY + getHeight(), backgroundPaint);

		// Wrapper
		Paint wrapperPaint = new Paint();
		wrapperPaint.setColor(wrapperColor);
		wrapperPaint.setStyle(Paint.Style.STROKE);
		wrapperPaint.setStrokeWidth(wrapperWidth);
		canvas.drawRect(mX, mY, mX + getWidth(), mY + getHeight(), wrapperPaint);

		// Header
		Paint playerOneInfoPaint = new Paint();
		playerOneInfoPaint.setColor(playerOneColor);
		playerOneInfoPaint.setTextSize(48);
		playerOneInfoPaint.setTextAlign(Paint.Align.LEFT);

		Paint playerTwoInfoPaint = new Paint();
		playerTwoInfoPaint.setColor(playerTwoColor);
		playerTwoInfoPaint.setTextSize(48);
		playerTwoInfoPaint.setTextAlign(Paint.Align.RIGHT);

		canvas.drawText("Player 1: " + quoridorGame.mPlayerOneName, mX + 24, mY + 64, playerOneInfoPaint);
		canvas.drawText("Player 2: " + quoridorGame.mPlayerTwoName, mX + getWidth() - 24, mY + 64, playerTwoInfoPaint);
		canvas.drawText("Walls left: " + quoridorGame.mPlayerOneWallsLeft, mX + 24, mY + 128, playerOneInfoPaint);
		canvas.drawText("Walls left: " + quoridorGame.mPlayerTwoWallsLeft, mX + getWidth() - 24, mY + 128, playerTwoInfoPaint);

		Paint consolePaint = new Paint();
		consolePaint.setColor(consoleMessageColor);
		consolePaint.setTextSize(84);
		consolePaint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(mConsoleMessage, mX + getWidth() / 2.0f, mY + 192, consolePaint);
		if (isConsoleMessageTimed) {
			consoleMessageTimer--;
			if (consoleMessageTimer <= 0) {
				consoleMessageTimer = 0;
				isConsoleMessageTimed = false;
				mConsoleMessage = "";
			}
		}


		// Players
		drawPlayer(canvas, 1, quoridorGame.mPlayerOnePosition[0], quoridorGame.mPlayerOnePosition[1]);
		drawPlayer(canvas, 2, quoridorGame.mPlayerTwoPosition[0], quoridorGame.mPlayerTwoPosition[1]);

		// Hover cells
		if (blink) {
			if (drawHover) {
				for (int[] coordinates : hoverPositions) {
					drawHover(canvas, coordinates[0], coordinates[1]);
				}
			}
			blinkTimer--;
			if (blinkTimer <= 0) {
				blinkTimer = blinkDelay;
				drawHover = !drawHover;
			}
		}

		// Grid
		drawGrid(canvas);

		// Walls
		drawWalls(canvas, quoridorGame.mHorizontalWalls, Quoridor.HORIZONTAL);
		drawWalls(canvas, quoridorGame.mVerticalWalls, Quoridor.VERTICAL);

		// Wall preview
		if (verticalWallPreview != null) {
			if (drawWallPreview)
				drawWallPreview(canvas, verticalWallPreview, Quoridor.VERTICAL);
			wallBlinkTimer--;
			if (wallBlinkTimer < 0) {
				drawWallPreview = !drawWallPreview;
				wallBlinkTimer = wallBlinkDelay;
			}
		} else if (horizontalWallPreview != null) {
			if (drawWallPreview)
				drawWallPreview(canvas, horizontalWallPreview, Quoridor.HORIZONTAL);
			wallBlinkTimer--;
			if (wallBlinkTimer < 0) {
				drawWallPreview = !drawWallPreview;
				wallBlinkTimer = wallBlinkDelay;
			}
		}

	}

	public void hoverCells(List<int[]> positions) {
		for (int[] coordinates : positions) {
			addHoverPosition(coordinates[0],coordinates[1]);
		}
	}

	public void addHoverPosition(int x, int y) {
		int[] coordinates = new int[2];
		coordinates[0] = x;
		coordinates[1] = y;
		hoverPositions.add(coordinates);
	}

	public void resetHoverPositions() {
		hoverPositions.clear();
	}

	private void drawHover(Canvas canvas, int x, int y) {
		Paint hoverPaint = new Paint();
		hoverPaint.setColor(hoverColor);
		float left = mX + gridMargin + (x - 1)*cellSize;
		float top = mY + headerHeight + (9 - y)*cellSize;

		canvas.drawRect(left, top, left + cellSize, top + cellSize, hoverPaint);
	}


	private void drawWalls(Canvas canvas, List<int[]> walls, int wallType) {
		float beginX = mX + gridMargin;
		float beginY = mY + headerHeight;

		Paint wallPaint = new Paint();
		wallPaint.setColor(wallColor);
		wallPaint.setStrokeWidth(cellBorderWidth);

		if (wallType == Quoridor.HORIZONTAL) {
			for (int[] coordinates : walls) {
				canvas.drawLine(beginX + (coordinates[0] - 1)*cellSize, beginY +(10 - coordinates[1])*cellSize,
						beginX + (coordinates[0] + 1)*cellSize, beginY + (10 - coordinates[1])*cellSize, wallPaint);
			}
		}

		if (wallType == Quoridor.VERTICAL) {
			for (int[] coordinates : walls) {
				canvas.drawLine(beginX + (coordinates[0] - 1)*cellSize, beginY + (9 - (coordinates[1] - 1))*cellSize,
						beginX + (coordinates[0] - 1)*cellSize, beginY + (9 - (coordinates[1] + 1))*cellSize, wallPaint);
			}
		}

	}

	private void drawPlayer(Canvas canvas, int playerNumber, int posX, int posY) {
		Paint playerPaint = new Paint();
		if (playerNumber == 1) {
			playerPaint.setColor(playerOneColor);
		} else {
			playerPaint.setColor(playerTwoColor);
		}

		float beginX = mX + gridMargin;
		float beginY = mY + headerHeight;
		float circleX = beginX + (posX - 1)*cellSize + cellSize / 2.0f;
		float circleY = beginY + (9 - posY)*cellSize + cellSize / 2.0f;
		canvas.drawCircle(circleX, circleY, cellSize/4.0f, playerPaint);


	}

	private void drawGrid(Canvas canvas) {
		Paint gridPaint = new Paint();
		gridPaint.setColor(gridColor);
		gridPaint.setStrokeWidth(cellBorderWidth);
		gridPaint.setTextSize(48);


		float beginX = mX + gridMargin;
		float beginY = mY + headerHeight;

		// Horizontal
		gridPaint.setTextAlign(Paint.Align.RIGHT);
		for (int i = 0; i < 10; i++) {
			if (0 < i && i < 9) canvas.drawLine(beginX + i*cellSize, beginY, beginX + i*cellSize, beginY + 9*cellSize, gridPaint);
			if (i > 0) {
				canvas.drawText(String.valueOf(10 - i), beginX - 24, beginY + i*cellSize - cellSize/2.0f - ((gridPaint.descent() + gridPaint.ascent()) / 2), gridPaint);
			}
		}

		// Vertical
		gridPaint.setTextAlign(Paint.Align.CENTER);
		for (int i = 0; i < 10; i++) {

			if (0 < i && i < 9) canvas.drawLine(beginX, beginY + i*cellSize, beginX + 9*cellSize, beginY + i*cellSize, gridPaint);
			if (i > 0) {
				canvas.drawText(String.valueOf(i), beginX + i*cellSize - cellSize/2.0f, beginY + 9*cellSize + 72 + ((gridPaint.descent() + gridPaint.ascent()) / 2), gridPaint);
			}
		}

		Paint borderGridPaint = new Paint();
		borderGridPaint.setColor(gridBorderColor);
		borderGridPaint.setStrokeWidth(cellBorderWidth);


		canvas.drawLine(beginX, beginY, beginX, beginY + 9*cellSize, borderGridPaint);
		canvas.drawLine(beginX + 9*cellSize, beginY, beginX + 9*cellSize, beginY + 9*cellSize, borderGridPaint);
		canvas.drawLine(beginX, beginY, beginX + 9*cellSize, beginY, borderGridPaint);
		canvas.drawLine(beginX, beginY + 9*cellSize, beginX + 9*cellSize, beginY + 9*cellSize, borderGridPaint);
	}

	public void setBlink(boolean blink) {
		this.blink = blink;
	}

	private void drawWallPreview(Canvas canvas, int[] coordinates, int wallType) {
		float beginX = mX + gridMargin;
		float beginY = mY + headerHeight;

		Paint PreviewWallPaint = new Paint();
		PreviewWallPaint.setColor(wallPreviewColor);
		PreviewWallPaint.setAlpha(150);
		PreviewWallPaint.setStrokeWidth(cellBorderWidth);

		if (wallType == Quoridor.HORIZONTAL) {
			canvas.drawLine(beginX + (coordinates[0] - 1)*cellSize, beginY +(10 - coordinates[1])*cellSize,
					beginX + (coordinates[0] + 1)*cellSize, beginY + (10 - coordinates[1])*cellSize, PreviewWallPaint);
		}

		if (wallType == Quoridor.VERTICAL) {
				canvas.drawLine(beginX + (coordinates[0] - 1)*cellSize, beginY + (9 - (coordinates[1] - 1))*cellSize,
						beginX + (coordinates[0] - 1)*cellSize, beginY + (9 - (coordinates[1] + 1))*cellSize, PreviewWallPaint);
			}


	}

	public void clearWallPreview() {
		horizontalWallPreview = null;
		verticalWallPreview = null;
	}

	public void offsetWallPreview(int wallType, int xOffset, int yOffset) {
		if (wallType == Quoridor.HORIZONTAL) {
			if (horizontalWallPreview != null) {
				int nextXValue = horizontalWallPreview[0] + xOffset;
				int nextYValue = horizontalWallPreview[1] + yOffset;

				if (nextXValue < 1) nextXValue = 1;
				if (nextXValue > 8) nextXValue = 8;
				if (nextYValue < 2) nextYValue = 2;
				if (nextYValue > 9) nextYValue = 9;

				horizontalWallPreview[0] = nextXValue;
				horizontalWallPreview[1] = nextYValue;
			}
		} else if (wallType == Quoridor.VERTICAL) {
			if (verticalWallPreview != null) {
				int nextXValue = verticalWallPreview[0] + xOffset;
				int nextYValue = verticalWallPreview[1] + yOffset;

				if (nextXValue < 2) nextXValue = 2;
				if (nextXValue > 9) nextXValue = 9;
				if (nextYValue < 1) nextYValue = 1;
				if (nextYValue > 8) nextYValue = 8;

				verticalWallPreview[0] = nextXValue;
				verticalWallPreview[1] = nextYValue;
			}
		}
		// Reset blink timer
		wallBlinkTimer = wallBlinkDelay;
		drawWallPreview = true;
	}

	public void setWallPreview(int wallType, int x, int y) {
		if (wallType == Quoridor.HORIZONTAL) {
			verticalWallPreview = null;
			horizontalWallPreview = new int[]{x, y};
		}
		if (wallType == Quoridor.VERTICAL) {
			horizontalWallPreview = null;
			verticalWallPreview = new int[]{x, y};
		}
		wallBlinkTimer = wallBlinkDelay;
		drawWallPreview = true;
	}

	public int[] getCellCorrespondingToTouch(int x, int y) {
		float beginX = mX + gridMargin;
		float beginY = mY + headerHeight;
		float endX = beginX + cellSize*9;
		float endY = beginY + cellSize*9;

		if (x < beginX || x > endX || y < beginY || y > endY) {
			return null;
		} else {
			int[] cellCoordinates = new int[2];
			cellCoordinates[0] = 1 + (x - (int) beginX) / cellSize;
			cellCoordinates[1] = 9 - (y - (int) beginY) / cellSize;
			return cellCoordinates;
		}

	}

	public int getBottom() {
		return (int) (mY + getHeight());
	}
	public int getTop() {
		return (int) (mY);
	}
	public int getLeft() {
		return (int) (mX);
	}
	public int getRight() {
		return (int) (mX + getWidth());
	}

	public int[] getWallPreviewCoordinates() {
		if (horizontalWallPreview != null) {
			return horizontalWallPreview;
		} else if (verticalWallPreview != null) {
			return verticalWallPreview;
		} else {
			return null;
		}
	}

	public void setConsoleMessage(String message) {
		mConsoleMessage = message;
	}

	public void setConsoleMessageColor(int color) {
		consoleMessageColor = color;
	}

	public void setConsoleMessageWithDuration(String message, int duration) {
		mConsoleMessage = message;
		consoleMessageTimer = duration;
		isConsoleMessageTimed = true;
	}

	public interface onClickAction {
		void onClick(GameView gameView, int x, int y);
	}

	public void setOnClickAction(onClickAction action) {
		mOnClickAction = action;
	}

	public void setVisible(boolean isVisible) {
		visible = isVisible;
	}

	public boolean isInRect(int x, int y) {
		// Does not consume the event if button is visible
		return (getLeft() < x && x < getRight() && getTop() < y && y < getBottom() && visible);
	}
}
