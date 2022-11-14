/*fun parsingNickname(emailString: String): String {
    val symbolsForNickname = Regex("\\w+\\.\\w@") // fix this condition
    val nicknameString = emailString.split("").first() // fix this condition
    val (firstName, lastName ) = nicknameString.split(symbolsForNickname)
    val nickname = // put your code here
    return nickname
}*/

fun parsingNickname(emailString: String): String {
    val symbolsForNickname = Regex("\\w+[._]\\w+") // fix this condition

    val x = (symbolsForNickname.find(emailString)?.value ?: "no").split("[._]".toRegex()).map { it.replaceFirstChar(Char::uppercase)  }.joinToString(" ")
    return x
}

