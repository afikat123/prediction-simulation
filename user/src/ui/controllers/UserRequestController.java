package ui.controllers;

import client.UserClient;
import enginetoui.dto.basic.RequestDTO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tree.item.impl.WorldTreeItem;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserRequestController implements Initializable {
    @FXML
    private TextField amountRunField;

    @FXML
    private Button executeButton;

    @FXML
    private ComboBox simulationChooseMenu;

    @FXML
    private Button submitButton;

    @FXML
    private ComboBox terminationMenu;

    @FXML
    private TextField tickField;

    @FXML
    private TextField timeField;

    @FXML
    private TableView requestTable;

    @FXML
    private TableColumn<RequestDTO, String> nameColumn;

    @FXML
    private TableColumn<RequestDTO, String> statusColumn;

    private ObservableList<RequestDTO> TableData;

    private UserClient client;
    private List<WorldTreeItem> worldTreeItemList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        client = (UserClient) resources.getObject("client");
        TableData = (ObservableList<RequestDTO>) resources.getObject("requestList");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("simulationName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        requestTable.setItems(TableData);
        terminationMenu.getItems().clear();
        terminationMenu.getItems().add("Tick");
        terminationMenu.getItems().add("Time");
        terminationMenu.getItems().add("Tick and Time");
        terminationMenu.getItems().add("By user");
        simulationChooseMenu.getItems().clear();
        worldTreeItemList = (List<WorldTreeItem>) resources.getObject("treeList");
        for (WorldTreeItem treeItem : worldTreeItemList) {
            simulationChooseMenu.getItems().add(treeItem.getWorldName());
        }
    }


    public void setComboMenuItemTermination(ActionEvent event) {
        timeField.setDisable(false);
        timeField.setDisable(false);
        if (terminationMenu.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("Tick")) {
            timeField.setDisable(true);
            tickField.setDisable(false);
        } else if (terminationMenu.getSelectionModel().getSelectedItem().equals("Time")) {
            timeField.setDisable(false);
            tickField.setDisable(true);
        } else if (terminationMenu.getSelectionModel().getSelectedItem().equals("By user")) {
            timeField.setDisable(true);
            tickField.setDisable(true);
        } else {
            timeField.setDisable(false);
            tickField.setDisable(false);
        }
    }

    public void setSimulations(ActionEvent event) {

    }

    public void createNewRequest(ActionEvent event) throws IOException {
        String worldName = (String) simulationChooseMenu.getSelectionModel().getSelectedItem();
        int seconds, ticks, numOfRuns;
        try {
            numOfRuns = Integer.parseInt(amountRunField.getText());
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            numOfRuns = 0;
        }
        try {
            ticks = Integer.parseInt(tickField.getText());
        } catch (NumberFormatException e) {
            ticks = Integer.MAX_VALUE;
        }
        try {
            seconds = Integer.parseInt(timeField.getText());
        } catch (NumberFormatException e) {
            seconds = Integer.MAX_VALUE;
        }
        RequestDTO requestDTO = new RequestDTO(worldName, numOfRuns, ticks, seconds);
        client.newRequest(requestDTO);
        TableData.add(requestDTO);
        requestTable.setItems(TableData);
        amountRunField.setText("");
        timeField.setText("");
        tickField.setText("");
    }
}
