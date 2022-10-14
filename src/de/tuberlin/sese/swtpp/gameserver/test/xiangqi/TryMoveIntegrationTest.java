package de.tuberlin.sese.swtpp.gameserver.test.xiangqi;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.tuberlin.sese.swtpp.gameserver.control.GameController;
import de.tuberlin.sese.swtpp.gameserver.model.Game;
import de.tuberlin.sese.swtpp.gameserver.model.Player;
import de.tuberlin.sese.swtpp.gameserver.model.User;

public class TryMoveIntegrationTest {


	User user1 = new User("Alice", "alice");
	User user2 = new User("Bob", "bob");
	
	Player redPlayer = null;
	Player blackPlayer = null;
	Game game = null;
	GameController controller;
	
	@Before
	public void setUp() throws Exception {
		controller = GameController.getInstance();
		controller.clear();
		
		int gameID = controller.startGame(user1, "", "xiangqi");
		
		game =  controller.getGame(gameID);
		redPlayer = game.getPlayer(user1);

	}
	
	public void startGame() {
		controller.joinGame(user2, "xiangqi");		
		blackPlayer = game.getPlayer(user2);
	}
	
	public void startGame(String initialBoard, boolean redNext) {
		startGame();
		
		game.setBoard(initialBoard);
		game.setNextPlayer(redNext? redPlayer:blackPlayer);
	}
	
	public void assertMove(String move, boolean red, boolean expectedResult) {
		if (red)
			assertEquals(expectedResult, game.tryMove(move, redPlayer));
		else 
			assertEquals(expectedResult,game.tryMove(move, blackPlayer));
	}
	
	public void assertGameState(String expectedBoard, boolean redNext, boolean finished, boolean redWon) {
		assertEquals(expectedBoard,game.getBoard());
		assertEquals(finished, game.isFinished());

		if (!game.isFinished()) {
			assertEquals(redNext, game.getNextPlayer() == redPlayer);
		} else {
			assertEquals(redWon, redPlayer.isWinner());
			assertEquals(!redWon, blackPlayer.isWinner());
		}
	}
	

	/*******************************************
	 * !!!!!!!!! To be implemented !!!!!!!!!!!!
	 *******************************************/
	
