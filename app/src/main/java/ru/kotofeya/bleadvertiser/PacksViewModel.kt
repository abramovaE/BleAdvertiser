package ru.kotofeya.bleadvertiser

import android.content.Context
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PacksViewModel(private val repo: PackRepository): ViewModel() {


    private val _packsList = MutableLiveData<List<PackageEntity>>()
    val packsList: LiveData<List<PackageEntity>>
        get() = _packsList

    fun loadAllPacks(){
        viewModelScope.launch(Dispatchers.IO){
            _packsList.postValue(repo.getPacks())
        }
    }

    fun saveNewPack(packageEntity: PackageEntity){
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertPack(packageEntity)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(context: Context) :
        ViewModelProvider.NewInstanceFactory() {
        private val appContext = context.applicationContext
        private val repo = PackRepository(AppDatabase.getDatabase(context = appContext).packDao())

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            when (modelClass) {
                PacksViewModel::class.java -> PacksViewModel(repo) as T
                else -> throw IllegalArgumentException()
            }
            return PacksViewModel(repo) as T
        }
    }
}