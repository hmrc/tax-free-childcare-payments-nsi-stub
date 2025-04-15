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

package models.request

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._

final case class MakePaymentRequest(
    tfc_account_ref: String,
    epp_urn: String,
    epp_account: String,
    parent_nino: String,
    ccp_opt: Option[ChildCareProvider],
    payment_amount: Int
)

object MakePaymentRequest extends ConstraintReads {

  /** This should match the API spec in <https://docs.google.com/document/d/1Z8mPFoOJQbkELv_3PSnODyaicL0Gx17P>. */
  implicit val reads: Reads[MakePaymentRequest] = (
    (__ \ "childAccountPaymentRef").read(minLength[String](1)) ~
      (__ \ "eppURN").read(minLength[String](1)) ~
      (__ \ "eppAccount").read(minLength[String](1)) ~
      (__ \ "parentNino").read(pattern(NINO_PATTERN)) ~
      readsOptCCP ~
      (__ \ "amount").read[Int](min(1))
  )(apply _)

  private lazy val readsOptCCP = (__ \ "payeeType").read[PayeeType.Value].flatMap(readOptCCP)

  private def readOptCCP(payeeType: PayeeType.Value) = Reads { json =>
    payeeType match {
      case PayeeType.CCP => json.validate[ChildCareProvider].map(Some.apply)
      case PayeeType.EPP => JsSuccess(None)
    }
  }

  private object PayeeType extends Enumeration {
    val CCP, EPP = Value

    implicit val reads: Reads[PayeeType.Value] = Json.formatEnum(this)
  }

  private lazy val NINO_PATTERN = "[A-Z]{2}\\d{6}[A-D]".r
}
