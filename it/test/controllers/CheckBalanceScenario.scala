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
import models.request.CheckBalanceRequest
import models.response.CheckBalanceResponse

import java.util.UUID

final case class CheckBalanceScenario(
    correlation_id: UUID,
    account_ref: String,
    epp_urn: String,
    epp_account: String,
    parent_nino: String
  ) {

  val queryString: String = CheckBalanceRequest.binder.unbind("", requestData)

  lazy private val requestData = CheckBalanceRequest(epp_urn, epp_account, parent_nino)
}

object CheckBalanceScenario extends Generators {
  import CheckBalanceResponse.AccountStatus
  import org.scalacheck.Gen
  import play.api.libs.functional.syntax.toFunctionalBuilderOps
  import play.api.libs.json.{Reads, __}

  val random: Gen[CheckBalanceScenario] = accountRefsForHappyPath flatMap withFixedAccountRef

  def withFixedAccountRef(accountRef: String): Gen[CheckBalanceScenario] =
    for {
      correlation_id <- Gen.uuid
      epp_urn        <- nonEmptyAlphaNumStrings
      epp_account    <- nonEmptyAlphaNumStrings
      nino           <- ninos
    } yield apply(correlation_id, accountRef, epp_urn, epp_account, nino)

  /** This should match the API spec in <https://docs.google.com/document/d/10ULaEScNhaAZqFf1hEzxseJB2u_a2GgS>. */
  val expectedResponseFormat: Reads[CheckBalanceResponse] = (
    (__ \ "accountStatus").read[AccountStatus.Value] ~
      (__ \ "topUpAvailable").read[Int] ~
      (__ \ "topUpRemaining").read[Int] ~
      (__ \ "paidIn").read[Int] ~
      (__ \ "totalBalance").read[Int] ~
      (__ \ "clearedFunds").read[Int]
  )(CheckBalanceResponse.apply _)
}
