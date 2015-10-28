package com.example.klyver.kotlinsample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.activity_login.email_edit_text
import kotlinx.android.synthetic.activity_login.login_button
import kotlinx.android.synthetic.activity_login.password_edit_text
import rx.Observable
import rx.subjects.BehaviorSubject

public class LoginActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val usernameObservable = Events.text(email_edit_text)
        val passwordObservable = Events.text(password_edit_text)
        val loginExecutingObservable: BehaviorSubject<Boolean> = BehaviorSubject.create(false)

        Observable
                .combineLatest(usernameObservable, passwordObservable, loginExecutingObservable, {
                    email, password, loginExecuting -> validateEmail(email) && password.length >= 4 && !loginExecuting
                }).subscribe({
                    login_button.isEnabled = it
                })

        login_button.setOnClickListener {
            val email = email_edit_text.editableText.toString()
            val password = password_edit_text.editableText.toString()

            loginExecutingObservable.onNext(true)
            LoginService.login(email, password)
                    .subscribe({
                        if (it) {
                            startActivity(Intent(this, UserListActivity::class.java))
                        } else {
                            Toast.makeText(this, "Wrong email or password", Toast.LENGTH_LONG).show();
                        }
                        loginExecutingObservable.onNext(false)
                    }, {
                        Toast.makeText(this, "Something went wrong, try again later", Toast.LENGTH_LONG).show();
                        loginExecutingObservable.onNext(false)
                    })
        }

        //        ViewObservable.clicks(login_button).subscribe({startActivity(Intent(this, javaClass<MainActivity>()))})
        //        val usernameObservable: Observable<String> = WidgetObservable.text(username_edit, true).map({it.text().toString()})


    }

    fun validateEmail(email: String) : Boolean = email.contains("@")
}

