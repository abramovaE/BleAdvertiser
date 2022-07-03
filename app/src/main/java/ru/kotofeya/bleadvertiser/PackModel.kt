package ru.kotofeya.bleadvertiser


data class PackModel(
    val id: Int?,
    val name: String,
    var pack: ByteArray?
) {

    fun setArrayValues(version: Byte,
                       serial: Int,
                       transpType: Byte,
                       buzzer: Byte,
                       increment: Byte,
                       time: Int,
                       streetId: Int,
                       streetSide: Byte){
        val byteArr = ByteArray(22, {0})

        byteArr[0] = version

        val serial0 = (serial shr 16).toByte()
        val serial1 = (serial shr 8).toByte()
        val serial2 = serial.toByte()
        byteArr[2] = serial0
        byteArr[3] = serial1
        byteArr[4] = serial2

        byteArr[5] = transpType
        byteArr[6] = buzzer
        byteArr[7] = increment

        val time0 = (time shr 24).toByte()
        val time1 = (time shr 16).toByte()
        val time2 = (time shr 8).toByte()
        val time3 = time.toByte()
        byteArr[8] = time0
        byteArr[9] = time1
        byteArr[10] = time2
        byteArr[11] = time3

        val streetId0 = (streetId shr 8).toByte()
        val streetId1= streetId.toByte()
        byteArr[12] = streetId0
        byteArr[13] = streetId1
        byteArr[16] = streetSide

        pack = byteArr
    }
}

