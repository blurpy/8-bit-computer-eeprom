package net.usikkert.eightbit.eeprom

fun main() {
    val programmer = InstructionDecoderProgrammer(2048)

    programmer.fill()
    programmer.print()
    programmer.writeToFile("instruction_decoder.bin")
}

class InstructionDecoderProgrammer(eepromSize: Int): Programmer(eepromSize) {

    val HLT = 0b1000000000000000  // Halt clock
    val MI  = 0b0100000000000000  // Memory address register in
    val RI  = 0b0010000000000000  // RAM data in
    val RO  = 0b0001000000000000  // RAM data out
    val IO  = 0b0000100000000000  // Instruction register out
    val II  = 0b0000010000000000  // Instruction register in
    val AI  = 0b0000001000000000  // A register in
    val AO  = 0b0000000100000000  // A register out
    val SO  = 0b0000000010000000  // ALU sum out
    val SM  = 0b0000000001000000  // ALU subtract (S-)
    val BI  = 0b0000000000100000  // B register in
    val OI  = 0b0000000000010000  // Output register in
    val CE  = 0b0000000000001000  // Program counter enable
    val CO  = 0b0000000000000100  // Program counter out
    val CJ  = 0b0000000000000010  // Jump (program counter in)
    val FI  = 0b0000000000000001  // Flags in

    val FLAGS_Z0C0 = 0
    val FLAGS_Z0C1 = 1
    val FLAGS_Z1C0 = 2
    val FLAGS_Z1C1 = 3

    val JC = 0b0111
    val JZ = 0b1000

    val UCODE_TEMPLATE = arrayOf(
        intArrayOf( MI or CO,  RO or II or CE,  0,         0,         0,                     0, 0, 0 ),   // 0000 - NOP
        intArrayOf( MI or CO,  RO or II or CE,  IO or MI,  RO or AI,  0,                     0, 0, 0 ),   // 0001 - LDA
        intArrayOf( MI or CO,  RO or II or CE,  IO or MI,  RO or BI,  SO or AI or FI,        0, 0, 0 ),   // 0010 - ADD
        intArrayOf( MI or CO,  RO or II or CE,  IO or MI,  RO or BI,  SO or AI or SM or FI,  0, 0, 0 ),   // 0011 - SUB
        intArrayOf( MI or CO,  RO or II or CE,  IO or MI,  AO or RI,  0,                     0, 0, 0 ),   // 0100 - STA
        intArrayOf( MI or CO,  RO or II or CE,  IO or AI,  0,         0,                     0, 0, 0 ),   // 0101 - LDI
        intArrayOf( MI or CO,  RO or II or CE,  IO or CJ,  0,         0,                     0, 0, 0 ),   // 0110 - JMP
        intArrayOf( MI or CO,  RO or II or CE,  0,         0,         0,                     0, 0, 0 ),   // 0111 - JC
        intArrayOf( MI or CO,  RO or II or CE,  0,         0,         0,                     0, 0, 0 ),   // 1000 - JZ
        intArrayOf( MI or CO,  RO or II or CE,  0,         0,         0,                     0, 0, 0 ),   // 1001
        intArrayOf( MI or CO,  RO or II or CE,  0,         0,         0,                     0, 0, 0 ),   // 1010
        intArrayOf( MI or CO,  RO or II or CE,  0,         0,         0,                     0, 0, 0 ),   // 1011
        intArrayOf( MI or CO,  RO or II or CE,  0,         0,         0,                     0, 0, 0 ),   // 1100
        intArrayOf( MI or CO,  RO or II or CE,  0,         0,         0,                     0, 0, 0 ),   // 1101
        intArrayOf( MI or CO,  RO or II or CE,  AO or OI,  0,         0,                     0, 0, 0 ),   // 1110 - OUT
        intArrayOf( MI or CO,  RO or II or CE,  HLT,       0,         0,                     0, 0, 0 ),   // 1111 - HLT
    )

    val ucode = Array(4) { Array(16) { IntArray(8) { 0 } } }

    fun initUCode() {
        // ZF = 0, CF = 0
        ucode[FLAGS_Z0C0] = UCODE_TEMPLATE.deepCopy()

        // ZF = 0, CF = 1
        ucode[FLAGS_Z0C1] = UCODE_TEMPLATE.deepCopy()
        ucode[FLAGS_Z0C1][JC][2] = IO or CJ

        // ZF = 1, CF = 0
        ucode[FLAGS_Z1C0] = UCODE_TEMPLATE.deepCopy()
        ucode[FLAGS_Z1C0][JZ][2] = IO or CJ

        // ZF = 1, CF = 1
        ucode[FLAGS_Z1C1] = UCODE_TEMPLATE.deepCopy()
        ucode[FLAGS_Z1C1][JC][2] = IO or CJ
        ucode[FLAGS_Z1C1][JZ][2] = IO or CJ
    }

    fun fill() {
        initUCode()

        for (address in 0..1023) {
            val flags = address and 768 shr 8
            val byte_sel = address and 128 shr 7
            val instruction = address and 120 shr 3
            val step = address and 7

            if (byte_sel == 1) {
                addToEEPROM(address, ucode[flags][instruction][step].toByte())
            } else {
                addToEEPROM(address, (ucode[flags][instruction][step] shr 8).toByte())
            }
        }

        // Fill the unused half with 0xFF
        for (address in 1024..2047) {
            addToEEPROM(address, 255.toByte())
        }
    }

    private fun Array<IntArray>.deepCopy() = Array(size) { get(it).clone() }
}
