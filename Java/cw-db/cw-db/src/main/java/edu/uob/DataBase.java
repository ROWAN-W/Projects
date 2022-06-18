package edu.uob;
import  java.io.*;
import java.util.ArrayList;

public class DataBase {
     String name;
     String DBPath;
     ArrayList<Table> tables;
     File DBFile;

     //constructor
     public DataBase(String rootPath, String Name){
          name=Name;
          DBPath=rootPath+File.separator+name;
          DBFile = new File(DBPath);
          tables=new ArrayList<>();
     }

     public String getName(){
          return name;
     }

     public void createDBDirectory () throws DBException{
          if(DBFile.mkdir()) return;
          throw new DBException("fail to create database");
     }

     public void loadFromDir() throws DBException{
          if(!DBFile.isDirectory())
          {
               throw new DBException("Database doesn't exist");
          }
          String[] fileList= DBFile.list();
          for(int i=0; i<fileList.length;i++) {
               if (fileList[i].contains(".tab")) {
                    Table t=new Table(DBPath,fileList[i].replace(".tab",""));
                    t.loadFromFile();
                    tables.add(t);
               }
          }

     }

     public void storeToDir() throws DBException{
          if(tables!=null){
               for(Table t:tables){
                    t.storeToFile();
               }
          }
     }

     public void deleteDir () throws DBException{
          if(!DBFile.delete()){
               throw new DBException("fail to delete directory "+DBFile.getName());
          }
     }

     public void deleteTableByName(String Name)throws DBException{
          if(!checkTableExists(Name)){
               throw new DBException(Name+" doesn't exist");
          }

          for(int i=0;i<tables.size();i++){
               if(tables.get(i).getName().equals(Name)){
                    tables.get(i).deleteFile();
               }
               tables.remove(i);
               return;
          }
     }

     public void deleteAllTables() throws DBException{
          for(int i=0;i<tables.size();i++){
               tables.get(i).deleteFile();
          }
          tables.clear();
          return;
     }

     public boolean checkTableExists(String Name){
          for(int i=0;i<tables.size();i++){
               if(tables.get(i).getName().equals(Name)){
                   return true;
               }
          }
          return false;
     }



     public Table CreatTable (String Name) throws DBException{
          if(checkTableExists(Name)){
               throw new DBException("table already exist");
          }
          Table t=new Table(DBPath,Name);
          tables.add(t);
          t.createTableFile();
          return t;
     }


     public Table getTableByIndex(int i) throws DBException{
          if(tables.size()>i){
               return tables.get(i);
          }
          else {
               throw new DBException("Table doesn't exist");
          }
     }

     public Table getTableByName(String Name) throws DBException{
          for(int i=0;i<tables.size();i++){
               if(Name.equals(tables.get(i).getName())){
                    return tables.get(i);
               }
          }
          throw new DBException("Table doesn't exit");
     }



}
