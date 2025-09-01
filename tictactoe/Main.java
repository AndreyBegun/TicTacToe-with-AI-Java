import java.util.*;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final char[][] table = new char[3][3];
    private static String[] parameters;
    private static char turn;

    public static void main(String[] args) {
        while (parameters == null) {
            getParameters();
        }
        setTable();
        displayTable();
        gameLoop();
    }

    private static void getParameters() {
        System.out.print("Input command: ");
        String[] input = scanner.nextLine().split("\\s+");

        try {
            if (Objects.equals(input[0], "exit")) {
                System.exit(0);
            } else if (Objects.equals(input[0], "start") && isValidPlayer(input[1]) && isValidPlayer(input[2])) {
                parameters = input;
            } else {
                System.out.println("Bad parameters!");
            }
        } catch (Exception e) {
            System.out.println("Bad parameters!");
        }
    }

    private static boolean isValidPlayer(String player) {
        return Objects.equals(player, "easy") ||
                Objects.equals(player, "user") ||
                Objects.equals(player, "medium") ||
                Objects.equals(player, "hard");
    }

    private static void setTable() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                table[i][j] = ' ';
            }
        }
    }

    private static void displayTable() {
        System.out.println("---------");
        for (char[] i : table) {
            System.out.print("| ");
            for (char j : i) {
                System.out.print(j + " ");
            }
            System.out.println("|");
        }
        System.out.println("---------");
    }

    private static void gameLoop() {
        while (true) {
            turn = getTurnMark();

            if (turn == 'X') makeMove(parameters[1]);
            if (turn == 'O') makeMove(parameters[2]);

            displayTable();

            if (win()) {
                System.out.println(turn + " wins");
                return;
            }

            if (draw()) {
                System.out.println("Draw");
                return;
            }
        }
    }

    private static void makeMove(String parameter) {
        switch (parameter) {
            case "user": playerMove(); break;
            case "easy": botMoveEasy(); break;
            case "medium": botMoveMedium(); break;
            case "hard": botMoveHard(); break;
        }
    }

    private static char getTurnMark() {
        int x = 0;
        int o = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (Main.table[i][j] == 'X') x++;
                if (Main.table[i][j] == 'O') o++;
            }
        }

        return (x > o) ? 'O' : 'X';
    }

    private static void playerMove() {
        while (true) {
            System.out.print("Enter the coordinates: ");
            String[] coordinates = scanner.nextLine().split("\\s+");
            try {
                int x = Integer.parseInt(coordinates[0]) - 1;
                int y = Integer.parseInt(coordinates[1]) - 1;

                if (x < 0 || x > 2 || y < 0 || y > 2) {
                    System.out.println("Coordinates should be from 1 to 3!");
                } else if (table[x][y] != ' ') {
                    System.out.println("This cell is occupied! Choose another one!");
                } else {
                    table[x][y] = turn;
                    break;
                }
            } catch (Exception e) {
                System.out.println("You should enter numbers!");
            }
        }
    }

    private static void botMoveEasy() {
        System.out.println("Making move level \"easy\"");
        Random random = new Random();
        while (true) {
            int x = random.nextInt(3);
            int y = random.nextInt(3);
            if (table[x][y] == ' ') {
                table[x][y] = turn;
                break;
            }
        }
    }

    private static void botMoveMedium() {
        System.out.println("Making move level \"medium\"");

        int[] move = findWinningOrBlockingMove(turn);
        if (move == null) {
            char opponent = (turn == 'X') ? 'O' : 'X';
            move = findWinningOrBlockingMove(opponent);
        }

        if (move == null) {
            Random random = new Random();
            do {
                move = new int[]{random.nextInt(3), random.nextInt(3)};
            } while (table[move[0]][move[1]] != ' ');
        }

        table[move[0]][move[1]] = turn;
    }

    private static int[] findWinningOrBlockingMove(char symbol) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (table[i][j] == ' ') {
                    table[i][j] = symbol;
                    boolean win = isWinning(symbol);
                    table[i][j] = ' ';
                    if (win) return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private static boolean isWinning(char player) {
        for (int i = 0; i < 3; i++) {
            if (table[i][0] == player && table[i][1] == player && table[i][2] == player) return true;
            if (table[0][i] == player && table[1][i] == player && table[2][i] == player) return true;
        }
        return (table[0][0] == player && table[1][1] == player && table[2][2] == player) ||
                (table[0][2] == player && table[1][1] == player && table[2][0] == player);
    }

    private static boolean win() {
        return isWinning(turn);
    }

    private static boolean draw() {
        for (char[] row : table) {
            for (char mark : row) {
                if (mark == ' ') return false;
            }
        }
        return true;
    }

    // ---------- HARD AI (Minimax) ----------
    private static void botMoveHard() {
        System.out.println("Making move level \"hard\"");
        int[] bestMove = minimax(table, turn);
        table[bestMove[0]][bestMove[1]] = turn;
    }

    private static int[] minimax(char[][] board, char currentPlayer) {
        char opponent = (currentPlayer == 'X') ? 'O' : 'X';

        int bestScore = (currentPlayer == turn) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int[] bestMove = null;

        if (isWinning(opponent)) return new int[]{-1, evaluateBoard()};
        if (draw()) return new int[]{-1, 0};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == ' ') {
                    board[i][j] = currentPlayer;

                    int[] result = minimax(board, opponent);
                    int score = result[1];

                    board[i][j] = ' ';

                    if (currentPlayer == turn) {
                        if (score > bestScore) {
                            bestScore = score;
                            bestMove = new int[]{i, j};
                        }
                    } else {
                        if (score < bestScore) {
                            bestScore = score;
                            bestMove = new int[]{i, j};
                        }
                    }
                }
            }
        }
        return (bestMove == null) ? new int[]{-1, 0} : new int[]{bestMove[0], bestMove[1], bestScore};
    }

    private static int evaluateBoard() {
        if (isWinning(turn)) return 10;
        if (isWinning(turn == 'X' ? 'O' : 'X')) return -10;
        return 0;
    }
}
