package day2

import java.io.File

fun main() {
    (1..99).forEach { noun ->
        (1..99).forEach { verb ->
            val output = calculateOutput(noun, verb)
            if (output == 19690720) {
                println(100 * noun + verb)
                return
            }
        }
    }
    return
}

val opcodes = File("src/main/resources/day2.txt")
    .readText()
    .split(",")

private fun calculateOutput(noun: Int, verb: Int): Int {
    val program = opcodes.mapTo(mutableListOf()) { it.toInt() }

    program[1] = noun
    program[2] = verb

    var index = 0
    while (true) {
        val opcode = program[index]
        when (opcode) {
            ADD -> {
                val fst = program[program[index + 1]]
                val snd = program[program[index + 2]]
                program[program[index + 3]] = fst + snd
                index += 4
            }
            MULTIPLY -> {
                val fst = program[program[index + 1]]
                val snd = program[program[index + 2]]
                program[program[index + 3]] = fst * snd
                index += 4
            }
            HALT -> {
                return program[0]
            }
        }
    }
}