package domain;

public class IceBlock extends Block {
    

    public IceBlock(BadIceCream board,int row, int column){
        super(board,row,column);
    }

    @Override 
    public boolean isIceBlock() {
    	return true;
    }
    
    public void introduceFruit(Fruit f) {
    	hasFruit = true;
    	fruit = f;
    }
    
    public boolean hasFruit() {
    	return hasFruit;
    }
    
    public void setHasFogata(boolean b) {
    	hasFogata = b;
    }

    @Override
    public String getBlockType() {
        return "ICE";
    }
    
}