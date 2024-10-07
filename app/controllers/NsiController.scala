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

import models.request._
import models.response.ErrorResponse
import services.AccountService

import play.api.Configuration
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class NsiController @Inject() (
    conf: Configuration,
    cc: ControllerComponents,
    correlate: CorrelationIdAction,
    accountService: AccountService
  ) extends BackendController(cc) {

  def link(accountRef: String, requestData: LinkAccountsRequest): Action[AnyContent] = correlate {
    withNsiErrorScenarios(accountRef, Ok, accountService.getLinkAccountResponse)
  }

  def balance(accountRef: String, requestData: CheckBalanceRequest): Action[AnyContent] = correlate {
    withNsiErrorScenarios(accountRef, Ok, accountService.getAccountBalanceResponse)
  }

  def payment(): Action[JsValue] = correlate(parse.json).async { implicit req =>
    withJsonBody { body: MakePaymentRequest =>
      withNsiErrorScenarios(body.tfc_account_ref, Created, accountService.getPaymentResponse)
    }
  }

  private def withNsiErrorScenarios[A: Writes](accountRef: String, status: Status, getBody: String => A) = {
    testErrorScenarios.get(accountRef take 4) match {
      case Some(errorResponse) => errorResponse.toResult
      case None                => status(Json toJson getBody(accountRef))
    }
  }

  private val testErrorScenarios = for {
    (accountRef, string) <- conf.get[Map[String, String]]("errorResponses")
    errorResponse        <- ErrorResponse parse string
  } yield accountRef -> errorResponse

  private def withJsonBody[T: Manifest: Reads](f: T => Result)(implicit request: Request[JsValue]): Future[Result] =
    withJsonBody(f andThen Future.successful)
}
