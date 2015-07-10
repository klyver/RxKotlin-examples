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
import java.util.*


interface GitHubService {
    @GET("/users/{user}/repos")
    fun listRepos(@Path("user") user: String, cb: Callback<JsonElement>);

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
    val githubService: GitHubService = restAdapter.create(javaClass<GitHubService>())


    fun findUsersWithCompleteDetails(query: String): Observable<List<GithubUser>> =
        findUsers(query)
                .flatMapIterable({it})
                .flatMap({
                    getUser(it)
                })
                .toList()


    fun findUsers(query: String): Observable<List<GithubUser>> =
        githubService.searchUsers(query).map({ jsonElement: JsonElement ->
            val items: JsonArray = jsonElement.getAsJsonObject().get("items").getAsJsonArray()
            items.map {             
                GithubUser(
                        it.getAsJsonObject().get("login").getAsString(),
                        it.getAsJsonObject().get("avatar_url").getAsString(),
                        it.getAsJsonObject().get("followers_url").getAsString(),
                        it.getAsJsonObject().get("repos_url").getAsString(),
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
                        val locationElem: JsonElement? = jsonElement.getAsJsonObject().get("location")
                        val location: String? = if (locationElem == null || locationElem.isJsonNull()) null else locationElem.getAsString()
                        val emailElem: JsonElement? = jsonElement.getAsJsonObject().get("email")
                        val email: String? = if (emailElem == null || emailElem.isJsonNull()) null else emailElem.getAsString()
                        GithubUser(
                                jsonElement.getAsJsonObject().get("login").getAsString(),
                                jsonElement.getAsJsonObject().get("avatar_url").getAsString(),
                                jsonElement.getAsJsonObject().get("followers_url").getAsString(),
                                jsonElement.getAsJsonObject().get("repos_url").getAsString(),
                                location,
                                email)
                    })
    }

    fun getFollowers(loginname: String): Observable<List<GithubUser>> =
            githubService.followers(loginname)
                    .map({ jsonElement: JsonElement ->
                        val jsonArray: JsonArray = jsonElement.getAsJsonArray()
                        val res: ArrayList<GithubUser> = ArrayList()
                        for (i in jsonArray) {
                            val elem = i.getAsJsonObject()
                            res.add(GithubUser(
                                    elem.get("login").getAsString(),
                                    elem.get("avatar_url").getAsString(),
                                    elem.get("followers_url").getAsString(),
                                    elem.get("repos_url").getAsString(),
                                    "",
                                    null))
                        }
                        res
                    })

}
