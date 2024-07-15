/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package base

import models.request.ChildCareProvider

trait Generators {
  import org.scalacheck.Gen

  val accountRefsForHappyPath: Gen[String] = for {
    accountRefInit <- Gen oneOf Array("AAAA", "AABB", "AACC", "AADD")
    accountRefTail <- nonEmptyAlphaNumStrings
  } yield accountRefInit + accountRefTail

  protected val nonEmptyAlphaNumStrings: Gen[String] = for {
    n     <- Gen.chooseNum(1, MAX_ID_LEN)
    chars <- Gen.containerOfN[Array, Char](n, Gen.alphaNumChar)
  } yield chars.mkString

  protected val ninos: Gen[String] = for {
    char0 <- Gen.alphaUpperChar
    char1 <- Gen.alphaUpperChar
    chars <- Gen.listOfN(6, Gen.numChar)
    char8 <- Gen oneOf "ABCD"
  } yield char0 +: char1 +: chars.mkString :+ char8

  lazy protected val postcodes: Gen[String] = for {
    n        <- Gen.chooseNum(1, 2)
    letters1 <- Gen.listOfN(n, Gen.alphaUpperChar)
    num1     <- Gen.chooseNum(1, 99)
    num2     <- Gen.chooseNum(1, 9)
    letters2 <- Gen.listOfN(2, Gen.alphaUpperChar)
  } yield s"$letters1$num1 $num2$letters2"

  lazy protected val childCareProviders: Gen[ChildCareProvider] = for {
    urn      <- nonEmptyAlphaNumStrings
    postcode <- postcodes
  } yield ChildCareProvider(urn, postcode)

  lazy private val MAX_ID_LEN = 32
}
