package org.semtex;

import java.util.Calendar;
import java.util.List;

/**
 * Created by ZEUS on 01.09.13.
 */
public class RaplaUtilies {

    public final static String ACTION_REFRESH = "org.semtex.intent.action.REFRESH_WIDGET";
    public final static String ACTION_LS_CLICK_1 = "org.semtex.intent.action.LEFTSIDECLICK_1_WIDGET";
    public final static String ACTION_LS_CLICK_2 = "org.semtex.intent.action.LEFTSIDECLICK_2_WIDGET";

    public static String joinList(String seperator, List<? extends Object> list) {
        return joinList(seperator, list, "");
    }

    public static String joinList(String seperator, List<? extends Object> list, String def) {
        if (list.isEmpty()) return def;
        StringBuilder b = new StringBuilder();
        boolean first = true;
        for (Object s : list) {
            if (! first) b.append(seperator);
            b.append(s.toString());
            first = false;
        }
        return b.toString();
    }

    public static boolean isIntListContinous(List<Integer> list) {
        if (list.isEmpty()) return true;

        int curr = list.get(0);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) != curr) return false;
            curr++;
        }

        return true;
    }

    public static Calendar getNow() {
        Calendar c = Calendar.getInstance();
        return c;
    }
}
