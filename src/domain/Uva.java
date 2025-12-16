package domain;

public class Uva extends Fruit {

    public Uva(BadIceCream board, int row, int column){
        super(board,row,column);
        score = 10;
    }

    @Override
    public void act() { }
    
    @Override
    public String getFruitType() {
        return "UVA";
    }
}
