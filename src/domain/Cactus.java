package domain;

public class Cactus extends Fruit{
	
    private boolean isActive;
    private int tickCounter = 0;

    private static final int ACTIVE_TICKS = 5;    
    private static final int INACTIVE_TICKS = 5;  
    public Cactus(BadIceCream board, int row, int column){
        super(board,row,column);
        score = 20;
        isActive = false; 
    }
	
    @Override
    public void act() throws BadIceCreamException {
        tickCounter++;

        if (isActive) {
            if (tickCounter >= ACTIVE_TICKS) {
                changeActivity(false); 
                tickCounter = 0;
            }
        } else {
            if (tickCounter >= INACTIVE_TICKS) {
                changeActivity(true); 
                tickCounter = 0;
            }
        }
    }
	
    @Override
    public boolean isCactus(){return true;}
	
    public boolean isActive(){return isActive;}

    public void changeActivity(boolean x){
        isActive = x;
    }

    @Override
    public String getFruitType() {
        return "CACTUS";
    }

    @Override
    public boolean isDangerous(){
        if(this.isActive){
            return true;
        }
        return false;
    }
}
