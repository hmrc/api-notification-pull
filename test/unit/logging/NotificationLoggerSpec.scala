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

package unit.logging

import org.mockito.ArgumentMatchers.any
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.apinotificationpull.logging.NotificationLogger
import uk.gov.hmrc.customs.api.common.logging.CdsLogger
import uk.gov.hmrc.play.test.UnitSpec
import unit.util.TestData.LoggingHeaders
import util.MockitoPassByNameHelper.PassByNameVerifier

class NotificationLoggerSpec extends UnitSpec with MockitoSugar {



  trait SetUp {
    val mockCdsLogger: CdsLogger = mock[CdsLogger]
    val logger = new NotificationLogger(mockCdsLogger)
  }

  "NotificationsLogger" should {
    "debug(s: => String, headers: => SeqOfHeader)" in new SetUp {
      logger.debug("msg", LoggingHeaders)

      PassByNameVerifier(mockCdsLogger, "debug")
        .withByNameParam("[clientId=client-id] msg\nheaders=List((X-Client-ID,client-id), (Accept,application/vnd.hmrc.1.0+xml))")
        .verify()
    }

    "info(s: => String, headers: => SeqOfHeader, e: => Throwable)" in new SetUp {
      logger.info("msg", LoggingHeaders, new Exception(""))

      PassByNameVerifier(mockCdsLogger, "info")
        .withByNameParam("[clientId=client-id] msg\nheaders=List((X-Client-ID,client-id), (Accept,application/vnd.hmrc.1.0+xml))")
        .withByNameParamMatcher(any[Throwable])
        .verify()
    }

    "info(s: => String, headers: => SeqOfHeader)" in new SetUp {
      logger.info("msg", LoggingHeaders)

      PassByNameVerifier(mockCdsLogger, "info")
        .withByNameParam("[clientId=client-id] msg\nheaders=List((X-Client-ID,client-id), (Accept,application/vnd.hmrc.1.0+xml))")
        .verify()
    }

    "warn(s: => String, headers: => SeqOfHeader)" in new SetUp {
      logger.warn("msg", LoggingHeaders)

      PassByNameVerifier(mockCdsLogger, "warn")
        .withByNameParam("[clientId=client-id] msg\nheaders=List((X-Client-ID,client-id), (Accept,application/vnd.hmrc.1.0+xml))")
        .verify()
    }

    "error(s: => String, headers: => SeqOfHeader)" in new SetUp {
      logger.error("msg", LoggingHeaders)

      PassByNameVerifier(mockCdsLogger, "error")
        .withByNameParam("[clientId=client-id] msg\nheaders=List((X-Client-ID,client-id), (Accept,application/vnd.hmrc.1.0+xml))")
        .verify()
    }

    "error(s: => String, headers: => SeqOfHeader, e: => Throwable)" in new SetUp {
      logger.error("msg", LoggingHeaders, new Exception(""))

      PassByNameVerifier(mockCdsLogger, "error")
        .withByNameParam("[clientId=client-id] msg\nheaders=List((X-Client-ID,client-id), (Accept,application/vnd.hmrc.1.0+xml))")
        .withByNameParamMatcher(any[Throwable])
        .verify()
    }
  }
}
