package simon.app.quoridor.CustomViews;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

import simon.app.quoridor.WindowViews.GameView;
import simon.app.quoridor.Core.Quoridor;

public class GQuoridorView extends GView {
	// TODO: Change colors into constants

	// Linked quoridorGame
	private Quoridor mQuoridor;

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

	// Border blink
	private boolean isBlinkingConsoleMessage = false;
	private boolean drawCustomConsoleMessage = false;
	private int mConsoleMessageTextSize = 0;
	private int mConsoleMessageBlinkTimer;
	private int mConsoleMessageBlinkDelay;
	private int mConsoleMessageBlinkColor;
	private int mConsoleMessageBlinksLeft = 0;
	private String mConsoleMessageBlinkMessage;

	// Border blink
	private boolean isBorderBlink = false;
	private boolean drawBorderBlink = false;
	private int mBorderBlinkTimer;
	private int mBorderBlinkDelay;
	private int mBorderBlinkColor;
	private int mBorderBlinksLeft = 0;

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

	// Margins
	public int gridMargin = 64;
	public int headerHeight = 250;
	public int cellSize;
	public int cellBorderWidth = 12;
	public int wrapperWidth = 5;



	// TODO: Implement flexibility in constructor
	public GQuoridorView(GameView gameView, Quoridor quoridor, int x, int y, int width) {
		super(gameView, x, y);
		mQuoridor = quoridor;
		cellSize = (int) ((width - (gridMargin*2 + wrapperWidth*2 + cellBorderWidth*10)) / 9.0);
	}

	@Override
	public int getWidth() {
		return cellSize * 9 + gridMargin * 2 + wrapperWidth * 2;
	}

	@Override
	public int getHeight() {
		return cellSize * 9 + gridMargin * 2 + wrapperWidth * 2 + headerHeight;
	}

