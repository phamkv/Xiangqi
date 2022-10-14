package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

public class PositionOffsetFactory {

	static Position build(Position current, int offsetX, int offsetY) {
		return new Position(current.getX() + offsetX, current.getY() + offsetY);
		
	}
}
