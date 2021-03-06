# Pull Notifications API

# Introduction
This API allows third party developers to collect notifications.

---

## Endpoints

### GET `/conversationId/{conversationId}`

Get a list of notifications by conversation ID

Required Headers:
  - `X-Client-ID`
  - `Accept`

```
curl -v -X GET "http://localhost:9649/conversationId/dc7cb9ef-75db-4adc-b2cb-e2b86bdbc478" \
  -H "X-Client-ID: 580e3940-fb35-4421-b7c7-949f64a97870" \
  -H "Accept: application/vnd.hmrc.1.0+xml"
```

#### Responses
##### Success
```
200 OK
 <resource href="/notifications/conversationId/dc7cb9ef-75db-4adc-b2cb-e2b86bdbc478/">
    <link rel="self" href="/notifications/conversationId/dc7cb9ef-75db-4adc-b2cb-e2b86bdbc478/"/>
    <link rel="notification" href="/notifications/unpulled/f1065d30-7854-44e8-a3bb-53986ad98e43"/>
    <link rel="notification" href="/notifications/pulled/7dbada32-3f0f-4784-8290-6fc86cff3528"/>
 </resource>
```


### GET `/conversationId/{conversationId}/unpulled`

Get a list of unpulled notifications by conversation ID

Required Headers:
  - `X-Client-ID`
  - `Accept`

```
curl -v -X GET "http://localhost:9649/conversationId/dc7cb9ef-75db-4adc-b2cb-e2b86bdbc478/unpulled" \
  -H "X-Client-ID: 580e3940-fb35-4421-b7c7-949f64a97870" \
  -H "Accept: application/vnd.hmrc.1.0+xml"
```

#### Responses
##### Success
```
200 OK
 <resource href="/notifications/conversationId/dc7cb9ef-75db-4adc-b2cb-e2b86bdbc478/unpulled/">
    <link rel="self" href="/notifications/conversationId/dc7cb9ef-75db-4adc-b2cb-e2b86bdbc478/unpulled/"/>
    <link rel="notification" href="/notifications/unpulled/f1065d30-7854-44e8-a3bb-53986ad98e43"/>
    <link rel="notification" href="/notifications/unpulled/7dbada32-3f0f-4784-8290-6fc86cff3528"/>
 </resource>
```

### GET `/conversationId/{conversationId}/pulled`

Get a list of pulled notifications by conversation ID

Required Headers:
  - `X-Client-ID`
  - `Accept`

```
curl -v -X GET "http://localhost:9649/conversationId/dc7cb9ef-75db-4adc-b2cb-e2b86bdbc478/pulled" \
  -H "X-Client-ID: 580e3940-fb35-4421-b7c7-949f64a97870" \
  -H "Accept: application/vnd.hmrc.1.0+xml"
```

#### Responses
##### Success
```
200 OK
 <resource href="/notifications/conversationId/dc7cb9ef-75db-4adc-b2cb-e2b86bdbc478/pulled/">
    <link rel="self" href="/notifications/conversationId/dc7cb9ef-75db-4adc-b2cb-e2b86bdbc478/pulled/"/>
    <link rel="notification" href="/notifications/pulled/f1065d30-7854-44e8-a3bb-53986ad98e43"/>
    <link rel="notification" href="/notifications/pulled/7dbada32-3f0f-4784-8290-6fc86cff3528"/>
 </resource>
```

### GET `/unpulled`

Get a list of unpulled notifications

Required Headers:
  - `X-Client-ID`
  - `Accept`

```
curl -v -X GET "http://localhost:9649/unpulled" \
  -H "X-Client-ID: 580e3940-fb35-4421-b7c7-949f64a97870" \
  -H "Accept: application/vnd.hmrc.1.0+xml"
```

#### Responses
##### Success
```
200 OK
 <resource href="/notifications/unpulled/">
    <link rel="self" href="/notifications/unpulled/"/>
    <link rel="notification" href="/notifications/unpulled/7ab99957-b138-4f09-888e-ab4e8107bbe0"/>
 </resource>
```

### GET `/unpulled/{notificationId}`

Get an unpulled notification

Required Headers:
  - `X-Client-ID`
  - `Accept`

```
curl -v -X GET "http://localhost:9649/unpulled/{notificationId}" \
  -H "X-Client-ID: 580e3940-fb35-4421-b7c7-949f64a97870" \
  -H "Accept: application/vnd.hmrc.1.0+xml"
```

#### Responses
##### Success
```
200 OK
 Notification
```

##### Bad Request
 ```
400 Bad Request
    <errorResponse>
        <code>BAD_REQUEST</code>
        <message>Notification has been pulled</message>
    </errorResponse>
```

##### Not Found
```
404 Not Found
    <errorResponse>
        <code>NOT_FOUND</code>
        <message>Resource was not found</message>
    </errorResponse>
```

### GET `/pulled`

Get a list of pulled notifications

Required Headers:
  - `X-Client-ID`
  - `Accept`

```
curl -v -X GET "http://localhost:9649/pulled" \
  -H "X-Client-ID: 580e3940-fb35-4421-b7c7-949f64a97870" \
  -H "Accept: application/vnd.hmrc.1.0+xml"
```

#### Responses
##### Success
```
200 OK
 <resource href="/notifications/pulled/">
    <link rel="self" href="/notifications/pulled/"/>
    <link rel="notification" href="/notifications/pulled/7ab99957-b138-4f09-888e-ab4e8107bbe0"/>
 </resource>
```

### GET `/pulled/{notificationId}`

Get a pulled notification

Required Headers:
  - `X-Client-ID`
  - `Accept`

```
curl -v -X GET "http://localhost:9649/pulled/{notificationId}" \
  -H "X-Client-ID: 580e3940-fb35-4421-b7c7-949f64a97870" \
  -H "Accept: application/vnd.hmrc.1.0+xml"
```

#### Responses
##### Success
```
200 OK

Notification 
```

##### Bad Request

```
400 Bad Request

    <errorResponse>
        <code>BAD_REQUEST</code>
        <message>Notification is unpulled</message>
    </errorResponse>
```

##### Not Found

```
404 Not Found

    <errorResponse>
        <code>NOT_FOUND</code>
        <message>Resource was not found</message>
    </errorResponse>
```

### DELETE `/{notificationId}`

Retrieves and deletes a notification from `api-notification-queue`

Required Headers:
  - `X-Client-ID`
  - `Accept`

```
curl -v -X DELETE "http://localhost:9649/{notificationId}" \
  -H "X-Client-ID: 580e3940-fb35-4421-b7c7-949f64a97870" \
  -H "Accept: application/vnd.hmrc.1.0+xml"
```

#### Responses

##### Success
```
200 OK

Notification
```

##### Not Found

`404 Not Found`

### GET `/`

Retrieves all notifications, for a specific client id, from `api-notification-queue`

Required Headers:
  - `X-Client-ID`
  - `Accept`

```
curl -v -X GET "http://localhost:9649/" \
  -H "X-Client-ID: 580e3940-fb35-4421-b7c7-949f64a97870" \
  -H "Accept: application/vnd.hmrc.1.0+xml"
```

#### Response
```
200 OK

<resource href="/notifications/">
   <link rel="self" href="/notifications/"/>
   <link rel="notification" href="/notifications/7ab99957-b138-4f09-888e-ab4e8107bbe0"/>
</resource>
```

---

### Tests
There are unit and component tests along with code coverage reports.
In order to run them, execute this command line:
```
./precheck.sh
```

---

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
