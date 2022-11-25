package org;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class syncDays extends TestBase {


    @DataProvider(name = "pageIndex")
    public Object[][] pageIndex() {
        return new Object[][]{
//                {"0"},
//                {"1"},
                {"2"},
//                {"3"},
        };
    }

    @Test(dataProvider = "pageIndex")
    public void SyncDays(String pageIndex) throws Exception {
        driver.get("https://m.one.co.il/mobile/general/tvschedule.aspx?d=" + pageIndex); //current day

        //First get the day on screen:
        String pageDate = driver.findElement(By.xpath("//span[@data-font-size='20']")).getText();

        String pageDateOnlyDate = pageDate.substring(8);


        String dbDayToUse = null;
        if (pageDate.contains("א'")) {
            System.out.println("It's Sunday on page: " + pageDate);
            dbDayToUse = "sunday";
        }
        if (pageDate.contains("ב'")) {
            System.out.println("It's Monday on page: " + pageDate);
            dbDayToUse = "monday";
        }
        if (pageDate.contains("ג'")) {
            System.out.println("It's Tuesday on page: " + pageDate);
            dbDayToUse = "tuesday";
        }
        if (pageDate.contains("ד'")) {
            System.out.println("It's Wednesday on page: " + pageDate);
            dbDayToUse = "wednesday";
        }
        if (pageDate.contains("ה'")) {
            System.out.println("It's Thursday on page: " + pageDate);
            dbDayToUse = "thursday";
        }
        if (pageDate.contains("ו'")) {
            System.out.println("It's Friday on page: " + pageDate);
            dbDayToUse = "friday";
        }
        if (pageDate.contains("ש'")) {
            System.out.println("It's Saturday on page: " + pageDate);
            dbDayToUse = "saturday";
        }


        LocalDate date_today = LocalDate.now();
        DateTimeFormatter formatters_today = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date_today_parsed_to_use_in_db = date_today.format(formatters_today);

        LocalDate date_yesterday = LocalDate.now().minusDays(1);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyy-MM-dd");
        String date_yesterday_parsed = date_yesterday.format(formatters);


        LocalDate date_tomorrow = LocalDate.now().plusDays(Long.parseLong(pageIndex));
        DateTimeFormatter formatters_tomm = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date_tomorrow_parsed = date_tomorrow.format(formatters_tomm);

        DBHelperPrivate.mysqlConnect();


        //Delete all data in table before start
        DBHelperPrivate.executeUpdate("DELETE from " + dbDayToUse);

        List<WebElement> game = driver.findElements(By.xpath("//td[@class='name']"));
        List<WebElement> time = driver.findElements(By.xpath("//td[3]"));

        List<String> channelAttributes = new ArrayList<String>();
        for (int i = 0; i < game.size(); i++) {

            WebElement channel = null;
            try {
                channel = driver.findElement(By.xpath("(//td[1])[" + (i + 1) + "]//img"));

                String checkImgExists = channel.getAttribute("src");

                //If img exists
                if (checkImgExists.length() > 0) {
                    if (checkImgExists.contains("mobile_228a2234cacf0bd10ad00bc4530f0a99.png")) {
                        channelAttributes.add("ONE");
                    }
                    if (checkImgExists.contains("mobile_73de6d0730735e3278c4903a614ddc49.png")) {
                        channelAttributes.add("כאן 11");
                    }
                    if (checkImgExists.contains("mobile_0d488502b21f39eec38ab785b4d6775b.png")) {
                        channelAttributes.add("ספורט 5");
                    }
                    if (checkImgExists.contains("mobile_3d0a321cdd543a85edcbce1da609b40e.png")) {
                        channelAttributes.add("ספורט 5 לייב");
                    }
                    if (checkImgExists.contains("mobile_d315b158b259889dd93bd6050895e78d.png")) {
                        channelAttributes.add("ONE 2");
                    }
                    if (checkImgExists.contains("mobile_16f9bc3bfc98b28212bc7668c72987d1.png")) {
                        channelAttributes.add("ספורט 1");
                    }
                    if (checkImgExists.contains("mobile_d8c063d693a1d5e6c2a3588e2f2300e8.png")) {
                        channelAttributes.add("ספורט 5 פלוס");
                    }
                    if (checkImgExists.contains("mobile_f45b6a36a482dbb93131367f0779d016.png")) {
                        channelAttributes.add("ספורט 4");
                    }
                } else {
                    channelAttributes.add(checkImgExists);
                }
            } catch (NoSuchElementException e) {
                channelAttributes.add(channelAttributes.get(i - 1));
            }

            String isoFormat_end;

            //Remove "-" from today's date
            LocalDate today = LocalDate.now();
            DateTimeFormatter today_formatted = DateTimeFormatter.ofPattern("yyyMMdd");
            String date_today_parsed = today.format(today_formatted);

            //Remove ":" from time
            String time_formatted = time.get(i).getText().replace(":", "");

            //Combine to ISO format

            String pageDateOnlyDateOppositeYear = null;
            String pageDateOnlyDateOppositeMonth = null;
            String pageDateOnlyDateOppositeDay = null;
            if (pageDateOnlyDate.length() == 10) {

                pageDateOnlyDateOppositeYear = pageDateOnlyDate.replace("/", "").substring(4);
                pageDateOnlyDateOppositeMonth = pageDateOnlyDate.replace("/", "").substring(2, 4);
                pageDateOnlyDateOppositeDay = pageDateOnlyDate.replace("/", "").substring(0, 2);

            } else if (pageDateOnlyDate.length() == 9) {
                //TODO: if date from page come as 1/12/2022 - need to get length of this date and if its 9 instead of 10 (01/12/2022)

                pageDateOnlyDateOppositeYear = pageDateOnlyDate.replace("/", "").substring(3);
                pageDateOnlyDateOppositeMonth = pageDateOnlyDate.replace("/", "").substring(1, 3);
                pageDateOnlyDateOppositeDay = pageDateOnlyDate.replace("/", "").substring(0, 1);
            }

            String completeDate = pageDateOnlyDateOppositeYear + pageDateOnlyDateOppositeMonth + pageDateOnlyDateOppositeDay;
            String isoFormat = completeDate + "T" + time_formatted + "00";

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
                isoFormat_end = completeDate + "T0" + index2_upgraded_by_1;
            } else if (index12.equals("24")) {
                isoFormat_end = completeDate + "T" + "0100";
            } else {
                int index12_i = Integer.parseInt(index1_s + index2_s);
                int index12_i_upgraded_by_1 = index12_i + 1;
                isoFormat_end = completeDate + "T" + index12_i_upgraded_by_1;
            }
            isoFormat_end = isoFormat_end + "0000";

            String game_name_trim = game.get(i).getText().replace("'", "").replace("\"", "");

            List<String> date_and_game_from_db = null;
            try {
                date_and_game_from_db = DBHelperPrivate.executeSelectQuery("Select * from " + db + "." + dbDayToUse + " where game_name = '" + game_name_trim + "' and date = '" + date_tomorrow_parsed + "'", "game_name");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (date_and_game_from_db.size() == 0) {
                {
                    List<String> game_name_from_db = null;
                    try {
                        game_name_from_db = DBHelperPrivate.executeSelectQuery("Select * from " + db + "." + dbDayToUse + " where game_name = '" + game_name_trim + "' and date = '" + date_tomorrow_parsed + "'", "game_name");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    if (game_name_from_db.size() == 0) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("game: " + game_name_trim + " not exists in DB table. ");

                        System.out.println("date: " + date_tomorrow_parsed);
                        System.out.println("channel: " + channelAttributes.get(i));
                        System.out.println("time: " + time.get(i).getText());
                        System.out.println("game: " + game_name_trim);

                        try {
                            DBHelperPrivate.executeUpdate("INSERT INTO `" + db + "`.`" + dbDayToUse + "`\n" +
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
                                    "'" + pageDateOnlyDate + "',\n" +
                                    "'" + isoFormat + "',\n" +
                                    "'" + isoFormat_end + "',\n" +
                                    "'" + time.get(i).getText() + "',\n" +
                                    "'" + channelAttributes.get(i) + "',\n" +
                                    "'" + game_name_trim + "',\n" +
                                    "'" + channelColor(channelAttributes.get(i)) + "');");
                        } catch (Exception e) {
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