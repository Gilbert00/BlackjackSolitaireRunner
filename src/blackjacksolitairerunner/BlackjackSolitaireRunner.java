/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package blackjacksolitairerunner;

import java.util.*;

/**
 * @author Kemper F.M. 
 * @version 0.6.0
 */

/**
* Карта
*/
class Card {
    String face;
    char color;
    
    Card (String face, char color) {
        this.face = face;
        this.color = color;
    }
    
    @Override
    public String toString() {
        return face + color;
    }
    
}

/**
* Карта с очками
*/
class CardPlay extends Card {
    short point;
    
    CardPlay(String face, char color, short point){
        super(face, color);
        this.point = point;
    }
    
}

/**
* Колода карт
*/
class Pack {
    final static String[] FACES = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    final static char[] COLORS = {'H', 'S', 'D', 'C'};
    final static short[] POINTS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 10, 10, 10};
    final static short FACES_LEN = (short)FACES.length;
    final static short COLORS_LEN = (short)COLORS.length;
    final static short PACK_LEN = (short)(FACES_LEN * COLORS_LEN);
    private ArrayList<CardPlay> initialPack;
    private ArrayList<CardPlay> pack;

/**
* Исходное заполнение колоды карт    
*/
    private void initInitialPack() {
        for (short i=0; i<COLORS_LEN; i++) {
           for (short j=0; j<FACES_LEN; j++) {
                    initialPack.add(new CardPlay(FACES[j], COLORS[i], POINTS[j]) );
           }
        }
    };

/**    
*Является ли карта тузом?    
*/
    static boolean isAce(Card card) {
        return card.face.equals(FACES[0]);
    }

    Pack() {
        this.initialPack = new ArrayList<CardPlay>();
        initInitialPack();  
        pack = (ArrayList<CardPlay>) initialPack.clone();
        // Тасуем колоду
        Collections.shuffle(pack);
    }

    CardPlay getCard(short ind) {
        return pack.get(ind);
    }
}    

