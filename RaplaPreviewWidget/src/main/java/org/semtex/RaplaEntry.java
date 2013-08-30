package org.semtex;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by ZEUS on 30.08.13.
 */
public class RaplaEntry {
    private Calendar startTime;
    private Calendar endTime;

    private String title;

    private List<String> rooms;
    private List<String> courses;

    public RaplaEntry() {
        title = "";

        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();

        rooms = new ArrayList<String>();
        courses = new ArrayList<String>();
    }

    public boolean parse(String code) {
        String plain = removeTooltip(code);
        String tooltip = getTooltip(code);
        if (tooltip == null) return false;
        String time = getTime(tooltip);
        if (time == null) return false;
        List<String> person = getPerson(plain);
        String title = getTitle(plain);
        if (title == null) return false;
        List<String> classes = getCourseResources(plain);
        List<String> rooms = getRoomResources(plain);

        this.title = title;
        this.courses = classes;
        this.rooms = rooms;

//        this.startTime =
//        this.endTime =

        return true;
    }

    private String removeTooltip(String html)
    {
        return RegexHelper.breakAndTrimText(RegexHelper.REGEX_TOOLTIP.matcher(html).replaceAll(""));
    }

    private String getTooltip(String html)
    {
        Matcher matcher = RegexHelper.REGEX_TOOLTIP.matcher(html);
        if (matcher.find()) {
            return RegexHelper.breakAndTrimText(matcher.group());
        } else {
            return null;
        }

    }

    private String getTime(String html) {
        Matcher matcher = RegexHelper.REGEX_TIME_FULL.matcher(html);

        if (matcher.find()) {
            String time_full = matcher.group();
            Matcher matcher2 = RegexHelper.REGEX_TIME_SMALL.matcher(time_full);

            if (matcher2.find()) {
                return matcher2.group();
            }
        }

        return null;
    }

    private List<String> getResources(String html) {
        List<String> result = new ArrayList<String>();

        Matcher matcher1 = RegexHelper.REGEX_RESOURCE_FULL.matcher(html);

        while (matcher1.find()) {
            String word = matcher1.group();

            Matcher matcher2 = RegexHelper.REGEX_RESOURCE_SMALL.matcher(word);

            if (matcher2.find()) {
                result.add(matcher2.group());
            }
        }

        return result;
    }

    private List<String> getPerson(String html)
    {
        List<String> result = new ArrayList<String>();
        Matcher matcher1 = RegexHelper.REGEX_PERSON_FULL.matcher(html);

        while (matcher1.find()) {
            String word = matcher1.group();

            Matcher matcher2 = RegexHelper.REGEX_PERSON_SMALL.matcher(word);

            if (matcher2.find())
            {
                result.add(matcher2.group());
            }
        }

        return result;
    }

    private String getTitle(String html)
    {
        Matcher matcher = RegexHelper.REGEX_TITLE.matcher(html);

        if (matcher.find())
        {
            return matcher.group();
        }
        return null;
    }

    private boolean isCourseResource(String res)
    {
        Matcher m = RegexHelper.REGEX_COURSE_RESOURCE.matcher(res);
        if (m.find()) return m.group().equals(res);
        return false;
    }

    public List<String> getCourseResources(String html)
    {
        List<String> resources = getResources(html);
        List<String> result = new ArrayList<String>();

        for (String resource : resources) if (isCourseResource(resource)) result.add(resource);
        return result;
    }

    public List<String> getRoomResources(String html)
    {
        List<String> resources = getResources(html);
        List<String> result = new ArrayList<String>();

        for (String resource : resources) if (!isCourseResource(resource)) result.add(resource);
        return result;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public String getTitle() {
        return title;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public List<String> getRooms() {
        return rooms;
    }

    public List<String> getCourses() {
        return courses;
    }
}
