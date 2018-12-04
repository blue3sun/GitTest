package com.xy.bizportdemo.util;

import android.util.Log;

/**
 * 日志输出类
 */
public class LogXY {
    private static String TAG = LogXY.class.getSimpleName();
    private static final int MAX_LOG_LENGTH = 2000;
    private static final int MAX_LOG_CUT_CNT = 10;
    private static int logLevel = Log.VERBOSE;
    private static boolean isDebug = false;

    public static void enableDebug(boolean isEnableDebug) {
        isDebug = isEnableDebug;
    }

    public static void setTag(String tag) {
        TAG = tag;
    }

    public static void setLogLevel(int xmcLogLevel) {
        logLevel = xmcLogLevel;
    }

    //log有4k字符长度限制，使用自己分节的方式来输出足够长度的msg
    private static void logCutOffV(String tag, Object str) {
        String formatStr = formatLog(str);
        int strLength = formatStr.length();
        int indexStart = 0;
        int cutCnt = 0;
        String subStr;
        while (indexStart < strLength) {
            int indexEnd = indexStart + MAX_LOG_LENGTH - 1;
            if (indexEnd < strLength) {
                subStr = formatStr.substring(indexStart, indexEnd + 1);
            } else {
                subStr = formatStr.substring(indexStart);
            }
            indexStart += MAX_LOG_LENGTH;
            cutCnt++;
            if(cutCnt > MAX_LOG_CUT_CNT) {
                break;
            } else {
                Log.v(tag, subStr);
            }
        }
    }

    private static void logCutOffD(String tag, Object str) {
        String formatStr = formatLog(str);
        int strLength = formatStr.length();
        int indexStart = 0;
        int cutCnt = 0;
        String subStr;
        while (indexStart < strLength) {
            int indexEnd = indexStart + MAX_LOG_LENGTH - 1;
            if (indexEnd < strLength) {
                subStr = formatStr.substring(indexStart, indexEnd + 1);
            } else {
                subStr = formatStr.substring(indexStart);
            }
            indexStart += MAX_LOG_LENGTH;
            cutCnt++;
            if(cutCnt > MAX_LOG_CUT_CNT) {
                break;
            } else {
                Log.d(tag, subStr);
            }
        }
    }

    private static void logCutOffI(String tag, Object str) {
        String formatStr = formatLog(str);
        int strLength = formatStr.length();
        int indexStart = 0;
        int cutCnt = 0;
        String subStr;
        while (indexStart < strLength) {
            int indexEnd = indexStart + MAX_LOG_LENGTH - 1;
            if (indexEnd < strLength) {
                subStr = formatStr.substring(indexStart, indexEnd + 1);
            } else {
                subStr = formatStr.substring(indexStart);
            }
            indexStart += MAX_LOG_LENGTH;
            cutCnt++;
            if(cutCnt > MAX_LOG_CUT_CNT) {
                break;
            } else {
                Log.i(tag, subStr);
            }
        }
    }

    private static void logCutOffW(String tag, Object str) {
        String formatStr = formatLog(str);
        int strLength = formatStr.length();
        int indexStart = 0;
        int cutCnt = 0;
        String subStr;
        while (indexStart < strLength) {
            int indexEnd = indexStart + MAX_LOG_LENGTH - 1;
            if (indexEnd < strLength) {
                subStr = formatStr.substring(indexStart, indexEnd + 1);
            } else {
                subStr = formatStr.substring(indexStart);
            }
            indexStart += MAX_LOG_LENGTH;
            cutCnt++;
            if(cutCnt > MAX_LOG_CUT_CNT) {
                break;
            } else {
                Log.w(tag, subStr);
            }
        }
    }

    private static void logCutOffE(String tag, Object str) {
        String formatStr = formatLog(str);
        int strLength = formatStr.length();
        int indexStart = 0;
        int cutCnt = 0;
        String subStr;
        while (indexStart < strLength) {
            int indexEnd = indexStart + MAX_LOG_LENGTH - 1;
            if (indexEnd < strLength) {
                subStr = formatStr.substring(indexStart, indexEnd + 1);
            } else {
                subStr = formatStr.substring(indexStart);
            }
            indexStart += MAX_LOG_LENGTH;
            cutCnt++;
            if(cutCnt > MAX_LOG_CUT_CNT) {
                break;
            } else {
                Log.e(tag, subStr);
            }
        }
    }

    public static void v(Object str) {
        v(TAG, str);
    }

    public static void v(String tag, Object str) {
        if (isDebug && logLevel <= Log.VERBOSE) {
            //Log.v(tag, formatLog(str));
            logCutOffV(tag, str);
        }
    }

    public static void d(Object str) {
        d(TAG, str);
    }

    public static void d(String tag, Object str) {
        if (isDebug && logLevel <= Log.DEBUG) {
            //Log.d(tag, formatLog(str));
            logCutOffD(tag, str);
        }
    }

    public static void i(Object str) {
        i(TAG, str);
    }

    public static void i(String tag, Object str) {
        if (isDebug && logLevel <= Log.INFO) {
            //Log.i(tag, formatLog(str));
            logCutOffI(tag, str);
        }
    }

    public static void w(Object str) {
        w(TAG, str);
    }

    public static void w(String tag, Object str) {
        if (isDebug && logLevel <= Log.WARN) {
            //Log.w(tag, formatLog(str));
            logCutOffW(tag, str);
        }
    }

    public static void e(Object str) {
        e(TAG, str);
    }

    public static void e(String tag, Object str) {
        if (isDebug && logLevel <= Log.ERROR) {
            //Log.e(tag, formatLog(str));
            logCutOffE(tag, str);
        }
    }

    public static void e(Exception exception) {
        e(TAG, exception);
    }

    public static void e(String tag, Exception exception) {
        if (isDebug && logLevel <= Log.ERROR) {
            StringBuilder sb = new StringBuilder();
            String methodInfo = getMethodInfo();
            StackTraceElement[] sts = exception.getStackTrace();
            if (methodInfo != null) {
                sb.append(methodInfo + " : " + exception + "\r\n");
            } else {
                sb.append(exception + "\r\n");
            }
            if (sts != null && sts.length > 0) {
                for (StackTraceElement st : sts) {
                    if (st != null) {
                        sb.append("[ at " + st.getClassName() + "." + st.getMethodName() + "(" + st.getFileName() + ":" + st.getLineNumber() + ")" + " ]\r\n");
                    }
                }
            }
            logCutOffE(tag, sb.toString());
        }
    }

    private static String formatLog(Object str) {
        String formatLog = "";
        if(null != str) {
            String methodInfo = getMethodInfo();
            formatLog = (null == methodInfo ? str.toString() : (methodInfo + " : " + str));
        }
        return formatLog;
    }

    private static String getMethodInfo() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null) {
            return null;
        }
        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }
            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }
            if (st.getClassName().equals(LogXY.class.getName())) {
                continue;
            }
            return "[" + Thread.currentThread().getName() + "-" + Thread.currentThread().getId() + " " + st.getFileName() + ":"
                    + st.getLineNumber() + " " + st.getMethodName() + "()]";
        }
        return null;
    }

}
