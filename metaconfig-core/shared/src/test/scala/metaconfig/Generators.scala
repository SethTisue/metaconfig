package metaconfig

import scala.meta.inputs.Input

import org.scalacheck.Prop.forAll
import org.scalacheck._
import org.scalameta.logger

object Generators {
  import Conf._
  val genIdent = for {
    len <- Gen.choose(1, 2)
    chars <- Gen.listOfN(len, Gen.oneOf('a' to 'd'))
  } yield chars.mkString
  val genKey = for {
    depth <- Gen.choose(1, 3)
    idents <- Gen.listOfN(depth, genIdent)
  } yield idents.mkString(".")
  val genNum: Gen[Num] = Gen.posNum[Int].map(x => Num(x))
  val genStr: Gen[Str] = genIdent.map(Str.apply)
  val genBool: Gen[Bool] = Gen.oneOf(true, false).map(Bool.apply)
  val genPrimitive: Gen[Conf] =
    Gen.frequency[Conf](1 -> genNum, 1 -> genStr, 1 -> genBool)

  val genConfShow: Gen[ConfShow] = for {
    lines <- Gen.listOfN(100, for {
      key <- genKey
      conf <- genPrimitive
    } yield s"$key = ${conf.show}")
  } yield ConfShow(lines.mkString("\n"))
  implicit val argConfShow: Arbitrary[ConfShow] = Arbitrary(genConfShow)
}

case class ConfShow(str: String) {
  def input = Input.String(str)
}
