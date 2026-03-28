package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int EMPTY = 0;
    private static final int PLAYER = 1; // X
    private static final int COMPUTER = 2; // O


    private int[] board = new int[9];
    private Button[][] buttons = new Button[3][3];
    private TextView tvStatus;
    private boolean gameOver = false;
    private Random random = new Random();
    private boolean copmuterHasMoved = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        Button btnReset = findViewById(R.id.btnReset);

        // Создаем таблицу 3x3
        for (int row = 0; row < 3; row++) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f));

            for (int col = 0; col < 3; col++) {
                Button button = new Button(this);
                button.setText("");
                button.setTextSize(24);
                final int index = row * 3 + col;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onCellClick(index);
                    }
                });

                TableRow.LayoutParams params = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f);
                button.setLayoutParams(params);

                tableRow.addView(button);
                buttons[row][col] = button;
            }
            tableLayout.addView(tableRow);
        }

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        resetGame();
    }

    private void onCellClick(int index) {
        if (gameOver) {
            Toast.makeText(this, "Игра окончена. Начните новую.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (board[index] != EMPTY) {
            Toast.makeText(this, "Клетка занята", Toast.LENGTH_SHORT).show();
            return;
        }

        makeMove(index, PLAYER);
        if (checkWin(PLAYER)) {
            gameOver = true;
            tvStatus.setText("Вы победили!");
            return;
        }
        if (isBoardFull()) {
            gameOver = true;
            tvStatus.setText("Ничья!");
            return;
        }

        tvStatus.setText("Ход компьютера...");
        int computerMove = getBestMove();
        makeMove(computerMove, COMPUTER);
        copmuterHasMoved = true;
        if (checkWin(COMPUTER)) {
            gameOver = true;
            tvStatus.setText("Компьютер победил!");
            return;
        }
        if (isBoardFull()) {
            gameOver = true;
            tvStatus.setText("Ничья!");
            return;
        }

        tvStatus.setText("Ваш ход (X)");
    }

    private void makeMove(int index, int player) {
        board[index] = player;
        String symbol = (player == PLAYER) ? "X" : "O";
        int row = index / 3;
        int col = index % 3;
        buttons[row][col].setText(symbol);
        buttons[row][col].setEnabled(false);
    }

    private boolean checkWin(int player) {
        int[][] winPatterns = {
                {0,1,2}, {3,4,5}, {6,7,8},
                {0,3,6}, {1,4,7}, {2,5,8},
                {0,4,8}, {2,4,6}
        };
        for (int[] pattern : winPatterns) {
            if (board[pattern[0]] == player &&
                    board[pattern[1]] == player &&
                    board[pattern[2]] == player) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int cell : board) {
            if (cell == EMPTY) return false;
        }
        return true;
    }



    private int getRandomEmptyCell() {
        // Собираем список свободных клеток
        java.util.ArrayList<Integer> emptyCells = new java.util.ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (board[i] == EMPTY) {
                emptyCells.add(i);
            }
        }
        if (emptyCells.isEmpty()) return -1;
        int randomIndex = random.nextInt(emptyCells.size());
        return emptyCells.get(randomIndex);
    }

    private int getBestMove() {

        if (!copmuterHasMoved) {
            return getRandomEmptyCell();
        }

        int bestScore = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 9; i++) {
            if (board[i] == EMPTY) {
                board[i] = COMPUTER;
                int score = minimax(board, 0, false);
                board[i] = EMPTY;
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }
        return bestMove;
    }

    private int minimax(int[] boardState, int depth, boolean isMaximizing) {
        if (checkWin(COMPUTER)) return 10 - depth;
        if (checkWin(PLAYER)) return depth - 10;
        if (isBoardFull()) return 0;

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (boardState[i] == EMPTY) {
                    boardState[i] = COMPUTER;
                    int score = minimax(boardState, depth + 1, false);
                    boardState[i] = EMPTY;
                    bestScore = Math.max(score, bestScore);
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (boardState[i] == EMPTY) {
                    boardState[i] = PLAYER;
                    int score = minimax(boardState, depth + 1, true);
                    boardState[i] = EMPTY;
                    bestScore = Math.min(score, bestScore);
                }
            }
            return bestScore;
        }
    }

    private void resetGame() {
        for (int i = 0; i < 9; i++) {
            board[i] = EMPTY;
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText("");
                buttons[row][col].setEnabled(true);
            }
        }
        gameOver = false;
        copmuterHasMoved = false;
        tvStatus.setText("Ваш ход (X)");
    }
}