/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blackjacksolitairerunner;

import java.util.*;

/**
* Карта
* @author Kemper F.M. 
*/
class Card {
    private String face;
    private char color;
    
    Card (String face, char color) {
        this.face = face;
        this.color = color;
    }
    
    String getFace(){
        return face;
    }
    
    char getColor() {
        return color;
    }
    
    @Override
    public String toString() {
        return face + color;
    }
    
}

/**
* Карта с очками
* @author Kemper F.M. 
*/
class CardPlay extends Card {
    private short point;
    
    CardPlay(String face, char color, short point){
        super(face, color);
        this.point = point;
    }
    
    short getPoint() {
        return this.point;
    }
}

/**
* Колода карт
* @author Kemper F.M.  
*/
class Pack {
    private final static String[] FACES = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private final static char[] COLORS = {'H', 'S', 'D', 'C'};
    private final static short[] POINTS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};
    private final static short FACES_LEN = (short)FACES.length;
    private final static short COLORS_LEN = (short)COLORS.length;
    private final static short PACK_LEN = (short)(FACES_LEN * COLORS_LEN);
    private ArrayList<CardPlay> initialPack;
    //тасованная колода карт
    private ArrayList<CardPlay> pack;

/**
* Исходное заполнение колоды карт    
*/
    private void initInitialPack() {
        for (short i=0; i<COLORS_LEN; i++) {
           for (short j=0; j<FACES_LEN; j++) {
                    this.initialPack.add(new CardPlay(FACES[j], COLORS[i], POINTS[j]) );
           }
        }
    };

/**    
*Является ли карта тузом?    
*/
    static boolean isAce(Card card) {
        return card.getFace().equals(FACES[0]);
    }

    Pack() {
        this.initialPack = new ArrayList<CardPlay>();
        initInitialPack();  
 //       setTestPack();
        this.pack = (ArrayList<CardPlay>) this.initialPack.clone();
        // Тасуем колоду
        Collections.shuffle(this.pack);
    }

    CardPlay getCard(short ind) {
        return this.pack.get(ind);
    }
    
    private void setTestPack() {
        String[] testCard = {"JH","8H","5C","2C","QS","AC","3S","3D","9S","JS",
                             "8S","4D","9C","AH","10S","8D","6H","4H","4C","AS"};
        String face;
        char color;
        short point;
        
        this.pack = new ArrayList<CardPlay>();
        
        for (String card : testCard) {
            face = card.toUpperCase().substring(0, card.length()-1);
            color = card.toUpperCase().charAt(card.length()-1);
            switch (face) {
                case "A":
                    point = 1;
                    break;
                case "J":
                case "Q":
                case "K":
                    point = 10;
                    break;
                default:        
                    point = (short)(int)Integer.parseInt(face);
            }
            this.pack.add(new CardPlay(face, color, point));
        }
    }
}    

/**
* Игровой массив
* @author Kemper F.M.  
*/
class Ceils{
    private final static short EMPTY = -1;
    
    private final CardPlay[] ceils;
    
    Ceils(short len) {
        ceils = new CardPlay[len];
        for (short i=0; i < len; i++) { 
            ceils[i] = new CardPlay("" + (i + 1), ' ', EMPTY);
        }
    };
     
    CardPlay getCeil(short ind) {
        return ceils[ind];
    };
    
    boolean setCeil(short ind, CardPlay card) {
        if (isEmptyCeil(ind)) {
            ceils[ind] = card;
            return true;
        }
        else {
            return false;
        }
    };

/**    
* Проверка, заполнен ли полностью игровой массив    
*/
    boolean isCeilsFull() {
        boolean flag = true;
        
        for (CardPlay card : ceils) { 
            if (EMPTY == card.getPoint()) {
                flag = false;
                break;
            }
        }
        return flag;
    }

/**    
* Подсчет пустых ячеек в игровом массиве    
*/
    short freeCeils() {
        short count = 0;

        for (CardPlay card : ceils) { 
            if (EMPTY == card.getPoint()) {
                count++;
            }
        }
        return count;
    }

/**    
* Является ли ячейка пустой?   
*/
    boolean isEmptyCeil(short ind) {
        return ceils[ind].getPoint() == EMPTY;
    };

/**    
* Является ли ячейка пустой?   
*/
    boolean isEmptyCeil(CardPlay ceil) {
        return ceil.getPoint() == EMPTY;
    };

    
/**    
* Длина игрового массива    
*/
    int lenCeils() {
        return ceils.length;
    }
}

/**
* Матрица игрового поля
* @author Kemper F.M.  
*/
class Field {
    final static short ROWS = 4;
    final static short COLS = 5;
    final static int[] BIG_ROWS = {0, 1};
    final static int[] LOW_ROWS = {2, 3};

    private CardPlay cardNone;
    private CardPlay[][] field;
    
    Field() {
        cardNone = new CardPlay(" ", ' ', (short)0);
        field = new CardPlay[ROWS][COLS];
        int[] noneRows = LOW_ROWS;
        int[] noneCols = {0, COLS-1};
        for (int i: noneRows) {
            for (int j: noneCols) {
                field[i][j] = cardNone;
            }
        }
    }

