import sun.net.www.content.text.plain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * Created by MB on 3/12/14.
 */
public class Grid extends JPanel  {

    private int row = 15;
    private int col = 15;
    private int total_cells = 16;
    private int nb_mines = 30;
    private final int  CELL_SIZE = 15;


    private final int RIEN = 0;
    private final int MINE = 9;
    private final int COUVERTE = 10;
    private final int FLAG = 11;
    private final int XFLAG = 12;

    private Image[] grid_icones;
    private Case[][] field;
    private JLabel status;
    Controller controller;
    boolean gameover = false;
    Random random;

    int ran_x;
    int ran_y;

    public Grid(JLabel status){

        this.status = status;
        grid_icones = new Image[13];


        controller = new Controller();
        field = new Case[row][col];
        for(int i=0;i<row;i++){
            for(int j=0; j<col; j++){
                field[i][j] = new Case();
                field[i][j].x = i;
                field[i][j].y = j;
                field[i][j].addMouseListener(controller);
                add(field[i][j]);
            }
        }
        setLayout(new GridLayout(row,col));

        for(int i=0; i<13; i++){
            grid_icones[i] = (new ImageIcon("img/j"+i+".gif")).getImage();
        }
        setDoubleBuffered(true);

        game();
    }


    private void game(){
        random = new Random();
        int ran_x,ran_y;
        status.setText(Integer.toString(nb_mines));
        for(int i=0; i< nb_mines ; i++){
            ran_x = random.nextInt(row-1);
            ran_y = random.nextInt(col-1);
            System.out.println("x: "+ran_x+ " y:"+ran_y);
            field[ran_x][ran_y].setStatus(MINE);
        }
        calculateIndice();
        AI();
     }

    private void calculateIndice(){
        int cal_status=0;
        for(int i=0; i < row; i++){
            for(int j=0; j< col; j++){
               if(field[i][j].getStatus() != MINE){
                    if(estValide(i+1,j)){
                        cal_status+=  (field[i+1][j].getStatus() == MINE) ? 1 : 0; //North
                    }
                    if(estValide(i+1,j-1)){
                        cal_status+=  (field[i+1][j-1].getStatus()==MINE) ? 1:  0; //North-Ouest
                    }
                    if(estValide(i+1,j+1)){
                        cal_status+= field[i+1][j+1].getStatus() == MINE ? 1: 0; //North-Est
                    }

                    if(estValide(i,j-1)){
                        cal_status+= field[i][j-1].getStatus() == MINE ? 1: 0;  //Ouest
                    }
                    if(estValide(i,j+1)){
                        cal_status+= field[i][j+1].getStatus() == MINE ? 1: 0;  //Est
                    }
                    if(estValide(i-1,j)){
                        cal_status+=   field[i-1][j].getStatus() == MINE ? 1: 0;  //Sud
                    }
                    if(estValide(i-1,j-1)){
                        cal_status+=  field[i-1][j-1].getStatus() == MINE ? 1: 0;  //Sud-Ouest
                    }
                    if(estValide(i-1,j+1)){
                        cal_status+=  field[i-1][j+1].getStatus() == MINE ? 1: 0;  //Sud-Est*/
                    }
                    field[i][j].setStatus(cal_status);
                    cal_status=0;
               }

              }
        }
    }

    private boolean estValide(int x, int y){
        return (x >= 0 && x < row
             && y >= 0 && y < col);
    }


    public void AI(){

        final Timer timer = new Timer(800,null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ran_x = random.nextInt(row-1);
                int ran_y = random.nextInt(col-1);
                play(field[ran_x][ran_y],false);
                System.out.println("think");
                if(gameover)
                    timer.stop();

            }
        });
        timer.start();
    }
   public void play(Case current,boolean flag){
       if(flag){
           current.setStatus(FLAG);
       }else{
            current.estDecouvert = true;
            gameover = (current.getStatus()==MINE);
            gameover = (gameover)? true : game_finish();
       }
       current.repaint();
   }

    /*Verifie si il reste une case qui n'est pas encore decouverte
    c'est a dire une case qui n'a pas de flag et qu'on peut jouer
    * */
    public boolean game_finish(){
       boolean game_finish = true;
       for(Case[] c : field){
            for(Case elem : c){
                if(elem.getStatus()!=FLAG && !elem.estDecouvert )
                    game_finish = false;
            }
       }
        return game_finish;
    }
   ///////////////////////////////////////////////////////////////////////////////////////////////
    private class Case extends JPanel{
        boolean estDecouvert = false;
        int status;
        Image icone;
        int x;
        int y;

       @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(estDecouvert){
                g.drawImage(grid_icones[status],0,0,this);
            }else{
                g.drawImage(grid_icones[COUVERTE],0,0,this);
            }
        }

       public void setStatus(int new_status){
           this.status=new_status;

       }
       public int getStatus(){return this.status;}
    }

///////////////////////////////////////////////////////////////////////////////////////////////
    class Controller extends MouseAdapter{

       @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
           Case current = (Case) e.getSource();
           System.out.println();
           play(current,false);
       }
    }
}
