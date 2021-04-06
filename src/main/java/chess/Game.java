package chess;

import java.util.Scanner;

public class Game {

    ChessBoard theBoard;
    WhiteSide whiteSide;
    BlackSide blackSide;

    Game() {
        theBoard = new ChessBoard();
        whiteSide = new WhiteSide();
        blackSide = new BlackSide();

        whiteSide.setBoard(theBoard);
        blackSide.setBoard(theBoard);
    }

    public static void main(String[] args) {
        Game g = new Game();
        g.listen();
    }

    Position chessPositionToIndex(char row, char column) {
        int theRow = row - '1';
        int theColumn = column - 'a';

        return new Position(theRow, theColumn);

    }

    void listen() {
        System.out.println("-".repeat(5) + "CHESS" + "-".repeat(5));

        Scanner sc = new Scanner(System.in);
        Color currentColor = Color.White;
        int code = 0;

        theBoard.printBoard();

        while (code != 1) {
            System.out.println(currentColor + "'s turn.");
            System.out.print("> ");

            String input = sc.nextLine();

            switch (input) {
                case "exit":
                    code = 1;
                    break;
                case "print":
                    theBoard.printBoard();
                    break;
                case "skip":
                    currentColor = (currentColor == Color.White) ? Color.Black : Color.White;
                    break;
                case "available":
                    if (currentColor == Color.White) {
                        theBoard.printAvailableMoves(whiteSide.getAllValidMoves(theBoard));
                    } else {
                        theBoard.printAvailableMoves(blackSide.getAllValidMoves(theBoard));
                    }
                    break;
                default:
                    if ((input.charAt(0) >= 'a' && input.charAt(0) <= 'h' && (input.charAt(1) >= '1' && input.charAt(1) <= '8')) && input.substring(3, 5).contains("to") && (input.charAt(6) >= 'a' && input.charAt(6) <= 'h' && (input.charAt(7) >= '1' && input.charAt(7) <= '8'))) {
                        System.out.println("Valid input");

                        Position oldPosition = this.chessPositionToIndex(input.charAt(1), input.charAt(0));
                        Position newPosition = this.chessPositionToIndex(input.charAt(7), input.charAt(6));

                        if (theBoard.get(oldPosition) == null) {
                            System.out.println("You can't move something at that position, because there's nothing there!");
                        } else if (theBoard.get(oldPosition).getColor() != currentColor) {
                            System.out.println("You can't move a piece that's not your current color.");
                        } else {
                            theBoard.move(theBoard.get(oldPosition), newPosition.row, newPosition.column);
                            currentColor = (currentColor == Color.White) ? Color.Black : Color.White;
                            theBoard.printBoard();
                        }

                    } else {
                        System.out.println("Invalid input. Try Again!");
                    }
            }
        }
    }
}
