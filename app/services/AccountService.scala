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

import java.time.LocalDate

import com.google.inject.{Inject, Singleton}
import models.Account
import models.response.CheckBalanceResponse.AccountStatus
import models.response.{CheckBalanceResponse, LinkAccountsResponse, MakePaymentResponse}

@Singleton
class AccountService @Inject() {
  def getLinkAccountResponse(accountRef: String): Option[LinkAccountsResponse] = accounts get (accountRef take 4) map (_.linkAccountsResponse)

  def getAccountBalanceResponse(accountRef: String): Option[CheckBalanceResponse] = accounts get (accountRef take 4) map (_.balanceResponse)

  def getPaymentResponse(accountRef: String): Option[MakePaymentResponse] = accounts get (accountRef take 4) map (_.paymentResponse)

  private val accounts = Map(
    "AAAA" -> Account(
      LinkAccountsResponse("Peter Pan"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        31415,
        65,
        66,
        67,
        68
      ),
      MakePaymentResponse("1234567887654321", LocalDate.of(2024,10,1))
    ),
    "AABB" -> Account(
      LinkAccountsResponse("Benjamin Button"),
      CheckBalanceResponse(
        AccountStatus.BLOCKED,
        92653,
        69,
        70,
        71,
        72
      ),
      MakePaymentResponse("1234567887654322", LocalDate.of(2024,10,2))
    ),
    "AACC" -> Account(
      LinkAccountsResponse("Christopher Columbus"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        58979,
        73,
        74,
        75,
        76
      ),
      MakePaymentResponse("1234567887654323", LocalDate.of(2024,10,3))
    ),
    "AADD" -> Account(
      LinkAccountsResponse("Donald Duck"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        32384,
        77,
        78,
        79,
        80
      ),
      MakePaymentResponse("1234567887654324", LocalDate.of(2024,10,4))
    ),
    "AAEE" -> Account(
      LinkAccountsResponse("Donald Duck"),
      CheckBalanceResponse(
        AccountStatus.UNKNOWN,
        62643,
        81,
        82,
        83,
        84
      ),
      MakePaymentResponse("1234567887654325", LocalDate.of(2024,10,5))
    ),
    "AAFF" -> Account(
      LinkAccountsResponse("Fred Flintstone"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        38327,
        85,
        86,
        87,
        88
      ),
      MakePaymentResponse("1234567887654326", LocalDate.of(2024,10,6))
    )
  )
}
