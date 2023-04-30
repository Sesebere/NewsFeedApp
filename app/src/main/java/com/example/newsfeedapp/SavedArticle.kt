package network

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_tbl")
data class SavedArticle(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
//    val source: Source,
    val author: String? = "Unknown",
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    var isSaved: String = "true"
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

