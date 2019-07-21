package com.infnitum.sleeptimer

import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.*

class TimerActivity : AppCompatActivity() {
    val NAME_VAL_TIME="time_to_count"

    var START_TIME_IN_MILLIS=0L

    private lateinit var mTextViewCountDown: TextView
    private lateinit var mButtonStartPause: Button
    private lateinit var mButtonReset: Button

    private lateinit var mCountDownTimer: CountDownTimer

    private var mTimerRunning: Boolean = false
    var mTimeLeftInMillis=0L


    val RESULT_ENABLE = 11

    lateinit var devicePolicyManager: DevicePolicyManager
    lateinit var activityManager: ActivityManager
    lateinit var compName: ComponentName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        //init time
        val time=intent.extras.getLong(NAME_VAL_TIME)
        START_TIME_IN_MILLIS=time
        mTimeLeftInMillis=time

        mTextViewCountDown = findViewById(R.id.text_view_countdown)


        mButtonStartPause = findViewById(R.id.button_start_pause)
        mButtonReset = findViewById(R.id.button_reset)


        mButtonReset.setOnClickListener {
            resetTimer()
        }

        mButtonStartPause.setOnClickListener {

            if (mTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        devicePolicyManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        compName = ComponentName(this,MyAdmin::class.java)

        updateCountDownText()
        startTimer()
    }

    private fun startTimer() {
        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                mTimerRunning = false
                mButtonStartPause.setText("Start")
                mButtonStartPause.setVisibility(View.INVISIBLE)
                mButtonReset.setVisibility(View.VISIBLE)

                val active = devicePolicyManager.isAdminActive(compName)
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(homeIntent)

                if (active) {
                    devicePolicyManager.lockNow()
                }
            }
        }.start()

        mTimerRunning = true
        mButtonStartPause.setText("pause")
        mButtonReset.setVisibility(View.INVISIBLE)
    }

    private fun pauseTimer() {
        mCountDownTimer.cancel()
        mTimerRunning = false
        mButtonStartPause.setText("Start")
        mButtonReset.setVisibility(View.VISIBLE)
    }

    private fun resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS
        updateCountDownText()
        mButtonReset.setVisibility(View.INVISIBLE)
        mButtonStartPause.setVisibility(View.VISIBLE)
    }

    private fun updateCountDownText() {
        val minutes = (mTimeLeftInMillis / 1000).toInt() / 60
        val seconds = (mTimeLeftInMillis / 1000).toInt() % 60

        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        mTextViewCountDown.setText(timeLeftFormatted)
    }
}
