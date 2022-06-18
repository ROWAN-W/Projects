package edu.uob;

import javax.management.openmbean.OpenDataException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBTokenizer {
    String query;
    ArrayList<ArrayList<String>> tokenPairs;
    String[] Operator={"=="  , ">=" ,"<=" , "!=", ">" , "<"};

    enum KeyWords{
        DATABASE,
        TABLE,
        INTO,
        FROM,
        WHERE,
        SET,
        AND,
        ON,
        ADD,
        DROP
    }

    enum ConditionOperators {
        AND,
        OR
    }

    enum BoolValues{
        TRUE,
        FALSE
    }

    public DBTokenizer(String Query){

        tokenPairs=new ArrayList<>();
        query=Query;

    }

    public ArrayList<ArrayList<String>> Tokenize() throws DBException{

        while(!query.isEmpty()){
            if(!checkTokenType()){
                throw new DBException("invalid token around "+query);
            }
        }
        return tokenPairs;
    }

    public void removeSpace(){
        if(query.matches("^\s*.*")){
            query=query.replaceFirst("^\s*","");
        }
    }

    public boolean checkTokenType() {
        removeSpace();
        if(isString()){return true;}
        else if(isLeftQuote()){return true;}
        else if(isRightQuote()){return true;}
        else if(isComma()){return true;}
        else if(isCommandType()){return true;}
        else if(isKeyWord()){return true;}
        else if(isConditionOperator()){return true;}
        else if(isOperator()){return true;}
        else if (isLiteral()) {return true;}
        else return endOfQuery();

    }
    public boolean isLiteral(){
        if(isEqualSignal()){return true;}
        else if(isBoolValue()){return true;}
        else if(isWildChar()){return true;}
        else if(isInt()){return true;}
        else if(isFloat()){return true;}
        else if(isNULL()){return true;}
        else return isID();
    }

    private boolean matchPattern(Pattern pattern){
        Matcher matcher= pattern.matcher(query);
        if(matcher.find()){
            query = query.replaceFirst(matcher.group(0),"");
            return true;
        }
        return false;
    }

    private void addToTokenList(String tokenType, String token ){
        ArrayList<String> tokenPair= new ArrayList<>(Arrays.asList(tokenType,token));
        tokenPairs.add(tokenPairs.size(),tokenPair);
    }

    public boolean isString() {
        Pattern pattern= Pattern.compile("^'[^0-9']*'");
        Matcher matcher=pattern.matcher(query);
        if(matcher.find()){
            addToTokenList(String.valueOf(TokenTypes.String),deQuote(matcher.group(0)));
            query=query.replaceFirst(matcher.group(0)+"\s*","");
            return true;
        }
        return false;
    }
    public String deQuote(String s){
        if(s.matches("^'.*'$")){
            if(s.length()<3){
                return "";
            }
            else{
                return s.substring(1,s.length()-1);
            }
        }
        return s;
    }

    public boolean isLeftQuote(){
        Pattern pattern= Pattern.compile("^"+Pattern.quote("(")+"\s*");
        Matcher matcher=pattern.matcher(query);
        if(matcher.find()){
            addToTokenList(String.valueOf(TokenTypes.LeftQuote),"(");
            query=query.replaceFirst("^"+Pattern.quote("(")+"\s*","");
            return true;
        }
        return  false;
    }

    public boolean isRightQuote(){

        Pattern pattern= Pattern.compile("^"+Pattern.quote(")")+"\s*");
        Matcher matcher=pattern.matcher(query);
        if(matcher.find()){
            addToTokenList(String.valueOf(TokenTypes.RightQuote),")");
            query=query.replaceFirst("^"+Pattern.quote(")")+"\s*","");
            return true;
        }
        return  false;
    }

    public boolean isComma(){

        Pattern pattern= Pattern.compile("^,\s*");
        if(matchPattern(pattern)){
            addToTokenList(String.valueOf(TokenTypes.Comma),",");
            return true;
        }
        return  false;
    }


    public boolean isCommandType(){
        for (CommandTypes obj : CommandTypes.values()) {
            Pattern pattern=Pattern.compile("^"+obj+"\s*",Pattern.CASE_INSENSITIVE);
            if(matchPattern(pattern)){
                addToTokenList(String.valueOf(TokenTypes.CommandType),obj.name());
                return true;
            }
        }
        return false;
    }

    public boolean isKeyWord(){
        for (KeyWords obj : KeyWords.values()) {
            Pattern pattern=Pattern.compile("^"+obj+"\s*",Pattern.CASE_INSENSITIVE);
            if(matchPattern(pattern)){
                addToTokenList(String.valueOf(TokenTypes.Keyword),obj.name());
                return true;
            }
        }
        Pattern pattern=Pattern.compile("^VALUES\s*",Pattern.CASE_INSENSITIVE);
        if(matchPattern(pattern)){
            addToTokenList(String.valueOf(TokenTypes.Keyword),"VALUES");
            return true;
        }

        return false;
    }

    public boolean isConditionOperator(){

        for (ConditionOperators obj : ConditionOperators.values()) {
            Pattern pattern=Pattern.compile("^"+obj+"\s*",Pattern.CASE_INSENSITIVE);
            if(matchPattern(pattern)){
                addToTokenList(String.valueOf(TokenTypes.ConditionOperator),obj.name());
                return true;
            }
        }
        return false;
    }

    public boolean isOperator(){
        for(String op :Operator){
            Pattern pattern=Pattern.compile("^"+op+"\s*",Pattern.CASE_INSENSITIVE);
            if(matchPattern(pattern)){
                addToTokenList(String.valueOf(TokenTypes.Operator),op);
                return true;
            }
        }
        Pattern pattern=Pattern.compile("^LIKE\s*",Pattern.CASE_INSENSITIVE);
        if(matchPattern(pattern)){
            addToTokenList(String.valueOf(TokenTypes.Operator),"LIKE");
            return true;
        }
        return false;
    }


    public boolean isBoolValue(){
        for (BoolValues obj :BoolValues.values()) {
            Pattern pattern=Pattern.compile("^"+obj+"\s*",Pattern.CASE_INSENSITIVE);
            if(matchPattern(pattern)){
                addToTokenList(String.valueOf(TokenTypes.BoolValue),obj.name());
                return true;
            }
        }
        return false;
    }

    public boolean isWildChar(){
        Pattern pattern= Pattern.compile("^"+Pattern.quote("*")+"\s*");
        Matcher matcher=pattern.matcher(query);
        if(matcher.find()){
            addToTokenList(String.valueOf(TokenTypes.WildChar),"*");
            query=query.replaceFirst("^"+Pattern.quote("*")+"\s*","");
            return true;
        }
        return false;
    }

    public boolean isEqualSignal(){
        Pattern pattern= Pattern.compile("^=\s*");
        if(matchPattern(pattern)){
            addToTokenList(String.valueOf(TokenTypes.EqualSignal),"=");
            return true;
        }
        return false;
    }

    public boolean isInt(){
        Pattern pattern= Pattern.compile("^[+-]?[0-9]+");
        Matcher matcher=pattern.matcher(query);
        if(matcher.find()){

            addToTokenList(String.valueOf(TokenTypes.Int),matcher.group(0));
            query=query.replaceFirst(matcher.group(0)+"\s*","");
            return true;
        }
        return false;
    }
    public boolean isFloat(){
        Pattern pattern= Pattern.compile("^[+-]?[0-9]+"+Pattern.quote(".")+"[0-9]+");
        Matcher matcher=pattern.matcher(query);
        if(matcher.find()){
            if(matcher.group(0).matches(Pattern.quote("+"))){
                matcher.group(0).replaceFirst(Pattern.quote("+"),"");
            }
            addToTokenList(String.valueOf(TokenTypes.Float),matcher.group(0));
            query=query.replaceFirst(matcher.group(0)+"\s*","");
            return true;
        }
        return false;
    }
    public boolean isNULL(){
        Pattern pattern= Pattern.compile("^NULL\s*",Pattern.CASE_INSENSITIVE);
        if(matchPattern(pattern)){
            addToTokenList(String.valueOf(TokenTypes.NULL),"NULL");
            return true;
        }
        return false;
    }

    public boolean isID(){
        Pattern pattern= Pattern.compile("^[a-zA-Z0-9]+");
        Matcher matcher=pattern.matcher(query);
        if(matcher.find()){
            addToTokenList(String.valueOf(TokenTypes.ID),matcher.group(0));
            query=query.replaceFirst(matcher.group(0)+"\s*","");
            return true;
        }
        return false;
    }

    public boolean endOfQuery(){
        if(query.matches(";\s*")){
            addToTokenList(String.valueOf(TokenTypes.Semicolon),";");
            query="";
            return true;
        }
        return false;
    }

    public void printTokenPairs(){
        for (int i=0;i<tokenPairs.size();i++){
            System.out.println(i+tokenPairs.get(i).get(0)+"  "+tokenPairs.get(i).get(1)+"!");
        }
    }
}
