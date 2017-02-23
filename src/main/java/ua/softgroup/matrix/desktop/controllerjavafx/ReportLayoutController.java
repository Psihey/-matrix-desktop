package ua.softgroup.matrix.desktop.controllerjavafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ua.softgroup.matrix.desktop.currentsessioninfo.CurrentSessionInfo;
import ua.softgroup.matrix.desktop.sessionmanagers.ReportServerSessionManager;
import ua.softgroup.matrix.server.desktop.model.ProjectModel;
import ua.softgroup.matrix.server.desktop.model.ReportModel;

import java.io.IOException;
import java.util.Set;

/**
 * @author Andrii Bei <sg.andriy2@gmail.com>
 */
public class ReportLayoutController {

    @FXML
    public TableView<ReportModel> tableViewReport;
    @FXML
    public TableColumn<ReportModel, Integer> reportTableColumnDate;
    @FXML
    public TableColumn<ReportModel, Long> reportTableColumnTime;
    @FXML
    public TableColumn<ReportModel, Boolean> reportTableColumnVerified;
    @FXML
    public TableColumn<ReportModel, String> reportTableColumnReport;
    @FXML
    public Button btnChangeReport;
    @FXML
    public Button btnCancelReport;
    @FXML
    public Label labelProjectName;
    @FXML
    public Label labelResponsible;
    @FXML
    public Label labelStartDate;
    @FXML
    public Label labelDeadlineDate;
    @FXML
    public TextArea taEditReport;
    private static final String DATE_COLUMN = "date";
    private static final String ID_COLUMN = "id";
    private static final String CHECKED_COLUMN = "checked";
    private static final String DESCRIPTION_COLUMN = "description";
    private ObservableList<ReportModel> reportData = FXCollections.observableArrayList();
    private ReportServerSessionManager reportServerSessionManager;
    private Long currentProjectId;
    private Set<ReportModel> report;
    private String reportText;
    private Long currentReportId;


    @FXML
    private void initialize() throws IOException {
        currentProjectId = CurrentSessionInfo.getProjectId();
        reportServerSessionManager = new ReportServerSessionManager();
        if (currentProjectId != null) {
            report = reportServerSessionManager.sendProjectDataAndGetReportById(currentProjectId);
        }
        initReport();
        setOtherProjectInfoInView(currentProjectId);
    }

    private void setOtherProjectInfoInView(Long id) {
        Set<ProjectModel> projectAll = CurrentSessionInfo.getUserActiveProjects();
        for (ProjectModel model :
                projectAll) {
            if (model.getId() == id) {
                labelResponsible.setText(model.getAuthorName());
                labelProjectName.setText(model.getTitle());
                taEditReport.setText(reportText);
                if(model.getEndDate()!=null&&model.getStartDate()!=null){
                    labelStartDate.setText(model.getStartDate().toString());
                    labelDeadlineDate.setText(model.getEndDate().toString());
                }
            }
        }
    }

    private void initReport() {
        reportTableColumnDate.setCellValueFactory(new PropertyValueFactory<>(DATE_COLUMN));
        reportTableColumnTime.setCellValueFactory(new PropertyValueFactory<>(ID_COLUMN));
        reportTableColumnVerified.setCellValueFactory(new PropertyValueFactory<>(CHECKED_COLUMN));
        reportTableColumnReport.setCellValueFactory(new PropertyValueFactory<>(DESCRIPTION_COLUMN));
        if (report != null && !report.isEmpty()) {
            for (ReportModel model :
                    report) {
                reportData.add(model);
                reportText=model.getDescription();
            }
            tableViewReport.setItems(reportData);
        }
    }

    private void checkVerifyReportAndSetButtonCondition(ReportModel reportModel) {
        if (reportModel.isChecked()){
            btnChangeReport.setDisable(true);
        }else btnChangeReport.setDisable(false);
    }

    public void CancelReportWindow(ActionEvent actionEvent) {
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void ChangeReportWindow(ActionEvent actionEvent) throws IOException {
        ReportModel reportModel=new ReportModel(CurrentSessionInfo.getTokenModel().getToken(), currentReportId,taEditReport.getText(),currentProjectId);
        System.out.println(reportModel.toString());
        reportModel.setTitle("dddsdsdsadasdsdsaad");
         reportServerSessionManager.changeReportOnServer(reportModel);
        ((Node) actionEvent.getSource()).getScene().getWindow().hide();
    }

    public void chooseReport(Event event) {
        if (tableViewReport.getSelectionModel().getSelectedItem()!=null){
            ReportModel selectReport=tableViewReport.getSelectionModel().getSelectedItem();
            checkVerifyReportAndSetButtonCondition(selectReport);
            currentReportId =selectReport.getId();
            taEditReport.setText(selectReport.getDescription());
        }

    }
}
