package day2

import java.io.File

const val ADD = 1
const val MULTIPLY = 2
const val HALT = 99

fun main() {
    val program = File("src/main/resources/day2.txt")
        .readText()
        .split(",")
        .mapTo(mutableListOf()) { it.toInt() }

    program[1] = 12
    program[2] = 2

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
                println(program[0])
                return
            }
        }
    }
}