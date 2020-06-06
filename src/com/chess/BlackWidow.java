package com.chess;

import com.chess.engine.classic.board.Board;
import com.chess.gui.Table;

public class BlackWidow {

    public static void main(final String[] args) throws Exception {
        Board board = Board.createStandardBoard();
        System.out.println(board);
        Table.get().show();
    }
}
