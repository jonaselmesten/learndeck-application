package menu.user.teacher;

import deck.Deck;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Window;
import menu.ChangeableWindow;
import menu.UserController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class StatController implements UserController, ChangeableWindow {

    @FXML private PieChart pieChart;
    @FXML private Spinner<Double> generalSpinner;
    @FXML private Text hardestCard;
    @FXML private Text easiestCard;
    @FXML private Text courseName;
    @FXML private Text cardViewCount;
    @FXML private Text studentCount;

    private final Logger log = LogManager.getLogger(StatController.class);
    private final SpinnerValueFactory.DoubleSpinnerValueFactory factoryGeneral = new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1,2);

    private Deck deck;
    private double generalDifficulty;

    @Override
    public void initData(Object ...obj) {

        deck = (Deck) obj[0];
        studentCount.setText(String.valueOf(obj[1]));
        courseName.setText(deck.getCourseName());
        factoryGeneral.setAmountToStepBy(0.01);
        generalSpinner.setValueFactory(factoryGeneral);
        getWindow().setOnCloseRequest(event -> windowClosing());

        setGUI();
    }

    private void windowClosing() {

        if(factoryGeneral.getValue() != generalDifficulty) {

            try {
                DatabaseUtil.updateGeneralDifficulty(deck.getCourseId(), factoryGeneral.getValue() - generalDifficulty);
            }catch(SQLException e) {
                log.debug("SQLException occurred while trying to update the general difficulty of deck:" + deck.getCourseName() + " Id:" + deck.getCourseId(), e);
                e.printStackTrace();
            }
        }
    }

    private void setGUI() {

        //No data to initialize if the course has no students.
        if(Integer.parseInt(studentCount.getText()) == 0) {

            courseName.setText(courseName.getText().concat(" - No students"));
            generalSpinner.setDisable(true);
            pieChart.setVisible(false);
            return;
        }

        try {
            QueryResult.CourseStats stats = DatabaseUtil.getCourseStats(deck.getCourseId());

            initializePieChart(stats.getHardCount(),stats.getMediumCount(), stats.getEasyCount(), stats.getVeryEasyCount());

            factoryGeneral.setValue(stats.getAvgDifficulty());
            generalDifficulty = factoryGeneral.getValue();

            cardViewCount.setText(String.valueOf(stats.getCardViewCount()));
            easiestCard.setText(String.valueOf(String.format("%.2f", stats.getEasiestDifficulty())));
            hardestCard.setText(String.valueOf(String.format("%.2f", stats.getHardestDifficulty())));


            if(Integer.parseInt(cardViewCount.getText()) == 0) {

                courseName.setText(courseName.getText().concat(" - Not used yet"));
                generalSpinner.setDisable(true);
                pieChart.setVisible(false);
            }

        }catch(SQLException e) {

            log.debug("SQLException while trying to get course stats of deck:" + deck.getCourseName() + " Id:" + deck.getCourseId(), e);
            e.printStackTrace();
        }
    }

    private void initializePieChart(int hardCount, int mediumCount, int easyCount, int veryEasyCount) {

        PieChart.Data hardSlice = new PieChart.Data("Hard", hardCount);
        PieChart.Data mediumSlice = new PieChart.Data("Medium", mediumCount);
        PieChart.Data easySlice = new PieChart.Data("Easy", easyCount);
        PieChart.Data veasySlice = new PieChart.Data("Very easy", veryEasyCount);

        pieChart.getData().add(hardSlice);
        pieChart.getData().add(mediumSlice);
        pieChart.getData().add(easySlice);
        pieChart.getData().add(veasySlice);
    }

    @Override
    public Window getWindow() {
        return hardestCard.getScene().getWindow();
    }
}
