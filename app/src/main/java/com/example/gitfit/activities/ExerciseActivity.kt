package com.example.gitfit.activities

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gitfit.Constants
import com.example.gitfit.models.ExerciseModel
import com.example.gitfit.adapters.ExerciseStatusAdapter
import com.example.gitfit.R
import com.example.gitfit.databinding.ActivityExerciseBinding
import com.example.gitfit.databinding.DialogCustomBackConfirmationBinding
import java.lang.Exception
import java.util.Locale

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restProgress = 0
    private var restTimerDuration: Long = 1
    private var exRestTimer: CountDownTimer? = null
    private var exRestProgress = 0
    private var exTimerDuration: Long = 1
    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition: Int = -1
    private var tts: TextToSpeech? = null
    private var player: MediaPlayer? = null
    private var ExerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.exerciseActivityToolbar)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.exerciseActivityToolbar?.setNavigationOnClickListener {
//            onBackPressed()// press back button of device
            backConfirmation()
        }

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)

        setupRestView()
        setupExerciseStatusRecyclerView()
    }

    override fun onBackPressed() {
        backConfirmation()
    }

    private fun backConfirmation() {
        val customDialog = Dialog(this)
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)

        dialogBinding.buttonYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.buttonNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun setupExerciseStatusRecyclerView() {
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        ExerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = ExerciseAdapter
    }

    private fun setupRestView() {
        // Media Player
        try {
            val soundURI = Uri.parse("android.resource://com.example.gitfit/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player!!.isLooping = false // To play only once
            player!!.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding?.RestProgressView?.visibility = View.VISIBLE
        binding?.progressTitle?.visibility = View.VISIBLE
        binding?.exerciseNameTextView?.visibility = View.INVISIBLE
        binding?.exerciseImage?.visibility = View.INVISIBLE
        binding?.exProgressView?.visibility = View.INVISIBLE
        binding?.upComingExerciseName?.visibility = View.VISIBLE
        binding?.upComingLabel?.visibility = View.VISIBLE

        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        binding?.upComingExerciseName?.text = exerciseList!![currentExercisePosition + 1].getName()
        setRestProgressBar()
    }

    private fun setupExView() {
        binding?.RestProgressView?.visibility = View.INVISIBLE
        binding?.progressTitle?.visibility = View.INVISIBLE
        binding?.exerciseNameTextView?.visibility = View.VISIBLE
        binding?.exerciseImage?.visibility = View.VISIBLE
        binding?.exProgressView?.visibility = View.VISIBLE
        binding?.upComingExerciseName?.visibility = View.INVISIBLE
        binding?.upComingLabel?.visibility = View.INVISIBLE

        if (exRestTimer != null) {
            exRestTimer?.cancel()
            exRestProgress = 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.exerciseImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.exerciseNameTextView?.text = exerciseList!![currentExercisePosition].getName()

        setExProgressBar()
    }

    private fun setRestProgressBar() {

        binding?.RestProgressBar?.progress = restProgress

        restTimer = object : CountDownTimer(restTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.RestProgressBar?.progress = 10 - restProgress
                binding?.RestProgressBarTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++

                exerciseList!![currentExercisePosition].setIsSelected(true)
                ExerciseAdapter!!.notifyDataSetChanged()
                setupExView()
            }
        }.start()
    }


    private fun setExProgressBar() {
        binding?.exProgressBar?.progress = exRestProgress

        exRestTimer = object : CountDownTimer(exTimerDuration * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                exRestProgress++
                binding?.exProgressBar?.progress = 30 - exRestProgress
                binding?.exProgressBarTimer?.text = (30 - exRestProgress).toString()
            }

            override fun onFinish() {

                // If there is still exercise
                if (currentExercisePosition < exerciseList?.size!! - 1) {
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    ExerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                } else {
                    finish()
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }

        if (exRestTimer != null) {
            exRestTimer?.cancel()
            exRestProgress = 0
        }

        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        if (player != null) {
            player!!.stop()
        }

        binding = null
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            var result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "LANG NOT SUPPORTED")
            }

        } else {
            Log.e("TTS", "INITIALISATION FAILED")
        }
    }

    private fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}