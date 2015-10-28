package com.example.klyver.kotlinsample

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.activity_github_user_list.*
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

public class UserListActivity : Activity() {

    var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_user_list)
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        recycler_view.itemAnimator = DefaultItemAnimator();

        subscription = search_field.textObservable()
                .filter{ it.length >= 3 }
                .debounce(500, TimeUnit.MILLISECONDS)
                .flatMap {
                    GitHubDataProvider.findUsersWithCompleteDetails(it)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ list: List<GithubUser> ->
                    recycler_view.adapter = UserListAdapter(this, list)
                }, {
                    Toast.makeText(this, "An error happened, try again later", Toast.LENGTH_LONG).show()
                }, {
                    Log.e("UserListActivity", "request completed")
                    Toast.makeText(this, "request completed", Toast.LENGTH_LONG).show()
                })

    }

    override fun onDestroy() {
        subscription!!.unsubscribe()
        super.onDestroy()
    }


}