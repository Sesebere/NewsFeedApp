package com.example.newsfeedapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.newsfeedapp.databinding.SavedNewsItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import network.Article

class SavedNewsFeedAdapter(val context: Context): RecyclerView.Adapter<SavedNewsFeedAdapter.MyViewHolder>() {

    private var articleList = mutableListOf<Article>()

    fun setArticleList(articles: MutableList<Article>) {
        val oldList = articleList
        val diffResult =  DiffUtil.calculateDiff(
            SavedArticleItemDiffCallback(oldList, articles)
        )
        articleList = articles
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SavedNewsFeedAdapter.MyViewHolder {
        val binding = SavedNewsItemBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
            return articleList!!.size
    }

    override fun onBindViewHolder(holder: SavedNewsFeedAdapter.MyViewHolder, position: Int) {
        val currentItem = articleList?.get(position)
        holder.bind(currentItem, position)
    }


    inner class MyViewHolder(private var binding: SavedNewsItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(article: Article?, position: Int){
            with(binding){
                tvArticleTitle.text = article?.title
                tvArticleDescription.text = article?.description
                tvArticleAuthor.text = article?.author
                with(ivArticleImage){
                    load(article?.urlToImage)
                    setOnClickListener{
                        var bundle = Bundle()
                        bundle.putString("webUrl",article?.url)
                        it.findNavController().navigate(R.id.webFragment, bundle)
                    }
                }
                with(ibDelete){
                    setOnClickListener{
                        Log.i("here1", "I am")
                        Log.i("here2", this@SavedNewsFeedAdapter.articleList.toString())
                        Log.i("here2", "\n")
                        this@SavedNewsFeedAdapter.articleList.remove(article)
                        runBlocking{
                            val somethingDeferred = GlobalScope.async { ArticleDatabase.getInstance(context).
                            ArticleDao().deleteSavedArticleById(article!!.id)}
                            somethingDeferred.await()
                        }
                        Log.i("here2", "\n")
                        Log.i("here3", this@SavedNewsFeedAdapter.articleList.toString())
                        setArticleList(this@SavedNewsFeedAdapter.articleList)
                        notifyItemRemoved(articleList.indexOf(article))
//                        notifyItemRemoved(articleList.removeAt(position))
                    }
                }
            }

        }
    }

}

class SavedArticleItemDiffCallback(
    var oldArticleList: List<Article>,
    var newArticleList: List<Article>
): DiffUtil.Callback(){
    override fun getOldListSize(): Int {
        return oldArticleList.size
    }

    override fun getNewListSize(): Int {
        return newArticleList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return(oldArticleList[oldItemPosition].title == newArticleList[newItemPosition].title)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldArticleList[oldItemPosition].equals(newArticleList[newItemPosition])
    }

}