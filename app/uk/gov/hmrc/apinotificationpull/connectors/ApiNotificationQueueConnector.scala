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

package uk.gov.hmrc.apinotificationpull.connectors

import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import uk.gov.hmrc.apinotificationpull.config.ServiceConfiguration
import uk.gov.hmrc.apinotificationpull.logging.NotificationLogger
import uk.gov.hmrc.apinotificationpull.model.{Notification, Notifications}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, NotFoundException}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.Future

class ApiNotificationQueueConnector @Inject()(config: ServiceConfiguration, http: HttpClient, logger: NotificationLogger) {

  private lazy val serviceBaseUrl: String = config.baseUrl("api-notification-queue")

  def getNotifications()(implicit hc: HeaderCarrier): Future[Notifications] = {
    val url = s"$serviceBaseUrl/notifications"
    logger.debug(s"Calling get notifications using url: $url", hc.headers)
    http.GET[Notifications](url)
  }

  def getById(notificationId: String)(implicit hc: HeaderCarrier): Future[Option[Notification]] = {

    val url = s"$serviceBaseUrl/notification/$notificationId"
    logger.debug(s"Getting notification by id using url: $url", hc.headers)
    http.GET[HttpResponse](url)
      .map { r =>
        logger.debug(s"Notification received successfully with id: $notificationId", hc.headers)
        Some(Notification(notificationId, r.allHeaders.map(h => h._1 -> h._2.head), r.body))
      }
      .recoverWith {
        case _: NotFoundException => {
          logger.debug(s"Notification not found with id: $notificationId", hc.headers)
          Future.successful(None)
        }
      }
  }

  def delete(notification: Notification)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val url = s"$serviceBaseUrl/notification/${notification.id}"
    logger.debug(s"Calling delete notifications using url: $url", hc.headers)
    http.DELETE(url)
  }

}
