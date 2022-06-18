package edu.uob;

import java.util.ArrayList;

public class ConditionNode {
    boolean isOp;
    ArrayList<String> content;
    ConditionNode leftChild;
    ConditionNode rightChild;


    public void setContent(ArrayList<String> Content){
        content=Content;
    }

    public ArrayList<String> getContent(){
        return content;
    }

    public void setIsOp(Boolean IsOp){
        isOp=IsOp;
    }

    public Boolean getIsOp(){
        return isOp;
    }

    public boolean addChild(ConditionNode child){
        if(leftChild==null){
            leftChild=child;
            return true;
        }
        else if(rightChild==null){
            rightChild=child;
            return true;
        }
        else{
            return false;
        }
    }

    public ConditionNode getLeftChild(){
        return leftChild;
    }
    public ConditionNode getRightChild(){
        return rightChild;
    }

    public boolean checkCondition( ArrayList<ArrayList<String>> toBeTested, ConditionNode node) throws DBException{
        if(!node.isOp){
            return check(node.getContent(),toBeTested);
        }
        if(node.getContent().get(0).equals("AND")){
           return checkCondition(toBeTested,node.getLeftChild()) && checkCondition(toBeTested,node.getRightChild());
        }
        if(node.getContent().get(0).equals("OR")){
            return checkCondition(toBeTested,node.getLeftChild()) || checkCondition(toBeTested,node.getRightChild());
        }

        throw new DBException("Error while checking CONDITION");
    }

    public boolean check(ArrayList<String> con,ArrayList<ArrayList<String>> testRows) throws DBException{
        if(!testRows.get(0).contains(con.get(0))){
            throw new DBException("unknown attribute name in CONDITION");
        }
        String expected=con.get(2);
        int index=testRows.get(0).indexOf(con.get(0));
        String actual=testRows.get(1).get(index);
        int compare=typeMatch(actual,expected);
        switch (con.get(1)){
            case "==":
                return (compare==0);

            case ">":
                return (compare>0);

            case "<":
                return (compare<0);

            case ">=":
                return (compare>=0);

            case "<=":
                return (compare<=0);
            case "!=":
                return compare!=0;
            case "LIKE":
                return actual.contains(expected);
            default:
                throw new DBException("unknown operator in CONDITION ");
        }
    }

    public int typeMatch(String actual, String expected) throws DBException{

        if(isDigit(actual)&&isDigit(expected)){
            return Float.compare(Float.valueOf(actual),Float.valueOf(expected));
        }
        else if(!isDigit(actual)&&!isDigit(expected)){
            return actual.compareTo(expected);
        }
        else{
            throw new DBException("compare between different type of values");
        }

    }


    public boolean isDigit(String s){
        if(s.matches("^[+-]?[0-9]+[.]?[0-9]*$")){
            return true;
        }
        return false;
    }

}
