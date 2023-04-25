package com.example.newsfeedapp

import androidx.room.*
import androidx.room.Insert
import network.Article

@Dao
interface ArticleDao {
    @Insert
    fun insert(vararg articles: Article?)
//    @Insert(tableName = "saved_tbl" )
//    fun insertIntoSaved(vararg articles: Article?)
    @Insert
    fun insertAll(articles: List<Article>?)
    @Update
    fun update(article: Article)
    @Delete
    fun delete(article: Article)

    @Query("INSERT INTO saved_tbl(author, title, description, url, urlToImage, isSaved) VALUES (:author, " +
            ":title, :description, :url, :urlToImage, :isSaved)")
    fun insertIntoSaved(author: String, title: String, description: String, url: String, urlToImage: String, isSaved: String)
    @Query("SELECT * from articles_tbl")
    fun getAllArticles(): List<Article>
    @Query("SELECT * from saved_tbl")
    fun getAllSavedArticles(): MutableList<Article>
    @Query("DELETE from articles_tbl")
    fun deleteAllArticles()
}