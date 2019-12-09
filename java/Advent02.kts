import java.io.File
import java.util.*
import kotlin.system.exitProcess

fun executeInstruction(data: IntArray, opCode: Int, opPos1: Int, opPos2: Int, resPos: Int) {
    if (opCode == 1) {
        data[resPos] = data[opPos1] + data[opPos2]
    } else {
        data[resPos] = data[opPos1] * data[opPos2]
    }
}

fun computeInput(wdata: IntArray): Int {
    var instPointer = 0

    while (instPointer < wdata.size) {
        val opCode = wdata[instPointer]
        if (opCode == 99) break

        val op1 = wdata[instPointer + 1]
        val op2 = wdata[instPointer + 2]
        val res = wdata[instPointer + 3]

        executeInstruction(wdata, opCode, op1, op2, res)
        instPointer += 4
    }

    return wdata[0]
}

fun part2(data: IntArray) {
    for (noun in 99 downTo 0) {
        for (verb in 99 downTo 0) {
            data[1] = noun
            data[2] = verb
            val res = computeInput(data.clone())
            if (res == 19690720) {
                println("noun $noun, verb $verb")
                println("ans ${noun * 100 + verb}")
                break
            }
        }
    }
}

if (args.isEmpty()) {
    println("Usage:\n prog [FILEPATH]")
    exitProcess(2)
}

val lines = File(args[0]).readLines()
var data = lines[0].split(",").map { it.trim().toInt() }.toIntArray()

println("part 1 ${computeInput(data.clone())}")

part2(data)