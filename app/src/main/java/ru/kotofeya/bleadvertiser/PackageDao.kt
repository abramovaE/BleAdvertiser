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

    @Query("DELETE FROM package_entity WHERE uid=:packId")
    fun deletePackById(packId: Int)

    @Query("SELECT * FROM package_entity WHERE uid=:packId LIMIT 1")
    fun getPackById(packId: Int): PackageEntity

    @Update
    fun updatePack(packageEntity: PackageEntity)


}