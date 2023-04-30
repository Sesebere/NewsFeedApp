package com.example.newsfeedapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.newsfeedapp.databinding.ActivityMainBinding
import kotlinx.coroutines.runBlocking
import network.APIClient
import network.ArticleResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//de76ec492b8f447981701f10898d1643
class MainActivity : AppCompatActivity() {
    private lateinit var client: Call<ArticleResponse>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        binding.menuBottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.mHome -> navHostFragment?.findNavController()?.navigate(R.id.homeFragment)
                R.id.mSaved -> navHostFragment?.findNavController()?.navigate(R.id.savedFragment)
            }
            true
        }


    }

    override fun onBackPressed() {
        Log.i("onBack", "in OnBackPressed")
        val currentFragment = supportFragmentManager.findFragmentById(R.id.webFragment)
        if (currentFragment is WebFragment) {
            val webView = currentFragment.getWebView()
            if (webView.canGoBack()) {
                Log.i("onBack", "WebView can go back")
                webView.goBack()
                return
            }
        }
        super.onBackPressed()
    }

}
    
