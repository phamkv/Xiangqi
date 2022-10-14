package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.util.List;

public class Palace {
	
	private static List<Position> redPalace = List.of(new Position(3,0), new Position(4,0), new Position(5,0),
			new Position(3,1), new Position(4,1), new Position(5,1),
			new Position(3,2), new Position(4,2), new Position(5,2));

	private static List<Position> blackPalace = List.of(new Position(3,7), new Position(4,7), new Position(5,7),
			new Position(3,8), new Position(4,8), new Position(5,8),
			new Position(3,9), new Position(4,9), new Position(5,9));

	static List<Position> getPalace(boolean red) {
		return red ? redPalace : blackPalace;
	}

}
