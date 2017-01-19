
/**
 *
 * @author xxjstudxx
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javazoom.jl.player.advanced.*;

import java.io.*;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javazoom.jl.decoder.JavaLayerException;

public class DigitalClock extends JFrame {
    Scanner settingsScanner;
    JFrame frame = new JFrame(); //adds the separate frames together   
    colorPuzzle pzzl;
    user joe = new user();
    user marty = new user();
    
    private user activeUser;
    
    JLabel timeLabel = new JLabel(); //Digital Clock
    JLabel theDay = new JLabel(); //Gives Date
    JLabel location = new JLabel();
    JLabel highLow = new JLabel();
    JLabel todaysWeather = new JLabel();
    JLabel weatherIconLabel = new JLabel();
    JLabel settingsTime = new JLabel();
    JLabel hourTenLabel = new JLabel();
    JLabel hourOneLabel = new JLabel();
    JLabel minTenLabel = new JLabel();
    JLabel minOneLabel = new JLabel();
    
    JTextArea labelHolder = new JTextArea();
    JTextArea newsText = new JTextArea(); //where the news is displayed
    JTextArea weatherMan = new JTextArea(); //where the weather is displayed
    JTextArea f = new JTextArea(); //unused, attempt at making the background a jpeg
    JTextArea settingsPage = new JTextArea();
    
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a"); //controls the time format
    Timer timer; //counts seconds
    
    Border buttonBorder = new LineBorder(Color.WHITE, 1); //used on white text frames
    Border buttonBorder2 = new LineBorder(Color.WHITE,1); //used on black text frames
    Border frameBorder = new LineBorder(Color.WHITE,2);

    Clip clip;
    
    URL sunImg = getClass().getResource("/digital_sun.png");
    URL overcastImg = getClass().getResource("/overcast_digital.png");
    URL rainImg = getClass().getResource("/rain_digital.png");
    URL snowImg = getClass().getResource("/snow_digital.png");
    URL fairImg = getClass().getResource("/digital_fair.png");
    URL clearImg = getClass().getResource("/clear_digital.png");
    URL windyImg = getClass().getResource("/windy_digital.png");
    URL nullImg = getClass().getResource("/weather_unknown.png");
    URL settingsImg = getClass().getResource("/settings_digital.png");
    URL settingsNews = getClass().getResource("/settings_news.png");
    URL settingsWeather = getClass().getResource("/weather_settings_use.png");
    ImageIcon sun, overcast, rain, snow, fair, clear, windy, nullWeather, settingsIcon, newsSettings, weatherSettings;
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //will be used in final version
    
    boolean isSnoozed = false, isBlack = false, isPlaying = false, 
            threadIsAlive = false, alarmIsOn = true, radioOn = false,
            alarmIsPlayable = false;
    String iconDefine = null, newsStory = null, musicPlay, radioStation;
    int newsPage = 0, frameWidth = 350, frameHeight = 200, hourTen = 0,
            hourOne = 6, minuteTen = 0, minuteOne = 0;
    int buttonWidth, buttonHeight, height;
    int alarmButtonW, alarmButtonH;
    
    int playerIndex = 0;
    
    alarmPlay alarm;
    
    InputStream musicIS;
    AdvancedPlayer soundToPlay;
    ArrayList<user> userList = new ArrayList<user>();
    
    /**
     * Clock Constructor
     * @throws LineUnavailableException
     * @throws UnsupportedAudioFileException 
     */
    public DigitalClock() throws LineUnavailableException, UnsupportedAudioFileException, JavaLayerException, FileNotFoundException { 
        //clock constructor
        super(); //not currently necessary
        InputStream in = getClass().getResourceAsStream("settings.txt");
        settingsScanner = new Scanner(in);
        
        //plays the alarm in a new thread
        alarm = new alarmPlay();
        
        String finalSettings = "";
        while(settingsScanner.hasNext()) {
            finalSettings += settingsScanner.nextLine();
        }
        
        String startUser = finalSettings.substring(finalSettings.indexOf("user=") + 5);
        System.out.println(startUser);
        
        try{
        musicIS = DigitalClock.class.getResource("alarmSound.mp3").openStream();
        }
        catch(IOException e) {
            System.out.println("Alarm could not be found");
        }
        
        soundToPlay = new AdvancedPlayer(musicIS);
        
        try{
        java.lang.Runtime.getRuntime().exec("mpc repeat on");
        }
        catch (IOException e) {
            System.out.println("command mpc could not be found");
        }
        
        joe = new user(); //the following block creates a new user Joe
        joe.name = "Joe";
        joe.preferredAlarmStatus = false;
        joe.radioStationOne = new radio("http://181fm-edge1.cdnstream.com/181-eagle_128k.mp3?listenerid=a384a3cc553ecbcfd7abfe8908928348&cb=524153.mp3&type=.flv", "Classic Rock");
        joe.radioStationTwo = new radio(" http://ksmu.streamguys1.com/ksmu1", "NPR");
        joe.radioStationThree = new radio("http://bbcwssc.ic.llnwd.net/stream/bbcwssc_mp1_ws-einws", "BBC");
        joe.zipcode = "65401";
        joe.isActive = false;
        
        marty = new user(); //the following block creates a new user Marty
        marty.name = "Marty";
        marty.preferredAlarmStatus = false;
        marty.radioStationOne = new radio("http://playerservices.streamtheworld.com/api/livestream-redirect/KWTOFMaac.flv", "Jock 98.7");
        marty.radioStationTwo = new radio(" http://ksmu.streamguys1.com/ksmu1", "NPR");
        marty.radioStationThree = new radio("http://bbcwssc.ic.llnwd.net/stream/bbcwssc_mp1_ws-einws", "BBC");  
        marty.zipcode = "65737";
        marty.isActive = false;
      
        userList.add(joe);
        userList.add(marty);
        
        for(int i = 0; i < userList.size(); i++) {
            if (startUser.equals(userList.get(i).name.toLowerCase())) {
                activeUser = userList.get(i);
                userList.get(i).isActive = true;
                System.out.println(activeUser.name);
                break;
            }
        }
        
        alarmIsOn = getActiveUser().preferredAlarmStatus;
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //ends program on close
        int width = screenSize.width; //used in final program
        height = screenSize.height; //used in final program
        height = height - 40;
        buttonWidth = width / 6;
        buttonHeight = height/ 6;
        timeLabel.setText(sdf.format(new Date(System.currentTimeMillis()))); //initializes the digital clock
        timeLabel.setFont(new Font("Clock", Font.BOLD, height / 4)); //sets the font for the digital clock
        timeLabel.setLocation(5, (height / 8) + 30);
        timeLabel.setSize(width, height /4);//sets the location of the clock
                
        theDay.setFont(new Font("Message", Font.BOLD, height / 8)); //sets the font of the date
        theDay.setFocusable(false);
        theDay.setLocation(5, 20);
        theDay.setSize(width - 100, height / 8 + 20);
        theDay.setVisible(true); //makes the date visible
        theDay.setBackground(Color.GREEN); //sets the date background to green
        
        try {
        sun = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("Digital_Sun.png")));
        overcast = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("overcast_digital.png")));
        rain = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("rain_digital.png")));
        snow = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("snow_digital.png")));
        fair = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("digital_fair.png")));
        clear = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("clear_digital.png")));
        windy = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("windy_digital.png")));
        nullWeather = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("weather_unknown.png")));
        settingsIcon = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("settings_digital.png")));
        }
        catch (IOException e) {
            System.out.println("Icon files could not be found");
        }
        labelHolder.add(timeLabel);
        labelHolder.add(theDay);
        labelHolder.setBorder(frameBorder);
        labelHolder.setBackground(Color.GREEN);
        labelHolder.setFocusable(false);
        labelHolder.setVisible(true);
                
        timer = new Timer(500, new timeAction()); //allows the clock to check the time every 1/2 second
        timer.setRepeats(true); //repeats
        timer.start(); //starts the timer
        
        weatherIconSet(weatherIdent().weatherIcon);
        //weatherIconLabel.setIcon(nullWeather);
        weatherIconLabel.setLocation(5, height / 4 + 20);
        weatherIconLabel.setSize(400, 400);
        weatherIconLabel.setVisible(true);
        
        settingsButton.setSize(60, 60);
        settingsButton.setText("");
        settingsButton.setIcon(settingsIcon);
        settingsButton.setLocation(width - 75, 5);
        settingsButton.setBorderPainted(false);
        settingsButton.setBackground(Color.GREEN);
        settingsButton.setVisible(true);
        
        radioPage.setSize(buttonWidth,buttonHeight);
        radioPage.setLocation(5 + buttonWidth, timeLabel.getY() + (timeLabel.getHeight() / 2));
        radioPage.setBackground(Color.BLACK);
        radioPage.setBorder(buttonBorder);
        radioPage.setForeground(Color.WHITE);
        radioPage.setVisible(false);
        
        switchUser.setSize(buttonWidth, buttonHeight);
        switchUser.setLocation(5 + (2 * buttonWidth), timeLabel.getY() + (timeLabel.getHeight() / 2));
        switchUser.setBackground(Color.BLACK);
        switchUser.setBorder(buttonBorder);
        switchUser.setForeground(Color.WHITE);
        switchUser.setText("USER: " + getActiveUser().name);
        switchUser.setVisible(false);
        
        playRadio.setLocation(5, timeLabel.getY() + (timeLabel.getHeight() / 2));
        playRadio.setSize(buttonWidth, buttonHeight);
        playRadio.setBackground(Color.BLACK);
        playRadio.setBorder(buttonBorder);
        playRadio.setForeground(Color.WHITE);
        playRadio.setVisible(false);
        
        playOne.setSize(buttonWidth, buttonHeight);
        playOne.setLocation(width - (2 * buttonWidth), height - buttonHeight);
        playOne.setBackground(Color.BLACK);
        playOne.setForeground(Color.WHITE);
        playOne.setBorder(buttonBorder);
        playOne.setText(getActiveUser().radioStationOne.title);
        playOne.setVisible(false);
        
        playTwo.setSize(buttonWidth, buttonHeight);
        playTwo.setLocation(width - (3 * buttonWidth), height - buttonHeight);
        playTwo.setBackground(Color.BLACK);
        playTwo.setForeground(Color.WHITE);
        playTwo.setBorder(buttonBorder);
        playTwo.setText(getActiveUser().radioStationTwo.title);
        playTwo.setVisible(false);
        
        playThree.setSize(buttonWidth, buttonHeight);
        playThree.setLocation(width - (4 * buttonWidth), height - buttonHeight);
        playThree.setBackground(Color.BLACK);
        playThree.setForeground(Color.WHITE);
        playThree.setBorder(buttonBorder);
        playThree.setText(getActiveUser().radioStationThree.title);
        playThree.setVisible(false);
        
        alarmSettings.setSize(buttonWidth, buttonHeight);
        alarmSettings.setLocation(5, timeLabel.getY() + (timeLabel.getHeight() / 2));
        alarmSettings.setBackground(Color.BLACK);
        alarmSettings.setBorder(buttonBorder);
        alarmSettings.setForeground(Color.WHITE);
        alarmSettings.setVisible(false);
        
        alarmSwitch.setSize((buttonWidth), (buttonHeight / 2));
        alarmSwitch.setLocation(width - buttonWidth - alarmSwitch.getWidth(), height - alarmSwitch.getHeight());
        alarmSwitch.setBackground(Color.BLACK);
        alarmSwitch.setForeground(Color.WHITE);
        alarmSwitch.setBorder(buttonBorder);
        if (getActiveUser().preferredAlarmStatus)
            alarmSwitch.setText("Alarm: ON");
            
        else
            alarmSwitch.setText("Alarm: OFF");
        alarmSwitch.setVisible(false);
        
        settingsBack.setSize(buttonWidth,buttonHeight);
        settingsBack.setLocation(width - buttonWidth, height - buttonHeight);
        settingsBack.setBackground(Color.BLACK);
        settingsBack.setForeground(Color.WHITE);
        settingsBack.setBorder(buttonBorder);
        settingsBack.setVisible(false);
                
        settingsPage.setBackground(Color.BLACK);
        settingsPage.setFocusable(false);
        settingsPage.setVisible(false);
        
        alarmButtonW = buttonWidth;
        alarmButtonH = buttonHeight;
        
        int upButtonHeight = timeLabel.getHeight() + 10;
        int downButtonHeight = height - upButtonHeight - 20;
        Font alarmLabelFont = new Font("alarmLabel", Font.BOLD, downButtonHeight - upButtonHeight - alarmButtonH - 10);
        hourTenUp.setSize(alarmButtonW, alarmButtonH);
        hourTenUp.setLocation(alarmButtonW, upButtonHeight);
        hourTenUp.setBorder(buttonBorder);
        hourTenUp.setBackground(Color.BLACK);
        hourTenUp.setForeground(Color.WHITE);
        hourTenUp.setVisible(false);
        
        hourTenDown.setSize(alarmButtonW, alarmButtonH);
        hourTenDown.setLocation(alarmButtonW, downButtonHeight);     
        hourTenDown.setBorder(buttonBorder);
        hourTenDown.setBackground(Color.BLACK);        
        hourTenDown.setForeground(Color.WHITE);
        hourTenDown.setVisible(false);
        
        hourOneUp.setSize(alarmButtonW, alarmButtonH);
        hourOneUp.setLocation(2*alarmButtonW, upButtonHeight);     
        hourOneUp.setBorder(buttonBorder);
        hourOneUp.setBackground(Color.BLACK);       
        hourOneUp.setForeground(Color.WHITE);        
        hourOneUp.setVisible(false);
        
        hourOneDown.setSize(alarmButtonW, alarmButtonH);
        hourOneDown.setLocation(2*alarmButtonW, downButtonHeight);    
        hourOneDown.setBorder(buttonBorder);
        hourOneDown.setBackground(Color.BLACK);        
        hourOneDown.setForeground(Color.WHITE);        
        hourOneDown.setVisible(false);

        minTenUp.setSize(alarmButtonW, alarmButtonH);
        minTenUp.setLocation(3*alarmButtonW, upButtonHeight);  
        minTenUp.setBorder(buttonBorder);
        minTenUp.setForeground(Color.WHITE);
        minTenUp.setBackground(Color.BLACK);
        minTenUp.setVisible(false);

        minTenDown.setSize(alarmButtonW, alarmButtonH);
        minTenDown.setLocation(3*alarmButtonW, downButtonHeight);    
        minTenDown.setBorder(buttonBorder);
        minTenDown.setBackground(Color.BLACK);
        minTenDown.setForeground(Color.WHITE);
        minTenDown.setVisible(false);

        minOneUp.setSize(alarmButtonW, alarmButtonH);
        minOneUp.setLocation(4*alarmButtonW, upButtonHeight);  
        minOneUp.setBorder(buttonBorder);
        minOneUp.setBackground(Color.BLACK);
        minOneUp.setForeground(Color.WHITE);
        minOneUp.setVisible(false);
       
        minOneDown.setSize(alarmButtonW, alarmButtonH);        
        minOneDown.setLocation(4*alarmButtonW, downButtonHeight); 
        minOneDown.setBorder(buttonBorder);
        minOneDown.setBackground(Color.BLACK);
        minOneDown.setForeground(Color.WHITE);
        minOneDown.setVisible(false);
        
        hourTenLabel.setSize(alarmButtonW, downButtonHeight - upButtonHeight - alarmButtonH);
        hourTenLabel.setLocation(alarmButtonW, upButtonHeight + alarmButtonH);
        hourTenLabel.setForeground(Color.WHITE);
        hourTenLabel.setBorder(buttonBorder);
        hourTenLabel.setText(String.valueOf(hourTen));
        hourTenLabel.setFont(alarmLabelFont);
        hourTenLabel.setVisible(false);
        
        hourOneLabel.setSize(alarmButtonW, downButtonHeight - upButtonHeight - alarmButtonH);
        hourOneLabel.setLocation(alarmButtonW * 2, upButtonHeight + alarmButtonH);
        hourOneLabel.setForeground(Color.WHITE);
        hourOneLabel.setBorder(buttonBorder);
        hourOneLabel.setText(String.valueOf(hourOne));
        hourOneLabel.setFont(alarmLabelFont);
        hourOneLabel.setVisible(false);
        
        minTenLabel.setSize(alarmButtonW, downButtonHeight - upButtonHeight - alarmButtonH);
        minTenLabel.setLocation(alarmButtonW * 3, upButtonHeight + alarmButtonH);
        minTenLabel.setForeground(Color.WHITE);
        minTenLabel.setBorder(buttonBorder);
        minTenLabel.setText(String.valueOf(minuteTen));
        minTenLabel.setFont(alarmLabelFont);
        minTenLabel.setVisible(false);
        
        minOneLabel.setSize(alarmButtonW, downButtonHeight - upButtonHeight - alarmButtonH);
        minOneLabel.setLocation(alarmButtonW * 4, upButtonHeight + alarmButtonH);
        minOneLabel.setForeground(Color.WHITE);
        minOneLabel.setBorder(buttonBorder);
        minOneLabel.setText(String.valueOf(minuteOne));
        minOneLabel.setFont(alarmLabelFont);
        minOneLabel.setVisible(false);
        
        news.setText("NEWS"); //names the JButton news as NEWS
        news.setSize(buttonWidth, buttonHeight); //sets the size to the preferred button size
        news.setLocation(width - buttonWidth, height-buttonHeight); //sets the preferred location of news
        news.setBackground(Color.GREEN); 
        news.setForeground(Color.WHITE);
        news.setBorder(buttonBorder); //sets the button for a white text screen
        
        //takes you to the weatherMan Frame
        weather.setText("WEATHER"); 
        weather.setSize(buttonWidth, buttonHeight);
        weather.setLocation(width - (2*buttonWidth), height - buttonHeight);
        weather.setBackground(Color.GREEN);
        weather.setForeground(Color.WHITE);
        weather.setBorder(buttonBorder);
        
        snooze.setText("SNOOZE");
        snooze.setSize(buttonWidth, buttonHeight);
        snooze.setLocation(width - (3 * buttonWidth), height-buttonHeight);
        snooze.setBackground(Color.GREEN);
        snooze.setForeground(Color.WHITE);
        snooze.setBorder(buttonBorder);
        snooze.setVisible(false);
        
        //returns to the clock from the weatherMan frame 
        weatherBack.setText("BACK");
        weatherBack.setSize(buttonWidth, buttonHeight);
        weatherBack.setLocation(width - buttonWidth, height - buttonHeight);
        weatherBack.setBackground(Color.ORANGE);
        weatherBack.setForeground(Color.WHITE);
        weatherBack.setBorder(buttonBorder2);        
        
        //shows the current political news
        newsText.setFont(new Font("NEWS", Font.PLAIN, height / 12));
        newsText.setBackground(Color.BLUE);
        newsText.setForeground(Color.WHITE);
        newsText.setSize(frameWidth,frameHeight);
        newsText.setFocusable(false);
        newsText.setLineWrap(true);
        newsText.setVisible(false);
        
        location.setLocation(5, 5);
        location.setFont(new Font("Location", Font.BOLD, height / 10));
        location.setSize(width, height / 10 + 10);
        location.setVisible(false);
        
        todaysWeather.setLocation(5, height / 10 + 15);
        todaysWeather.setFont(new Font("weather", Font.PLAIN, height / 12));
        todaysWeather.setSize(width, height / 10 + 10);
        todaysWeather.setVisible(false);
        
        highLow.setLocation(5, (height / 12) * 2 + 20 );
        highLow.setFont(new Font("weather", Font.PLAIN, height/10));
        highLow.setSize(width, height / 10 + 10);
        highLow.setVisible(false);
        
        //shows the current weather
        weatherMan.setFont(new Font("weather", Font.TRUETYPE_FONT, 18));
        weatherMan.setBackground(Color.ORANGE);
        weatherMan.setForeground(Color.BLACK);
        weatherMan.setSize(frameWidth,frameHeight);
        weatherMan.setFocusable(false);
        weatherMan.setLineWrap(true);
        weatherMan.setVisible(false);
        weatherMan.setBorder(frameBorder);
        weatherMan.add(location);
        weatherMan.add(todaysWeather);
        weatherMan.add(highLow);
        weatherMan.add(weatherBack);
        
        //returns from news to the clock
        back.setLocation(width - buttonWidth, height-buttonHeight);
        back.setSize(buttonWidth, buttonHeight);
        back.setVisible(false);
        back.setBorder(buttonBorder);
        back.setBackground(Color.BLUE);
        back.setForeground(Color.WHITE);        
        
        newsRight.setBackground(Color.BLUE);
        newsRight.setBorder(buttonBorder);
        newsRight.setSize(buttonWidth, buttonHeight);
        newsRight.setText(">");
        newsRight.setLocation(width - (2*buttonWidth), height-buttonHeight);
        newsRight.setForeground(Color.WHITE);
        newsRight.setVisible(false);
        
        newsLeft.setBackground(Color.BLUE);
        newsLeft.setBorder(buttonBorder);
        newsLeft.setSize(buttonWidth, buttonHeight);
        newsLeft.setText("<");
        newsLeft.setLocation(width - (3*buttonWidth), height-buttonHeight);
        newsLeft.setForeground(Color.WHITE);
        newsLeft.setVisible(false);

        settingsPage.add(hourTenUp);
        settingsPage.add(hourTenDown);
        settingsPage.add(hourOneUp);
        settingsPage.add(hourOneDown);
        settingsPage.add(minTenUp);
        settingsPage.add(minTenDown);
        settingsPage.add(minOneUp);
        settingsPage.add(minOneDown);  

        settingsPage.add(hourTenLabel);
        settingsPage.add(hourOneLabel);
        settingsPage.add(minTenLabel);
        settingsPage.add(minOneLabel);   
        
        settingsPage.add(settingsBack);
        settingsPage.add(radioPage);
        settingsPage.add(switchUser);
        settingsPage.add(alarmSwitch);
        settingsPage.add(playRadio);
        settingsPage.add(playOne);
        settingsPage.add(playTwo);
        settingsPage.add(playThree);
        
        //adds all components to a 'allfather'
        frame.add(settingsPage);
        frame.add(newsText);
        frame.add(news);
        frame.add(weather);
        frame.add(snooze);
        frame.add(weatherMan);
        frame.add(back);
        frame.add(weatherIconLabel);
        frame.add(settingsButton);
        frame.add(newsRight);
        frame.add(newsLeft);
        frame.add(labelHolder);
        frame.setUndecorated(true);
        frame.pack();
        
        //sizes the 'allfather' frame
        frame.setSize(width,height);
        frame.setBackground(Color.GREEN);
        newsText.setLayout(null);
        frame.setResizable(false);
        frame.setVisible(true);   
        

    } //ENDS CONSTRUCTOR
    
    
