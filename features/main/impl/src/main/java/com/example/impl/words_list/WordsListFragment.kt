package com.example.impl.words_list

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Button
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.api.WordConstants.ACTION_ADDWORD
import com.example.api.WordConstants.ACTION_STUDY
import com.example.impl.R
import com.example.impl.data.database.DatabaseProvider
import com.example.impl.data.repository.WordsRepository
import com.example.impl.spacedrepetition.data.repository.SpacedRepetitionRepository
import com.example.impl.spacedrepetition.domain.usecase.GetWordsByStatusUseCase
import com.example.impl.spacedrepetition.ui.WordsListWithSpacedRepetitionViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class WordsListFragment: Fragment() {
    private val viewModel: WordsListWithSpacedRepetitionViewModel by viewModels {
        WordsListWithSpacedRepetitionViewModelFactory(requireContext().applicationContext)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("WordsListFragment", "onCreateView() called")
        val view = inflater.inflate(R.layout.words_list, container, false)
        Log.d("WordsListFragment", "View inflated: ${view != null}")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("WordsListFragment", "onViewCreated() called")
        
        val searchButton = view.findViewById<ImageView>(R.id.btn_search)
        val searchInput = view.findViewById<EditText>(R.id.search_input)
        val addWordButton = view.findViewById<ImageButton>(R.id.btn_add)
        val studyButton = view.findViewById<Button>(R.id.btn_learning)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)
        
        Log.d("WordsListFragment", "Views found - TabLayout: ${tabLayout != null}, ViewPager: ${viewPager != null}")
        
        // Проверка, что TabLayout и ViewPager найдены
        if (tabLayout == null) {
            Log.e("WordsListFragment", "TabLayout is null!")
            return
        }
        if (viewPager == null) {
            Log.e("WordsListFragment", "ViewPager is null!")
            return
        }
        
        Log.d("WordsListFragment", "ViewModel obtained: ${viewModel != null}")

        // Настройка поиска
        searchButton?.let { button ->
            button.isClickable = true
            button.isFocusable = true
            button.setOnClickListener {
                Log.d("WordsListFragment", "Search button clicked")
                searchInput?.let { input ->
                    if (input.visibility == View.GONE) {
                        input.visibility = View.VISIBLE
                        input.requestFocus()
                        Log.d("WordsListFragment", "Search input shown")
                    } else {
                        input.visibility = View.GONE
                        input.setText("")
                        viewModel.setSearchQuery("")
                        Log.d("WordsListFragment", "Search input hidden")
                    }
                } ?: Log.e("WordsListFragment", "Search input is null in click handler")
            }
        } ?: Log.e("WordsListFragment", "Search button is null, cannot set click listener")

        searchInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
        })

        // Кнопки добавления и обучения
        addWordButton.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply {
                data = ACTION_ADDWORD.toUri()
                flags += Intent.FLAG_ACTIVITY_SINGLE_TOP
            }.also(::startActivity)
        }

        studyButton.setOnClickListener {
            Log.d("WordsListFragment", "Study button clicked")
            Intent(Intent.ACTION_VIEW).apply {
                data = ACTION_STUDY.toUri()
                flags += Intent.FLAG_ACTIVITY_SINGLE_TOP
            }.also { intent ->
                Log.d("WordsListFragment", "Starting activity with ACTION_STUDY: ${intent.data}")
                startActivity(intent)
            }
        }

        // Настройка ViewPager и TabLayout
        Log.d("WordsListFragment", "Creating fragments for ViewPager...")
        val fragments = listOf(
            WordsListTabFragment().apply {
                setWordsFlow(viewModel.newWords)
                setViewModel(viewModel)
                Log.d("WordsListFragment", "Created fragment 0 (New Words)")
            },
            WordsListTabFragment().apply {
                setWordsFlow(viewModel.learningWords)
                setViewModel(viewModel)
                Log.d("WordsListFragment", "Created fragment 1 (Learning Words)")
            },
            WordsListTabFragment().apply {
                setWordsFlow(viewModel.learnedWords)
                setViewModel(viewModel)
                Log.d("WordsListFragment", "Created fragment 2 (Learned Words)")
            }
        )

        Log.d("WordsListFragment", "Creating adapter with ${fragments.size} fragments")
        val adapter = WordsListPagerAdapter(requireActivity(), fragments)
        viewPager.adapter = adapter
        Log.d("WordsListFragment", "Adapter set to ViewPager")

        // Связываем TabLayout с ViewPager2
        Log.d("WordsListFragment", "Attaching TabLayoutMediator...")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Новые"
                1 -> "Обучаемые"
                2 -> "Выученные"
                else -> ""
            }
            Log.d("WordsListFragment", "Tab $position created with text: ${tab.text}")
        }.attach()
        Log.d("WordsListFragment", "TabLayoutMediator attached")
        
        // Логирование для отладки
        Log.d("WordsListFragment", "TabLayout and ViewPager initialized")
        Log.d("WordsListFragment", "TabLayout: ${tabLayout != null}, ViewPager: ${viewPager != null}")
        Log.d("WordsListFragment", "TabLayout visibility: ${tabLayout.visibility}, height: ${tabLayout.height}")
        Log.d("WordsListFragment", "ViewPager visibility: ${viewPager.visibility}, height: ${viewPager.height}")
    }
}

// ViewModelFactory для WordsListWithSpacedRepetitionViewModel
class WordsListWithSpacedRepetitionViewModelFactory(
    private val applicationContext: android.content.Context
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordsListWithSpacedRepetitionViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(applicationContext)
            val wordsRepository = WordsRepository(database.wordDao())
            val spacedRepetitionRepository = SpacedRepetitionRepository(database.spacedRepetitionDao())
            val getWordsByStatusUseCase = GetWordsByStatusUseCase(wordsRepository, spacedRepetitionRepository)
            @Suppress("UNCHECKED_CAST")
            return WordsListWithSpacedRepetitionViewModel(getWordsByStatusUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}