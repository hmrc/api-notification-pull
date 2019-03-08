/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.apinotificationpull.controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, AnyContent, Request, Result}
import uk.gov.hmrc.apinotificationpull.controllers.CustomHeaderNames.X_CLIENT_ID_HEADER_NAME
import uk.gov.hmrc.apinotificationpull.logging.NotificationLogger
import uk.gov.hmrc.apinotificationpull.model.XmlErrorResponse
import uk.gov.hmrc.apinotificationpull.presenters.NotificationPresenter
import uk.gov.hmrc.apinotificationpull.services.ApiNotificationQueueService
import uk.gov.hmrc.apinotificationpull.util.XmlBuilder
import uk.gov.hmrc.apinotificationpull.validators.HeaderValidator
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.controller.BaseController

@Singleton
class NotificationsController @Inject()(apiNotificationQueueService: ApiNotificationQueueService,
                                        headerValidator: HeaderValidator,
                                        notificationPresenter: NotificationPresenter,
                                        xmlBuilder: XmlBuilder,
                                        logger: NotificationLogger) extends BaseController {

  implicit val hc: HeaderCarrier = HeaderCarrier()

  private def recovery[A](request: Request[A]): PartialFunction[Throwable, Result] = {
    case e =>
      logger.error(s"An unexpected error occurred: ${e.getMessage}", request.headers.headers)
      InternalServerError(XmlErrorResponse("An unexpected error occurred"))
  }

  def delete(notificationId: String): Action[AnyContent] =
    (headerValidator.validateAcceptHeader andThen headerValidator.validateXClientIdHeader).async { implicit request =>

      logger.debug("In NotificationsController.delete", request.headers.headers)
      implicit val hc: HeaderCarrier = buildHeaderCarrier(request)
      apiNotificationQueueService.getAndRemoveNotification(notificationId)
        .map(notificationPresenter.present)
        .recover(recovery(request))
  }

  def getAll: Action[AnyContent] =
    (headerValidator.validateAcceptHeader andThen headerValidator.validateXClientIdHeader).async { implicit request =>

      logger.debug("In NotificationsController.getAll", request.headers.headers)
      implicit val hc: HeaderCarrier = buildHeaderCarrier(request)
      apiNotificationQueueService.getNotifications().map { notifications =>
        Ok(xmlBuilder.toXml(notifications)).as(XML)
      } recover recovery(request)
  }

  private def buildHeaderCarrier(request: Request[AnyContent]): HeaderCarrier = {
    val maybeClientId = request.headers.get(X_CLIENT_ID_HEADER_NAME)
    maybeClientId match {
      case Some(clientId: String) =>
        logger.debug(s"Got client id from header: $maybeClientId", request.headers.headers)
        hc.withExtraHeaders(X_CLIENT_ID_HEADER_NAME -> clientId)
      case _ =>
        // It should never happen
        logger.warn(s"Header $X_CLIENT_ID_HEADER_NAME not found in the request.", request.headers.headers)
        hc
    }
  }
}
