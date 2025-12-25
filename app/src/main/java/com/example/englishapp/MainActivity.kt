package com.example.englishapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.api.WordConstants.ACTION_ADDWORD
import com.example.api.WordConstants.ACTION_EDITWORD
import com.example.api.WordConstants.ACTION_STUDY
import com.example.englishapp.add_word.AddWordFragment
import com.example.impl.words_list.WordsListFragment
import com.example.englishapp.edit_word.EditWordFragment
import com.example.englishapp.words_study.WordsStudyFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(savedInstanceState == null){
            handleIntent(intent)
        }
    }
    
    private fun handleIntent(intent: Intent?) {
        val deeplink = intent?.data
        if (deeplink == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, WordsListFragment())
                .commitAllowingStateLoss()
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
                val wordStudyFragment = WordsStudyFragment()

                supportFragmentManager.beginTransaction()
                    .replace(R.id.main, wordStudyFragment)
                    .addToBackStack("com/example/impl/words_list")
                    .commit()
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
        setIntent(intent)
        handleIntent(intent)
    }
}