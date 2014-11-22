package org.nexbook.generator

import java.util.concurrent.atomic.AtomicLong

class TradeIdGenerator(initValue: String) {
  val ASCII_LETTER_OFFSET = 65
  val LETTER_RANGE = 'Z' - 'A' + 1
  val LETTER_COUNT = 3
  val DIGIT_COUNT = 6
  val REGEX_PATTERN = "[A-Z]{" + LETTER_COUNT + "}[0-9]{" + DIGIT_COUNT + "}"
  val currentValue = new AtomicLong(toLong(initValue))
  val seq = 'A' to 'Z'

  require(initValue.matches(REGEX_PATTERN), "Initial value must match regex: " + REGEX_PATTERN)

  /**
   * default AAA000000
   */
  def this() = this((List.fill(3)('A') ::: List.fill(6)('0')).mkString)

  def next = toString(currentValue.incrementAndGet)

  private def toString(value: Long): String = {
    def decodeAtLevel(index: Int, value: Long, decodedChars: List[Char]): List[Char] = index match {
      case -1 => decodedChars.reverse
      case _ => {
        val levelResult = Math.pow(LETTER_RANGE, index).toLong
        val countedInt = (value / levelResult).toInt
        val countedChar: Char = (countedInt + ASCII_LETTER_OFFSET).toChar
        decodeAtLevel(index - 1, value - (countedInt * levelResult), countedChar :: decodedChars)
      }
    }
    (decodeAtLevel(LETTER_COUNT - 1, value / (Math.pow(10, DIGIT_COUNT).toLong), List())).mkString + ("%0" + DIGIT_COUNT + "d").format(value % Math.pow(10, DIGIT_COUNT).toInt)
  }

  private def toLong(str: String): Long = {
    def decodeLetters(letters: String): Long = {
      def encodeLetterIndex(letterIndex: (Int, Int)): Long = letterIndex match {
        case (letter, index) => letter * Math.pow(10, DIGIT_COUNT).toLong * Math.pow(LETTER_RANGE, index).toLong
      }
      letters.toList.reverse.map(c => c.toInt - ASCII_LETTER_OFFSET).view.zipWithIndex.map(encodeLetterIndex(_)).toList.reduceLeft[Long](_ + _)
    }
    val (letters, digits) = str.splitAt(LETTER_COUNT)
    decodeLetters(letters) + digits.toInt
  }
}
