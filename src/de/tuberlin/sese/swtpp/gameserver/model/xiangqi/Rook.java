package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Rook extends Figure {

	private static final long serialVersionUID = 7572738052078589732L;

	public Rook(char name, Position pos) {
		super(name, pos);
	}

	@Override
	protected List<Position> getValidMoves(Map<Position, Figure> map) {
		List<Position> moves = new ArrayList<Position>();
		moves.addAll(this.getVerticalMoves(map, 1));
		moves.addAll(this.getVerticalMoves(map, -1));
		moves.addAll(this.getHorizontalMoves(map, 1));
		moves.addAll(this.getHorizontalMoves(map, -1));
		return moves;
	}
	
	private List<Position> getVerticalMoves(Map<Position, Figure> map, int offsetY) {
		List<Position> moves = new ArrayList<Position>();
		for (int i = this.getPos().getY() + offsetY; i >= 0 & i <= 9; i += offsetY ) {
			Position next = new Position(this.getPos().getX(), i);
			if (map.containsKey(next)) {
				if (map.get(next).isRed() ^ this.isRed()) moves.add(next);
				break;
			}
			moves.add(next);
		}
		
		return moves;
	}
	
	private List<Position> getHorizontalMoves(Map<Position, Figure> map, int offsetX) {
		List<Position> moves = new ArrayList<Position>();
		for (int i = this.getPos().getX() + offsetX; i >= 0 & i <= 8; i += offsetX ) {
			Position next = new Position(i, this.getPos().getY());
			if (map.containsKey(next)) {
				if (map.get(next).isRed() ^ this.isRed()) moves.add(next);
				break;
			}
			moves.add(next);
		}
		
		return moves;
	}

	@Override
	public List<Position> getBlockablePositionsForCheck(Map<Position, Figure> map, Figure enemyGeneral) {
		return this.getValidMoves(map).stream().filter(pos -> !pos.equals(enemyGeneral.getPos()))
				.filter(pos -> (pos.getX()==enemyGeneral.getPos().getX() 
						& (Math.abs(pos.getY()-enemyGeneral.getPos().getY()) < Math.abs(this.getPos().getY()-enemyGeneral.getPos().getY())))
				| (pos.getY()==enemyGeneral.getPos().getY() 
						& (Math.abs(pos.getX()-enemyGeneral.getPos().getX()) < Math.abs(this.getPos().getX()-enemyGeneral.getPos().getX()))))
				.collect(Collectors.toList());
	}

}
