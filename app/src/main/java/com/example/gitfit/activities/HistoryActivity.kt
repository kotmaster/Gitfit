package com.example.gitfit.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gitfit.adapters.HistoryAdapter
import com.example.gitfit.HistoryDao
import com.example.gitfit.application.WorkOutApp
import com.example.gitfit.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private var binding: ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.HistoryToolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "History"
        }
        binding?.HistoryToolbar?.setNavigationOnClickListener {
            onBackPressed()// press back button of device
        }
        val dao = (application as WorkOutApp).db.historyDao()
        getAllDates(dao)
    }

    private fun getAllDates(historyDao: HistoryDao) {
        lifecycleScope.launch {
            historyDao.fetchAllDates().collect() { DatesList ->
                if (DatesList.isNotEmpty()) {
                    binding?.textViewHistory?.visibility = View.VISIBLE
                    binding?.recyclerViewHistory?.visibility = View.VISIBLE
                    binding?.textViewNoDate?.visibility = View.INVISIBLE

                    binding?.recyclerViewHistory?.layoutManager =
                        LinearLayoutManager(this@HistoryActivity)

                    val dates = ArrayList<String>()
                    for (data in DatesList) {
                        dates.add(data.date)
                    }
                    val historyAdapter = HistoryAdapter(dates)
                    binding?.recyclerViewHistory?.adapter = historyAdapter

                } else {
                    binding?.textViewHistory?.visibility = View.GONE
                    binding?.recyclerViewHistory?.visibility = View.GONE
                    binding?.textViewNoDate?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}