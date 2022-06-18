package edu.uob;
import java.util.*;
public abstract class DBCmd {
    ConditionNode condition;
    ArrayList<String> colNames;
    ArrayList<String> values;
    ArrayList<String> tableNames;
    String DBName;
    DataBase dataBase;
    String commandType;
    DBServer server;


    public abstract String query(DBServer Server) throws DBException;

    public void setCondition(ConditionNode c){
        condition=c;
    }
    public ConditionNode getCondition(){
        return condition;
    }

    public void setColNames(ArrayList<String> colList){
        colNames=colList;
    }

    public ArrayList<String> getColNames(){
        return colNames;
    }

    public void setValues(ArrayList<String> Values){
        values=Values;
    }

    public ArrayList<String> getValues(){
        return values;
    }

    public void setTableNames(ArrayList<String> TableNames){
        tableNames=TableNames;
    }

    public ArrayList<String> getTableNames(){
        return tableNames;
    }

    public void setDBName(String Name){
        DBName=Name;
    }

    public String getDBName(){
        return  DBName;
    }

    public void setCommandType(String CommandType){
        commandType=CommandType;
    }

    public String getCommandType(){
        return  commandType;
    }

    public String arrayToString(ArrayList<ArrayList<String>> array){
        String s="";
        for(int i=0;i<array.size();i++){
            for(int j=0;j<array.get(0).size();j++){
                s=s+array.get(i).get(j)+"\t";
            }
            s=s+System.lineSeparator();
        }
        return s;
    }

    public void printConditionTree(ConditionNode start){

        if(start.getIsOp()){
            printConditionTree(start.leftChild);
            printConditionTree(start.rightChild);
        }
        else{
            System.out.println(start.getContent().toString());
        }


    }

}
