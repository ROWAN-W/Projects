package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;

public class DBParser {
    //Keyword DROP may be classified as command type
    //Condition Op AND may be classified as Keyword
    DBTokenizer tokenizer;
    ArrayList<ArrayList<String>> tokenPairs;
    ArrayList<String> cleanCommand;
    DBCmd Cmd;
    String query;
    int index=0;

    public DBParser(String Query){
        tokenizer=new DBTokenizer(Query);
        query= Query;
    }

    public DBCmd parse() throws DBException{
        tokenPairs=tokenizer.Tokenize();
        emptyQuery();
        getCleanCommand();
        tokenPairs.size();
        isSemiColon(tokenPairs.size()-1);
        isCommandType(0);
        switch (tokenPairs.get(0).get(1)){
                case "USE":
                   parseUSECmd();
                   break;
                case "SELECT":
                    parseSELECTCmd();
                    break;
                case "JOIN":
                    parseJOINCmd();
                    break;
                case "CREATE":
                    parseCREATECmd();
                    break;
                case "INSERT":
                    parseINSERTCmd();
                    break;
                case "UPDATE":
                    parseUPDATECmd();
                    break;
                case "DROP":
                    parseDROPCmd();
                    break;
                case "ALTER":
                    parseALTERCmd();
                    break;
                case "DELETE":
                   parseDELETECmd();
                   break;
                default:
                    throw new DBException("invalid Command Type");
            }
        return Cmd;
    }

    public void emptyQuery()throws DBException{
        if(tokenPairs==null||tokenPairs.size()==0){
            throw new DBException("empty Query");
        }
    }
    public void getCleanCommand(){
        cleanCommand=new ArrayList<>();
        for(ArrayList<String> tokenPair :tokenPairs){
            cleanCommand.add(cleanCommand.size(),tokenPair.get(1));
        }
    }

    public void isSemiColon(int index) throws DBException{
        if(!cleanCommand.get(index).equals(";"))
        {   throw new DBException("Error during Parsing: missing Semicolon");
        }
    }

    public void isCommandType(int index) throws DBException{
        if(!tokenPairs.get(index).get(0).equals(String.valueOf(TokenTypes.CommandType))){
            throw new DBException("invalid Command Type");
        }
    }
    public void isValueType(int index) throws DBException{
        String type=tokenPairs.get(index).get(0);
        if(!type.equals("String")&&!type.equals("BoolValue")&&!type.equals("Int")&&!type.equals("Float")&&!type.equals("NULL")){
            throw new DBException("invalid token type at "+ tokenPairs.get(index).get(0)+", "+tokenPairs.get(index).get(1));
        }
    }

    public void checkStringValue(int index,String expected) throws DBException{
        if(!cleanCommand.get(index).equals(expected)){
            throw new DBException("invalid syntax at " + cleanCommand.get(index));
        }
    }

    public void checkTokenType(int index,String type) throws DBException
    {
        if(!tokenPairs.get(index).get(0).equals(type)){
            throw new DBException("invalid token type at"+tokenPairs.get(index).get(0)+", "+tokenPairs.get(index).get(0));
        }
    }

    public void checkQueryLength(int expected) throws DBException{
        if(!(cleanCommand.size()==expected)){
            throw new DBException("invalid length of query");
        }
    }

    public void checkMinLength(int expected) throws DBException{
        if(cleanCommand.size()<expected){
            throw new DBException("query too short");
        }
    }

    public  void  isInQuery(String s)throws  DBException{
        if(!cleanCommand.contains(s)){
            throw new DBException("missing "+s);
        }
    }



    public void parseDELETECmd() throws DBException{
        Cmd=new DeleteCMD();
        checkMinLength(8);
        checkStringValue(1,"FROM");
        checkTokenType(2,"ID");
        ArrayList<String> tableNames=new ArrayList<>();
        tableNames.add(cleanCommand.get(2));
        Cmd.setTableNames(tableNames);
        checkStringValue(3,"WHERE");
        parseCondition(4,cleanCommand.size()-2);
    }

