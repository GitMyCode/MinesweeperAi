import javax.swing.*;
import java.awt.*;

/**
 * Created by MB on 3/12/14.
 */
public class Minesweeper extends JFrame{

    private final int WIDTH = 260;
    private final int HEIGHT = 280;


    private final JLabel status;

    public Minesweeper(){

        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        status = new JLabel("");
        add(status, BorderLayout.SOUTH);
        add(new Grid(status));



    }

    public static void main(String[] args){

                JFrame executer = new Minesweeper();
                executer.setVisible(true);

    }


}
