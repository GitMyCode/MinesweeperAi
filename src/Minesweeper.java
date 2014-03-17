import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Created by MB on 3/12/14.
 */
public class Minesweeper extends JFrame implements ActionListener{

    private final int WIDTH = 410;
    private final int HEIGHT = 410;
    final int ROW = 25;
    final int COL = 25;

    private JPanel menu;
    private final JLabel status;
    private JButton reset;
    private JButton Ai;
    private JButton AiUnCoup;
    private JButton flags;

    private Grid grid;
    private Box cadre;



    public Minesweeper(){

        setSize(WIDTH+100,HEIGHT+100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        reset= new JButton("Reset");
        reset.setFont(new Font("Default",Font.PLAIN,10));
        reset.addActionListener(this);

        AiUnCoup = new JButton("Un coup");
        AiUnCoup.addActionListener(this);

        status = new JLabel("");
        status.setSize(50,30);

        flags = new JButton("flags");
        flags.addActionListener(this);

        JButton test100 = new JButton("100");
        test100.addActionListener(this);



        Ai = new JButton("Ai");
        Ai.setSize(30,30);
        Ai.addActionListener(this);

        menu = new JPanel();
        menu.setLayout(new GridLayout(1, 0));

        menu.add(status, BorderLayout.SOUTH);
        menu.add(Ai,BorderLayout.SOUTH);
        menu.add(AiUnCoup,BorderLayout.SOUTH);
        menu.add(reset, BorderLayout.SOUTH);
        menu.add(flags);
        menu.add(test100);

        add(menu, BorderLayout.SOUTH);



        cadre = new Box(BoxLayout.Y_AXIS);
        cadre.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        cadre.add(Box.createVerticalGlue());
        cadre.add(Box.createHorizontalGlue());


        Rule les_y = new Rule(1);
        Dimension dim_y = new Dimension(WIDTH,7);
        les_y.setPreferredSize(dim_y);
        les_y.setMinimumSize(dim_y);
        les_y.setMaximumSize(dim_y);
        les_y.setLayout(new GridLayout(1,ROW));

        Rule les_x = new Rule(0);
        Dimension dim_x = new Dimension(10,HEIGHT-20);
        les_x.setPreferredSize(dim_x);
        les_x.setMinimumSize(dim_x);
        les_x.setMaximumSize(dim_x);
        //les_x.setBackground(Color.CYAN);
        les_x.setLayout(new GridLayout(COL,1));



        JPanel containter = new JPanel(new GridBagLayout());
        Dimension dim_container = new Dimension(WIDTH+20,HEIGHT+20);
        containter.setMaximumSize(dim_container);
        containter.setMinimumSize(dim_container);
        containter.setPreferredSize(dim_container);
        //containter.setBackground(Color.red);


        addItem(containter, les_y, 1, 0, 0, -40, GridBagConstraints.NORTHWEST);

        addItem(containter, les_x, 0, 1, 0, 80, GridBagConstraints.WEST);
        createGrid();

        grid = new Grid(status,ROW,COL);
        Dimension dim_grid = new Dimension(WIDTH-15,HEIGHT-15);
        grid.setPreferredSize(dim_grid);
        grid.setMaximumSize(dim_grid);
        grid.setMinimumSize(dim_grid);
        //grid.setBackground(Color.green);

        addItem(containter,grid,1,1,0,0,GridBagConstraints.CENTER);


        Dimension dim_cadre = new Dimension(WIDTH,HEIGHT);
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

        String test = "flags";
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
            grid.AI(250);

        }else if(e.getActionCommand()=="Un coup"){
            grid.AiPlay();

        }else if(e.getActionCommand()=="flags"){

            grid.calculProbabilite();
        }else if(e.getActionCommand()== "100" ){
            int win=0;
            int lose=0;
            for(int z=0;z<100;z++){
                grid.game();
                boolean firstplay = true;
                Random random = new Random();
                while(!grid.gameover){

                    grid.AiPlay();
                }
                if(grid.gameWin()){
                    win++;
                }else{
                    lose++;
                }

                /*
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
                */
            }
            System.out.println("                                            WIN:"+win+ "           LOSE:"+lose);

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
            for(int i=0;i<ROW;i++){
                JLabel num = new JLabel();
                num.setText(Integer.toString(i));
                num.setForeground(Color.BLUE);
                num.setFont(new Font("Serif",Font.PLAIN,10));
                add(num, BorderLayout.CENTER);
            }
        }

    }
    private void addItem(JPanel p, JComponent c, int x, int y, int width, int height, int align) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = x;
        gc.gridy = y;
        gc.gridwidth = width;
        gc.gridheight = height;
        gc.weightx = 100.0;
        gc.weighty = 100.0;
        gc.insets = new Insets(5, 5, 5, 5);
        gc.anchor = align;
        gc.fill = GridBagConstraints.NONE;
        p.add(c, gc);
    }



}
