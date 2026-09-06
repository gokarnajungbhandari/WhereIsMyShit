package com.example.whereismyshit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.whereismyshit.data.AppDatabase
import com.example.whereismyshit.data.ShitRepository
import com.example.whereismyshit.ui.ShitScreen
import com.example.whereismyshit.ui.WhereIsMyShitApp
import com.example.whereismyshit.viewmodel.ShitViewModel
import com.example.whereismyshit.viewmodel.ShitViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    private val repository by lazy {
        ShitRepository(database.shitDao())
    }

    private val viewModel: ShitViewModel by viewModels {
        ShitViewModelFactory(repository)
    }/* Give me the ShitViewModel associated with this Activity/UI owner.
        If one needs to be created, use this factory.*/

    override fun onCreate(savedInstanceState: Bundle?) { // android calls this we never do
        super.onCreate(savedInstanceState)

        setContent {
            WhereIsMyShitApp(viewModel = viewModel)
        }
    }
}
/*
                ANDROID
                    │
                    │ launches
                    ▼
              MainActivity
                    │
        ┌───────────┴───────────┐
        │                       │
        │                       │
        ▼                       ▼
   AppDatabase              setContent
        │                       │
        ▼                       ▼
    ShitDao              ShitScreen
        │                       │
        ▼                       │
ShitRepository                │
        │                       │
        ▼                       │
ShitViewModelFactory          │
        │                       │
        ▼                       │
 ShitViewModel ◄──────────────┘
        │
        │
        ▼
   ShitRepository
        │
        ▼
    ShitDao
        │
        ▼
   AppDatabase
*/