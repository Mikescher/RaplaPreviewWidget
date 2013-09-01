package org.semtex;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by ZEUS on 30.08.13.
 */
public class RaplaConnector implements ResultListener {
    private final static String URL_RAPLA = "https://rapla.dhbw-karlsruhe.de/rapla?page=calendar&user=vollmer&file=tinf12b5&day=%d&month=%d&year=%d";

    private final static int ENTRY_SEARCH_RANGE = 10; // show Course even if it started x minutes before

    private final static boolean SKIP_WEEKEND = true;
    private final static int MAX_TRY_COUNT = 4;
    private final static int MAX_FORWARD_COUNT = 16;

    //##############################

    private final RaplaResultListener listener;
    private final Context context;

    private int currentForwardCount;
    private int currentTryCount;
    private Calendar currentDate;

    public RaplaConnector(RaplaResultListener listener, Context context) {
        this.currentTryCount = 0;
        this.currentDate = RaplaUtilies.getNow();
        this.listener = listener;
        this.context = context;
    }

    public void update() {
        new HTMLRequestTask(this).execute(getURL());
    }

    public void recieve(String html) {
        List<RaplaEntry> entries = getEntries(html, (Calendar) currentDate.clone());
        if (entries == null) {
            listener.recieveFailed("Could not parse HTML", this);
        } else {
            RaplaEntry nextEntry = getFirstEntry(entries);
            if (nextEntry != null) {
                listener.recieve(nextEntry, this);
            } else {
                currentDate.add(Calendar.DAY_OF_MONTH, 7);
                currentForwardCount++;
                currentTryCount = 0;

                if (currentForwardCount > MAX_FORWARD_COUNT) {
                    listener.recieveFailed("Max seeking range, no Entrys found", this);
                } else {
                    System.out.println("SEEK NR " + currentForwardCount);
                    update();
                }
            }
        }
    }

    public RaplaEntry getFirstEntry(List<RaplaEntry> entries) {
        if (entries.isEmpty()) return null;

        Calendar now = RaplaUtilies.getNow();
        now.add(Calendar.MINUTE, -ENTRY_SEARCH_RANGE);

        List<RaplaEntry> entries2 = new ArrayList<RaplaEntry>();

        // Remove old entries
        for (RaplaEntry entry : entries) {
            if (entry.getStartTime().after(now)){
                entries2.add(entry);
            }
        }

        RaplaEntry minimumEntry = null;

        for (RaplaEntry entry : entries2) {
            if (minimumEntry == null || entry.getStartTime().before(minimumEntry.getStartTime())) {
                minimumEntry = entry;
            }
        }

        return minimumEntry;
    }

    public void recieveFailed(String errormsg) {
        currentTryCount++;

        if (currentTryCount > MAX_TRY_COUNT) {
            listener.recieveFailed("Max reconnect Try's reached", this);
        } else {
            System.out.println("TRY NR " + currentTryCount);
            update();
        }
    }

    private List<RaplaEntry> getEntries(String html, Calendar date) {
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

                if (entry.parse(block, date)) {
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
        int weekday = (currentDate.get(Calendar.DAY_OF_WEEK) + 5) % 7;

            switch(weekday) {
                case 1:
                    currentDate.add(Calendar.DAY_OF_MONTH, -1);
                    break;
                case 2:
                    currentDate.add(Calendar.DAY_OF_MONTH, -2);
                    break;
                case 3:
                    currentDate.add(Calendar.DAY_OF_MONTH, -3);
                    break;
                case 4:
                    currentDate.add(Calendar.DAY_OF_MONTH, -4);
                    break;
                case 5:
                    currentDate.add(Calendar.DAY_OF_MONTH, 2);
                    break;
                case 6:
                    currentDate.add(Calendar.DAY_OF_MONTH, 1);
                    break;
            }

        int day = currentDate.get(Calendar.DAY_OF_MONTH);
        int month = currentDate.get(Calendar.MONTH) + 1;
        int year = currentDate.get(Calendar.YEAR);

        String url = String.format(URL_RAPLA, day, month, year);

        return url;
    }

    public Context getContext() {
        return context;
    }
}