    public void parseALTERCmd() throws DBException{
        Cmd=new AlterCMD();
        checkQueryLength(6);
        checkStringValue(1,"TABLE");
        checkTokenType(2,"ID");
        ArrayList<String> tableNames=new ArrayList<>();
        tableNames.add(cleanCommand.get(2));
        Cmd.setTableNames(tableNames);
        if(!cleanCommand.get(3).equals("ADD")&&!cleanCommand.get(3).equals("DROP")){
            throw new DBException("invalid ALTER command");
        }
        Cmd.setCommandType(cleanCommand.get(3));
        checkTokenType(4,"ID");
        ArrayList<String> colNames=new ArrayList<>();
        colNames.add(cleanCommand.get(4));
        Cmd.setColNames(colNames);
    }


    public void parseDROPCmd() throws DBException{
        Cmd=new DropCMD();
        checkQueryLength(4);
        checkTokenType(2,"ID");
        if(cleanCommand.get(1).equals("TABLE")){
            Cmd.setCommandType("TABLE");
              ArrayList<String> table=new ArrayList<>();
              table.add(cleanCommand.get(2));
              Cmd.setTableNames(table);
              return;
        }
        else if(cleanCommand.get(1).equals("DATABASE")){
            Cmd.setCommandType("DATABASE");
            Cmd.setDBName(cleanCommand.get(2));
            return;
        }
        throw new DBException("invalid DROP command");
    }


    public void parseINSERTCmd() throws  DBException{
        Cmd=new InsertCMD();
        checkMinLength(8);
        checkStringValue(1,"INTO");
        checkStringValue(3,"VALUES");
        checkTokenType(2,"ID");
        ArrayList<String> tableNames=new ArrayList<>();
        tableNames.add(cleanCommand.get(2));
        Cmd.setTableNames(tableNames);
        parseValueList(4,cleanCommand.size()-2);

    }

    public void parseValueList(int start, int end)throws DBException{
        checkStringValue(start,"(");
        checkStringValue(end,")");
        ArrayList<String> valueList=new ArrayList<>();
        for(int i=start+1;i<end;i+=2){
            if (i != end-1) {
                checkStringValue(i + 1, ",");
            }
            isValueType(i);
            valueList.add(tokenPairs.get(i).get(1));
        }
        Cmd.setValues(valueList);

    }

    public void parseUPDATECmd()throws DBException{
        Cmd=new UpdateCMD();
        checkMinLength(11);
        checkTokenType(1,"ID");
        ArrayList<String> table=new ArrayList<>();
        table.add(cleanCommand.get(1));
        Cmd.setTableNames(table);

        checkStringValue(2,"SET");
        isInQuery("WHERE");
        int WHERE=cleanCommand.indexOf("WHERE");
        NameValueList(3,WHERE-1);
        if(cleanCommand.size()-WHERE<5){
            throw new DBException("invalid condition clause");
        }
        parseCondition(WHERE+1,cleanCommand.size()-2);
    }

    public void NameValueList(int start, int end) throws DBException
    {
          ArrayList<String> attributes=new ArrayList<>();
          ArrayList<String> values=new ArrayList<>();
          for(int i=start;i<=end;i=i+4){
              checkTokenType(i,"ID");
              checkStringValue(i+1,"=");
              isValueType(i+2);
              if(i+2!=end){
                  checkStringValue(i+3,",");
              }
              attributes.add(cleanCommand.get(i));
              values.add(cleanCommand.get(i+2));
          }
          Cmd.setColNames(attributes);
          Cmd.setValues(values);
    }




    public void parseCREATECmd() throws DBException{
        Cmd=new CreateCMD();
        checkMinLength(4);
        if(cleanCommand.get(1).equals("DATABASE")){
           CreateDatabase();
           return;
        }
        if(cleanCommand.get(1).equals("TABLE")){
             CreateTable();
             return;
        }
        throw new DBException("invalid CREATE command");

    }
    public void CreateDatabase() throws DBException{
        checkQueryLength(4);
        checkTokenType(2,"ID");
        Cmd.setDBName(cleanCommand.get(2));
        Cmd.setCommandType("DATABASE");
    }


