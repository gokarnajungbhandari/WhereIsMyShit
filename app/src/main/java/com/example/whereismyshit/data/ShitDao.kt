package com.example.whereismyshit.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShitDao {

    @Insert
    suspend fun insert(shit: Shit) /* suspend allows this database operation to pause without blocking
                                        the coroutine's thread while the operation is waiting/completing.*/

    @Update
    suspend fun update(shit: Shit)

    @Delete
    suspend fun delete(shit: Shit)

    @Query("SELECT * FROM shit WHERE shit.parentId IS NULL ORDER BY id DESC")
    fun getAllPrimaryShits(): Flow<List<Shit>>

    @Query("SELECT * FROM shit WHERE shit.parentId = :parentId ORDER BY id DESC")
    fun getAllShitInsideAContainer(parentId: Int): Flow<List<Shit>>

    @Query("SELECT * FROM shit WHERE shit.name LIKE '%' || :searchText || '%'")
    suspend fun searchText(searchText : String): List<Shit>

    @Query("SELECT * FROM shit WHERE shit.id = :id")
    suspend fun getShit(id: Int): Shit?

    @Query(value= "SELECT Count(*) FROM shit Where shit.parentId = :id AND shit.isContainer = 1")
    fun getNumOfChildContainers(id: Int): Flow<Int>

    @Query(value= "SELECT Count(*) FROM shit Where shit.parentId = :id AND shit.isContainer = 0")
    fun getNumOfChildShits(id: Int): Flow<Int>
    /*
    A Flow can produce new values over time.
    For example:

        Database initially:
        []
                ↓
        Flow emits:
        []
        ----------------------
        Add John
                ↓
        Database changes
                ↓
        Flow emits:
        [John]
        ----------------------
        Add Sarah
                ↓
        Database changes
                ↓
        Flow emits:
        [John, Sarah]
        Compose needs to observe this.
        That's what:
        collectAsState(...)
        does.
        Conceptually:
            Room Database
                 ↓
            ShitDao
                 ↓
            Flow<List<Shit>>
                 ↓
            Repository
                 ↓
            ViewModel.allPeople
                 ↓
            collectAsState()
                 ↓
            ShitScreen
                 ↓
            people
    */
}