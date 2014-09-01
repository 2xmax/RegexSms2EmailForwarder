package ru.mmaximov.smsalert.biz;

import java.util.regex.Pattern;

public class MessageFilter {
    private final String phonePattern;
    private final String textPattern;
    private final FilterType type;

    public MessageFilter(String phonePattern, String textPattern, FilterType type) {
        this.phonePattern = phonePattern;
        this.textPattern = textPattern;
        this.type = type;
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.equals("");
    }

    private static boolean checkRegex(String s, String pattern) {
        if (s == null || pattern == null) {
            return true;
        }

        String pat = pattern;
        if (pattern.indexOf('^') < 0 && pattern.indexOf('$') < 0) {
            //without explicit specification of start and end it should look everywhere
            pat = ".*" + pattern + ".*";
        }

        Pattern patObj = Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
        return patObj.matcher(s).matches();
    }

    public boolean matches(Message msg) {
        boolean phoneEnabled = !MessageFilter.isNullOrEmpty(phonePattern);
        boolean textEnabled = !MessageFilter.isNullOrEmpty(textPattern);

        boolean phoneMatches = checkRegex(msg.getPhone(), phonePattern);
        boolean textMatches = checkRegex(msg.getText(), textPattern);

        if (!phoneEnabled) {
            return textMatches;
        }

        if (!textEnabled) {
            return phoneMatches;
        }

        switch (type) {
            case And:
                return phoneMatches && textMatches;
            case Or:
                return phoneMatches || textMatches;
        }

        return true;
    }

    public enum FilterType {
        Or,
        And
    }
}
