package ru.kotofeya.bleadvertiser


data class PackModel(
    val id: Int?,
    val name: String,
    var pack: ByteArray?
) {
    fun setTransportArrayValues(version: Int,
                                 serial: Int,
                                 transType: Int,
                                 buzzer: Int,
                                 increment: Int,
                                 transportType: Int,
                                 litera3: Int,
                                 route: Int,
                                 litera1: Int,
                                 litera2: Int){
        val byteArr = ByteArray(22) { 0 }

        byteArr[0] = version.toByte()

        val serial0 = (serial shr 16).toByte()
        val serial1 = (serial shr 8).toByte()
        val serial2 = serial.toByte()
        byteArr[2] = serial0
        byteArr[3] = serial1
        byteArr[4] = serial2

        byteArr[5] = transType.toByte()
        byteArr[6] = buzzer.toByte()
        byteArr[7] = increment.toByte()

        byteArr[14] = transportType.toByte()

        byteArr[15] = litera3.toByte()

        val route0 = (route shr 8).toByte()
        val route1= route.toByte()
        byteArr[16] = route0
        byteArr[17] = route1

        byteArr[18] = litera1.toByte()
        byteArr[19] = litera2.toByte()

        pack = byteArr
    }

    fun setStationaryArrayValues(version: Int,
                                serial: Int,
                                transType: Int,
                                buzzer: Int,
                                increment: Int,
                                crc: Long,
                                cityId: Int,
                                stationaryType: Int,
                                floor: Int){
        val byteArr = ByteArray(22) { 0 }

        byteArr[0] = version.toByte()

        val serial0 = (serial shr 16).toByte()
        val serial1 = (serial shr 8).toByte()
        val serial2 = serial.toByte()
        byteArr[2] = serial0
        byteArr[3] = serial1
        byteArr[4] = serial2

        byteArr[5] = transType.toByte()
        byteArr[6] = buzzer.toByte()
        byteArr[7] = increment.toByte()

        val crcArray = getByteArrayFromCRC(crc = crc)
        byteArr[8] = crcArray[0]
        byteArr[9] = crcArray[1]
        byteArr[10] = crcArray[2]
        byteArr[11] = crcArray[3]

        val cityId0 = (cityId shr 8).toByte()
        val cityId1= cityId.toByte()
        byteArr[12] = cityId0
        byteArr[13] = cityId1

        val stationaryType0 = (stationaryType shr 8).toByte()
        val stationaryType1= stationaryType.toByte()
        byteArr[14] = stationaryType0
        byteArr[15] = stationaryType1

        byteArr[16] = floor.toByte()

        pack = byteArr
    }



    fun setStoplightArrayValues(version: Int,
                                serial: Int,
                                transType: Int,
                                buzzer: Int,
                                increment: Int,
                                time: Long,
                                streetId: Int,
                                streetSide: Int){
        val byteArr = ByteArray(22) { 0 }

        byteArr[0] = version.toByte()

        val serial0 = (serial shr 16).toByte()
        val serial1 = (serial shr 8).toByte()
        val serial2 = serial.toByte()
        byteArr[2] = serial0
        byteArr[3] = serial1
        byteArr[4] = serial2

        byteArr[5] = transType.toByte()
        byteArr[6] = buzzer.toByte()
        byteArr[7] = increment.toByte()

        val timeArray = getByteArrayFromTime(time = time)
        byteArr[8] = timeArray[0]
        byteArr[9] = timeArray[1]
        byteArr[10] = timeArray[2]
        byteArr[11] = timeArray[3]

        val streetId0 = (streetId shr 8).toByte()
        val streetId1= streetId.toByte()
        byteArr[12] = streetId0
        byteArr[13] = streetId1

        byteArr[16] = streetSide.toByte()

        pack = byteArr
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PackModel

        if (id != other.id) return false
        if (name != other.name) return false
        if (pack != null) {
            if (other.pack == null) return false
            if (!pack.contentEquals(other.pack)) return false
        } else if (other.pack != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + (pack?.contentHashCode() ?: 0)
        return result
    }
}

fun getTimeFromByteArray(byteArray: ByteArray): Long {
    val byte8 = byteArray[8].toLong()
    val byte9 = byteArray[9].toLong()
    val byte10 = byteArray[10].toLong()
    val byte11 = byteArray[11].toLong()
    return ((byte8 and 0xFF) shl 24) or ((byte9 and 0xFF) shl 16) or ((byte10 and 0xFF) shl 8) or (byte11 and 0xFF)
}


fun getCRCFromByteArray(byteArray: ByteArray): Long {
    val byte8 = byteArray[8].toLong()
    val byte9 = byteArray[9].toLong()
    val byte10 = byteArray[10].toLong()
    val byte11 = byteArray[11].toLong()
    return ((byte8 and 0xFF) shl 24) or ((byte9 and 0xFF) shl 16) or ((byte10 and 0xFF) shl 8) or (byte11 and 0xFF)
}

fun getByteArrayFromTime(time: Long): ByteArray{
    val time0 = (time shr 24).toByte()
    val time1 = (time shr 16).toByte()
    val time2 = (time shr 8).toByte()
    val time3 = (time).toByte()
    return byteArrayOf(time0, time1, time2, time3)
}

fun getByteArrayFromCRC(crc: Long): ByteArray{
    val crc0 = (crc shr 24).toByte()
    val crc1 = (crc shr 16).toByte()
    val crc2 = (crc shr 8).toByte()
    val crc3 = (crc).toByte()
    return byteArrayOf(crc0, crc1, crc2, crc3)
}

