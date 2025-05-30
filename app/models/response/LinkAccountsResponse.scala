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

package models.response

import play.api.libs.json.{OWrites, __}

final case class LinkAccountsResponse(child_full_name: String)

object LinkAccountsResponse {

  /** This should match the Swagger API spec in <https://docs.google.com/document/d/1QkNM3HCp228OwFS7elTtboKjmFS6jqS7>.
    */
  implicit val writes: OWrites[LinkAccountsResponse] =
    (__ \ "childFullName").write[String].contramap(_.child_full_name)

}
