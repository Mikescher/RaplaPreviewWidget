package org.semtex;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by ZEUS on 30.08.13.
 */
public class RaplaEntry {
    private static final String[] WEEKDAY_SHORTS = {"So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"};
    private static final String[] WEEKDAY_RAPLA = {"Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"};

    private Calendar startTime;
    private Calendar endTime;

    private String title;

    private List<String> rooms;
    private List<String> courses;
    private List<String> teacher;

    public RaplaEntry() {
        title = "";

        startTime = RaplaUtilies.getNow();
        endTime = RaplaUtilies.getNow();

        rooms = new ArrayList<String>();
        courses = new ArrayList<String>();
    }

    public boolean parse(String code, Calendar date) {
        String plain = removeTooltip(code);
        String tooltip = getTooltip(code);
        if (tooltip == null) return false;

        String time_full = getFullTime(tooltip);
        if (time_full == null) return false;
        String time = getSmallTime(time_full);
        if (time == null) return false;
        String time0 = getTimeDirect(time, 0);
        if (time0 == null) return false;
        String time1 = getTimeDirect(time, 1);
        if (time1 == null) return false;

        int time0_h;
        int time0_m;
        int time1_h;
        int time1_m;
        try {
            time0_h = Integer.parseInt(time0.split(":")[0]);
            time0_m = Integer.parseInt(time0.split(":")[1]);
            time1_h = Integer.parseInt(time1.split(":")[0]);
            time1_m = Integer.parseInt(time1.split(":")[1]);
        } catch (Exception e) {
            return false;
        }

        int weekday = getWeekday(time_full);
        if (weekday < 0) {
            weekday = 0; //TODO get wd info woanders her
        }

        String title = getTitle(plain);
        if (title == null) return false;

        List<String> person = getPerson(plain);
        List<String> classes = getCourseResources(plain);
        List<String> rooms = getRoomResources(plain);

        reformatPersonList(person);

        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);

        this.title = title;

        this.courses = classes;
        this.rooms = rooms;
        this.teacher = person;

        this.startTime.set(year, month, day, time0_h, time0_m, 0);
        this.startTime.add(Calendar.DAY_OF_MONTH, weekday);
        this.endTime.set(year, month, day, time1_h, time1_m, 0);
        this.endTime.add(Calendar.DAY_OF_MONTH, weekday);

