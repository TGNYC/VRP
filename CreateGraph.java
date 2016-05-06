package finaltsp;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import java.awt.Point;
import java.awt.*;

public class CreateGraph extends JFrame 
{
    public static Point[] Points;

    public CreateGraph(Point[] P) 
    {
        super("Homerville");
        
        setSize(1000,1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setDefaultLookAndFeelDecorated(true);
        
       Points = new Point[P.length];
       Points = P.clone();
    }
    
    public void paint(Graphics g) 
    {
       super.paint(g);
       Graphics2D g2d = (Graphics2D) g;
       //creating coordinate system
       for (int j=0; j<1000;j+=20)
       {
            g2d.drawLine(0, j, 1000, j);
       }
       for (int k=0; k<1000;k+=4)
       {
            g2d.drawLine(k, 0,k , 1000);
       } ///draw points

       for (int i=0; i<Points.length ;i++)
       {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(4));
        
        ///delay points
        try{
          Thread.sleep(5);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          
        }
        
      
            g2d.drawLine((int)Points[i].getX()*4, 1000-(int)Points[i].getY()*20,(int) Points[i].getX()*4, 1000-(int)Points[i].getY()*20);
       }
 
        g2d.setStroke(new BasicStroke(2));   
        //Bart Complex
        g2d.setColor(Color.BLUE);
        g2d.drawRect(2*4,1000-3*20,4,20 );
        //Lisa Complex
        g2d.setColor(Color.magenta);
        g2d.drawRect(149*4, 1000-33*20, 4,20);
        //Distribution Center
        g2d.setColor(Color.RED);
        g2d.drawRect(125*4,1000-22*20,4,20);
    }
}



