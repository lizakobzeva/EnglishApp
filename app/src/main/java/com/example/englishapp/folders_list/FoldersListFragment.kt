package com.example.englishapp.folders_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.englishapp.MainActivity
import com.example.englishapp.R

class FoldersListFragment: Fragment() {
    private val viewModel: FoldersListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.folders_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val container = view.findViewById<ViewGroup>(R.id.folders_list)
        viewModel.foldersList().forEach { folder ->
            val card: LinearLayout = layoutInflater.inflate(R.layout.folder, container, false) as LinearLayout
            val name = card.findViewById<TextView>(R.id.folder_name)
            container.addView(card)

            name.text = folder.title
            card.setOnClickListener {
                val intent = Intent(context, MainActivity::class.java).apply{
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra("feature", folder.key)
                }
                startActivity(intent)
            }
        }
    }


}