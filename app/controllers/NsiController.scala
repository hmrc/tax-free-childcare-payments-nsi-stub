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

import models.ErrorResponse
import models.ErrorResponse.Code._
import models.request._
import play.api.Logging
import play.api.libs.json.{JsValue, Json, Reads, Writes}
import play.api.mvc._
import services.AccountService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class NsiController @Inject() (
    cc: ControllerComponents,
    correlate: CorrelationIdAction,
    accountService: AccountService
  ) extends BackendController(cc) with Logging {
  import NsiController._

  def link(accountRef: String, requestData: LinkAccountsRequest): Action[AnyContent] = correlate {
    withNsiErrorScenarios(accountRef, accountService.getLinkAccountResponse, json => Created(json: JsValue))
  }

  def balance(accountRef: String, requestData: CheckBalanceRequest): Action[AnyContent] = correlate {
    withNsiErrorScenarios(accountRef, accountService.getAccountBalanceResponse, json => Ok(json: JsValue))
  }

  def payment(): Action[JsValue] = correlate(parse.json).async { implicit req =>
    withJsonBody { body: MakePaymentRequest =>
      withNsiErrorScenarios(body.tfc_account_ref, accountService.getPaymentResponse, json => Created(json: JsValue))
    }
  }

  private def withNsiErrorScenarios[A: Writes](accountRef: String, block: String => Option[A], toResult: JsValue => Result) = {
    testErrorScenarios.get(accountRef take 4) match {
      case Some(nsiErrorCode) =>
        new Status(nsiErrorCode.status)(
          Json.toJson(nsiErrorCode)
        )
      case None               =>
        block(accountRef) match {
          case Some(model) => toResult(Json.toJson(model))
          case None        => BadRequest(
              Json.toJson(ErrorResponse(E0000, s"Unsupported test scenario: $accountRef"))
            )
        }
    }
  }

  private def withJsonBody[T: Manifest: Reads](f: T => Result)(implicit request: Request[JsValue]): Future[Result] =
    withJsonBody(f andThen Future.successful)
}

object NsiController {

  private val testErrorScenarios = Map(
    "EEAA" -> E0000,
    "EEBL" -> E0001Link,
    "EEBB" -> E0001Balance,
    "EEBP" -> E0001Payment,
    "EECC" -> E0002,
    "EEDD" -> E0003,
    "EEEE" -> E0004,
    "EEFF" -> E0005,
    "EEGG" -> E0006,
    "EEHH" -> E0007,
    "EEII" -> E0008,
    "EELL" -> E0020,
    "EEMM" -> E0021,
    "EENN" -> E0022,
    "EEOO" -> E0023,
    "EEPP" -> E0024,
    "EEQQ" -> E0025,
    "EERR" -> E0026,
    "EESS" -> E0401,
    "EETT" -> E0030,
    "EEUU" -> E0031,
    "EEVV" -> E0032,
    "EEWW" -> E0033,
    "EEXX" -> E0034,
    "EEYY" -> E0035,
    "EEZZ" -> E0040,
    "EEBA" -> E0041,
    "EEBC" -> E0042,
    "EEBD" -> E0043,
    "EEBE" -> E9000,
    "EEBF" -> E9999,
    "EEBG" -> E8000,
    "EEBH" -> E8001,
    "EEBI" -> UNKNOWN
  )
}
