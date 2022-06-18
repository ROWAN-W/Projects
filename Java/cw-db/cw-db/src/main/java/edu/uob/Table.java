package edu.uob;
import java.io.*;
import java.util.ArrayList;

public class Table {
     String name;
     String tablePath;
     File tableFile;
     String idPath;
     File idFile;
     int maxID;
     ArrayList<ArrayList<String>> content;



   /*
    public table (String TablePath){
        //for reading in local tables
        this.name= new File(TablePath).getName().replace(".tab","");
        tablePath=TablePath;
        content=new ArrayList<>();

    }
*/
     public Table(String DBPath, String name) throws DBException{

         this.name= name;
         tablePath=DBPath+File.separator+name+".tab";
         idPath=DBPath+File.separator+name+".txt";
         tableFile=new File(tablePath);
         idFile=new File(idPath);
         content=new ArrayList<>();
     }


     public void calculateMaxID() throws DBException{
         if(content==null||content.size()==0||content.size()==1){
             maxID=0;
             return;
         }
         int IDIndex=content.get(0).indexOf("id");
         for(int i=1;i<content.size();i++){
             int id=Integer.valueOf(content.get(i).get(IDIndex));
             if(id>maxID){
                 maxID=id;
             }
         }
     }

     public int getMaxID()throws DBException {
      return maxID;
     }

     public String getName(){
         return name;
    }

    public ArrayList<ArrayList<String>> getContent(){
         return content;
    }
    public void setContent(ArrayList<ArrayList<String>> Table) throws DBException{
         content=Table;
    }

    public void addRow(ArrayList<String> Row){
         content.add(Row);
    }

    public void insertRow(ArrayList<String> Row,int index){
         content.add(index,Row);
    }

    public void getRowByIndex(int index){

    }



     public boolean createTableFile()throws DBException{
         try{
             tableFile.createNewFile();
             calculateMaxID();
             idFile.createNewFile();
             storeMaxID();
             return true;
         }catch(java.io.IOException e){
             throw new DBException("fail to create table file");
         }

     }

    public void loadFromFile() throws DBException {

        FileReader reader;
         try{ reader = new FileReader(tableFile);}
         catch (FileNotFoundException e){
             throw new DBException("file "+tableFile.getName()+" not found");
         }
        BufferedReader buffer =new BufferedReader(reader);
         try{String line = buffer.readLine();
             while(line!=null){
                 String[] thisLine=line.split("\t");
                 ArrayList<String> list =new ArrayList<>();
                 for (int i=0; i<thisLine.length; i++){
                     list.add(thisLine[i]);
                 }
                 content.add(list);
                 line= buffer.readLine();
             }
             buffer.close();
         }
         catch (IOException e){
             throw new DBException("fail to read from file "+tableFile.getName());
         }
         loadMaxID();

    }

    public void loadMaxID()throws DBException{
         FileReader reader;
        try{ reader = new FileReader(idFile);}
        catch (FileNotFoundException e){
            throw new DBException("file "+idFile.getName()+" not found");
        }
        BufferedReader buffer =new BufferedReader(reader);
        try{
            String max=buffer.readLine();
            maxID=Integer.valueOf(max);
            buffer.close();
            }
        catch (IOException e){
            throw new DBException("fail to read from file "+idFile.getName());
        }
    }

    public String toString(){
       String bigLine="";

         for(int i=0;i<content.size();i++){
             for(int j =0; j<content.get(0).size();j++){
                 bigLine=bigLine+content.get(i).get(j)+'\t';
             }
                 bigLine=bigLine+System.lineSeparator();
         }
         return bigLine;
    }

    public void storeToFile() throws DBException {
         try{
             FileWriter writer = new FileWriter(tableFile);
             BufferedWriter buffer =new BufferedWriter(writer);
             buffer.write(this.toString());
             buffer.close();
         }catch (IOException e){
             throw new DBException("fail to store table");
         }
         storeMaxID();
    }
    public void storeMaxID() throws DBException{
        try{
            calculateMaxID();
            FileWriter writer = new FileWriter(idFile);
            BufferedWriter buffer =new BufferedWriter(writer);
            buffer.write(String.valueOf(maxID));
            buffer.close();
        }catch (IOException e){
            throw new DBException("fail to store max id");
        }
    }


    public void deleteFile() throws DBException{
            if(!tableFile.delete()||!idFile.delete()) {
                throw new DBException("fail to delete "+tableFile.getName()+"and "+idFile.getName());
            }
    }

}
