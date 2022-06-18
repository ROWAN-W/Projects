package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Paths;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class BasicCommandTests {

  private GameServer server;

  // Make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup() {
      File entitiesFile = Paths.get("config/basic-entities.dot").toAbsolutePath().toFile();
      File actionsFile = Paths.get("config/basic-actions.xml").toAbsolutePath().toFile();
      server = new GameServer(entitiesFile, actionsFile);
  }

  // Test to spawn a new server and send a simple "look" command
  @Test
  void testLookingAroundStartLocation() {
    String response = server.handleCommand("player 1: look").toLowerCase();
    assertTrue(response.contains("empty room"), "Did not see description of room in response to look");
    assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
    assertTrue(response.contains("wooden trapdoor"), "Did not see description of furniture in response to look");
  }

  // Add more unit tests or integration tests here.

  @Test
  void testCommandSyntax() {
    //test some illegal commands
    String response = server.handleCommand("rowan: looka").toLowerCase();
    assertTrue(response.contains("no"));

    response = server.handleCommand("rowan: foto forest").toLowerCase();
    assertTrue(response.contains("no"));
    // check I'm still in the cabin
    response = server.handleCommand("rowan: look").toLowerCase();
    assertTrue(response.contains("empty room"));

    response = server.handleCommand("rowan: ").toLowerCase();
    assertTrue(response.contains("no"));

    response = server.handleCommand("rowan: potion").toLowerCase();
    assertTrue(response.contains("no"));
    // check the potion is still in the cabin
    response = server.handleCommand("rowan: look").toLowerCase();
    assertTrue(response.contains("magic potion"));
// test that the order of words doesnt matter
    response = server.handleCommand("rowan: potion get").toLowerCase();
    assertTrue(response.contains("to your inventory"));

  }

  @Test
  void testInvalid() {
    //test some legal but unsuccessful commands

   // two possible actions, neither gets executed
    String response = server.handleCommand("rowan: look and goto forest").toLowerCase();
    assertTrue(response.contains("no"));
    // check I'm still in the cabin
    response = server.handleCommand("rowan: look").toLowerCase();
    assertTrue(response.contains("empty room"));

    //only one valid action: get potion
    response = server.handleCommand("rowan: get potion and goto").toLowerCase();
    assertTrue(response.contains("to your inventory"));
    // check the potion is picked up
    response = server.handleCommand("rowan: look").toLowerCase();
    assertTrue(!response.contains("potion"));
  }


  @Test
  void testGameSpecificActions() {


    String response = server.handleCommand("rowan:goto forest").toLowerCase();
    assertTrue(response.contains("moved"));

    response = server.handleCommand("rowan: get key").toLowerCase();
    assertTrue(response.contains("to your inventory"));
    // check the key is picked up
    response = server.handleCommand("rowan: inv").toLowerCase();
    assertTrue(response.contains("key"));

    response = server.handleCommand("rowan: goto cabin").toLowerCase();
    assertTrue(response.contains("moved"));

    //ambiguous command
    response = server.handleCommand("rowan: unlock ").toLowerCase();
    assertTrue(response.contains("no"));

    response = server.handleCommand("rowan: unlock the trapdoor ").toLowerCase();
    assertTrue(response.contains(" see steps leading down into a cellar"));



  }



}
