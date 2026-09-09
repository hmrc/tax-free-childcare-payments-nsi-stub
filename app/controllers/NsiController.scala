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

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import models.request.*
import models.response.ErrorResponse
import services.AccountService
import utils.ConfigMapping
import play.api.Configuration
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import play.api.mvc.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.net.URLDecoder

@Singleton
class NsiController @Inject() (
    protected val config: Configuration,
    cc: ControllerComponents,
    correlate: CorrelationIdAction,
    accountService: AccountService
) extends BackendController(cc)
    with ConfigMapping {

  def link(accountRef: String): Action[AnyContent] = correlate {
    withNsiErrorScenarios(URLDecoder.decode(accountRef, "UTF-8"), Ok, accountService.getLinkAccountResponse)
  }

  def balance(accountRef: String): Action[AnyContent] = correlate {
    withNsiErrorScenarios(URLDecoder.decode(accountRef, "UTF-8"), Ok, accountService.getAccountBalanceResponse)
  }

  def payment(): Action[JsValue] = correlate(parse.json).async { request =>
    given Request[JsValue] = request

    withJsonBody { (body: MakePaymentRequest) =>
      withNsiErrorScenarios(body.tfc_account_ref, Created, accountService.getPaymentResponse)
    }
  }

  private def withNsiErrorScenarios[A: Writes](accountRef: String, status: Status, getBody: String => A) =
    testErrorScenarios.get(accountRef.take(4)) match {
      case Some(errorResponse) => errorResponse.toResult
      case None                => status(Json.toJson(getBody(accountRef)))
    }

  private val testErrorScenarios = getConfigMap("data.errorResponses")(ErrorResponse.parse)

  private def withJsonBody[T: Manifest: Reads](f: T => Result)(using Request[JsValue]): Future[Result] =
    withJsonBody(f.andThen(Future.successful))

}
