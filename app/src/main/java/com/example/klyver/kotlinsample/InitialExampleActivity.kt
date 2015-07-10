package com.example.klyver.kotlinsample

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.activity_initial_example.*

public class InitialExampleActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial_example)










        var clicks = 0;

        screen.setOnTouchListener { view, motionEvent ->
            if (clicks < 10) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("TAG", "(${motionEvent.getX()} , ${motionEvent.getY()})")
                    clicks++;
                }
            } else {
                screen.setOnTouchListener(null)
            }
            false
        }








        Events.touch(screen)
                .filter {it.getAction() == MotionEvent.ACTION_DOWN}
                .take(10)
                .subscribe {Log.d("TAG", "(${it.getX()} , ${it.getY()})")}


    }
}
