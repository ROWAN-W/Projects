package edu.uob;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinCMD extends DBCmd{
    ArrayList<ArrayList<String>> tableA;
    ArrayList<ArrayList<String>> tableB;
    ArrayList<ArrayList<String>> joined;
    ArrayList<ArrayList<String>> filtered;

    @Override
    public  String query(DBServer Server) throws DBException{
        server=Server;
        dataBase=server.getDataBase();
        checkDB();
        tableA=dataBase.getTableByName(tableNames.get(0)).getContent();
        tableB=dataBase.getTableByName(tableNames.get(1)).getContent();
        if(tableA.size()<2||tableB.size()<2){
            return null;
        }
        validateQuery();
        createJoinTable();
        filterJoinTable();
        polishJoinTable();
        return arrayToString(filtered);
    }


    public void polishJoinTable(){
        removeCols();
        for(int i=0;i<filtered.get(0).size();i++){
            String colName=filtered.get(0).get(i);
            Pattern pattern= Pattern.compile("_[AB]$");
            Matcher matcher=pattern.matcher(colName);
            if(matcher.find()){
                filtered.get(0).set(i,colName.replaceFirst(matcher.group(0),""));
            }
        }
        addID();
    }

    public void removeCols(){
           removeIDa();
           removeIDb();
           removeMatchA();
           removeMatchB();
    }

    public void removeIDa(){
        ArrayList<String> firstRow=filtered.get(0);
        int index=firstRow.indexOf("id_A");
        for(int i=0;i<filtered.size();i++){
            filtered.get(i).remove(index);
        }
    }

    public void removeIDb(){
        ArrayList<String> firstRow=filtered.get(0);
        int index=firstRow.indexOf("id_B");
        for(int i=0;i<filtered.size();i++){
            filtered.get(i).remove(index);
        }
    }
    public void removeMatchA(){
        ArrayList<String> firstRow=filtered.get(0);
        if(!firstRow.contains(colNames.get(0))){
            return;
        }
        int index=firstRow.indexOf(colNames.get(0));
        for(int i=0;i<filtered.size();i++){
            filtered.get(i).remove(index);
        }
    }
    public void removeMatchB(){
        ArrayList<String> firstRow=filtered.get(0);
        if(!firstRow.contains(colNames.get(1))){
            return;
        }
        int index=firstRow.indexOf(colNames.get(1));
        for(int i=0;i<filtered.size();i++){
            filtered.get(i).remove(index);
        }
    }


    public void addID(){
           filtered.get(0).add(0,"id");
           if(filtered.size()<2){
               return;
           }
           for(int i=1;i<filtered.size();i++){
               filtered.get(i).add(0,String.valueOf(i));
           }
    }


    public void filterJoinTable() throws DBException{
        filtered=new ArrayList<>();
        colNames.set(0, colNames.get(0)+"_A");
        colNames.set(1, colNames.get(1)+"_B");
        ArrayList<String> firstRow=joined.get(0);
        filtered.add(firstRow);

        for(int i=1;i<joined.size();i++){
            int  indexA=firstRow.indexOf(colNames.get(0));
            int indexB=firstRow.indexOf(colNames.get(1));
            if(joined.get(i).get(indexA).equals(joined.get(i).get(indexB))){
                filtered.add(joined.get(i));
            }
        }
    }


    public void validateQuery() throws DBException{
        if(!tableA.get(0).contains(colNames.get(0))||!tableB.get(0).contains(colNames.get(1))){
            throw new DBException("joining on non-existing column");
        }
    }


    public void  createJoinTable() throws DBException{
        joined=new ArrayList<>();
        joined.add(createFirstRow());

        for(int i=1;i<tableA.size();i++){
            for(int j=1;j<tableB.size();j++){
                ArrayList<String> row=new ArrayList<>();
                row.addAll(tableA.get(i));
                row.addAll(tableB.get(j));
                joined.add(row);
            }
        }

    }

    public ArrayList<String> createFirstRow(){
        ArrayList<String> row=new ArrayList<>();
        tableA.get(0).forEach((cell)->{row.add(cell+"_A");});
        tableB.get(0).forEach((cell)->{row.add(cell+"_B");});
        return row;
    }

    public void checkDB()throws  DBException {
        if(dataBase==null){
            throw new DBException("no database used");
        }
    }

}
