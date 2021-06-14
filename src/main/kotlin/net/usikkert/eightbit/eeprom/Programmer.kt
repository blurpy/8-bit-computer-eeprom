package net.usikkert.eightbit.eeprom

import java.io.File

abstract class Programmer(eepromSize: Int) {

    private val eeprom = ByteArray(eepromSize)

    fun addToEEPROM(address: Int, data: Byte) {
//        println("%04x:  %02x".format(address, data))
        eeprom[address] = data
    }

    fun print() {
        println("\nPrinting contents of ${eeprom.size}k EEPROM")

        for (base in 0 until eeprom.size -1 step 16) {
            val data = ByteArray(16)

            for (offset in 0..15) {
                data[offset] = eeprom[base + offset]
            }

            println("%04x:  %02x %02x %02x %02x %02x %02x %02x %02x   %02x %02x %02x %02x %02x %02x %02x %02x".format(
                base, data[0], data[1], data[2], data[3], data[4], data[5], data[6], data[7],
                data[8], data[9], data[10], data[11], data[12], data[13], data[14], data[15]))
        }

        println("Done")
    }

    fun writeToFile(fileName: String) {
        println("\nWriting contents of ${eeprom.size}k EEPROM to $fileName")
        File(fileName).writeBytes(eeprom)
        println("Done")
    }
}