    public void CreateTable() throws DBException{
        Cmd.setCommandType("TABLE");
        checkTokenType(2,"ID");
        ArrayList<String> tableName=new ArrayList<>();
        tableName.add(cleanCommand.get(2));
        Cmd.setTableNames(tableName);
          if(cleanCommand.size()==4){
              ArrayList<String> colNames=new ArrayList<>();
              colNames.add("id");
              Cmd.setColNames(colNames);
              return;
          }
          else{
              checkMinLength(7);
              checkStringValue(3,"(");
              checkStringValue(cleanCommand.size()-2,")");
              parseAttributeList(4,cleanCommand.size()-3);
          }
    }

    public void parseJOINCmd() throws DBException{
        Cmd=new JoinCMD();
        validateJOINCmd();
        ArrayList<String> tables=new ArrayList<>();
        tables.add(cleanCommand.get(1));
        tables.add(cleanCommand.get(3));
        Cmd.setTableNames(tables);
        ArrayList<String> attributes=new ArrayList<>();
        attributes.add(cleanCommand.get(5));
        attributes.add(cleanCommand.get(7));
        Cmd.setColNames(attributes);
    }

    public void validateJOINCmd() throws  DBException{

        checkQueryLength(9);
        checkTokenType(1,"ID");
        checkTokenType(3,"ID");
       checkTokenType(5,"ID");
       checkTokenType(7,"ID");
       checkStringValue(2,"AND");
       checkStringValue(6,"AND");
       checkStringValue(4,"ON");
    }

    public void parseUSECmd() throws DBException
    {   Cmd=new UseCMD();
        checkQueryLength(3);
        Cmd.setCommandType("USE");
        checkTokenType(1,"ID");
        Cmd.setDBName(tokenPairs.get(1).get(1));
        return;
    }

    public void parseSELECTCmd() throws DBException
    {   Cmd=new SelectCMD();
        isInQuery("FROM");
        checkMinLength(5);
        int indexOfFROM=cleanCommand.indexOf("FROM");
        handleSelectList(indexOfFROM);
        checkTokenType(indexOfFROM+1,"ID");
        ArrayList<String> tableNames=new ArrayList<>();
        tableNames.add(0,tokenPairs.get(indexOfFROM+1).get(1));
        Cmd.setTableNames(tableNames);
        handleSelectCondition(indexOfFROM);
    }

    public void handleSelectList(int indexOfFROM)throws DBException{
        if(indexOfFROM>2){
            parseAttributeList(1,indexOfFROM-1);
        }
        else{
            if(cleanCommand.get(1)=="*"){
                ArrayList<String> colNames=new ArrayList<>(Arrays.asList("*"));
                Cmd.setColNames(colNames);
            }
            else if(tokenPairs.get(1).get(0)=="ID"){
                ArrayList<String> colNames=new ArrayList<>(Arrays.asList(tokenPairs.get(1).get(1)));
                Cmd.setColNames(colNames);
            }
            else{
                throw new DBException("invalid SELECT command");
            }
        }
    }

    public void handleSelectCondition(int indexOfFROM) throws DBException{
        if(cleanCommand.get(indexOfFROM+2)==";"&&cleanCommand.size()==indexOfFROM+3){
            return;
        }
        else if(cleanCommand.get(indexOfFROM+2)=="WHERE"&&cleanCommand.size()>=indexOfFROM+6){
            parseCondition(indexOfFROM+3,tokenPairs.size()-2);
            return;
        }
        else {
            throw new DBException("invalid SELECT command");
        }
    }

    public void parseAttributeList(int start, int end)throws DBException{

          ArrayList<String> attributeList=new ArrayList<>();
           for(int i=start;i<=end;i+=2){
               if (i != end) {
                   checkStringValue(i + 1, ",");
               }
               checkTokenType(i,"ID");
               attributeList.add(tokenPairs.get(i).get(1));
           }
           Cmd.setColNames(attributeList);
    }

    public void parseCondition(int startIndex,int endIndex) throws DBException{
       ConditionNode rootNode=new ConditionNode();
       ConditionParser conditionParser=new ConditionParser(tokenPairs,cleanCommand);
       ConditionNode StartNode=conditionParser.fillBinaryTree(startIndex,endIndex,rootNode);
       Cmd.setCondition(StartNode);
    }



}
