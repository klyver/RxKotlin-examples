package com.example.klyver.kotlinsample

import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.http.GET
import retrofit.http.Path
import retrofit.http.Query
import rx.Observable
import rx.lang.kotlin.observable
import java.util.*


object LoginService {

    fun login(email: String, password: String) = observable<Boolean> {
        it.onNext(true)
        it.onCompleted()
//        it.onError(Exception("network error"))
    }

}


interface GitHubService {
    @GET("/users/{user}")
    fun user(@Path("user") user: String): Observable<JsonElement>;

    @GET("/users/{user}/followers")
    fun followers(@Path("user") user: String): Observable<JsonElement>;

    @GET("/users/{repos}/repos")
    fun repos(@Path("user") user: String): Observable<JsonElement>;

    @GET("/search/users")
    fun searchUsers(@Query("q") query: String): Observable<JsonElement>;

}


object GitHubDataProvider {
    val TAG = "GitHubDataProvider";

    val restAdapter: RestAdapter = RestAdapter.Builder().setEndpoint("https://api.github.com").build()
    val githubService: GitHubService = restAdapter.create(GitHubService::class.java)

    fun findUsersWithCompleteDetails(query: String): Observable<List<GithubUser>> =
        findUsers(query)
                .flatMapIterable({it})
                .flatMap({
                    getUser(it)
                })
                .toList()


    fun findUsers(query: String): Observable<List<GithubUser>> =
        githubService.searchUsers(query).map({ jsonElement: JsonElement ->
            val items: JsonArray = jsonElement.asJsonObject.get("items").asJsonArray
            items.map {             
                GithubUser(
                        it.asJsonObject.get("login").asString,
                        it.asJsonObject.get("avatar_url").asString,
                        it.asJsonObject.get("followers_url").asString,
                        it.asJsonObject.get("repos_url").asString,
                        null,
                        null)
            }
        })



    /*
        gets user with complete data
     */
    fun getUser(user: GithubUser): Observable<GithubUser> =
            getUser(user.login)
                .doOnError({
                    Log.d(TAG, "error getting ${user.login} ${it.getMessage()}")
                })
                .onErrorReturn({
                    /*
                        From time to time I get some 403 errors, maybe Github has limits to usage of their api.
                        In this case I just use the data I have already got
                     */
                    user
                })



    fun getUser(loginname: String): Observable<GithubUser> {
            return githubService.user(loginname)
                    .map({ jsonElement: JsonElement ->
                        Log.d(TAG, "got user detail: $loginname")
                        val locationElem: JsonElement? = jsonElement.asJsonObject.get("location")
                        val location: String? = if (locationElem == null || locationElem.isJsonNull) null else locationElem.asString
                        val emailElem: JsonElement? = jsonElement.asJsonObject.get("email")
                        val email: String? = if (emailElem == null || emailElem.isJsonNull) null else emailElem.asString
                        GithubUser(
                                jsonElement.asJsonObject.get("login").asString,
                                jsonElement.asJsonObject.get("avatar_url").asString,
                                jsonElement.asJsonObject.get("followers_url").asString,
                                jsonElement.asJsonObject.get("repos_url").asString,
                                location,
                                email)
                    })
    }

    fun getFollowers(loginname: String): Observable<List<GithubUser>> =
            githubService.followers(loginname)
                    .map({ jsonElement: JsonElement ->
                        val jsonArray: JsonArray = jsonElement.asJsonArray
                        val res: ArrayList<GithubUser> = ArrayList()
                        for (i in jsonArray) {
                            val elem = i.asJsonObject
                            res.add(GithubUser(
                                    elem.get("login").asString,
                                    elem.get("avatar_url").asString,
                                    elem.get("followers_url").asString,
                                    elem.get("repos_url").asString,
                                    "",
                                    null))
                        }
                        res
                    })

}
