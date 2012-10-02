package dbmonitor;

import java.io.File;

import java.text.DecimalFormat;

import java.util.Scanner;
import java.util.Vector;

public class Analysis
{
    public Analysis()
    {
        super();
    }
    public static final File statisticsFile = new File("C:\\statics.txt");

    public static void main(String[] args)
    {
        Analysis analysis = new Analysis();
        doanalysis();
    }

    public static void doanalysis()
    {
        Vector<String> profileVector = new Vector<String>();
        Vector<Integer> timeVector = new Vector<Integer>();
        Vector<Integer> countVector = new Vector<Integer>();
        Vector<Integer> totalVector = new Vector<Integer>();
        Vector<Integer> total63Vector = new Vector<Integer>();

        try {
            Scanner sc = new Scanner(statisticsFile);
            while (sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                if (nextLine.startsWith(" C:\\")) {
                    String[] stringarray = nextLine.split(",");
                    String profile = stringarray[0];
                    Integer time = Integer.decode(stringarray[1].replace("s", ""));
                    Integer total = Integer.decode(stringarray[2]);
                    Integer total63 = Integer.decode(stringarray[4]);

                    int position = profileVector.indexOf(profile);
                    if (position == -1) {
                        profileVector.add(profile);
                        timeVector.add(time);
                        totalVector.add(total);
                        total63Vector.add(total63);
                        countVector.add(1);
                    } else {
                        Integer count = countVector.get(position);
                        timeVector.set(position, (timeVector.get(position).intValue() * count + time) / (count + 1));
                        totalVector.set(position,
                                        (totalVector.get(position).intValue() * count + total) / (count + 1));
                        total63Vector.set(position, (total63Vector.get(position).intValue() + total63));
                        countVector.set(position, count + 1);
                    }
                }
            }
            for (int i = 0; i < profileVector.size(); i++) {
                StringBuffer profilename = new StringBuffer(profileVector.get(i));
                for (int j = profileVector.get(i).length(); j < 50; j++) {
                    profilename.append(" ");
                }
                Double avgdroprate = (double)totalVector.get(i) / ((double)timeVector.get(i) / 3600);
                Double total63droprate = (double)total63Vector.get(i) / ((double)timeVector.get(i) / 3600);
                DecimalFormat df = new DecimalFormat("0.00");
                System.out.println(profilename.toString() + "\tAverage Time:" + timeVector.get(i) +
                                   "s\tAvg Total Drop:" + totalVector.get(i) + "\tAvg Drop Rate:\t" +
                                   df.format(avgdroprate) + "\t\tTotal 63 Drop:\t" + total63Vector.get(i)+"\tTotal 63 Drop Rate:"+df.format(total63droprate));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
