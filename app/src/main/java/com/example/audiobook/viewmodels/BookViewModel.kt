package com.example.audiobook.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiobook.Repository
import com.example.audiobook.objects.Chapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookViewModel: ViewModel() {

    private var chapters: MutableLiveData<ArrayList<Chapter>> = MutableLiveData()
    private var description: MutableLiveData<String> = MutableLiveData()
    private var source: MutableLiveData<String> = MutableLiveData()
    private val repository = Repository()

    fun getChapters(url: String) : MutableLiveData<ArrayList<Chapter>>
    {
        viewModelScope.launch(Dispatchers.IO) {
            chapters.postValue(repository.getChapters(url))
        }
        return chapters
    }

    fun getDescription(url: String): MutableLiveData<String>
    {
        viewModelScope.launch(Dispatchers.IO) {
            description.postValue(repository.getDescription(url))
        }
        return description
    }

    fun getSource(url: String): MutableLiveData<String>
    {
        viewModelScope.launch(Dispatchers.IO) {
            source.postValue(repository.getSource(url))
        }
        return source
    }
}