    CardPlay getField(int i, int j) {
        return field[i][j];
    }
    
/**
* Заполение игрового поля    
*/
    void setField(Ceils ceils) {
        short k = 0;
        for (int i: BIG_ROWS) {
            for (int j=0; j<COLS; j++) {
                field[i][j] = ceils.getCeil(k);
                k++;
            }
        }
        for (int i: LOW_ROWS) {
            for (int j=1; j<COLS-1; j++) {
                field[i][j] = ceils.getCeil(k);
                k++;
            }
        }
    }
    
}

/**
 * Рассчет баллов игры в BlackJack
 * @author Kemper F.M.
 */
class CalcPlayBJ{ 
 //   final static int[][] IND_CALC = {{0,1,2,3,4}, {5,6,7,8,9}, {10,11,12}, {13,14,15},
 //                                    {1,6,10,13}, {2,7,11,14}, {3,8,12,15}};
 //   final static int[][] IND_BJ = {{0,5}, {4,9}};
    private int[][] indCalc; // = IND_CALC;
    private int[][] indBj; // = IND_BJ;
/**    
* Подсчет баллов игры    
*/
    int calcResult(Ceils workerCeils) {
        genLinesInds(Field.ROWS, Field.COLS, Field.LOW_ROWS.length);
        int sum = 0;
        for (int[] inds: indCalc) {
            sum += markOfLine(workerCeils, inds);        
        }
        
        for (int[] inds: indBj) {
            sum += calcBlackjackMark(workerCeils, inds);
        }
        return sum;
    }

/**
 * генерация массивов индексов линий
 */    
    private void genLinesInds(int rows, int cols, int lowRows) {
//        int rows = Field.ROWS;
//        int cols = Field.COLS;
//        int lowRows = Field.LOW_ROWS.length;
        int bigRows = rows - lowRows;
        int[] colsBj = {0, cols-1}; 
        int lenColsBj = colsBj.length;
        int lowCols = cols - lenColsBj;
        int lenCalc = rows + lowCols;
        indCalc = new int[lenCalc][];
        indBj = new int[lenColsBj][];
//Big rows
        int k = 0;        
        int ind = 0;
        for (int i=0; i<bigRows; i++) {
            int[] row = new int[cols];
            for (int j=0; j<cols; j++) {
                row[j] = ind;
                ind++;
            }
            indCalc[k] = row;
            k++;
        }
//Low rows
        for (int i=0; i<lowRows; i++) {
            int[] row = new int[lowCols];
            for (int j=0; j<lowCols; j++) {
                row[j] = ind;
                ind++;
            }
            indCalc[k] = row;
            k++;
        }
//Cols
        int delta = 0;
        for (int i=0; i<lowCols; i++) {
            ind = i+1;
            int[] col = new int[rows];
            for (int j=0; j<rows; j++) {
                col[j] = ind;
                if (j < bigRows-1) {
                    delta = cols;
                } else if (j == bigRows-1) {
                    delta = cols-1;
                } else {  // j >= bigRows 
                    delta = lowCols;
                }
                ind += delta;
            }    
            indCalc[k] = col;
            k++;
        }
//Bj cols        
        k = 0;
        for (int i: colsBj) { 
            ind = i;
            int[] col = new int[bigRows];
            for (int j=0; j<bigRows; j++) {
                col[j] = ind;
                ind += cols;
            }    
            indBj[k] = col;
            k++;
        }
    }
    
/**    
* Баллы одной линии    
*/
    private int markOfLine(Ceils workerCeils, int[] inds){
        return pointToMark(pointsOfLine(workerCeils, inds));       
    }

/**    
* Очки одной линии    
*/
    private int pointsOfLine(Ceils workerCeils, int[] inds) {
        final int CRIT_VAL = 11;
        CardPlay card;
        int sum = 0;
        boolean existAce = false;

        for (int ind: inds) {
            card = workerCeils.getCeil((short) ind);
            existAce |= Pack.isAce(card);
            sum += card.getPoint();
        }
        
        if (existAce)
            sum = sum <= CRIT_VAL ? sum+10 : sum;

        return sum;
    }

/**
* Пересчет очков в баллы    
*/
    private int pointToMark(int point) {
        if (point==21) {return 7;}
        else if (point==20) {return 5;}
        else if (point==19) {return 4;}
        else if (point==18) {return 3;}
        else if (point==17) {return 2;}
        else if (point<=16) {return 1;}
        else {return 0;}
    }

/**    
* Подсчет баллов для Blackjack    
*/
    private int calcBlackjackMark(Ceils workerCeils, int[] inds) {
        for (short i=0; i<2; i++) {
            CardPlay card0 = workerCeils.getCeil((short) inds[i]);
            CardPlay card1 = workerCeils.getCeil((short) inds[(short)(1-i)]);
            if (Pack.isAce(card0) && (card1.getPoint()==10) && ! Pack.isAce(card1))
                {return 10;}
        }
        return markOfLine(workerCeils, inds);
    }
    
}

