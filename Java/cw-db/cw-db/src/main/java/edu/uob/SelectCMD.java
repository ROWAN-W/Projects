package edu.uob;

import java.util.ArrayList;

public class SelectCMD extends DBCmd{
    Table table;

    @Override
    public String query(DBServer Server) throws DBException{
        server=Server;
        dataBase=server.getDataBase();
        checkDB();
        table=dataBase.getTableByName(tableNames.get(0));
        if(condition==null){
           return selectAttributes(table.getContent());
        }
        else {
            ArrayList<ArrayList<String>> processed=getQualifiedRows();
            if(processed==null){
                return null;
            }
            return selectAttributes(processed);
        }

    }

    public void checkDB()throws  DBException {
        if(dataBase==null){
            throw new DBException("no database used");
        }
    }


    public ArrayList<ArrayList<String>> getQualifiedRows() throws DBException{
        ArrayList<ArrayList<String>> array=table.getContent();
        if(array.size()<2){
            return null;
        }
        ArrayList<ArrayList<String>> qualified=new ArrayList<>();
        qualified.add(0,table.content.get(0));
        ArrayList<ArrayList<String>> toBeTested=new ArrayList<>();
        toBeTested.add(0,table.content.get(0));
        toBeTested.add(1,table.content.get(0));
        boolean exist=false;
        for(int i=1;i<array.size();i++){
            toBeTested.set(1,array.get(i));
            if(condition.checkCondition(toBeTested,condition)){
                exist=true;
                qualified.add(array.get(i));
            }
        }
        if(exist){
            return qualified;
        }
        else{
            return null;
        }
    }



    public String selectAttributes(ArrayList<ArrayList<String>> array) throws DBException {
        if(colNames.get(0)=="*"){
            return arrayToString(array);
        }
        if(array.size()>0)
        {
            return selectArray(array);
        }
        else{
            throw new DBException("empty table");
        }
    }




    public String selectArray(ArrayList<ArrayList<String>> array){
        String bigS="";
        for(int i=0;i<array.size();i++){
            bigS=bigS+selectRow(array.get(i),array)+System.lineSeparator();
        }
        return bigS;
        }

        public String selectRow(ArrayList<String> row,ArrayList<ArrayList<String>> array){
            String line="";
            ArrayList<String> firstRow=array.get(0);
            for(int i=0;i<row.size();i++){
                for(int j=0;j<colNames.size();j++){
                    if (firstRow.get(i).equals(colNames.get(j))){
                        line=line+row.get(i)+"\t";
                    }
                }
            }
            return line;
        }




}
