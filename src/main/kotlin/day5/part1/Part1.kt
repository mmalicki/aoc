package day5.part1


import java.io.File
import java.lang.IllegalArgumentException
import kotlin.system.exitProcess

sealed class Instruction {
    abstract fun execute(program: MutableList<String>, index: Int): Int
}

class Add(private val fstMode: day5.part2.ParamMode, private val sndMode: day5.part2.ParamMode) : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): Int {
        val (fstArg, sndArg, trdArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = if (fstMode == day5.part2.ParamMode.REFERENCE) program[fstArg.toInt()] else fstArg
        val sndVal = if (sndMode == day5.part2.ParamMode.REFERENCE) program[sndArg.toInt()] else sndArg
        program[trdArg.toInt()] = (fstVal.toInt() + sndVal.toInt()).toString()
        return 4
    }
}

class Multiply(private val fstMode: day5.part2.ParamMode, private val sndMode: day5.part2.ParamMode) : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): Int {
        val (fstArg, sndArg, trdArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = if (fstMode == day5.part2.ParamMode.REFERENCE) program[fstArg.toInt()] else fstArg
        val sndVal = if (sndMode == day5.part2.ParamMode.REFERENCE) program[sndArg.toInt()] else sndArg
        program[trdArg.toInt()] = (fstVal.toInt() * sndVal.toInt()).toString()
        return 4
    }
}

object Input : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): Int {
        val inputValue = readLine()!!
        val targetAddr = program[index + 1]
        program[targetAddr.toInt()] = inputValue
        return 2
    }
}

class Output(private val mode: day5.part2.ParamMode) : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): Int {
        val inputArg = program[index + 1]
        val value = if (mode == day5.part2.ParamMode.REFERENCE) program[inputArg.toInt()] else inputArg
        println(value)
        return 2
    }
}

object Halt : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): Int {
        exitProcess(0)
    }
}

fun parseInstruction(instruction: String): Instruction {
    val modes = instruction.dropLast(2)
    return when (instruction.takeLast(2).toInt()) {
        1 -> Add(getParam(modes, 0), getParam(modes, 1))
        2 -> Multiply(getParam(modes, 0), getParam(modes, 1))
        3 -> Input
        4 -> Output(getParam(modes, 0))
        99 -> Halt
        else -> throw IllegalArgumentException(instruction)
    }
}

fun getParam(modes: String, index: Int): day5.part2.ParamMode {
    return modes
        .reversed()
        .elementAtOrNull(index)
        ?.toString()
        ?.toInt()
        ?.let { day5.part2.ParamMode.of(it) }
        ?: day5.part2.ParamMode.REFERENCE
}

enum class ParamMode(val mode: Int) {
    REFERENCE(0),
    VALUE(1);

    companion object {
        fun of(param: Int) = values().find { it.mode == param }
    }
}

fun main() {
    val program = File("src/main/resources/day5.txt")
        .readText()
        .split(",")
        .mapTo(mutableListOf()) { it }

    runProgram(program)
}

fun runProgram(program: MutableList<String>) {
    var index = 0
    while (true) {
        val instruction = parseInstruction(program[index])
        val offset = instruction.execute(program, index)
        index += offset
    }
}