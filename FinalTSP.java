package finaltsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
import java.awt.Point;
import javax.swing.SwingUtilities;

public class FinalTSP 
{

    public static void main(String[] args) throws FileNotFoundException
    {
        Scanner in = new Scanner(System.in);
        String filename = in.next();
        ArrayList file = Reader(filename); // reads cycledata.txt into arraylist
        
        ArrayList file2 = file; // duplicates the file
        int cycleNum, packages, bartUnits, lisaUnits;
        cycleNum = Integer.parseInt((String)file.get(0)); // finds what cycle
        String comma = ((String) file.get(1)).replace(",", "");
        packages = Integer.parseInt(comma); // finds number of packages
        String bartName = (String) file.get(file.size()-4), lisaName = (String) file.get(file.size()-2);
        if (!(bartName.equals("Bart Complex"))) {
            bartUnits = 0;
        } else {
            bartUnits = Integer.parseInt((String)file.get(file.size()-3)); // finds number of packages for the Bart Complex
        }
        if (!(lisaName.equals("Lisa Complex"))) {
            lisaUnits = 0;
        } else {
            lisaUnits = Integer.parseInt((String)file.get(file.size()-1)); // finds number of packages for the Lisa Complex
        }
        
        file2.remove(0); // removes the cyclenumber
        file2.remove(0); // removes the number of packages
        file2.remove(file2.size()-1); // removes the Bart Complex Statement
        file2.remove(file2.size()-1); // removes number of Bart packages
        file2.remove(file2.size()-1); // removes the Lisa Complex Statement
        file2.remove(file2.size()-1); // removes number of Lisa packages
        file2.add(0, "125s,22a,A"); // Distribution Center
        file2.add(1, "2s,3a,A"); // Bart Complex Added
        file2.add(1, "149s,33a,A"); // Lisa Complex Added
        
        int number_of_addresses; 
        number_of_addresses = file2.size(); // finds number of total addresses
        
        String[] address_array = new String[number_of_addresses]; // creates corresponding array of addresses
        Point[] point_array = new Point[number_of_addresses]; // creates corresponding array of points
        Point[] intersections = new Point[number_of_addresses]; // creates corresponding array of intersections
        for (int i = 0; i < number_of_addresses; i++)
        {   
            // POINTS
            String current = (String) file2.get(i);
            String[] elements = current.split(","); 
            int street = Integer.parseInt(elements[0].substring(0, elements[0].length()-1));
            int avenue = Integer.parseInt(elements[1].substring(0, elements[1].length()-1));
            int house = (int)(elements[2].charAt(0) - 'A');
            int x_value = street;
            int y_value = avenue+house;
            Point final_address = new Point(x_value, y_value);
            
            // FEED
            address_array[i] = current; // populates address array
            point_array[i] = final_address; // populates point array
            
            // INTERSECTIONS
            int mod_number = y_value % 10;
            int y_coor;
            Point intersect;
            if (mod_number < 5) // rounds the y-coordinate up or down
            {
                y_coor = y_value - mod_number;
            }
            else
            {
                y_coor = y_value + (10 - mod_number);
            }
            intersect = new Point(x_value, y_coor); // converts point to intersection
            intersections[i] = intersect; // populates intersections array
        }
        
        Point[] ordered_array = ReorderArray(intersections); // reorders array of intersections
        int[] x_and_y = GetDistance(ordered_array);
        int x_distance = x_and_y[0];
        int y_distance = x_and_y[1];
        int block_distance = x_distance+y_distance;
        
        ArrayList ordered = new ArrayList<Point>(Arrays.asList(ordered_array));
        
        for (int count = 0; count < ordered.size(); count++)
        {
            Point test = (Point)ordered.get(count);
            for (int count2 = count+1; count2 < ordered.size(); count2++) 
            {
                if (test.equals(ordered.get(count2))) 
                {
                    ordered.remove(count2);
                    count2--;
                }
                    
            }
        }
        
        double total_distance_points = 0;
        for (int w = 0; w < ordered_array.length; w++) 
        {
            for (int z = 0; z < intersections.length;z++) {
                if (ordered_array[w].equals(intersections[z])) 
                {
                    double currentdistance = DistanceBetweenPoints(intersections[z], point_array[z]);
                    //System.out.println("I have visited " + address_array[z]);
                    total_distance_points += currentdistance;
                }
            }
        }
        int number_of_trucks = 1;
        double finaldistance = (block_distance+total_distance_points); // in units | 1 unit = 100 feet | 50 units = 1 mile
        double house_distance = total_distance_points; // in units
        double initial_time = 100000;
        double employees_per_truck, block_time, house_time, delivery_time, complex_time;
        int number_of_employees;
        while (true)
        {
            number_of_employees = number_of_trucks*2; // needs to have at least 2 people per truck
            employees_per_truck = number_of_employees/number_of_trucks; // need to find how many employees per truck
            block_time = 0.5*block_distance; // in minutes, multiply by 0.5 because 30 seconds 
            house_time = house_distance * 0.05; // in minutes, multiply by 0.05 because it takes 3 seconds to partially
            delivery_time = (point_array.length)/employees_per_truck; // in minutes
            complex_time = ((bartUnits + lisaUnits)*0.5)/number_of_employees; // in minutes
            initial_time = ((block_time + house_time + delivery_time + complex_time)/60)/number_of_trucks;
            //System.out.println(initial_time);
            if (initial_time > 24) {
                number_of_trucks++;
            } else {
                break;
            }
        }
        int employee_cost;
        if (initial_time <= 8) {
            employee_cost = number_of_employees * (int) initial_time * 30;
        }
        else 
        {
            employee_cost = number_of_employees * 8 * 30 + number_of_employees * ((int)initial_time - 8) * 45;
        }
        double gasCost = (finaldistance/50)*5;
        double rentalCost = (number_of_trucks * 15000);
        System.out.println("It takes " + initial_time + " hours");
        System.out.println("The cost is $" + (employee_cost+gasCost+rentalCost));
        System.out.println(number_of_trucks);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CreateGraph(point_array).setVisible(true);
                
            }
        });
                
    }
    
    public static Point[] ReorderArray(Point[] Points)
    {
       ArrayList<Point> List = new ArrayList<Point>(Arrays.asList(Points));
       int total_distance=0;
       double min_distance = Math.pow(5, 50); // sets minimum distance arbitrarily high
       Point min_Point = List.get(0);
       int min_index = 0;
       Point Start =  List.get(0);
       int n = List.size();
       Point[] PointsArray = new Point[n];
       for (int j=0;j<n;j++)
       {
            min_distance = Math.pow(5, 50);
            for (int i=0;i<List.size();i++)
            {
                if (Start.distance(List.get(i)) < min_distance)
                {
                    min_Point=List.get(i);
                    min_index =i;
                    min_distance = Start.distance(List.get(i));
                }
            }
        List.remove(min_Point);
        PointsArray[j] = min_Point;
        Start = min_Point;
        }
       
        return PointsArray;
    }
    
    public static int[] GetDistance(Point[] PointsArray)
    {
        int total_distance=0;
        int totalx = 0;
        int totaly = 0;
        for (int j=0;j<(PointsArray.length)-1;j++)
        {
            double x_diff =Math.abs(PointsArray[j].getX()-PointsArray[j+1].getX());
            double y_diff =Math.abs(PointsArray[j].getY()- PointsArray[j+1].getY());
            total_distance += x_diff+y_diff;
            totalx = (int)x_diff;
            totaly = (int)y_diff;
            //System.out.println(j + ": " + "(" + PointsArray[j].getX() + "," + PointsArray[j].getY() + ")" + "✓");
        }
        double x_dif =Math.abs(PointsArray[PointsArray.length-1].getX()-PointsArray[0].getX()); 
        double y_dif =Math.abs(PointsArray[PointsArray.length-1].getY()- PointsArray[0].getY());
        total_distance += x_dif+y_dif;
        totalx += x_dif;
        totaly += y_dif;
        int[] output = {totalx, totaly};

        return output;
    }


    
    public static ArrayList Reader(String name) throws FileNotFoundException 
    {
        File filename = new File(name);
        Scanner input = new Scanner(filename);
        ArrayList file = new ArrayList();
        while (input.hasNext())
        {
            file.add(input.nextLine());
        }
        input.close();
        return file;
    }
    
    public static double DistanceBetweenPoints(Point a, Point b) 
    {
       double x_difference = a.getX() - b.getX();
       double y_difference = a.getY() - b.getY();
       double result = Math.sqrt(Math.pow(y_difference,2) + Math.pow(x_difference, 2));
       return result;
    }
    
}





