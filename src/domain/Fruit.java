package domain;
import java.io.*;

public abstract class Fruit implements Unit, Serializable, InteractWithPlayer{
	
    protected boolean move;
    protected String type;
    protected int row;
    protected int column;
    protected BadIceCream board;
    protected int score;

    public Fruit(BadIceCream board,int row, int column){
        this.board = board;
        this.row = row;
        this.column = column;
        this.board.setFruit(row,column,this);
        this.board.addUnit(this);
    }

    @Override
    public void act() throws BadIceCreamException { }

    public void destroy() { }

    @Override
    public int getRow() {
        return row;
    }

    public final int getScore(){return score;}

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public void changePosition(int newRow, int newColumn) {
        board.getFruits()[row][column] = null;

        row = newRow;
        column = newColumn;

        board.setFruit(row, column, this);
    }

    @Override
    public boolean isFruit(){return true;}
    
    public boolean isCactus(){return false;}
    
    public final void dissapear() {
    	this.board.setFruit(row, column, null);
    }
    
    public abstract String getFruitType();

    @Override
    public boolean isDangerous(){
        return false;
    }

}

