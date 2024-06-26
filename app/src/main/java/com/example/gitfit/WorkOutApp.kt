package com.example.gitfit

import android.app.Application

class WorkOutApp : Application() {
    val db by lazy {
        HistoryDatabase.getInstance(this)
    }
}