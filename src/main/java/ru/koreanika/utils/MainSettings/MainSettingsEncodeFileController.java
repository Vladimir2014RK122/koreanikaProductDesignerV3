package ru.koreanika.utils.MainSettings;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import ru.koreanika.project.ProjectFileDecoder;
import ru.koreanika.project.ProjectFileEncoder;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class MainSettingsEncodeFileController {

    @FXML
    TextField textFieldSrcEncode, textFieldDstEncode;
    @FXML
    Button btnSrcEncode, btnDstEncode, btnEncode, btnDecode;


    public void btnSrcEncodeClick(MouseEvent actionEvent) {
        System.out.println("btnSrcEncode clicked");
        File file = MainSettings.getInstance().showSrcFileChooser();
        if(file == null) return;
        textFieldSrcEncode.setText(file.getPath());
    }

    public void btnDstEncodeClick(MouseEvent actionEvent) {
        System.out.println("btnDstEncode clicked");
        File file = MainSettings.getInstance().showDstFileChooser();
        if(file == null) return;
        textFieldDstEncode.setText(file.getPath());
    }

    public void btnEncodeClick(MouseEvent actionEvent) {
        try {
            ProjectFileEncoder.encodeProjectFile(
                    new File(textFieldSrcEncode.getText()),
                    new File(textFieldDstEncode.getText()));

            Desktop.getDesktop().open(new File(textFieldDstEncode.getText()).getParentFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btnDecodeClick(MouseEvent actionEvent) {
        try {
            ProjectFileDecoder.decodeProjectFile(
                    new File(textFieldSrcEncode.getText()),
                    new File(textFieldDstEncode.getText()));
            Desktop.getDesktop().open(new File(textFieldDstEncode.getText()).getParentFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
