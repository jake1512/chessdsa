package com.chess.engine.classic.pieces;

import com.chess.engine.classic.Alliance;
import com.chess.engine.classic.board.Board;
import com.chess.engine.classic.board.BoardUtils;
import com.chess.engine.classic.board.Move;
import com.chess.engine.classic.board.Move.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Pawn extends Piece {

    /*  From the current piece's position, adding or subtracting these coordinates 
        provides possible move positions, hence the name CANDIDATE_MOVE_COORDINATES
    */
    private final static int[] CANDIDATE_MOVE_COORDINATES = {8, 16, 7, 9};  // cannot go backward

    public Pawn(final Alliance allegiance,
                final int piecePosition) {
        super(PieceType.PAWN, allegiance, piecePosition, true);
    }

    public Pawn(final Alliance alliance,
                final int piecePosition,
                final boolean isFirstMove) {
        super(PieceType.PAWN, alliance, piecePosition, isFirstMove);
    }

    @Override
    public int locationBonus() {
        return this.pieceAlliance.pawnBonus(this.piecePosition);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();
        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATES) {
             // because the direction of movement of either is opposition 
             // getDirection method is set in Alliance class with 1 for black and -1 for white
            int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);
            // If destination is invalid, skip
            if (!BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                continue;
            }

            // If destination is not occupied
            if (currentCandidateOffset == 8 && board.getPiece(candidateDestinationCoordinate) == null) {
                // If pawn gets to the respective last square of the board/the pawn promotion square
                if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                    legalMoves.add(new PawnPromotion(
                            new PawnMove(board, this, candidateDestinationCoordinate), PieceUtils.INSTANCE.getMovedQueen(this.pieceAlliance, candidateDestinationCoordinate)));
                    legalMoves.add(new PawnPromotion(
                            new PawnMove(board, this, candidateDestinationCoordinate), PieceUtils.INSTANCE.getMovedRook(this.pieceAlliance, candidateDestinationCoordinate)));
                    legalMoves.add(new PawnPromotion(
                            new PawnMove(board, this, candidateDestinationCoordinate), PieceUtils.INSTANCE.getMovedBishop(this.pieceAlliance, candidateDestinationCoordinate)));
                    legalMoves.add(new PawnPromotion(
                            new PawnMove(board, this, candidateDestinationCoordinate), PieceUtils.INSTANCE.getMovedKnight(this.pieceAlliance, candidateDestinationCoordinate)));
                }
                else {
                    legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                }
            }
            // Pawn move 2 steps forward
            else if (currentCandidateOffset == 16 && this.isFirstMove() &&  
                    ((BoardUtils.INSTANCE.SECOND_ROW.get(this.piecePosition) && this.pieceAlliance.isBlack()) ||
                     (BoardUtils.INSTANCE.SEVENTH_ROW.get(this.piecePosition) && this.pieceAlliance.isWhite()))) {
                final int behindCandidateDestinationCoordinate =
                        this.piecePosition + (this.pieceAlliance.getDirection() * 8);
                    // Verify if the destination tile and the tile behind it is unoccupied so that the pawn can make the move
                    if (board.getPiece(candidateDestinationCoordinate) == null &&
                    board.getPiece(behindCandidateDestinationCoordinate) == null) {
                    legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                }
            }
            // Pawn attack move
            else if (currentCandidateOffset == 7 &&
                    !((BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.piecePosition) && this.pieceAlliance.isWhite()) ||    //out of bound
                      (BoardUtils.INSTANCE.FIRST_COLUMN.get(this.piecePosition) && this.pieceAlliance.isBlack()))) {
                if(board.getPiece(candidateDestinationCoordinate) != null) {
                    final Piece pieceOnCandidate = board.getPiece(candidateDestinationCoordinate);
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {     //pawn in bottom row, execute Promotion
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate), PieceUtils.INSTANCE.getMovedQueen(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate), PieceUtils.INSTANCE.getMovedRook(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate), PieceUtils.INSTANCE.getMovedBishop(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate), PieceUtils.INSTANCE.getMovedKnight(this.pieceAlliance, candidateDestinationCoordinate)));
                        }
                        else {
                            legalMoves.add(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate, pieceOnCandidate));
                        }
                    }
                } else if (board.getEnPassantPawn() != null && board.getEnPassantPawn().getPiecePosition() ==
                           (this.piecePosition + (this.pieceAlliance.getOppositeDirection()))) {
                    final Piece pieceOnCandidate = board.getEnPassantPawn();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
                        legalMoves.add(
                                new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));

                    }
                }
            }
            // Pawn attack move
            else if (currentCandidateOffset == 9 &&
                    !((BoardUtils.INSTANCE.FIRST_COLUMN.get(this.piecePosition) && this.pieceAlliance.isWhite()) ||
                      (BoardUtils.INSTANCE.EIGHTH_COLUMN.get(this.piecePosition) && this.pieceAlliance.isBlack()))) {
                if(board.getPiece(candidateDestinationCoordinate) != null) {
                    if (this.pieceAlliance !=
                            board.getPiece(candidateDestinationCoordinate).getPieceAllegiance()) {
                        if (this.pieceAlliance.isPawnPromotionSquare(candidateDestinationCoordinate)) {
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                            board.getPiece(candidateDestinationCoordinate)), PieceUtils.INSTANCE.getMovedQueen(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                            board.getPiece(candidateDestinationCoordinate)), PieceUtils.INSTANCE.getMovedRook(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                            board.getPiece(candidateDestinationCoordinate)), PieceUtils.INSTANCE.getMovedBishop(this.pieceAlliance, candidateDestinationCoordinate)));
                            legalMoves.add(new PawnPromotion(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                            board.getPiece(candidateDestinationCoordinate)), PieceUtils.INSTANCE.getMovedKnight(this.pieceAlliance, candidateDestinationCoordinate)));
                        }
                        else {
                            legalMoves.add(
                                    new PawnAttackMove(board, this, candidateDestinationCoordinate,
                                            board.getPiece(candidateDestinationCoordinate)));
                        }
                    }
                }
                // Pawn en passant move 
                // (If a pawn jumps 2 steps forward and there is an enemy pawn next to it, 
                // that pawn can be attacked by said enemy pawn)
                else if (board.getEnPassantPawn() != null && board.getEnPassantPawn().getPiecePosition() ==
                        (this.piecePosition - (this.pieceAlliance.getOppositeDirection()))) {
                    final Piece pieceOnCandidate = board.getEnPassantPawn();
                    if (this.pieceAlliance != pieceOnCandidate.getPieceAllegiance()) {
                        legalMoves.add(
                                new PawnEnPassantAttack(board, this, candidateDestinationCoordinate, pieceOnCandidate));

                    }
                }
            }
        }
        return Collections.unmodifiableList(legalMoves);
    }

    @Override
    public String toString() {
        return this.pieceType.toString();
    }

    @Override
    public Pawn movePiece(final Move move) {
        return PieceUtils.INSTANCE.getMovedPawn(move.getMovedPiece().getPieceAllegiance(), move.getDestinationCoordinate());
    }

}