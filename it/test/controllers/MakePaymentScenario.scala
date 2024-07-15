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

package controllers

import base.Generators
import models.request.ChildCareProvider
import models.response.MakePaymentResponse
import play.api.libs.json._

import java.util.UUID

final case class MakePaymentScenario(
    correlation_id: UUID,
    account_ref: String,
    epp_urn: String,
    epp_account: String,
    parent_nino: String,
    ccp_opt: Option[ChildCareProvider],
    payment_amount: Int
  ) {

  /** This should match the Swagger API spec in <https://docs.google.com/document/d/1QkNM3HCp228OwFS7elTtboKjmFS6jqS7>. */
  val requestBody: JsObject = Json.obj(
    "childAccountPaymentRef" -> account_ref,
    "eppURN"                 -> epp_urn,
    "eppAccount"             -> epp_account,
    "parentNino"             -> parent_nino,
    "payeeType"              -> (if (ccp_opt.isDefined) "CCP" else "EPP"),
    "ccpURN"                 -> ccp_opt.map(_.urn),
    "ccpPostcode"            -> ccp_opt.map(_.postcode),
    "amount"                 -> payment_amount
  )
}

object MakePaymentScenario extends Generators {
  import org.scalacheck.Gen
  import play.api.libs.functional.syntax.toFunctionalBuilderOps

  import java.time.LocalDate

  val random: Gen[MakePaymentScenario] = accountRefsForHappyPath flatMap withFixedAccountRef

  def withFixedAccountRef(account_ref: String): Gen[MakePaymentScenario] =
    for {
      correlation_id <- Gen.uuid
      epp_urn        <- nonEmptyAlphaNumStrings
      epp_account    <- nonEmptyAlphaNumStrings
      nino           <- ninos
      ccp_opt        <- Gen option childCareProviders
      payment_amount <- Gen.posNum[Int]
    } yield apply(correlation_id, account_ref, epp_urn, epp_account, nino, ccp_opt, payment_amount)

  /** This should match the Swagger API spec in <https://docs.google.com/document/d/1QkNM3HCp228OwFS7elTtboKjmFS6jqS7>. */
  val expectedResponseFormat: Reads[MakePaymentResponse] = (
    (__ \ "paymentReference").read[String] ~
      (__ \ "paymentDate").read[LocalDate]
  )(MakePaymentResponse.apply _)
}
