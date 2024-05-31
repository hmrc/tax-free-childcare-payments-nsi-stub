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

import play.api.libs.json.{JsObject, Json}

import scala.reflect.ClassTag
import scala.util.Random

trait JsonGenerators {

  def randomSharedRequestDataJsonWithNino(nino: String): JsObject = Json.obj(
    "epp_unique_customer_id"     -> randomStringOf(16, alphaNumChars),
    "epp_reg_reference"          -> randomStringOf(11, alphaNumChars),
    "outbound_child_payment_ref" -> randomStringOf(12, alphaNumChars),
    "nino"                       -> nino
  )

  private def randomStringOf[A: ClassTag](n: Int, elems: Seq[A]) = {
    def randomElem = elems(Random.nextInt(elems.length))
    Array.fill(n)(randomElem).mkString
  }

  private val alphaNumChars = ('0' to '9') ++ ('a' to 'z') ++ ('A' to 'Z')
}
