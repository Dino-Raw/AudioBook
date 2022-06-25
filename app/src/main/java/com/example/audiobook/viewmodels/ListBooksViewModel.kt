package com.example.audiobook.viewmodels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiobook.objects.Book
import com.example.audiobook.Repository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ListBooksViewModel : ViewModel(){
    private val booksMutableLiveData: MutableLiveData<MutableList<Book>> = MutableLiveData()
    private val lastPageMutableLiveData: MutableLiveData<Int> = MutableLiveData()

    val booksLiveData: LiveData<MutableList<Book>> = booksMutableLiveData
    val lastPageMLiveData: LiveData<Int> = lastPageMutableLiveData

    private val repository = Repository()

    fun getLastPage(url: String): MutableLiveData<Int>
    {
        viewModelScope.launch(IO) {
            lastPageMutableLiveData.postValue(repository.getLastPage(url))
        }
        return lastPageMutableLiveData
    }

    fun getListBooks(url: String, type: String): MutableLiveData<MutableList<Book>>
    {
        viewModelScope.launch(IO) {
            booksMutableLiveData.postValue(repository.getListBooks(url, type))
        }
        return booksMutableLiveData
    }
}