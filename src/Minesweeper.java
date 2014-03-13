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
    private final int HEIGHT = 290;


    private JPanel menu;
    private final JLabel status;
    private JButton reset;
    private JButton Ai;

    private Grid grid;

    public Minesweeper(){

        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        reset= new JButton("Reset");
        reset.addActionListener(this);

        status = new JLabel("");
        status.setSize(50,30);

        Ai = new JButton("Ai");
        Ai.setSize(30,30);
        Ai.addActionListener(this);

        menu = new JPanel();
        menu.setLayout(new GridLayout(1, 0));

        menu.add(status, BorderLayout.SOUTH);
        menu.add(Ai,BorderLayout.SOUTH);
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
        }else if(e.getActionCommand() == "Ai"){
            System.out.println("Ai");
            grid.AI();

        }
    }

    public class controllerMenu implements EventListener{


    }


}
