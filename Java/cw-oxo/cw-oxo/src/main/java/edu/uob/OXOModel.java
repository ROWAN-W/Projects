package edu.uob;
import java.util.ArrayList;
class OXOModel {
  private final ArrayList<ArrayList<OXOPlayer>> cells;
  private int row;
  private int col;
  private final ArrayList<OXOPlayer> players;
  private int currentPlayerNumber;
  private OXOPlayer winner;
  private boolean gameDrawn;
  private int winThreshold;
  private final static int maxSize =9;
  private final static int minSize =0;

  public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
    winThreshold = winThresh;
    players=new ArrayList<>();
    cells = new ArrayList<>();

    for(int i=0;i<numberOfRows;i++){
      ArrayList<OXOPlayer> ini=new ArrayList<>();
      for(int r=0;r<numberOfColumns;r++){
        ini.add(null);
      }
      cells.add(ini);
    }
    row=numberOfRows;
    col=numberOfColumns;
  }

  public int getNumberOfPlayers() {
    return players.size();
  }

  public void addPlayer(OXOPlayer player) {
    players.add(player);
  }

  public OXOPlayer getPlayerByNumber(int number) {
    return players.get(number);
  }


  public OXOPlayer getWinner() {
    return winner;
  }


  public void setWinner(OXOPlayer player) {
    winner = player;
  }

  public int getCurrentPlayerNumber() {
    return currentPlayerNumber;
  }

  public void setCurrentPlayerNumber(int playerNumber) {
    currentPlayerNumber = playerNumber;
  }

  public int getNumberOfRows() {
    return row;
  }

  public int getNumberOfColumns() {
    return col;
  }

  public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
    return cells.get(rowNumber).get(colNumber);
  }

  public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
    cells.get(rowNumber).set(colNumber,player);
  }
  public void addRow() {
    if(row<maxSize){
      ArrayList<OXOPlayer> r=new ArrayList<>();
      for(int i =0;i<col;i++){
        r.add(null);
      }
      cells.add(r);
      row++;
    }
  }
  public void removeRow() {
    if (row>minSize){
      cells.remove(row-1);
      row--;
    }
  }
  public void addColumn() {
    if(col<9){ for(int i=0;i<row;i++){
      cells.get(i).add(null);
    }
      col++;
    }
  }
  public void removeColumn() {
    if (col>minSize){
      for(int i=0;i<row;i++){
        cells.get(i).remove(col-1);
      }
      col--;
    }
  }

  public void setWinThreshold(int winThresh) {
    winThreshold = winThresh;
  }

  public int getWinThreshold() {
    return winThreshold;
  }

  public void setGameDrawn() {
    gameDrawn = true;
  }

  public boolean isGameDrawn() {
    return gameDrawn;
  }

}
