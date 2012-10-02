package dbmonitor;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.TextArea;


public class DBMonitor
{

    private JFrame frmDiabloMonitor;
    private JTextField LegendaryNo;
    private JTextField TotalNo;
    private JTextField NoChangeTimer;
    private TextArea ProfileTextArea;
    private Timer timer;
    private int timeNoChange = -1;
    private dbmonitor.DBMonitor dbmonitor;

    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    try {
                        dbmonitor.DBMonitor window = new dbmonitor.DBMonitor();
                        window.frmDiabloMonitor.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
    }

    public TextArea getProfileTextArea()
    {
        return ProfileTextArea;
    }

    /**
     * Create the application.
     */
    public DBMonitor()
    {
        this.dbmonitor = this;
        initialize();
    }

    public JTextField getLegendaryNo()
    {
        return LegendaryNo;
    }

    public void setLegendaryNo(JTextField legendaryNo)
    {
        LegendaryNo = legendaryNo;
    }

    public JTextField getTotalNo()
    {
        return TotalNo;
    }

    public void setTotalNo(JTextField totalNo)
    {
        TotalNo = totalNo;
    }

    public JTextField getNoChangeTimer()
    {
        return NoChangeTimer;
    }

    public void setNoChangeTimer(JTextField noChangeTimer)
    {
        NoChangeTimer = noChangeTimer;
    }

    public int getTimeNoChange()
    {
        return timeNoChange;
    }

    public void setTimeNoChange(int timeNoChange)
    {
        this.timeNoChange = timeNoChange;
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        frmDiabloMonitor = new JFrame();
        frmDiabloMonitor.setResizable(false);
        frmDiabloMonitor.setTitle("Diablo Monitor");
        frmDiabloMonitor.setBounds(100, 100, 403, 365);
        frmDiabloMonitor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmDiabloMonitor.getContentPane().setLayout(null);

        JLabel lblLegendaryItemPicked = new JLabel("Legendary Item Picked Up");
        lblLegendaryItemPicked.setBounds(65, 33, 138, 22);
        frmDiabloMonitor.getContentPane().add(lblLegendaryItemPicked);

        JLabel lblTotalItemPicked = new JLabel("Total Item Picked Up");
        lblTotalItemPicked.setBounds(65, 66, 116, 14);
        frmDiabloMonitor.getContentPane().add(lblTotalItemPicked);

        JLabel lblTotalItemTimer = new JLabel("Total Item No Change Timer");
        lblTotalItemTimer.setBounds(65, 95, 138, 14);
        frmDiabloMonitor.getContentPane().add(lblTotalItemTimer);

        timer = new Timer(1000, new RefereshListener());
        timer.start();


        LegendaryNo = new JTextField();
        LegendaryNo.setText("0");
        LegendaryNo.setEditable(false);
        LegendaryNo.setBounds(240, 34, 86, 20);
        frmDiabloMonitor.getContentPane().add(LegendaryNo);
        LegendaryNo.setColumns(10);

        TotalNo = new JTextField();
        TotalNo.setText("0");
        TotalNo.setEditable(false);
        TotalNo.setColumns(10);
        TotalNo.setBounds(240, 63, 86, 20);
        frmDiabloMonitor.getContentPane().add(TotalNo);

        NoChangeTimer = new JTextField();
        NoChangeTimer.setText("0");
        NoChangeTimer.setEditable(false);
        NoChangeTimer.setColumns(10);
        NoChangeTimer.setBounds(240, 92, 86, 20);
        frmDiabloMonitor.getContentPane().add(NoChangeTimer);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(new RefereshListener());
        btnRefresh.setBounds(148, 290, 89, 23);
        frmDiabloMonitor.getContentPane().add(btnRefresh);

        ProfileTextArea = new TextArea();
        ProfileTextArea.setEditable(false);
        ProfileTextArea.setBounds(53, 140, 322, 125);
        frmDiabloMonitor.getContentPane().add(ProfileTextArea);
        
        WebTail webtail = new WebTail();
        webtail.setDbmonitor(this);
        webtail.start();


    }


    private class RefereshListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (e.getActionCommand() != null && e.getActionCommand().equalsIgnoreCase("Refresh")) {
                timeNoChange = 0;
            }
            try {
                timeNoChange++;
                NoChangeTimer.setText(timeNoChange + "s");

                if (timeNoChange % 5 == 0) {
                    StartMonitoring startMonitor = new StartMonitoring();
                    startMonitor.setDbmonitor(dbmonitor);
                    startMonitor.getStatusFile();

                    if (timeNoChange % 30 == 15) {
                        //startMonitor.getLogFile();
                        //startMonitor.updateStaticTable();
                    }
                }


                if (timeNoChange > 400) {
                    frmDiabloMonitor.toFront();
                }
            } catch (Exception ee) {
                ee.printStackTrace();
            }
            //System.out.println(e.getActionCommand());
        }

    }

    public void resetTimer()
    {
        timeNoChange = 0;
    }

    public void flashWindow()
    {
        frmDiabloMonitor.toFront();
    }
}
