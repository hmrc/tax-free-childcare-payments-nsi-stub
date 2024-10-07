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

package models.response

import java.time.LocalDate

final case class MakePaymentResponse(
    payment_ref: String,
    payment_date: Option[LocalDate]
  )

object MakePaymentResponse {
  import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
  import play.api.libs.json.{__, OWrites}

  def apply(payment_ref: String, payment_date: LocalDate): MakePaymentResponse = apply(payment_ref, Some(payment_date))

  implicit val writes: OWrites[MakePaymentResponse] = (
    (__ \ "paymentReference").write[String] ~
      (__ \ "paymentDate").writeNullable[LocalDate]
  )(unlift(unapply))

  def parse(config: String): Option[MakePaymentResponse] =
    config.split(",").map(_.trim).toList match {
      case reference :: rest => Some(apply(reference, rest.headOption map LocalDate.parse))
      case _                 => None
    }
}
