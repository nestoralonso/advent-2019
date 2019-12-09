var lower = 134564
var upper = 585159

fun hasCriteria(num: Int): Boolean {
    val ar = num.toString().split("").filter { it.isNotEmpty() }.map { it.toInt() }

    for (i in 1 until ar.size) {
        if (ar[i] < ar[i-1]) return false
    }

    val counts = mutableMapOf<Int, Int>()

    for (i in 1 until ar.size) {
        val digit = ar[i]
        if (digit == ar[i-1]) {
            counts[digit] = counts.getOrDefault(digit,1) + 1
        }
    }

    val hasTwice = counts.filterValues { it == 2 }.isNotEmpty()

    return hasTwice
}

var total=0
for (c in lower..upper) {
    if (hasCriteria(c)) {
        total++
    }
}
println("total $total")