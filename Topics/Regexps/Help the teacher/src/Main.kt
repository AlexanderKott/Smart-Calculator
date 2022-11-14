fun main() {
    val report = readLine()!!
    val str = ". wrong answers?".toRegex()
    println(str.matches(report))

}