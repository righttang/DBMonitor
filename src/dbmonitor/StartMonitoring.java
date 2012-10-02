package dbmonitor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.Date;
import java.util.Scanner;
import java.util.Vector;

public class StartMonitoring
{

    /**
     * @param args
     */
    private static dbmonitor.DBMonitor dbmonitor;

    public dbmonitor.DBMonitor getDbmonitor()
    {
        return dbmonitor;
    }

    public void setDbmonitor(dbmonitor.DBMonitor dbmonitor)
    {
        this.dbmonitor = dbmonitor;
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        getLogFile();

    }

    public static final String GET_URL = "http://115.70.160.71:8080/diablo3/";
    public static final String JUNK_FILE = "Oracle%20-%20JunkLog%20-%20Barbarian.log";
    public static final String STASH_FILE = "Oracle%20-%20StashLog%20-%20Barbarian.log";
    public static final String STATUS_FILE = "laner%236460%20-%20Stats%20-%20Barbarian.log";
    public static final String GET_LOG = "http://115.70.160.71:8080/logs/";
    public static final File statisticsFile = new File("C:\\statics.txt");


    //Profile List
    static Vector<String> profileList = new Vector<String>();
    //Statics
    static String lastKnowProfile;
    static String lastKnowTotalDrop;
    static String lastKnowTotal63Drop;
    static Date lastPrifleTime;
    static String currentTotalDrop;
    static String currentTotal63Drop;

    public static void getStatusFile()
    {
        try {
            String getURL = GET_URL + STATUS_FILE;

            URL getUrl = new URL(getURL);

            HttpURLConnection connection = (HttpURLConnection)getUrl.openConnection();

            connection.connect();
            Scanner sc = new Scanner(new InputStreamReader(connection.getInputStream(), "utf-8"));

            while (sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                if (nextLine != null && nextLine.indexOf("- Legendary: ") != -1) {
                    String oldTotalNumber = dbmonitor.getLegendaryNo().getText();
                    String newTotalNumber = parseNumber(nextLine, "- Legendary: ", "[");
                    dbmonitor.getLegendaryNo().setText(newTotalNumber);
                    if (!newTotalNumber.equalsIgnoreCase(oldTotalNumber)) {
                        dbmonitor.flashWindow();
                        return;
                    }
                }
                if (nextLine != null && nextLine.indexOf("Total items dropped: ") != -1) {
                    String oldTotalNumber = dbmonitor.getTotalNo().getText();
                    String newTotalNumber = parseNumber(nextLine, "Total items dropped: ", "[");
                    currentTotalDrop = newTotalNumber + "";
                    //ResetTimer
                    if (!newTotalNumber.equalsIgnoreCase(oldTotalNumber)) {
                        dbmonitor.resetTimer();
                    }
                    dbmonitor.getTotalNo().setText(newTotalNumber);
                }

                if (nextLine != null && nextLine.indexOf("--- ilvl 63 Rare:") != -1) {
                    currentTotal63Drop = parseNumber(nextLine, "--- ilvl 63 Rare:", "[");
                }

            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void getLogFile()
    {
        try {
            String getURL = GET_LOG + "?C=M;O=D";

            URL getUrl = new URL(getURL);

            HttpURLConnection connection = (HttpURLConnection)getUrl.openConnection();

            connection.connect();
            Scanner sc = new Scanner(new InputStreamReader(connection.getInputStream(), "utf-8"));

            while (sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                //System.out.println(nextLine);
                if (nextLine.indexOf("><img src=\"/icons/text.gif\" alt=\"[TXT]\"></td><td><a href=\"") != -1) {
                    int beginIndex =
                        nextLine.indexOf("><img src=\"/icons/text.gif\" alt=\"[TXT]\"></td><td><a href=\"");
                    beginIndex = beginIndex + 58;
                    int endIndex = nextLine.indexOf("\"", beginIndex);

                    getURL = GET_LOG + nextLine.substring(beginIndex, endIndex);
                    break;
                }
            }
          
            getUrl = new URL(getURL);

            connection = (HttpURLConnection)getUrl.openConnection();

            connection.connect();
            
            sc = new Scanner(new InputStreamReader(connection.getInputStream(), "utf-8"));
            while (sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                //System.out.println(nextLine);

                int beginIndex = nextLine.indexOf("[RadsAtom] - Loading next profile:");
                if (beginIndex != -1) {
                    beginIndex = beginIndex + 34;
                    profileList.add(nextLine.substring(beginIndex));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer profileWindow = new StringBuffer();
        for (String profile : profileList) {
            profileWindow.append(profile).append("\r\n");
        }
        dbmonitor.getProfileTextArea().setText(profileWindow.toString());
        int length = dbmonitor.getProfileTextArea().getText().length();
        dbmonitor.getProfileTextArea().setCaretPosition(length);
        int i = 0;
        if (profileList.size() >= 2) {
            i = profileList.size() - 2;
        }
        for (; i < profileList.size(); i++) {
            if (i > 0) {
                if (profileList.get(i).equalsIgnoreCase(profileList.get(i - 1))) {
                    dbmonitor.flashWindow();
                }
            }
        }
    }

    public static String parseNumber(String nextLine, String prefix, String suffix)
    {
        String returnText = "";
        if (nextLine != null && nextLine.indexOf(prefix) != -1) {
            int beginindex = nextLine.indexOf(prefix);
            int endindex = nextLine.indexOf(suffix);
            beginindex = beginindex + prefix.length();
            if (beginindex >= endindex) {
                return "";
            }

            returnText = nextLine.substring(beginindex, endindex).trim();

        }
        return returnText;
    }

    public static void updateStaticTable()
    {
        if (profileList == null || profileList.size() == 0) {
            return;
        }
        String currentProfile = profileList.get(profileList.size() - 1);
        if (lastKnowProfile == null || lastKnowProfile.length() == 0) {
            lastKnowProfile = currentProfile+"";
        }
        if (lastKnowTotalDrop == null || lastKnowTotalDrop.length() == 0) {
            lastKnowTotalDrop = currentTotalDrop+"";
        }
        if (lastKnowTotal63Drop == null || lastKnowTotal63Drop.length() == 0) {
            lastKnowTotal63Drop = currentTotal63Drop+"";
        }
        if (lastPrifleTime==null)
        {
            lastPrifleTime=new Date();
        }
       
        //If Profile changed, write Current Profile,Time,TotalDrop,DropRate,Total63Drop,DropRate
        if (!lastKnowProfile.equalsIgnoreCase(currentProfile)) {
            try {
                long time = new Date().getTime()-lastPrifleTime.getTime();
                int profileTotalDrop = Integer.decode(currentTotalDrop)-Integer.decode(lastKnowTotalDrop);
                int profileTota63lDrop = Integer.decode(currentTotal63Drop)-Integer.decode(lastKnowTotal63Drop);
                float totalrate = profileTotalDrop / (time / 60000);
                float total63rate = profileTota63lDrop / (time / 60000);
                String output = lastKnowProfile + "," + time/1000+"s,"+profileTotalDrop+ "," + totalrate+ "," + profileTota63lDrop + "," + total63rate+"\r\n";
                BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(statisticsFile,true)));
                bfw.write(output);
                bfw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            lastKnowProfile = currentProfile;
            lastKnowTotalDrop = currentTotalDrop;
            lastKnowTotal63Drop = currentTotal63Drop;
            lastPrifleTime=new Date();

        }
    }
}
