import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

/**
 * Created by MB on 3/12/14.
 */
public class Minesweeper extends JFrame implements ActionListener{

    private final int WIDTH = 260;
    private final int HEIGHT = 280;


    private JPanel menu;
    private final JLabel status;
    private JButton reset;
    private Grid grid;
    public Minesweeper(){

        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        reset= new JButton("Reset");
        reset.addActionListener(this);

        status = new JLabel("");
        status.setSize(50,30);

        menu = new JPanel();
        menu.setLayout(new GridLayout(1, 0));

        menu.add(status, BorderLayout.SOUTH);
        menu.add(reset, BorderLayout.SOUTH);
        add(menu,BorderLayout.SOUTH);

        createGrid();

    }
    public void createGrid(){
        grid = new Grid(status);
        add(grid,BorderLayout.CENTER);

    }

    public static void main(String[] args){

                JFrame executer = new Minesweeper();
                executer.setVisible(true);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Reset"){
            System.out.println("reset");
            grid.game();
            /*grid.removeAll();
            grid.invalidate();
            remove(grid);
            grid= null;
            createGrid();*/
        }
    }

    public class controllerMenu implements EventListener{


    }


}
