package com.example.klyver.kotlinsample

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.transition.Visibility
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import kotlinx.android.synthetic.activity_github_user.*
import rx.Observable
import rx.Subscription
import java.util.concurrent.TimeUnit


public class UserDetailActivity : Activity() {

    companion object {
        val EXTRA_USER_LOGIN: String = "EXTRA_USER_LOGIN"
    }

    var userDetailSubscription: Subscription? = null
    var followersSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_user)
        recycler_view.layoutManager = LinearLayoutManager(this)
        val loginname = intent.extras.getString(EXTRA_USER_LOGIN);
//        val loginname = "benjchristensen"

        userDetailSubscription = GitHubDataProvider.getUser(loginname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ g: GithubUser ->
                    login_text_view.text = g.login
                    location_text_view.text = g.location
                    show_followers_button.visibility = View.VISIBLE
                }, {
                    Toast.makeText(this, "Could not get user data, try again later", Toast.LENGTH_LONG).show()
                }, {
                    Log.e("UserDetailActivity", "request completed")
                    Toast.makeText(this, "request completed", Toast.LENGTH_LONG).show()
                })


        val followersReadyObservable: Observable<List<GithubUser>?> = GitHubDataProvider.getFollowers(loginname)
//                .onErrorReturn({null})
                .subscribeOn(Schedulers.io())

        val clickedAndDataReadyObservable: Observable<List<GithubUser>> = Observable.zip(show_followers_button.clickObservable(), followersReadyObservable, { c, d -> d })
        followersSubscription = clickedAndDataReadyObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it == null) {
                        Toast.makeText(this, "Could not get followers, try again later", Toast.LENGTH_LONG).show()
                    } else {
                        show_followers_button.visibility = View.GONE
                        followers_label.visibility = View.VISIBLE
                        recycler_view.adapter = UserListAdapter(this, it)
                    }
                })
    }


    override fun onDestroy() {
        userDetailSubscription!!.unsubscribe()
        followersSubscription!!.unsubscribe()
        super.onDestroy()
    }


}

