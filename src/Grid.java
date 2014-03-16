import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Created by MB on 3/12/14.
 */
public class Grid extends JPanel  {

    private final int ROW = 15;
    private final int COL = 15;
    private final int TOTAL_CELLS = 16;
    private final int NB_MINES = 30;


    private final int RIEN = 0;
    private final int MINE = 9;
    private final int COUVERTE = 10;
    private final int FLAG = 11;
    private final int XFLAG = 12;

    private Image[] grid_icones;
    public Case[][] field;
    private JLabel status;
    private int mines_restantes;
    Controller controller;
    boolean gameover = false;
    Random random;

    ArrayList<int[]> listCoord = new ArrayList<int[]>();
    ArrayList<Case> nextToMine = new ArrayList<Case>();
    ArrayList<Case> toVerify   = new ArrayList<Case>();

    boolean firstplay = true;

    int ran_x;
    int ran_y;

    public Grid(JLabel status){

        this.status = status; //reference au jlabel du jframe
        grid_icones = new Image[13]; // nb d'image


        controller = new Controller();
        setLayout(new GridLayout(ROW, COL));

        for(int i=0; i<13; i++){//I:\Dropbox\PROJETS\CSGAME\Minesweeper\img /I/Dropbox/PROJETS/CSGAME/Minesweeper/
            //grid_icones[i] = (new ImageIcon(("img/j"+i+".gif"))).getImage();
            java.net.URL imageUrl = getClass().getResource("img/j"+i+".gif");
            grid_icones[i] = (new ImageIcon(imageUrl)).getImage();
        }
        setDoubleBuffered(true);
        game();
    }

    //Initialise la game
    public void game(){
        removeAll();
        updateUI();
        gameover = false;
        mines_restantes = NB_MINES;

        nextToMine.clear();
        toVerify.clear();

        field=null;
        field = new Case[ROW][COL];

        /* Creer les case dans la field*/
        for(int i=0;i< ROW;i++){
            for(int j=0; j< COL; j++){
                field[i][j] = new Case();
                field[i][j].x = i;
                field[i][j].y = j;
                field[i][j].addMouseListener(controller);
                add(field[i][j]);
            }
        }

/*        for(Case[] cases : field){
            for(Case c : cases){
                System.out.println(c.x+" "+c.y);
                c.voisins = getVoisins(c);
            }
        }
*/

        random = new Random();
        int ran_x,ran_y;
        status.setText(Integer.toString(NB_MINES));
        for(int i=0; i< NB_MINES; i++){ //Placer les mines!
            ran_x = random.nextInt(ROW -1);
            ran_y = random.nextInt(COL -1);
            if  (field[ran_x][ran_y].getStatus()==MINE){i--;} //Si deja une mine on recommence
            else{field[ran_x][ran_y].setStatus(MINE);} // place la mine
        }//Fini placement mine


        calculateIndice(); // Place les indice autour des mines
        //  AI();
    }

    public Grid(Grid toCopy){
        field = new Case[toCopy.ROW][toCopy.COL];
        for(int i=0;i < toCopy.ROW; i++){
            for(int j=0; j< toCopy.COL ; j++){
                field[i][j] = new Case();
                field[i][j].copyCase(toCopy.field[i][j]);
            }
        }
    }

