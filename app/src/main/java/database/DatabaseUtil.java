package database;

import deck.Deck;
import deck.DeckUtil;
import deck.card.Card;
import javafx.scene.control.TextField;
import menu.user.User;
import menu.user.UserType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**<h1>Database utilities</h1>
 *
 * This class contains static functions to execute different SQL queries on the database.
 * <p>It also has one static class "ReviewUpdateBatch" for batch execution.</p>
 * @author Jonas Elmesten
 */
public class DatabaseUtil {

    private final static Logger logger = LogManager.getLogger(DatabaseUtil.class);

    private static final String USER_LOG_IN = "{CALL User_Login (?,?,?,?)}";
    private static final String ADD_NEW_COURSE = "{CALL Course_Add (?,?,?) }";
    private static final String ADD_NEW_CARD = "{CALL Card_Add (?,?) }";
    private static final String ADD_STUDENT_TO_COURSE = "{CALL Course_Add_Student (?,?) }";
    private static final String CREATE_STUDENT_USER = "{CALL User_Student_Add (?,?,?,?,?,?) }";
    private static final String CREATE_USER_NAME = "{CALL User_Name_Create (?,?) }";
    private static final String UPDATE_CARD_DIFFICULTY = "{CALL Card_Difficulty_Update (?,?) }";
    private static final String UPDATE_CARD_DIFFICULTY_GENERAL = "{CALL Card_Difficulty_Update_General (?,?) }";

    private DatabaseUtil() {}

