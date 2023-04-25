package com.example.newsfeedapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsfeedapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import network.APIClient
import network.Article
import network.ArticleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var articlesFromDb: List<Article>
    private var searchedAndMatchedArticlesFromDb: MutableList<Article> = mutableListOf()
    private lateinit var adapter:  NewsFeedAdapter
    private lateinit var articlesFromApi: List<Article>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        //Initalizing adapter
        adapter = NewsFeedAdapter(requireContext())
        //Setting u[ SwipeRefreshLayout
        with(binding.sfSwipeRefresh){
            setOnRefreshListener {
                doRetrofitThing()
                this.isRefreshing = false
                this.isRefreshing = false
        }
        }
        //Setting up SearchView
        with(binding.searchView){
            setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    performSearch(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    performSearch(newText)
                    return true
                }
            })

            isSubmitButtonEnabled = true
        }

        val recyclerView = binding.rvArticles
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        doRetrofitThing()

        
        Log.d("headlines", "After enqueue")

        return binding.root

    }
    fun performSearch(query: String?) {
        searchedAndMatchedArticlesFromDb.clear()
        query.let{
            articlesFromDb.forEach{
                if(it.title!!.contains(query!!, true)){
                    searchedAndMatchedArticlesFromDb.add(it)
                }
            }
            updateRecyclerView(searchedAndMatchedArticlesFromDb)

            if (searchedAndMatchedArticlesFromDb.isEmpty()) {
                Toast.makeText(requireContext(), "No match found!", Toast.LENGTH_SHORT).show()
                updateRecyclerView(articlesFromDb)
            }
        }
    }
    fun updateRecyclerView(articleList:List<Article>){

        adapter.setArticleList(articleList!!)
    }

    fun loadArticles() {
        //Loading articles to into Db
        GlobalScope.launch(){
            ArticleDatabase.getInstance(requireContext()).ArticleDao().deleteAllArticles()
            articlesFromApi.forEach{it.isSaved = "false"}
            ArticleDatabase.getInstance(requireContext()).ArticleDao().insertAll(articlesFromApi)
            articlesFromDb = ArticleDatabase.getInstance(requireContext()).ArticleDao().getAllArticles()
            Log.v("headlines", "Setting articles to the adpater")
            getActivity()?.runOnUiThread{
                updateRecyclerView(articlesFromDb)
            }
        }
    }
    fun doRetrofitThing() {
        //Retrofit related stuff
        val client =
            APIClient.apiService.fetchHeadlines("us", "de76ec492b8f447981701f10898d1643")

        Log.d("headlines", "Before enqueue")
        client.enqueue(object : Callback<ArticleResponse> {

            override fun onResponse(
                call: Call<ArticleResponse>,
                response: Response<ArticleResponse>
            ) {
                Log.v("headlines", "onResponding")
                if (response.isSuccessful) {
                    Log.v("headlines", "\napi result--->"+response.body().toString())

                    val apiResult = response.body()//This is the list of articles ArticleResponse
                    articlesFromApi = apiResult?.articles!!
                    Log.v("headlines", "\npost api result--->"+articlesFromApi.toString())
                    loadArticles()
                }
            }

            override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                Log.e("headlines", "Response thing unsuccessful")
            }
        })
    }
}

