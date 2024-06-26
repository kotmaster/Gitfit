package com.example.gitfit

import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gitfit.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    companion object {
        private val metricUnitView = "METRIC_UNIT_VIEW"
        private val imperialUnitView = "IMPERIAL_UNIT_VIEW"
    }

    private var currentVisibleView: String = metricUnitView


    private var binding: ActivityBmiBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.BMIToolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Calculate BMI"
        }
        binding?.BMIToolbar?.setNavigationOnClickListener {
            onBackPressed()// press back button of device
        }

        makeVisibleMetricUnitsView()

        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbMetricUnits) {
                makeVisibleMetricUnitsView()
            } else {
                makeVisibleImperialUnitsView()
            }

        }

        binding?.buttonCalculateUnits?.setOnClickListener {
            CalculateUnits()
        }
    }

    private fun makeVisibleMetricUnitsView() {
        currentVisibleView = metricUnitView
        binding?.textInputMetricUnitHeight?.visibility = View.VISIBLE
        binding?.textInputMetricUnitWeight?.visibility = View.VISIBLE
        binding?.textInputImperialUnitsHeightFeet?.visibility = View.GONE
        binding?.textInputImperialUnitsHeightInch?.visibility = View.GONE
        binding?.textInputImperialUnitsWeight?.visibility = View.GONE

        binding?.editTextMetricUnitHeight?.text?.clear()
        binding?.editTextMetricUnitWeight?.text?.clear()

        binding?.displayBMIResults?.visibility = View.INVISIBLE
    }

    private fun makeVisibleImperialUnitsView() {
        currentVisibleView = imperialUnitView
        binding?.textInputMetricUnitHeight?.visibility = View.GONE
        binding?.textInputMetricUnitWeight?.visibility = View.GONE
        binding?.textInputImperialUnitsHeightFeet?.visibility = View.VISIBLE
        binding?.textInputImperialUnitsHeightInch?.visibility = View.VISIBLE
        binding?.textInputImperialUnitsWeight?.visibility = View.VISIBLE

        binding?.editTextImperialUnitWeight?.text?.clear()
        binding?.editTextImperialUnitHeightFeet?.text?.clear()
        binding?.editTextImperialUnitHeightInch?.text?.clear()

        binding?.displayBMIResults?.visibility = View.INVISIBLE
    }

    private fun displayBMIResults(bmi: Float) {

        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take care of your better! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops! You really need to take care of your better! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take care of your better! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of yourself! Workout!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0) {
            bmiLabel = "Obese Class | (Moderately Obese)"
            bmiDescription = "Oops! You really need to take care of yourself! Workout!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0) {
            bmiLabel = "Obese Class | (Severely Obese)"
            bmiDescription = "Oops! You really need to take care of yourself! Workout!"
        } else {
            bmiLabel = "Obese Class | (Very Severely Obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }
        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()
        binding?.displayBMIResults?.visibility = View.VISIBLE
        binding?.textViewBMIValue?.text = bmiValue
        binding?.textViewBMIDescription?.text = bmiDescription
        binding?.textViewBMIType?.text = bmiLabel
    }

    private fun validateMetricUnits(): Boolean {
        var isValid = true
        if (binding?.editTextMetricUnitHeight?.text.toString().isEmpty()) {
            isValid = false
        } else if (binding?.editTextMetricUnitWeight?.text.toString().isEmpty()) {
            isValid = false
        }
        return isValid
    }

    private fun CalculateUnits() {
        if (currentVisibleView == metricUnitView) {
            if (validateMetricUnits()) {
                val Height: Float =
                    binding?.editTextMetricUnitHeight?.text.toString().toFloat() / 100
                val Weight: Float = binding?.editTextMetricUnitWeight?.text.toString().toFloat()

                val bmi = Weight / (Height * Height)

                displayBMIResults(bmi)
            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Please enter valid values",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            if (validateImperialUnits()) {

                val HeightFeet: Float =
                    binding?.editTextImperialUnitHeightFeet?.text.toString().toFloat()
                val HeightInch: Float =
                    binding?.editTextImperialUnitHeightInch?.text.toString().toFloat()
                val Weight: Float = binding?.editTextImperialUnitWeight?.text.toString().toFloat()

                val Height = HeightFeet * 12 + HeightInch

                val bmi = 703 * (Weight) / (Height * Height)
                displayBMIResults(bmi)

            } else {
                Toast.makeText(
                    this@BMIActivity,
                    "Please enter valid values",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun validateImperialUnits(): Boolean {
        var isValid = true

        when {
            binding?.editTextImperialUnitHeightFeet?.text.toString().isEmpty() -> isValid = false
            binding?.editTextImperialUnitHeightInch?.text.toString().isEmpty() -> isValid = false
            binding?.editTextImperialUnitHeightInch?.text.toString().isEmpty() -> isValid = false
        }
        return isValid
    }
}