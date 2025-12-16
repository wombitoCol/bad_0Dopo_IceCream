package domain;

public class Platano extends Fruit {

    public Platano(BadIceCream board, int row, int column){
        super(board,row,column);
        score = 5;
    }

    @Override
    public void act() { }
    
    @Override
    public String getFruitType() {
        return "BANANA";
    }

}