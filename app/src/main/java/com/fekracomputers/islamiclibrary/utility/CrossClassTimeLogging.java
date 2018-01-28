package com.fekracomputers.islamiclibrary.utility;

import android.support.annotation.NonNull;

/**
 * Created by Mohammad Yahia on 24/11/2016.
 */

public class CrossClassTimeLogging {
    @NonNull
    private static TimingLogger sTimingLogger = new TimingLogger("CCTL","");

    public static void EnterMehtod(String className,String methodName)
    {
        sTimingLogger.addSplit(" Enter class "+className+"."+methodName+"()");
    }
    public static void ExitrMehtod(String className,String methodName)
    {
        sTimingLogger.addSplit(" Exit class "+className+"."+methodName+"()");
    }
    public static void dumpToLog()
    {
        sTimingLogger.dumpToLog();
    }
}
