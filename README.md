HUSQUIZ -

HUSQUIZ is an interactive studying tool made specifically for University of Washington students and their respective courses. Students will be able to view and create flashcards specific to UW courses.

Features -
In this version the following features were implemented:
User has the ability to register and login. With proper validation & navigation

User has the ability to save login info for future use

User also has the ability to browse a sample set of flashcards that are stored using Heroku webservices

User has the ability to view the question on the flashcard

User has the ability to view the answer on the flashcard

User has the ability to sign out of the app and use the back navigation to access the previous screen

User Stories -

As a user, I can log in using my email, and access the app in its entirety (Priority: High)

As a user, I can access a set of flashcards from a UW course (Priority: High)

As a user, I can pick between the different UW classes to access the previous flashcards (Priority: High)

As a user, I can sign-out of the app at any point in my usage (Priority: Medium)

Shared Preferences - 
Using SharedPreferences users will be able to login into HUSQUIZ and save their login info for future use. By using the SharedPreferences API which stores a small collection of key-values in the device’s storage. 

Content Sharing -
When user’s login into the app, they will receive a corresponding email alerting them of their login. The email will be sent to the same email the user used to login.


Built With -
Android Studio
Java
Heroku
Versioning -

1.0	- Initial version

2.0	– HUSQUIZ 2.0

Authors -
Faiz Ahmed - Co-Creator - Team 8

Kerolos Adib - Co-Creator - Team 8

License -
This project is licensed under the MIT License

Acknowledgments -
Quizlet
 
Bugs/Deficiencies -
When a question is clicked on an error occurs. We’ve acknowledged it’s an error with the Flashcard fragment – This issue was resolved by creating a new class called FlashCardDetailActiviy which was the framework for the fragment, and called the fragment class from there

