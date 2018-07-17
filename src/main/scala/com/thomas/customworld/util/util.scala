package com.thomas.customworld

import java.util.regex.Pattern

import scala.util.Try

package object util {
  def QuoteSurround (str:AnyRef) :String = {
    val replaced = str.toString replaceAll("""\\""", """\""") replaceAll("%", """\%""") replaceAll("_", """\_""") replaceAll("""\[""", """\[""")
    '"' + replaced + '"'
  }

  case class Box (minx:Int, miny:Int, minz:Int, maxx:Int, maxy:Int, maxz:Int)

  trait Parser[T] {
    def parse(input: String): Option[T]
  }

  def parse[T](input: String)(implicit parser: Parser[T]): Option[T] =
    parser.parse(input)

  implicit object IntParser extends Parser[Int] {
    def parse(input: String):Option[Int] = Try(input.toInt).toOption
  }
  implicit object BooleanParser extends Parser[Boolean] {
    def parse(input: String):Option[Boolean] = Try(input.toBoolean).toOption
  }
}