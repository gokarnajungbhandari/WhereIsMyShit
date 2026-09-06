package com.example.whereismyshit.data

import kotlinx.coroutines.flow.Flow

class ShitRepository( // Layer between DAO(more concerned with database) and viewmodel
    private val shitDao: ShitDao
) {
            /*The repository provides a layer between the ViewModel and DAO:

            ViewModel
                ↓
            Repository
                ↓
            ShitDao
                ↓
            Database*/
    val allPrimaryShits: Flow<List<Shit>> = // No reason to use suspend as Flow is being used
        shitDao.getAllPrimaryShits()

    fun getAllShitInsideAContainer(parentId:Int) : Flow<List<Shit>>{
        return shitDao.getAllShitInsideAContainer(parentId)
    }

    fun getNumOfChildContainers(id: Int): Flow<Int>{
        return shitDao.getNumOfChildContainers(id)
    }

    fun getNumbOfChildShits(id: Int): Flow<Int>{
        return shitDao.getNumOfChildShits(id)
    }

    suspend fun getShit(id: Int) : Shit?{
        return shitDao.getShit(id)
    }

    suspend fun insert(shit: Shit) {
        shitDao.insert(shit)
    }

    suspend fun update(shit: Shit) {
        shitDao.update(shit)
    }

    suspend fun delete(shit: Shit) {
        shitDao.delete(shit)
    }

    suspend fun getParentsStack(shit: Shit): MutableList<Shit>{
        var currentShit = shit
        var parentList = mutableListOf<Shit>()
        while (currentShit.parentId != null){
            currentShit = shitDao.getShit(currentShit.parentId) ?: return parentList
            parentList.add(currentShit)
        }
        parentList.reverse()
        return parentList
    }

    suspend fun searchText(searchText: String): List<Shit>{
        return shitDao.searchText(searchText)
    }
}
/*
        INSERT / UPDATE / DELETE
        "Do this operation and wait for it to complete"
        → suspend

        versus:
        SELECT returning Flow
        "Give me something I can observe over time"
        → Flow
        → usually no suspend
*/