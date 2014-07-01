import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Created by MB on 3/12/14.
 */
public class GridView extends JFrame implements ActionListener{

    /*
    * Expert : row 16, col 30, width 480, height 280
    * Medium : row 20, col 20, width 330, height 330
    * */
    int ROW = 20;
    int COL = 20;
    int NB_MINES = 80;

    private final int WIDTH = (COL*15) ; //pour expert : 480
    private final int HEIGHT = (ROW * 15); //poru expert :280
    private JPanel menu;
    private final JLabel status;
    private JButton reset;
    private JButton Ai;
    private JButton AiUnCoup;
    private JButton flags;

    private GridLogic grid;
    private Rule les_y;
    private Rule les_x;
    private Box cadre;
    private JPanel containter;


    private JLabel label_choice_row;
    private JLabel label_choice_col;
    JTextField t_choice_row;
    JTextField t_choice_col;

    private JLabel label_mine;
    JTextField t_mine;

    private JButton create;
    JPanel panel_creation;


    JPanel test;


    public GridView(){

        test= new JPanel();
        test.setPreferredSize(new Dimension(200, 200));
        test.setBackground(Color.red);


        setSize(WIDTH + 300, HEIGHT + 100);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        label_choice_row = new JLabel("Nb row");
        label_choice_col = new JLabel("Nb col");
        label_mine       = new JLabel("Nb mines");

        Dimension dim_jtext = new Dimension(120,20);
        t_choice_row = new JTextField();
        t_choice_row.setPreferredSize(dim_jtext);
        t_choice_row.setMinimumSize(dim_jtext);

        t_choice_col = new JTextField();
        t_choice_col.setPreferredSize(dim_jtext);
        t_choice_col.setMinimumSize(dim_jtext);

        t_mine       = new JTextField();
        t_mine.setPreferredSize(dim_jtext);
        t_mine.setMinimumSize(dim_jtext);

        create = new JButton("create");
        create.addActionListener(this);

        panel_creation = new JPanel(new GridBagLayout());

        panel_creation.setBackground(Color.orange);
        Dimension panel_creation_dim = new Dimension(200,200);
        panel_creation.setPreferredSize(panel_creation_dim);
        panel_creation.setMinimumSize(panel_creation_dim);
        panel_creation.setMaximumSize(panel_creation_dim);

        addItem(panel_creation,label_choice_row,0,0,1,1,GridBagConstraints.WEST);
        addItem(panel_creation,t_choice_row,    1,0,1,1,GridBagConstraints.EAST);

        addItem(panel_creation,label_choice_col,0,1,1,1,GridBagConstraints.WEST);
        addItem(panel_creation,t_choice_col,    1,1,1,1,GridBagConstraints.EAST);


        addItem(panel_creation,label_mine,0,2,1,1,GridBagConstraints.WEST);
        addItem(panel_creation,t_mine,1,2,1,1,GridBagConstraints.EAST);

        addItem(panel_creation,create,0,3,0,0,GridBagConstraints.WEST);


        add(panel_creation,BorderLayout.EAST);
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

        createGrid(ROW,COL,NB_MINES);

        add(cadre, BorderLayout.CENTER);
        //grid.setBackground(Color.green);




    }
    public void createGrid(int row,int col,int nb_mines){


    int width = (col*15) ; //pour expert : 480
    int height = (row * 15); //poru expert :280

        les_y = new Rule(1,col);
        Dimension dim_y = new Dimension(width+20,7);
        les_y.setPreferredSize(dim_y);
        les_y.setMinimumSize(dim_y);
        les_y.setMaximumSize(dim_y);
        les_y.setLayout(new GridLayout(1,col));

        les_x = new Rule(0,row);
        Dimension dim_x = new Dimension(10,height);
        les_x.setPreferredSize(dim_x);
        les_x.setMinimumSize(dim_x);
        les_x.setMaximumSize(dim_x);
       // les_x.setBackground(Color.CYAN);
        les_x.setLayout(new GridLayout(row,1));



        containter = new JPanel(new GridBagLayout());
        Dimension dim_container = new Dimension(width+40,height+30);
        containter.setMaximumSize(dim_container);
        containter.setMinimumSize(dim_container);
        containter.setPreferredSize(dim_container);
        //containter.setBackground(Color.red);


        addItem(containter, les_y, 1, 0, 0, 7, GridBagConstraints.NORTHWEST);

        addItem(containter, les_x, 0, 1, 0, 7, GridBagConstraints.WEST);

        grid = new GridLogic(status,row,col, nb_mines);
        Dimension dim_grid = new Dimension(width ,height);
        grid.setPreferredSize(dim_grid);
        grid.setMaximumSize(dim_grid);
        grid.setMinimumSize(dim_grid);
        //grid.setBackground(Color.cyan);
        addItem(containter, grid, 1, 2, 0, 0, GridBagConstraints.CENTER);

        Dimension dim_cadre = new Dimension(width+30,height+30);
        cadre.setPreferredSize(dim_cadre);
        cadre.setMaximumSize(dim_cadre);
        cadre.setMaximumSize(dim_cadre);


        cadre.add(containter);
        cadre.add(Box.createVerticalGlue());
        cadre.add(Box.createHorizontalGlue());
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
        }else if(e.getActionCommand() == "create" ){
            System.out.println("create");

            cadre.removeAll();
            cadre.repaint();
            cadre.revalidate();
       /* cadre = new Box(BoxLayout.Y_AXIS);
        cadre.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        cadre.add(Box.createVerticalGlue());
        cadre.add(Box.createHorizontalGlue());
*/

            String text_row = t_choice_row.getText();
            String text_col = t_choice_col.getText();
            System.out.println(text_col);
            int row = Integer.parseInt(text_row);
            int col = Integer.parseInt(text_col);
            String text_mine = t_mine.getText();
            int mines = Integer.parseInt(text_mine) ;

            les_x =null;
            les_y= null;
            createGrid(row, col, mines);


            cadre.repaint();
            cadre.revalidate();
            /*
            containter.remove(grid);
            containter.remove(les_x);
            containter.remove(les_y);
            containter.removeAll();
            cadre.remove(grid);
            remove(grid);

            //remove(grid);
            grid= null;

            containter.repaint();
            containter.revalidate();


           // containter.add(test);

            grid.repaint_cases();
            grid.repaint();
            grid.revalidate();
            invalidate();
            add(grid);


            grid.setVisible(true);
            containter.repaint();
            containter.revalidate();
           containter.updateUI();
            repaint();
            validate();


            //containter.updateUI();
/*
            cadre.invalidate();
            cadre.validate();
            cadre.updateUI();*/
/*
            getContentPane().invalidate();
            getContentPane().validate();
*/
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
        JLabel[] indicateurs;
        public Rule(int xOry,int length){
            super();
            indicateurs=null;
            if(xOry == 1){
                JLabel placeholder = new JLabel();
                placeholder.setText("x-y");
                placeholder.setForeground(Color.BLACK);
                placeholder.setFont(new Font("Arial",Font.BOLD,8));
                add(placeholder);
            }
            indicateurs = new JLabel[length];
            for(int i=0;i<length;i++){
                JLabel num = new JLabel();
                num.setText(Integer.toString(i));
                num.setForeground(Color.BLUE);
                num.setFont(new Font("Serif",Font.PLAIN,10));
                indicateurs[i] = num;
                add(num);
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




    public static void main(String[] args){

        JFrame executer = new GridView();
        executer.pack();
        executer.setVisible(true);

    }}