	@Test
	public void exampleTest() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR",true);
	    assertMove("e3-e4",true,true);
	    assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR",false,false,false);
	}

	//TODO: implement test cases of same kind as example here
	
	@Test
	public void blackItsNotYourTurn() {
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR", true);
		assertMove("e6-e5",false,false);
		assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR", true, false, false);
	}

	@Test
	public void redItsNotYourTurn() {
	    startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR",false);
	    assertMove("e3-e4",true,false);
	    assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR",false,false,false);
	}
	
	@Test
	public void fileMoveIsInvalid1() {
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR", true);
		assertMove("24-e5",true,false);
		assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR", true, false, false);
	}
	
	@Test
	public void fileMoveIsInvalid2() {
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR", true);
		assertMove("e4-k5",true,false);
		assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR", true, false, false);
	}
	
	@Test
	public void rankMoveIsInvalid1() {
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR", false);
		assertMove("ea-e5",false,false);
		assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR", false, false, false);
	}
	
	@Test
	public void rankMoveIsInvalid2() {
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR", false);
		assertMove("e5-ha",false,false);
		assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR", false, false, false);
	}
	
	@Test
	public void moveStringDashInvalid() {
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR", false);
		assertMove("e5+ha",false,false);
		assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR", false, false, false);
	}
	
	@Test
	public void moveStringTooShort() {
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR", false);
		assertMove("e5aa",false,false);
		assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR", false, false, false);
	}
	
	@Test
	public void whatDaSoldierDoing() {
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR", true);
		assertMove("e3-d3", true, false);
		assertGameState("rheagaehr/9/1c5c1/s1s1s1s1s/9/9/S1S1S1S1S/1C5C1/9/RHEAGAEHR", true, false, false);
	}
	
	@Test
	public void moveHorseFromStart() {
		startGame("rheagaehr/9/1c5c1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR", false);
		assertMove("h9-g7",false,true);
		assertGameState("rheagae1r/9/1c4hc1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR", true, false, false);
	}
	
	@Test
	public void moveGeneralForward() {
		startGame("rheagae1r/9/1c4hc1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/9/RHEAGAEHR", true);
		assertMove("e0-e1", true, true);
		assertGameState("rheagae1r/9/1c4hc1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/4G4/RHEA1AEHR", false, false, false);
		assertMove("e9-e8", false, true);
		assertGameState("rhea1ae1r/4g4/1c4hc1/s1s1s1s1s/9/4S4/S1S3S1S/1C5C1/4G4/RHEA1AEHR", true, false, false);
	}
	
	@Test
	public void redSoldierOverRiver() {
		startGame("rheag1ehr/4a4/1c5c1/s1s3s1s/4S4/9/S1S3S1S/1C5C1/9/RHEAGAEHR", true);
		assertMove("e5-f5", true, true);
		assertGameState("rheag1ehr/4a4/1c5c1/s1s3s1s/5S3/9/S1S3S1S/1C5C1/9/RHEAGAEHR", false, false, false);
	}
	
	@Test
	public void blackSoldierBeatsRedUnit() {
		startGame("r1eag1ehr/4a4/1ch4c1/s1s3s1s/6S2/9/S1S3S1S/1C5C1/9/RHEAGAEHR", false);
		assertMove("g6-g5", false, true);
		assertGameState("r1eag1ehr/4a4/1ch4c1/s1s5s/6s2/9/S1S3S1S/1C5C1/9/RHEAGAEHR", true, false, false);
	}
	
	@Test
	public void horseMoveTest() {
		startGame("r1eag1ehr/4a4/1ch4c1/s1s5s/9/6s2/S1S3S1S/1C4C2/9/RHEAGAEHR", true);
		assertMove("h0-g2", true, false);
		assertGameState("r1eag1ehr/4a4/1ch4c1/s1s5s/9/6s2/S1S3S1S/1C4C2/9/RHEAGAEHR", true, false, false);
	}
	
	@Test
	public void ElephantMove() {
		startGame("r1eag1ehr/4a4/1ch4c1/s1s5s/9/6s2/S1S3S1S/1C4C2/9/RHEAGAEHR", true);
		assertMove("c0-e2", true, true);
		assertGameState("r1eag1ehr/4a4/1ch4c1/s1s5s/9/6s2/S1S3S1S/1C2E1C2/9/RH1AGAEHR", false, false, false);
	}
	
	@Test
	public void blackSoldierOverRiver() {
		startGame("r1eag1ehr/4a4/1ch4c1/s1s5s/9/6s2/S1S3S1S/1C2E1C2/9/RH1AGAEHR", false);
		assertMove("g4-f4", false, true);
		assertGameState("r1eag1ehr/4a4/1ch4c1/s1s5s/9/5s3/S1S3S1S/1C2E1C2/9/RH1AGAEHR", true, false, false);
	}
	
	@Test
	public void canElephantsSwim() {
		startGame("r1eag1ehr/4a4/1ch4c1/s1s5s/9/2E6/S1S2sS1S/1C4C2/9/RH1AGAEHR", true);
		assertMove("c4-a6", true, false);
		assertGameState("r1eag1ehr/4a4/1ch4c1/s1s5s/9/2E6/S1S2sS1S/1C4C2/9/RH1AGAEHR", true, false, false);
	}
	
	@Test
	public void repositionCannon() {
		startGame("r1eag2hr/4a4/1ch1e2c1/s1s5s/9/9/S1S2sS1S/EC4C2/9/RH1AGAEHR", true);
		assertMove("b2-b3", true, true);
		assertGameState("r1eag2hr/4a4/1ch1e2c1/s1s5s/9/9/SCS2sS1S/E5C2/9/RH1AGAEHR", false, false, false);
	}
	
	@Test
	public void canElephantSwimBlackEdition() {
		startGame("r1eag2hr/4a4/1ch1e2c1/s1s5s/9/9/SCS2sS1S/E5C2/9/RH1AGAEHR", false);
		assertMove("e7-g5", false, true);
		assertGameState("r1eag2hr/4a4/1ch4c1/s1s5s/6e2/9/SCS2sS1S/E5C2/9/RH1AGAEHR", true, false, false);
	}
	
	@Test
	public void waitWrongTargetCannon() {
		startGame("r1eag2hr/4a4/1c4c2/s1s1h3s/6e2/9/SCS1H1s1S/E5C2/9/R2AGAEHR", false);
		assertMove("g7-g3", false, false);
		assertGameState("r1eag2hr/4a4/1c4c2/s1s1h3s/6e2/9/SCS1H1s1S/E5C2/9/R2AGAEHR", false, false, false);
	}
	
	@Test
	public void rookActivated() {
		startGame("r1eag2hr/4a4/1c4c2/s1s1h4/6C2/9/SCS1H1s1s/E8/9/R2AGAEHR", true);
		assertMove("i0-i3", true, true);
		assertGameState("r1eag2hr/4a4/1c4c2/s1s1h4/6C2/9/SCS1H1s1R/E8/9/R2AGAEH1", false, false, false);
	}
	
	@Test
	public void flyingGeneralTest() {
		startGame("r1eag4/4a4/1c4c1h/s1s1h4/6C2/9/SCS1H1s1r/E8/4A4/R2A1GEH1", false);
		assertMove("e9-f9", false, false);
		assertGameState("r1eag4/4a4/1c4c1h/s1s1h4/6C2/9/SCS1H1s1r/E8/4A4/R2A1GEH1", false, false, false);
	}
	
	@Test
	public void flyingGeneralRed() {
		startGame("r1ea1g3/4a4/1c4c1h/s1s1h4/6C2/9/SCS1H1s1r/E8/4A4/R2AG1EH1", true);
		assertMove("e0-f0", true, false);
		assertGameState("r1ea1g3/4a4/1c4c1h/s1s1h4/6C2/9/SCS1H1s1r/E8/4A4/R2AG1EH1", true, false, false);
	}
	
	@Test
	public void moveGeneralBlack() {
		startGame("r1eag4/4a4/1c4c1h/s1s1h4/6C2/9/SCS1H1s1r/E8/4A4/R2AG1EH1", false);
		assertMove("e9-f9", false, true);
		assertGameState("r1ea1g3/4a4/1c4c1h/s1s1h4/6C2/9/SCS1H1s1r/E8/4A4/R2AG1EH1", true, false, false);
	}
	
	@Test
	public void moveGeneralRed() {
		startGame("r1e2g3/4a4/1c3ac1h/s1s1h4/6C2/9/SCS1H1s1r/E4A3/4A4/R3G1EH1", true);
		assertMove("e0-d0", true, true);
		assertGameState("r1e2g3/4a4/1c3ac1h/s1s1h4/6C2/9/SCS1H1s1r/E4A3/4A4/R2G2EH1", false, false, false);
	}
	
	@Test
	public void noFigureSelected() {
		startGame("r1e2g3/4a4/1c3ac1h/s1s1h4/6C2/9/SCS1H1s1r/E4A3/4A4/R2G2EH1", false);
		assertMove("i8-h7", false, false);
		assertGameState("r1e2g3/4a4/1c3ac1h/s1s1h4/6C2/9/SCS1H1s1r/E4A3/4A4/R2G2EH1", false, false, false);
	}
	
	@Test
	public void takeYourOwnColor() {
		startGame("r1e2g3/4a4/1c3ac1h/s1s1h4/6C2/9/SCS1H1s1r/E4A3/4A4/R2G2EH1", false);
		assertMove("c3-c4", false, false);
		assertGameState("r1e2g3/4a4/1c3ac1h/s1s1h4/6C2/9/SCS1H1s1r/E4A3/4A4/R2G2EH1", false, false, false);
	}
	
	@Test
	public void blackCheckButGeneralCanMove() {
		startGame("r1e2g3/4a4/1c3ac1h/s3h4/2s3C2/9/SCS1H1s1r/E4A3/4A4/R2G2EH1", true);
		assertMove("g5-f5", true, true);
		assertGameState("r1e2g3/4a4/1c3ac1h/s3h4/2s2C3/9/SCS1H1s1r/E4A3/4A4/R2G2EH1", false, false, false);
	}
	
	
	@Test
	public void iThinkIMissedSomething() {
		startGame("r2cgc3/9/e7h/s3h2H1/2s2C3/S8/1C1S2s1r/E2A1A3/9/R2G2EH1", true);
		assertMove("f5-e5", true, true);
		assertGameState("r2cgc3/9/e7h/s3h2H1/2s1C4/S8/1C1S2s1r/E2A1A3/9/R2G2EH1", false, false, false);
	}
	
	@Test
	public void cannonCheckEntrenchmentMoveY() {
		startGame("r2cg1c2/9/e1h5h/s6H1/2S2C3/S8/1C4s1r/E4A3/2s6/R2G1AEH1", false);
		assertMove("g9-g0", false, true);
		assertGameState("r2cg4/9/e1h5h/s6H1/2S2C3/S8/1C4s1r/E4A3/2s6/R2G1AcH1", true, false, false);
	}
	
	@Test
	public void rookCheckY() {
		startGame("r2cg4/9/e1h5h/s6H1/2S2C3/S8/1C4s1r/E4AH2/2s1A4/R2G5", false);
		assertMove("i3-i0", false, true);
		assertGameState("r2cg4/9/e1h5h/s6H1/2S2C3/S8/1C4s2/E4AH2/2s1A4/R2G4r", true, false, false);
	}
	
	@Test
	public void horseCheckGeneralMove() {
		startGame("2rcgh3/9/e7h/s6H1/1S7/S4C3/1C4s2/E4AH2/2s6/R2G1A2r", true);
		assertMove("h6-f7", true, true);
		assertGameState("2rcgh3/9/e4H2h/s8/1S7/S4C3/1C4s2/E4AH2/2s6/R2G1A2r", false, false, false);
	}
	
	@Test
	public void soldierCheck() {
		startGame("2rcgh3/7H1/e7h/sS7/9/S4C3/1C4s2/E4AH2/2s6/R2G1A2r", false);
		assertMove("c1-d1", false, true);
		assertGameState("2rcgh3/7H1/e7h/sS7/9/S4C3/1C4s2/E4AH2/3s5/R2G1A2r", true, false, false);
	}
	
	@Test
	public void horseCheckButCanBeKilled() {
		startGame("2rcgh3/9/e3H3h/sS3H3/9/S4C3/1C4s2/E4A3/3G5/R4A2r", true);
		assertMove("e7-c8", true, true);
		assertGameState("2rcgh3/2H6/e7h/sS3H3/9/S4C3/1C4s2/E4A3/3G5/R4A2r", false, false, false);
	}
	
	@Test
	public void rookCheckX() {
		startGame("3cgh3/2r6/e7h/sS3H3/9/S4C3/1C4s2/E4A3/3G5/R4A2r", true);
		assertMove("a0-e0", true, true);
		assertGameState("3cgh3/2r6/e7h/sS3H3/9/S4C3/1C4s2/E4A3/3G5/4RA2r", false, false, false);
	}
	
	@Test
	public void elephantCannotJump() {
		startGame("4gh3/1H1cr4/e8/s2S2h2/9/S5C2/3C2s2/E4A3/3G5/4RA2r", false);
		assertMove("a7-c9", false, false);
		assertGameState("4gh3/1H1cr4/e8/s2S2h2/9/S5C2/3C2s2/E4A3/3G5/4RA2r", false, false, false);
	}
	
	@Test
	public void horseCheckButBlockedXPlus() {
		startGame("3cgh3/4r4/e8/s2S1Hh2/9/S5C2/3C2s2/E4A3/3G5/4RA2r", true);
		assertMove("f6-d7", true, true);
		assertGameState("3cgh3/4r4/e2H5/s2S2h2/9/S5C2/3C2s2/E4A3/3G5/4RA2r", false, false, false);
	}
	
	@Test
	public void h2() {
		startGame("rheagaehr/4c4/8H/sc6s/2s1s4/6S2/S1S1S3S/1C5C1/9/RHEAGAE1R", true);
		assertMove("i7-g8", true, true);
		assertGameState("rheagaehr/4c1H2/9/sc6s/2s1s4/6S2/S1S1S3S/1C5C1/9/RHEAGAE1R", false, false, false);
	}
	
	@Test
	public void h3() {
		startGame("r1eagaehr/9/1c5c1/s3s1s1s/2s1S4/SC7/6S1S/h8/4C4/RHEAGAEHR", false);
		assertMove("a2-c1", false, true);
		assertGameState("r1eagaehr/9/1c5c1/s3s1s1s/2s1S4/SC7/6S1S/9/2h1C4/RHEAGAEHR", true, false, false);
	}
	
	@Test
	public void h4() {
		startGame("r1eg1aehr/4a4/1c2c4/s5s1s/4S4/1s4S1S/Sh7/1C5C1/4A4/R1EHGAEHR", false);
		assertMove("b3-d2", false, true);
		assertGameState("r1eg1aehr/4a4/1c2c4/s5s1s/4S4/1s4S1S/S8/1C1h3C1/4A4/R1EHGAEHR", true, true, false);
	}
	
	@Test
	public void cannonCheckMateRed() {
		startGame("rhe1gaehr/4a4/1c5c1/s1s3s1s/6S2/2S6/S3s3S/9/3CGC3/RHEA1AEHR", false);
		assertMove("h7-e7", false, true);
		assertGameState("rhe1gaehr/4a4/1c2c4/s1s3s1s/6S2/2S6/S3s3S/9/3CGC3/RHEA1AEHR", true, true, false);
	}
	
	@Test
	public void cannonCheckMateBlack() {
		startGame("rhea1aehr/3cgc3/9/s1s3s1s/2S1S4/9/S5S1S/1C5C1/4A4/RHEAG1EHR", true);
		assertMove("h2-e2", true, true);
		assertGameState("rhea1aehr/3cgc3/9/s1s3s1s/2S1S4/9/S5S1S/1C2C4/4A4/RHEAG1EHR", false, true, true);
	}
	
	@Test
	public void rookCheckMateRed() {
		startGame("1heagaehr/4r4/1c5c1/s7s/4S1s2/2s6/S1S3S1S/9/3CGC3/RHEA1AEHR", false);
		assertMove("e8-e5", false, true);
		assertGameState("1heagaehr/9/1c5c1/s7s/4r1s2/2s6/S1S3S1S/9/3CGC3/RHEA1AEHR", true, true, false);
	}
	
	@Test
	public void checkStartRed() {
		startGame("rheagaehr/9/1c2c4/s1s3s1s/4S4/9/S1S3S1S/1C5C1/9/RHEAGAEHR", true);
		assertMove("f0-e1", true, true);
		assertGameState("rheagaehr/9/1c2c4/s1s3s1s/4S4/9/S1S3S1S/1C5C1/4A4/RHEAG1EHR", false, false, false);
	}
	
	@Test
	public void checkStartBlack() {
		startGame("rheagaehr/9/1cc6/s1s1R4/4S1s2/9/S1S3S2/1C5C1/4A4/RHEAG1EH1", false);
		assertMove("d9-e8", false, true);
		assertGameState("rhe1gaehr/4a4/1cc6/s1s1R4/4S1s2/9/S1S3S2/1C5C1/4A4/RHEAG1EH1", true, false, false);
	}
}
