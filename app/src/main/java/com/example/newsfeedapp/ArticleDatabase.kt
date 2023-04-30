package com.example.newsfeedapp

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import network.Article
import network.SavedArticle

@Database(entities = [Article::class, SavedArticle::class], version=6)
abstract class ArticleDatabase: RoomDatabase() {
    abstract fun ArticleDao(): ArticleDao

    companion object{
        private val migration_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // add default value to isSaved column
                database.execSQL("ALTER TABLE articles_tbl ADD COLUMN isSaved TEXT NOT NULL DEFAULT 'false'")

                // create the new table
                database.execSQL("CREATE TABLE IF NOT EXISTS articles_tbl_new (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, author TEXT, " +
                        "title TEXT, description TEXT, url TEXT, urlToImage TEXT, isSaved TEXT NOT NULL DEFAULT 'false')")

                // copy the data from the old table to the new table
                database.execSQL("INSERT INTO articles_tbl_new (id , author, title, description, " +
                        "url, urlToImage, isSaved) SELECT id , author, title, description, " +
                        "url, urlToImage, COALESCE(isSaved, 'false') FROM articles_tbl")

                // drop the old table
                database.execSQL("DROP TABLE IF EXISTS articles_tbl")

                // rename the new table to the old table name
                database.execSQL("ALTER TABLE articles_tbl_new RENAME TO articles_tbl")
            }
        }
        private val migration_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // create the new table
                database.execSQL("CREATE TABLE IF NOT EXISTS saved_tbl (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, author TEXT, " +
                        "title TEXT, description TEXT, url TEXT, urlToImage TEXT, isSaved TEXT NOT NULL DEFAULT 'true')")
            }
        }
        private val migration_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // create the new table
//                database.execSQL("ALTER TABLE saved_tbl ADD unique_title UNIQUE (title)")
//                database.execSQL("ALTER TABLE saved_tbl ADD CONSTRAINT unique_title UNIQUE (title)")
//                database.execSQL("CREATE UNIQUE INDEX unique_title ON saved_tbl(title)")
//                database.execSQL("CREATE UNIQUE INDEX index_title ON saved_tbl(title)")

//                database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_title ON saved_tbl (title)")

                database.execSQL("CREATE TABLE IF NOT EXISTS saved_tbl_new (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, author TEXT, " +
                        "title TEXT UNIQUE, description TEXT, url TEXT, urlToImage TEXT, isSaved TEXT NOT NULL DEFAULT 'true')")

                database.execSQL("INSERT INTO saved_tbl_new (id , author, title, description, " +
                        "url, urlToImage, isSaved) SELECT id , author, title, description, " +
                        "url, urlToImage, COALESCE(isSaved, 'true') FROM saved_tbl")

                // drop the old table
                database.execSQL("DROP TABLE IF EXISTS saved_tbl")

                // rename the new table to the old table name
                database.execSQL("ALTER TABLE saved_tbl_new RENAME TO saved_tbl")



            }
        }

        private var INSTANCE:ArticleDatabase? = null
        fun getInstance(context: Context): ArticleDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArticleDatabase::class.java,
                    "article_db"
//                ).addMigrations( migration_5_6).build()
                ).addMigrations(migration_3_4, migration_4_5, migration_5_6).build()
                INSTANCE = instance
                Log.i("migration","migrated")
                instance
            }
        }
    }
}