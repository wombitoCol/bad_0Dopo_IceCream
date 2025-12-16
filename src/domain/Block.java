package domain;
import java.io.*;

public abstract class Block implements Serializable, InteractWithPlayer{
	private long baldosaCoverTime;
    private static final long BALDOSA_MELT_DELAY = 2000;
    protected int row;
    protected int column;
    protected BadIceCream board;
    protected boolean hasFruit;
    protected Fruit fruit;
    protected boolean hasFogata;
    protected boolean hasBaldosa;

    public Block(BadIceCream board,int row, int column){
        this.row = row;
        this.column = column;
        this.board = board;
        this.board.setBlock(row, column,this);
        fruit = null;
        hasFruit = false;
        hasFogata = false;
        hasBaldosa = false;
    }


    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
    
    public boolean isIceBlock() {
    	return false;
    }
    
    public abstract String getBlockType();
    
    public void introduceFruit(Fruit f) {}
    
    public void setHasFruit(boolean b) {
    	hasFruit = b;
    }
    
    public boolean hasFogata() {return hasFogata;}
    
    public void setHasFogata(boolean b) {
    	hasFogata = b;
    }
    
    public boolean hasBaldosa() {return hasBaldosa;}
    
    public void setHasBaldosa(boolean hasBaldosa) {
        this.hasBaldosa = hasBaldosa;
        if (hasBaldosa) {
            baldosaCoverTime = System.currentTimeMillis();
        }
    }
    public boolean shouldMeltIceOverBaldosa() {
        return hasBaldosa &&
               (System.currentTimeMillis() - baldosaCoverTime) >= BALDOSA_MELT_DELAY;
    }
    
    public boolean hasFruit() {return false;}
    
    public boolean isBaldosaCaliente() {
    	return false;
    }
    
    public boolean isFogata() {
    	return false;
    }
    
    public boolean isDecorationBlock() {
    	return false;
    }

    @Override
    public boolean isDangerous(){
        return false;
    }

}

