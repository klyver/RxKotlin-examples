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

fun EditText.textObservable() : Observable<String> {
    val behaviourSubject: BehaviorSubject<String> = BehaviorSubject.create(text.toString());
    this.addTextChangedListener(object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable) {
            behaviourSubject.onNext(s.toString())
        }
    })
    return behaviourSubject
}

fun View.clickObservable() : Observable<Any> {
    val subject: PublishSubject<Any> = PublishSubject.create()
    setOnClickListener({subject.onNext(Any())})
    return subject
}

fun AbsListView.itemClickObservable() : Observable<Int> {
    val subject: PublishSubject<Int> = PublishSubject.create()
    setOnItemClickListener { adapterView, view, position, l ->  subject.onNext(position)}
    return subject
}

fun View.touchObservable() : Observable<MotionEvent> {
    val subject: PublishSubject<MotionEvent> = PublishSubject.create()
    setOnTouchListener {
        view, motionEvent -> subject.onNext(motionEvent)
        true
    }
    return subject
}