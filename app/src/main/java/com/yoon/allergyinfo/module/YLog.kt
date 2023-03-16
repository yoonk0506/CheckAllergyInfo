package com.yoon.allergyinfo.module

import android.util.Log

object YLog {
    var DEBUG_TAG = tag()
    //    var DEBUG = BuildConfig.DEBUG
    var DEBUG = true

    private fun tag(): String? {
        return Thread.currentThread().stackTrace[4].let {
            val link = "(${it.fileName}:${it.lineNumber})"
            val path = "ㅇㅇㅇ App# ${it.className.substringAfterLast(".")}.${it.methodName}"
            if (path.length + link.length > 80) {
                "${path.take(80 - link.length)}...${link}"
            } else {
                "$path$link"
            }
        }
    }
    
    fun v(log: String?) {
        if (DEBUG) {
            Log.v(DEBUG_TAG, "$log")
        }
    }

    fun vm(log: String?) {
        if (DEBUG) {
            v(Exception().stackTrace[1].methodName + " = " + log)
        }
    }

    fun d(log: String?) {
        if (DEBUG) {
            Log.d(DEBUG_TAG, "$log")
        }
    }

    fun dm(log: String?) {
        if (DEBUG) {
            d(Exception().stackTrace[1].methodName + " = " + log)
        }
    }

    fun e(log: String?) {
        if (DEBUG) {
            Log.e(DEBUG_TAG, "$log")
        }
    }

    fun em() {
        if (DEBUG) {
            em( "")
        }
    }

    fun em(log: String?) {
        if (DEBUG) {
            e(Exception().stackTrace[1].methodName + " = ㅇㅇㅇ :: " + log)
        }
    }
}