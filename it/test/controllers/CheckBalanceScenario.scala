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
  import org.scalacheck.Gen

  val genWithRandomNino: Gen[CheckBalanceScenario] = ninos flatMap genWithFixedNino

  def genWithFixedNino(nino: String): Gen[CheckBalanceScenario] =
    for {
      correlation_id <- Gen.uuid
      account_ref    <- nonEmptyAlphaNumStrings
      epp_urn        <- nonEmptyAlphaNumStrings
      epp_account    <- nonEmptyAlphaNumStrings
    } yield apply(correlation_id, account_ref, epp_urn, epp_account, nino)
}