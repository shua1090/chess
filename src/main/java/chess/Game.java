/*
 *     The Game of Chess in Java
 *     Copyright (C) 2021 Shynn Lawrence
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 */

package chess;

import java.util.Scanner;

enum status {
    DEFAULT,
    NULLMOVE,
    WRONGCOLOR,
    INVALIDMOVE,
    INTOCHECK,
    CHECK,
    CHECKMATE
}

class logic {
    ChessBoard theBoard;
    WhiteSide whiteSide;
    BlackSide blackSide;
    MoveStack theStack;

    Color currentColor;

    logic() {
        theBoard = new ChessBoard();
        whiteSide = new WhiteSide();
        blackSide = new BlackSide();

        whiteSide.setBoard(theBoard);
        blackSide.setBoard(theBoard);

        theStack = new MoveStack(theBoard);

        currentColor = Color.White;
    }

    boolean isItCheck(Color col) {
        if (col == Color.White) {
            var z = blackSide.getAllValidMoves(theBoard);
            return z.contains(whiteSide.findPiece(new King(null, null), theBoard).get(0).currentPosition);
        } else {
            var z = whiteSide.getAllValidMoves(theBoard);
            return z.contains(blackSide.findPiece(new King(null, null), theBoard).get(0).currentPosition);
        }
    }

    // Input Color is the color who may be checkmated on
    boolean isItCheckMate(Color colo) {

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {

                if (theBoard.get(row, col) != null && theBoard.get(row, col).getColor() == colo) {
                    for (Position p : theBoard.get(row, col).getAvailableMoves(theBoard)) {

                        theStack.add(new Move(theBoard.get(row, col).getCurrentPosition(), p, theBoard.get(row, col), theBoard.get(p)));
                        theBoard.move(theBoard.get(row, col), p.row, p.column);

                        if (!isItCheck(colo)) {
                            theStack.applyUndo();
                            return false;
                        }
                        theStack.applyUndo();
                    }
                }
            }
        }
        return true;
    }

    status move(Position start, Position end) {
        Piece p = theBoard.get(start);

        if (isItCheckMate(currentColor)) return status.CHECKMATE;
        else if (p == null) {
            return status.NULLMOVE;
        } else if (p instanceof OutOfBoundsPiece) {
            return status.INVALIDMOVE;
        } else if (theBoard.get(start).getColor() != this.currentColor) {
            return status.WRONGCOLOR;
        } else {
            if (!theBoard.get(start).getAvailableMoves(theBoard).contains(end)) {
                return status.INVALIDMOVE;
            }

            theStack.add(new Move(start, end, theBoard.get(start), theBoard.get(end)));
            theBoard.move(theBoard.get(start), end.row, end.column);

            if (isItCheck(currentColor)) {
                theStack.applyUndo();
                return status.INTOCHECK;
            } else {
                currentColor = (currentColor == Color.White) ? Color.Black : Color.White;
                return status.DEFAULT;
            }
        }
    }
}

public class Game {

    Game() {
    }

    public static void main(String[] args) {
        Game g = new Game();
        g.listen();
    }

    static Position chessPositionToIndex(char row, char column) {
        int theRow = row - '1';
        int theColumn = column - 'a';

        return new Position(theRow, theColumn);
    }

    static String indexToChessPosition(Position p) {
        if (p == null) return "null";
        char theRow = (char) (p.row + '1');
        char theColumn = (char) (p.column + 'a');

        return String.valueOf(theColumn) + theRow;
    }

