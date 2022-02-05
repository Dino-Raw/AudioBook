package com.example.audiobook.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiobook.models.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyBooksViewModel : ViewModel() {
    var items: MutableLiveData<MutableList<Book>> = MutableLiveData()


}