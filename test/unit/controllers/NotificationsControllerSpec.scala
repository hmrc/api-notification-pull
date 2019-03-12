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

package unit.controllers

import java.util.UUID
import java.util.concurrent.TimeoutException

import akka.stream.Materializer
import org.mockito.ArgumentMatchers.{eq => meq, _}
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.mockito.MockitoSugar
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.http.MimeTypes
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.AnyContentAsEmpty
import play.api.mvc.Results.Ok
import play.api.test.FakeRequest
import uk.gov.hmrc.apinotificationpull.controllers.NotificationsController
import uk.gov.hmrc.apinotificationpull.model.{Notification, Notifications}
import uk.gov.hmrc.apinotificationpull.presenters.NotificationPresenter
import uk.gov.hmrc.apinotificationpull.services.ApiNotificationQueueService
import uk.gov.hmrc.apinotificationpull.util.XmlBuilder
import uk.gov.hmrc.customs.api.common.config.ServicesConfig
import uk.gov.hmrc.customs.api.common.logging.CdsLogger
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import unit.fakes.SuccessfulHeaderValidatorFake
import unit.util.StubNotificationLogger
import unit.util.RequestHeaders.{ACCEPT_HEADER, X_CLIENT_ID_HEADER}
import unit.util.XmlUtil.string2xml

import scala.concurrent.Future

class NotificationsControllerSpec extends UnitSpec with WithFakeApplication with MockitoSugar with BeforeAndAfterEach {

  private val mockApiNotificationQueueService = mock[ApiNotificationQueueService]
  private val notificationPresenter = mock[NotificationPresenter]
  private val mockXmlBuilder = mock[XmlBuilder]
  private val mockLogger = new StubNotificationLogger(new CdsLogger(mock[ServicesConfig]))

  private val errorXml = scala.xml.Utility.trim(
    <error_response>
      <code>UNKNOWN_ERROR</code>
      <errors>
        <error>
          <type>SERVICE_UNAVAILABLE</type>
          <description>An unexpected error occurred</description>
        </error>
      </errors>
    </error_response>
  )

  trait Setup {
    implicit val materializer: Materializer = fakeApplication.materializer

    val clientId = "client_id"

    val validHeaders = Seq(ACCEPT_HEADER, X_CLIENT_ID_HEADER)
    val headerValidator = new SuccessfulHeaderValidatorFake(new StubNotificationLogger(new CdsLogger(mock[ServicesConfig])))

    val controller = new NotificationsController(mockApiNotificationQueueService, headerValidator, notificationPresenter, mockXmlBuilder, mockLogger)
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(notificationPresenter, mockApiNotificationQueueService, mockXmlBuilder)
  }

  "delete notification by id" should {
    trait SetupDeleteNotification extends Setup {
      val notificationId: String = UUID.randomUUID().toString
      val validRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("DELETE", s"/$notificationId").
        withHeaders(ACCEPT_HEADER, X_CLIENT_ID_HEADER)

      val presentedNotification = Ok("presented notification")
      val headers = Map(CONTENT_TYPE -> MimeTypes.XML)
      val notification = Notification(notificationId, headers, "notification")
    }

    "return the presented notification" in new SetupDeleteNotification {
      when(mockApiNotificationQueueService.getAndRemoveNotification(meq(notificationId))(any[HeaderCarrier]))
        .thenReturn(Some(notification))

      when(notificationPresenter.present(Some(notification))).thenReturn(presentedNotification)

      val result = await(controller.delete(notificationId).apply(validRequest))

      result shouldBe presentedNotification
    }

    "fail if ApiNotificationQueueService failed" in new SetupDeleteNotification {
      when(mockApiNotificationQueueService.getAndRemoveNotification(anyString)(any(classOf[HeaderCarrier])))
        .thenReturn(Future.failed(new TimeoutException()))

      val result = await(controller.delete(notificationId).apply(validRequest))

      status(result) shouldBe INTERNAL_SERVER_ERROR
      string2xml(bodyOf(result)) shouldBe errorXml
    }
  }

  "get all notifications" should {

    trait SetupGetAllNotifications extends Setup {

      protected val notifications = Notifications(List(s"/notifications/1"))

      val validRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/").withHeaders(validHeaders: _*)
    }

    "return all notifications" in new SetupGetAllNotifications {
      val xmlNotifications = <resource href="/notifications/"><link rel="notification" href="/notifications/1"/></resource>

      when(mockApiNotificationQueueService.getNotifications()(any(classOf[HeaderCarrier])))
        .thenReturn(Future.successful(notifications))

      when(mockXmlBuilder.toXml(notifications)).thenReturn(xmlNotifications)

      val result = await(controller.getAll().apply(validRequest))

      status(result) shouldBe OK

      string2xml(bodyOf(result)) shouldBe xmlNotifications
    }

    "fail if ApiNotificationQueueService failed" in new SetupGetAllNotifications {
      when(mockApiNotificationQueueService.getNotifications()(any(classOf[HeaderCarrier])))
        .thenReturn(Future.failed(new TimeoutException()))

      val result = await(controller.getAll().apply(validRequest))

      status(result) shouldBe INTERNAL_SERVER_ERROR

      string2xml(bodyOf(result)) shouldBe errorXml
    }
  }

}
