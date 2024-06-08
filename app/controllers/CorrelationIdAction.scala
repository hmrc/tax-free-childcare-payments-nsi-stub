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

import java.util.UUID
import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

import com.google.inject.Inject

import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.backend.http.ErrorResponse

@Singleton
class CorrelationIdAction @Inject() (
    val parser: BodyParsers.Default
  )(implicit val executionContext: ExecutionContext
  ) extends ActionBuilder[Request, AnyContent] with Results with Status {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    val maybeCorrelationId = for {
      correlationIdHeader <- request.headers get CORRELATION_ID toRight "Missing correlation ID."
      correlationId       <- Try(UUID fromString correlationIdHeader).toEither.left.map(_.getMessage)
    } yield correlationId

    maybeCorrelationId match {
      case Left(errorMessage) =>
        Future.successful(BadRequest(Json.toJson(
          ErrorResponse(statusCode = BAD_REQUEST, message = errorMessage)
        )))

      case Right(correlationId) =>
        block(request) map { response =>
          response.withHeaders(CORRELATION_ID -> correlationId.toString)
        }
    }
  }

  private val CORRELATION_ID = "Correlation-ID"
}
