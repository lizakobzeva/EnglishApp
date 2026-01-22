package com.example.impl.wordsstudy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.impl.words_list.WordsListFragment
import com.example.impl.wordsstudy.R

class StudyResultsFragment : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.study_results, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val correctCount = arguments?.getInt("correct_count", 0) ?: 0
        val incorrectCount = arguments?.getInt("incorrect_count", 0) ?: 0
        
        val correctCountText = view.findViewById<TextView>(R.id.correct_count)
        val incorrectCountText = view.findViewById<TextView>(R.id.incorrect_count)
        val closeButton = view.findViewById<Button>(R.id.close_button)
        
        correctCountText.text = correctCount.toString()
        incorrectCountText.text = incorrectCount.toString()
        
        closeButton.setOnClickListener {
            val containerId = requireActivity().resources.getIdentifier("main", "id", requireActivity().packageName)
            parentFragmentManager.beginTransaction()
                .replace(containerId, WordsListFragment())
                .commit()
        }
    }
    
    companion object {
        fun newInstance(correctCount: Int, incorrectCount: Int): Fragment {
            val fragment = StudyResultsFragment()
            fragment.arguments = Bundle().apply {
                putInt("correct_count", correctCount)
                putInt("incorrect_count", incorrectCount)
            }
            return fragment
        }
    }
}


