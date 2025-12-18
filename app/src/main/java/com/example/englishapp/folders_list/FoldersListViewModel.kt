package com.example.englishapp.folders_list

import androidx.lifecycle.ViewModel

class FoldersListViewModel: ViewModel() {
    fun foldersList(): List<Folders> {
        return Folders.entries
    }
}