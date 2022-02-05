package com.example.audiobook.viewmodels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiobook.models.Book
import com.example.audiobook.Repository
import com.example.audiobook.models.Chapter
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ListBooksViewModel : ViewModel(){
    private var books: MutableLiveData<MutableList<Book>> = MutableLiveData()
    private var chapters: MutableLiveData<ArrayList<Chapter>> = MutableLiveData()
    private var lastPage: MutableLiveData<Int> = MutableLiveData()
    private var description: MutableLiveData<String> = MutableLiveData()
    private val repository = Repository()

    fun getLastPage(url: String): MutableLiveData<Int>
    {
        viewModelScope.launch(IO) {
            lastPage.postValue(repository.getLastPage(url))
        }
        return lastPage
    }

    fun getListBooks(url: String, type: String): MutableLiveData<MutableList<Book>>
    {
        viewModelScope.launch(IO) {
            books.postValue(repository.getListBooks(url, type))
        }
        return books
    }

    fun getChapters(url: String) : MutableLiveData<ArrayList<Chapter>>
    {
        viewModelScope.launch(IO) {
            chapters.postValue(repository.getChapters(url))
        }
        return chapters
    }

    fun getDescription(url: String): MutableLiveData<String>
    {
        viewModelScope.launch(IO) {
            description.postValue(repository.getDescription(url))
        }
        return description
    }
}