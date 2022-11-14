fun main() {
    val text = readln()
    val pattern = ".*Computer.*".toRegex()
    println(pattern.matches(text))
}