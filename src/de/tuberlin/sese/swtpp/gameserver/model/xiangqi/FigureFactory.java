package de.tuberlin.sese.swtpp.gameserver.model.xiangqi;

public class FigureFactory {

	// TODO: Was wenn es ein Uppercase char ist?
	static Figure buildFigure(char name, Position pos) {
		char c = Character.toLowerCase(name);
		Figure fig = new Advisor(name, pos);;

		if (c == 'a') fig = new Advisor(name, pos);
		else if (c == 's') fig = new Soldier(name, pos);
		else if (c == 'r') fig = new Rook(name, pos);
		else if (c == 'h') fig = new Horse(name, pos);
		else if (c == 'c') fig = new Cannon(name, pos);
		else if (c == 'e') fig = new Elephant(name, pos);
		else fig = new General(name, pos);
		
		return fig;
	}
}
