package chess;

import java.util.ArrayList;
import java.util.Objects;

import static chess.MoveCode.*;


enum Color {
    Black,
    White,
}

enum MoveCode {
    SamePieceAtPosition,
    EnemyPieceAtPosition,
    Valid,
    OutOfBounds
}

class OutOfBoardException extends IndexOutOfBoundsException {
}

class Position {
    public int row;
    public int column;

    Position(int row, int col) {
        this.row = row;
        this.column = col;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Override
    public String toString() {
        return "(" + row +
                ", " + column + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return row == position.row && column == position.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

}

public abstract class Piece {

    protected final Color color;
    protected ArrayList<Position> availableMoves = new ArrayList<>();
    protected Position currentPosition;
    protected String pieceIdentification = null;

    Piece(Color theColor, Position thePosition) {
        this.color = theColor;
        this.currentPosition = thePosition;
        this.pieceIdentification = (theColor == Color.Black) ? outputHelpers.RED : outputHelpers.YELLOW;
        this.pieceIdentification += outputHelpers.BOLD;

    }

    public boolean positionInAvailableMoves(Position positionToCheck) {
        for (Position p : availableMoves) if (positionToCheck.equals(p)) return true;
        return false;
    }

    // GETTERS
    protected abstract void getMoves(ChessBoard theChessBoard);

    public ArrayList<Position> getAvailableMoves() {
        return availableMoves;
    }

    public Color getColor() {
        return color;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Position currentPosition) {
        this.currentPosition = currentPosition;
    }

    // SETTERS

    public void setPosition(int row, int col) {
        this.currentPosition = new Position(row, col);
    }

    protected MoveCode addMove(Position p, Piece thePiece) {

        if (thePiece == null) {
            this.availableMoves.add(p);
            return Valid;
        } else if (thePiece instanceof OutOfBoundsPiece) {
            return OutOfBounds;
        } else if (thePiece.getColor() == this.getColor()) {
            return SamePieceAtPosition;
        } else if (thePiece.getColor() != this.getColor()) {
            this.availableMoves.add(p);
            return EnemyPieceAtPosition;
        }
        return null;
    }

}

class OutOfBoundsPiece extends Piece {

    OutOfBoundsPiece(Color theColor, Position thePosition) {
        super(theColor, thePosition);
    }

    @Override
    protected void getMoves(ChessBoard theChessBoard) {
    }
}

class Rook extends Piece {

    Rook(Color theColor, Position thePosition) {
        super(theColor, thePosition);
        pieceIdentification += "R";
//        pieceIdentification = (theColor == Color.Black) ? outputHelpers.BLACK : outputHelpers.WHITE + "R";
//        pieceIdentification = (theColor == Color.Black) ? "♜" : "♖";
//        pieceIdentification = theColor.toString().charAt(0) + "R";
    }

    @Override
    protected void getMoves(ChessBoard theChessBoard) {

        this.availableMoves.clear();

        for (int multiplier : new int[]{1, -1}) {
            // EAST
            for (int i = 1; i < 8; i++) {
                Piece pieceAtThisPosition = theChessBoard.get(this.currentPosition.row, currentPosition.column + i * multiplier);
                MoveCode code = (addMove(new Position(currentPosition.row, currentPosition.column + i * multiplier), pieceAtThisPosition));
                if (code != Valid) {
                    break;
                }
            }

            // SOUTH
            for (int i = 1; i < 8; i++) {
                Piece pieceAtThisPosition = theChessBoard.get(currentPosition.row + i * multiplier, currentPosition.column);
                MoveCode code = (addMove(new Position(currentPosition.row + i * multiplier, currentPosition.column), pieceAtThisPosition));
                if (code != Valid) {
                    break;
                }
            }
        }
    }

}

class Knight extends Piece {

    Knight(Color theColor, Position thePosition) {
        super(theColor, thePosition);
        pieceIdentification += "K";
//        pieceIdentification = (theColor == Color.Black) ? outputHelpers.BLACK : outputHelpers.WHITE + "K";
//        pieceIdentification = (theColor == Color.Black) ? "♞" : "♘";
        //pieceIdentification = theColor.toString().charAt(0) + "K";
    }

    public static void main(String[] args) {
        ChessBoard b = new ChessBoard();

        Queen z = new Queen(Color.Black, new Position(3, 3));

        b.theBoard[3][3] = z;
        b.theBoard[2][4] = new Rook(Color.White, new Position(2, 4));

        z.getMoves(b);
        System.out.println(" = " + z.availableMoves);
    }

    @Override
    protected void getMoves(ChessBoard theChessBoard) {
        this.availableMoves.clear();

        for (int row : new int[]{currentPosition.row - 1, currentPosition.row + 1}) {
            for (int col : new int[]{currentPosition.column - 2, currentPosition.column + 2}) {
                addMove(new Position(row, col), theChessBoard.get(row, col));
            }
        }

        for (int row : new int[]{currentPosition.row - 2, currentPosition.row + 2}) {
            for (int col : new int[]{currentPosition.column - 1, currentPosition.column + 1}) {
                addMove(new Position(row, col), theChessBoard.get(row, col));
            }
        }

    }

}

class Bishop extends Piece {

    Bishop(Color theColor, Position thePosition) {
        super(theColor, thePosition);
        pieceIdentification += "B";
//        pieceIdentification = theColor.toString().charAt(0) + "B";
    }

    @Override
    protected void getMoves(ChessBoard theChessBoard) {
        this.availableMoves.clear();

        for (int z : new int[]{-1, 1}) {
            for (int k : new int[]{-1, 1}) {
                for (int i = 1; i < 8; i++) {
                    MoveCode code = addMove(new Position(currentPosition.row + z * i, currentPosition.column + k * i),
                            theChessBoard.get(currentPosition.row + z * i, currentPosition.column + k * i));

                    if (code != Valid) break;
                } // Innermost For Loop
            } // 2nd Innermost For Loop
        } // Outermost for loop

    }
}

class Queen extends Piece {

    Queen(Color theColor, Position thePosition) {
        super(theColor, thePosition);
        pieceIdentification += "Q";
    }

    @Override
    protected void getMoves(ChessBoard theChessBoard) {
        this.availableMoves.clear();

        // DIAGONAL
        for (int z : new int[]{-1, 1}) {
            for (int k : new int[]{-1, 1}) {
                for (int i = 1; i < 8; i++) {
                    MoveCode code = addMove(new Position(currentPosition.row + z * i, currentPosition.column + k * i),
                            theChessBoard.get(currentPosition.row + z * i, currentPosition.column + k * i));

                    if (code != Valid) break;

                } // Innermost For Loop
            } // 2nd Innermost For Loop
        } // Outermost for loop

        for (int multiplier : new int[]{1, -1}) {
            // EAST
            for (int i = 1; i < 8; i++) {
                Piece pieceAtThisPosition = theChessBoard.get(this.currentPosition.row, currentPosition.column + i * multiplier);
                MoveCode code = (addMove(new Position(currentPosition.row, currentPosition.column + i * multiplier), pieceAtThisPosition));
                if (code != Valid) {
                    break;
                }
            }

            // SOUTH
            for (int i = 1; i < 8; i++) {
                Piece pieceAtThisPosition = theChessBoard.get(currentPosition.row + i * multiplier, currentPosition.column);
                MoveCode code = (addMove(new Position(currentPosition.row + i * multiplier, currentPosition.column), pieceAtThisPosition));
                if (code != Valid) {
                    break;
                }
            }
        }
    }
}

class King extends Piece {

    King(Color theColor, Position thePosition) {
        super(theColor, thePosition);
        pieceIdentification += "W";        // pieceIdentification = theColor.toString().charAt(0) + "W";
    }

    @Override
    protected void getMoves(ChessBoard theChessBoard) {
        // DIAGONAL
        for (int z : new int[]{-1, 1}) {
            for (int k : new int[]{-1, 1}) {
                int i = 1;
                MoveCode code = addMove(new Position(currentPosition.row + z * i, currentPosition.column + k * i),
                        theChessBoard.get(currentPosition.row + z * i, currentPosition.column + k * i));
            } // 2nd Innermost For Loop
        } // Outermost for loop

        for (int multiplier : new int[]{1, -1}) {
            // EAST
            int i = 1;
            Piece pieceAtThisPosition = theChessBoard.get(this.currentPosition.row, currentPosition.column + i * multiplier);
            addMove(new Position(currentPosition.row, currentPosition.column + i * multiplier), pieceAtThisPosition);

            pieceAtThisPosition = theChessBoard.get(currentPosition.row + i * multiplier, currentPosition.column);
            addMove(new Position(currentPosition.row + i * multiplier, currentPosition.column), pieceAtThisPosition);
        }
    }

}

class Pawn extends Piece {

    Pawn(Color theColor, Position thePosition) {
        super(theColor, thePosition);
        pieceIdentification += "P";
//        pieceIdentification = (theColor == Color.Black) ? outputHelpers.BLACK : outputHelpers.WHITE + "P";
//         pieceIdentification = outputHelpers.BACKGROUND_BLACK + "P";
    }

    @Override
    protected void getMoves(ChessBoard theChessBoard) {
        if (this.color == Color.Black) {
            addMove(new Position(currentPosition.row + 1, currentPosition.column), theChessBoard.get(currentPosition.row + 1, currentPosition.column));
        }
        if (this.color == Color.White) {
            addMove(new Position(currentPosition.row - 1, currentPosition.column), theChessBoard.get(currentPosition.row - 1, currentPosition.column));
        }
    }
}



