package dbmonitor;

/*
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of WebTail.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: WebTail.java,v 1.3 2004/02/20 15:42:03 pjm2 Exp $

*/


import java.io.*;

import java.net.*;

import java.util.Scanner;
import java.util.Vector;


public class WebTail extends Thread
{
    private static dbmonitor.DBMonitor dbmonitor;
    public static final String USER_AGENT = "WebTail Client [http://www.jibble.org/webtail/]";
    public static final String GET_LOG = "http://115.70.160.71:8080/logs/";
    static Vector<String> profileList = new Vector<String>();
    static int lastSize = 0;

    public static void getTail()
    {
        boolean running = true;
        while (running) {
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
                //System.out.println(getUrl);
                HttpURLConnection conn = (HttpURLConnection)getUrl.openConnection();
                conn.setRequestMethod("HEAD");
                conn.setRequestProperty("User-Agent", USER_AGENT);
                int length = conn.getContentLength();

                if (length == -1) {
                    System.out.println("Unable to determine length of document with HEAD request.");
                    System.out.println("This document and/or web server is not suitable for tailing.");
                    System.exit(0);
                }

                if (lastSize == -1) {
                    lastSize = length;
                }

                conn.disconnect();

                if (length > lastSize) {
                    conn = (HttpURLConnection)getUrl.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", USER_AGENT);
                    conn.setRequestProperty("Range", "bytes=" + lastSize + "-" + length);

                    sc = new Scanner(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    String haijinglog = "";
                    while (sc.hasNextLine()) {
                        String nextLine = sc.nextLine();
                        //System.out.println(nextLine);
                        int haijingBeginIndex = nextLine.indexOf("[HaiJingFang]:");
                        if (haijingBeginIndex != -1)
                        {
                            haijinglog=nextLine.substring(haijingBeginIndex);
                        }
                        int beginIndex = nextLine.indexOf("[RadsAtom] - Loading next profile:");
                        if (beginIndex != -1) {
                            beginIndex = beginIndex + 34;
                            profileList.add(nextLine.substring(beginIndex));
                            if (haijinglog.length()>0)
                            {
                                profileList.add(haijinglog); 
                            }
                            
                            //System.out.println(nextLine.substring(beginIndex));
                        }

                    }


                    conn.disconnect();
                } else if (length < lastSize) {
                    System.out.println();
                    System.out.println("[Log file shrank in size. Restarting at end.]");
                    System.out.println();
                }

                lastSize = length;

            } catch (IOException e) {
                e.printStackTrace();
            }



            StringBuffer profileWindow = new StringBuffer();
            for (String profile : profileList) {
                profileWindow.append(profile).append("\r\n");
            }
            dbmonitor.getProfileTextArea().setText(profileWindow.toString());
            int length = dbmonitor.getProfileTextArea().getText().length();
            dbmonitor.getProfileTextArea().setCaretPosition(length);

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // Do nothing.
            }
            

        }
    }

    @Override
    public void run()
    {
        super.run();
        getTail();
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

    public static void setDbmonitor(DBMonitor dbmonitor)
    {
        WebTail.dbmonitor = dbmonitor;
    }
}
