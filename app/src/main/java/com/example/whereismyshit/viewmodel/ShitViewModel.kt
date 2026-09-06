package com.example.whereismyshit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whereismyshit.data.Shit
import com.example.whereismyshit.data.ShitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ShitViewModel(
    private val repository: ShitRepository
)/* This is the primary Constructor */ : ViewModel() { // ViewModel() is a abstract class the ShitViewModel inherits from
    /*ShitScreen
         │
         │ User clicks buttons,
         │ enters name/age, etc.
         ▼
    ShitViewModel
         │
         │ Decides what action to perform
         ▼
    ShitRepository
         │
         ▼
    ShitDao
         │
         ▼
    Database*/

    val allPrimaryShits: Flow<List<Shit>> =
        repository.allPrimaryShits

    var searchResults by mutableStateOf<List<Shit>>(emptyList())
        private set


    fun getAllShitInsideAContainer(parentId: Int): Flow<List<Shit>> {
        return repository.getAllShitInsideAContainer(parentId)
    }

    suspend fun getParentStack(shit: Shit): List<Shit>{
        return repository.getParentsStack(shit).toList()
    }


    fun getNumOfChildContainers(id: Int): Flow<Int>{
        return repository.getNumOfChildContainers(id)
    }

    fun getNumOfChildShits(id: Int): Flow<Int>{
        return repository.getNumbOfChildShits(id)
    }

    suspend fun getShit(id: Int) : Shit?{
        return repository.getShit(id)
    }

    fun performSearchText(searchText: String){
        viewModelScope.launch{
            searchResults = searchText(searchText)
        }
    }
    suspend fun searchText(searchText: String): List<Shit>{
        return repository.searchText(searchText)
    }
    fun addShit(name: String,
                isContainer: Boolean,
                parentId: Int?) {
        viewModelScope.launch {

            val shit = Shit(
                name = name,
                isContainer = isContainer,
                parentId = parentId
            )

            repository.insert(shit)
        }
    }

    fun updateShit(shit: Shit) {
        viewModelScope.launch {
            repository.update(shit)
        }
    }

    fun deleteShit(shit: Shit) {
        viewModelScope.launch {
            repository.delete(shit)
        }
    }
}
/*
        COMMAND / ACTION
        "I want this to happen."
                ↓
        addShit()
        updateShit()
        deleteShit()
                ↓
        ViewModel uses
        viewModelScope.launch { }


        REQUEST FOR A RESULT
        "I need this value."
                ↓
        getShit(id)
                ↓
        suspend fun
                ↓
        returns Shit?
* */