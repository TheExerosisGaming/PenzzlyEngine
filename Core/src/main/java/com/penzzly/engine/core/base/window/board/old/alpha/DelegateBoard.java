package com.penzzly.engine.core.base.window.board.old.alpha;

import com.google.common.collect.BiMap;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class DelegateBoard implements Board {
	private Board board;
	private Board secondaryBoard;
	
	public DelegateBoard() {
		this(null);
	}
	
	public DelegateBoard(Board board) {
		this.board = board;
	}
	
	public void setSecondaryBoard(Board secondaryBoard) {
		this.secondaryBoard = secondaryBoard;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
	
	@Override
	public BiMap<Integer, Line> lines() {
		return board.lines();
	}
	
	@Nullable
	@Override
	public Line blank() {
		return board.blank();
	}
	
	@Override
	public Line line(Object text) {
		return board.line(text);
	}
	
	@Override
	public Board remove(int index) {
		return board.remove(index);
	}
	
	@Override
	public Board title(Object title) {
		return board.title(title);
	}
	
	@Override
	public Board numberingScheme(Function<Line, Integer> numberingScheme) {
		return board.numberingScheme(numberingScheme);
	}
}
