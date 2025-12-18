package com.example.englishapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.englishapp.edit_word.EditWordFragment
import com.example.englishapp.words_list.WordsListFragment

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
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, WordsListFragment())
                .commitAllowingStateLoss()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val folder = intent.getStringExtra("feature") ?: "unknown"
        if (folder != "unknown") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, WordsListFragment())
                .addToBackStack(folder)
                .commitAllowingStateLoss()
        } else {
            Toast.makeText(this, "feature not found", Toast.LENGTH_SHORT).show()
        }


        val word = intent.getStringExtra("word") ?: "unknown"
        if (word != "unknown") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, EditWordFragment())
                .addToBackStack(folder)
                .commitAllowingStateLoss()
        } else {
            Toast.makeText(this, "feature not found", Toast.LENGTH_SHORT).show()
        }
    }
}