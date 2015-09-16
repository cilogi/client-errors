# Record client-side JavaScript errors

Record errors from JavaScript.

## Requirements

Java on App Engine.

## Get started

You need to set up something like this code on the client, to send errors up.

        function logError(details) {
            $.ajax({
                type: 'POST',
                url: '/jserror',
                data: JSON.stringify({
                    token: "{{token}}",
                    userAgent: navigator.userAgent,
                    details: details
                }),
                dataType: "json",
                contentType: 'application/json; charset=utf-8',
                xhrFields: {
                    withCredentials: true
                },
                success: function() {
                    console.log("error recorded");
                },
                error: function(jqXHR, status, error) {
                    console.log("FAIL to record error");
                }
            });
        }

        window.onerror = function (message, file, line) {
            logError(file + ':' + line + '\n\n' + message);
        };

It could no doubt be a lot smarter.