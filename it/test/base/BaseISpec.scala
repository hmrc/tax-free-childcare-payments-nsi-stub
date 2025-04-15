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

package base

import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.WsTestClient

abstract class BaseISpec(config: (String, Any)*)
    extends AnyWordSpec
    with should.Matchers
    with OptionValues
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with WsTestClient
    with Status
    with TableDrivenPropertyChecks
    with JsonGenerators
    with ScalaCheckPropertyChecks {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(config: _*)
      .build()

  private val contextRoot = "/tax-free-childcare-payments-nsi-stub"
  protected val baseUrl   = s"http://localhost:$port$contextRoot"

  protected val link_url    = "/account/v1/accounts/link-to-epp"
  protected val balance_url = "/account/v1/accounts/balance"
  protected val payment_url = "/payment/v1/payments/pay-childcare"

  protected val CORRELATION_ID = "correlationId"
}
