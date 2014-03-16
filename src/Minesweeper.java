import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Created by MB on 3/12/14.
 */
public class Minesweeper extends JFrame implements ActionListener{

    private final int WIDTH = 280;
    private final int HEIGHT = 345;


    private JPanel menu;
    private final JLabel status;
    private JButton reset;
    private JButton Ai;
    private JButton test;

    private Grid grid;
    private Box cadre;

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

        JButton test100 = new JButton("100");
        test100.addActionListener(this);



        Ai = new JButton("Ai");
        Ai.setSize(30,30);
        Ai.addActionListener(this);

        menu = new JPanel();
        menu.setLayout(new GridLayout(1, 0));

        menu.add(status, BorderLayout.SOUTH);
        menu.add(Ai,BorderLayout.SOUTH);
        menu.add(reset, BorderLayout.SOUTH);
        menu.add(test);
        menu.add(test100);

        add(menu, BorderLayout.SOUTH);



        cadre = new Box(BoxLayout.Y_AXIS);
        cadre.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        cadre.add(Box.createVerticalGlue());
        cadre.add(Box.createHorizontalGlue());


        Rule les_y = new Rule(1);
        Dimension dim_y = new Dimension(250,7);
        les_y.setPreferredSize(dim_y);
        les_y.setMinimumSize(dim_y);
        les_y.setMaximumSize(dim_y);
        les_y.setLayout(new GridLayout(1, 15));

        Rule les_x = new Rule(0);
        Dimension dim_x = new Dimension(10,250);
        les_x.setPreferredSize(dim_x);
        les_x.setMinimumSize(dim_x);
        les_x.setMaximumSize(dim_x);
        les_x.setLayout(new GridLayout(15,1));



        JPanel containter = new JPanel();
        Dimension dim_container = new Dimension(260,260);
        containter.setMaximumSize(dim_container);
        containter.setMinimumSize(dim_container);
        containter.setPreferredSize(dim_container);
      //  containter.setBackground(Color.red);


        containter.add(les_y, BorderLayout.NORTH);
        
        containter.add(les_x, BorderLayout.WEST);
        createGrid();

        grid = new Grid(status);
        Dimension dim_grid = new Dimension(235,250);
        grid.setPreferredSize(dim_grid);
        grid.setMaximumSize(dim_grid);
        grid.setMinimumSize(dim_grid);
       // grid.setBackground(Color.green);
        containter.add(grid, BorderLayout.CENTER);

        Dimension dim_cadre = new Dimension(250,250);
        cadre.setPreferredSize(dim_cadre);
        cadre.setMaximumSize(dim_cadre);
        cadre.setMaximumSize(dim_cadre);


        cadre.add(containter);
        cadre.add(Box.createVerticalGlue());
        cadre.add(Box.createHorizontalGlue());
        add(cadre, BorderLayout.CENTER);
    }
    public void createGrid(){



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

        }else if(e.getActionCommand()=="test"){

            grid.calculProbabilite();
        }else if(e.getActionCommand()== "100" ){
            for(int z=0;z<100;z++){
                grid.game();
                Random random = new Random();
                int ran_x = random.nextInt(15 -1);
                int ran_y = random.nextInt(15 -1);
                grid.play(grid.field[ran_x][ran_y],false);
                if(!grid.gameover){
                    grid.calculProbabilite();
                    boolean marche = false;
                    for(int i=0;i<15;i++){
                        for(int j=0; j<15 ;j++){
                            if(grid.field[i][j].getFlag()){
                                marche = true;
                                break;
                            }
                        }
                    }
                    if (!marche){
                        System.out.println("                                                           NON:"+z);
                        return;}
                }
            }

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
