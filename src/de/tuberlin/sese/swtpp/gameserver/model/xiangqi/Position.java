package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.io.Serializable;
import java.util.Objects;

public class Position implements Serializable, Comparable<Position> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3597257695593891590L;
	
	
	// x goes left to right abcdefghi -> 0123456789
	// y goes from up to down -> 9876543210
	private int x;
	private int y;
	
	public Position(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		Position other = (Position) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public int compareTo(Position obj) {
		Position other = (Position) obj;
		
		if (this.x < other.x) return -1;
		else  if (this.x > other.x) return 1;
		if (this.y < other.y) return -1;
		else  if (this.y > other.y) return 1;
		return 0;
	}

	@Override
	public String toString() {
		return "Position [x=" + x + ", y=" + y + "]";
	}
	
	

}
