
cd path/to/your/Quiz_Forge
git init
git add .

echo "# Quiz Forge

## Project Description
Quiz Forge is a Java-based quiz application with dynamic difficulty adjustment, user authentication, and performance tracking.

## Features
- 🔐 User Authentication (Login/Register)
- 🎯 Dynamic Difficulty Adjustment (Easy → Medium → Hard)
- 📊 Performance Analytics
- 🏆 Leaderboard
- 🗂️ Category-based Questions (Java, Networking, Cybersecurity, SQL, General Knowledge)
- 📝 Admin CRUD Operations for Questions
- 📈 Score Tracking and History

## Technologies Used
- Java (JDK 8+)
- SQLite Database
- JDBC
- Swing GUI

## Setup Instructions
1. Clone the repository
2. Open the project in your IDE
3. Ensure SQLite driver is included
4. Run the main application

## Project Structure
- `model/` - Data model classes (Question, User, QuizResult, Category)
- `dao/` - Database operations (DBConnection, QuestionDAO, UserDAO, ResultDAO)
- `controller/` - Business logic (AdminController, QuizController, AuthController)
- `view/` - GUI components

## Author
Amina Sajid

## License
MIT License
" > README.md
git add README.md
git commit -m "Initial commit: Quiz Forge application
git branch -M main
git remote add origin https://github.com/amina-cyberr/Quiz_Forge.git

git push -u origin main
