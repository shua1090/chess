package chess;

import java.util.HashSet;

public interface Side {

    default void setBoard(ChessBoard theBoard) {
        int row = (this.getColor() == Color.White) ? 6 : 1;

        for (int i = 0; i < 8; i++) {
            theBoard.move(new Pawn(this.getColor(), new Position(row, i)), row, i);
        }

        row = (this.getColor() == Color.White) ? 7 : 0;

        int col = 0;

        for (int i : new int[]{1, -1}) {
            theBoard.move(new Rook(this.getColor(), new Position(row, col)), row, col);
            col += i;
            theBoard.move(new Knight(this.getColor(), new Position(row, col)), row, col);
            col += i;
            theBoard.move(new Bishop(this.getColor(), new Position(row, col)), row, col);
            col = 7;
        }

        col = 3;

        theBoard.move(new Queen(this.getColor(), new Position(row, col)), row, col);

        col = 4;

        theBoard.move(new King(this.getColor(), new Position(row, col)), row, col);

    }

    Color getColor();

    default HashSet<Position> getAllValidMoves(ChessBoard theBoard) {
        HashSet<Position> validMoveSet = new HashSet<>();

        for (Piece[] row : theBoard.theBoard) {
            for (Piece col : row) {
                if (col != null && col.getColor() == this.getColor()) {
                    col.getMoves(theBoard);
                    validMoveSet.addAll(col.getAvailableMoves());
                }

            }
        }

        return validMoveSet;

    }

}

class WhiteSide implements Side {
    Color theColor;

    WhiteSide() {
        theColor = Color.White;
    }

    @Override
    public Color getColor() {
        return theColor;
    }

}

class BlackSide implements Side {
    Color theColor;

    BlackSide() {
        theColor = Color.Black;
    }

    @Override
    public Color getColor() {
        return theColor;
    }

}