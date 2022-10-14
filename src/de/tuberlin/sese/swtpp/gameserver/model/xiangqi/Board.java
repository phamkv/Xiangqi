package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Board implements Serializable {

	private static final long serialVersionUID = 5821159170194014445L;

	private Map<Position, Figure> positionFigureMap;
	private Map<Position, Figure> copy;
	
	private Figure redGeneral = null;
	private Figure blackGeneral = null;
	private Player checkedPlayer = Player.NONE;
	
	public Board(String fenString) {
		positionFigureMap = new TreeMap<Position, Figure>();
		String[] fenArray = fenString.split("/");
		for (int y = 0; y < fenArray.length; y++) {
			int x = 0;
			for (int i = 0; i < fenArray[y].length(); i++) {
				if ("rheagcs".indexOf(Character.toLowerCase(fenArray[y].charAt(i))) == -1) {
					x += Character.getNumericValue(fenArray[y].charAt(i));
					continue;
				}
				
				Position pos = new Position(x, 9 - y);
				// Attach a figure to pos
				Figure fig = FigureFactory.buildFigure(fenArray[y].charAt(i), pos);
				this.positionFigureMap.put(pos, fig);
				if (fenArray[y].charAt(i) == 'g') blackGeneral = positionFigureMap.get(pos);
				else if (fenArray[y].charAt(i) == 'G') redGeneral = positionFigureMap.get(pos);
				
				x++;
			}
		}
		copy = new TreeMap<Position, Figure>(positionFigureMap);
		// System.out.println(blackGeneral.toString() + redGeneral.toString());
		
		// Überprüfen, ob dieses Board, eine Schachsituation besitzt, 
		if (generalInCheck(true)) checkedPlayer = Player.RED;
		else if (generalInCheck(false)) checkedPlayer = Player.BLACK;
		// und wenn ja, ob es ein Schachmatt gib. (muessen wir das?)
	}
	
	public String getFEN() {
		String[] fenArray = new String[10];
		for (int y = 0; y < 10; y++) {
			int rowNum = y;
			TreeMap<Position, Figure> row = this.positionFigureMap.entrySet()
					.stream().filter(k -> k.getKey().getY() == 9 - rowNum)
					.collect(Collectors.toMap(e->e.getKey(),e->e.getValue(), (v1,v2) ->{ throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));}, TreeMap::new));
			
			if (row.isEmpty()) {
				fenArray[y] = Integer.toString(9);
				continue;
			}
			int x = -1;
			StringBuilder rowString = new StringBuilder();
			for (Map.Entry<Position, Figure> fig : row.entrySet()) {
				if (fig.getKey().getX() - x > 1) rowString.append(fig.getKey().getX() - x - 1);
				rowString.append(fig.getValue().getName());
				x = fig.getKey().getX();
			}
			if (x < 8) rowString.append(8-x);
			fenArray[y] = rowString.toString();
		}
		// System.out.println(String.join("/", fenArray));
		// System.out.println(blackGeneral.toString() + redGeneral.toString());
		return String.join("/", fenArray);
	}
	
	public boolean tryMove(String[] moveArray, boolean redTurn) {
		Position current = new Position(((int) moveArray[0].charAt(0) - 97), Integer.parseInt(moveArray[1]));
		Position destination = new Position(((int) moveArray[3].charAt(0) - 97), Integer.parseInt(moveArray[4]));

		Figure fig = this.positionFigureMap.remove(current);
		if (fig == null) return false;
		if (fig.isRed() != redTurn) {
			this.positionFigureMap.put(current, fig);
			return false;
		}
		
		if (!checkMove(current, destination, fig, redTurn)) return false;

		return true;
	}
	
	private boolean checkMove(Position current, Position destination, Figure fig, boolean redTurn) {
		if (!fig.validateMove(positionFigureMap, destination)) {
			this.positionFigureMap.put(current, fig);
			return false;
		}
		copy = new TreeMap<Position, Figure>(positionFigureMap);
		copy.put(destination, fig);
		fig.setPos(destination);
		if (generalInCheck(redTurn)) {
			System.out.println("Eigener General steht so in Schach. Zug wird nicht ausgeführt!");
			this.positionFigureMap.put(current, fig);
			fig.setPos(current);
			return false;
		} else {
			checkedPlayer = Player.NONE;
		}
		this.positionFigureMap.put(destination, fig);
		
		if (generalInCheck(!redTurn)) {
			checkedPlayer = redTurn ? Player.BLACK : Player.RED;
		}
		return true;
	}	
	
	private Map<Position, ArrayList<Figure>> getEveryValidMoveFromTeam(Map<Position, Figure> map, boolean red) {
		List<Figure> figureSameColor = map.values().stream().filter(fig -> fig.isRed() == red).collect(Collectors.toList());
		
		Map<Position, ArrayList<Figure>> movesFigureMap = new HashMap<>();
		for (Figure fig : figureSameColor) {
			fig.getValidMoves(map)
			.forEach(pos -> movesFigureMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(fig));			
		}
		
		return movesFigureMap;
	}
	
	private Map<Position, ArrayList<Figure>> getEveryAttackMoveFromTeam(Map<Position, Figure> map, boolean red) {
		List<Figure> figureSameColor = map.values().stream().filter(fig -> fig.isRed() == red).collect(Collectors.toList());
		
		Map<Position, ArrayList<Figure>> movesFigureMap = new HashMap<>();
		for (Figure fig : figureSameColor) {
			if (Character.toLowerCase(fig.getName()) == 'c') {
				Cannon cannon = (Cannon) fig;
				cannon.getValidAttacks(map)
				.forEach(pos -> movesFigureMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(fig));
			} else {
				fig.getValidMoves(map)
				.forEach(pos -> movesFigureMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(fig));
			}
		}
		
		return movesFigureMap;
	}
	
	private boolean generalInCheck(boolean red) {
		Map<Position, ArrayList<Figure>> enemyAttacks = getEveryAttackMoveFromTeam(copy, !red);
		Figure ownGeneral = getGeneral(red);
		return enemyAttacks.containsKey(ownGeneral.getPos());
	}
	
	public String pattCheck(boolean redTurn) {
		if (positionFigureMap.entrySet().stream().filter(v -> v.getValue().isRed() != redTurn).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).size() > 1) {
			return "";
		}
		System.out.println("Pattsituation ueberpruefen");
		Map<Position, ArrayList<Figure>> ownAttacks = getEveryAttackMoveFromTeam(positionFigureMap, redTurn);
		Figure enemyGeneral = getGeneral(!redTurn);
		if (canGeneralMove(!redTurn, enemyGeneral, ownAttacks)) {
			return "";
		}
		return "p";
	}
	
	
	public String checkMateCheck() {
		if (checkedPlayer == Player.NONE) return "";
		boolean red = checkedPlayer == Player.RED ? true : false;
		Map<Position, ArrayList<Figure>> enemyAttacks = getEveryAttackMoveFromTeam(positionFigureMap, !red);
		Figure ownGeneral = getGeneral(red);
		
		// Fall 1: Kann der General sich noch bewegen?
		if (canGeneralMove(red, ownGeneral, enemyAttacks)) return "";

		// Fall 2: Kann die Figur, die den General in Schach stellt, geschlagen werden?
		if (canEnemyBeKilled(red, ownGeneral, enemyAttacks)) {
			System.out.println("Enemy attack can be killed by unit.");
			return "";
		}
		
		// Fall 3: Kann die Angrifsbahn der Figur, die den General in Schach stellt, blockiert werden?
		if (canEnemyBeBlocked(red, ownGeneral, enemyAttacks)) {
			System.out.println("Enemy attack can be blocked by unit.");
			return "";
		}
		
		if (cannonEntrenchmentMove(red, ownGeneral, enemyAttacks)) return "";
		
		return red ? "r" : "b";
	}
	
	private boolean cannonEntrenchmentMove(boolean red, Figure ownGeneral, Map<Position, ArrayList<Figure>> enemyAttacks) {
		Figure enemyChecking = enemyAttacks.get(ownGeneral.getPos()).get(0);
		if (Character.toLowerCase(enemyChecking.getName()) != 'c') return false;
		Cannon cannon = (Cannon) enemyChecking;
		Map<Position, ArrayList<Figure>> movesFigureMap = new HashMap<>();
		// System.out.println(cannon.getCurrentEntrenchments());
		List<Position> entrenchmentFig = cannon.getCurrentEntrenchments().stream()
				.filter(pos -> (pos.getX()==ownGeneral.getPos().getX() 
				& (Math.abs(pos.getY()-ownGeneral.getPos().getY()) < Math.abs(cannon.getPos().getY()-ownGeneral.getPos().getY())))
		| (pos.getY()==ownGeneral.getPos().getY() 
				& (Math.abs(pos.getX()-ownGeneral.getPos().getX()) < Math.abs(cannon.getPos().getX()-ownGeneral.getPos().getX()))))
		.collect(Collectors.toList());
		
		// System.out.println(entrenchmentFig.get(0));
		if (positionFigureMap.get(entrenchmentFig.get(0)).isRed() ^ red) return false;
		List<Position> validMoves = positionFigureMap.get(entrenchmentFig.get(0)).getValidMoves(positionFigureMap);
		validMoves.forEach(pos -> movesFigureMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(positionFigureMap.get(entrenchmentFig.get(0))));
		// System.out.println("Checking Unit Moves: " + movesFigureMap.toString());
		
		return testMoveIfReallyOkay(movesFigureMap, red);
	}
	
	private boolean canGeneralMove(boolean red, Figure ownGeneral, Map<Position, ArrayList<Figure>> enemyAttacks) {

		List<Position> ownGeneralMoves = ownGeneral.getValidMoves(positionFigureMap);
		// System.out.println("General Moves: " + ownGeneralMoves.toString());
		// System.out.println(enemyAttacks.toString());
		List<Position> validMoves = ownGeneralMoves.stream().filter(pos -> !enemyAttacks.containsKey(pos)).collect(Collectors.toList());
		// System.out.println("Valid General Moves: " + validMoves.toString());
		if (validMoves.size() == 0) return false;
		
		Map<Position, ArrayList<Figure>> movesFigureMap = new HashMap<>();
		validMoves.forEach(pos -> movesFigureMap.computeIfAbsent(pos, k -> new ArrayList<>()).add(ownGeneral));
		// System.out.println("Checking General Moves: " + movesFigureMap.toString());
		
		return testMoveIfReallyOkay(movesFigureMap, red);
	}
	
	private boolean canEnemyBeKilled(boolean red, Figure ownGeneral, Map<Position, ArrayList<Figure>> enemyAttacks) {
		Figure enemyChecking = enemyAttacks.get(ownGeneral.getPos()).get(0);
		Map<Position, ArrayList<Figure>> ownTeamMoves = getEveryValidMoveFromTeam(positionFigureMap, red).entrySet().stream()
				.filter(entry -> entry.getKey().equals(enemyChecking.getPos()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));;
		
		// System.out.println("Moves zum Schlagen werden ueberprueft: " + ownTeamMoves.toString());
		return testMoveIfReallyOkay(ownTeamMoves, red);
	}
	
	private boolean canEnemyBeBlocked(boolean red, Figure ownGeneral, Map<Position, ArrayList<Figure>> enemyAttacks) {
		Figure enemyChecking = enemyAttacks.get(ownGeneral.getPos()).get(0);
		System.out.println("Folgende Steine stellen den General in Schach: " + enemyAttacks.get(ownGeneral.getPos()).toString() + " und folgender ist ausgewaehlt: " + enemyChecking);
		
		List<Position> blockablePositions = enemyChecking.getBlockablePositionsForCheck(positionFigureMap, ownGeneral);
		System.out.println("Folgende Positionen koennen blockiert werden: " + blockablePositions.toString());
		Map<Position, ArrayList<Figure>> ownTeamMoves = getEveryValidMoveFromTeam(positionFigureMap, red).entrySet().stream()
				.filter(entry -> blockablePositions.contains(entry.getKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));;
		System.out.println("Moves zum Blocken werden ueberprueft: " + ownTeamMoves.toString());
				
		return testMoveIfReallyOkay(ownTeamMoves, red);
	}
	
	// Sollte der Move den eigenen General anschließend weiterhin in Schach setzen -> Move ist doch ungültig
	private boolean testMoveIfReallyOkay(Map<Position, ArrayList<Figure>> testedMoveMap, boolean red) {
		Map<Position, ArrayList<Figure>> reallyValidMoves = new HashMap<>();
		for (Map.Entry<Position, ArrayList<Figure>> entry : testedMoveMap.entrySet()) {
			for (Figure fig : entry.getValue()) {
				copy = new TreeMap<Position, Figure>(positionFigureMap);
				copy.remove(fig.getPos());
				copy.put(entry.getKey(), fig);
				Position oldPos = fig.getPos();
				fig.setPos(entry.getKey());
				if (!generalInCheck(red)) {
					reallyValidMoves.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(fig);
				}
				fig.setPos(oldPos);
			}
		}
		System.out.println("Diese Moves sind wirklich gueltig: " + reallyValidMoves.toString());
		return reallyValidMoves.size() > 0;
	}
	
	private Figure getGeneral(boolean red) {
		return red ? redGeneral : blackGeneral;
	}
	
	public Map<Position, Figure> getPositionFigureMap() {
		return positionFigureMap;
	}
	
	private enum Player {
		RED, BLACK, NONE
	}
}