public class alarmPlay extends Thread
{
    @Override
    public void run()
    {
        while (true)
        {
            try {
                //use below to debug (all three must be true for alarm to play)
                //System.out.println(alarmIsPlayable + " " + !isSnoozed + " " + alarmIsOn);
                this.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }
            while (alarmIsPlayable && !isSnoozed && alarmIsOn) 
            {  
                System.out.println("here");
                String DD = getDay();
                //use this to create an alarm
                backAction(); //opens up the main page
                snooze.setVisible(true); //allow snooze to operate
                theDay.setText("WAKEUP! " + DD); 
                 //if the alarm isn't playing
                if(!isPlaying)
                {
                    try
                    {
                        java.lang.Runtime.getRuntime().exec("mpc stop");
                    } 
                    catch (IOException ex)
                    {
                        Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
                    }                
                    isPlaying = true;
                }
                try 
                {
                    soundToPlay.play(0, 5000);
                } 
                catch (JavaLayerException ex)
                {
                    Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
                }
                alarmIsOn = true;
                alarmIsPlayable = false;            
            }
        }
    }
}
//runs every time the time action is called
public class timeAction implements ActionListener 
{
    /**
     * Timer Action
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) 
    { 
        //if the thread is not running, start the thread
        if (!alarm.isAlive())
        {
            alarm.start();
        }
        String MMSS = getMinTime();
        String hhMM = getHourTime();
        String DD = getDay();
        //sets the time
        timeLabel.setText(sdf.format(new Date(System.currentTimeMillis())));
        
        //updates weather on the hour
        if(MMSS.equals("00:00")) 
        { 
            //Taken out temporarily, until wifi problem is fixed on campus
            weatherIconSet(weatherIdent().weatherIcon);
            //unsnoozes on the hour
            if (isSnoozed)
                isSnoozed = false;
        }
        //Sets off the alarm
        if (hhMM.equals(getAlarmTime()) && !isSnoozed && alarmIsOn)
        { 
            alarmIsPlayable = true;
        } 
        else
        {
            theDay.setText(DD);
        }
    } //end timer
}
    
    //***BEGIN JBUTTONS***//
    
