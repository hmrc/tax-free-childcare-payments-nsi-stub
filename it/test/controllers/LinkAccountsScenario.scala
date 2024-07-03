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
import models.response.LinkAccountsResponse
import play.api.libs.json.{JsObject, Json, Reads, __}

import java.time.LocalDate
import java.util.UUID

final case class LinkAccountsScenario(
    correlation_id: UUID,
    account_ref: String,
    epp_urn: String,
    epp_account: String,
    parent_nino: String,
    child_dob: LocalDate
  ) {

  /** This should match the Swagger API spec in <https://docs.google.com/document/d/1QkNM3HCp228OwFS7elTtboKjmFS6jqS7>. */
  val requestBody: JsObject = Json.obj(
    "eppURN"     -> epp_urn,
    "eppAccount" -> epp_account,
    "parentNino" -> parent_nino,
    "childDoB"   -> child_dob
  )
}

object LinkAccountsScenario extends Generators {
  import org.scalacheck.Gen

  val random: Gen[LinkAccountsScenario] = nonEmptyAlphaNumStrings flatMap withFixedAccountRef

  def withFixedAccountRef(account_ref: String): Gen[LinkAccountsScenario] =
    for {
      correlation_id <- Gen.uuid
      epp_urn        <- nonEmptyAlphaNumStrings
      epp_account    <- nonEmptyAlphaNumStrings
      nino           <- ninos
      child_age_days <- Gen.chooseNum(0, 18 * 365)
    } yield apply(correlation_id, account_ref, epp_urn, epp_account, nino, LocalDate.now() minusDays child_age_days)

  /** This should match the Swagger API spec in <https://docs.google.com/document/d/1QkNM3HCp228OwFS7elTtboKjmFS6jqS7>. */
  val expectedResponseFormat: Reads[LinkAccountsResponse] = (__ \ "childFullName").read[String] map LinkAccountsResponse.apply
}
