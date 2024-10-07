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

import models.response.CheckBalanceResponse.AccountStatus

import play.api.libs.json.Format

final case class CheckBalanceResponse(
    account_status: AccountStatus.Value,
    top_up_available: Int,
    top_up_remaining: Int,
    paid_in: Int,
    total_balance: Int,
    cleared_funds: Int
  )

object CheckBalanceResponse {
  import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
  import play.api.libs.json.{__, Json, OWrites}

  object AccountStatus extends Enumeration {
    val ACTIVE, BLOCKED, UNKNOWN = Value

    implicit val format: Format[AccountStatus.Value] = Json.formatEnum(this)
  }

  implicit val writes: OWrites[CheckBalanceResponse] = (
    (__ \ "accountStatus").write[AccountStatus.Value] ~
      (__ \ "topUpAvailable").write[Int] ~
      (__ \ "topUpRemaining").write[Int] ~
      (__ \ "paidIn").write[Int] ~
      (__ \ "totalBalance").write[Int] ~
      (__ \ "clearedFunds").write[Int]
  )(unlift(unapply))

  def parse(config: String): Option[CheckBalanceResponse] =
    config.split(",").map(_.trim).toList match {
      case status :: topUpAvailable :: topUpRemaining :: paidIn :: totalBalance :: clearedFunds :: _ =>
        Some(
          apply(
            AccountStatus withName status,
            topUpAvailable.toInt,
            topUpRemaining.toInt,
            paidIn.toInt,
            totalBalance.toInt,
            clearedFunds.toInt
          )
        )

      case _ => None
    }
}
