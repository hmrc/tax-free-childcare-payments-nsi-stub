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

package models

import models.response.{CheckBalanceResponse, LinkAccountsResponse, MakePaymentResponse}

final case class Account(
    linkAccountsResponse: LinkAccountsResponse,
    balanceResponse: CheckBalanceResponse,
    paymentResponse: MakePaymentResponse
  )

object Account {

  def parse(config: String): Option[Account] =
    config.split("\\|").map(_.trim).toList match {
      case childFullName :: balanceConfig :: paymentConfig :: _ =>
        for {
          balance <- CheckBalanceResponse parse balanceConfig
          payment <- MakePaymentResponse parse paymentConfig
        } yield apply(
          LinkAccountsResponse(childFullName),
          balance,
          payment
        )

      case _ => None
    }
}
