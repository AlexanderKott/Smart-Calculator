fun main() {
    val string = readLine()!!
    val n = readLine()!!.toInt()
    val res = string.split("\\s+".toRegex(),n)
    res.forEach { println(it) }
}