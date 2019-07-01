Use this API to "pull" business event notifications that CDS generates in response to requests you submitted using the CDS APIs.

If you submit a declaration to a CDS API, we accept the request with an HTTP status code 202. As your declaration is processed, we generate notifications.

If you provided a callback URL when you subscribed to the CDS API, we push the notifications to your callback URL.

If you did not provide a callback URL when you subscribed to the CDS API, we send the notifications to this pull queue.

Pull notifications remain queued for 14 days, after which we delete notifications from the queue automatically.

The Pull Notifications API works in 2 discrete modes. We recommend that your application uses the Pull Notifications API as described under Retrieve and persist pull notifications.

## Retrieve and persist pull notifications

Use these 2 endpoints to pull notifications you have not pulled yet:

* `GET /notifications/unpulled` returns a list of identifiers for notifications that you have not pulled previously 
* `GET /notifications/unpulled/{notificationId}` returns a notification you have not pulled previously

To retrieve notifications that you have pulled previously, use these 2 endpoints (which gives you a backup for previously pulled notifications):

* `GET /notifications/pulled` returns a list of identifiers for notifications that you have pulled previously 
* `GET /notifications/pulled/{notificationId}` returns a notification that you have pulled previously


## Retrieve and delete pull notifications (deprecated)

To delete notifications as you pull them, use these 2 endpoints:
    
* `GET /notifications`  returns a list of identifiers for notifications available for you to pull
* `DELETE /notifications/{Id}` pull and delete the requested notification

