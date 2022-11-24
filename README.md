# WebviewIssueExample
This is a simple Android application to test an issue with a Webview.

## Steps to reproduce the issue:
1. Go to https://www.jiji.ng by typing the address in the edit text in the top of the screen and pressing "Go" button on the software keyboard;
2. Go to "Fashion" category
3. Try navigating to https://www.google.com (or any other site)

## Expected result
The next site is loaded

## Actual result
The loading of the next site gets stuck, user is unable to load any new webpage, recreating the webview (with "Recreate webview" button doesn't fix the issue). The Webview only starts working after restarting the application.

I assume this bug is caused by javascript changing the internal state of some persistent element of the Webview engine, as disabling DOM storage API makes the issue go away (but it breaks some webviews at the same time).
