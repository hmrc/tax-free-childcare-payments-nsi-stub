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

import javax.inject.Singleton
import scala.concurrent.{ExecutionContext, Future}

import com.google.inject.Inject

import play.api.http.Status
import play.api.mvc._

@Singleton
class CorrelationIdAction @Inject() (
    val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends ActionBuilder[Request, AnyContent]
    with Results
    with Status {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] =
    block(request).map { response =>
      request.headers.get(CORRELATION_ID) match {
        case Some(correlationId) => response.withHeaders(CORRELATION_ID -> correlationId)
        case None                => response
      }

    }

  /** This should match the header name in the Swagger API spec at
    * <https://drive.google.com/drive/folders/1ES36CjJpVumXXCM8VC5VQQa7J3xIIqoW>.
    */
  private val CORRELATION_ID = "correlationId"
}
