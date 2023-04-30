package network

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles_tbl")
data class Article(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
//    val source: Source,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    var isSaved: String = "false"
) {
    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        other as Article
        if (title != other.title) false
        if (author != other.author) false
        if (description != other.description) false
        if (url != other.url) false
        if (urlToImage != other.urlToImage) false

        return true
    }
}

data class Source(
    val id: String,
    val name: String

){
    override fun equals(other: Any?): Boolean {
        if(javaClass != other?.javaClass) return false
        other as Source
        if(id !=other.id) return false
        if(name !=other.name) return false

        return true
    }
}

data class ArticleResponse(
    val articles: List<Article>
)
