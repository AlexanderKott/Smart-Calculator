package calculator

import java.util.Stack
import kotlin.math.pow

fun String.isOperator() = this == "+" || this == "-" || this == "*" || this == "/" || this == "^"
fun String.isNumber() = toBigIntegerOrNull() != null
enum class Compare { EQUAL, LOWER, HIGHER }

fun Stack<String>.takeWhileLeftPar(value: String, lowerValueCounts: Boolean): String {
    val buffer = mutableListOf<String>()
    val numbersIterator = this.iterator()
    while (numbersIterator.hasNext()) {
        val item = this.peek()
        if (item == "(" || (lowerValueCounts && item.precedence(value) == Compare.LOWER)) {
            break
        } else buffer += this.pop()
    }

    return buffer.joinToString(" ")
}

fun main() {
    val wrongCharsExpression = "(.)+((\\*\\*)|(\\/\\/))(.)+(.)+((\\*\\*)|(\\/\\/))(.)+".toRegex()
    val patternAssign = "[a-zA-Z]+\\s*=\\s*[-+]?([a-zA-Z]+|\\d+)\\s*".toRegex()
    val patternVariable = "[a-zA-Z]+".toRegex()

    val variables = mutableMapOf<String, String>()

    while (true) {
        val input = readLine()!!.trim()
        when {
            input == "/exit" -> break
            input == "/help" -> println("The program calculates the sum and subtraction of numbers")

            patternVariable.matches(input) -> {
                if (variables[input]?.toBigIntegerOrNull() != null) {
                    println(variables[input])
                } else if (variables.containsKey(input))
                    println(getVarValue(variables[input] ?: "", variables))
                else println("Unknown variable")
            }

            input.isEmpty() -> continue

            patternAssign.matches(input) -> {
                val varibale = input.replace(Regex("\\s+"), "").split("=").map { it.trim() }
                if (varibale.last().toBigIntegerOrNull() != null || variables.containsKey(varibale.last())) {
                    variables[varibale.first()] = varibale.last()
                } else {
                    println("Unknown variable")
                }
            }

            input.contains("=") -> {
                val invalidAssign = input.split("=".toRegex(), 2).map { it.trim() }
                if (!patternVariable.matches(invalidAssign.first())) {
                    println("Invalid identifier"); continue
                }
                if ((!patternVariable.matches(invalidAssign.last()) || invalidAssign.last().toBigIntegerOrNull() == null))
                    println("Invalid assignment")
            }

            input.isNotEmpty() && input[0] == '/' -> println("Unknown command")

            !wrongCharsExpression.matches(input) -> {
                try {
                    val normStr = normalazeString(input)
                    val substedStr =
                        normStr.split(" ").map { if (variables.containsKey(it)) getVarValue(it, variables) else it }
                    val revnot = reverseNotation(substedStr).split(" ")
                    println(calculateExpression(revnot))
                } catch (e: Exception) {
                    println("Invalid expression")
                }
            }
            input.isEmpty() -> continue
            else -> println("Invalid expression")
        }
    }
    println("Bye!")
}

fun String.precedence(value: String): Compare {
    val weigh = buildMap<String, Int> {
        put("-", 0)
        put("+", 0)
        put("*", 1)
        put("/", 1)
        put("^", 2)
    }
    return when {
        weigh[this]!! > weigh[value]!! -> Compare.HIGHER
        weigh[this]!! < weigh[value]!! -> Compare.LOWER
        weigh[this]!! == weigh[value]!! -> Compare.EQUAL
        else -> throw Exception("Something is wrong with a sign")
    }
}

fun calculateExpression(expression: List<String>): String {
    val stack = Stack<String>()
    for (i in expression) {
        when {
            i.isNumber() -> stack.push(i)
            i.isOperator() -> {
                val op2 = stack.pop().toBigInteger()
                val op1 = stack.pop().toBigInteger()
                val result = when (i) {
                    "+" -> op1 + op2;
                    "-" -> op1 - op2;
                    "*" -> op1 * op2
                    "/" -> op1 / op2
                    "^" -> op1.pow(op2.toInt())
                    else -> throw Exception("Operation is not supported:  $i")
                }
                stack.push(result.toString())
            }
        }
    }
    return stack.peek()
}


fun reverseNotation(inData: List<String>): String {
    val stack = Stack<String>()
    var result = ""

    for (i in inData) {
        when {
            i.isNumber() -> result += "$i "

            i.isOperator() && (stack.isEmpty() || stack.peek() == "(") -> stack.push(i)

            i.isOperator() &&
                    i.precedence(stack.peek()) == Compare.HIGHER -> stack.push(i)

            i.isOperator() && (
                    i.precedence(stack.peek()) == Compare.LOWER || i.precedence(stack.peek()) == Compare.EQUAL) -> {
                result += "${stack.takeWhileLeftPar(i, true)} "
                stack.push(i)
            }

            i == "(" -> stack.push(i)

            i == ")" -> {
                result += "${stack.takeWhileLeftPar(i, false)} "
                if (stack.peek() == "(") stack.pop()
            }
            else -> throw Exception("Parse error")
        }
    }
    result += stack.reversed().joinToString(" ")
    return result
}


fun getVarValue(id: String, variables: MutableMap<String, String>): String {
    var address: String = id
    var result = ""
    for ((key, value) in variables) {
        result = variables[address] ?: throw Exception("NoSuchElement $address")
        if (result.last().isDigit()) {
            return result
        } else {
            address = result
        }
    }
    throw Exception("No numeric value for the $id variable")
}

private fun normalazeString(inStr: String): String {
    val signs = arrayOf("\\+", "\\*", "\\/", "\\-", "\\^")
    var str = inStr

    if (inStr.count { it == '(' } != inStr.count { it == ')' })
        throw Exception("Wrong number of brackets")

    for (i in signs) {
        str = str.replace(Regex("[${i}]\\b"), "$i ")
            .replace(Regex("\\b[${i}]"), " $i")
    }
    str = str.replace(Regex("\\("), " ( ")
        .replace(Regex("\\)"), " ) ")
        .replace(Regex("\\s+"), " ")

    str = str.replace(Regex("(\\(|^|\\/|\\*)\\s*(\\-)(\\s+)"), "$1$3$2").trim()

    return str.split("\\s+".toRegex()).joinToString(" ") { extractAndCleanSign(it) }
}

private fun extractAndCleanSign(inStr: String): String {
    val signs = inStr.toCharArray().toMutableList()
    while (signs.size != 1) {
        when {
            signs.size == 0 -> return inStr

            signs[0].isDigit() || signs.size > 2 && signs[1].isDigit() -> {
                return inStr
            }

            signs[0] == '-' && signs[1] == '-' -> {
                signs.removeAt(0)
                signs.removeAt(0)
                signs.add(0, '+')
            }
            signs[0] == '-' && signs[1] == '+' ||
                    signs[0] == '+' && signs[1] == '-' -> {
                signs.removeAt(0)
                signs.removeAt(0)
                signs.add(0, '-')
            }
            signs[0] == '+' && signs[1] == '+' -> {
                signs.removeAt(0)
            }
            else -> return inStr
        }
    }
    return String(signs.toCharArray())
}