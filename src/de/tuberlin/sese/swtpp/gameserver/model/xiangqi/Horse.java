package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Horse extends Figure {

	private static final long serialVersionUID = 843580670701470882L;

	public Horse(char name, Position pos) {
		super(name, pos);
	}

	@Override
	protected List<Position> getValidMoves(Map<Position, Figure> map) {
		List<Position> moves = new ArrayList<Position>();
		moves.addAll(this.getHorizontalJumpMove(map, PositionOffsetFactory.build(this.getPos(), 1, 0)));
		moves.addAll(this.getHorizontalJumpMove(map, PositionOffsetFactory.build(this.getPos(), -1, 0)));
		moves.addAll(this.getVerticalJumpMove(map, PositionOffsetFactory.build(this.getPos(), 0, 1)));
		moves.addAll(this.getVerticalJumpMove(map, PositionOffsetFactory.build(this.getPos(), 0, -1)));
		return moves;
	}
	
	private List<Position> getHorizontalJumpMove(Map<Position, Figure> map, Position direction) {
		if (map.containsKey(direction)) return Collections.emptyList();
		
		List<Position> moves = new ArrayList<Position>();
		moves.add(PositionOffsetFactory.build(direction, direction.getX()-this.getPos().getX(), 1));
		moves.add(PositionOffsetFactory.build(direction, direction.getX()-this.getPos().getX(), -1));
		
		return moves.stream().filter(pos -> {
			if (map.containsKey(pos)) {
				return (map.get(pos).isRed() ^ this.isRed());
			}
			return true;
		}).collect(Collectors.toList());
	}
	
	private List<Position> getVerticalJumpMove(Map<Position, Figure> map, Position direction) {
		if (map.containsKey(direction)) return Collections.emptyList();
		
		List<Position> moves = new ArrayList<Position>();
		moves.add(PositionOffsetFactory.build(direction, 1, direction.getY()-this.getPos().getY()));
		moves.add(PositionOffsetFactory.build(direction, -1, direction.getY()-this.getPos().getY()));
		
		return moves.stream().filter(pos -> {
			if (map.containsKey(pos)) {
				return (map.get(pos).isRed() ^ this.isRed());
			}
			return true;
		}).collect(Collectors.toList());
	}

	@Override
	public List<Position> getBlockablePositionsForCheck(Map<Position, Figure> map, Figure enemyGeneral) {
		Position direction = PositionOffsetFactory.build(this.getPos(), 1, 0);
		if (this.getHorizontalJumpMove(map, direction).contains(enemyGeneral.getPos())) return List.of(direction);
		
		direction = PositionOffsetFactory.build(this.getPos(), -1, 0);
		if (this.getHorizontalJumpMove(map, direction).contains(enemyGeneral.getPos())) return List.of(direction);
		
		direction = PositionOffsetFactory.build(this.getPos(), 0, 1);
		if (this.getVerticalJumpMove(map, direction).contains(enemyGeneral.getPos())) return List.of(direction);

		direction = PositionOffsetFactory.build(this.getPos(), 0, -1);
		return List.of(direction);
	}

}
