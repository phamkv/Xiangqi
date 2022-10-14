package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cannon extends Figure {

	private static final long serialVersionUID = 6795508467084386381L;
	private List<Position> currentEntrenchments;

	public Cannon(char name, Position pos) {
		super(name, pos);
		currentEntrenchments = new ArrayList<Position>();
	}

	@Override
	protected List<Position> getValidMoves(Map<Position, Figure> map) {
		currentEntrenchments.clear();
		List<Position> moves = new ArrayList<Position>();
		moves.addAll(this.getVerticalMoves(map, 1, false));
		moves.addAll(this.getVerticalMoves(map, -1, false));
		moves.addAll(this.getHorizontalMoves(map, 1, false));
		moves.addAll(this.getHorizontalMoves(map, -1, false));
		// System.out.println("Cannon Moves: " + moves.toString());
		return moves;
	}
	
	public List<Position> getValidAttacks(Map<Position, Figure> map) {
		currentEntrenchments.clear();
		List<Position> moves = new ArrayList<Position>();
		moves.addAll(this.getVerticalMoves(map, 1, true));
		moves.addAll(this.getVerticalMoves(map, -1, true));
		moves.addAll(this.getHorizontalMoves(map, 1, true));
		moves.addAll(this.getHorizontalMoves(map, -1, true));
		// System.out.println("Cannon Attacks: " + moves.toString());
		return moves;
	}
	
	private List<Position> getVerticalMoves(Map<Position, Figure> map, int offsetY, boolean attack) {
		List<Position> moves = new ArrayList<Position>();
		boolean hasEntrenchment = false;
		for (int i = this.getPos().getY() + offsetY; i >= 0 & i <= 9; i += offsetY ) {
			Position next = new Position(this.getPos().getX(), i);
			
			if (hasEntrenchment) {
				if (map.containsKey(next)) {
					if (map.get(next).isRed() ^ this.isRed()) moves.add(next);
					break;
				} else if (attack) {
					moves.add(next);
				}
				continue;
			}
			
			if (map.containsKey(next)) {
				hasEntrenchment = true;
				currentEntrenchments.add(next);
				continue;
			} else {
				if (!attack) moves.add(next);
			}
		}
		return moves;
	}
	
	private List<Position> getHorizontalMoves(Map<Position, Figure> map, int offsetX, boolean attack) {
		List<Position> moves = new ArrayList<Position>();
		boolean hasEntrenchment = false;
		for (int i = this.getPos().getX() + offsetX; i >= 0 & i <= 8; i += offsetX ) {
			Position next = new Position(i, this.getPos().getY());
			
			if (hasEntrenchment) {
				if (map.containsKey(next)) {
					if (map.get(next).isRed() ^ this.isRed()) moves.add(next);
					break;
				} else if (attack) {
					moves.add(next);
				}
				continue;
			}
			
			if (map.containsKey(next)) {
				hasEntrenchment = true;
				currentEntrenchments.add(next);
				continue;
			} else {
				if (!attack) moves.add(next);
			}
		}
		return moves;
	}

	@Override
	public List<Position> getBlockablePositionsForCheck(Map<Position, Figure> map, Figure enemyGeneral) {
		return Stream.concat(getValidAttacks(map).stream(), getValidMoves(map).stream()).filter(pos -> !pos.equals(enemyGeneral.getPos()))
				.filter(pos -> (pos.getX()==enemyGeneral.getPos().getX() 
				& (Math.abs(pos.getY()-enemyGeneral.getPos().getY()) < Math.abs(this.getPos().getY()-enemyGeneral.getPos().getY())))
		| (pos.getY()==enemyGeneral.getPos().getY() 
				& (Math.abs(pos.getX()-enemyGeneral.getPos().getX()) < Math.abs(this.getPos().getX()-enemyGeneral.getPos().getX()))))
		.collect(Collectors.toList());
				
	}

	public List<Position> getCurrentEntrenchments() {
		return currentEntrenchments;
	}
}
