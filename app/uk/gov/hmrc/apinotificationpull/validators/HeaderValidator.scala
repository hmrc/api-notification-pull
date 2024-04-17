/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.apinotificationpull.validators

import javax.inject.{Inject, Singleton}
import play.api.http.HeaderNames._
import play.api.http.Status._
import play.api.mvc.{ActionBuilder, AnyContent, BodyParser, ControllerComponents, Request, Result, Results}
import uk.gov.hmrc.apinotificationpull.controllers.CustomHeaderNames.{ACCEPT_HEADER_VALUE, X_CLIENT_ID_HEADER_NAME, getHeadersFromRequest}
import uk.gov.hmrc.apinotificationpull.logging.NotificationLogger

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HeaderValidator @Inject()(logger: NotificationLogger, cc: ControllerComponents) extends Results {

  private def validateHeader(rules: Option[String] => Boolean, headerName: String, error: Result): ActionBuilder[Request, AnyContent] =
    new ActionBuilder[Request, AnyContent] {
      override protected def executionContext: ExecutionContext = cc.executionContext
      override def parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser
      override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
        implicit val implicitRequest: Request[A] = request
        val maybeHeader = request.headers.get(headerName)
        if (rules(maybeHeader)) {
          logger.debug(s"$headerName passed validation: $maybeHeader")
          block(request)
        } else {
          logger.info(s"$headerName failed validation: $maybeHeader")
          Future.successful(error)
        }
      }
  }

  private val acceptHeaderRules: Option[String] => Boolean = _ contains ACCEPT_HEADER_VALUE
  private val xClientIdHeaderRules: Option[String] => Boolean = _ exists (_ => true)

  def validateAcceptHeader: ActionBuilder[Request, AnyContent] = validateHeader(acceptHeaderRules, ACCEPT, Status(NOT_ACCEPTABLE))
  def validateXClientIdHeader: ActionBuilder[Request, AnyContent] = validateHeader(xClientIdHeaderRules, X_CLIENT_ID_HEADER_NAME, Status(INTERNAL_SERVER_ERROR))
}
