fun main() {
    val text = readLine()!!
    val regexColors = "#[0-9a-fA-F]{6}\\b".toRegex()
    val x = regexColors.findAll(text)
    for (matches in x) println(matches.value)
}