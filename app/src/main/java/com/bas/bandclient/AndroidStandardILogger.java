package com.bas.bandclient;


import android.util.Log;

import com.ndmsystems.infrastructure.logging.ILogger;

public class AndroidStandardILogger implements ILogger {

    public void v(String message) {
        Log.v(getTag(), message);
    }

    public void d(String message) {
        int maxLogSize = 1000;
        for(int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            Log.d(getTag(), message.substring(start, end));
        }
    }

    public void i(String message) {
        Log.i(getTag(), message);
    }

    public void w(String message) {
        Log.w(getTag(), message);
    }

    public void e(String message) {
        Log.e(getTag(), message);
    }

    private String getTag() {
        String fullClassName = Thread.currentThread().getStackTrace()[5].getClassName();
        String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
        String methodName = Thread.currentThread().getStackTrace()[5].getMethodName();
        int lineNumber = Thread.currentThread().getStackTrace()[5].getLineNumber();

        return className + "." + methodName + "():" + lineNumber;
    }
}
