package com.example.audiobook.viewmodels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiobook.models.Book
import com.example.audiobook.models.Repository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ListBooksViewModel : ViewModel(){
    private val booksMutableLiveData: MutableLiveData<MutableList<Book>> = MutableLiveData()
    private val lastPageMutableLiveData: MutableLiveData<Int> = MutableLiveData()

    val booksLiveData: LiveData<MutableList<Book>> = booksMutableLiveData
    val lastPageLiveData: LiveData<Int> = lastPageMutableLiveData

    private val repository = Repository()

    fun getLastPage(url: String)
    {
        viewModelScope.launch(IO) {
            lastPageMutableLiveData.postValue(repository.getLastPage(url))
        }
    }

    fun getListBooks(url: String, type: String)
    {
        viewModelScope.launch(IO) {
            booksMutableLiveData.postValue(repository.getListBooks(url, type))
        }
    }
}