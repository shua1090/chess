package chess;

import java.util.ArrayList;
import java.util.Stack;

class GameOutOfMoves extends IndexOutOfBoundsException {
}

public class MoveStack extends Stack<Move> {
    ChessBoard theBoard;

    MoveStack(ChessBoard board) {
        theBoard = board;
    }

    @Override
    public synchronized Move pop() {
        return super.pop();
    }

    @Override
    public synchronized boolean add(Move move) {
        System.out.println("Added");
        return super.add(move);
    }

    public ArrayList<String> takenPieces(Color takenColor) {
        ArrayList<String> theTakenPieces = new ArrayList<>();
        Move[] theMoves = (Move[]) this.toArray();
        for (Move i : theMoves) {
            if (i.takenPiece != null && i.takenPiece.getColor() == takenColor) {
                theTakenPieces.add(i.getClass().getSimpleName());
            }
        }
        return theTakenPieces;
    }

    public void applyUndo() {
        System.out.println(this.size());
        if (this.size() == 0) throw new GameOutOfMoves();
        else {
            Move theMove = this.pop();
            theBoard.forceSet(theMove.movedPiece, theMove.start);
            theBoard.forceSet(theMove.takenPiece, theMove.end);

        }
    }

}

class Move {
    Position start;
    Position end;
    Piece movedPiece;
    Piece takenPiece;

    Move(Position startingPos, Position endPos, Piece pieceThatMoved, Piece pieceThatWasTaken) {
        start = startingPos;
        end = endPos;
        movedPiece = pieceThatMoved;
        takenPiece = pieceThatWasTaken;
    }
}


