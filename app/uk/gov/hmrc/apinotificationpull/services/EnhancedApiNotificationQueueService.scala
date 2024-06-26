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

package uk.gov.hmrc.apinotificationpull.services

import uk.gov.hmrc.apinotificationpull.connectors.EnhancedApiNotificationQueueConnector
import uk.gov.hmrc.apinotificationpull.model.{Notification, NotificationStatus, Notifications}
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

class EnhancedApiNotificationQueueService @Inject()(enhancedApiNotificationQueueConnector: EnhancedApiNotificationQueueConnector) {

  def getNotificationBy(notificationId: String, notificationStatus: NotificationStatus.Value)(implicit hc: HeaderCarrier): Future[Either[UpstreamErrorResponse, Notification]] = {
    enhancedApiNotificationQueueConnector.getNotificationBy(notificationId, notificationStatus)
  }

  def getAllNotificationsBy(notificationStatus: NotificationStatus.Value)(implicit hc: HeaderCarrier): Future[Notifications] = {
    enhancedApiNotificationQueueConnector.getAllNotificationsBy(notificationStatus)
  }

  def getAllNotificationsBy(conversationId: UUID)(implicit hc: HeaderCarrier): Future[Notifications] = {
    enhancedApiNotificationQueueConnector.getAllNotificationsBy(conversationId)
  }

  def getAllNotificationsBy(conversationId: UUID, notificationStatus: NotificationStatus.Value)(implicit hc: HeaderCarrier): Future[Notifications] = {
    enhancedApiNotificationQueueConnector.getAllNotificationsBy(conversationId, notificationStatus)
  }
}
