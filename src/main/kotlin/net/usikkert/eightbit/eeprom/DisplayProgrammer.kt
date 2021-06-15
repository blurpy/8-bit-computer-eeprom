package net.usikkert.eightbit.eeprom

import kotlin.math.abs

fun main() {
    val programmer = DisplayProgrammer(2048)

    programmer.fill()
    programmer.print()
    programmer.writeToFile("display.bin")
}

class DisplayProgrammer(eepromSize: Int): Programmer(eepromSize) {

    fun fill() {
        // Bit patterns for the digits 0..9
        val digits = byteArrayOf(0x7e, 0x30, 0x6d, 0x79, 0x33, 0x5b, 0x5f, 0x70, 0x7f, 0x7b)

        println("Programming ones place")

        for (value in 0..255) {
            addToEEPROM(value, digits[value % 10])
        }

        println("Programming tens place")

        for (value in 0..255) {
            addToEEPROM(value + 256, digits[(value / 10) % 10])
        }

        println("Programming hundreds place")

        for (value in 0..255) {
            addToEEPROM(value + 512, digits[(value / 100) % 10])
        }

        println("Programming sign")

        for (value in 0..255) {
            addToEEPROM(value + 768, 0)
        }

        println("Programming ones place (twos complement)")

        // Arduino C: A byte stores an 8-bit unsigned number, from 0 to 255.
        for (value in -128..127) {
            addToEEPROM(value.toUByte().toInt() + 1024, digits[abs(value) % 10])
        }

        println("Programming tens place (twos complement)")

        for (value in -128..127) {
            addToEEPROM(value.toUByte().toInt() + 1280, digits[abs(value / 10) % 10])
        }

        println("Programming hundreds place (twos complement)")

        for (value in -128..127) {
            addToEEPROM(value.toUByte().toInt() + 1536, digits[abs(value / 100) % 10])
        }

        println("Programming sign (twos complement)")

        for (value in -128..127) {
            if (value < 0) {
                addToEEPROM(value.toUByte().toInt() + 1792, 0x01)
            } else {
                addToEEPROM(value.toUByte().toInt() + 1792, 0)
            }
        }
    }
}
