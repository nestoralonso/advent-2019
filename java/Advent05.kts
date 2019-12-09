import java.io.File
import kotlin.system.exitProcess

enum class OpCode(val value: Int) {
    OP_EXIT(99),
    OP_ADD(1),
    OP_MUL(2),
    OP_INPUT(3),
    OP_PRINT(4),
    JUMP_IF_TRUE(5),
    JUMP_IF_FALSE(6),
    LESS_THAN(7),
    EQUALS(8),
}

fun executeInstruction(data: IntArray, opCode: OpCode, instrPointer: Int, paramModes: List<Int>, instrSize: Int): Int {
    val params = getParamValues(data, instrPointer, paramModes, instrSize)
    var newIP = instrPointer + instrSize
    println("final-params = ${params}")
    when (opCode) {
        OpCode.OP_ADD -> {
            val (op1, op2, res) = params
            data[res] = op1 + op2
        }
        OpCode.OP_MUL -> {
            val (op1, op2, res) = params
            data[res] = op1 * op2
        }
        OpCode.OP_INPUT -> {
            val (op1) = params
            print("INPUT>>> ")
            val input = readLine() ?: "0"
            data[op1] = input.toInt()
        }
        OpCode.OP_PRINT -> {
            val (op1) = params
            println("paramModes = ${paramModes[0]}")

            if (paramModes[0] == 1) {
                println("OUTPUT>>> $op1")
            } else {
                println("OUTPUT>>> ${data[op1]}")
            }
        }
        OpCode.JUMP_IF_TRUE -> {
            val (op1, res) = params
            if (op1 != 0 && paramModes[1] == 1) {
                newIP = res
            } else if (op1 != 0) {
                newIP = data[res]
            }
        }
        OpCode.JUMP_IF_FALSE -> {
            val (op1, res) = params
            if (op1 == 0 && paramModes[1] == 1) {
                newIP = res
            } else if (op1 == 0) {
                newIP = data[res]
            }
        }
        OpCode.LESS_THAN -> {
            val (op1, op2, res) = params

            data[res] = if (op1 < op2) 1 else 0
        }
        OpCode.EQUALS -> {
            val (op1, op2, res) = params

            data[res] = if (op1 == op2) 1 else 0
        }
        else -> Unit
    }

    return newIP
}

/**
 * return params in position or immediate mode
 */
fun getParamValues(data: IntArray, instrPointer: Int, paramModes: List<Int>, instrSize: Int): MutableList<Int> {
    val retParams = paramModes.toMutableList()
    for (ix in 0 until instrSize) {
        if (instrPointer + ix > data.lastIndex) {
            retParams[ix] = 0
        }

        val pMode = paramModes[ix]
        val opVal = data[instrPointer + ix]
        var paramValue = if (pMode == 1) {
            opVal
        } else {
            data[opVal]
        }

        // the last param is always an address
        paramValue = if (ix == instrSize - 1) opVal else paramValue

        retParams[ix] = paramValue
    }

    return retParams
}

fun intToOpCodeEnum(inum: Int): OpCode {
    if (inum == 99) return OpCode.OP_EXIT;
    return OpCode.values()[inum];
}

fun parseOpCode(opCodeRaw: Int): Triple<OpCode, Int, List<Int>> {
    val opCodeArr = opCodeRaw.toString().split("").filter { it.isNotEmpty() }.map { s -> s.toInt() }

    val opCodeInt = if (opCodeArr.size > 1) opCodeArr.subList(opCodeArr.lastIndex - 1, opCodeArr.size).joinToString("").toInt() else opCodeArr[0]
    val opCode = intToOpCodeEnum(opCodeInt)
    val instrSize = when (opCode) {
        OpCode.OP_ADD, OpCode.OP_MUL, OpCode.LESS_THAN, OpCode.EQUALS -> 3
        OpCode.OP_INPUT -> 1
        OpCode.OP_PRINT -> 1
        OpCode.JUMP_IF_TRUE, OpCode.JUMP_IF_FALSE -> 2
        OpCode.OP_EXIT -> 0
    }

    if (opCodeArr.size == 1) {
        return Triple(opCode, instrSize, listOf(0, 0, 0))
    }

    val paramsMode = opCodeArr.asReversed().subList(2, opCodeArr.size)
    val completePModes = (0 until 3).map { ix -> if (ix <= paramsMode.lastIndex) paramsMode[ix] else 0 }
    return Triple(opCode, instrSize, completePModes)
}

fun computeInput(wdata: IntArray): Int {
    var instrPointer = 0

    while (instrPointer < wdata.size) {
        val opCodeRaw = wdata[instrPointer]
        val (opCode, instrSize, paramModes) = parseOpCode(opCodeRaw)

        if (opCode == OpCode.OP_EXIT) break

        instrPointer++

        println("opCode=$opCode, paramModes=$paramModes")

        instrPointer = executeInstruction(wdata, opCode, instrPointer, paramModes, instrSize)
        println("instrPointer = ${instrPointer}")
    }

    return wdata[0]
}

fun testInstructionMult() {
    val program = intArrayOf(1002, 4, 3, 4, 33)
    computeInput(program)

    println(program.asList())
}

fun testInstructionPrint() {
    val program = intArrayOf(3, 4, 4, 4, 33)
    computeInput(program)

    println(program.asList())
}

if (args.isEmpty()) {
    println("Usage:\n prog [FILEPATH]")
    exitProcess(2)
}

val lines = File(args[0]).readLines()
var data = lines[0].split(",").map { it.trim().toInt() }.toIntArray()

println("${data.size}::${data.asList()}")
println("part 1 ${computeInput(data)}")

// testInstructionMult()
// testInstructionPrint()