package domain;

public class BaldosaCaliente extends Block{

	public BaldosaCaliente(BadIceCream board,int row, int column){
        super(board,row,column);
        this.setHasBaldosa(true);
    }
	
	@Override
	public boolean isBaldosaCaliente() {
    	return true;
    }
	
    @Override
    public String getBlockType() {
        return "BALDOSACALINETE";
    }
}