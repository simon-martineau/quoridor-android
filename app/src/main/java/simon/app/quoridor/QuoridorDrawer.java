package simon.app.quoridor;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class QuoridorDrawer {

	//// Logic
	public List<int[]> hoverPositions = new ArrayList<>();

	//// Colors
	public int hoverColor = 0x6A347836;
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

	public QuoridorDrawer(int width) {
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

		// Players
		drawPlayer(canvas, 1, quoridorGame.mPlayerOnePosition[0], quoridorGame.mPlayerOnePosition[1]);
		drawPlayer(canvas, 2, quoridorGame.mPlayerTwoPosition[0], quoridorGame.mPlayerTwoPosition[1]);

		// Hover cells
		for (int[] coordinates : hoverPositions) {
			drawHover(canvas, coordinates[0], coordinates[1]);
		}

		// Grid
		drawGrid(canvas);

		// Walls
		drawWalls(canvas, quoridorGame.mHorizontalWalls, Quoridor.HORIZONTAL);
		drawWalls(canvas, quoridorGame.mVerticalWalls, Quoridor.VERTICAL);

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
				canvas.drawLine(beginX + (coordinates[0] - 1)*cellSize, beginY +(coordinates[1] - 1)*cellSize,
						beginX + (coordinates[0] + 1)*cellSize, beginY + (coordinates[1] - 1)*cellSize, wallPaint);
			}
		}

		if (wallType == Quoridor.VERTICAL) {
			for (int[] coordinates : walls) {
				canvas.drawLine(beginX + (coordinates[0] - 1)*cellSize, beginY + (coordinates[1] - 1)*cellSize,
						beginX + (coordinates[0] - 1)*cellSize, beginY + (coordinates[1] + 1)*cellSize, wallPaint);
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
}
