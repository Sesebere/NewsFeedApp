package com.example.newsfeedapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsfeedapp.databinding.FragmentSavedBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import network.Article

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SavedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SavedFragment : Fragment() {
    private lateinit var binding: FragmentSavedBinding
    private lateinit var adapter: SavedNewsFeedAdapter
    private var savedArticles = mutableListOf<Article>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedBinding.inflate(inflater, container, false)


        adapter = SavedNewsFeedAdapter(requireContext())
        val recyclerView = binding.rvArticles
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        loadSavedArticles()
//        updateRecyclerView(savedArticles)

        return binding.root
    }


    fun updateRecyclerView(articleList:MutableList<Article>){
        adapter.setArticleList(articleList!!)
    }

    fun loadSavedArticles(){
        GlobalScope.launch {
            savedArticles = ArticleDatabase.getInstance(requireContext()).ArticleDao().getAllSavedArticles()
            withContext(Dispatchers.Main) {
                updateRecyclerView(savedArticles)
                first mess up
                second mess up
                third mess up
                fourth mess up
            }
        }
    }
}