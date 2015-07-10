package com.example.klyver.kotlinsample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.View
import kotlinx.android.synthetic.activity_login.*
import rx.Observable
import rx.android.view.ViewObservable
import rx.android.widget.WidgetObservable
import rx.functions.Func2
import rx.subjects.BehaviorSubject
import java.util.ArrayList

public class LoginActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {
            startActivity(Intent(this, javaClass<UserListActivity>()))
        }

        val usernameObservable = Events.text(username_edit)
        val passwordObservable = Events.text(password_edit)

        Observable
                .combineLatest(usernameObservable, passwordObservable, { s1, s2 -> s1 != "" && s2 != "" })
                .subscribe({login_button.setEnabled(it)})


        //        ViewObservable.clicks(login_button).subscribe({startActivity(Intent(this, javaClass<MainActivity>()))})
        //        val usernameObservable: Observable<String> = WidgetObservable.text(username_edit, true).map({it.text().toString()})
    }
}