    JButton news = new JButton( new AbstractAction("news") { //news button
        @Override
        public void actionPerformed( ActionEvent e ) { //button is pressed
            settingsButton.setVisible(false);
            if (newsPage == 0) {
            try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/politics"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: Politics");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            if (newsPage == 1) {
              try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/world"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: World");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }  
            }
            if (newsPage == 2) {
              try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/science"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: Science");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }  
            }
            if (newsPage == 3) {
              try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/tech"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: Tech");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }  
            }
            newsStory = newsStory.substring(0, newsStory.indexOf("<STORY 5>"));
            //removes the <STORY i> from the text
            for( int i = 0; i <= 4; i++) {
                newsStory = newsStory.replace("<STORY " + i + ">", ""); 
            } //end cleanup
            
            timeLabel.setVisible(false); //hides the timer
            weather.setVisible(false); //hides the weather button
            theDay.setVisible(false); //hides the day of the week
            labelHolder.setVisible(false);
            weatherIconLabel.setVisible(false);
            frame.add(newsText); //adds the news frame to the main frame
            newsText.setWrapStyleWord(true); //forces the text to stay on the frame
            newsText.setText(newsStory); //sets the text to the top 3 stories
            newsText.setVisible(true);  //sets the news to visible
            newsRight.setVisible(true);
            newsLeft.setVisible(true);
            back.setVisible(true); //sets the back button to visible
            news.setVisible(false); //hides the news button
         
        }
    });
    JButton weather = new JButton( new AbstractAction("weather") {
        @Override
        public void actionPerformed(ActionEvent e) {
            weatherReturn wr = new weatherReturn();
            String weatherType = null;
            
            wr = weatherIdent();
                
            settingsButton.setVisible(false);
            iconDefine = wr.weatherIcon;
            System.out.println(iconDefine);
            String whereYouAt = wr.location;
            String weatherToday = wr.temp + " " + wr.windChill;
            System.out.println("wt" + weatherToday);
            String highsandlows = wr.high + " " + wr.low;
            location.setText(whereYouAt.trim());
            System.out.println(whereYouAt);
            todaysWeather.setText(weatherToday);
            highLow.setText(highsandlows.trim());
            highLow.setVisible(true);
            location.setVisible(true);
            todaysWeather.setVisible(true);
            weatherIconLabel.setVisible(false);
            timeLabel.setVisible(false); //hides the timer
            theDay.setVisible(false); //hides the date
            labelHolder.setVisible(false);
            weather.setVisible(false); //hides the weather button
            news.setVisible(false); //hides the news button
            weatherMan.add(location);
            weatherMan.add(todaysWeather);
            weatherMan.add(highLow);
            frame.add(weatherMan);
            weatherMan.setVisible(true);  //shows the weather jframe
            weatherBack.setVisible(true); //shows the back button
            frame.setVisible(true);
        }
    });
    
    /*
        Add calendar here
    */
    JButton calendar = new JButton( new AbstractAction("calendar")
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            
        }
    });
    JButton settingsBack = new JButton( new AbstractAction("Back") {
        @Override
        public void actionPerformed( ActionEvent e) {
            hourTenUp.setVisible(false);
            hourOneUp.setVisible(false);
            minTenUp.setVisible(false);
            minOneUp.setVisible(false);
            hourTenDown.setVisible(false);
            hourOneDown.setVisible(false);
            minTenDown.setVisible(false);
            minOneDown.setVisible(false);
            hourTenLabel.setVisible(false);
            hourOneLabel.setVisible(false);
            minTenLabel.setVisible(false);
            minOneLabel.setVisible(false);
            settingsBack.setVisible(false);
            alarmSwitch.setVisible(false);
            playRadio.setVisible(false);
            playOne.setVisible(false);
            playTwo.setVisible(false);
            playThree.setVisible(false);
            
            radioPage.setVisible(true);
            switchUser.setVisible(true);
            alarmSettings.setVisible(true);
            back.setVisible(true);
        }
    });
    JButton alarmSettings = new JButton( new AbstractAction("Alarm Settings") {
        @Override
        public void actionPerformed( ActionEvent e ) {
            alarmSettings.setVisible(false);
        
            weather.setVisible(false); //hides the weather button
            theDay.setVisible(false); //hides the day of the week
            labelHolder.setVisible(false);
            weatherIconLabel.setVisible(false);
            back.setBackground(Color.BLACK);
            back.setVisible(false); //sets the back button to visible
            news.setVisible(false); //hides the news button
            settingsButton.setVisible(false);
            settingsPage.setVisible(true);
            timeLabel.setLocation(5,5);
            timeLabel.setForeground(Color.LIGHT_GRAY);
            settingsPage.add(timeLabel);
            timeLabel.setVisible(true);
            settingsBack.setVisible(true);
            radioPage.setVisible(false);
            switchUser.setVisible(false);
            
            alarmSwitch.setVisible(true);
            hourTenUp.setVisible(true);
            hourOneUp.setVisible(true);
            minTenUp.setVisible(true);
            minOneUp.setVisible(true);
            hourTenDown.setVisible(true);
            hourOneDown.setVisible(true);
            minTenDown.setVisible(true);
            minOneDown.setVisible(true);
            hourTenLabel.setVisible(true);
            hourOneLabel.setVisible(true);
            minTenLabel.setVisible(true);
            minOneLabel.setVisible(true);
             
        }
    });
    JButton radioPage = new JButton (new AbstractAction("RADIO") {
        @Override
        public void actionPerformed( ActionEvent e ) {
            alarmSettings.setVisible(false);
            radioPage.setVisible(false);
            back.setVisible(false);
            switchUser.setVisible(false);
            
            playOne.setVisible(true);
            playTwo.setVisible(true);
            playThree.setVisible(true);
            playRadio.setVisible(true);
            settingsBack.setVisible(true);
}
    });
    JButton playRadio = new JButton(new AbstractAction("Status: OFF") {
        @Override
        public void actionPerformed( ActionEvent e) {
                try {
                    java.lang.Runtime.getRuntime().exec("mpc stop");
                    radioOn = false;
                    switchOnText(radioOn);
                } catch (IOException ex) {
                    Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
    });
    
    public void switchOnText(boolean isOn) {
        if (isOn) {
            playRadio.setText("STOP");
        } else {
            playRadio.setText("Status: OFF");
        }
        
    }
   
    JButton playOne = new JButton(
            new AbstractAction("NPR") {
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                java.lang.Runtime.getRuntime().exec("mpc stop");
                java.lang.Runtime.getRuntime().exec("mpc clear");
                java.lang.Runtime.getRuntime().exec("mpc add " + getActiveUser().radioStationOne.location);
                System.out.println("Playing one");
                java.lang.Runtime.getRuntime().exec("mpc play");
                radioOn = true;
                switchOnText(radioOn);
            }
            catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


    });
    
    JButton playTwo = new JButton(new AbstractAction("NPR2") {
        @Override
        public void actionPerformed(ActionEvent e) {
            try{
                java.lang.Runtime.getRuntime().exec("mpc stop");
                java.lang.Runtime.getRuntime().exec("mpc clear");
                java.lang.Runtime.getRuntime().exec("mpc add " + getActiveUser().radioStationTwo.location);
                java.lang.Runtime.getRuntime().exec("mpc play");
                radioOn = true;
                switchOnText(radioOn);
            }
            catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    });
    
    JButton playThree = new JButton(new AbstractAction("Classic Rock") {
        @Override
        public void actionPerformed(ActionEvent e) {
            try
            {
                java.lang.Runtime.getRuntime().exec("mpc stop");
                java.lang.Runtime.getRuntime().exec("mpc clear");
                java.lang.Runtime.getRuntime().exec("mpc add " + getActiveUser().radioStationThree.location);
                java.lang.Runtime.getRuntime().exec("mpc play");
                radioOn = true;
                switchOnText(radioOn);
            }
            catch (IOException ex)
            {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    });

    JButton alarmSwitch = new JButton ( new AbstractAction() {
        @Override
        public void actionPerformed( ActionEvent e ) {            
            if (alarmIsOn) 
            {
                alarmIsOn = false;
                alarmSwitch.setText("Alarm: OFF");
            }
            else {
                alarmIsOn = true;
                alarmSwitch.setText("Alarm: ON");
            }
        }
    });
    JButton weatherBack = new JButton ( new AbstractAction("WeatherBack") { //takes you back to the timer from weather
        @Override
        public void actionPerformed( ActionEvent e ) {
            settingsButton.setVisible(true);
            weather.setVisible(false);
            weatherMan.setVisible(false);
            timeLabel.setVisible(true);
            weatherIconLabel.setVisible(true);
            theDay.setVisible(true);
            labelHolder.setVisible(true);
            weatherBack.setVisible(false);
            news.setVisible(true);
            weather.setVisible(true);
        }
    });
    JButton back = new JButton( new AbstractAction("BACK") { //back button
                @Override
                public void actionPerformed( ActionEvent e ) { //button is pressed
                    backAction();
        }
    });
    JButton newsRight = new JButton(new AbstractAction("RIGHT") { //only available in the news JTextArea
        @Override
        public void actionPerformed (ActionEvent e) {
            if (newsPage < 3) {
            newsPage++;
            }
            if (newsPage == 0) {
            try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/politics"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: Politics");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            if (newsPage == 1) {
              try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/world"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: World");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }  
            }
            if (newsPage == 2) {
              try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/science"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: Science");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }  
            }
            if (newsPage == 3) {
              try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/tech"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: Tech");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }  
            }
            newsStory = newsStory.substring(0, newsStory.indexOf("<STORY 5>"));
            //removes the <STORY i> from the text
            for( int i = 0; i <= 4; i++) {
                newsStory = newsStory.replace("<STORY " + i + ">", ""); 
            } //end cleanup
            
            timeLabel.setVisible(false); //hides the timer
            weather.setVisible(false); //hides the weather button
            theDay.setVisible(false); //hides the day of the week
            labelHolder.setVisible(false);
            newsText.add(newsLeft);
            newsText.add(newsRight);
            //newsText.add(back);
            frame.add(newsText); //adds the news frame to the main frame
            newsText.setWrapStyleWord(true); //forces the text to stay on the frame
            newsText.setText(newsStory); //sets the text to the top 3 stories
            newsText.setVisible(true);  //sets the news to visible
            newsRight.setVisible(true);
            newsLeft.setVisible(true);
            back.setVisible(true); //sets the back button to visible
            news.setVisible(false); //hides the news button
        }
    });
    JButton newsLeft = new JButton(new AbstractAction("LEFT") {
        @Override
        public void actionPerformed (ActionEvent e) {
            if(newsPage > 0) {
            newsPage--;
            }
            if (newsPage == 0) {
            try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/politics"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: Politics");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            if (newsPage == 1) {
              try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/world"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: World");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }  
            }
            if (newsPage == 2) {
              try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/science"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: Science");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }  
            }
            if (newsPage == 3) {
              try {
                newsStory = readRSS("http://feeds.foxnews.com/foxnews/tech"); //calls the readRSS method for fox news
                newsStory = newsStory.replace("FOX News", "FOX News: Tech");
            } catch (IOException ex) {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }  
            }
            newsStory = newsStory.substring(0, newsStory.indexOf("<STORY 5>"));
            //removes the <STORY i> from the text
            for( int i = 0; i <= 4; i++) {
                newsStory = newsStory.replace("<STORY " + i + ">", ""); 
            } //end cleanup
            
            timeLabel.setVisible(false); //hides the timer
            weather.setVisible(false); //hides the weather button
            theDay.setVisible(false); //hides the day of the week
            labelHolder.setVisible(false);
            newsText.add(newsLeft);
            newsText.add(newsRight);
            //newsText.add(back);
            frame.add(newsText); //adds the news frame to the main frame
            newsText.setWrapStyleWord(true); //forces the text to stay on the frame
            newsText.setText(newsStory); //sets the text to the top 3 stories
            newsText.setVisible(true);  //sets the news to visible
            newsRight.setVisible(true);
            newsLeft.setVisible(true);
            back.setVisible(true); //sets the back button to visible
            news.setVisible(false); //hides the news button
        }
    });
    
    JButton snooze = new JButton( new AbstractAction("SNOOZE")
    {
        public void actionPerformed(ActionEvent e) 
        {
                pzzl = new colorPuzzle(screenSize.width, screenSize.height, "med");
                pzzl.setVisible(true);
        }
    }); 
    
    JButton settingsButton = new JButton( new AbstractAction("SETTINGS") {
        public void actionPerformed(ActionEvent e) {
            weather.setVisible(false); //hides the weather button
            theDay.setVisible(false); //hides the day of the week
            labelHolder.setVisible(false);
            weatherIconLabel.setVisible(false);
            back.setBackground(Color.BLACK);
            back.setVisible(true); //sets the back button to visible
            news.setVisible(false); //hides the news button
            settingsButton.setVisible(false);
            settingsPage.setVisible(true);
            timeLabel.setLocation(5,5);
            timeLabel.setForeground(Color.LIGHT_GRAY);
            settingsPage.add(timeLabel);
            timeLabel.setVisible(true);
            settingsPage.add(alarmSettings);
            frame.add(settingsPage);           
            playRadio.setVisible(false);
            alarmSettings.setVisible(true);
            radioPage.setVisible(true);
            switchUser.setVisible(true);
        }
    });
    
    JButton switchUser = new JButton( new AbstractAction("User: Joe") {
        public void actionPerformed(ActionEvent e) {
            setUser();
        }
    });
    
    public void setUser() {
        
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).isActive) {
                int index = i + 1;
                user newActive = userList.get(0);
                if (i < userList.size() - 1) {
                    activeUser = userList.get(i + 1);
                    userList.get(i).isActive = false;
                    userList.get(index).isActive = true;
                    alarmIsOn = getActiveUser().preferredAlarmStatus;
                }
                else if (i <= userList.size()) {
                    activeUser = userList.get(0);
                    userList.get(0).isActive = true;
                    userList.get(i).isActive = false;
                    alarmIsOn = getActiveUser().preferredAlarmStatus;
                }

                switchUser.setText("User: " + getActiveUser().name);
                playOne.setText(getActiveUser().radioStationOne.title);

                if (newActive.preferredAlarmStatus)
                    alarmSwitch.setText("Alarm: ON");
                else
                    alarmSwitch.setText("Alarm: OFF");
                break;
            }
        }
    }

    JButton hourTenUp = new JButton( new AbstractAction("/\\") {
        public void actionPerformed(ActionEvent e) {
            if (hourTen < 2) {
                hourTen++;
                hourTenLabel.setText(String.valueOf(hourTen));
                System.out.println(hourTen);
            }
        }
    });
    
    JButton hourTenDown = new JButton( new AbstractAction("\\/") {
        public void actionPerformed(ActionEvent e) {
            if (hourTen > 0) {
                hourTen--;
                hourTenLabel.setText(String.valueOf(hourTen));
            }
        }
    });
    
    JButton hourOneUp = new JButton( new AbstractAction("/\\") {
        public void actionPerformed(ActionEvent e) {
            if (hourOne < 10) {
                hourOne++;
                hourOneLabel.setText(String.valueOf(hourOne));               
           }
        }
    });
    
    JButton hourOneDown = new JButton( new AbstractAction("\\/") {
        public void actionPerformed(ActionEvent e) {
            if (hourOne > 0) {
                hourOne--;
                hourOneLabel.setText(String.valueOf(hourOne));               
            }
        }
    });
    
    JButton minTenUp = new JButton( new AbstractAction("/\\") {
        public void actionPerformed(ActionEvent e) {
            if (minuteTen < 6) {
                minuteTen++;
                minTenLabel.setText(String.valueOf(minuteTen));               
            }
        }
    });
    
    JButton minTenDown = new JButton( new AbstractAction("\\/") {
        public void actionPerformed(ActionEvent e) {
            if (minuteTen > 0) {
                minuteTen--;
                minTenLabel.setText(String.valueOf(minuteTen));               
            }
        }
    });
    
    JButton minOneUp = new JButton( new AbstractAction("/\\") {
        public void actionPerformed(ActionEvent e) {
            if (minuteOne < 10) {
                minuteOne++;
                minOneLabel.setText(String.valueOf(minuteOne));               
            }
        }
    });
    
    JButton minOneDown = new JButton( new AbstractAction("\\/") {
        public void actionPerformed(ActionEvent e) {
            if (minuteOne > 0) {
                minuteOne--;
                minOneLabel.setText(String.valueOf(minuteOne));               
           }
        }
    });

    //***END JBUTTONS***//
    
    public static String readRSS(String urlAddress) throws MalformedURLException, IOException { //reads from news websites
        try{
            URL rssUrl = new URL(urlAddress);
            BufferedReader in = new BufferedReader(new InputStreamReader(rssUrl.openStream()));
            String sourceCode = "";
            String line;
            int i = 0;
            while((line = in.readLine()) != null){
                //creates a title
                    if (line.contains("<title>")){
                    i++;
                    int firstPos = line.indexOf("<title>");
                    String temp = line.substring(firstPos);
                    temp = temp.replace("<title>","");
                    int lastPos = temp.indexOf("</title>");
                    temp = temp.substring(0,lastPos);
                    sourceCode += temp+" <STORY " + i + ">\n"; //I use this for my counting purposes

                }
            }
            sourceCode = sourceCode.substring(sourceCode.indexOf("\n") + 1, sourceCode.length()); //removes the second 'Fox News' from fox websites
            in.close();
            return sourceCode; //returns the news
        } catch(MalformedURLException ue){
            System.out.println("Malformed URL");
        } catch (IOException ioe) {
            System.out.println("Something went wrong reading the contents");
        
        }
        return null;

    }
    
    public weatherReturn readRSSWeather(String urlAddress) { //reads from weather websites
        try{
            URL rssUrl = new URL(urlAddress);
            weatherReturn wr = new weatherReturn();
            BufferedReader input = new BufferedReader(new InputStreamReader(rssUrl.openStream()));
            String sourceCode = "";
            String temp = null, temp1 = null, page = null, temperature = null, windChill = null;
            String line,title = "", img = null;
            String high = null, low = null, currentWeather = null;
            String weatherIcon = "null";
            
            while((line = input.readLine()) != null){
                    page += line;
            }
                    //finds the location
                    title = page.substring(page.indexOf("<title>"), page.indexOf("</title>"));
                    title = title.substring(title.indexOf(">") + 1).trim();
                    if (title.contains("weather"))
                        title = title.substring(0, title.indexOf("weather"));
                    
                    //finds the temperature & wind chill 
                    if (page.contains("\"temp\">")) {
                    currentWeather = page.substring(page.indexOf("\"temp\">"));
                    currentWeather = "Temp: " + currentWeather.substring(currentWeather.indexOf(">") + 1, currentWeather.indexOf("F") + 1) + "  ";
                    currentWeather = currentWeather.replace("&deg;", "");
                    } else {
                        currentWeather = "COULD NOT BE FOUND";
                    }
                    
                    
                    if (page.indexOf("\"windchill\">") >= 0) {
                    windChill = page.substring(page.indexOf("\"windchill\">"));
                    windChill = windChill.substring(windChill.indexOf(">") + 1, windChill.indexOf("F") + 1);
                    windChill = "Wind Chill: " + windChill.replace("&deg;", "");
                    } else {
                        windChill = "CANNOT BE FOUND";
                    }
                    
                    //finds the summary of today's weather for a symbol
                    if (page.contains("\"temp high\">")) {
                    high = page.substring(page.indexOf("\"temp high\">"));
                    high = high.substring(high.indexOf(">") + 1, high.indexOf("F") + 1);
                    high = high.replace("&deg;", "");
                    } else {
                        high = "CANNOT BE FOUND";
                    }
                    
                    
                    if (page.contains("\"temp low\">")) {
                    low = page.substring(page.indexOf("\"temp low\">"));
                    low = low.substring(low.indexOf(">") + 1, low.indexOf("F") + 1);
                    low = low.replace("&deg;", "");
                    } else {
                        low = "CANNOT BE FOUND";
                    }

                    if (page.contains("\"summary\">")) {
                    weatherIcon = page.substring(page.indexOf("\"summary\">"));
                    weatherIcon = weatherIcon.substring(weatherIcon.indexOf(">") + 1, weatherIcon.indexOf("<"
                            + ""));
                    } else {
                        weatherIcon = "null";
                    }
                    
                    wr.high = high;
                    wr.low = low;
                    wr.temp = currentWeather;
                    wr.location = title;
                    wr.windChill = windChill;
                    wr.weatherIcon = weatherIcon.toLowerCase();
                    
                    
                    //System.out.println("High: " + wr.high);
                    //System.out.println("Low: " + wr.low);
                    //System.out.println("Temp: " + wr.temp);
                    //System.out.println("Location: " + wr.location);
                    //System.out.println("Wind Chill: " + wr.windChill);
                    input.close();
                    return wr;
            }
        catch(MalformedURLException ue){
            System.out.println("Malformed URL");
        }
        catch (IOException ioe) {
            System.out.println("Something went wrong reading the contents");
        
        }

        return null;
    }
    
    public class weatherReturn {
        String location = "default";
        String temp = "default";
        String windChill = "default";
        String high = "default";
        String low = "default";
        String weatherIcon = "null";
    }
    public class user {
        //Default Values
        String zipcode = "65737";
        String name = "Average Joe";
        radio radioStationOne = new radio("http://181fm-edge1.cdnstream.com/181-eagle_128k.mp3?listenerid=a384a3cc553ecbcfd7abfe8908928348&cb=524153.mp3&type=.flv", "Classic Rock");
        radio radioStationTwo = new radio("http://war.str3am.com:7230/", "NPR");
        radio radioStationThree = new radio("http://war.str3am.com:7250/", "NPR2");
        
        boolean preferredAlarmStatus = true;
        boolean isActive = true;
    }
    public class radio {
        String location;
        String title;
        public radio(String loc, String tit) {
            location = loc;
            title = tit;
        }
    }
    public user getActiveUser() {
        return activeUser;
    }
    
    public String getMinTime()
    {
        Date date = new Date(); //gives the date
        String dateString = date.toString(); 
        
        String MMSS = dateString.substring(14,19);
        return MMSS;
    }
    
    public String getHourTime()
    {
        Date date = new Date(); //gives the date
        String dateString = date.toString(); 
        
        String hhMM = dateString.substring(11,16); //hhMM now only contains hours and minutes
        return hhMM;
    }  
    
    public String getDay()
    {
        Date date = new Date(); //gives the date
        String dateString = date.toString(); 
        
        String DD = date.toString().substring(0,10); //DD now only contains day of the week, and the day of the month
        return DD;
    }
    public final weatherReturn weatherIdent() {
                weatherReturn wr = readRSSWeather("http://www.rssweather.com/zipcode/" +getActiveUser().zipcode + "/wx.php");
                return wr;
    }
    
    public void weatherIconSet(String weatherIcon) {
        if(weatherIcon.contains("sun")) {
            weatherIconLabel.setIcon(sun);
        }
        else if(weatherIcon.contains("snow")) {
            weatherIconLabel.setIcon(snow);
        }
        else if(weatherIcon.contains("cloud") || weatherIcon.contains("overcast")) {
            weatherIconLabel.setIcon(overcast);
        }
        else if(weatherIcon.contains("rain") || weatherIcon.contains("storm")) {
            weatherIconLabel.setIcon(rain);
        }
        else if(weatherIcon.contains("wind")) {
            weatherIconLabel.setIcon(windy);
        }
        else if(weatherIcon.contains("clear")) {
            weatherIconLabel.setIcon(clear);
        }
        else if(weatherIcon.contains("fair")) {
            weatherIconLabel.setIcon(fair);
        }
        else {
            weatherIconLabel.setIcon(nullWeather);
        }
    }

        public void backAction() {
            timeLabel.setLocation(5, (height / 8) + 30);
            timeLabel.setForeground(Color.DARK_GRAY);
            back.setBackground(Color.BLUE);
            settingsButton.setVisible(true);
            settingsPage.setVisible(false);
            newsText.setVisible(false);
            timeLabel.setVisible(true);
            theDay.setVisible(true);
            labelHolder.setVisible(true);
            weatherIconLabel.setVisible(true);
            frame.add(timeLabel);
            frame.add(theDay);  
            frame.add(labelHolder);
            back.setVisible(false);
            newsLeft.setVisible(false);
            newsRight.setVisible(false);
            news.setVisible(true);
            weather.setVisible(true);
            
            alarmSwitch.setVisible(false);
            hourTenUp.setVisible(false);
            hourOneUp.setVisible(false);
            minTenUp.setVisible(false);
            minOneUp.setVisible(false);
            hourTenDown.setVisible(false);
            hourOneDown.setVisible(false);
            minTenDown.setVisible(false);
            minOneDown.setVisible(false);
            hourTenLabel.setVisible(false);
            hourOneLabel.setVisible(false);
            minTenLabel.setVisible(false);
            minOneLabel.setVisible(false);
            settingsBack.setVisible(false);
            radioPage.setVisible(false);
            switchUser.setVisible(true);
        }
        
    
 
    /**
     * silences the Clip object called clip
     */
    public void silence() {
        clip.stop();
    }
    /**
     * 
     * @return the time the alarm will go off 
     */
    public String getAlarmTime() {
        String alarmTime = String.valueOf(hourTen) + String.valueOf(hourOne) + ":" +
                String.valueOf(minuteTen) + String.valueOf(minuteOne);
        return alarmTime;
    }
    
    
