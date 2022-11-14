fun main() {
    val text = readln()
    val pattern = "[aA]+"
    println(text.replace(pattern.toRegex(), "a"))
}