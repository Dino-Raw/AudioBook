package com.example.audiobook.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiobook.models.Repository
import com.example.audiobook.models.Chapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookViewModel: ViewModel() {

    private val chaptersMutableLiveData: MutableLiveData<ArrayList<Chapter>> = MutableLiveData()
    private val descriptionMutableLiveData: MutableLiveData<String> = MutableLiveData()
    private val sourceMutableLiveData: MutableLiveData<String> = MutableLiveData()

    val chaptersLiveData = chaptersMutableLiveData
    val descriptionLiveData = descriptionMutableLiveData
    val sourceLiveData = sourceMutableLiveData

    private val repository = Repository()

    fun getChapters(url: String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            chaptersMutableLiveData.postValue(repository.getChapters(url))
        }
    }

    fun getDescription(url: String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            descriptionMutableLiveData.postValue(repository.getDescription(url))
        }
    }

    fun getSource(url: String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            sourceMutableLiveData.postValue(repository.getSource(url))
        }
    }
}