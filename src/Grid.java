import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by MB on 3/12/14.
 */
public class Grid extends JPanel  {

    private final int ROW = 15;
    private final int COL = 15;
    private final int total_cells = 16;
    private final int nb_mines = 3;
    private final int CELL_SIZE = 15;


    private final int RIEN = 0;
    private final int MINE = 9;
    private final int COUVERTE = 10;
    private final int FLAG = 11;
    private final int XFLAG = 12;

    private Image[] grid_icones;
    private Case[][] field;
    private JLabel status;
    private int mines_restantes = nb_mines;
    Controller controller;
    boolean gameover = false;
    Random random;

    int ran_x;
    int ran_y;

    public Grid(JLabel status){

        this.status = status;
        grid_icones = new Image[13];


        controller = new Controller();
        field = new Case[ROW][COL];
        for(int i=0;i< ROW;i++){
            for(int j=0; j< COL; j++){
                field[i][j] = new Case();
                field[i][j].x = i;
                field[i][j].y = j;
                field[i][j].addMouseListener(controller);
                add(field[i][j]);
            }
        }
        setLayout(new GridLayout(ROW, COL));

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
            ran_x = random.nextInt(ROW -1);
            ran_y = random.nextInt(COL -1);
            System.out.println("x: "+ran_x+ " y:"+ran_y);
            field[ran_x][ran_y].setStatus(MINE);
        }
        calculateIndice();
       // AI();
     }

    private void calculateIndice(){
        int cal_status=0;
        for(int i=0; i < ROW; i++){
            for(int j=0; j< COL; j++){
               if(field[i][j].getStatus() != MINE){
                    if(estValide(i+1,j))     cal_status+=  (field[i+1][j].getStatus() == MINE) ? 1 : 0; //North
                    if(estValide(i+1,j-1))   cal_status+=  (field[i+1][j-1].getStatus()==MINE) ? 1:  0; //North-Ouest
                    if(estValide(i+1,j+1))   cal_status+= field[i+1][j+1].getStatus() == MINE ? 1: 0; //North-Est
                    if(estValide(i,j-1))     cal_status+= field[i][j-1].getStatus() == MINE ? 1: 0;  //Ouest
                    if(estValide(i,j+1))     cal_status+= field[i][j+1].getStatus() == MINE ? 1: 0;  //Est
                    if(estValide(i-1,j))     cal_status+=   field[i-1][j].getStatus() == MINE ? 1: 0;  //Sud
                    if(estValide(i-1,j-1))   cal_status+=  field[i-1][j-1].getStatus() == MINE ? 1: 0;  //Sud-Ouest
                    if(estValide(i-1,j+1))   cal_status+=  field[i-1][j+1].getStatus() == MINE ? 1: 0;  //Sud-Est*/
                    field[i][j].setStatus(cal_status);
                    cal_status=0;
                   field[i][j].repaint();
               }
              }
        }

    }

    private boolean estValide(int x, int y){
        return (x >= 0 && x < ROW
             && y >= 0 && y < COL && !field[x][y].estDecouvert);
    }


    public void AI(){
        final Timer timer = new Timer(800,null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int ran_x = random.nextInt(ROW -1);
                int ran_y = random.nextInt(COL -1);
                play(field[ran_x][ran_y],false);
                System.out.println("think");
                if(gameover)
                    timer.stop();
            }
        });
        timer.start();
    }
   public void play(Case current,boolean flag){
       System.out.println("play");
       if(!gameover && !(!flag && current.flaged)){ //Si partie pas fini et que la case n'est pas flager
           if(flag && mines_restantes>0 ){
               current.switchFlag();
               status.setText(Integer.toString(mines_restantes));
               gameover =  (game_finish()) || mines_restantes==0;
           }else{
               current.estDecouvert = true;
               gameover = (current.getStatus() == MINE) || (game_finish()) || mines_restantes==0;
               if(current.getStatus()==RIEN){
                   ArrayList<Case> voisins = getVoisins(current);
                   for(Case v : voisins){
                       play(v,false);
                   }
               }
           }
           current.repaint();
       }

       if(gameover){
           if(game_finish()){
               status.setText("Game Win!");
           }else{
               status.setText("Game Lost!");
           }
           for(Case[] cases : field){
               for(Case c : cases){
                   c.repaint();
               }
           }
       }
   }
   public ArrayList<Case> getVoisins(Case c){
       ArrayList<Case> voisins = new ArrayList<Case>();
       int x = c.getXpos();
       int y = c.getYpos();
       if(estValide(x+1,y))      voisins.add(field[x+1][y]);
       if(estValide(x+1,y-1))    voisins.add(field[x+1][y-1]);
       if(estValide(x+1,y+1))    voisins.add(field[x+1][y+1]);
       if(estValide(x,y-1))      voisins.add(field[x][y-1]);
       if(estValide(x,y+1))      voisins.add(field[x][y+1]);
       if(estValide(x-1,y))      voisins.add(field[x-1][y]);
       if(estValide(x-1,y-1))    voisins.add(field[x-1][y-1]);
       if(estValide(x-1,y+1))    voisins.add(field[x-1][y+1]);
       return voisins;
    }

    /*Verifie si il reste une case qui n'est pas encore decouverte
    c'est a dire une case qui n'a pas de flag et qu'on peut jouer
    * */
    public boolean game_finish(){
       boolean gameWin = false;
       if(mines_restantes == 0){
           gameWin = true;
           for(Case[] c : field){
               for(Case elem : c){
                   if( (elem.getStatus() == MINE && !elem.getFlag()) //Si une mine n'est pas flagger
                           || elem.estDecouvert == false ) // ou Si un element n'est pas decouvert
                       gameWin = false;
                   break;
               }
           }
       }
        return gameWin;
    }
   ///////////////////////////////////////////////////////////////////////////////////////////////
    private class Case extends JPanel{
        boolean estDecouvert = false;
        int status;
        boolean flaged = false;
        Image icone;
        int x;
        int y;


       @Override
        protected void paintComponent(Graphics g) {
           super.paintComponent(g);
           if(!gameover){
               System.out.println(status);
               if(flaged)            {g.drawImage(grid_icones[FLAG],0,0,this);}
               else if(estDecouvert) {g.drawImage(grid_icones[status],0,0,this);}
               else                  {g.drawImage(grid_icones[COUVERTE],0,0,this);}
           }else{// Si la partie est fini
               if(status != MINE && flaged){g.drawImage(grid_icones[XFLAG],0,0,this);
                   System.out.println("xflag");}
               else if(flaged)             {g.drawImage(grid_icones[FLAG],0,0,this);}
               else                        {g.drawImage(grid_icones[status],0,0,this);}
           }
       }

       public void setStatus(int new_status){
           this.status=new_status;
       }
       public int getStatus(){return this.status;}

       public int getXpos(){return this.x;}
       public int getYpos(){return this.y;}

       public void setFlag(boolean flag){this.flaged = flag;}
       public boolean getFlag(){return this.flaged;}

       public void switchFlag(){
           this.flaged = !flaged;
           if(flaged){ mines_restantes--;}
           else      { mines_restantes++;}
       }
    }


///////////////////////////////////////////////////////////////////////////////////////////////
    class Controller extends MouseAdapter{
       @Override
        public void mousePressed(MouseEvent e) {
          super.mousePressed(e);
           Case current = (Case) e.getSource();
           if(e.getButton() == MouseEvent.BUTTON3){
               System.out.println("droite");
               play(current,true);
           }else{
               play(current,false);
           }
       }
    }
}
