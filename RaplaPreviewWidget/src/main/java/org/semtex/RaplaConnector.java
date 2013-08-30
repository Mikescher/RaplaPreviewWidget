package org.semtex;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ZEUS on 30.08.13.
 */
public class RaplaConnector implements ResultListener{
    private final static String URL_RAPLA = "https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=vollmer&file=tinf12b5&day=%d&month=%d&year=%d";

    private final static int MAX_TRY_COUNT = 4;

    //##############################

    private int currentTryCount;
    private Calendar currentDate;

    public RaplaConnector() {
        this.currentTryCount = 0;
        this.currentDate = Calendar.getInstance();
    }

    public boolean update() {
        new HTMLRequestTask(this).execute(getURL());

        return false;
    }

    public void recieve(String html) {
        if (html == null) {
            currentTryCount++;
            currentDate.add(Calendar.DAY_OF_MONTH, 7);

            if (currentTryCount > MAX_TRY_COUNT) {
                System.out.println("ERROR MAX TRY");
                //TODO ERROR
            } else {
                System.out.println("TRY NR " + currentTryCount);
                update();
            }
        } else {
            List<RaplaEntry> entries = getEntries(html);
            if (entries == null) {
                System.out.println("ERROR PARSE");
                //TODO ERROR
            } else {
                for (RaplaEntry e : entries) {
                    System.out.println("ENTRY FOUND: " + e.getTitle());
                    for(String s : e.getCourses()) System.out.println("          S: " + s);
                    for(String s : e.getRooms()) System.out.println("          R: " + s);
                }
            }
        }
    }

    private List<RaplaEntry> getEntries(String html) {
        List<RaplaEntry> result = new ArrayList<RaplaEntry>();
        html = RegexHelper.breakAndTrimText(html);

        Matcher matcher = RegexHelper.REGEX_BLOCKSTART.matcher(html);

        while (matcher.find()) {
            int start = matcher.start();
            int end = getClosingTag(html, matcher.end());

            if (start >= end) {
                return null;
            } else {
                String block = html.substring(start, end);

                RaplaEntry entry = new RaplaEntry();

                if (entry.parse(block)) {
                    result.add(entry);
                } else {
                    return null;
                }
            }
        }

        return result;
    }

    private int getClosingTag(String html, int startIndex)
    {
        int height = 1;

        Matcher matcher = RegexHelper.REGEX_TD_TAG.matcher(html);

        while (matcher.find()) {
            String word = matcher.group();
            int start = matcher.start();
            int end = matcher.end();

            if (start < startIndex) continue;

            if (isClosingTag(word)) {
                height--;
            } else {
                height++;
            }

            if (height == 0)
            {
                return end;
            }
        }

        return -1;
    }
    public boolean isClosingTag(String tag)
    {
        return RegexHelper.REGEX_CLOSING_TAG.matcher(tag).find();
    }

    private String getURL() {
        int weekday = (currentDate.get(Calendar.DAY_OF_WEEK) + 5) % 7 + 1;

        if (weekday == 5) { // Samstag
            currentDate.add(Calendar.DAY_OF_MONTH, 2);
        } else if (weekday == 6) { // Sonntag
            currentDate.add(Calendar.DAY_OF_MONTH, 1);
        }

        int day = currentDate.get(Calendar.DAY_OF_MONTH);
        int month = currentDate.get(Calendar.MONTH) + 1;
        int year = currentDate.get(Calendar.YEAR);

        //TODO REM
        day = 19;//TODO REM
        month = 7;//TODO REM
        year=2013;//TODO REM
        //TODO REM

        String url = String.format(URL_RAPLA, day, month, year);

        return url;
    }
}
