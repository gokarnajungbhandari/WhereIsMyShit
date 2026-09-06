package com.example.whereismyshit.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whereismyshit.data.ShitRepository

class ShitViewModelFactory(
    private val repository: ShitRepository
) : ViewModelProvider.Factory/*Android's ViewModelProvider.Factory interface
                                          ↑
                                          │ implements
                                          │
                                 ShitViewModelFactory
     By implementing that interface, your class becomes something Android recognises as a ViewModel factory.*/ {

    override fun <T : ViewModel> create(
        modelClass: Class<T> //modelClass holds the class itself instead of the object
    ): T { /*Given a requested ViewModel class, create and return an instance of it.
            T can be any type that is a ViewModel or inherits from ViewModel.
            The reason we are using T instead of ShitViewModel is that the class inherits
            ViewModelProvider.Factory interface, which uses T to allow objects to any class*/

        if (modelClass.isAssignableFrom(ShitViewModel::class.java)) {
            // is android asking me to create a ShitViewModel?
            @Suppress("UNCHECKED_CAST") /* The compiler can't completely prove that our cast is
                                                        safe because of how generic type information works.*/
            return ShitViewModel(repository) as T // since the create function returns T we are assigning
                                                    // ShitViewModel as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
            /*
            AppDatabase
                 │
                 ▼
             ShitDao
                 │
                 ▼
            ShitRepository
                 │
                 │
                 ├────────────────────┐
                 │                    │
                 ▼                    │
            ShitViewModelFactory    │
                 │                    │
                 │ has repository     │
                 │                    │
                 ▼                    │
            creates                   │
                 │                    │
                 ▼                    │
            ShitViewModel ◄─────────┘
                 │
                 ▼
            ShitScreen


            ShitViewModel requires:
                    ↓
            ShitRepository

            Android creates/manages ViewModels
            but needs help providing our repository
                    ↓
            ShitViewModelFactory
                    ↓
            knows how to do:
            ShitViewModel(repository)

            So ShitViewModelFactory is a constructor helper that tells Android how to create
            your custom ShitViewModel with the dependencies it requires.
            */