        return true;
    }

    private void reformatPersonList(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String person = list.get(i);
            String[] split = person.split(",");
            if (split.length == 2) {
                split[0] = split[0].trim();
                split[1] = split[1].trim();

                String[] vsplit = split[1].split(" ");

                if (vsplit.length > 1) {
                    split[1] = vsplit[0];
                }

                list.set(i, String.format("%s %s", split[1], split[0]));
            }
        }
    }

    private int getWeekday(String html) {
        Matcher matcher = RegexHelper.REGEX_TIME_WEEKDAY.matcher(html);
        if (matcher.find()) {
            String wd = matcher.group();
            for (int i = 0; i < WEEKDAY_RAPLA.length; i++) {
                if (wd.equalsIgnoreCase(WEEKDAY_RAPLA[i])) return i;
            }
        }

        return -1;
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

    private String getFullTime(String html) {
        Matcher matcher = RegexHelper.REGEX_TIME_FULL.matcher(html);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    private String getSmallTime(String html) {
        Matcher matcher = RegexHelper.REGEX_TIME_SMALLEST.matcher(html);

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    private String getTimeDirect(String html, int index) {
        String[] split = RegexHelper.REGEX_TIME_SPLIT.split(html);

        if (split.length > index) return split[index];

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
        return m.find() && m.group().equals(res);
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

    public String getStringRoomList() {
        return RaplaUtilies.joinList(", ", getRooms(), "No rooms assigned");
    }

    public List<String> getCourses() {
        return courses;
    }

    public String getStringCourseList() {
        List<String> otherCourses = new ArrayList<String>();
        List<Integer> tinf11bCourses = new ArrayList<Integer>();
        List<Integer> tinf12bCourses = new ArrayList<Integer>();

        for(String s : getCourses()) {
            if (s.startsWith("TINF12B")) {
                String snum  = s.substring("TINF12B".length());
                try {
                    int num = Integer.parseInt(snum);
                    tinf12bCourses.add(num);
                } catch (NumberFormatException e) {
                    // nothing
                }
            } else if (s.startsWith("TINF11B")) {
                String snum  = s.substring("TINF11B".length());
                try {
                    int num = Integer.parseInt(snum);
                    tinf11bCourses.add(num);
                } catch (NumberFormatException e) {
                    // nothing
                }
            } else {
                otherCourses.add(s);
            }
        }

        List<String> result = new ArrayList<String>();

        Collections.sort(tinf12bCourses);
        Collections.sort(tinf11bCourses);

        if (! tinf12bCourses.isEmpty()) {
            if (tinf12bCourses.size() == 1) {
                result.add("TINF12B" + tinf12bCourses.get(0));
            } else if (RaplaUtilies.isIntListContinous(tinf12bCourses)) {
                result.add(String.format("TINF12B%d-%d", tinf12bCourses.get(0), tinf12bCourses.get(tinf12bCourses.size() - 1)));
            } else {
                result.add(String.format("TINF12B%s", RaplaUtilies.joinList("+", tinf12bCourses, "")));
            }
        }
        if (! tinf11bCourses.isEmpty()) {
            if (tinf11bCourses.size() == 1) {
                result.add("TINF11B" + tinf11bCourses.get(0));
            } else if (RaplaUtilies.isIntListContinous(tinf11bCourses)) {
                result.add(String.format("TINF11B%d-%d", tinf11bCourses.get(0), tinf11bCourses.get(tinf11bCourses.size() - 1)));
            } else {
                result.add(String.format("TINF11B%s", RaplaUtilies.joinList("+", tinf11bCourses, "")));
            }
        }
        if (! otherCourses.isEmpty()) {
            result.addAll(otherCourses);
        }

        return RaplaUtilies.joinList(", ", result, "No courses assigned");
    }

    public List<String> getTeacher() {
        return teacher;
    }

    public String getStringTeacherList() {
        return RaplaUtilies.joinList(", ", getTeacher(), "No lecturer assigned");
    }

    public String getFormattedDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyy");

        return format.format(getStartTime().getTime());
    }

    public String getFormattedStartTime() {
        SimpleDateFormat format = new SimpleDateFormat("kk:mm");

        return format.format(getStartTime().getTime());
    }

    public String getFormattedEndTime() {
        SimpleDateFormat format = new SimpleDateFormat("kk:mm");

        return format.format(getEndTime().getTime());
    }

    public String getFormattedTimeIntervall() {
        return String.format("%s - %s", getFormattedStartTime(), getFormattedEndTime());
    }

    public String getWeekdayString() {
        return WEEKDAY_SHORTS[getStartTime().get(Calendar.DAY_OF_WEEK)-1];
    }

    public String getTimeUntilString() {
        Calendar calendar1 = RaplaUtilies.getNow();
        Calendar calendar2 = getStartTime();

        long milliseconds1 = calendar1.getTimeInMillis();
        long milliseconds2 = calendar2.getTimeInMillis();
        long diff = milliseconds2 - milliseconds1;
        long diffMinutes = Math.round(diff / (60 * 1000.0));
        long diffHours = Math.round(diff / (60 * 60 * 1000.0));
        long diffDays = Math.round(diff / (24 * 60 * 60 * 1000.0));

        if (diffDays > 0) {
            return diffDays + " days";
        }

        if (diffHours > 0) {
            return diffHours + " h";
        }

        return diffMinutes + " min";
    }

    public String getTimeInfoString() {
        return String.format("%s (%s)", getWeekdayString(), getTimeUntilString());
    }

    @Override
    public String toString() {
        return getTitle() + " " + getFormattedDate() + " " + getFormattedTimeIntervall();
    }
}
