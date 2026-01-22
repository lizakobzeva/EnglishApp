package com.example.englishapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.api.WordConstants.ACTION_ADDWORD
import com.example.api.WordConstants.ACTION_EDITWORD
import com.example.api.WordConstants.ACTION_STUDY
import com.example.impl.words_list.WordsListFragment
import com.example.impl.addword.AddWordFragment
import com.example.impl.editword.EditWordFragment
import com.example.impl.wordsstudy.WordsStudyFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate() called")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(savedInstanceState == null){
            Log.d("MainActivity", "savedInstanceState is null, handling intent")
            handleIntent(intent)
        } else {
            Log.d("MainActivity", "savedInstanceState is not null, skipping handleIntent")
        }
    }
    
    private fun handleIntent(intent: Intent?) {
        Log.d("MainActivity", "handleIntent() called, intent: ${intent?.data}")
        val deeplink = intent?.data
        if (deeplink == null) {
            Log.d("MainActivity", "No deeplink, showing WordsListFragment")
            val fragment = WordsListFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment)
                .commitAllowingStateLoss()
            Log.d("MainActivity", "WordsListFragment transaction committed")
            return
        }

        when(deeplink.toString()) {
            ACTION_ADDWORD-> {
                val addWordFragment = AddWordFragment()

                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, addWordFragment)
                    .addToBackStack("com/example/impl/words_list")
                    .commit()
            }

            ACTION_EDITWORD -> {
                val name = intent.getStringExtra("word_id") ?: ""
                val editWordFragment = EditWordFragment.newInstance(name)

                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, editWordFragment)
                    .addToBackStack("com/example/impl/words_list")
                    .commit()
            }

            ACTION_STUDY -> {
                Log.d("MainActivity", "ACTION_STUDY received, creating WordsStudyFragment")
                val wordStudyFragment = WordsStudyFragment()

                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, wordStudyFragment)
                    .addToBackStack("com/example/impl/words_list")
                    .commit()
                Log.d("MainActivity", "WordsStudyFragment transaction committed")
            }

            else -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, WordsListFragment())
                    .commitAllowingStateLoss()
            }

        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "onNewIntent() called, intent: ${intent.data}")
        setIntent(intent)
        handleIntent(intent)
    }
}