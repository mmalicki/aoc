package day5.part2


import day5.part2.Add.Companion.ADD
import day5.part2.Equals.Companion.EQUALS
import day5.part2.Halt.HALT
import day5.part2.Input.INPUT
import day5.part2.JumpIfFalse.Companion.JUMP_IF_FALSE
import day5.part2.JumpIfTrue.Companion.JUMP_IF_TRUE
import day5.part2.LessThan.Companion.LESS_THAN
import day5.part2.Multiply.Companion.MULTIPLY
import day5.part2.Output.Companion.OUTPUT
import java.io.File
import java.lang.IllegalArgumentException
import kotlin.system.exitProcess

sealed class InstructionResult {
    abstract fun calculateAddr(currentAddress: Int): Int
}

class RelativeOffset(private val value: Int) : InstructionResult() {
    override fun calculateAddr(currentAddress: Int) = currentAddress + value
}

class AbsoluteOffset(private val value: Int) : InstructionResult() {
    override fun calculateAddr(currentAddress: Int) = value
}

sealed class Instruction {
    abstract fun execute(program: MutableList<String>, index: Int): InstructionResult
}

class Add(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    companion object {
        const val ADD = 1
    }
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg, trdArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = fstMode.calculateParam(program, fstArg)
        val sndVal = sndMode.calculateParam(program, sndArg)
        program[trdArg.toInt()] = (fstVal.toInt() + sndVal.toInt()).toString()
        return RelativeOffset(4)
    }
}

class Multiply(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    companion object {
        const val MULTIPLY = 2
    }
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg, trdArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = fstMode.calculateParam(program, fstArg)
        val sndVal = sndMode.calculateParam(program, sndArg)
        program[trdArg.toInt()] = (fstVal.toInt() * sndVal.toInt()).toString()
        return RelativeOffset(4)
    }
}

class JumpIfTrue(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    companion object {
        const val JUMP_IF_TRUE = 5
    }
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg) = program.subList(index + 1, index + 1 + 2)
        val fstVal = fstMode.calculateParam(program, fstArg)
        val sndVal = sndMode.calculateParam(program, sndArg)
        return if (fstVal.toInt() != 0) {
            AbsoluteOffset(sndVal.toInt())
        } else {
            RelativeOffset(3)
        }
    }
}

class JumpIfFalse(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    companion object {
        const val JUMP_IF_FALSE = 6
    }
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = fstMode.calculateParam(program, fstArg)
        val sndVal = sndMode.calculateParam(program, sndArg)
        return if (fstVal.toInt() == 0) {
            AbsoluteOffset(sndVal.toInt())
        } else {
            RelativeOffset(3)
        }
    }
}

class LessThan(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    companion object {
        const val LESS_THAN = 7
    }
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg, trdArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = fstMode.calculateParam(program, fstArg)
        val sndVal = sndMode.calculateParam(program, sndArg)
        program[trdArg.toInt()] = if (fstVal.toInt() < sndVal.toInt()) "1" else "0"
        return RelativeOffset(4)
    }
}

class Equals(private val fstMode: ParamMode, private val sndMode: ParamMode) : Instruction() {
    companion object {
        const val EQUALS = 8
    }
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val (fstArg, sndArg, trdArg) = program.subList(index + 1, index + 1 + 3)
        val fstVal = fstMode.calculateParam(program, fstArg)
        val sndVal = sndMode.calculateParam(program, sndArg)
        program[trdArg.toInt()] = if (fstVal == sndVal) "1" else "0"
        return RelativeOffset(4)
    }
}

object Input : Instruction() {
    const val INPUT = 3
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val inputValue = readLine()!!
        val targetAddr = program[index + 1]
        program[targetAddr.toInt()] = inputValue
        return RelativeOffset(2)
    }
}

class Output(private val mode: ParamMode) : Instruction() {
    companion object {
        const val OUTPUT = 4
    }
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        val inputArg = program[index + 1]
        val value = mode.calculateParam(program, inputArg)
        println(value)
        return RelativeOffset(2)
    }
}

object Halt : Instruction() {
    const val HALT = 99
    override fun execute(program: MutableList<String>, index: Int): InstructionResult {
        exitProcess(0)
    }
}

fun parseInstruction(instruction: String): Instruction {
    val modes = instruction.dropLast(2)
    return when (instruction.takeLast(2).toInt()) {
        ADD -> Add(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        MULTIPLY -> Multiply(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        INPUT -> Input
        OUTPUT -> Output(getParam(modes, 0))
        JUMP_IF_TRUE -> JumpIfTrue(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        JUMP_IF_FALSE -> JumpIfFalse(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        LESS_THAN -> LessThan(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        EQUALS -> Equals(
            getParam(modes, 0),
            getParam(modes, 1)
        )
        HALT -> Halt
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
    REFERENCE(0) {
        override fun calculateParam(program: MutableList<String>, param: String) = program[param.toInt()]
    },
    VALUE(1) {
        override fun calculateParam(program: MutableList<String>, param: String) = param
    };

    abstract fun calculateParam(program: MutableList<String>, param: String): String

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
        index = offset.calculateAddr(index)
    }
}