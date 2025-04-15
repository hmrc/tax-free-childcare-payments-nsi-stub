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

package services

import com.google.inject.{Inject, Singleton}
import models.Account
import models.response.{CheckBalanceResponse, LinkAccountsResponse, MakePaymentResponse}
import utils.ConfigMapping

import play.api.Configuration

@Singleton
class AccountService @Inject() (protected val config: Configuration) extends ConfigMapping {

  def getLinkAccountResponse(accountRef: String): LinkAccountsResponse = accounts(
    accountRef.take(4)
  ).linkAccountsResponse

  def getAccountBalanceResponse(accountRef: String): CheckBalanceResponse = accounts(accountRef.take(4)).balanceResponse

  def getPaymentResponse(accountRef: String): MakePaymentResponse = accounts(accountRef.take(4)).paymentResponse

  private val accounts = {
    val accountsBuilder = getConfigMap("data.accounts")(Account.parse)
    accountsBuilder.withDefaultValue(accountsBuilder("default"))
  }

}