	@Override
	public void draw(Canvas canvas) {
		// Background
		Paint backgroundPaint = new Paint();
		backgroundPaint.setColor(backgroundColor);
		canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), backgroundPaint);

		// Wrapper
		Paint wrapperPaint = new Paint();
		wrapperPaint.setColor(wrapperColor);
		if (isBorderBlink) {
			if (drawBorderBlink) {
				wrapperPaint.setColor(mBorderBlinkColor);
				mBorderBlinkTimer--;
				if (mBorderBlinkTimer <= 0) {
					drawBorderBlink = false;
					mBorderBlinkTimer = mBorderBlinkDelay;
					mBorderBlinksLeft--;
				}
			} else {
				mBorderBlinkTimer--;
				if (mBorderBlinkTimer <= 0) {
					drawBorderBlink = true;
					mBorderBlinkTimer = mBorderBlinkDelay;
				}
			}

			if (mBorderBlinksLeft <= 0) isBorderBlink = false;
		}
		wrapperPaint.setStyle(Paint.Style.STROKE);
		wrapperPaint.setStrokeWidth(wrapperWidth);
		canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), wrapperPaint);

		// Header
		Paint playerOneInfoPaint = new Paint();
		playerOneInfoPaint.setColor(playerOneColor);
		playerOneInfoPaint.setTextSize(48);
		playerOneInfoPaint.setTextAlign(Paint.Align.LEFT);

		Paint playerTwoInfoPaint = new Paint();
		playerTwoInfoPaint.setColor(playerTwoColor);
		playerTwoInfoPaint.setTextSize(48);
		playerTwoInfoPaint.setTextAlign(Paint.Align.RIGHT);

		canvas.drawText("Player 1: " + mQuoridor.mPlayerOneName, getLeft() + 24, getTop() + 64, playerOneInfoPaint);
		canvas.drawText("Player 2: " + mQuoridor.mPlayerTwoName, getRight() - 24, getTop() + 64, playerTwoInfoPaint);
		canvas.drawText("Walls left: " + mQuoridor.mPlayerOneWallsLeft, getLeft() + 24, getTop() + 128, playerOneInfoPaint);
		canvas.drawText("Walls left: " + mQuoridor.mPlayerTwoWallsLeft, getRight() - 24, getTop() + 128, playerTwoInfoPaint);

		Paint consolePaint = new Paint();
		consolePaint.setColor(consoleMessageColor);
		consolePaint.setTextSize(84);
		consolePaint.setTextAlign(Paint.Align.CENTER);

		if (isBlinkingConsoleMessage) {
			if (drawCustomConsoleMessage) {
				consolePaint.setColor(mConsoleMessageBlinkColor);
				consolePaint.setTextSize(mConsoleMessageTextSize);
				canvas.drawText(mConsoleMessageBlinkMessage, getRight() / 2.0f, getTop() + 192, consolePaint);
				mConsoleMessageBlinkTimer--;
				if (mConsoleMessageBlinkTimer <= 0) {
					drawCustomConsoleMessage = false;
					mConsoleMessageBlinkTimer = mConsoleMessageBlinkDelay;
					mConsoleMessageBlinksLeft--;
				}
			} else {
				mConsoleMessageBlinkTimer--;
				if (mConsoleMessageBlinkTimer <= 0) {
					drawCustomConsoleMessage = true;
					mConsoleMessageBlinkTimer = mConsoleMessageBlinkDelay;
				}
			}

			if (mConsoleMessageBlinksLeft <= 0) {
				isBlinkingConsoleMessage = false;
			}
		} else {
			canvas.drawText(mConsoleMessage, getRight() / 2.0f, getTop() + 192, consolePaint);
		}

		// Players
		drawPlayer(canvas, 1, mQuoridor.mPlayerOnePosition[0], mQuoridor.mPlayerOnePosition[1]);
		drawPlayer(canvas, 2, mQuoridor.mPlayerTwoPosition[0], mQuoridor.mPlayerTwoPosition[1]);

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
		drawWalls(canvas, mQuoridor.mHorizontalWalls, Quoridor.HORIZONTAL);
		drawWalls(canvas, mQuoridor.mVerticalWalls, Quoridor.VERTICAL);

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

	public void setBorderBlink(int color, int blinkDelay, int repetitions) {
		isBorderBlink = true;
		drawBorderBlink = true;
		mBorderBlinkColor = color;
		mBorderBlinkDelay = blinkDelay;
		mBorderBlinkTimer = blinkDelay;
		mBorderBlinksLeft = repetitions;

	}

	public void setCustomMessageBlink(String message, int color, int textSize, int blinkDelay, int repetitions) {
		isBlinkingConsoleMessage = true;
		drawCustomConsoleMessage = true;
		mConsoleMessageTextSize = textSize;
		mConsoleMessageBlinkTimer = blinkDelay;
		mConsoleMessageBlinkDelay = blinkDelay;
		mConsoleMessageBlinkColor = color;
		mConsoleMessageBlinksLeft = repetitions;
		mConsoleMessageBlinkMessage = message;
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
		float left = getLeft() + gridMargin + (x - 1)*cellSize;
		float top = getTop() + headerHeight + (9 - y)*cellSize;

		canvas.drawRect(left, top, left + cellSize, top + cellSize, hoverPaint);
	}


	private void drawWalls(Canvas canvas, List<int[]> walls, int wallType) {
		float beginX = getLeft() + gridMargin;
		float beginY = getTop() + headerHeight;

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

		float beginX = getLeft() + gridMargin;
		float beginY = getTop() + headerHeight;
		float circleX = beginX + (posX - 1)*cellSize + cellSize / 2.0f;
		float circleY = beginY + (9 - posY)*cellSize + cellSize / 2.0f;
		canvas.drawCircle(circleX, circleY, cellSize/4.0f, playerPaint);


	}

	private void drawGrid(Canvas canvas) {
		Paint gridPaint = new Paint();
		gridPaint.setColor(gridColor);
		gridPaint.setStrokeWidth(cellBorderWidth);
		gridPaint.setTextSize(48);


		float beginX = getLeft() + gridMargin;
		float beginY = getTop() + headerHeight;

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
		float beginX = getLeft() + gridMargin;
		float beginY = getTop() + headerHeight;

		Paint PreviewWallPaint = new Paint();
		PreviewWallPaint.setColor(wallPreviewColor);
		// PreviewWallPaint.setAlpha(150);
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




		if (isWallPreviewInvalid()) wallPreviewColor = Color.RED;
		else wallPreviewColor = Color.GREEN;


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
		if (isWallPreviewInvalid()) wallPreviewColor = Color.RED;
		else wallPreviewColor = Color.GREEN;
		wallBlinkTimer = wallBlinkDelay;
		drawWallPreview = true;
	}

	public boolean isWallPreviewInvalid() {
		// Temporary game state to perform check
		Quoridor tempQuoridorGame = new Quoridor(mQuoridor); // Copy constructor
		if (horizontalWallPreview != null) {
			if (Quoridor.positionIncluded(horizontalWallPreview, tempQuoridorGame.getInvalidWallCoordinates(Quoridor.HORIZONTAL))) return true;
			tempQuoridorGame.placeWall(1, Quoridor.HORIZONTAL, horizontalWallPreview[0], horizontalWallPreview[1]);
		} else if (verticalWallPreview != null) {
			if (Quoridor.positionIncluded(verticalWallPreview, tempQuoridorGame.getInvalidWallCoordinates(Quoridor.VERTICAL))) return true;
			tempQuoridorGame.placeWall(1, Quoridor.VERTICAL, verticalWallPreview[0], verticalWallPreview[1]);
		} else {
			return true;
		}

		if (tempQuoridorGame.getShortestPathToVictory(1) == null) return true;
		return tempQuoridorGame.getShortestPathToVictory(2) == null;
	}

	public int[] getCellCorrespondingToTouch(int x, int y) {
		float beginX = getLeft() + gridMargin;
		float beginY = getTop() + headerHeight;
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

	public void setPlayerColor(int playerNumber, int color) {
		if (playerNumber == 1) {
			playerOneColor = color;
		} else if (playerNumber == 2) {
			playerTwoColor = color;
		}
	}

	public void linkQuoridorGame(Quoridor quoridor) {
		mQuoridor = quoridor;
	}

}


