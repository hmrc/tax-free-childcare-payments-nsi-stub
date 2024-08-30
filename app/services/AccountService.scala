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
  private val PAYMENT_DATE = LocalDate parse "2024-10-01"

  def getLinkAccountResponse(accountRef: String): Option[LinkAccountsResponse] = accounts get (accountRef take 4) map (_.linkAccountsResponse)

  def getAccountBalanceResponse(accountRef: String): Option[CheckBalanceResponse] = accounts get (accountRef take 4) map (_.balanceResponse)

  def getPaymentResponse(accountRef: String): Option[MakePaymentResponse] = accounts get (accountRef take 4) map (_.paymentResponse)

  private val accounts = Map(
    "AAAA" -> Account(
      LinkAccountsResponse("Peter Pan"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        31415,
        100,
        100,
        100,
        100
      ),
      MakePaymentResponse("8327950288419716", PAYMENT_DATE)
    ),
    "AABB" -> Account(
      LinkAccountsResponse("Benjamin Button"),
      CheckBalanceResponse(
        AccountStatus.BLOCKED,
        92653,
        100,
        100,
        100,
        100
      ),
      MakePaymentResponse("8327950288419716", PAYMENT_DATE)
    ),
    "AACC" -> Account(
      LinkAccountsResponse("Christopher Columbus"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        58979,
        100,
        100,
        100,
        100
      ),
      MakePaymentResponse("8327950288419716", PAYMENT_DATE)
    ),
    "AADD" -> Account(
      LinkAccountsResponse("Donald Duck"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        32384,
        100,
        100,
        100,
        100
      ),
      MakePaymentResponse("8327950288419716", PAYMENT_DATE)
    ),
    "AAEE" -> Account(
      LinkAccountsResponse("Donald Duck"),
      CheckBalanceResponse(
        AccountStatus.UNKNOWN,
        62643,
        100,
        100,
        100,
        100
      ),
      MakePaymentResponse("8327950288419716", PAYMENT_DATE)
    ),
    "AAFF" -> Account(
      LinkAccountsResponse("Fred Flintstone"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        38327,
        100,
        100,
        100,
        100
      ),
      MakePaymentResponse("8327950288419716", None)
    )
  )
}
