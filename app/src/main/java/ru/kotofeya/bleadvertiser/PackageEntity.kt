package ru.kotofeya.bleadvertiser

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "package_entity")
data class PackageEntity(
    @ColumnInfo(name = "uid") @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "pack") var pack: ByteArray?
):Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PackageEntity

        if (uid != other.uid) return false
        if (name != other.name) return false
        if (pack != null) {
            if (other.pack == null) return false
            if (!pack.contentEquals(other.pack)) return false
        } else if (other.pack != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (pack?.contentHashCode() ?: 0)
        return result
    }
}