/**
 * Игровой стол
 * @author Kemper F.M.
 */
class PlayDesk {

/**    
* Ввод и анализ места текущей карты    
*/
    int getCeilInd(CardPlay card, Ceils workerCeils, Ceils garbageCeils, short workerLen, short sumLen) {
        short inp;
        short ind;
        Scanner sc = new Scanner(System.in);
        CardPlay oldCard;
        
        do {
            if (sc.hasNextShort()) {
                inp = sc.nextShort();
                if (inp<1 || inp>sumLen) {
                    System.out.println("Value must be between 1 and " + sumLen +" inclusive");
                    outCard(card);
                } else if (inp<=workerLen) {
                    ind = (short) (inp-1);
                    if (workerCeils.setCeil(ind, card)) {
                        return ind;
                    }else{
                        oldCard = workerCeils.getCeil((short) (ind));
                        System.out.println("Already occupied by " + oldCard.toString() + ".  Pick another place.");                        
                        outCard(card);
                    }
                } else {  // Garbage
                    ind = (short) (inp - 1 - workerLen); 
                    if (garbageCeils.setCeil(ind, card)) {
                        return inp-1;                        
                    }else{
                        oldCard = garbageCeils.getCeil((short) (ind));
                        System.out.println("Already occupied by " + oldCard.toString() + ".  Pick another place.");                        
                        outCard(card);
                    }
                }
            } else {
                return -1;
            }
        }while(true);
    };

/**    
* Вывод игрового поля    
*/
    void outField(Field fieldBJS) {
        for (int i=0; i<Field.ROWS; i++) {
            for (int j=0; j<Field.COLS; j++) {
                System.out.printf("%-8s", fieldBJS.getField(i, j).toString());    
            }
            System.out.println("");
        }    
    }

/**    
* Вывод подвала    
*/
    void outGarbage(Ceils garbageCeils, short base) {
        int len = garbageCeils.lenCeils();
        short place = base;
        System.out.print("Discard pile: ");
        for (int i=0; i<len; i++) {
            place++;
            if (garbageCeils.isEmptyCeil((short) i))
                {System.out.printf("%-4d", place);}
            else
                {System.out.printf("%-4s", garbageCeils.getCeil((short) i).toString());}
        }
        System.out.println();
    }

/**
* Вывод запроса о текущей карте    
*/
    void outCard(CardPlay card){
        String s = "";
        for (int i = 0; i < 40; i++) {
            s += "*";
        }
        System.out.println(s);
        System.out.print("Current card is " + card + "; where do you want to place it? ");
    }

/**    
* Вывод сообщения о конце игры    
*/
    void breakMessage() {
        System.out.println("Game over!");
    }

/**
* Вывод сообщения о счете игры    
*/
    void outMarks(int marks) {
        System.out.println("Final score: " + marks);        
    }
    
}

/**
* Управление игрой
* @author Kemper F.M.  
*/
class BlackjackSolitaire {
    final static short GARBAGE_LEN = 4;
    final static short WORKER_LEN = 16;
    final static short PLAY_LEN = WORKER_LEN + GARBAGE_LEN;
    final static short SUM_LEN =  WORKER_LEN + GARBAGE_LEN;
    
    Pack pack;
    Ceils garbageCeils;
    Ceils workerCeils;
    Field fieldBJS;
    PlayDesk playDesk;
    
    BlackjackSolitaire() {
        pack = new Pack();
        garbageCeils = new Ceils(GARBAGE_LEN);
        workerCeils = new Ceils(WORKER_LEN);
        fieldBJS = new Field();
        playDesk = new PlayDesk();
    }

/**    
* Основной цикл игры    
*/
    void play(){
        CardPlay card;
        CalcPlayBJ calc = new CalcPlayBJ();
                
        for (short i=0; i<=PLAY_LEN; i++) {
            if (i >= WORKER_LEN && workerCeils.isCeilsFull()) {
                fieldBJS.setField(workerCeils);
                playDesk.outField(fieldBJS);
                playDesk.outGarbage(garbageCeils, WORKER_LEN);
                int sum = calc.calcResult(workerCeils);
                playDesk.outMarks(sum);
                break;
            }
            
            card = pack.getCard(i);
            
            if (! confirmationCard(card)) {
                playDesk.breakMessage();
                break;
            }
        }
    }

/**    
* Вывод игровых сообщений и запрос места текущей карты    
*/
    private boolean confirmationCard(CardPlay card) {
        
        fieldBJS.setField(workerCeils);
        playDesk.outField(fieldBJS);
        playDesk.outGarbage(garbageCeils, WORKER_LEN);
        playDesk.outCard(card);
        
        int ind = playDesk.getCeilInd(card, workerCeils, garbageCeils, WORKER_LEN, SUM_LEN);
        return (ind >= 0);
    }

}    

/**
* Запуск игры
*/
public class BlackjackSolitaireRunner {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BlackjackSolitaire bjs = new BlackjackSolitaire();
        bjs.play();
    }
    
}
