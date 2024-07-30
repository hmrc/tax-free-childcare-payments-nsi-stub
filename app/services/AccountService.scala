package services

import com.google.inject.{Inject, Singleton}
import controllers.NsiController.PAYMENT_DATE
import models.Account
import models.response.CheckBalanceResponse.AccountStatus
import models.response.{CheckBalanceResponse, LinkAccountsResponse, MakePaymentResponse}

import java.time.LocalDate

@Singleton
class AccountService @Inject() {
  private val PAYMENT_DATE = LocalDate parse "2024-10-01"

  def getLinkAccountResponse(accountRef: String) = accounts get (accountRef take 4) map (_.linkAccountsResponse)
  def getAccountBalanceResponse(accountRef: String) = accounts get (accountRef take 4) map (_.balanceResponse)
  def getPaymentResponse(accountRef: String) = accounts get (accountRef take 4) map (_.paymentResponse)

  private val accounts = Map(
    "AAAA" -> Account(
      LinkAccountsResponse("Peter Pan"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        14159,
        26535,
        89793,
        23846,
        26433),
      MakePaymentResponse("8327950288419716", PAYMENT_DATE)),
    "AABB" -> Account(
      LinkAccountsResponse("Benjamin Button"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        14159,
        26535,
        89793,
        23846,
        26433),
      MakePaymentResponse("8327950288419716", PAYMENT_DATE)),
    "AACC" -> Account(
      LinkAccountsResponse("Christopher Columbus"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        14159,
        26535,
        89793,
        23846,
        26433),
      MakePaymentResponse("8327950288419716", PAYMENT_DATE)),
    "AADD" -> Account(
      LinkAccountsResponse("Donald Duck"),
      CheckBalanceResponse(
        AccountStatus.ACTIVE,
        14159,
        26535,
        89793,
        23846,
        26433),
      MakePaymentResponse("8327950288419716", PAYMENT_DATE))
  )
}
