package models

import models.response.{CheckBalanceResponse, LinkAccountsResponse, MakePaymentResponse}

case class Account (linkAccountsResponse: LinkAccountsResponse,
                    balanceResponse: CheckBalanceResponse,
                    paymentResponse: MakePaymentResponse)
