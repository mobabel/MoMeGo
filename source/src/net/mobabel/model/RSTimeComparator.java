package net.mobabel.model;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import javax.microedition.rms.RecordComparator;

/** RMS Comparator*/
public class RSTimeComparator implements RecordComparator{
    
	private ByteArrayInputStream stream;
    private DataInputStream reader;

    public int compare(byte[] rec1, byte[] rec2) {
        long d1 = 0, d2 = 0;
        try {
            // Compares records, sorts them
            // in descending order

            //Retrieve ExpenseDate from the first record
            stream = new ByteArrayInputStream(rec1);
            reader = new DataInputStream(stream);
            //Get our date from the byte stream
            //in "number of milliseconds in epoch" format
    		try{
				reader.readUnsignedByte();
				reader.readUTF();
				d1 = reader.readLong();
    		}catch (Exception e) { 
                //#debug error
                System.out.println("RSTimeComparator Exception:" + e.toString());
    		}

            //Retrieve ExpenseDate from the second record
            stream = new ByteArrayInputStream(rec2);
            reader = new DataInputStream(stream);
            //Get our date from the byte stream
    		try{
    			reader.readUnsignedByte();
				reader.readUTF();
				d2 = reader.readLong();
    		}catch (Exception e) { 
                //#debug error
                System.out.println("RSTimeComparator Exception:" + e.toString());
    		}
        }
        catch (Exception e) {
            //#debug error
            System.out.println("RSTimeComparator Exception:" + e.toString());
        }
        finally {
            if (d1 == d2) {
                return RecordComparator.EQUIVALENT;
            } 
            else if (d1 > d2) {
                return RecordComparator.PRECEDES;
            } 
            else {
                return RecordComparator.FOLLOWS;
            }
        }
    }
}