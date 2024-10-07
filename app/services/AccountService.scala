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
  def getLinkAccountResponse(accountRef: String): LinkAccountsResponse = accounts(accountRef take 4).linkAccountsResponse

  def getAccountBalanceResponse(accountRef: String): CheckBalanceResponse = accounts(accountRef take 4).balanceResponse

  def getPaymentResponse(accountRef: String): MakePaymentResponse = accounts(accountRef take 4).paymentResponse

  private val accounts = Map(
    "AABB" -> Account(
      LinkAccountsResponse("Benjamin Button"),
      CheckBalanceResponse(
        AccountStatus.BLOCKED,
        5500,
        4500,
        6000,
        11500,
        9000
      ),
      MakePaymentResponse("1234567887654322", LocalDate.of(2024, 10, 2))
    ),
    "AACC" -> Account(
      LinkAccountsResponse("Christopher Columbus"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        6500,
        3500,
        7000,
        13500,
        10000
      ),
      MakePaymentResponse("1234567887654323", LocalDate.of(2024, 10, 3))
    ),
    "AADD" -> Account(
      LinkAccountsResponse("Donald Duck"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        7500,
        2500,
        8000,
        16500,
        11000
      ),
      MakePaymentResponse("1234567887654324", LocalDate.of(2024, 10, 4))
    ),
    "AAEE" -> Account(
      LinkAccountsResponse("Donald Duck"),
      CheckBalanceResponse(
        AccountStatus.UNKNOWN,
        7500,
        2500,
        8000,
        16500,
        11000
      ),
      MakePaymentResponse("1234567887654325", LocalDate.of(2024, 10, 5))
    ),
    "AAFF" -> Account(
      LinkAccountsResponse("Fred Flintstone"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        4500,
        5500,
        5000,
        9500,
        8000
      ),
      MakePaymentResponse("1234567887654326", None)
    )
  ) withDefaultValue
    Account(
      LinkAccountsResponse("Peter Pan"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        4500,
        5500,
        5000,
        9500,
        8000
      ),
      MakePaymentResponse("1234567887654321", LocalDate.of(2024, 10, 1))
    )
}
