package domain;

public class Fogata extends Block {

    private boolean isActive;
    private long creationTime;                         
    private static final long ACTIVATION_DELAY = 3000; 

    public Fogata(BadIceCream board, int row, int column) {
        super(board, row, column);
        this.isActive = false;            
        this.creationTime = System.currentTimeMillis();
        this.setHasFogata(true);          
    }

    @Override
    public boolean isFogata() {
        return true;
    }

    public boolean isActive() {
        if (!isActive && System.currentTimeMillis() - creationTime >= ACTIVATION_DELAY) {
            isActive = true;
        }
        return isActive;
    }

    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getBlockType() {
        return "FOGATA";
    }

    @Override
    public boolean isDangerous(){
        if(this.isActive){
            return true;
        }
        return false;
    }
}