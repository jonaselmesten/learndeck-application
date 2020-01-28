package database;

import java.sql.Date;
import java.util.Arrays;

/**<h1>Query result</h1>
 *
 * This class contains nested static classes that can hold different data returned from some of the methods in the class "DatabaseUtil".<br/>
 * <br />
 * Classes that can be instantiated:<br/>
 * TeacherCourse<br/>
 * StudentInfo<br/>
 * StudentCourseInfo<br/>
 * CardReview<br/>
 * CourseStats<br/>
 * @author Jonas Elmesten
 */
public class QueryResult {

    private QueryResult() {};

    public static class TeacherCourse {

        private final String COURSE_NAME;
        private final int COURSE_ID;

        public TeacherCourse(String courseName, int courseId) {
            this.COURSE_NAME = courseName;
            this.COURSE_ID = courseId;
        }

        public String getCourseName() {
            return COURSE_NAME;
        }

        public int getCourseId() {
            return COURSE_ID;
        }
    }

    public static class StudentInfo {

        private final int studentId;
        private final String firstName;
        private final String lastName;
        private final Date birthDate;

        public StudentInfo(int studentId, String firstName, String lastName, Date birthDate) {
            this.studentId = studentId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthDate = birthDate;
        }

        public Date getBirthDate() {
            return birthDate;
        }

        public String getLastName() {
            return lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public int getStudentId() {
            return studentId;
        }
    }

    public static class StudentCourseInfo {

        private final String courseName;
        private final int totalCards;
        private final int dueCards;
        private final int newCards;

        public StudentCourseInfo(String courseName, int totalCards, int dueCards, int newCards) {
            this.courseName = courseName;
            this.totalCards = totalCards;
            this.dueCards = dueCards;
            this.newCards = newCards;
        }

        public String getCourseName() {
            return courseName;
        }

        public int getTotalCards() {
            return totalCards;
        }

        public int getDueCards() {
            return dueCards;
        }

        public int getNewCards() {
            return newCards;
        }
    }

    public static class CardReview {

        private final int cardId;
        private final int courseId;
        private final int studentId;
        private final String nextReview;
        private final int[] buttonValues;

        public CardReview(int cardId, int courseId, int studentId, String nextReview, int[] buttonValues) {
            this.cardId = cardId;
            this.courseId = courseId;
            this.studentId = studentId;
            this.nextReview = nextReview;
            this.buttonValues = buttonValues;
        }

        public int[] getReviewStats() {
            return Arrays.copyOf(buttonValues, 4);
        }

        public String getNextReview() {
            return nextReview;
        }

        public int getStudentId() {
            return studentId;
        }

        public int getCourseId() {
            return courseId;
        }

        public int getCardId() {
            return cardId;
        }
    }

    public static class CourseStats {

        private final int[] cardButtonStat;
        private final double[] difficultyStat;


        public CourseStats(int[] cardButtonStat, double[] difficultyStat) {
            this.cardButtonStat = cardButtonStat;
            this.difficultyStat = difficultyStat;
        }

        public int getHardCount() {
            return cardButtonStat[0];
        }

        public int getMediumCount() {
            return cardButtonStat[1];
        }

        public int getEasyCount() {
            return cardButtonStat[2];
        }

        public int getVeryEasyCount() {
            return cardButtonStat[3];
        }

        public double getAvgDifficulty() {
            return difficultyStat[0];
        }

        public double getEasiestDifficulty() {
            return difficultyStat[1];
        }

        public double getHardestDifficulty() {
            return difficultyStat[2];
        }

        public int getCardViewCount() {
            return cardButtonStat[0] + cardButtonStat[1] + cardButtonStat[2] + cardButtonStat[3];
        }
    }

}
