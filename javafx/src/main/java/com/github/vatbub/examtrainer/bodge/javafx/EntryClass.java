package com.github.vatbub.examtrainer.bodge.javafx;

/*-
 * #%L
 * examtrainer.bodge.javafx
 * %%
 * Copyright (C) 2016 - 2018 Frederik Kammel
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.github.vatbub.common.core.Common;
import com.github.vatbub.common.core.logging.FOKLogger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EntryClass extends Application {
    private MainWindow controllerInstance;
    private Stage stage;
    private static EntryClass currentEntryClassInstance;
    public static void main(String[] args){
        Common.getInstance().setAppName("examtrainer.bodge");
        FOKLogger.enableLoggingOfUncaughtExceptions();

        launch(args);
    }

    public static EntryClass getCurrentEntryClassInstance() {
        return currentEntryClassInstance;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        currentEntryClassInstance = this;
        stage = primaryStage;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
        Parent root = fxmlLoader.load();
        setControllerInstance(fxmlLoader.getController());

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public MainWindow getControllerInstance() {
        return controllerInstance;
    }

    private void setControllerInstance(MainWindow controllerInstance) {
        this.controllerInstance = controllerInstance;
    }

    public Stage getStage() {
        return stage;
    }
}
