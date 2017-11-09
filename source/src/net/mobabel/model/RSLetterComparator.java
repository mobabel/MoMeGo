package net.mobabel.model;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import javax.microedition.rms.RecordComparator;

/** RMS Comparator*/
public class RSLetterComparator implements RecordComparator{
    
	private ByteArrayInputStream stream;
    private DataInputStream reader;

    public int compare(byte[] rec1, byte[] rec2) {
        String s1 = "", s2 = "";
        try {
            // Compares records, sorts them
            // in descending order

            //Retrieve ExpenseDate from the first record
            stream = new ByteArrayInputStream(rec1);
            reader = new DataInputStream(stream);
    		try{

    			//s1 = records[2].toLowerCase();
    		}catch (Exception e) { 
                //#debug error
                System.out.println("RSLetterComparator Exception:" + e.toString());
    		}

            //Retrieve ExpenseDate from the second record
            stream = new ByteArrayInputStream(rec2);
            reader = new DataInputStream(stream);
            try{

    			//s2 = records[2].toLowerCase();
    		}catch (Exception e) { 
                //#debug error
                System.out.println("RSLetterComparator Exception:" + e.toString());
    		}
        }
        catch (Exception e) {
            //#debug error
            System.out.println("RSLetterComparator Exception:" + e.toString());
        }
        finally {
            if (s1.compareTo(s2)==0) {
                return RecordComparator.EQUIVALENT;
            } 
            else if (s1.compareTo(s2)>0) {
                return RecordComparator.PRECEDES;
            } 
            else {
                return RecordComparator.FOLLOWS;
            }
        }
    }
}