# EventtusSimpleTwitterTask
A simple twitter client made for job's task purpose for Eventtus.
# Getting Started
# 1- Login 
Here used the Twitter and TwitterCore library to authorize app on end-user's device and start a new active session
# 2- Followers
After logging successfully into twitter, user is directed to the second screen "Followers" in where a list of his/her followers show up with the ability to refresh it and scroll down it infinitly.
# 3- User Timeline
After clicking on any handle in the list (Ex. @omar3alaa), user is directed to the third screen in where the clicked user's profile is showing (Profile image, Background image) and a list of first 10 tweets and on scrolling down more tweets are being showed.
# 4- CustomViewBinder
It is a custom view binder as stated in its name to help populate data in listitems
# 5- ObjectSerializer
It is a class used to serialize objects to make it a one whole string to save it in a sharedprefrence to be cached and used later in an offline mode.
# 6- OAuthentication
A class where an authentication url is being called with authorization header to get access token of application.
