val chars = ('A' to 'Z').toList

chars.indexOf('A')
chars.indexOf('Z')

val str = "ABC00001"

val ASCII_LETTER_OFFSET = 65

'A'.toInt-ASCII_LETTER_OFFSET
val (letters, digits) = str.splitAt(3)
var letterMultiplier: Int = 0
//10000

def decodeLetters(letters: String): Long = {
  val digitCount = 6
  val letterRange = 'Z'-'A'+1
  def encodeLetterIndex(letterIndex: (Int, Int)): Long = letterIndex match {
    case (letter, index) => letter * Math.pow(10, digitCount).toLong * Math.pow(letterRange, index).toLong
  }

  letters.toList.reverse.map(c => c.toInt-ASCII_LETTER_OFFSET).view.zipWithIndex.map(encodeLetterIndex(_)).toList.reduceLeft[Long](_+_)
}
"0200".toInt
println("Start1")
decodeLetters("ZDA")
println("End")
//letters.toList.reverse.foreach()
println("test")
//str.foreach(c => println("Char" + c + " : " + (c.toInt-48)))

def toLong(str: String): Long = {
  val (letters, digits) = str.splitAt(3)
  decodeLetters(letters) + digits.toInt
}
/*toLong("AAZ99999")
toLong("ABA00000")

toLong("AZZ99999")
toLong("BAA00000")*/
/*
val dgs = 5
val range: Int = 26

var index = 0
var letter = 'A'.toInt-ASCII_LETTER_OFFSET
var A =letter *Math.pow(10, dgs).toInt*Math.pow(range, index)
letter = 'B'.toInt-ASCII_LETTER_OFFSET
val B = letter * Math.pow(10, dgs).toInt*Math.pow(range, index)
letter = 'C'.toInt-ASCII_LETTER_OFFSET
val C = letter * Math.pow(10, dgs).toInt*Math.pow(range, index)
letter = 'Z'.toInt-ASCII_LETTER_OFFSET
val Z = letter * Math.pow(10, dgs).toInt*Math.pow(range, index)
index = 1
letter = 'A'.toInt-ASCII_LETTER_OFFSET
var A2 =letter *Math.pow(10, dgs).toInt*Math.pow(range, index)
letter = 'B'.toInt-ASCII_LETTER_OFFSET
val B2 = letter * Math.pow(10, dgs).toInt*Math.pow(range, index)
letter = 'C'.toInt-ASCII_LETTER_OFFSET
val C2 = letter * Math.pow(10, dgs).toInt*Math.pow(range, index)
letter = 'Z'.toInt-ASCII_LETTER_OFFSET
val Z2 = letter * Math.pow(10, dgs).toInt*Math.pow(range, index).toLong
index = 2
letter = 'B'.toInt-ASCII_LETTER_OFFSET
letter.toLong * Math.pow(10, dgs).toLong*Math.pow(range, index).toLong




val B3 =letter * Math.pow(10, dgs).toLong * Math.pow(range, index).toLong*/

"%05d".format(2)

/*index = 1
letter = 'C'.toInt-ASCII_LETTER_OFFSET
26*(letter+1) * Math.pow(10, index+dgs).toInt*/

toLong("AAA999999")
toLong("ABB000000") //27000000