class SoundJLayer extends PlaybackListener implements Runnable
{
    private String filePath;
    private AdvancedPlayer player;
    private Thread playerThread;    

    public SoundJLayer(String filePath)
    {
        this.filePath = filePath;
    }

    public void play()
    {
        try
        {
            String urlAsString = 
                "file:///" 
                + new java.io.File(".").getCanonicalPath()          + "/" 
                + this.filePath;

            this.player = new AdvancedPlayer
            (
                new java.net.URL(urlAsString).openStream(),
                javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice()
            );

            this.player.setPlayBackListener(this);

            this.playerThread = new Thread(this, "AudioPlayerThread");

            this.playerThread.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void run()
    {
        try
        {
            this.player.play();
        }
        catch (javazoom.jl.decoder.JavaLayerException ex)
        {
            ex.printStackTrace();
        }

    }
}
/**
 * extension of the class colorChange
 * extended to allow manipulation of alarm music
 * @Overrides the method checkWin
 */
public class colorPuzzle extends colorChange {

        public colorPuzzle(int width, int height, String difficulty) 
        {
            super(width, height, difficulty);
        }
        
        @Override
        //runs when buttons are pressed
        public boolean checkWin(String goal) 
        {
            for (int i = 0; i < sqr.length; i++) 
            {
                if (!goal.equals(sqr[i].color))
                    return false;
            }
            
            //only makes it here if theres a win
            this.setVisible(false); //hides the puzzle
            
            //closes the player
            soundToPlay.close();
            
            try 
            {
                musicIS.close();
                System.out.println("CLOSED");
            }
            catch (IOException ex)
            {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }
            try
            {
                musicIS = DigitalClock.class.getResource("04 Walk This Way.mp3").openStream();
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            isSnoozed = true;
            isPlaying = false;
            snooze.setVisible(false);
            
            try 
            {
                soundToPlay = new AdvancedPlayer(musicIS);
            }
            catch (JavaLayerException ex)
            {
                Logger.getLogger(DigitalClock.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }
    }
    
    
    public static void main(String[] args) throws LineUnavailableException, UnsupportedAudioFileException, JavaLayerException, FileNotFoundException 
    {
        DigitalClock dc = new DigitalClock();
    }
}

