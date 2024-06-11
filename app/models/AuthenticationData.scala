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

package models

import play.api.mvc.QueryStringBindable

final case class AuthenticationData(epp_urn: String, epp_account: String, parent_nino: String)

object AuthenticationData {

  implicit val binder: QueryStringBindable[AuthenticationData] = new QueryStringBindable[AuthenticationData] {

    def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, AuthenticationData]] = {
      val optData = for {
        epp_urn     <- params get epp_urn_key flatMap (_.headOption)
        epp_account <- params get epp_account_key flatMap (_.headOption)
        parent_nino <- params get parent_nino_key flatMap (_.headOption)
      } yield apply(epp_urn, epp_account, parent_nino)

      optData map Right.apply
    }

    def unbind(key: String, value: AuthenticationData): String = Map(
      epp_urn_key     -> value.epp_urn,
      epp_account_key -> value.epp_account,
      parent_nino_key -> value.parent_nino
    )
      .map { case (k, v) => s"$k=$v" }
      .mkString("&")
  }

  private val epp_urn_key     = "eppURN"
  private val epp_account_key = "eppAccount"
  private val parent_nino_key = "parentNino"
}
