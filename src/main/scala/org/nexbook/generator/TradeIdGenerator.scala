package org.nexbook.generator

import java.util.concurrent.atomic.AtomicLong

object TradeIdGenerator {
  val ASCII_LETTER_OFFSET = 65
  val LETTER_RANGE = 'Z' - 'A' + 1
  val LETTER_COUNT = 3
  val DIGIT_COUNT = 6
  val REGEX_PATTERN = "[A-Z]{" + TradeIdGenerator.LETTER_COUNT + "}[0-9]{" + TradeIdGenerator.DIGIT_COUNT + "}"
}

class TradeIdGenerator(initValue: String) {
  val currentValue = new AtomicLong(toLong(initValue))

  require(initValue.matches(TradeIdGenerator.REGEX_PATTERN), "Initial value must match regex: " + TradeIdGenerator.REGEX_PATTERN)

  /**
   * default AAA000000
   */
  def this() = this((List.fill(3)('A') ::: List.fill(6)('0')).mkString)

  def next = asStringValue(currentValue.incrementAndGet)

  private def asStringValue(value: Long): String = {
    def decodeAtLevel(index: Int, value: Long, decodedChars: List[Char]): List[Char] = index match {
      case -1 => decodedChars.reverse
      case _ => {
        val levelResult = Math.pow(TradeIdGenerator.LETTER_RANGE, index).toLong
        val countedInt = (value / levelResult).toInt
        val countedChar: Char = (countedInt + TradeIdGenerator.ASCII_LETTER_OFFSET).toChar
        decodeAtLevel(index - 1, value - (countedInt * levelResult), countedChar :: decodedChars)
      }
    }
    (decodeAtLevel(TradeIdGenerator.LETTER_COUNT - 1, value / (Math.pow(10, TradeIdGenerator.DIGIT_COUNT).toLong), List())).mkString + ("%0" + TradeIdGenerator.DIGIT_COUNT + "d").format(value % Math.pow(10, TradeIdGenerator.DIGIT_COUNT).toInt)
  }

  private def toLong(str: String): Long = {
    def decodeLetters(letters: String): Long = {
      def encodeLetterIndex(letterIndex: (Int, Int)): Long = letterIndex match {
        case (letter, index) => letter * Math.pow(10, TradeIdGenerator.DIGIT_COUNT).toLong * Math.pow(TradeIdGenerator.LETTER_RANGE, index).toLong
      }
      letters.toList.reverse.map(c => c.toInt - TradeIdGenerator.ASCII_LETTER_OFFSET).view.zipWithIndex.map(encodeLetterIndex(_)).toList.reduceLeft[Long](_ + _)
    }
    val (letters, digits) = str.splitAt(TradeIdGenerator.LETTER_COUNT)
    decodeLetters(letters) + digits.toInt
  }
}
