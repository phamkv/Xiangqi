package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

abstract public class Figure implements Serializable {

	private static final long serialVersionUID = -6410894975363049863L;

	private boolean red = false;
	private char name;
	private Position pos;
	
	public Figure(char name, Position pos) {
		if ("RHEAGCS".indexOf(name) != -1) this.red = true;
		else this.red = false;
		
		this.name = name;
		this.pos = pos;
	}
	
	public boolean isRed() {
		return red;
	}
	public char getName() {
		// if (red) return Character.toUpperCase(name);
		return name;
	}
	public Position getPos() {
		return pos;
	}
	public void setPos(Position pos) {
		this.pos = pos;
	}
	
	protected abstract List<Position> getValidMoves(Map<Position, Figure> map);
	
	public boolean validateMove(Map<Position, Figure> map, Position destination) {
		List<Position> moves = getValidMoves(map);
		return moves.contains(destination);
	}
	
	public abstract List<Position> getBlockablePositionsForCheck(Map<Position, Figure> map, Figure enemyGeneral);

	@Override
	public String toString() {
		return "Figure [red=" + red + ", name=" + name + ", pos=" + pos + "]";
	}
	
	
	
}
