package edu.uob;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

// PLEASE READ:
// The tests in this file will fail by default for a template skeleton, your job is to pass them
// and maybe write some more, read up on how to write tests at
// https://junit.org/junit5/docs/current/user-guide/#writing-tests
final class DBTests {

  private DBServer server;

  // we make a new server for every @Test (i.e. this method runs before every @Test test case)
  @BeforeEach
  void setup(@TempDir File dbDir) {
    // Notice the @TempDir annotation, this instructs JUnit to create a new temp directory somewhere
    // and proceeds to *delete* that directory when the test finishes.
    // You can read the specifics of this at
    // https://junit.org/junit5/docs/5.4.2/api/org/junit/jupiter/api/io/TempDir.html

    // If you want to inspect the content of the directory during/after a test run for debugging,
    // simply replace `dbDir` here with your own File instance that points to somewhere you know.
    // IMPORTANT: If you do this, make sure you rerun the tests using `dbDir` again to make sure it
    // still works and keep it that way for the submission.

    server = new DBServer(dbDir);
  }

  // Here's a basic test for spawning a new server and sending an invalid command,
  // the spec dictates that the server respond with something that starts with `[ERROR]`
  @Test
  void testInvalidCommandIsAnError() {
    assertTrue(server.handleCommand("foo").startsWith("[ERROR]"));
  }
  // Add more unit tests or integration tests here.
  // Unit tests would test individual methods or classes whereas integration tests are geared
  // towards a specific use-case (i.e. creating a table and inserting rows and asserting whether the
  // rows are actually inserted)

  @Test
  //unit test
  void testTokenizer() throws DBException {
    DBTokenizer tokenizer1=new DBTokenizer("select * from marks where ((name=='Steve')AND(pass==TRUE))OR(name=='Dave');");
    ArrayList<ArrayList<String>> tokenPairs1=tokenizer1.Tokenize();
    assertEquals("WildChar",tokenPairs1.get(1).get(0));
    assertEquals("LeftQuote",tokenPairs1.get(6).get(0));
    assertEquals("Keyword",tokenPairs1.get(4).get(0));
    assertEquals("String",tokenPairs1.get(9).get(0));

    DBTokenizer tokenizer2=new DBTokenizer("ALTER TABLE marks ADD newCol ;");
    ArrayList<ArrayList<String>> tokenPairs2=tokenizer2.Tokenize();
    assertEquals("ID",tokenPairs2.get(2).get(0));

    //invalid token
    DBTokenizer tokenizer3=new DBTokenizer("%$&#");
    assertThrows(DBException.class, ()->tokenizer3.Tokenize());

  }


  @Test
  //unit test
  void testParser() throws DBException{
    DBParser parser1=new DBParser("    DROP DATABASE markbook;");
    DBCmd Cmd1=parser1.parse();
    assertEquals("markbook",Cmd1.DBName);

    //invalid query
    DBParser parser2=new DBParser("DROP DATABASE markbook");
    assertThrows(DBException.class, ()->parser2.parse());

    DBParser parser3=new DBParser("DROP DATABASE");
    assertThrows(DBException.class, ()->parser3.parse());

    DBParser parser4=new DBParser("select * from marks where (name=='Steve')AND(pass==TRUE))OR(name=='Dave');");
    assertThrows(DBException.class, ()->parser4.parse());

    DBParser parser5=new DBParser("");
    assertThrows(DBException.class, ()->parser5.parse());

    DBParser parser6=new DBParser("ksugveubp0w9345");
    assertThrows(DBException.class, ()->parser6.parse());
  }

  @Test
  //integration test
  void testQuery1 ()throws DBException{
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE name != 'Dave';").contains("Steve"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE pass == TRUE;").contains("Dave"));

    //nested loop
    assertTrue(server.handleCommand("select * from marks where ((name=='Steve')AND(pass==TRUE))OR(name=='Dave');").contains("Dave"));
    assertTrue(server.handleCommand("select * from marks where ((name=='Steve')AND(pass==TRUE))OR(name=='Dave');").contains("Steve"));
    assertTrue(!server.handleCommand("select * from marks where ((name=='Steve')AND(pass==FALSE))OR(name=='Dave');").contains("Steve"));

    assertTrue(server.handleCommand("CREATE TABLE coursework (id, task, grade);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO coursework VALUES ( 1, 'OXO', 3);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO coursework VALUES ( 2, 'DB', 1);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO coursework VALUES ( 3, 'OXO', 4);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO coursework VALUES ( 4, 'STAG' , 2);").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM coursework;").contains("STAG"));
    assertTrue(server.handleCommand("JOIN coursework AND marks ON grade AND id;").startsWith("[OK]"));
    assertTrue(server.handleCommand("UPDATE marks SET mark = 38 WHERE name == 'Clive';").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE name == 'Clive';").contains("FALSE"));
    assertTrue(server.handleCommand("DELETE FROM marks WHERE name == 'Dave';").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE (pass == FALSE) AND (mark > 35);").contains("Clive"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE name LIKE 've';").contains("65"));
    assertTrue(server.handleCommand("SELECT id FROM marks WHERE pass == FALSE;").contains("4"));
    assertTrue(server.handleCommand("SELECT name FROM marks WHERE mark>60;").contains("Steve"));
    assertTrue(server.handleCommand("DELETE FROM marks WHERE mark<40;").startsWith("[OK]"));
    assertTrue(!server.handleCommand("SELECT * FROM marks;").contains("Dave"));

    //error query
    assertTrue(server.handleCommand("SELECT * FROM marks").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("SELECT * FROM crew;").startsWith("[ERROR]"));
    assertTrue(server.handleCommand("SELECT * FROM marks pass == TRUE;").startsWith("[ERROR]"));


  }

@Test
  void testQuery2()throws DBException{
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE name != 'Dave';").contains("Steve"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE pass == TRUE;").contains("Dave"));


  }

  @Test
  void testQuery3()throws DBException{
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE name != 'Dave';").contains("Steve"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE pass == TRUE;").contains("Dave"));

    assertTrue(server.handleCommand("ALTER TABLE marks ADD newCol ;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").contains("newCol"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").contains("NULL"));

    assertTrue(server.handleCommand("ALTER TABLE marks DROP newCol ;").startsWith("[OK]"));
    assertTrue(!server.handleCommand("SELECT * FROM marks;").contains("newCol"));
    assertTrue(!server.handleCommand("SELECT * FROM marks;").contains("NULL"));
  }


  @Test
  void testQuery4()throws DBException{
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE name != 'Dave';").contains("Steve"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE pass == TRUE;").contains("Dave"));

    assertTrue(server.handleCommand("DROP TABLE marks;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").contains("[ERROR]"));

  }


  @Test
  void testQuery5()throws DBException{
    assertTrue(server.handleCommand("CREATE DATABASE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("USE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("CREATE TABLE marks (name, mark, pass);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Steve', 65, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Dave', 55, TRUE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Bob', 35, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("INSERT INTO marks VALUES ('Clive', 20, FALSE);").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE name != 'Dave';").contains("Steve"));
    assertTrue(server.handleCommand("SELECT * FROM marks WHERE pass == TRUE;").contains("Dave"));

    assertTrue(server.handleCommand("DROP DATABASE markbook;").startsWith("[OK]"));
    assertTrue(server.handleCommand("SELECT * FROM marks;").contains("[ERROR]"));

  }

}
