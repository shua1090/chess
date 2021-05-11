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
        return super.add(move);
    }

    public ArrayList<String> takenPieces(Color takenColor) {
        ArrayList<String> theTakenPieces = new ArrayList<>();
        Move[] theMoves = new Move[this.size()];

        for (int i = 0; i < this.size(); i++) {
            theMoves[i] = this.get(i);
        }

        for (Move i : theMoves) {
            if (i.takenPiece != null && i.takenPiece.getColor() == takenColor) {
                theTakenPieces.add(i.takenPiece.getClass().getSimpleName());
            }
        }
        return theTakenPieces;
    }

    public void applyUndo() {
//        System.out.println(this.size());
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


