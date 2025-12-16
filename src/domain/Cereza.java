package domain;
import java.util.Random;

public class Cereza extends Fruit {

    // 10 tics * 500 ms (monsterTimer) = 5000 ms = 5 segundos
    private static final int TICKS_PER_TELEPORT = 10;
    private int tickCounter = 0;

    public Cereza(BadIceCream board, int row, int column){
        super(board,row,column);
        score = 15;
    }

    @Override
    public void act() throws BadIceCreamException {
        tickCounter++;
        if (tickCounter < TICKS_PER_TELEPORT) {
            return;
        }
        tickCounter = 0;

        Random rand = new Random();
        int maxRow = board.getHeight();
        int maxCol = board.getWidth();

        int newRow, newCol;
        int intentos = 0;

        do {
            newRow = rand.nextInt(maxRow);   
            newCol = rand.nextInt(maxCol);   
            intentos++;
        } while (intentos < 100 &&
                (board.getBlocks()[newRow][newCol] != null ||
                 board.getFruits()[newRow][newCol] != null ||
                 board.getPlayers()[newRow][newCol] != null ||
                 board.getMonsters()[newRow][newCol] != null));

        if (intentos < 100) {
            changePosition(newRow, newCol);
        }
    }

    @Override
    public String getFruitType() {
        return "CEREZA";
    }
}
