package test;

import domain.BadIceCream;
import domain.BadIceCreamException;

public class BadIceCreamTest {
    public static void main(String[] args) {
        try {
            BadIceCream game = new BadIceCream(1);
            game.importLevel("level1.txt");
            System.out.println("Nivel cargado correctamente.");
            System.out.println("Jugador 1 en: " + game.getFirstPlayer().getRow()
                               + ", " + game.getFirstPlayer().getColumn());
        } catch (BadIceCreamException e) {
            e.printStackTrace();
        }
    }
} 
