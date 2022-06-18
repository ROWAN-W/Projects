package edu.uob;
import edu.uob.OXOMoveException.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class ControllerTests {
  OXOModel model;
  OXOController controller;

  // create your standard 3*3 OXO board (where three of the same symbol in a line wins) with the X
  // and O player
  private static OXOModel createStandardModel() {
    OXOModel model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    return model;
  }

  // we make a new board for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() {
    model = createStandardModel();
    controller = new OXOController(model);
  }

  // here's a basic test for the `controller.handleIncomingCommand` method
  @Test
  void testHandleIncomingCommand() throws OXOMoveException {
    // take note of whose gonna made the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a1");

    // A move has been made for A1 (i.e. the [0,0] cell on the board), let's see if that cell is
    // indeed owned by the player
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0));

    // take note of whose gonna made the second move
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("b1");
    // A move has been made for b1 (i.e. the [1,0] cell on the board), let's see if that cell is
    // indeed owned by the player
    assertEquals(secondMovingPlayer, controller.gameModel.getCellOwner(1, 0));
  }
  @Test
  void testHandleIncomingCommand2() throws OXOMoveException {
    // take note of whose gonna made the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("b1");

    // A move has been made for b1 (i.e. the [1,0] cell on the board), let's see if that cell is
    // indeed owned by the player
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(1, 0));

    // take note of whose gonna made the second move
    OXOPlayer secondMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("c2");
    // A move has been made for c2 (i.e. the [2,1] cell on the board), let's see if that cell is
    // indeed owned by the player
    assertEquals(secondMovingPlayer, controller.gameModel.getCellOwner(2, 1));
  }


  // here's a complete game where we find out if someone won
  @Test
  void testBasicWinWithA1A2A3() throws OXOMoveException {
    // take note of whose gonna made the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("a3");

    // OK, so A1, A2, A3 is a win and that last A3 move is made by the first player (players
    // alternative between moves) let's make an assertion to see whether the first moving player is
    // the winner here
    assertEquals(
        firstMovingPlayer,
        model.getWinner(),
        "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testBasicWinWithA1B1C1() throws OXOMoveException {
    // take note of whose gonna made the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("b1");

    // OK, so A1, B1, C1 is a win and that last C1 move is made by the first player (players
    // alternative between moves) let's make an assertion to see whether the first moving player is
    // the winner here
    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testBasicWinWithA3B2C1() throws OXOMoveException {
    // take note of whose gonna made the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("c1");

    // OK, so A3, B2, C1 is a win and that last C1 move is made by the first player (players
    // alternative between moves) let's make an assertion to see whether the first moving player is
    // the winner here
    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void draw() throws OXOMoveException {

    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("c2");

    assertTrue(model.isGameDrawn());
  }


  @Test
  void testAddRow() throws OXOMoveException {
    model.addRow();

    // take note of whose gonna made the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c1");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("d1");

    // OK, so B1, C1, D1 is a win and that last D1 move is made by the first player (players
    // alternative between moves) let's make an assertion to see whether the first moving player is
    // the winner here
    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void testAddRow2() throws OXOMoveException {
    //Add 10 rows to the board
    model.addRow();
    model.addRow();
    model.addRow();
    model.addRow();
    model.addRow();
    model.addRow();
    model.addRow();
    model.addRow();
    model.addRow();
    model.addRow();

    //The max board size is 9x9, so the row number shouldn't be higher than 9
    assertEquals(9,model.getNumberOfRows());

  }

  @Test
  void testAddCol() throws OXOMoveException {
    model.addColumn();

    // take note of whose gonna made the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("a4");

    // OK, so A2, A3, A4 is a win and that last A4 move is made by the first player (players
    // alternative between moves) let's make an assertion to see whether the first moving player is
    // the winner here
    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }
  @Test
  void testAddColumn2() throws OXOMoveException {
    //Add 10 rows to the board
    model.addColumn();
    model.addColumn();
    model.addColumn();
    model.addColumn();
    model.addColumn();
    model.addColumn();
    model.addColumn();
    model.addColumn();
    model.addColumn();
    model.addColumn();

    //The max board size is 9x9, so the column number shouldn't be higher than 9
    assertEquals(9, model.getNumberOfColumns());
  }
    @Test
   void shrinkSize_draw() throws OXOMoveException{
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b2");
    //After removeRow() and removeColumn(), size of the board is shrunk to 2x2, the game should be drawn
    controller.removeRow();
    controller.removeColumn();
    assertTrue(model.isGameDrawn());
  }

  @Test
  void shrinkSize_draw2() throws OXOMoveException{
    controller.handleIncomingCommand("a1");
    controller.removeColumn();
    controller.removeColumn();
    controller.removeRow();
    controller.removeRow();
    //After removeRow() and removeColumn(), size of the board is shrunk to 1x1, the game should be drawn
    assertTrue(model.isGameDrawn());
  }
@Test
  void shrinkSize_draw3() throws OXOMoveException{
    controller.removeColumn();
    controller.removeColumn();
    controller.removeColumn();
    //After removeColumn(), size of the board is shrunk to size zero, and there is no winner, so it is a draw
    assertTrue(model.isGameDrawn());
    //For a board of size zero, "a1" would be out of range
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("a1"));
  }


  @Test
  void lengthException() {
    //Checks that a InvalidIdentifierLengthException is thrown out
    assertThrows(InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand("aaaaaa1"));
  }

  @Test
  void lengthException2(){
    //Checks that a InvalidIdentifierLengthException is thrown out
    assertThrows(InvalidIdentifierLengthException.class, ()->controller.handleIncomingCommand("1"));
  }

  @Test

  void characterException() {
    //Checks that a InvalidIdentifierCharacterException is thrown out
    assertThrows(InvalidIdentifierCharacterException.class, ()->controller.handleIncomingCommand("a&"));
  }

  @Test
  void characterException2() {
    //Checks that a InvalidIdentifierCharacterException is thrown out
    assertThrows(InvalidIdentifierCharacterException.class, ()->controller.handleIncomingCommand(" 9"));
  }

  @Test
  void characterException3() {
    //Checks that a InvalidIdentifierCharacterException is thrown out
    assertThrows(InvalidIdentifierCharacterException.class, ()->controller.handleIncomingCommand("- "));
  }

  @Test
  void rangeException()throws OXOMoveException{
    //Checks that a OutsideCellRangeException (column 3) is thrown out
    assertThrows(OutsideCellRangeException.class, ()->controller.handleIncomingCommand("a4"));

    //checks that a4 is accessible after addColumn()
    controller.addColumn();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a4");
    assertEquals(firstMovingPlayer,model.getCellOwner(0,3));
  }

  @Test
  void rangeException2()throws OXOMoveException{
    //Checks that a OutsideCellRangeException (row 3) is thrown out
    assertThrows(OutsideCellRangeException.class, ()->controller.handleIncomingCommand("d3"));

    //checks that d3 is accessible after addColumn()
    controller.addRow();
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("d3");
    assertEquals(firstMovingPlayer,model.getCellOwner(3,2));
  }

  @Test
  void cellTaken()throws OXOMoveException{
    //Checks that a CellAlreadyTakenException [0,0] is thrown out
    controller.handleIncomingCommand("a1");
    assertThrows(CellAlreadyTakenException.class, ()->controller.handleIncomingCommand("a1"));
  }

  @Test
  void winThresholdChange() throws OXOMoveException{
    // take note of whose gonna made the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("c3");
    controller.decreaseWinThreshold();
    //After decreasing the win threshold, there should be a winner
    assertEquals(
            firstMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(firstMovingPlayer.getPlayingLetter()));
  }

  @Test
  void winThresholdChange2() throws OXOMoveException{
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("b3");
    controller.decreaseWinThreshold();
    //After decreasing the win threshold, there should be two winners, and the game is drawn
    assertTrue(model.isGameDrawn());
  }

  @Test
  void winThresholdChange3() throws OXOMoveException{
    controller.increaseWinThreshold();
    controller.handleIncomingCommand("a2");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("a1");
    //After increasing the win threshold, there should be no winner here
    assertNull(model.getWinner());
  }

  @Test
  void addPlayer() {
    //Add a new player
    model.addPlayer(new OXOPlayer('-'));
    int num=model.getNumberOfPlayers();
    //Test the number of players
    assertEquals(3,num);
  }
  @Test
  void addPlayer2() {
    //Add several new players
    model.addPlayer(new OXOPlayer('-'));
    model.addPlayer(new OXOPlayer('-'));
    model.addPlayer(new OXOPlayer(' '));
    model.addPlayer(new OXOPlayer('+'));
    model.addPlayer(new OXOPlayer('*'));
    model.addPlayer(new OXOPlayer('$'));
    model.addPlayer(new OXOPlayer('&'));
    int num=model.getNumberOfPlayers();
    //Test the number of players
    assertEquals(9,num);
  }
  @Test
  void addPlayer3() throws OXOMoveException{
    //Add a new user
    model.addPlayer(new OXOPlayer('-'));
    int num=model.getNumberOfPlayers();
    //Test the number of players
    assertEquals(3,num);
    //use the third player
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("a2");
    // take note of the third player
    OXOPlayer thirdMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a3");
    //Checks that [0,2] is taken by the third player
    assertEquals(thirdMovingPlayer, controller.gameModel.getCellOwner(0, 2));
  }
  @Test
  void addPlayer4() throws OXOMoveException{
    //Add a new player
    model.addPlayer(new OXOPlayer('-'));
    //Play the game with three players
    controller.handleIncomingCommand("a1");
    controller.handleIncomingCommand("a2");
    // take note of the third player
    OXOPlayer thirdMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    controller.handleIncomingCommand("a3");
    controller.handleIncomingCommand("b1");
    controller.handleIncomingCommand("b3");
    controller.handleIncomingCommand("b2");
    controller.handleIncomingCommand("c2");
    controller.handleIncomingCommand("c3");
    controller.handleIncomingCommand("c1");
    //Checks that the third player wan the game
    assertEquals(
            thirdMovingPlayer,
            model.getWinner(),
            "Winner was expected to be %s but wasn't".formatted(thirdMovingPlayer.getPlayingLetter()));
  }
}