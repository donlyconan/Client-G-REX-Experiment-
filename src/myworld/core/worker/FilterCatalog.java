package myworld.core.worker;


public class FilterCatalog {
    public static final long ONE_DAY = 24 * 3600 * 1000;
    public static final String[] Titles = {"Today", "Yesterday", "This Weak"
            , "Last Week", "This Month", "Last Month", "This Year", "Last Year"};


    public static int filterCatalog(long time) {
        time = System.currentTimeMillis() - time;

        //today
        if (time < ONE_DAY)
            return 0;

        //yesterday
        else if (time < 2 * ONE_DAY)
            return 1;

        //this week
        else if (time < 7 * ONE_DAY)
            return 2;

        //last weak
        else if (time < 14 * ONE_DAY)
            return 3;

        //this month
        else if (time < 30 * ONE_DAY)
            return 4;

        //last mothn
        else if (time < 60 * ONE_DAY)
            return 5;

        //this year
        else if (time < 365 * ONE_DAY)
            return 6;

        //last year
         return 7;
    }

}
