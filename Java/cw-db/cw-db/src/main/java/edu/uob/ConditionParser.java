package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;

public class ConditionParser {
    ArrayList<ArrayList<String>> tokenPairs;
    ArrayList<String> cleanCommand;


    public ConditionParser(ArrayList<ArrayList<String>> TokenPairs,ArrayList<String> CleanCommand){
        tokenPairs=TokenPairs;
        cleanCommand=CleanCommand;
    }

    public int findMiddleOp(int startIndex,int endIndex) throws DBException {
        int leftQuote=1;
        int rightQuote=0;
        for(int i=startIndex+1;i<endIndex;i++){
            if(cleanCommand.get(i).equals("(")){
                leftQuote++;
            }
            else if(cleanCommand.get(i).equals(")"))
            {rightQuote++;}

            if(leftQuote==rightQuote){
                return i+1;
            }
        }
        throw new DBException("invalid Condition");
    }

    ConditionNode fillBinaryTree(int startIndex,int endIndex,ConditionNode parent) throws DBException{
        if(!cleanCommand.get(startIndex).equals("(")){
            validateLeaf(startIndex,endIndex);
            ConditionNode newNode=fillLeaf(startIndex,endIndex);
            parent.addChild(newNode);
            return newNode;
        }

        validateNode(startIndex,endIndex);
        ConditionNode newNode=fillNode(startIndex,endIndex);
        parent.addChild(newNode);
        int middle=findMiddleOp(startIndex,endIndex);
        fillBinaryTree(startIndex+1,middle-2,newNode);
        fillBinaryTree(middle+2,endIndex-1,newNode);
        return newNode;
    }

    public void validateNode(int startIndex,int endIndex)throws DBException{
        if(!cleanCommand.get(startIndex).equals("(")||!cleanCommand.get(endIndex).equals(")")){
            throw new DBException("invalid Condition");
        }
        int middle=findMiddleOp(startIndex,endIndex);
        String op=cleanCommand.get(middle);
        if(op!="AND"&&op!="OR"){
            throw new DBException("invalid Condition");
        }

        if(!cleanCommand.get(middle-1).equals(")")||!cleanCommand.get(middle+1).equals("(")){
            throw new DBException("invalid Condition");
        }
    }

    public ConditionNode fillNode(int startIndex,int endIndex) throws DBException{
        ConditionNode node=new ConditionNode();
        int middle=findMiddleOp(startIndex,endIndex);
        node.setIsOp(true);
        ArrayList<String> content =new ArrayList<>(Arrays.asList(cleanCommand.get(middle)));
        node.setContent(content);
        return node;
    }

    public void validateLeaf(int startIndex,int endIndex) throws DBException{
        if(endIndex!=startIndex+2 ||!tokenPairs.get(startIndex).get(0).equals("ID")){
            throw new DBException("invalid Condition");
        }
        if(tokenPairs.get(startIndex+1).get(0)!="Operator"){
            throw new DBException("invalid Condition");
        }
        String value=tokenPairs.get(startIndex+2).get(0);
        if(value!="String"&&value!="BoolValue"&&value!="Int"&&value!="Float"&&value!="NULL"){
            throw new DBException("invalid Condition");
        }

    }

    public ConditionNode fillLeaf(int startIndex,int endIndex)throws DBException{
        ConditionNode leaf=new ConditionNode();
        leaf.setIsOp(false);
        ArrayList<String> content=new ArrayList<>();
        for(int i=startIndex;i<=endIndex;i++){
            content.add(cleanCommand.get(i));
        }
        leaf.setContent(content);
        return leaf;
    }

}
