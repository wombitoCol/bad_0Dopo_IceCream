package domain;
import java.io.*;

import java.util.prefs.BackingStoreException;

public class Player implements Unit, Serializable {
	
    private String flavor; 
    private int score;
    private int row;
    private int column;
    private String directionOfView;
    private BadIceCream board;

    public Player(BadIceCream board, int row, int column, String flavor){
        this.board = board;
        this.row = row;
        this.column = column;
        this.flavor = flavor; 
        this.board.setPlayer(row, column,this);
        this.board.addUnit(this);
        directionOfView = "down";
        score = 0;
    }
    
    public Player(BadIceCream board, int row, int column){
        this(board, row, column, "vanilla");
    }

    @Override
    public void act() { }

    public void setScore(int profit) {
        score += profit;
     }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public int getRow() {
        return row;
    }
    
    public String getFlavor() {
        return flavor;
    }
    
    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    public void changeOfView(String direction) throws BadIceCreamException{
        if(direction.equals("down") || direction.equals("up") || direction.equals("left") || direction.equals("right")){
            directionOfView = direction;
        } else {
            throw new BadIceCreamException(BadIceCreamException.DIRECTION_NO_ALLOWED);
        }
    }

    public String getDirectionOfView(){
        return directionOfView;
    }

    @Override
    public void changePosition(int newRow, int newColumn) {
        row = newRow;
        column = newColumn;
        this.board.setPlayer(row, column, this);
    }
    
    @Override
    public boolean isPlayer(){return true;}
    
    public int getScore() {
        return score;
    }
    
    public void shootOrBreakIce() throws BadIceCreamException{
    	if(directionOfView.equals("up")) {
            shootOrBreakIceUp();
        }
        else if(directionOfView.equals("down")) {
            shootOrBreakIceDown();
        }
        else if(directionOfView.equals("left")) {
            shootOrBreakIceLeft();
        }
        else if(directionOfView.equals("right")) {
            shootOrBreakIceRight();
        }
        else {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_ACTION);
        }
    }
    
    public void shootOrBreakIceUp() throws BadIceCreamException {
        int r = getRow();
        int c = getColumn();
        if (r - 1 < 0) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_ACTION);
        }
        
        Block[][] blocks = board.getBlocks();
        
        if (blocks[r - 1][c] != null && blocks[r - 1][c].isIceBlock()) {
            board.breakIceUp(r, c);
        }
        else if (blocks[r - 1][c] == null) {
            board.createIceUp(r, c);
        }
    }

    public void shootOrBreakIceDown() throws BadIceCreamException {
        int r = getRow();
        int c = getColumn();
        
        if (r + 1 >= board.getHeight()) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_ACTION);
        }
        
        Block[][] blocks = board.getBlocks();
        
        if (blocks[r + 1][c] != null && blocks[r + 1][c].isIceBlock()) {
            board.breakIceDown(r, c);  
        }
        else if (blocks[r + 1][c] == null) {
            board.createIceDown(r, c);  
        }
    }


    public void shootOrBreakIceLeft() throws BadIceCreamException {
        int r = getRow();
        int c = getColumn();
        
        if (c - 1 < 0) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_ACTION);
        }
        Block[][] blocks = board.getBlocks();   
        if (blocks[r][c - 1] != null && blocks[r][c - 1].isIceBlock()) {
            board.breakIceLeft(r, c);  
        }
        else if (blocks[r][c - 1] == null) {
            board.createIceLeft(r, c); 
        }
    }


    public void shootOrBreakIceRight() throws BadIceCreamException {
        int r = getRow();
        int c = getColumn();
        
        if (c + 1 >= board.getWidth()) {
            throw new BadIceCreamException(BadIceCreamException.CANNOT_EXECUTE_ACTION);
        }
        
        Block[][] blocks = board.getBlocks();
        
        if (blocks[r][c + 1] != null && blocks[r][c + 1].isIceBlock()) {
            board.breakIceRight(r, c);  
        }
        else if (blocks[r][c + 1] == null) {
            board.createIceRight(r, c);  
        }
    }
    
    public void die() {
    	this.board.setPlayer(row, column, null);
    }
    
}