import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by MB on 3/12/14.
 */
public class Minesweeper extends JFrame implements ActionListener{

    private final int WIDTH = 260;
    private final int HEIGHT = 315;


    private JPanel menu;
    private final JLabel status;
    private JButton reset;
    private JButton Ai;
    private JButton test;

    private Grid grid;

    public Minesweeper(){

        setSize(WIDTH,HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        reset= new JButton("Reset");
        reset.setFont(new Font("Default",Font.PLAIN,10));
        reset.addActionListener(this);

        status = new JLabel("");
        status.setSize(50,30);

        test = new JButton("test");
        test.addActionListener(this);



        Ai = new JButton("Ai");
        Ai.setSize(30,30);
        Ai.addActionListener(this);

        menu = new JPanel();
        menu.setLayout(new GridLayout(1, 0));

        menu.add(status, BorderLayout.SOUTH);
        menu.add(Ai,BorderLayout.SOUTH);
        menu.add(reset, BorderLayout.SOUTH);
        menu.add(test);

        Rule les_y = new Rule(1);
        les_y.setSize(270,10);
        les_y.setAlignmentX(10);
        les_y.setLayout(new GridLayout(1, 15));

        Rule les_x = new Rule(0);
        les_x.setSize(10,270);
        les_x.setLayout(new GridLayout(15,1)); 
        add(les_y,BorderLayout.NORTH);
        add(les_x,BorderLayout.WEST);
        add(menu, BorderLayout.SOUTH);

        createGrid();

    }
    public void createGrid(){
        grid = new Grid(status);
        add(grid, BorderLayout.CENTER);

    }

    public static void main(String[] args){

                JFrame executer = new Minesweeper();
                executer.setVisible(true);

        String test = "test";
        System.out.println(test.substring(1));
        System.out.println(test.charAt(0));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if     (e.getActionCommand() == "Reset"){
            System.out.println("reset");
            grid.game();
       }else if(e.getActionCommand() == "Ai"){
            System.out.println("Ai");
            grid.AI();

        }else if(e.getActionCommand()== "test" ){
            grid.calculProbabilite();
        }
    }


    public class Rule extends JPanel{

        public Rule(int xOry){
            super();
            if(xOry == 1){
                JLabel placeholder = new JLabel();
                placeholder.setText("x-y");
                placeholder.setForeground(Color.BLACK);
                placeholder.setFont(new Font("Arial",Font.BOLD,8));
                add(placeholder);
            }
            for(int i=0;i<15;i++){
                JLabel num = new JLabel();
                num.setText(Integer.toString(i));
                num.setForeground(Color.BLUE);
                num.setFont(new Font("Serif",Font.PLAIN,10));
                add(num, BorderLayout.CENTER);
            }
        }

    }


}
