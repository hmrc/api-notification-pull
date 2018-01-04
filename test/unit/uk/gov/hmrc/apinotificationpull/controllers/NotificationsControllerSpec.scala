/*
 * Copyright 2018 HM Revenue & Customs
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

import java.util.UUID

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.http.HeaderNames._
import play.api.http.Status._
import play.api.test.FakeRequest
import uk.gov.hmrc.apinotificationpull.fakes.SuccessfulHeaderValidatorFake
import uk.gov.hmrc.apinotificationpull.model.Notifications
import uk.gov.hmrc.apinotificationpull.services.ApiNotificationQueueService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class NotificationsControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  private val notificationId1 = UUID.randomUUID()
  private val notificationId2 = UUID.randomUUID()
  private val notifications = Notifications(List(s"/notification/$notificationId1", s"/notification/$notificationId2"))

  private val xClientIdHeader = "X-Client-ID"
  private val clientId = "client_id"

  private val validHeaders = Seq(ACCEPT -> "application/vnd.hmrc.1.0+xml", xClientIdHeader -> clientId)

  trait Setup {
    implicit val materializer = fakeApplication.materializer

    val headerValidator = new SuccessfulHeaderValidatorFake

    val mockApiNotificationQueueService = mock[ApiNotificationQueueService]
    val controller = new NotificationsController(mockApiNotificationQueueService, headerValidator)

    when(mockApiNotificationQueueService.getNotifications()(any(classOf[HeaderCarrier])))
      .thenReturn(Future.successful(notifications))
  }

  "delete notification" should {

    val validRequest = FakeRequest("DELETE", s"/$notificationId1").withHeaders(validHeaders: _*)

    "return 404 NOT_FOUND response when the notification does not exist" in new Setup {
        val result = await(controller.delete(notificationId1.toString).apply(validRequest))

        status(result) shouldBe NOT_FOUND
      }
    }

  "get all notifications" should {

    val validRequest = FakeRequest("GET", "/").withHeaders(validHeaders: _*)

    "return all notifications" in new Setup {
      val result = await(controller.getAll().apply(validRequest))

      status(result) shouldBe OK

      val expectedXml = s"<notifications><notification>/notification/$notificationId1</notification><notification>/notification/$notificationId2</notification></notifications>"
      bodyOf(result) shouldBe expectedXml
    }
  }

}
