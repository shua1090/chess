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

import java.util.HashSet;

import static chess.outputHelpers.RESET;

class outputHelpers {

    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BOLD = "\u001B[1m";

    public static final String BACKGROUND_BLACK = "\u001B[40m";
    public static final String BACKGROUND_RED = "\u001B[41m";
    public static final String BACKGROUND_GREEN = "\u001B[42m";
    public static final String BACKGROUND_MAGENTA = "\u001B[45m";
    public static final String BACKGROUND_YELLOW = "\u001B[43m";
    public static final String BACKGROUND_BLUE = "\u001B[44m";
    public static final String BACKGROUND_CYAN = "\u001B[46m";
    public static final String BACKGROUND_WHITE = "\u001B[47m";

    public static final String RESET = "\033[0m";
}

public class ChessBoard {
    Piece[][] theBoard = new Piece[8][8];

    ChessBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                theBoard[row][col] = null;
            }
        }
    }

    private static void checkeredPattern(int row, int col) {
        if ((row * 9 + col) % 2 == 0) System.out.print(outputHelpers.BACKGROUND_BLACK);
        else System.out.print(outputHelpers.BACKGROUND_WHITE);
    }

    public static void main(String[] args) {
        ChessBoard b = new ChessBoard();

        WhiteSide ws = new WhiteSide();
        BlackSide bs = new BlackSide();

        ws.setBoard(b);
        bs.setBoard(b);

        ws.getAllValidMoves(b);
        bs.getAllValidMoves(b);

        b.move(b.get(6, 3), 5, 3);

        b.printAvailableMoves(ws.getAllValidMoves(b));

        b.printBoard();
    }

    private Piece checkPieceValidity(int row, int column) {
        if (row > 7 || column > 7 || row < 0 || column < 0) return new OutOfBoundsPiece(null, null);
        return null;
    }

    private Piece checkPieceValidity(Position pos) {
        return checkPieceValidity(pos.row, pos.column);
    }

    public Piece get(int row, int column) {
        if (checkPieceValidity(row, column) instanceof OutOfBoundsPiece) return new OutOfBoundsPiece(null, null);
        else return theBoard[row][column];
    }

    public Piece get(Position p) {
        return this.get(p.getRow(), p.getColumn());
    }

    public void forceSet(Piece replacement, Position thePosition) {
//        System.out.println("Forceset: " + thePosition + " " + replacement);
        theBoard[thePosition.row][thePosition.column] = replacement;
        if (replacement != null)
            replacement.setPosition(thePosition.row, thePosition.column);
    }

    public Piece move(Piece replacement, int newRow, int newColumn) {
        if (checkPieceValidity(newRow, newColumn) instanceof OutOfBoundsPiece)
            return new OutOfBoundsPiece(null, null);
        else {
            theBoard[replacement.getCurrentPosition().row][replacement.getCurrentPosition().column] = null;
            Piece currentPiece = theBoard[newRow][newColumn];
            theBoard[newRow][newColumn] = replacement;
            replacement.setPosition(newRow, newColumn);
            return currentPiece;
        }

    }

    public void printBoard() {
        printTitle();

        for (int row = 8; row >= 1; row--) {
            System.out.print(row + " |");
            for (int col = 0; col < 8; col++) {
                Piece pieceToBePrinted = this.get(row - 1, col);

                checkeredPattern(row - 1, col);

                if (pieceToBePrinted == null) {
                    System.out.print("   ");
//                    if ((row * 9 + col) % 2 == 0) System.out.print("\u2009");
                } else {
//                    System.out.println("Printing something");
                    if (pieceToBePrinted.getColor() == Color.Black) {
                        System.out.print(" ");
                        System.out.print(pieceToBePrinted.pieceIdentification);
                        checkeredPattern(row - 1, col);
                        System.out.print(" ");
                    } else {
                        System.out.print(" ");
                        System.out.print(pieceToBePrinted.pieceIdentification);
                        checkeredPattern(row - 1, col);
                        System.out.print(" ");
                    }

                }
                System.out.print(RESET);
                System.out.print("|");

            }
            System.out.print("\n");
        }
    }

    private void printTitle() {
        System.out.print("  ");
        System.out.print("|");
        for (int i = 'a'; i < 'i'; i++) {
            System.out.print(" " + (char) i + " |");
        }
        System.out.print("\n");

        System.out.print("—".repeat(35) + "\n");
    }

    public void printAvailableMoves(HashSet<Position> moves) {
        printTitle();

        for (int row = 1; row < 9; row++) {
            System.out.print(row + " |");
            for (int col = 0; col < 8; col++) {

                checkeredPattern(row - 1, col);

//                    System.out.println("Printing something");
                if (moves.contains(new Position(row - 1, col))) {
                    System.out.print(outputHelpers.BACKGROUND_CYAN);
                    if (get(row - 1, col) == null) {
                        System.out.print(" * ");
                    } else {
                        System.out.print(" " + get(row - 1, col).pieceIdentification + " ");
                    }
                } else {
                    if (get(row - 1, col) == null)
                        System.out.print("   ");
                    else
                        System.out.print(" " + get(row - 1, col).pieceIdentification + " ");
                }

                System.out.print(RESET);
                System.out.print("|");

            }
            System.out.print("\n");
        }
    }

}
