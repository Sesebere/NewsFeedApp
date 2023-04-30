package com.example.newsfeedapp

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.newsfeedapp.databinding.NewsItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import network.Article

class NewsFeedAdapter(val context: Context): RecyclerView.Adapter<NewsFeedAdapter.MyViewHolder>() {
    private var articleList: List<Article> = emptyList()

    fun setArticleList(articles: List<Article>) {
        val oldList = articleList
        val diffResult =  DiffUtil.calculateDiff(
            ArticleItemDiffCallback(oldList, articles)
        )
        articleList = articles
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsFeedAdapter.MyViewHolder {
        val binding = NewsItemBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
            return articleList!!.size
    }

    override fun onBindViewHolder(holder: NewsFeedAdapter.MyViewHolder, position: Int) {
        val currentItem = articleList?.get(position)
        holder.bind(currentItem)
    }


    inner class MyViewHolder(private var binding: NewsItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(article: Article?){
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
                //Logic for making a previously saved button persist and appear as saved
                GlobalScope.launch(){
                    Log.i("heart", ArticleDatabase.getInstance(context).ArticleDao().getSavedArticleIdByTitle(article?.title!!).toString())

                    if(ArticleDatabase.getInstance(context).ArticleDao().getSavedArticleIdByTitle(article?.title!!)!=0) {
                        Handler(Looper.getMainLooper()).post {
                            article?.isSaved = "true"
                            with(ibSave) {
                                setImageResource(R.drawable.baseline_favorite_24)
                                setBackgroundColor(Color.TRANSPARENT)
                                setColorFilter(ContextCompat.getColor(context, R.color.blue))
                            }
                        }
                    }
                }
                //Implementing Article Saving
                with(ibSave){
                    //Logic for saving a button
                    setOnClickListener{
                        if(article!!.isSaved.equals("false")) {
                            setImageResource(R.drawable.baseline_favorite_24)
                            setBackgroundColor(Color.TRANSPARENT)
                            setColorFilter(ContextCompat.getColor(context, R.color.blue))
                            article.isSaved = "true"
                            GlobalScope.launch{
//                                article.isSaved = "true"
//                                ArticleDatabase.getInstance(context).ArticleDao().update(article)
                                Log.i("null", article.toString())
                                try{
                                    ArticleDatabase.getInstance(context).ArticleDao().insertIntoSaved(article.author,
                                        article.title!!, article.description!!, article.url!!, article.urlToImage!!, article.isSaved!!)
                                }catch(e: SQLiteConstraintException){
                                    Handler(Looper.getMainLooper()).post{
                                        Toast.makeText(context, "You already saved this article", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                 }
                        }else{
                            setImageResource(R.drawable.baseline_favorite_border_24)
                            setColorFilter(ContextCompat.getColor(context, R.color.black))
                            setBackgroundColor(Color.TRANSPARENT)
                            article.isSaved = "false"
                            GlobalScope.launch(){
                                ArticleDatabase.getInstance(context).ArticleDao().deleteSavedArticleByTitle(article.title!!)
                            }
                        }

                    }
                }
            }

        }
    }

}

class ArticleItemDiffCallback(
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