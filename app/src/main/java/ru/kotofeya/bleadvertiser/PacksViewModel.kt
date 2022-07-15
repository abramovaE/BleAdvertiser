package ru.kotofeya.bleadvertiser

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PacksViewModel(private val repo: PackRepository): ViewModel() {

    private val _packsList = MutableLiveData<List<PackModel>>()
    val packsList: LiveData<List<PackModel>>
        get() = _packsList

    private val _selectedPack = MutableLiveData<PackModel>()
    val selectedPack: LiveData<PackModel> get() = _selectedPack


    fun deletePack(uid: Int){
        Log.d("TAG", "deletePack $uid")
       viewModelScope.launch(Dispatchers.IO){
           repo.deletePackById(uid = uid)
       }
    }

    fun loadAllPacks(){
        viewModelScope.launch(Dispatchers.IO){
            val packsList = mutableListOf<PackModel>()
            for(packEntity in repo.getPacks()){
                packsList.add(PackModel(packEntity.uid!!, packEntity.name!!, packEntity.pack!!))
            }
            _packsList.postValue(packsList)
            Log.d("TAG", "loadAllPacks(): $packsList")
        }
    }

    fun saveNewPack(packModel: PackModel){
        viewModelScope.launch(Dispatchers.IO) {
            val packageEntity = PackageEntity(packModel.uid, packModel.name, packModel.pack)
            repo.insertPack(packageEntity)
            Log.d("TAG", "insert: ${packModel.uid}")
        }
    }

    fun setSelectedPackById(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val packageEntity = repo.getPackById(id)
            val packModel =
                PackModel(packageEntity.uid!!, packageEntity.name!!, packageEntity.pack!!)
            _selectedPack.postValue(packModel)
        }
    }

    fun updatePack(packModel: PackModel){
        viewModelScope.launch (Dispatchers.IO){
            val packageEntity = PackageEntity(packModel.uid, packModel.name, packModel.pack)
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