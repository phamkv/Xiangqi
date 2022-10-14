package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Elephant extends Figure {

	private static final long serialVersionUID = 1404466798077279537L;

	public Elephant(char name, Position pos) {
		super(name, pos);
	}

	@Override
	protected List<Position> getValidMoves(Map<Position, Figure> map) {
		List<Position> moves = new ArrayList<Position>();
		if (this.isRed()) {
			if (this.getPos().getY() < 3) {
				moves.add(this.getDirectionalMove(map, PositionOffsetFactory.build(getPos(), 1, 1)));
				moves.add(this.getDirectionalMove(map, PositionOffsetFactory.build(getPos(), -1, 1)));
			}
			moves.add(this.getDirectionalMove(map, PositionOffsetFactory.build(getPos(), 1, -1)));
			moves.add(this.getDirectionalMove(map, PositionOffsetFactory.build(getPos(), -1, -1)));
		} else {
			if (this.getPos().getY() > 6) {
				moves.add(this.getDirectionalMove(map, PositionOffsetFactory.build(getPos(), 1, -1)));
				moves.add(this.getDirectionalMove(map, PositionOffsetFactory.build(getPos(), -1, -1)));
			}
			moves.add(this.getDirectionalMove(map, PositionOffsetFactory.build(getPos(), 1, 1)));
			moves.add(this.getDirectionalMove(map, PositionOffsetFactory.build(getPos(), -1, 1)));
		}
		moves.removeAll(Collections.singleton(null));
		return moves.stream().filter(pos -> {
			if (map.containsKey(pos)) {
				return (map.get(pos).isRed() ^ this.isRed());
			}
			return true;
		}).collect(Collectors.toList());
	}
	
	private Position getDirectionalMove(Map<Position, Figure> map, Position direction) {
		if (map.containsKey(direction)) return null;
		return PositionOffsetFactory.build(direction, direction.getX()-this.getPos().getX(), direction.getY()-this.getPos().getY());
	}

	@Override
	public List<Position> getBlockablePositionsForCheck(Map<Position, Figure> map, Figure enemyGeneral) {
		return Collections.emptyList();
	}

}
