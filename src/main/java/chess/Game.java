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

import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    ChessBoard theBoard;
    WhiteSide whiteSide;
    BlackSide blackSide;
    MoveStack theStack;

    Game() {
        theBoard = new ChessBoard();
        whiteSide = new WhiteSide();
        blackSide = new BlackSide();

        whiteSide.setBoard(theBoard);
        blackSide.setBoard(theBoard);

        theStack = new MoveStack(theBoard);

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
        char theRow = (char) (p.row + '1');
        char theColumn = (char) (p.column + 'a');

        return String.valueOf(theColumn) + theRow;
    }

    boolean isItCheck(Color oppositeTeamColor) {
        if (oppositeTeamColor == Color.Black) {
            var z = blackSide.getAllValidMoves(theBoard);
            return z.contains(whiteSide.findPiece(new King(null, null), theBoard).get(0).currentPosition);
        } else if (oppositeTeamColor == Color.White) {
            var z = whiteSide.getAllValidMoves(theBoard);
            return z.contains(blackSide.findPiece(new King(null, null), theBoard).get(0).currentPosition);
        }
        throw new IllegalArgumentException();
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
                case "\n":
                    break;
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
                case "undo":
                    try {
                        theStack.applyUndo();
                    } catch (GameOutOfMoves e) {
                        System.out.println("Nothing to undo.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "stat":
                    System.out.print("  White  ");
                    System.out.print("|");
                    System.out.print("  Black  \n");
                    System.out.println("---------|---------");

                    final ArrayList<String> whitePieces = theStack.takenPieces(Color.White);
                    final ArrayList<String> blackPieces = theStack.takenPieces(Color.Black);

                    int vals = Math.min(whitePieces.size(), blackPieces.size());

                    for (int i = 0; i < vals; i++) {
                        System.out.print(whitePieces.get(i) + " ".repeat(9 - whitePieces.get(i).length()));
                        System.out.print("|");
                        System.out.print(" ".repeat(9 - blackPieces.get(i).length()) + blackPieces.get(i));
                        System.out.print("\n");
                    }

                    if (whitePieces.size() > blackPieces.size()) {
                        for (int i = vals; i < whitePieces.size(); i++) {
                            System.out.print(whitePieces.get(i) + " ".repeat(9 - whitePieces.get(i).length()));
                            System.out.print("|\n");
                        }
                    }

                    if (blackPieces.size() > whitePieces.size()) {
                        for (int i = vals; i < blackPieces.size(); i++) {
                            System.out.print(" ".repeat(9 - blackPieces.get(i).length()) + blackPieces.get(i));
                            System.out.print("|\n");
                        }
                    }


                default:
                    if (input.length() == 0) {
                        break;
                    }

                    boolean b = input.charAt(0) >= 'a' && input.charAt(0) <= 'h' && (input.charAt(1) >= '1' && input.charAt(1) <= '8');

                    if (input.length() == 2 && b) {
                        Position thePosition = chessPositionToIndex(input.charAt(1), input.charAt(0));
                        System.out.println(theBoard.get(thePosition));
                    } else if (input.length() == 8 && b && input.substring(3, 5).contains("to") && (input.charAt(6) >= 'a' && input.charAt(6) <= 'h' && (input.charAt(7) >= '1' && input.charAt(7) <= '8'))) {
//                        System.out.println("Valid input");

                        Position oldPosition = chessPositionToIndex(input.charAt(1), input.charAt(0));
                        Position newPosition = chessPositionToIndex(input.charAt(7), input.charAt(6));

                        if (theBoard.get(oldPosition) == null) {
                            System.out.println("You can't move something at that position, because there's nothing there!");
                        } else if (theBoard.get(oldPosition).getColor() != currentColor) {
                            System.out.println("You can't move a piece that's not your current color.");
                        } else {

                            if (!theBoard.get(oldPosition).getAvailableMoves(theBoard).contains(newPosition)) {
                                System.out.println("You can't move that piece there. These are your valid moves with that piece:");
                                theBoard.printAvailableMoves(theBoard.get(oldPosition).getAvailableMoves(theBoard));
                                break;
                            }

                            theStack.add(new Move(oldPosition, newPosition, theBoard.get(oldPosition), theBoard.get(newPosition)));

                            theBoard.move(theBoard.get(oldPosition), newPosition.row, newPosition.column);
                            currentColor = (currentColor == Color.White) ? Color.Black : Color.White;
                            theBoard.printBoard();

                            System.out.println("Check: " + isItCheck(
                                    (currentColor == Color.Black) ? Color.White : Color.Black)
                            );

                        }

                    } else {
                        System.out.println("Invalid input. Try Again!");
                    }
            }
        }
    }
}
