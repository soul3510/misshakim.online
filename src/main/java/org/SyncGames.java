package org;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncGames {
    public static String gridURL = "http://192.168.68.120:4444/";
    private static WebDriver driver;

    public static void createDriver() throws Exception {
        try {
            WebDriverManager.chromedriver().setup();
            /**
             * Get read of selenium and chrome logs
             */
            System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
            System.setProperty("webdriver.chrome.silentOutput", "true");
            Logger.getLogger("org.openqa.selenium").setLevel(Level.OFF);
            System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
            /**
             * End of Get read of selenium and chrome logs
             */
            ChromeOptions chromeOptions = new ChromeOptions();

            chromeOptions.addArguments("--start-maximized");
            chromeOptions.setCapability("platform", "WINDOWS");
            chromeOptions.addArguments("--log-level=3");
            chromeOptions.addArguments("--silent");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--headless");

            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.BROWSER, Level.INFO);
            logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
//            driver = new ChromeDriver();
            driver = new RemoteWebDriver(new URL(gridURL), chromeOptions);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.manage().window().maximize();
//        driver.get("https://www.livegames.co.il/broadcastspage.aspx");
            driver.get("https://www.isramedia.net/sports-broadcasts");
            Thread.sleep(3000);
        } catch (Exception ex) {
            killDriver();
            throw new RuntimeException(ex);
        }
    }

    public static void killDriver() throws Exception {
        driver.close();
        driver.quit();
    }

    @Test()
    public void syncGames() throws Exception {

        createDriver();


        LocalDate date_today = LocalDate.now();
        DateTimeFormatter formatters_today = DateTimeFormatter.ofPattern("yyy-MM-dd");
        String date_today_parsed_to_use_in_db = date_today.format(formatters_today);


        LocalDate date_yesterday = LocalDate.now().minusDays(1);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyy-MM-dd");
        String date_yesterday_parsed = date_yesterday.format(formatters);


        LocalDate date_tomorrow = LocalDate.now().plusDays(1);
        DateTimeFormatter formatters_tomm = DateTimeFormatter.ofPattern("yyy-MM-dd");
        String date_tomorrow_parsed = date_tomorrow.format(formatters_tomm);


        try {
            DBHelperPrivate.mysqlConnect();
        } catch (Exception e) {
            try {
                killDriver();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

        /**
         * First parsedDate old dates from DB
         */
        List<String> old_games;
        try {
            old_games = DBHelperPrivate.executeSelectQuery("Select * from u204686394_mishakim.games where date = '" + date_yesterday_parsed + "'", "id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (old_games.size() != 0) {
            try {
                DBHelperPrivate.executeUpdate("DELETE FROM `u204686394_mishakim`.`games`\n" +
                        "WHERE date='" + date_yesterday_parsed + "';");
            } catch (Exception e) {
                try {
                    killDriver();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
        }


        List<WebElement> channel = driver.findElements(By.xpath("//li[@class='channelname']"));
        List<WebElement> time = driver.findElements(By.xpath("//li[@class='time']"));
        List<WebElement> game = driver.findElements(By.xpath("//li[@class='showname']"));


        for (int i = 0; i < game.size(); i++) {
            String isoFormat_end;

            //Remove "-" from today's date
            LocalDate today = LocalDate.now();
            DateTimeFormatter today_formatted = DateTimeFormatter.ofPattern("yyyMMdd");
            String date_today_parsed = today.format(today_formatted);

            //Remove ":" from time
            String time_formatted = time.get(i).getText().replace(":", "");

            //Combine to ISO format
            String isoFormat = date_today_parsed + "T" + time_formatted + "00";

            //Get HH from combined string (index: 9 + 10)
            char index1 = isoFormat.charAt(9);
            char index2 = isoFormat.charAt(10);
            String index1_s = "" + index1;
            String index2_s = "" + index2;

            String index12 = index1_s + index2_s;

            if (index1_s.equals("0")) {
                //convert to int and increase by 1
                int index2_i = Integer.parseInt(index2_s);
                int index2_upgraded_by_1 = index2_i + 1;
                isoFormat_end = date_today_parsed + "T0" + index2_upgraded_by_1;
            } else if (index12.equals("24")) {
                isoFormat_end = date_tomorrow_parsed + "T" + "0100";
            } else {
                int index12_i = Integer.parseInt(index1_s + index2_s);
                int index12_i_upgraded_by_1 = index12_i + 1;
                isoFormat_end = date_today_parsed + "T" + index12_i_upgraded_by_1;
            }
            isoFormat_end = isoFormat_end + "0000";

            String game_name_trim = game.get(i).getText().replace("'", "");

            List<String> date_and_game_from_db = null;
            try {
                date_and_game_from_db = DBHelperPrivate.executeSelectQuery("Select * from u204686394_mishakim.games where game_name = '" + game_name_trim + "' and date = '" + date_today_parsed_to_use_in_db + "'", "game_name");
            } catch (SQLException e) {
                try {
                    killDriver();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                throw new RuntimeException(e);
            }
            if (date_and_game_from_db.size() == 0) {
                {
                    List<String> game_name_from_db = null;
                    try {
                        game_name_from_db = DBHelperPrivate.executeSelectQuery("Select * from u204686394_mishakim.games where game_name = '" + game_name_trim + "' and date = '" + date_today_parsed_to_use_in_db + "'", "game_name");
                    } catch (SQLException e) {
                        try {
                            killDriver();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                        throw new RuntimeException(e);
                    }

                    if (game_name_from_db.size() == 0) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            try {
                                killDriver();
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                            throw new RuntimeException(e);
                        }
                        System.out.println("game: " + game_name_trim + " not exists in DB table. ");

                        System.out.println("date: " + date_today_parsed_to_use_in_db);
                        System.out.println("channel: " + channel.get(i).getText());
                        System.out.println("time: " + time.get(i).getText());
                        System.out.println("game: " + game_name_trim);

                        LocalDate date = LocalDate.now();
                        try {
                            DBHelperPrivate.executeUpdate("INSERT INTO `u204686394_mishakim`.`games`\n" +
                                    "(\n" +
                                    "`date`,\n" +
                                    "`isoFormat`,\n" +
                                    "`isoFormat_end`,\n" +
                                    "`time`,\n" +
                                    "`channel`,\n" +
                                    "`game_name`,\n" +
                                    "`color`)\n" +
                                    "VALUES\n" +
                                    "(\n" +
                                    "'" + date + "',\n" +
                                    "'" + isoFormat + "',\n" +
                                    "'" + isoFormat_end + "',\n" +
                                    "'" + time.get(i).getText() + "',\n" +
                                    "'" + channel.get(i).getText() + "',\n" +
                                    "'" + game_name_trim + "',\n" +
                                    "'" + channelColor(channel.get(i).getText()) + "');");
                        } catch (Exception e) {
                            try {
                                killDriver();
                            } catch (Exception ex) {
                                throw new RuntimeException(ex);
                            }
                            throw new RuntimeException(e);
                        }


                    } else {
                        System.out.println("game already exists in table");
                    }
                }
            } else {
                System.out.println("date and game already exists in DB table. ");
            }
        }
        killDriver();
    }


    public String channelColor(String channel) {
        String color;
        switch (channel) {
            case "ספורט 5":
                color = "blue";
                break;
            case "ספורט 1":
                color = "yellow";
                break;
            case "ספורט 5+ לייב":
                color = "IndianRed";
                break;
            case "כאן 11":
                color = "LightSalmon";
                break;
            case "ספורט 5+":
                color = "LightSalmon";
                break;
            case "5 סטארס":
                color = "Lime";
                break;


            default:
                color = "Fuchsia";
                break;
        }
        return color;
    }
}