    public static Optional<User> tryUserLogIn(String user_name, String password) throws SQLException {

        logger.debug("Method call:tryUserLogIn");
        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                CallableStatement statement = connection.prepareCall(USER_LOG_IN)
            ) {

            statement.setString(1, user_name);
            statement.setString(2, password);

            statement.registerOutParameter(3, Types.INTEGER);
            statement.registerOutParameter(4, Types.VARCHAR);

            statement.execute();

            int userId = statement.getInt(3);
            String userType = statement.getString(4);

            if(Objects.isNull(userType))
                return Optional.empty();
            else
                return Optional.of(new User(userId, UserType.stringToUserType(userType)));
        }
    }


    /**Removes a certain card from the database. This will cause all the related card reviews to be removed too.
     * So all the review data will be lost.
     * @exception SQLException On connection error.
     */
    public static void removeCard(int cardId, int courseId) throws SQLException {

        logger.debug("Method call:removeCard CardId:" + cardId + " CourseId:" + courseId);

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM card " +
                                "WHERE card_id = ? AND course_id = ? ")
                ) {

            statement.setInt(1, cardId);
            statement.setInt(2, courseId);

            statement.executeUpdate();
        }
    }


    /**Removes student from course.
     * The students review data will be removed.
     * @exception SQLException On connection error.
     */
    public static void removeStudentFromCourse(int studentId, int courseId) throws SQLException {

        logger.debug("Method call:removeStudentFromCourse StudentId:" + studentId + " CourseId:" + courseId);

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM student_has_course " +
                                "WHERE student_id = ? AND course_id = ? ")
            ) {

            statement.setInt(1, studentId);
            statement.setInt(2, courseId);

            statement.executeUpdate();
        }
    }


    /**Removes a course.
     * All cards and the students' review data will be removed.
     * @exception SQLException On connection error.
     */
    public static void removeCourse(int courseId) throws SQLException {

        logger.debug("Method call:removeCourse CourseId:" + courseId);

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM course" +
                                " WHERE course_id = ? ")
            ) {

            statement.setInt(1, courseId);
            statement.executeUpdate();
        }
    }


    /**@exception SQLException On connection error.*/

    public static void addStudentUser(String firstName, String lastName, String userName, String password, String birthDate, String eMail) throws SQLException {

        logger.debug("Method call:addStudentUser UserName:" + userName);

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                CallableStatement statement = connection.prepareCall(CREATE_STUDENT_USER)
            ) {

            statement.setString(1, firstName.toLowerCase());
            statement.setString(2, lastName.toLowerCase());
            statement.setString(3, userName.toLowerCase());
            statement.setString(4, password);
            statement.setString(5, birthDate);
            statement.setString(6, eMail);

            statement.executeUpdate();
        }
    }


    /**Adds a student to a course. This will give the student a new card review for every card in this deck.
     * @exception SQLException On connection error.
     */
    public static void addStudentToCourse(int studentId, int courseId) throws SQLException {

        logger.debug("Method call:addStudentToCourse StudentId:" + studentId + " CourseId:" + courseId);

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                CallableStatement statement = connection.prepareCall(ADD_STUDENT_TO_COURSE)
            ) {

            statement.setInt(1, studentId);
            statement.setInt(2, courseId);

            statement.execute();
        }
    }


    /**@exception SQLException On connection error.*/

    public static int addNewCourse(String courseName, int teacherId) throws SQLException {

        logger.debug("Method call:addNewCourse CourseName:" + courseName + " TeacherId:" + teacherId);

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                CallableStatement statement = connection.prepareCall(ADD_NEW_COURSE)
            ) {

            statement.setString(1, courseName);
            statement.setInt(2, teacherId);
            statement.registerOutParameter(3, Types.INTEGER);
            statement.execute();

            return statement.getInt(3);
        }
    }


    /**@exception SQLException On connection error.*/

    public static int addNewCard(int courseId) throws SQLException {

        logger.debug("Method call:addNewCard CourseId:" + courseId);

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                CallableStatement statement = connection.prepareCall(ADD_NEW_CARD)
            ) {

            statement.setInt(1, courseId);
            statement.registerOutParameter(2, Types.INTEGER);
            statement.execute();

            return statement.getInt(2);
        }
    }


    /**Returns the amount of cards that a certain course has.
     * @return int Cards
     * @exception SQLException On connection error.
     */
    public static int getCardAmount(int courseId) throws SQLException {

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();

                PreparedStatement statement = connection.prepareStatement(
                        "SELECT COUNT(*) FROM card " +
                                "INNER JOIN course ON card.course_id = course.course_id " +
                                "WHERE card.course_id = ? ")
            ) {
            statement.setInt(1, courseId);

            try(ResultSet resultSet = statement.executeQuery()) {

                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }


    /**Returns the number of students that are studying a certain course.
     * @return int Students
     * @exception SQLException On connection error.
     */
    public static int getStudentAmount(int courseId) throws SQLException {

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT COUNT(DISTINCT student_id) FROM student_has_course " +
                                "WHERE course_id = ? ")
            ) {

            statement.setInt(1, courseId);

            try(ResultSet resultSet = statement.executeQuery()) {

                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }


    /**Returns the number of students that are studying a certain course.
     * @return int Students
     * @exception SQLException On connection error.
     */
    public static String getValidUserName(String userName) throws SQLException {

        logger.info("Method call:getValidUserName UserName:" + userName);

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                CallableStatement statement = connection.prepareCall(CREATE_USER_NAME)
            ) {

            statement.setString(1, userName);
            statement.registerOutParameter(2, Types.VARCHAR);
            statement.executeQuery();

            return statement.getString(2);
        }
    }


    /**This method will give you all the card review data of a student for a certain course.
     * @return List with QueryResult.CardReview for every card in the deck.
     * @exception SQLException On connection error.
     */
    public static List<QueryResult.CardReview> getReviews(int studentId, int courseId) throws SQLException {

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM card_review " +
                                "WHERE student_id = ? AND course_id = ? " +
                                "ORDER BY card_id ")
            ) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);

            List<QueryResult.CardReview> list = new ArrayList<>();

            try(ResultSet rs = statement.executeQuery()) {

                while(rs.next()) {

                    int[] buttonValues = {rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8)};
                    list.add(new QueryResult.CardReview(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getString(4), buttonValues));
                }
            }
            return list;
        }
    }


    /**Get all the review data of a certain course.
     * <p>
     * -How many times the hard button has been pushed<br/>
     * -How many times the medium button has been pushed<br/>
     * -How many times the easy button has been pushed<br/>
     * -How many times the very easy button has been pushed<br/>
     * -Average general difficulty<br/>
     * -Easiest card difficulty<br/>
     * -Hardest card difficulty<br/>
     * @return QueryResult.CourseStats
     * @exception SQLException On connection error.
     */
    public static QueryResult.CourseStats getCourseStats(int courseId) throws SQLException {

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT " +
                                "   SUM(hard_pushed), " +
                                "   SUM(medium_pushed), " +
                                "   SUM(easy_pushed), " +
                                "   SUM(very_easy_pushed), " +
                                "   AVG(general_difficulty), " +
                                "   MIN(general_difficulty), " +
                                "   MAX(general_difficulty) FROM card_review CR " +
                                "       INNER JOIN card C " +
                                "           ON CR.course_id = C.course_id AND CR.card_id = C.card_id " +
                                "   WHERE CR.course_id = ? ")
        ) {
            statement.setInt(1, courseId);

            int[] cardButtonStat = new int[4];
            double[] difficultyStat = new double[3];

            try(ResultSet rs = statement.executeQuery()) {

                rs.next();

                cardButtonStat[0] = rs.getInt(1);
                cardButtonStat[1] = rs.getInt(2);
                cardButtonStat[2] = rs.getInt(3);
                cardButtonStat[3] = rs.getInt(4);
                difficultyStat[0] = rs.getDouble(5);
                difficultyStat[1] = rs.getDouble(6);
                difficultyStat[2] = rs.getDouble(7);
            }

            return new QueryResult.CourseStats(cardButtonStat, difficultyStat);
        }
    }


    /**Gives you a list with information about each student studying a certain course.
     * <p>
     * -User ID<br/>
     * -First name<br/>
     * -Last name<br/>
     * -Birth date<br/>
     * </p>
     * @return List with QueryResult.StudentInfo for every student studying this course.
     * @exception SQLException On connection error.
     */
    public static List<QueryResult.StudentInfo> getCourseStudents(int courseId) throws SQLException {

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT DISTINCT user_id, first_name, last_name, birth_date FROM student_has_course SH " +
                                "INNER JOIN student S ON SH.student_id = S.student_id " +
                                "INNER JOIN user U ON S.student_id = U.user_id " +
                                "WHERE course_id = ? " +
                                "ORDER BY last_name")
            ) {
            statement.setInt(1, courseId);

            List<QueryResult.StudentInfo> list = new ArrayList<>();

            try(ResultSet rs = statement.executeQuery()) {

                while(rs.next()) {
                    list.add(new QueryResult.StudentInfo(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4)));
                }
            }

            return list;
        }
    }


    /**Gives you information about each course that the student is currently studying.
     * <p>
     *  Information about each course:<br/>
     * -Name of course<br/>
     * -Number of cards<br/>
     * -Number of cards that's due for today<br/>
     * -Number of new cards that's yet to be reviewed<br/>
     * </p>
     * @return List with QueryResult.StudentCourseInfo for every course this students it studying
     * @exception SQLException On connection error.
     */
    public static List<QueryResult.StudentCourseInfo> getStudentCourseInfo(int studentId) throws SQLException {

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT " +
                                "course_name AS COURSE, " +

                                "made_by_teacher AS TEACHER, " +

                                "COUNT(card_id) AS CARDS, " +

                                "(SELECT COUNT(DISTINCT card_id) FROM card_review CR " +
                                "   WHERE (CURDATE() >= next_review_date) AND CR.course_id = CO.course_Id AND CR.student_id = SHC.student_id) " +
                                "AS DUE_CARDS, " +

                                "(SELECT COUNT(DISTINCT card_id) FROM card_review CR " +
                                "   WHERE next_review_date = '1111-11-11' AND CR.course_id = CO.course_id AND CR.student_id = SHC.student_id) " +
                                "AS NEW_CARDS, " +

                                "CO.modification_date AS MOD_DATE " +

                                "FROM course CO " +
                                "   INNER JOIN card C " +
                                "       ON CO.course_id = C.course_id " +
                                "   INNER JOIN student_has_course SHC" +
                                "        ON SHC.course_id = CO.course_id " +

                                "WHERE SHC.student_id = ? GROUP BY CO.course_name")
        ) {
            statement.setInt(1, studentId);
            List<QueryResult.StudentCourseInfo> list = new ArrayList<>();

            try(ResultSet rs = statement.executeQuery()) {

                while(rs.next()) {
                    list.add(new QueryResult.StudentCourseInfo(
                            rs.getString(1),
                            rs.getInt(2),
                            rs.getInt(3),
                            rs.getInt(4),
                            rs.getInt(5),
                            rs.getTimestamp(6)));
                }
            }
            return list;
        }
    }


    /**Search for students by their first and last name.
     * @param searchWord Word/letter to be searched in each students first and last name.
     * @return List with QueryResult.StudentInfo for all students that matches the search word.
     * @exception SQLException On connection error.
     */
    public static List<QueryResult.StudentInfo> searchStudent(String searchWord) throws SQLException {

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT user_id, first_name, last_name, birth_date FROM user U " +
                                "INNER JOIN student S ON U.user_id =  S.student_id " +
                                "WHERE first_name LIKE ? OR last_name LIKE ?")
        ) {
            searchWord = "%" + searchWord.trim() + "%";
            statement.setString(1, searchWord);
            statement.setString(2, searchWord);

            List<QueryResult.StudentInfo> list = new ArrayList<>();

            try(ResultSet rs = statement.executeQuery()) {

                while(rs.next()) {
                    list.add(new QueryResult.StudentInfo(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4)));
                }
            }
            return list;
        }
    }


    /**Get information about all the courses that a certain teacher manages.
     * @return List with QueryResult.TeacherCourse for all courses created by this teacher.
     * @exception SQLException On connection error.
     */
    public static List<QueryResult.TeacherCourse> getTeacherCourses(int teacherId) throws SQLException {

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();

                PreparedStatement statement = connection.prepareStatement(
                        "SELECT course_name, course_id FROM course " +
                                "WHERE made_by_teacher = ? " +
                                "ORDER BY course_name ")
        ) {

            statement.setInt(1, teacherId);
            List<QueryResult.TeacherCourse> list = new ArrayList<>();

            try(ResultSet rs = statement.executeQuery()) {

                while(rs.next()) {
                    list.add(new QueryResult.TeacherCourse(rs.getString(1), rs.getInt(2)));
                }
            }

            return list;
        }
    }


    /**Update the general difficulty for all cards in a certain course.
     * This will affect the difficulty for all the students studying the course.
     * The difficulty will never go over 2.0 or under 0.1.
     * </p>
     * @param difficulty How much to add/remove from the difficulty
     * @exception SQLException On connection error.
     */
    public static void updateGeneralDifficulty(int courseId, Double difficulty) throws SQLException {

        logger.debug("Method call:updateGeneralDifficulty CourseId:" + courseId + " Difficulty:" + difficulty);

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                CallableStatement statement = connection.prepareCall(UPDATE_CARD_DIFFICULTY_GENERAL)
        ) {
            statement.setInt(1,courseId);
            statement.setDouble(2,difficulty);

            statement.executeUpdate();
        }
    }


    /**Update a courses' modification date. The date and time will be the instant this method is called.
     * @param deck Deck to update.
     * @throws SQLException On connection error.
     */
    public static void updateCourseModificationDate(Deck deck) throws SQLException {

        System.out.println("UPPDATEINT TIME DB" );

        try(
                Connection connection = ConnectionPool.getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE course " +
                                "SET modification_date = ? " +
                                "WHERE course_id = ? "

        )) {

            statement.setTimestamp(1, Timestamp.from(Instant.now()));
            statement.setInt(2, deck.getCourseId());

            statement.executeUpdate();
        }
    }


    /** This class is used to execute review- and card difficulty update queries in batches.
     * <p>Instantiate it to add queries to be executed in a batch.
     * This class implements runnable, so you can execute multiple batches concurrently.</p>
     * */
    public static class ReviewUpdateBatch implements Runnable {

        private final int studentId;
        private final int courseId;
        private final List<Card> list = new ArrayList<>();
        private static final String UPDATE_REVIEW =
                "UPDATE card_review " +
                        "SET next_review_date = ?, " +
                        "hard_pushed = ?, " +
                        "medium_pushed = ?, " +
                        "easy_pushed = ?, " +
                        "very_easy_pushed = ? " +
                        "WHERE student_id = ? AND course_id = ? AND card_id = ? ";


        public ReviewUpdateBatch(int studentId, int courseId) {

            logger.debug("Object created:ReviewUpdateBatch StudentId:" + studentId + " CourseId:" + courseId);

            this.studentId = studentId;
            this.courseId = courseId;
        }

        public void addReviewUpdate(Card card) {
            list.add(card);
        }

        private void executeUpdateBatch() {

            logger.debug("Executing update batch:");

            try(
                    Connection connection = ConnectionPool.getDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement(UPDATE_REVIEW);
                    CallableStatement callableStatement = connection.prepareCall(UPDATE_CARD_DIFFICULTY)
            )  {

                for(Card card : list) {

                    logger.debug("- CardId:" + card.getCardId());

                    //Update next review
                    int[] stats = card.getCardReviewStats();
                    statement.setString(1, card.getNextReview().toString());
                    statement.setInt(2, stats[0]);
                    statement.setInt(3, stats[1]);
                    statement.setInt(4, stats[2]);
                    statement.setInt(5, stats[3]);
                    statement.setInt(6, studentId);
                    statement.setInt(7, courseId);
                    statement.setInt(8, card.getCardId());

                    statement.addBatch();

                    //Update card general difficulty
                    callableStatement.setInt(1, courseId);
                    callableStatement.setInt(2, card.getCardId());

                    callableStatement.addBatch();
                }

                statement.executeBatch();
                callableStatement.executeBatch();

            }catch(SQLException e) {
                logger.error("Exception occurred while trying to execute an update batch." + e);
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            executeUpdateBatch();
        }
    }
}
