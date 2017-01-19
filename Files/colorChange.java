
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author xxjstudxx
 */
public class colorChange extends JFrame{
    colorButton[] sqr;
    String winColor;
    boolean isWon = false;

    public colorChange(int width, int height, String difficulty) {
        Random rand = new Random();
        
        GridLayout easy = new GridLayout(3,3);
        GridLayout med = new GridLayout (4,4);
        GridLayout hard = new GridLayout (5,5);
        this.setSize(width, height);
        
        switch(difficulty) {
            case "easy":
                this.setLayout(easy);;
                sqr = new colorButton[9];
                break;
            case "med":
                this.setLayout(med);
                sqr = new colorButton[16];
                break;
            case "hard":
                this.setLayout(hard);
                sqr = new colorButton[25];
                break;
            default:
                this.setLayout(easy);
                sqr = new colorButton[9];
                break;
        }
        int btmLftCorner = (int) (3 * Math.sqrt(sqr.length));
        for (int i = 0; i < sqr.length; i++) {
            sqr[i] = new colorButton();
            sqr[i].index = i;
            sqr[i].borders = borderBoxes(i);
            sqr[i].setAction(changeColor);
            if (i == btmLftCorner) {
                sqr[i].setEnabled(false);
            }
            
            int clrPick = rand.nextInt(6);
            switch (clrPick) {
                case 0:
                    sqr[i].setBackground(Color.RED);
                    sqr[i].color = "red";
                    break;
                case 1:
                    sqr[i].setBackground(Color.BLUE);
                    sqr[i].color = "blue";
                    break;
                case 2:
                    sqr[i].setBackground(Color.GREEN);
                    sqr[i].color = "green";
                    break;
                case 3:
                    sqr[i].setBackground(Color.ORANGE);
                    sqr[i].color = "orange";
                    break;
                case 4:
                    sqr[i].setBackground(Color.YELLOW);
                    sqr[i].color = "yellow";
                    break;
                case 5:
                    sqr[i].setBackground(Color.WHITE);
                    sqr[i].color = "white";
                    break;
                default:
                    sqr[i].setBackground(Color.PINK);
                    sqr[i].color = "pink";
                    break;
            }
            sqr[i].setVisible(true);
            this.add(sqr[i]);
        }
        winColor = sqr[btmLftCorner].color;
        
        this.setTitle(winColor.toUpperCase());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    /**
     * @param args the command line arguments
     */
    Action changeColor = new AbstractAction("") {
        @Override
        public void actionPerformed(ActionEvent e) {
            colorButton source = (colorButton) e.getSource();
            String sColor = source.color;
            int index = source.index;
            
            switch(sColor) {
                case "red":
                    sqr[index].setBackground(Color.BLUE);
                    brdrClrChange(sColor, source.borders, index, "blue");
                    sqr[index].color = "blue";
                    break;
                case "blue":
                    sqr[index].setBackground(Color.GREEN);
                    brdrClrChange(sColor, source.borders, index, "green");
                    sqr[index].color = "green";
                    break;
                case "green":
                    sqr[index].setBackground(Color.ORANGE);
                    brdrClrChange(sColor, source.borders, index, "orange");
                    sqr[index].color = "orange";
                    break;
                case "orange":
                    sqr[index].setBackground(Color.YELLOW);
                    brdrClrChange(sColor, source.borders, index, "yellow");
                    sqr[index].color = "yellow";
                    break;
                case "yellow":
                    sqr[index].setBackground(Color.WHITE);
                    brdrClrChange(sColor, source.borders, index, "white");
                    sqr[index].color = "white";
                    break;
                case "white":
                    sqr[index].setBackground(Color.RED);
                    brdrClrChange(sColor, source.borders, index, "red");
                    sqr[index].color = "red";
                    break;
                case "pink":
                    sqr[index].setBackground(Color.RED);
                    brdrClrChange(sColor, source.borders, index, "red");
                    sqr[index].color = "red";
                    break;
                default:
                    sqr[index].setBackground(Color.RED);
                    brdrClrChange(sColor, source.borders, index, "red");
                    sqr[index].color = "red";
                    break;
            }
                        
            if(checkWin(winColor)) {
                isWon = true;
               
            }
            
        }
    };
    
    public void brdrClrChange(String clr, int[] brdrs, int orig, String newClr) {
        int btmLftCorner = (int) (3 * Math.sqrt(sqr.length));
        for(int i = 0; i < brdrs.length; i++) {
            if (sqr[brdrs[i]].color.equals(clr) && brdrs[i] != btmLftCorner) {
                sqr[brdrs[i]].setBackground(sqr[orig].getBackground());
                sqr[brdrs[i]].color = newClr;
            }
        }
    }
    
    public boolean checkWin(String goal) {
        for (int i = 0; i < sqr.length; i++) {
            if (!goal.equals(sqr[i].color))
                return false;
        }
        return true;
    }
    
    public int[] borderBoxes(int i) {
        int[] brdr;
        int width = (int) Math.sqrt(sqr.length);
        int btmLftCorner = (int) (3 * Math.sqrt(sqr.length));
        
        if (i == 0 || i == sqr.length - 1 || i == width - 1 || i == btmLftCorner) { //corners
            brdr = new int[2]; //two boxes border this square 
            if (i == 0) {
                brdr[0] = 1;
                brdr[1] = width;
            }
            if (i == sqr.length - 1) {
                brdr[0] = sqr.length - 2;
                brdr[1] = sqr.length - 1 - width;
            }
            if (i == width - 1) {
                brdr[0] = width - 2;
                brdr[1] = width - 1 + width;
            }
            if (i == sqr.length + 1 - width) {
                brdr[0] = sqr.length + 1 - (2 * width);
                brdr[1] = sqr.length + 2 - width;
            }
        } 
        else if (i > 0 && i < width - 1) {  //top row (excluding corners)
            brdr = new int [3]; //three boxes border this square
            brdr[0] = i - 1;
            brdr[1] = i + 1;
            brdr[2] = i + width;
        }
        else if  (i < sqr.length && i > sqr.length + 1 - width) { //bottom row (excluding corners)
            brdr = new int[3];
            brdr[0] = i - 1;
            brdr[1] = i + 1;
            brdr[2] = i - width;
        }
        else if (i == width || i == width * 2 || i == width * 3) { //left edge (excluding corners)
            brdr = new int [3];
            brdr[0] = i - width;
            brdr[1] = i + width;
            brdr[2] = i + 1;
        }
        else if (i == (2 * width) - 1 || i == (3 * width) - 1 || i == (4 * width) - 1) { //right edge(excluding corners)
            brdr = new int[3];
            brdr[0] = i - width;
            brdr[1] = i + width;
            brdr[2] = i - 1;
        }
        else {
            brdr = new int[4];
            brdr[0] = i - 1;
            brdr[1] = i + 1;
            brdr[2] = i - width;
            brdr[3] = i + width;
        }
        return brdr;
    }
    
    public class colorButton extends JButton {
        String color;
        int index;
        int[] borders;
    }

}