    void listen() {
        System.out.println("-".repeat(5) + "CHESS" + "-".repeat(5));

        logic l = new logic();

        Scanner sc = new Scanner(System.in);
        int code = 0;

        l.theBoard.printBoard();

        while (code != 1) {
            System.out.println(l.currentColor + "'s turn.");
            l.theBoard.printBoard();
            System.out.print("> ");

            String input = sc.nextLine();

            switch (input) {
                case "\n":
                    break;
                case "exit":
                    code = 1;
                    break;
                case "print":
                    l.theBoard.printBoard();
                    break;
//                case "available":
//                    if (currentColor == Color.White) {
//                        l.theBoard .printAvailableMoves(whiteSide.getAllValidMoves(theBoard));
//                    } else {
//                        l.theBoard .printAvailableMoves(blackSide.getAllValidMoves(theBoard));
//                    }
//                    break;
                case "undo":
                    try {
                        l.theStack.applyUndo();
                    } catch (GameOutOfMoves e) {
                        System.out.println("Nothing to undo.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "skip":
                    l.currentColor = (l.currentColor == Color.White) ? Color.Black : Color.White;
                    break;
//                case "stat": {
//                    System.out.print("  White  ");
//                    System.out.print("|");
//                    System.out.print("  Black  \n");
//                    System.out.println("---------|---------");
//
//                    final ArrayList<String> whitePieces = theStack.takenPieces(Color.White);
//                    final ArrayList<String> blackPieces = theStack.takenPieces(Color.Black);
//
//                    int vals = Math.min(whitePieces.size(), blackPieces.size());
//
//                    for (int i = 0; i < vals; i++) {
//                        System.out.print(outputHelpers.YELLOW + whitePieces.get(i) + outputHelpers.RESET + " ".repeat(9 - whitePieces.get(i).length()));
//                        System.out.print("|");
//                        System.out.print(" ".repeat(9 - blackPieces.get(i).length()) + outputHelpers.RED + blackPieces.get(i) + outputHelpers.RESET);
//                        System.out.print("\n");
//                    }
//
//                    if (whitePieces.size() > blackPieces.size()) {
//                        for (int i = vals; i < whitePieces.size(); i++) {
//                            System.out.print(outputHelpers.YELLOW + whitePieces.get(i) + outputHelpers.RESET + " ".repeat(9 - whitePieces.get(i).length()));
//                            System.out.print("|\n");
//                        }
//                    }
//
//                    if (blackPieces.size() > whitePieces.size()) {
//                        for (int i = vals; i < blackPieces.size(); i++) {
//                            System.out.print(" ".repeat(9 - blackPieces.get(i).length()) + outputHelpers.RED + blackPieces.get(i) + outputHelpers.RESET);
//                            System.out.print("|\n");
//                        }
//                    }
//
//                    break;
//                }

                default:
                    if (input.length() == 0) {
                        break;
                    }

                    boolean b = input.charAt(0) >= 'a' && input.charAt(0) <= 'h' && (input.charAt(1) >= '1' && input.charAt(1) <= '8');

                    /* if (input.length() == 2 && b) {
                        Position thePosition = chessPositionToIndex(input.charAt(1), input.charAt(0));
                        System.out.println(l.theBoard.get(thePosition));
                    } else */
                    if (input.length() == 8 && b && input.substring(3, 5).contains("to") && (input.charAt(6) >= 'a' && input.charAt(6) <= 'h' && (input.charAt(7) >= '1' && input.charAt(7) <= '8'))) {
//                        System.out.println("Valid input");

                        Position oldPosition = chessPositionToIndex(input.charAt(1), input.charAt(0));
                        Position newPosition = chessPositionToIndex(input.charAt(7), input.charAt(6));

                        System.out.println(oldPosition);
                        System.out.println(newPosition);

                        var f = l.move(oldPosition, newPosition);

                        if (f != status.DEFAULT) System.out.println("Redo.");

                        System.out.println("status: " + f);

                        System.out.println("Check: " + l.isItCheck(l.currentColor)
//                                (l.currentColor == Color.Black) ? Color.White : Color.Black)
                        );


//                        l.currentColor = (l.currentColor == Color.White) ? Color.Black : Color.White;

                        System.out.println("Checkmate: " + l.isItCheckMate(l.currentColor));

//                        if (theBoard.get(oldPosition) == null) {
//                            System.out.println("You can't move something at that position, because there's nothing there!");
//                        } else if (theBoard.get(oldPosition).getColor() != currentColor) {
//                            System.out.println("You can't move a piece that's not your current color.");
//                        } else {
//
//                            if (!theBoard.get(oldPosition).getAvailableMoves(theBoard).contains(newPosition)) {
//                                System.out.println("You can't move that piece there. These are your valid moves with that piece:");
//                                theBoard.printAvailableMoves(theBoard.get(oldPosition).getAvailableMoves(theBoard));
//                                break;
//                            }
//
//                            theStack.add(new Move(oldPosition, newPosition, theBoard.get(oldPosition), theBoard.get(newPosition)));
//
//                            theBoard.move(theBoard.get(oldPosition), newPosition.row, newPosition.column);
//                            currentColor = (currentColor == Color.White) ? Color.Black : Color.White;
//                            theBoard.printBoard();
//
//                            System.out.println("Check: " + isItCheck(
//                                    (currentColor == Color.Black) ? Color.White : Color.Black)
//                            );
//
//                        }
//
//                    } else {
//                        System.out.println("Invalid input. Try Again!");
                    }
            }
        }
    }
}
