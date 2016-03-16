package utils;

import android.util.Log;

/**
 * Created by zy on 15-10-16.
 */
public class Logger {
    public static final boolean OPEN = true;

    public static final String TAG = "ZUIMEI";

    public static int v(String msg){
        return Logger.v(TAG,msg);
    }

    public static int v(String tag,String msg){
        if (!OPEN){
            return -1;
        }
        return Log.v(tag,msg);
    }

    public static int d(String msg){
        return Logger.d(TAG,msg);
    }

    public static int d(String tag,String msg){
        if (!OPEN){
            return -1;
        }
        return Log.d(tag,msg);
    }

    public static int i(String msg){
        return Logger.i(TAG,msg);
    }

    public static int i(String tag,String msg){
        if (!OPEN){
            return -1;
        }
        return Log.i(tag,msg);
    }

    public static int w(String msg){
        return Logger.w(TAG,msg);
    }

    public static int w(String tag,String msg){
        if (!OPEN){
            return -1;
        }
        return Log.w(tag,msg);
    }

    public static int e(String msg){
        return Logger.e(TAG, msg);
    }

    public static int e(String tag, String msg){
        if (!OPEN){
            return -1;
        }
        return Log.e(tag, msg);
    }
}
