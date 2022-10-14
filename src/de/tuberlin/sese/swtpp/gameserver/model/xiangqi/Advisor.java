package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Advisor extends Figure {

	private static final long serialVersionUID = -8756761340683204473L;

	public Advisor(char name, Position pos) {
		super(name, pos);
	}
	
	@Override
	protected List<Position> getValidMoves(Map<Position, Figure> map) {
		List<Position> moves = new ArrayList<Position>();
		moves.add(PositionOffsetFactory.build(this.getPos(), 1, 1));
		moves.add(PositionOffsetFactory.build(this.getPos(), 1, -1));
		moves.add(PositionOffsetFactory.build(this.getPos(), -1, 1));
		moves.add(PositionOffsetFactory.build(this.getPos(), -1, -1));
		
		return moves.stream().filter(pos -> Palace.getPalace(this.isRed()).contains(pos)).filter(pos -> {
			if (map.containsKey(pos)) {
				return (map.get(pos).isRed() ^ this.isRed());
			}
			return true;
		}).collect(Collectors.toList());
	}

	@Override
	public List<Position> getBlockablePositionsForCheck(Map<Position, Figure> map, Figure enemyGeneral) {
		return Collections.emptyList();
	}

	

}
