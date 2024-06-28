package com.example.gitfit.application

import android.app.Application
import com.example.gitfit.HistoryDatabase

class WorkOutApp : Application() {
    val db by lazy {
        HistoryDatabase.getInstance(this)
    }
}