package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Soldier extends Figure implements Serializable {

	private static final long serialVersionUID = -3029956106790298281L;


	public Soldier(char name, Position pos) {
		super(name, pos);
	}
	
	protected List<Position> getValidMoves(Map<Position, Figure> map) {
		List<Position> moves = new ArrayList<Position>();
		if (this.isRed()) {
			moves.add(PositionOffsetFactory.build(this.getPos(), 0, 1));
			if (this.getPos().getY() >= 5) {
				moves.add(PositionOffsetFactory.build(this.getPos(), 1, 0));
				moves.add(PositionOffsetFactory.build(this.getPos(), -1, 0));
			}
		} else {
			moves.add(PositionOffsetFactory.build(this.getPos(), 0, -1));
			if (this.getPos().getY() <= 4) {
				moves.add(PositionOffsetFactory.build(this.getPos(), 1, 0));
				moves.add(PositionOffsetFactory.build(this.getPos(), -1, 0));
			}
		}
		
		return moves.stream().filter(pos -> {
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
