package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests

final public class ExtendedCommandTests {
    private GameServer server;

    // Make a new server for every @Test (i.e. this method runs before every @Test test case)
    @BeforeEach
    void setup() {
        File entitiesFile = Paths.get("config/extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config/extended-actions.xml").toAbsolutePath().toFile();
        server = new GameServer(entitiesFile, actionsFile);
    }

    // Test to spawn a new server and send a simple "look" command
    @Test
    void testLookingAroundStartLocation() {
        String response = server.handleCommand("look").toLowerCase();
        assertTrue(response.contains("log cabin"), "Did not see description of room in response to look");
        assertTrue(response.contains("magic potion"), "Did not see description of artifacts in response to look");
        assertTrue(response.contains("razor sharp axe"), "Did not see description of artifacts in response to look");
        assertTrue(response.contains("silver coin"), "Did not see description of artifacts in response to look");
        assertTrue(response.contains("trapdoor"), "Did not see description of furniture in response to look");
    }

    // Add more unit tests or integration tests here.

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

        //more than one possible actions
        response = server.handleCommand("rowan: get coin and axe ").toLowerCase();
        assertTrue(response.contains("no"));

        response = server.handleCommand("rowan: get  axe ").toLowerCase();
        assertTrue(response.contains("to your inventory"));
        //check the axe is in my inventory
        response = server.handleCommand("rowan: inv ").toLowerCase();
        assertTrue(response.contains("axe"));

        response = server.handleCommand("rowan: get coin ").toLowerCase();
        assertTrue(response.contains("to your inventory"));

        response = server.handleCommand("rowan: goto forest ").toLowerCase();
        assertTrue(response.contains("moved"));

        response = server.handleCommand("rowan: chop tree ").toLowerCase();
        assertTrue(response.contains("cut down the tree with the axe"));

        response = server.handleCommand("rowan: get log ").toLowerCase();
        assertTrue(response.contains("to your inventory"));

        response = server.handleCommand("rowan: goto cabin ").toLowerCase();
        assertTrue(response.contains("moved"));

        response = server.handleCommand("rowan: goto cellar ").toLowerCase();
        assertTrue(response.contains("moved"));

        response = server.handleCommand("rowan: pay the elf ").toLowerCase();
        assertTrue(response.contains("pay the elf your silver coin and he produces a shovel"));

        response = server.handleCommand("rowan: get shovel ").toLowerCase();
        assertTrue(response.contains("to your inventory"));

        response = server.handleCommand("rowan: goto cabin ").toLowerCase();
        assertTrue(response.contains("moved"));

        response = server.handleCommand("rowan: goto forest ").toLowerCase();
        assertTrue(response.contains("moved"));

        response = server.handleCommand("rowan: goto riverbank ").toLowerCase();
        assertTrue(response.contains("moved"));

        response = server.handleCommand("rowan: bridge with log").toLowerCase();
        assertTrue(response.contains("bridge the river with the log and can now reach the other side"));

        response = server.handleCommand("rowan: goto clearing ").toLowerCase();
        assertTrue(response.contains("moved"));

        response = server.handleCommand("rowan: dig the ground ").toLowerCase();
        assertTrue(response.contains("dig into the soft ground and unearth a pot of gold"));


        response = server.handleCommand("rowan: goto riverbank ").toLowerCase();
        assertTrue(response.contains("moved"));

        response = server.handleCommand("rowan: blow the horn").toLowerCase();
        assertTrue(response.contains("blow the horn and as if by magic"));


    }


    @Test
    void testPlayerHealth() {
       // check the health

        String response = server.handleCommand("rowan: health").toLowerCase();
        assertTrue(response.contains("3"));

        response = server.handleCommand("rowan:goto forest").toLowerCase();
        assertTrue(response.contains("moved"));

        response = server.handleCommand("rowan: get key").toLowerCase();
        assertTrue(response.contains("to your inventory"));

        response = server.handleCommand("rowan: goto cabin").toLowerCase();
        assertTrue(response.contains("moved"));


        response = server.handleCommand("rowan: unlock the trapdoor ").toLowerCase();
        assertTrue(response.contains(" see steps leading down into a cellar"));

        response = server.handleCommand("rowan: goto cellar").toLowerCase();
        assertTrue(response.contains("moved"));


        response = server.handleCommand("rowan: fight the elf").toLowerCase();
        assertTrue(response.contains("but he fights back and you lose some health"));
// check the health is down by one
        response = server.handleCommand("rowan: health").toLowerCase();
        assertTrue(response.contains("2"));

        response = server.handleCommand("rowan: hit the elf").toLowerCase();
        assertTrue(response.contains("but he fights back and you lose some health"));
// check the health is down by one
        response = server.handleCommand("rowan: health").toLowerCase();
        assertTrue(response.contains("1"));

        response = server.handleCommand("rowan: attack the elf").toLowerCase();
        // health level is zero now, player state is reseted
        assertTrue(response.contains("you died and lost all of your items"));
        // check the player is in the initial location
        response = server.handleCommand("rowan: look").toLowerCase();
        assertTrue(response.contains("log cabin in the woods"));

        response = server.handleCommand("rowan: get potion").toLowerCase();
        assertTrue(response.contains("to your inventory"));

        response = server.handleCommand("rowan: goto cellar").toLowerCase();
        assertTrue(response.contains("moved"));

        response = server.handleCommand("rowan: hit the elf").toLowerCase();
        assertTrue(response.contains("but he fights back and you lose some health"));
// check the health is down by one
        response = server.handleCommand("rowan: health").toLowerCase();
        assertTrue(response.contains("2"));

        response = server.handleCommand("rowan: drink the potion").toLowerCase();
        assertTrue(response.contains("drink the potion and your health improves"));
        // check the health is up by one
        response = server.handleCommand("rowan: health").toLowerCase();
        assertTrue(response.contains("3"));

    }

    @Test
    void testMultiPlayers(){

        String response = server.handleCommand("simon: look").toLowerCase();
        assertTrue(response.contains("default"));

        response = server.handleCommand("rowan: look").toLowerCase();
        assertTrue(response.contains("simon"));

        response = server.handleCommand("sion: look").toLowerCase();
        assertTrue(response.contains("simon"));

        response = server.handleCommand("sion: look").toLowerCase();
        assertTrue(response.contains("rowan"));


        response = server.handleCommand("rowan:goto forest").toLowerCase();
        assertTrue(response.contains("moved"));

        response = server.handleCommand("rowan: get key").toLowerCase();
        assertTrue(response.contains("to your inventory"));

        response = server.handleCommand("rowan: goto cabin").toLowerCase();
        assertTrue(response.contains("moved"));


        response = server.handleCommand("rowan: unlock the trapdoor ").toLowerCase();
        assertTrue(response.contains(" see steps leading down into a cellar"));


        response = server.handleCommand("sion: look").toLowerCase();
        assertTrue(response.contains("cellar"));

        response = server.handleCommand("simon: look").toLowerCase();
        assertTrue(response.contains("cellar"));




    }



}
