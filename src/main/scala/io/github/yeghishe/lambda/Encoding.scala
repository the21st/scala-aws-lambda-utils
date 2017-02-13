package io.github.yeghishe.lambda

import java.io.{InputStream, OutputStream}

import scala.io.Source
import scala.util.{Try, Success, Failure}

private[lambda] object Encoding {
  import io.circe._
  import io.circe.parser._
  import io.circe.syntax._

  def toTry[A <: Exception, B](either: Either[A, B]): Try[B] = either match {
    case Right(b) => Success(b)
    case Left(a)  => Failure(a)
  }

  def in[T](is: InputStream)(implicit decoder: Decoder[T]): Try[T] = {
    val t = Try(Source.fromInputStream(is).mkString).map(decode[T](_)).flatMap(toTry)
    is.close()
    t
  }

  def out[T](value: T, os: OutputStream)(implicit encoder: Encoder[T]): Try[Unit] = {
    val t = Try(os.write(value.asJson.noSpaces.getBytes("UTF-8")))
    os.close()
    t
  }
}