    private void calculateIndice(){
        int cal_status=0;
        for(int i=0; i < ROW; i++){
            for(int j=0; j< COL; j++){
                if(field[i][j].getStatus() != MINE){
                    if(estValide(i+1,j))     cal_status+= (field[i+1][j].getStatus() == MINE) ? 1 : 0; //North
                    if(estValide(i+1,j-1))   cal_status+= (field[i+1][j-1].getStatus()==MINE) ? 1:  0; //North-Ouest
                    if(estValide(i+1,j+1))   cal_status+= field[i+1][j+1].getStatus() == MINE ? 1: 0; //North-Est
                    if(estValide(i,j-1))     cal_status+= field[i][j-1].getStatus() == MINE ? 1: 0;  //Ouest
                    if(estValide(i,j+1))     cal_status+= field[i][j+1].getStatus() == MINE ? 1: 0;  //Est
                    if(estValide(i-1,j))     cal_status+= field[i-1][j].getStatus() == MINE ? 1: 0;  //Sud
                    if(estValide(i-1,j-1))   cal_status+= field[i-1][j-1].getStatus() == MINE ? 1: 0;  //Sud-Ouest
                    if(estValide(i-1,j+1))   cal_status+= field[i-1][j+1].getStatus() == MINE ? 1: 0;  //Sud-Est*/
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



    /*S'occuper de la mecanique d'un coup dans le jeu*/
    public void play(Case current,boolean flag){

        if(flag && current.flaged){
            current.switchFlag();
            current.repaint();
            status.setText(Integer.toString(mines_restantes));
            return;}
        if(!gameover && !(!flag && current.flaged)){ //Si partie pas fini et que la case n'est pas flager
            if(flag && mines_restantes>0 ){
                current.switchFlag();
                status.setText(Integer.toString(mines_restantes));
                gameover =  (gameWin());
            }else{
                current.estDecouvert = true;
                gameover = (current.getStatus() == MINE) || (gameWin());
                if(current.getStatus()==RIEN){
                    ArrayList<Case> voisins =  getVoisins(current,this.field);
                    for(Case v : voisins){
                        play(v,false);
                    }
                }
            }
            current.repaint();
            if(gameover){
                if(gameWin()){status.setText("Game Win!");}
                else             {status.setText("Game Lost!");}

           /* repaint() pour reveler les caes*/
                for(Case[] cases : field){
                    for(Case c : cases){c.repaint();}
                }
            }//fin gameover
        }
    }
    /* Va chercher les voisins immediat et les retourne
     * dans un arraylist */
    public ArrayList<Case> getVoisins(Case c,Case[][] field){
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

    /*Verifie si la game est gagné
     check si tout les flags sont deposer et ensuite si il sont bien deposer
    * */
    public boolean gameWin(){
        boolean gameWin = true;
        if(mines_restantes == 0){
            for(int i=0; i< ROW; i++){
                for(int j =0 ;j< COL ;j++){
                    if(  (field[i][j].getStatus() == MINE && !field[i][j].getFlag()) //Si une mine n'est pas flagger
                      || (field[i][j].estDecouvert == false && !field[i][j].getFlag())){ // ou Si un element n'est pas decouvert
                        gameWin = false;
                        break;
                    }
                }
            }
        }else{gameWin = false;}

        return gameWin;
    }

    public void calculProbabilite(){


       /*
       * copy field
       * pour chaque case de bordure
       *    faire toute les combinaison de flag qui satisfait l'indice
       *    pour chacune de ces combinaison
       *        faire la meme chose pour la bordure suivante( sur la meme field)
       *        place flag appel calculRecurs(field)
       *        si calculRecurs(retourne)vrai alors +1 pour cette case
       *        if(calculRecurs){ +1}
       * */ // cpy field


        for(Case[] cases : field){
            for(Case c : cases){
               if(c.estDecouvert && c.status >RIEN && c.status< MINE) nextToMine.add(c);
            }
        }

          //System.out.print("=="+b.x+" "+b.y+"    sta:"+b.status);

        for(Case[] cases : field){
           for(Case c : cases) {
               if(c.flaged){c.setFlag(false);
                c.repaint();
               }
           }
        }
        mines_restantes=NB_MINES;

        Grid copyGrid = new Grid(this);// un copy de la field pour la nouvelle combinaison
            calculRecurs(copyGrid, 0);

        nextToMine.clear();
        toVerify.clear();
    }

    public boolean calculRecurs(Grid grid,int index){
        /*
        * Okey. Je check le nombre de flag qui doit etre placer autour
        * (1)
        * -status -nbdeFlag = nb de flag restant  a placer
        * -nbVoisinNonDecouvert retourn une liste des cases avec mine possible
        *
        * (2)
        * - trouver combinaison possible qui satisfait la case
        * - pour chaque combinison cree une nouvelle grille et placer la combinaison
        * - appel recurssif sur prochaine case avec cette grille
        *   -si retourne false alors on essai une autre
        *
        * (3) condition d'arret
         * -on est au bout de la liste
         *      -True
         * -checkValidFlag ( sur les case deja combiner) retourn faux
         *      -False

        * */
        //int start = (index == 0)? 0: index-1;
         for(int i =index ; i< toVerify.size(); i++){
            toVerify.remove(nextToMine.get(i));
        }

        if(!checkValidFlag(grid.field,index)){
            System.out.println("ici checkVlidFlag false");

            return false;
        }
        if(index >= nextToMine.size()){
            System.out.println("ici nbbordure > size :"+index);
            return true;
        }
        ArrayList<Case> possibleMine = getVoisinNonDecouvert(nextToMine.get(index),grid.field);



        System.out.print("\n------------------"+possibleMine.size()+"  x:"+nextToMine.get(index).x+"    y:"+nextToMine.get(index).y+
                "  stat:"+nextToMine.get(index).status+"  ind:"+index+"\n");
        ArrayList<int[]> listC = new ArrayList<int[]>();

        ArrayList<Case> voisins = getVoisins(nextToMine.get(index),grid.field);
        int nbFlagAplacer = 0;
        for(int i=0 ;i<voisins.size(); i++){
            if(voisins.get(i).getFlag()){ nbFlagAplacer--;}
        }
        nbFlagAplacer+= nextToMine.get(index).status;

        if(nbFlagAplacer <0){
            System.out.println("Flag negatif");
            return false;
        }

        int[] combinaison = new int[nbFlagAplacer];
        combinaisonFlag(0,nbFlagAplacer,possibleMine.size(),combinaison,listC); // tout les combinaison de flag pour cette case

        // (2)                           //////////////////////////////


        if(nbFlagAplacer == 0){
            System.out.println(nbFlagAplacer);
            toVerify.add(nextToMine.get(index));
            return calculRecurs(grid, index+1);
        }
        for(int[] list : listC){ // Pour chaque combinison trouvé
            Case temp;
            System.out.print("\n------------------"+possibleMine.size()+"  x:"+nextToMine.get(index).x+"    y:"+nextToMine.get(index).y+
                    "  stat:"+nextToMine.get(index).status+"  ind:"+index+"\n");
            for(int i=0; i<nbFlagAplacer ; i++){
                System.out.print(list[i] + " ");
                temp = possibleMine.get(list[i]);
                System.out.println("      ==  x:"+temp.x+"  y:"+temp.y);
                grid.field[temp.x][temp.y].switchFlag(); // placer les flag sur la nouvelle field

            }

            toVerify.add(nextToMine.get(index));
            if(calculRecurs(grid, index+1)){
                if(index ==0){
                    for(Case[] cases: grid.field){
                        for (Case c : cases){
                            if(c.getFlag()){
                                this.field[c.x][c.y].setFlag(true);
                                this.field[c.x][c.y].repaint();
                                mines_restantes--;
                            }
                        }

                    }
                    status.setText(Integer.toString(mines_restantes));

                }
                return true;
            }
            for(int i=0; i<nbFlagAplacer ; i++){
                System.out.print(list[i] + " ");
                temp = possibleMine.get(list[i]);
                System.out.println("      ==  x:"+temp.x+"  y:"+temp.y);
                grid.field[temp.x][temp.y].setFlag(false); // placer les flag sur la nouvelle field
            }
            //start = (index == 0)? 0: index-1;
            toVerify.remove(nextToMine.get(index));
            /*for(int i =index ; i< toVerify.size(); i++){
                toVerify.remove(i);
            /*}
            /*if(index ==0){toVerify.clear();}else{
            toVerify.remove(index);
            }*/
            System.out.println();

        }
        listC.clear();
        voisins.clear();

        //Arrive jusqu'ici si il y a des flag a poser mais peut importe ou il le place
        // Cela cause une erreur plus loin
        /*if(index ==0){// Ici la sollution la plus bs de l'univers si il n'a pas trouver de solution, shuffle et recommence..
            long seed = System.nanoTime();
            Collections.shuffle(nextToMine, new Random(seed));
            toVerify.clear();
            calculRecurs(this,0);
        }*/
        //start = (index == 0)? 0: index-1;
        for(int i = index ; i< toVerify.size(); i++){
            toVerify.remove(nextToMine.get(i));

        }
        return false;


    }

    ///////////////////////////////////////////////////
    public boolean checkValidFlag(Case[][] copyField,int index){
        for(int i=0; i< index ; i++){
            Case c = nextToMine.get(i);
            ArrayList<Case> voisins = getVoisins(copyField[c.x][c.y],copyField);
            int indice=0;
            for(Case v : voisins){
                if(v.flaged) indice++;
            }
         //   System.out.println("x:"+c.x+"  y"+c.y+ "   indice:"+indice + "  status:"+c.getStatus());
            if(indice!= c.getStatus()){
               System.out.println("x:"+c.x+"  y"+c.y+ "   indice:"+indice + "  status:"+c.getStatus() +"   indexV:"+toVerify.indexOf(c) +" indexNTM:"+nextToMine.indexOf(c));
               voisins.clear();
                voisins=null;

                return false;}
        }
         /*
        for(Case c : toVerify){
            ArrayList<Case> voisins = getVoisins(copyField[c.x][c.y],copyField);
            int indice=0;
            for(Case v : voisins){
                if(v.flaged) indice++;
            }
         //   System.out.println("x:"+c.x+"  y"+c.y+ "   indice:"+indice + "  status:"+c.getStatus());
            if(indice!= c.getStatus()){
               System.out.println("x:"+c.x+"  y"+c.y+ "   indice:"+indice + "  status:"+c.getStatus() +"   indexV:"+toVerify.indexOf(c) +" indexNTM:"+nextToMine.indexOf(c));
               voisins.clear();
                voisins=null;

                return false;}
        }
        */
        return true;
    }
    ///////////////////////////////////////////////////
    public ArrayList<Case> getVoisinNonDecouvert(Case c ,Case[][]field){
        ArrayList<Case> vInconnu = new ArrayList<Case>();
        if(estValide(c.x+1,c.y) && !field[c.x+1][c.y].estDecouvert && !field[c.x+1][c.y].flaged) vInconnu.add(field[c.x+1][c.y]);
        if(estValide(c.x+1,c.y-1) && !field[c.x+1][c.y-1].estDecouvert && !field[c.x+1][c.y-1].flaged) vInconnu.add(field[c.x+1][c.y-1]);
        if(estValide(c.x+1,c.y+1) && !field[c.x+1][c.y+1].estDecouvert && !field[c.x+1][c.y+1].flaged) vInconnu.add(field[c.x+1][c.y+1]);
        if(estValide(c.x,c.y-1) && !field[c.x][c.y-1].estDecouvert && !field[c.x][c.y-1].flaged) vInconnu.add(field[c.x][c.y-1]);
        if(estValide(c.x,c.y+1) && !field[c.x][c.y+1].estDecouvert && !field[c.x][c.y+1].flaged) vInconnu.add(field[c.x][c.y+1]);
        if(estValide(c.x-1,c.y) && !field[c.x-1][c.y].estDecouvert && !field[c.x-1][c.y].flaged) vInconnu.add(field[c.x-1][c.y]);
        if(estValide(c.x-1,c.y-1) && !field[c.x-1][c.y-1].estDecouvert && !field[c.x-1][c.y-1].flaged) vInconnu.add(field[c.x-1][c.y-1]);
        if(estValide(c.x-1,c.y+1) && !field[c.x-1][c.y+1].estDecouvert && !field[c.x-1][c.y+1].flaged) vInconnu.add(field[c.x-1][c.y+1]);

        return vInconnu;
    }
    ///////////////////////////////////////////////////
    public void AI(){
        final Timer timer = new Timer(400,null);
        //http://stackoverflow.com/questions/3858920/stop-a-swing-timer-from-inside-the-action-listener
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Case> caseToPlay = new ArrayList<Case>();

                if(firstplay){
                    int ran_x = random.nextInt(ROW -1);
                    int ran_y = random.nextInt(COL -1);

                    play(field[ran_x][ran_y],false);
                    firstplay=false;
                }else{

                    Case caseToPlays = caseToPlay();
                    play(caseToPlays,false);
                    calculProbabilite();

                }

                System.out.println("joue encore");
                if(gameover){
                        if(gameWin()){status.setText("Game Win!");}
                        else             {status.setText("Game Lost!");}

           /* repaint() pour reveler les caes*/
                        for(Case[] cases : field){
                            for(Case c : cases){c.repaint();}
                        }
                    System.out.println("FINI");
                    timer.stop();
                }

                //partir des cases non-null (qui on un indice) et prendre leurs voisins non decouvert


                //check les combinaison de flag qui satisfait l'indice

                //
            }
        });
        timer.start();
    }


    public Case caseToPlay(){
        Case play =null;
        //ArrayList<Case> bordures = new ArrayList<Case>();
        for(int i =0; i<ROW; i++){
            for(int j=0; j<COL ; j++){
               if(field[i][j].estDecouvert ){
                   if(indiceEgalFlag(field[i][j])){
                      ArrayList<Case> voisins = getVoisins(field[i][j],field);
                       for(Case voisin:voisins){
                           if(!voisin.estDecouvert && !voisin.flaged){
                               if(play!=null && play.riskProbability > (field[i][j].status*2.5) -getNbVoisinSafe(voisin) ){
                                   play=voisin;
                                   play.riskProbability = (field[i][j].status*2.5) - getNbVoisinSafe(voisin);
                               }else if(play==null){
                                   play= voisin;
                                   play.riskProbability = (field[i][j].status* 2.5) - getNbVoisinSafe(voisin);
                               }
                           }
                       }
                   }/*else{

                       if(play!= null && getNbVoisinSafe(play) < getNbVoisinSafe(field[i][j])){
                           play=field[i][j];
                       }else if(play==null) {
                           play=field[i][j];
                       }*/
               }
            }
        }
        if(play!=null)
            return play;

        ran_x = random.nextInt(ROW-1);
        ran_y = random.nextInt(COL - 1);

        return field[ran_x][ran_y];
    }


    public boolean indiceEgalFlag(Case c){
        ArrayList<Case> voisins = getVoisins(c,field);
        int nbVoisinFlag = 0;
        for(Case voisin : voisins){
           if(voisin.flaged){
               nbVoisinFlag++;
           }
        }

        return (nbVoisinFlag == c.getStatus());
    }
    public int getNbVoisinSafe(Case c){
        int nbVoisinSafe =0;

       // if(indiceEgalFlag(c)){return 10;}
        ArrayList<Case> voisins = getVoisins(c,field);
        for(Case voisin : voisins){
            if(voisin.estDecouvert ){
                nbVoisinSafe++;
            }
        }

        return nbVoisinSafe;
    }
    /*Verifie si cet case est peut etre une mine
    * Elle regarde si elle a une case avec un indice a cote
    * */
    public boolean canBeMine(Case c){
        boolean canBeMine = false;

        ArrayList<Case> voisins = getVoisins(c,field);
        for(Case c_voisin : voisins ){
           if(c_voisin.getStatus()>RIEN && c_voisin.getStatus()<MINE){
               return true;
           }
        }

        return canBeMine;
    }
    ///////////////////////////////////////////////////
    public void combinaisonFlag(int index,int nbFlag, int nbCase,int[] combinaison, ArrayList<int[]> listeC){
        if(nbFlag ==0){return;}
        if(index >= nbFlag){

            int[] newCombinaison = combinaison.clone();
             listeC.add(newCombinaison);
            return ;
        }
        int start =0;
        if(index >0) start = combinaison[index-1]+1;
        for(int i=start; i<nbCase;i++){
            combinaison[index]=i;
            combinaisonFlag(index+1, nbFlag, nbCase, combinaison, listeC);
        }
    }
    ///////////////////////////////////////  CLASSE CASE  //////////////////////////////////////////////
    public class Case extends JPanel{
        boolean estDecouvert = false;
        int status;
        boolean flaged = false;
        Image icone;

        double riskProbability;
        int x;
        int y;
        ArrayList<Case> voisins;


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if(!gameover){ // Si patie en cours
                if(flaged)            {g.drawImage(grid_icones[FLAG],0,0,this);}
                else if(estDecouvert) {g.drawImage(grid_icones[status],0,0,this);}
                else                  {g.drawImage(grid_icones[COUVERTE],0,0,this);}


            }else{// Si la partie est fini
                if(status != MINE && flaged){g.drawImage(grid_icones[XFLAG],0,0,this);}
                else if(flaged)             {g.drawImage(grid_icones[FLAG],0,0,this);}
                else                        {g.drawImage(grid_icones[status],0,0,this);}
            }
        }


        /*Setter getter -------------------------*/
        public void copyCase(Case c){
            this.x = c.x;
            this.y= c.y;
            this.estDecouvert = c.estDecouvert;
            this.status = c.status;
            this.flaged = c.flaged;
        }
        public void setStatus(int new_status){ this.status=new_status; }
        public int getStatus(){return this.status;}
        public int getXpos(){return this.x;} public int getYpos(){return this.y;}
        public void setFlag(boolean flag){this.flaged = flag;}
        public boolean getFlag(){return this.flaged;}
        public void switchFlag(){
            this.flaged = !flaged;
            if(flaged){ mines_restantes--;}
            else      { mines_restantes++;}
        }
        //////////////////////////////////////

    }
    /////////////////////////////////// CLASSE CONTROLLER UTILISATEUR///////////////////////////////////////////////
    class Controller extends MouseAdapter{
        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            Case current = (Case) e.getSource();
            if(e.getButton() == MouseEvent.BUTTON3){
                play(current,true);
            }else{
                play(current,false);
            }
        }
    }
}
