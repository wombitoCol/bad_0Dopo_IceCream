package domain;

public class DecorationBlock extends Block {

    public DecorationBlock(BadIceCream board,int row, int column){
        super(board,row,column);
    }
    
    @Override
    public boolean isDecorationBlock() {
    	return true;
    }
    
    @Override
    public String getBlockType() {
        return "DECORATION";
    }
}