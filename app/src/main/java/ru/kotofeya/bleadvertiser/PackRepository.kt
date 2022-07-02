package ru.kotofeya.bleadvertiser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PackRepository(private val packageDao: PackageDao) {

    suspend fun getPacks(): List<PackageEntity> = withContext(Dispatchers.IO){
        packageDao.getPacks()
    }

    suspend fun deletePack(packageEntity: PackageEntity) = withContext(Dispatchers.IO){
        packageDao.deletePack(packageEntity = packageEntity)
    }

    suspend fun insertPack(packageEntity: PackageEntity) = withContext(Dispatchers.IO){
        packageDao.insertPack(packageEntity = packageEntity)
    }
}