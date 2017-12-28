/*
 * Copyright 2017 HM Revenue & Customs
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

import akka.stream.Materializer
import play.api.Configuration
import play.api.http.Status._
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.libs.json.Json
import play.api.test.FakeRequest
import uk.gov.hmrc.apinotificationpull.config.AppContext
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import views.txt

class DefinitionControllerSpec extends UnitSpec with WithFakeApplication {

  private implicit val materializer: Materializer = fakeApplication.materializer

  private val apiScope = "scope"
  private val apiContext = "context"
  private val appContext = new AppContext(Configuration("api.definition.api-scope" -> apiScope, "api.context" -> apiContext))
  private val controller = new DefinitionController(appContext)

  "DocumentationController.definition" should {
    lazy val result = getDefinition(controller)

    "return OK status" in {
      status(result) shouldBe OK
    }

    "have a JSON content type" in {
      result.header.headers should contain (CONTENT_TYPE -> "application/json;charset=utf-8")
    }

    "return definition in the body" in {
      jsonBodyOf(result) shouldBe Json.parse(txt.definition(apiScope, apiContext).toString())
    }
  }

  "DocumentationController.conf" should {
    lazy val result = getConf(controller)

    "return OK status" in {
      status(result) shouldBe OK
    }

    "return application.raml in the body" in {
      bodyOf(result) shouldBe txt.application(apiContext).toString()
    }
  }

  private def getDefinition(controller: DefinitionController) = {
    await(controller.get().apply(FakeRequest("GET", "/api/definition")))
  }

  private def getConf(controller: DefinitionController) = {
    await(controller.conf("1.0","application.raml").apply(FakeRequest("GET", "/api/conf/1.0/application.raml")))
  }
}