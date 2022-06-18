package edu.uob;
import edu.uob.OXOMoveException.*;

import java.util.Locale;

class OXOController {
  OXOModel gameModel;

  public OXOController(OXOModel model) {
    gameModel = model;
  }

  public void handleIncomingCommand(String command) throws OXOMoveException {

    validate(command.toLowerCase(),gameModel.getNumberOfRows(),gameModel.getNumberOfColumns());
    int x = command.charAt(0) - 'a';
    int y = command.charAt(1) - '1' ;

    if(gameModel.getCellOwner(x, y) !=null){
      throw new CellAlreadyTakenException(x,y);
    }
    int currentnum;
    currentnum = gameModel.getCurrentPlayerNumber();

    if (gameModel.getWinner()==null) {
      gameModel.setCellOwner(x, y, gameModel.getPlayerByNumber(currentnum));
      if(!winDetect(x, y, gameModel.getPlayerByNumber(currentnum))&& !drawDetect()){
        gameModel.setCurrentPlayerNumber(iterate(currentnum));
      }
    }
  }

 private int iterate(int playernum){
    playernum++;
    return playernum%gameModel.getNumberOfPlayers();
 }

  private void validate (String command, int row, int col) throws OXOMoveException{
    if(command==null){
      throw new InvalidIdentifierLengthException(0);
    }
    if(command.length()!=2){
      throw new InvalidIdentifierLengthException(command.length());
    }
    if(command.charAt(0)<'a'|| command.charAt(0)>'z'){
      throw new InvalidIdentifierCharacterException(RowOrColumn.ROW,command.charAt(0));
    }
    if(command.charAt(1)<'0'||command.charAt(1)>'9'){
      throw new InvalidIdentifierCharacterException(RowOrColumn.COLUMN,command.charAt(1));
    }
    if((command.charAt(0)>'a'+row-1)){
      throw new OutsideCellRangeException(RowOrColumn.ROW,command.charAt(0)-'a');
    }
    if(command.charAt(1)>'1'+col-1){
      throw new OutsideCellRangeException(RowOrColumn.COLUMN,command.charAt(1)-'1');
    }
  }

  public void addRow() {
    gameModel.addRow();
    drawDetect();
  }

  public void removeRow() {
    gameModel.removeRow();
    drawDetect();
  }

  public void addColumn() {
    gameModel.addColumn();
  }

  public void addPlayer(OXOPlayer newplayer){
    gameModel.addPlayer(newplayer);
    drawDetect();
  }

  public void removeColumn() {
    gameModel.removeColumn();
    drawDetect();
  }

  public void increaseWinThreshold() {
    int winthre=gameModel.getWinThreshold()+1;
    gameModel.setWinThreshold(winthre);
    drawDetect();
  }

  public void decreaseWinThreshold() {
    int winthre=gameModel.getWinThreshold()-1;
    gameModel.setWinThreshold(winthre);
    drawDetect();
  }

  private boolean winDetect(int x, int y, OXOPlayer curplayer) {
    boolean a=horizontal(x,y,curplayer);
    boolean b=vertical(x,y,curplayer);
    boolean c=rightDiagonal(x,y,curplayer);
    boolean d=leftDiagonal(x,y,curplayer);
    if (a || b || c || d ) {
      gameModel.setWinner(curplayer);
      return true;
    }
    return false;
  }

  private boolean drawDetect(){
    int row=gameModel.getNumberOfRows();
    int col=gameModel.getNumberOfColumns();
    boolean full=true;
    int winnum=0;
    for(int i=0;i<row;i++){
      for(int j=0;j<col;j++){
        if (gameModel.getCellOwner(i,j)!=null){
          if(winDetect(i,j,gameModel.getCellOwner(i,j)))
          {
            winnum++;
          }
        }
        else{
          full=false;
        }
      }
    }
    winnum=winnum/gameModel.getWinThreshold();
    if(winnum>1){
      gameModel.setGameDrawn();
      return true;
    }
    else if(winnum==0 && full){
      gameModel.setGameDrawn();
      return true;
    }
    return false;
  }

  private boolean horizontal(int x, int y, OXOPlayer curplayer) {
    int n = 0;
    int left=y;
    int right=y+1;
    while ( left>=0 && gameModel.getCellOwner(x,left) == curplayer ) {
      n++;
      left--;
    }

    while (right < gameModel.getNumberOfColumns() && gameModel.getCellOwner(x,right) == curplayer ) {
      n++;
      right++;
    }
    return n == gameModel.getWinThreshold();
  }

  private boolean vertical(int x, int y, OXOPlayer curplayer) {
    int n = 0;
    int up=x;
    int down=x+1;

    while (up>= 0  && gameModel.getCellOwner(up,y) == curplayer ) {
      n++;
      up--;
    }
    while ( down< gameModel.getNumberOfRows() && gameModel.getCellOwner(down,y) == curplayer) {
      n++;
      down++;
    }
    return n == gameModel.getWinThreshold();
  }

  private boolean rightDiagonal(int x, int y, OXOPlayer curplayer) {
    int n = 0;
    int a = x;
    int b = y;
    int c = x + 1;
    int d = y - 1;
    while (a >= 0 && b < gameModel.getNumberOfColumns() && gameModel.getCellOwner(a,b) == curplayer) {
      n++;
      a--;
      b++;
    }
    while ( c < gameModel.getNumberOfRows() && d >=0 && gameModel.getCellOwner(c,d) == curplayer) {
      n++;
      c++;
      d--;
    }
    return n ==gameModel.getWinThreshold();

  }

  private boolean leftDiagonal(int x, int y, OXOPlayer curplayer) {
    int n = 0;
    int a = x;
    int b = y;
    int c = x + 1;
    int d = y + 1;
    while ( a >= 0 && b >=0 && gameModel.getCellOwner(a,b) == curplayer) {
      n++;
      a--;
      b--;
    }
    while (c < gameModel.getNumberOfRows() && d <gameModel.getNumberOfColumns() && gameModel.getCellOwner(c,d) == curplayer) {
      n++;
      c++;
      d++;
    }
    return n ==gameModel.getWinThreshold();
  }
}