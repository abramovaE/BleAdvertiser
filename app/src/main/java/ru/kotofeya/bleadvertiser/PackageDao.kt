package ru.kotofeya.bleadvertiser

import androidx.room.*

@Dao
interface PackageDao {

    @Query("SELECT * FROM package_entity")
    fun getPacks() : List<PackageEntity>

    @Delete
    fun deletePack(packageEntity: PackageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPack(packageEntity: PackageEntity)
}