package org;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class syncDays2 extends TestBase2 {


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


        driver.get("https://www.telesport.co.il/%D7%A9%D7%99%D7%93%D7%95%D7%A8%D7%99%20%D7%A1%D7%A4%D7%95%D7%A8%D7%98");

        //First get the day on screen:
        String pageDate1 = driver.findElement(By.className("current")).getText(); //  01/12/22
        String pageDateIncreased1 = add1Day(pageDate1, increaseDayBy, "dd/MM/yy");

        String pageDate2 = driver.findElement(By.className("current")).getText().replace("/22", "/2022").replace("/23", "2023"); //  01/12/2022
        String pageDateIncreased2 = add1Day(pageDate2, increaseDayBy, "dd/MM/yyyy");


        DBHelperPrivate.mysqlConnect();

        //First delete all the past records of last week
        String yesterday = remove1day(pageDate2, "dd/MM/yyyy");
        DBHelperPrivate.executeUpdate("DELETE from `" + db + "`.`games` where game_date = '" + yesterday + "'");


        //Click on the date
        driver.findElement(By.xpath("//*[text()='" + pageDateIncreased1 + "']")).click();


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


        List<WebElement> channels = driver.findElements(By.xpath("//div[@id='bigContext']/div[1]/div[" + xpathIndex + "]/div/div[2]")); //Need to remove first object
        List<WebElement> games = driver.findElements(By.xpath("//div[@id='bigContext']/div[1]/div[" + xpathIndex + "]/div/div[4]"));
        List<WebElement> times = driver.findElements(By.xpath("//div[@id='bigContext']/div[1]/div[" + xpathIndex + "]/div/div[3]")); //Need to remove first object

        channels.remove(0);
        times.remove(0);


        for (int i = 0; i < games.size(); i++) {
            System.out.println(channels.get(i).getText());
            System.out.println(games.get(i).getText());
            System.out.println(times.get(i).getText());

            //increase time by 1 hour
            Date newTime = convertStringToDate(times.get(i).getText());
            String time_end = add1Hour(newTime, 2);


            //trim game name
            String game_name_trim = games.get(i).getText().replace("'", "").replace("\"", "");


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
                            "`day`)\n" +
                            "VALUES\n" +
                            "(\n" +
                            "'" + pageDateIncreased2 + "',\n" +
                            "'" + pageDateIncreased2 + " " + times.get(i).getText() + "',\n" +
                            "'" + pageDateIncreased2 + " " + time_end + "',\n" +
                            "'" + times.get(i).getText() + "',\n" +
                            "'" + channels.get(i).getText() + "',\n" +
                            "'" + game_name_trim + "',\n" +
                            "'white',\n" +
                            "'" + day + "');");

                }
                System.out.println("Record already exists");
            } catch (Exception e) {
                throw new RuntimeException(e);
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

}