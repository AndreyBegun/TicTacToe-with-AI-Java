package tictactoe;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Game {
    private final int boardSize;
    private final char[][] board;
    private char move = 'X';
    private int moveCount = 0;

    private enum GameState {
        RUNNING,
        DRAW,
        X_WIN,
        ZERO_WIN
    }

    private static class Cell {
        private final int row;
        private final int column;

        public Cell(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }
    }

    public Game(int boardSize) {
        this.boardSize = boardSize;
        this.board = new char[boardSize][boardSize];
    }

    public void start() {
        parseBoard();
        printBoard();
        Cell cell = parseCell();
        makeMove(cell);
        printBoard();
        GameState gameState = detectGameState(cell);
        printGameState(gameState);
    }

    private void parseBoard() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the cells:");
        String initialState = scanner.nextLine();

        int zeroCount = 0, xCount = 0;

        for (int row = 0; row < boardSize; ++row) {
            for (int column = 0; column < boardSize; ++column) {
                char symbol = initialState.charAt(row * boardSize + column);
                board[row][column] = symbol == '_' ? ' ' : symbol;

                if (symbol == 'X') ++xCount;
                else if (symbol == 'O') ++zeroCount;
            }
        }

        move = xCount > zeroCount ? 'O' : 'X';
        moveCount = xCount + zeroCount;
    }

    private void printBoard() {
        System.out.println("-".repeat(boardSize * boardSize));
        for (int row = 0; row < boardSize; ++row) {
            System.out.print("| ");
            for (int column = 0; column < boardSize; ++column) {
                System.out.print(board[row][column] + " ");
            }
            System.out.println("|");
        }
        System.out.println("-".repeat(boardSize * boardSize));
    }

    private Cell parseCell() {
        Scanner scanner = new Scanner(System.in);
        int cellRow, cellColumn;

        while (true) {
            try {
                System.out.println("Enter the coordinates:");
                cellRow = scanner.nextInt();

                if (cellRow < 1 || cellRow > boardSize) {
                    System.out.println("Coordinates should be from 1 to 3!");
                    scanner.nextLine();
                    continue;
                }

                cellColumn = scanner.nextInt();

                if (cellColumn < 1 || cellColumn > boardSize) {
                    System.out.println("Coordinates should be from 1 to 3!");
                    scanner.nextLine();
                    continue;
                }

                if (board[cellRow - 1][cellColumn - 1] != ' ') {
                    System.out.println("This cell is occupied! Choose another one!");
                    continue;
                }

                return new Cell(cellRow - 1, cellColumn - 1);
            } catch (InputMismatchException exception) {
                System.out.println("You should enter numbers!");
                scanner.nextLine();
            }
        }
    }

    private void makeMove(Cell cell) {
        board[cell.getRow()][cell.getColumn()] = move;
        ++moveCount;
    }

    private GameState detectGameState(Cell cell) {
        for (int row = 0; true; ++row) {
            if (board[row][cell.getColumn()] != move) break;
            if (row == boardSize - 1) {
                return move == 'X'
                        ? GameState.X_WIN
                        : GameState.ZERO_WIN;
            }
        }

        for (int column = 0; true; ++column) {
            if (board[cell.getRow()][column] != move) break;
            if (column == boardSize - 1) {
                return move == 'X'
                        ? GameState.X_WIN
                        : GameState.ZERO_WIN;
            }
        }

        if (cell.getRow() == cell.getColumn()) {
            for (int index = 0; true; ++index) {
                if (board[index][index] != move) break;
                if (index == boardSize - 1) {
                    return move == 'X'
                            ? GameState.X_WIN
                            : GameState.ZERO_WIN;
                }
            }
        }

        if (cell.getRow() == boardSize - cell.getColumn() - 1) {
            for (int index = 0; true; ++index) {
                if (board[index][boardSize - index - 1] != move) break;
                if (index == boardSize - 1) {
                    return move == 'X'
                            ? GameState.X_WIN
                            : GameState.ZERO_WIN;
                }
            }
        }

        if (moveCount == boardSize * boardSize) return GameState.DRAW;

        return GameState.RUNNING;
    }

    private void printGameState(GameState gameState) {
        switch (gameState) {
            case DRAW: {
                System.out.println("Draw");
                break;
            }
            case X_WIN: {
                System.out.println("X wins");
                break;
            }
            case ZERO_WIN: {
                System.out.println("O wins");
                break;
            }
            default: {
                System.out.println("Game not finished");
            }
        }
    }
}