package com.example.klyver.kotlinsample

import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.AbsListView
import android.widget.EditText
import rx.Observable
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject
import rx.subjects.Subject

public class Events {
    companion object Events {
        public fun text(editText: EditText): Observable<String> {
            val behaviourSubject: BehaviorSubject<String> = BehaviorSubject.create(editText.getText().toString());
            editText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    behaviourSubject.onNext(s.toString())
                }
            })
            return behaviourSubject
        }

        public fun click(view: View): Observable<Any> {
            val subject: PublishSubject<Any> = PublishSubject.create()
            view.setOnClickListener({subject.onNext(Any())})
            return subject
        }

        public fun itemClick(view: AbsListView): Observable<Int> {
            val subject: PublishSubject<Int> = PublishSubject.create()
            view.setOnItemClickListener { adapterView, view, position, l ->  subject.onNext(position)}
            return subject
        }

        public fun touch(view: View): Observable<MotionEvent> {
            val subject: PublishSubject<MotionEvent> = PublishSubject.create()
            view.setOnTouchListener {
                view, motionEvent -> subject.onNext(motionEvent)
                true
            }
            return subject
        }
    }
}
