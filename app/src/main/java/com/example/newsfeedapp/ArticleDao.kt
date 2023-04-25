package com.example.newsfeedapp

import androidx.room.*
import network.Article

@Dao
interface ArticleDao {
    @Insert
    fun insert(vararg articles: Article?)
    @Insert
    fun insertAll(articles: List<Article>?)
    @Update
    fun update(article: Article)
    @Delete
    fun delete(article: Article)

    @Query("SELECT * from articles_tbl")
    fun getAllArticles(): List<Article>
}