/**
* Игровой массив
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
        short len = (short)ceils.length;
        
        for (short i=0; i < len; i++) { 
            if (EMPTY == ceils[i].point) {
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
        short len = (short)ceils.length;

        for (short i=0; i < len; i++) { 
            if (EMPTY == ceils[i].point) {
                count++;
            }
        }
        return count;
    }

/**    
* Является ли ячейка пустой?   
*/
    boolean isEmptyCeil(short ind) {
        return ceils[ind].point == EMPTY;
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
*/
class Field {
    final static short ROWS = 4;
    final static short COLS = 5;
    final static int[] BIG_ROWS = {0, 1};
    final static int[] LOW_ROWS = {2, 3};

    CardPlay cardNone;
    CardPlay[][] field;
    
    Field() {
        cardNone = new CardPlay(" ", ' ', (short)0);
        field = new CardPlay[ROWS][COLS];
        int[] noneRows = {2, 3};
        int[] noneCols = {0, 4};
        for (int i: noneRows) {
            for (int j: noneCols) {
                field[i][j] = cardNone;
            }
        }
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
* Управление игрой
*/
class BlackjackSolitaire {
    final static short GARBAGE_LEN = 4;
    final static short WORKER_LEN = 16;
    final static short PLAY_LEN = WORKER_LEN + GARBAGE_LEN;
    final static short SUM_LEN =  WORKER_LEN + GARBAGE_LEN;
    final static int[][] IND_CALC = {{0,1,2,3,4}, {5,6,7,8,9}, {10,11,12}, {13,14,15},
                                     {1,6,10,13}, {2,7,11,14}, {3,8,12,15}};
    final static int[][] IND_BJ = {{0,5}, {4,9}};
    
    Pack pack;
    Ceils garbageCeils;
    Ceils workerCeils;
    Field fieldBJG;
    
    BlackjackSolitaire() {
        pack = new Pack();
        garbageCeils = new Ceils(GARBAGE_LEN);
        workerCeils = new Ceils(WORKER_LEN);
        fieldBJG = new Field();
    }

/**    
* Основной цикл игры    
*/
    void play(){
        CardPlay card;
        
        for (short i=0; i<=PLAY_LEN; i++) {
            if (i >= WORKER_LEN && workerCeils.isCeilsFull()) {
                fieldBJG.setField(workerCeils);
                outField();
                outGarbage();
                int sum = calcResult(workerCeils);
                outMarks(sum);
                break;
            }
            
            card = pack.getCard(i);
            
            if (! confirmationCard(card)) {
                breakMessage();
                break;
            }
        }
    }

/**    
* Вывод игровых сообщений и запрос места текущей карты    
*/
    boolean confirmationCard(CardPlay card) {
        
        fieldBJG.setField(workerCeils);
        outField();
        outGarbage();
        outCard(card);
        
        int ind = GetCeilInd(card);
        return (ind >= 0);
    }

/**    
* Ввод и анализ места текущей карты    
*/
    int GetCeilInd(CardPlay card) {
        short inp;
        short ind;
        Scanner sc = new Scanner(System.in);
        CardPlay oldCard;
        
        do {
            if (sc.hasNextShort()) {
                inp = sc.nextShort();
                if (inp<1 || inp>SUM_LEN) {
                    System.out.println("Value must be between 1 nad " + SUM_LEN +"inclusive");
                    outCard(card);
                } else if (inp<=WORKER_LEN) {
                    ind = (short) (inp-1);
                    if (workerCeils.setCeil(ind, card)) {
                        return ind;
                    }else{
                        oldCard = workerCeils.getCeil((short) (ind));
                        System.out.println("Already occupied by " + oldCard.toString() + ".  Pick another place.");                        
                        outCard(card);
                    }
                } else {  // Garbage
                    ind = (short) (inp - 1 - WORKER_LEN); 
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
    void outField() {
        for (int i=0; i<Field.ROWS; i++) {
            for (int j=0; j<Field.COLS; j++) {
                System.out.printf("%-8s", fieldBJG.field[i][j].toString());    
            }
            System.out.println("");
        }    
    }

/**    
* Вывод подвала    
*/
    void outGarbage() {
//        int len = garbageCeils.freeCeils();
        int len = garbageCeils.lenCeils();
        short place = WORKER_LEN;
        System.out.print("Discard pile: ");
        for (int i=0; i<len; i++) {
            place++;
            if (garbageCeils.isEmptyCeil((short) i))
                {System.out.printf("%-4d", place);}
            else
                {System.out.printf("%-418s", garbageCeils.getCeil((short) i).toString());}
        }
        System.out.println();
//        System.out.println("Free slots on discard pile:"+ (short)len);    
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
        System.out.print("Current card is " + card.toString() + "; where do you want to place it? ");
    }

/**    
* Вывод сообщения о конце игры    
*/
    void breakMessage() {
        System.out.println("Game over");
    }

/**
* Вывод сообщения о счете игры    
*/
    void outMarks(int marks) {
        System.out.println("Final score: " + marks);        
    }

/**    
* Подсчет баллов игры    
*/
    int calcResult(Ceils workerCeils) {
        int sum = 0;
        for (int[] inds: IND_CALC) {
            sum += markOfLine(workerCeils, inds);        
        }
        
        for (int[] inds: IND_BJ) {
            sum += calcBlackjackMark(workerCeils, inds);
        }
        return sum;
    }

/**    
* Баллы одной линии    
*/
    int markOfLine(Ceils workerCeils, int[] inds){
        return pointToMark(pointsOfLine(workerCeils, inds));       
    }

/**    
* Очки одной линии    
*/
    int pointsOfLine(Ceils workerCeils, int[] inds) {
        final int CRIT_VAL = 11;
        CardPlay card;
        int sum = 0;
        boolean existAce = false;

        for (int ind: inds) {
            card = workerCeils.getCeil((short) ind);
            existAce |= Pack.isAce(card);
            sum += card.point;
        }
        
        if (existAce)
            sum = sum <= CRIT_VAL ? sum+10 : sum;

        return sum;
    }

/**
* Пересчет очков в баллы    
*/
    int pointToMark(int point) {
        if (point==21) {return 7;}
        else if (point==20) {return 5;}
        else if (point==19) {return 4;}
        else if (point==18) {return 3;}
        else if (point==17) {return 2;}
        else if (point<=16) {return 1;}
        else {return 0;}
    }

/**    
* Подсет баллов для Blackjack    
*/
    int calcBlackjackMark(Ceils workerCeils, int[] inds) {
        for (short i=0; i<2; i++) {
            CardPlay card0 = workerCeils.getCeil((short) inds[i]);
            CardPlay card1 = workerCeils.getCeil((short) inds[(short)(1-i)]);
            if (Pack.isAce(card0) && (card1.point==10) && ! Pack.isAce(card1))
                {return 10;}
            else 
                {return markOfLine(workerCeils, inds);}
        }
        return 0;
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
