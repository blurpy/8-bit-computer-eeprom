# 8-bit-computer-eeprom

This is code for generating the microcode used for the display and the instruction decoder EEPROMs of my [8-bit-computer](https://github.com/blurpy/8-bit-computer).

It's based on the code at [beneater/eeprom-programmer](https://github.com/beneater/eeprom-programmer).

The main propose is to generate binaries that can be written with the [minipro](https://github.com/blurpy/minipro) chip programmer instead of using the Arduino.

The microcode is available in the [eeprom](eeprom) folder as well.

There's currently no difference between the microcode used by my computer and the original.
