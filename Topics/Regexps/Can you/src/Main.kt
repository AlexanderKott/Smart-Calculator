fun main() {
    val answer = readln()
    val str = "I can'?t? do my homework on time!".toRegex()
    println(str.matches(answer))
}