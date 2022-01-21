/*
 * Copyright 2022 HM Revenue & Customs
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

import controllers.Assets
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.apinotificationpull.config.AppContext
import uk.gov.hmrc.apinotificationpull.controllers.ApiDocumentationController
import unit.util.MaterializerSupport
import util.UnitSpec
import views.txt

class DefinitionControllerSpec extends UnitSpec with MaterializerSupport with MockitoSugar {

  private val apiScope = "scope"
  private val apiContext = "context"
  private val appContext = new AppContext(Configuration("api.definition.scope" -> apiScope, "api.context" -> apiContext))
  private val controller = new ApiDocumentationController(mock[Assets], Helpers.stubControllerComponents(), appContext)

  "DefinitionController.definition" should {
    lazy val result = getDefinition(controller)

    "return OK status" in {
      status(result) shouldBe OK
    }

    "have a JSON content type" in {
      result.body.contentType shouldBe Some("application/json; charset=utf-8")
    }

    "return definition in the body" in {
      jsonBodyOf(result) shouldBe Json.parse(txt.definition(apiContext).toString())
    }
  }

  private def getDefinition(controller: ApiDocumentationController) = {
    controller.definition().apply(FakeRequest("GET", "/api/definition")).futureValue
  }

}
