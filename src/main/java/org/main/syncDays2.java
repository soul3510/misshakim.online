package org.main;

import org.DBHelperPrivate;
import org.TestBase2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class syncDays2 extends TestBase2 {

    private String completeDate;

    public static Date convertStringToDate(String time) throws Exception {
        SimpleDateFormat formatter5 = new SimpleDateFormat("HH:mm");
        Date bewTime = formatter5.parse(time);
        return bewTime;
    }

    public static int getDayNumberOld(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(date));
        return c.get(Calendar.DAY_OF_WEEK);
    }

    @DataProvider(name = "pageIndex")
    public Object[][] pageIndex() {
        return new Object[][]{
                {0, "1"},
                {1, "2"},
                {2, "3"},
                {3, "4"},
                {4, "5"},
                {5, "6"},
                {6, "7"}
        };
    }

    @Test(dataProvider = "pageIndex")
    public void SyncDays2(int increaseDayBy, String xpathIndex) throws Exception {
        RemoteWebDriver driver = driverContainer.get();

        driver.get("https://www.telesport.co.il/%D7%A9%D7%99%D7%93%D7%95%D7%A8%D7%99%20%D7%A1%D7%A4%D7%95%D7%A8%D7%98");

        //First get the day on screen:
        String pageDate1 = driver.findElement(By.className("current")).getText(); //  01/12/22
        String pageDateIncreased1 = add1Day(pageDate1, increaseDayBy, "dd/MM/yy");

        String pageDate2 = pageDate1.replace("/22", "/2022").replace("/23", "/2023"); //  01/12/2022
        String pageDateIncreased2 = add1Day(pageDate2, increaseDayBy, "dd/MM/yyyy");

        DBHelperPrivate.mysqlConnect();

        //First delete all the past records of last week
        String yesterday = remove1day(pageDate2, "dd/MM/yyyy");
        DBHelperPrivate.executeUpdate("DELETE from `" + db + "`.`games` where game_date = '" + yesterday + "'");


        //Click on the date
        try {
            driver.findElement(By.linkText(pageDateIncreased1)).click();
//            driver.findElement(By.xpath("//*[text()='" + pageDateIncreased1 + "']")).click();
        }catch (Exception e){
            System.out.println(pageDateIncreased1 + " wasn't found.");
            throw new Exception(pageDateIncreased1 + " wasn't found. Opps we have a problem.");
        }

        //day of the week:
        int day_int = getDayNumberOld(pageDateIncreased2, "dd/MM/yyyy");
        String day = null;
        switch (day_int) {
            case 1:
                day = "sunday";
                break;
            case 2:
                day = "monday";
                break;
            case 3:
                day = "tuesday";
                break;
            case 4:
                day = "wednesday";
                break;
            case 5:
                day = "thursday";
                break;
            case 6:
                day = "friday";
                break;
            case 7:
                day = "saturday";
                break;
        }


        //Delete all records for this day
        DBHelperPrivate.executeUpdate("DELETE from `" + db + "`.`games` where day = '" + day + "'");



        List<WebElement> channels = driver.findElements(By.xpath("//div[@id='bigContext']/div[1]/div[" + xpathIndex + "]/div/div[2]")); //Need to remove first object
        List<WebElement> games = driver.findElements(By.xpath("//div[@id='bigContext']/div[1]/div[" + xpathIndex + "]/div/div[4]"));
        List<WebElement> times = driver.findElements(By.xpath("//div[@id='bigContext']/div[1]/div[" + xpathIndex + "]/div/div[3]")); //Need to remove first object

        try {
            channels.remove(0);
        }catch (Exception e){
            throw new Exception("Opps we have a problem. ");
        }
        times.remove(0);


        for (int i = 0; i < games.size(); i++) {
            System.out.println(channels.get(i).getText());
            System.out.println(games.get(i).getText());
            System.out.println(times.get(i).getText());


            //Exclude some games
            if (games.get(i).getText().contains("פאדל")) {
                //don't add to games
            } else {


                //increase time by 1 hour
                Date newTime = convertStringToDate(times.get(i).getText());
                String time_end = add1Hour(newTime, 2);


                //trim game name
                String game_name_trim = games.get(i).getText().replace("'", "").replace("\"", "");


                //Convert date to ISO format for web:
                String isoFormat_start = getIsoFormat_start(pageDateIncreased2, times.get(i).getText());
                String isoFormat_end = getIsoFormat_end(isoFormat_start);


                //First see if record already saved:
                try {
                    List<String> id = DBHelperPrivate.executeSelectQuery("SELECT * FROM `u204686394_mishakim`.`games` where game_name = '" + game_name_trim + "' and game_date = '" + pageDateIncreased2 + "' and time = '" + times.get(i).getText() + "'", "id");
                    if (id.size() == 0) {
                        System.out.println("Record not exists");


                        DBHelperPrivate.executeUpdate("INSERT INTO `" + db + "`.`games`" +
                                "(\n" +
                                "`game_date`,\n" +
                                "`cal_start`,\n" +
                                "`cal_end`,\n" +
                                "`time`,\n" +
                                "`channel`,\n" +
                                "`game_name`,\n" +
                                "`color`,\n" +
                                "`day`,\n" +
                                "`isoFormat_start`,\n" +
                                "`isoFormat_end`)\n" +
                                "VALUES\n" +
                                "(\n" +
                                "'" + pageDateIncreased2 + "',\n" +
                                "'" + pageDateIncreased2 + " " + times.get(i).getText() + "',\n" +
                                "'" + pageDateIncreased2 + " " + time_end + "',\n" +
                                "'" + times.get(i).getText() + "',\n" +
                                "'" + channels.get(i).getText() + "',\n" +
                                "'" + game_name_trim + "',\n" +
                                "'white',\n" +
                                "'" + day + "',\n" +
                                "'" + isoFormat_start + "',\n" +
                                "'" + isoFormat_end + "');");

                    }
                    System.out.println("Record already exists");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public String add1Hour(Date date, int hours) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);

        String originalString = String.valueOf(calendar.getTime());
        Date newDate = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy").parse(originalString);
        String newString = new SimpleDateFormat("HH:mm").format(calendar.getTime()); // 9:00


        System.out.println(newString);
        return newString;
    }

    public String add1Day(String current, int increaseBy, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(current));
        c.add(Calendar.DATE, increaseBy);  // number of days to add
        current = sdf.format(c.getTime());  // current is now the new date
        return current;
    }


    public String remove1Week(String current, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(current));
        c.add(Calendar.DATE, -7);
        current = sdf.format(c.getTime());
        return current;
    }

    public String remove1day(String current, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(current));
        c.add(Calendar.DATE, -1);
        current = sdf.format(c.getTime());
        return current;
    }

    public String getIsoFormat_start(String game_date, String time) throws ParseException {
        //Need to be like this: 20221203T19000000

        String pageDateOnlyDateOppositeYear = null;
        String pageDateOnlyDateOppositeMonth = null;
        String pageDateOnlyDateOppositeDay = null;
        if (game_date.length() == 10) {

            pageDateOnlyDateOppositeYear = game_date.replace("/", "").substring(4);
            pageDateOnlyDateOppositeMonth = game_date.replace("/", "").substring(2, 4);
            pageDateOnlyDateOppositeDay = game_date.replace("/", "").substring(0, 2);

        } else if (game_date.length() == 9) {
            //TODO: if date from page come as 1/12/2022 - need to get length of this date and if its 9 instead of 10 (01/12/2022)

            pageDateOnlyDateOppositeYear = game_date.replace("/", "").substring(3);
            pageDateOnlyDateOppositeMonth = game_date.replace("/", "").substring(1, 3);
            pageDateOnlyDateOppositeDay = game_date.replace("/", "").substring(0, 1);
        }

        completeDate = pageDateOnlyDateOppositeYear + pageDateOnlyDateOppositeMonth + pageDateOnlyDateOppositeDay;


        //Remove ":" from time
        String time_formatted = time.replace(":", "");
        String isoFormat = completeDate + "T" + time_formatted + "0000";
        return isoFormat;
    }


    public String getIsoFormat_end(String isoFormat_start) throws ParseException {

        //Get HH from combined string (index: 9 + 10)
        char index1 = isoFormat_start.charAt(9);
        char index2 = isoFormat_start.charAt(10);
        String index1_s = "" + index1;
        String index2_s = "" + index2;

        String index12 = index1_s + index2_s;
        int index12_i_upgraded_by_1 = 0;
        String isoFormat_end;
        if (index1_s.equals("0")) {
            //convert to int and increase by 1
            int index2_i = Integer.parseInt(index2_s);
            int index2_upgraded_by_1 = index2_i + 2;
            isoFormat_end = completeDate + "T0" + index2_upgraded_by_1;
        } else if (index12.equals("24")) {
            isoFormat_end = completeDate + "T" + "0100";
        } else {
            int index12_i = Integer.parseInt(index1_s + index2_s);
            index12_i_upgraded_by_1 = index12_i + 1;
            isoFormat_end = completeDate + "T" + index12_i_upgraded_by_1;
        }
        isoFormat_end = isoFormat_end + "000000";
        return isoFormat_end;
    }
}