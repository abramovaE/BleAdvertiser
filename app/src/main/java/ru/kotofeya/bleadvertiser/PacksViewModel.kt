package ru.kotofeya.bleadvertiser

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PacksViewModel(private val repo: PackRepository): ViewModel() {

    private val _packsList = MutableLiveData<List<PackModel>>()
    val packsList: LiveData<List<PackModel>>
        get() = _packsList

    private val _selectedPack = MutableLiveData<PackModel>()
    val selectedPack: LiveData<PackModel> get() = _selectedPack


    fun deletePack(packModel: PackModel){
       viewModelScope.launch(Dispatchers.IO){
           val packageEntity = PackageEntity(packModel.id, packModel.name, packModel.pack)
           repo.deletePack(packageEntity)
           _packsList.postValue(_packsList.value?.filter { it.id != packModel.id })
       }
    }

    fun loadAllPacks(){
        viewModelScope.launch(Dispatchers.IO){
            val packsList = mutableListOf<PackModel>()
            for(packEntity in repo.getPacks()){
                packsList.add(PackModel(packEntity.id!!, packEntity.name!!, packEntity.pack!!))
            }
            _packsList.postValue(packsList)
        }
    }

    fun saveNewPack(packModel: PackModel){
        viewModelScope.launch(Dispatchers.IO) {
            val packageEntity = PackageEntity(packModel.id, packModel.name, packModel.pack)
            repo.insertPack(packageEntity)
            Log.d("TAG", "insert: ${packModel.id}")

            val packsList = mutableListOf<PackModel>()
            for(packEntity in repo.getPacks()){
                packsList.add(PackModel(packEntity.id!!, packEntity.name!!, packEntity.pack!!))
            }
            _packsList.postValue(packsList)
        }
    }

    fun getPackById(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val packageEntity = repo.getPackById(id)
            val packModel = PackModel(packageEntity.id!!, packageEntity.name!!, packageEntity.pack!!)
            _selectedPack.postValue(packModel)
        }
    }

    fun updatePack(packModel: PackModel){
        viewModelScope.launch (Dispatchers.IO){
            val packageEntity = PackageEntity(packModel.id, packModel.name, packModel.pack)
            repo.updatePack(packageEntity)
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