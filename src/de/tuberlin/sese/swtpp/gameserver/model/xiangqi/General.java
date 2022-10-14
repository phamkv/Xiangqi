package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class General extends Figure {

	private static final long serialVersionUID = 740069615128246264L;

	public General(char name, Position pos) {
		super(name, pos);
	}

	@Override
	protected List<Position> getValidMoves(Map<Position, Figure> map) {
		List<Position> moves = new ArrayList<Position>();
		moves.add(PositionOffsetFactory.build(this.getPos(), 0, 1));
		moves.add(PositionOffsetFactory.build(this.getPos(), 0, -1));
		moves.add(PositionOffsetFactory.build(this.getPos(), 1, 0));
		moves.add(PositionOffsetFactory.build(this.getPos(), -1, 0));
		moves.add(this.getFlyingGeneralMove(map));
		moves.removeAll(Collections.singleton(null));
		
		return moves.stream().filter(pos -> pos.equals(this.getFlyingGeneralMove(map)) || Palace.getPalace(this.isRed()).contains(pos)).filter(pos -> {
			if (map.containsKey(pos)) {
				return (map.get(pos).isRed() ^ this.isRed());
			}
			return true;
		}).collect(Collectors.toList());
	}
	
	private Position getFlyingGeneralMove(Map<Position, Figure> map) {
		int offset = this.isRed() ? 1 : -1;
		for (int i = this.getPos().getY() + offset; i >= 0 & i <= 9; i += offset ) {
			Position next = new Position(this.getPos().getX(), i);
			if (map.containsKey(next)) {
				
				if ((Character.toLowerCase(map.get(next).getName()) == 'g') & (map.get(next).isRed() ^ this.isRed())) {
					return next;
				}
				break;
			}
		}
		return null;
	}

	@Override
	public boolean validateMove(Map<Position, Figure> map, Position destination) {
		List<Position> moves = getValidMoves(map);
		// System.out.println(moves);
		return moves.contains(destination);
	}

	@Override
	public List<Position> getBlockablePositionsForCheck(Map<Position, Figure> map, Figure enemyGeneral) {
		return Collections.emptyList();
	}

}
