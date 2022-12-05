package org;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestBase2 {

    //    private static String table = "games";
//        private static String gridURL = "http://10.162.10.197:4444/";//new computer (IP not static
    private static final String gridURL = "http://192.168.68.115:4444/"; //old octopai computer
    //    public static String db = "mishakim"; //LOCAL
    public static String db = "u204686394_mishakim"; //REMOTE
    //    private static String gridURL = "http://192.168.68.115:4444/"; // https://www.gridlastic.com/
    public static WebDriver driver;
//    private static final String gridURL = "http://3.229.185:4444/"; //work

//    @BeforeSuite
//    public static void deleteDbs() throws Exception {
//                    DBHelperPrivate.mysqlConnect();
//            DBHelperPrivate.mysqlConnectDisconnect();
//
//    }

    @BeforeMethod
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
//            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.manage().window().maximize();
//            Thread.sleep(3000);
        } catch (Exception ex) {
            killDriver();
            throw new RuntimeException(ex);
        }
    }

    @AfterMethod
    public static void killDriver() throws Exception {
        driver.close();
        driver.quit();
    }

//    @Test()
//    public void syncByDay() throws Exception {
//
//        createDriver();
//
//        for (int x = 0; x < 3; x++) {
//            int dayIndex = x;
//            driver.get("https://m.one.co.il/mobile/general/tvschedule.aspx?d=" + dayIndex);
//
//            //First get the day on screen:
//            String pageDate = driver.findElement(By.xpath("//span[@data-font-size='20']")).getText();
//
//            String dbDayToUse = null;
//            if (pageDate.contains("א")) {
//                System.out.println("It's Sunday on page: " + pageDate);
//                dbDayToUse = "sunday";
//            }
//            if (pageDate.contains("ב")) {
//                System.out.println("It's Monday on page: " + pageDate);
//                dbDayToUse = "monday";
//            }
//            if (pageDate.contains("ג")) {
//                System.out.println("It's Tuesday on page: " + pageDate);
//                dbDayToUse = "tuesday";
//            }
//            if (pageDate.contains("ד")) {
//                System.out.println("It's Wednesday on page: " + pageDate);
//                dbDayToUse = "wednesday";
//            }
//            if (pageDate.contains("ה")) {
//                System.out.println("It's Thursday on page: " + pageDate);
//                dbDayToUse = "thursday";
//            }
//            if (pageDate.contains("ו")) {
//                System.out.println("It's Friday on page: " + pageDate);
//                dbDayToUse = "friday";
//            }
//            if (pageDate.contains("ש")) {
//                System.out.println("It's Saturday on page: " + pageDate);
//                dbDayToUse = "saturday";
//            }
//
//
//            LocalDate date_today = LocalDate.now();
//            DateTimeFormatter formatters_today = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//            String date_today_parsed_to_use_in_db = date_today.format(formatters_today);
//
//            LocalDate date_yesterday = LocalDate.now().minusDays(1);
//            DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyy-MM-dd");
//            String date_yesterday_parsed = date_yesterday.format(formatters);
//
//
//            LocalDate date_tomorrow = LocalDate.now().plusDays(1);
//            DateTimeFormatter formatters_tomm = DateTimeFormatter.ofPattern("yyy-MM-dd");
//            String date_tomorrow_parsed = date_tomorrow.format(formatters_tomm);
//
//
//            try {
//                DBHelperPrivate.mysqlConnect();
//            } catch (Exception e) {
//                try {
//                    killDriver();
//                } catch (Exception ex) {
//                    throw new RuntimeException(ex);
//                }
//                throw new RuntimeException(e);
//            }
//
//            //Delete all data in table before start
//            DBHelperPrivate.executeUpdate("DELETE from " + dbDayToUse);
//
//            List<WebElement> game = driver.findElements(By.xpath("//td[@class='name']"));
//            List<WebElement> time = driver.findElements(By.xpath("//td[3]"));
//
//            List<String> channelAttributes = new ArrayList<String>();
//            for (int i = 0; i < game.size(); i++) {
//
//
//                WebElement channel = null;
//                try {
//                    channel = driver.findElement(By.xpath("(//td[1])[" + (i + 1) + "]//img"));
//
//                    String checkImgExists = channel.getAttribute("src");
//
//                    //If img exists
//                    if (checkImgExists.length() > 0) {
//                        if (checkImgExists.contains("mobile_228a2234cacf0bd10ad00bc4530f0a99.png")) {
//                            channelAttributes.add("ONE");
//                        }
//                        if (checkImgExists.contains("mobile_73de6d0730735e3278c4903a614ddc49.png")) {
//                            channelAttributes.add("כאן 11");
//                        }
//                        if (checkImgExists.contains("mobile_0d488502b21f39eec38ab785b4d6775b.png")) {
//                            channelAttributes.add("ספורט 5");
//                        }
//                        if (checkImgExists.contains("mobile_3d0a321cdd543a85edcbce1da609b40e.png")) {
//                            channelAttributes.add("ספורט 5 לייב");
//                        }
//                    } else {
//                        channelAttributes.add(checkImgExists);
//                    }
//                } catch (NoSuchElementException e) {
//                    channelAttributes.add(channelAttributes.get(i-1));
//                }
//
//                String isoFormat_end;
//
//                //Remove "-" from today's date
//                LocalDate today = LocalDate.now();
//                DateTimeFormatter today_formatted = DateTimeFormatter.ofPattern("yyyMMdd");
//                String date_today_parsed = today.format(today_formatted);
//
//                //Remove ":" from time
//                String time_formatted = time.get(i).getText().replace(":", "");
//
//                //Combine to ISO format
//                String isoFormat = date_today_parsed + "T" + time_formatted + "00";
//
//                //Get HH from combined string (index: 9 + 10)
//                char index1 = isoFormat.charAt(9);
//                char index2 = isoFormat.charAt(10);
//                String index1_s = "" + index1;
//                String index2_s = "" + index2;
//
//                String index12 = index1_s + index2_s;
//
//                if (index1_s.equals("0")) {
//                    //convert to int and increase by 1
//                    int index2_i = Integer.parseInt(index2_s);
//                    int index2_upgraded_by_1 = index2_i + 1;
//                    isoFormat_end = date_today_parsed + "T0" + index2_upgraded_by_1;
//                } else if (index12.equals("24")) {
//                    isoFormat_end = date_tomorrow_parsed + "T" + "0100";
//                } else {
//                    int index12_i = Integer.parseInt(index1_s + index2_s);
//                    int index12_i_upgraded_by_1 = index12_i + 1;
//                    isoFormat_end = date_today_parsed + "T" + index12_i_upgraded_by_1;
//                }
//                isoFormat_end = isoFormat_end + "0000";
//
//                String game_name_trim = game.get(i).getText().replace("'", "");
//
//                List<String> date_and_game_from_db = null;
//                try {
//                    date_and_game_from_db = DBHelperPrivate.executeSelectQuery("Select * from " + db + "." + dbDayToUse + " where game_name = '" + game_name_trim + "' and date = '" + date_today_parsed_to_use_in_db + "'", "game_name");
//                } catch (SQLException e) {
//                    try {
//                        killDriver();
//                    } catch (Exception ex) {
//                        throw new RuntimeException(ex);
//                    }
//                    throw new RuntimeException(e);
//                }
//                if (date_and_game_from_db.size() == 0) {
//                    {
//                        List<String> game_name_from_db = null;
//                        try {
//                            game_name_from_db = DBHelperPrivate.executeSelectQuery("Select * from " + db + "." + dbDayToUse + " where game_name = '" + game_name_trim + "' and date = '" + date_today_parsed_to_use_in_db + "'", "game_name");
//                        } catch (SQLException e) {
//                            try {
//                                killDriver();
//                            } catch (Exception ex) {
//                                throw new RuntimeException(ex);
//                            }
//                            throw new RuntimeException(e);
//                        }
//
//                        if (game_name_from_db.size() == 0) {
//                            try {
//                                Thread.sleep(3000);
//                            } catch (InterruptedException e) {
//                                try {
//                                    killDriver();
//                                } catch (Exception ex) {
//                                    throw new RuntimeException(ex);
//                                }
//                                throw new RuntimeException(e);
//                            }
//                            System.out.println("game: " + game_name_trim + " not exists in DB table. ");
//
//                            System.out.println("date: " + date_today_parsed_to_use_in_db);
//                            System.out.println("channel: " + channelAttributes.get(i));
//                            System.out.println("time: " + time.get(i).getText());
//                            System.out.println("game: " + game_name_trim);
//
//                            try {
//                                DBHelperPrivate.executeUpdate("INSERT INTO `" + db + "`.`" + dbDayToUse + "`\n" +
//                                        "(\n" +
//                                        "`date`,\n" +
//                                        "`isoFormat`,\n" +
//                                        "`isoFormat_end`,\n" +
//                                        "`time`,\n" +
//                                        "`channel`,\n" +
//                                        "`game_name`,\n" +
//                                        "`color`)\n" +
//                                        "VALUES\n" +
//                                        "(\n" +
//                                        "'" + date_today_parsed_to_use_in_db + "',\n" +
//                                        "'" + isoFormat + "',\n" +
//                                        "'" + isoFormat_end + "',\n" +
//                                        "'" + time.get(i).getText() + "',\n" +
//                                        "'" + channelAttributes.get(i) + "',\n" +
//                                        "'" + game_name_trim + "',\n" +
//                                        "'" + channelColor(channelAttributes.get(i)) + "');");
//                            } catch (Exception e) {
//                                try {
//                                    killDriver();
//                                } catch (Exception ex) {
//                                    throw new RuntimeException(ex);
//                                }
//                                throw new RuntimeException(e);
//                            }
//
//
//                        } else {
//                            System.out.println("game already exists in table");
//                        }
//                    }
//                } else {
//                    System.out.println("date and game already exists in DB table. ");
//                }
//            }
//        }
//        killDriver();
//    }

//    public String channelColor(String channel) {
//        String color;
//        switch (channel) {
//            case "ספורט 5":
//                color = "blue";
//                break;
//            case "ספורט 1":
//                color = "yellow";
//                break;
//            case "ספורט 5+ לייב":
//                color = "IndianRed";
//                break;
//            case "כאן 11":
//                color = "LightSalmon";
//                break;
//            case "ספורט 5+":
//                color = "LightSalmon";
//                break;
//            case "5 סטארס":
//                color = "Lime";
//                break;
//
//
//            default:
//                color = "Fuchsia";
//                break;
//        }
//        return color;
//    }
}