package day5.part2


import java.io.File
import java.lang.IllegalArgumentException
import kotlin.system.exitProcess

sealed class InstructionResult(val value: Int)

class RelativeOffset(value: Int) : InstructionResult(value)

class AbsoluteOffset(value: Int) : InstructionResult(value)

sealed class Instruction {
    abstract fun execute(program: MutableList<String>, index: Int): InstructionResult
}

class Add(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg, trdArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = if (fstMode == ParamMode.REFERENCE) program[fstArg.toInt()] else fstArg
        val sndVal = if (sndMode == ParamMode.REFERENCE) program[sndArg.toInt()] else sndArg
        program[trdArg.toInt()] = (fstVal.toInt() + sndVal.toInt()).toString()
        return RelativeOffset(4)
    }
}

class Multiply(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg, trdArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = if (fstMode == ParamMode.REFERENCE) program[fstArg.toInt()] else fstArg
        val sndVal = if (sndMode == ParamMode.REFERENCE) program[sndArg.toInt()] else sndArg
        program[trdArg.toInt()] = (fstVal.toInt() * sndVal.toInt()).toString()
        return RelativeOffset(4)
    }
}

class JumpIfTrue(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg) = program.subList(index + 1, index + 1 + 2)
        val fstVal = if (fstMode == ParamMode.REFERENCE) program[fstArg.toInt()] else fstArg
        val sndVal = if (sndMode == ParamMode.REFERENCE) program[sndArg.toInt()] else sndArg
        return if (fstVal.toInt() != 0) {
            AbsoluteOffset(sndVal.toInt())
        } else {
            RelativeOffset(3)
        }
    }
}

class JumpIfFalse(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = if (fstMode == ParamMode.REFERENCE) program[fstArg.toInt()] else fstArg
        val sndVal = if (sndMode == ParamMode.REFERENCE) program[sndArg.toInt()] else sndArg
        return if (fstVal.toInt() == 0) {
            AbsoluteOffset(sndVal.toInt())
        } else {
            RelativeOffset(3)
        }
    }
}

class LessThan(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg, trdArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = if (fstMode == ParamMode.REFERENCE) program[fstArg.toInt()] else fstArg
        val sndVal = if (sndMode == ParamMode.REFERENCE) program[sndArg.toInt()] else sndArg
        program[trdArg.toInt()] = if (fstVal.toInt() < sndVal.toInt()) "1" else "0"
        return RelativeOffset(4)
    }
}

class Equals(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg, trdArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = if (fstMode == ParamMode.REFERENCE) program[fstArg.toInt()] else fstArg
        val sndVal = if (sndMode == ParamMode.REFERENCE) program[sndArg.toInt()] else sndArg
        program[trdArg.toInt()] = if (fstVal == sndVal) "1" else "0"
        return RelativeOffset(4)
    }
}

object Input : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val inputValue = readLine()!!
        val targetAddr = program[index + 1]
        program[targetAddr.toInt()] = inputValue
        return RelativeOffset(2)
    }
}

class Output(private val mode: ParamMode) : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val inputArg = program[index + 1]
        val value = if (mode == ParamMode.REFERENCE) program[inputArg.toInt()] else inputArg
        println(value)
        return RelativeOffset(2)
    }
}

object Halt : Instruction() {
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        exitProcess(0)
    }
}

fun parseInstruction(instruction: String): Instruction {
    val modes = instruction.dropLast(2)
    return when (instruction.takeLast(2).toInt()) {
        1 -> Add(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        2 -> Multiply(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        3 -> Input
        4 -> Output(getParam(modes, 0))
        5 -> JumpIfTrue(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        6 -> JumpIfFalse(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        7 -> LessThan(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        8 -> Equals(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        99 -> Halt
        else -> throw IllegalArgumentException(instruction)
    }
}

fun getParam(modes: String, index: Int): ParamMode {
    return modes
        .reversed()
        .elementAtOrNull(index)
        ?.toString()
        ?.toInt()
        ?.let { ParamMode.of(it) }
        ?: ParamMode.REFERENCE
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
        index = when (offset) {
            is RelativeOffset -> index + offset.value
            is AbsoluteOffset -> offset.value
        }
    }
}