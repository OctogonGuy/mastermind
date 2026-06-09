module tech.octopusdragon.mastermind {
    requires javafx.controls;
    requires javafx.fxml;


    opens tech.octopusdragon.mastermind to javafx.fxml;
    exports tech.octopusdragon.mastermind;
}