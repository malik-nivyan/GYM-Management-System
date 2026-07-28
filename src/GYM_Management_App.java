import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;

public class GYM_Management_App extends Application {

    private static final String ACCENT_COLOR    = "#00E676";
    private static final String MAIN_BG         = "#F4F7F6";
    private static final String CARD_BG         = "#FFFFFF";
    private static final String TEXT_PRIMARY    = "#2C3E50";
    private static final String TEXT_SECONDARY  = "#7F8C8D";
    private static final String BORDER_COLOR    = "#DCDDE1";


    private static final String GYM_NAME = "TITAN-FORGE";

    private static final String DB_URL      = "jdbc:mysql://127.0.0.1:3306/dump?serverTimezone=UTC";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "nivyan";
    private Connection conn;

    private String currentUserRole = "";

    private final String[] QUOTES = {
            "\"The only bad workout is the one that didn't happen.\"",
            "\"Sore today, strong tomorrow.\"",
            "\"Sweat is just fat crying.\"",
            "\"Don't stop when you're tired. Stop when you're done.\"",
            "\"Your body can stand almost anything. It's your mind that you have to convince.\""
    };


    @Override
    public void start(Stage primaryStage) {
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException ex) {
            showAlert(Alert.AlertType.ERROR, "DB Connection Error", "Database offline: " + ex.getMessage());
            return;
        }

        primaryStage.setTitle(GYM_NAME + " — Gym Management System");
        primaryStage.setScene(createLoginScene(primaryStage));
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.show();
    }


    private Scene createLoginScene(Stage stage) {
        StackPane root = new StackPane();

        // Background Layer
        VBox backgroundLayer = new VBox();
        try {
            Image img = new Image(new FileInputStream("gym_bg.jpg"));
            BackgroundImage bgImg = new BackgroundImage(img,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(1.0, 1.0, true, true, false, false));
            backgroundLayer.setBackground(new Background(bgImg));
        } catch (FileNotFoundException e) {
            backgroundLayer.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, #0f0c29, #302b63, #24243e);"
            );
        }

        VBox loginBox = new VBox(15);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setMaxWidth(420);
        loginBox.setPadding(new Insets(40));
        loginBox.setStyle(
                "-fx-background-color: rgba(255,255,255,0.93);" +
                        "-fx-background-radius: 18;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 30, 0, 0, 0);"
        );

        StackPane logo = createLogoGraphic();

        Label title = new Label(GYM_NAME);
        title.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 32));
        title.setTextFill(Color.web(TEXT_PRIMARY));

        Label sub = new Label("Admin Portal Access");
        sub.setTextFill(Color.web(TEXT_SECONDARY));
        sub.setFont(Font.font("System", 13));

        TextField userF = new TextField();
        userF.setPromptText("Username");
        styleField(userF);

        PasswordField passF = new PasswordField();
        passF.setPromptText("Password");
        styleField(passF);

        Button loginBtn = new Button("LOGIN");
        styleButton(loginBtn);
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        loginBtn.setOnAction(e -> {
            String role = authenticateAndGetRole(userF.getText(), passF.getText());
            if (role != null) {
                currentUserRole = role;
                stage.setScene(createDashboardScene(stage));
            } else {
                showAlert(Alert.AlertType.ERROR, "Access Denied", "Invalid username or password.");
            }
        });
        passF.setOnAction(e -> loginBtn.fire()); // Enter key triggers login

        Label quote = new Label(QUOTES[new java.util.Random().nextInt(QUOTES.length)]);
        quote.setStyle("-fx-font-style: italic; -fx-text-fill: #777; -fx-font-size: 11;");
        quote.setWrapText(true);
        quote.setMaxWidth(320);

        loginBox.getChildren().addAll(logo, title, sub, new Separator(), userF, passF, loginBtn, new Separator(), quote);
        root.getChildren().addAll(backgroundLayer, loginBox);
        return new Scene(root);
    }


    private Scene createDashboardScene(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + MAIN_BG + ";");

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_RIGHT);
        header.setPadding(new Insets(12, 30, 12, 30));
        header.setStyle(
                "-fx-background-color: " + CARD_BG + ";" +
                        "-fx-border-color: " + BORDER_COLOR + ";" +
                        "-fx-border-width: 0 0 1 0;"
        );

        Label gymLabel = new Label(GYM_NAME);
        gymLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 16));
        gymLabel.setTextFill(Color.web(ACCENT_COLOR));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label welcome = new Label("Welcome");
        welcome.setTextFill(Color.web(TEXT_PRIMARY));
        welcome.setFont(Font.font("System", FontWeight.BOLD, 13));

        Label roleBadge = new Label(currentUserRole.toUpperCase());
        roleBadge.setStyle(
                "-fx-background-color: " + ACCENT_COLOR + ";" +
                        "-fx-text-fill: #2C3E50;" +
                        "-fx-padding: 3 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 11;"
        );

        Button logout = new Button("Log Out");
        styleSmallButton(logout);
        logout.setOnAction(e -> {
            currentUserRole = "";
            stage.setScene(createLoginScene(stage));
        });

        header.getChildren().addAll(gymLabel, spacer, welcome, roleBadge, logout);
        root.setTop(header);

        TabPane tabs = new TabPane();
        tabs.setSide(Side.LEFT);
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabs.getTabs().add(new Tab("HOME", createHomePane()));

        if (currentUserRole.equals("Admin") || currentUserRole.equals("Staff")) {
            tabs.getTabs().add(new Tab("MEMBERS", createMembersPane()));
        }

        if (currentUserRole.equals("Admin")) {
            tabs.getTabs().add(new Tab("MEMBERSHIPS", createMembershipPane()));
            tabs.getTabs().add(new Tab("TRAINERS", createTrainersPane()));
        }

        if (currentUserRole.equals("Admin") || currentUserRole.equals("Staff")) {
            tabs.getTabs().add(new Tab("PAYMENTS", createPaymentsPane()));
        }

        tabs.getTabs().add(new Tab("SEARCH", createSearchPane()));

        root.setCenter(tabs);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("data:text/css," +
                ".tab-pane .tab-header-area .tab-header-background { -fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 0 1 0 0; } " +
                ".tab { -fx-background-color: transparent; -fx-padding: 15 30; } " +
                ".tab-label { -fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-weight: bold; } " +
                ".tab:selected { -fx-background-color: " + MAIN_BG + "; } " +
                ".tab:selected .tab-label { -fx-text-fill: " + ACCENT_COLOR + "; } " +
                ".table-view { -fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; } " +
                ".table-view .column-header { -fx-background-color: #F8F9FA; -fx-text-fill: " + TEXT_PRIMARY + "; } " +
                ".table-row-cell:odd { -fx-background-color: #FBFCFC; } " +
                ".table-row-cell:selected { -fx-background-color: " + ACCENT_COLOR + "; }"
        );
        return scene;
    }

    private VBox createHomePane() {
        VBox vbox = new VBox(25);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: " + MAIN_BG + ";");

        Label lbl = new Label("Revenue Analytics Dashboard");
        lbl.setFont(Font.font("System", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.web(TEXT_PRIMARY));

        int[] totalMembers  = {0};
        int[] activeMembers = {0};
        int[] expiredCount  = {0};
        double[] paidRevenue    = {0};
        double[] pendingRevenue = {0};

        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM member");
            if (rs.next()) totalMembers[0] = rs.getInt(1);

            rs = st.executeQuery("SELECT COUNT(*) FROM member WHERE Status = 'Active'");
            if (rs.next()) activeMembers[0] = rs.getInt(1);

            rs = st.executeQuery("SELECT COALESCE(SUM(Amount), 0) FROM payment WHERE Status = 'Paid'");
            if (rs.next()) paidRevenue[0] = rs.getDouble(1);

            rs = st.executeQuery("SELECT COALESCE(SUM(Amount), 0) FROM payment WHERE Status = 'Pending'");
            if (rs.next()) pendingRevenue[0] = rs.getDouble(1);

            rs = st.executeQuery(
                    "SELECT COUNT(*) FROM member m " +
                            "JOIN membership ms ON m.Membership_id = ms.Membership_id " +
                            "WHERE DATE_ADD(m.Join_date, INTERVAL ms.Duration_months MONTH) < CURDATE() " +
                            "AND m.Status = 'Active'"
            );
            if (rs.next()) expiredCount[0] = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);
        statsRow.getChildren().addAll(
                createStatCard("Total Members",   String.valueOf(totalMembers[0]),                         "#3498DB"),
                createStatCard("Active Members",  String.valueOf(activeMembers[0]),                        "#00E676"),
                createStatCard("Total Revenue",   "PKR " + String.format("%,.0f", paidRevenue[0]),        "#9B59B6"),
                createStatCard("Pending Dues",    "PKR " + String.format("%,.0f", pendingRevenue[0]),     "#E74C3C")
        );

        if (expiredCount[0] > 0) {
            Label warningLbl = new Label(
                    "⚠   " + expiredCount[0] + " member(s) have expired memberships still marked Active. " +
                            "Click 'Run Expiry Check' below to update their status."
            );
            warningLbl.setStyle(
                    "-fx-background-color: #FFF3CD;" +
                            "-fx-text-fill: #856404;" +
                            "-fx-padding: 10 15;" +
                            "-fx-background-radius: 8;" +
                            "-fx-font-weight: bold;"
            );
            warningLbl.setWrapText(true);
            vbox.getChildren().addAll(lbl, statsRow, warningLbl);
        } else {
            vbox.getChildren().addAll(lbl, statsRow);
        }

        HBox chartsRow = new HBox(20);
        VBox.setVgrow(chartsRow, Priority.ALWAYS);

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Payment Status Breakdown");
        double paidVal    = paidRevenue[0]    > 0 ? paidRevenue[0]    : 0.001; // avoid zero slice
        double pendingVal = pendingRevenue[0] > 0 ? pendingRevenue[0] : 0.001;
        pieChart.getData().addAll(
                new PieChart.Data("Paid (PKR " + String.format("%,.0f", paidRevenue[0]) + ")",       paidVal),
                new PieChart.Data("Pending (PKR " + String.format("%,.0f", pendingRevenue[0]) + ")", pendingVal)
        );
        pieChart.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        HBox.setHgrow(pieChart, Priority.ALWAYS);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Member Status");
        yAxis.setLabel("Count");
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Member Statistics");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Members");
        series.getData().add(new XYChart.Data<>("Active",         activeMembers[0]));
        series.getData().add(new XYChart.Data<>("Inactive",       totalMembers[0] - activeMembers[0]));
        series.getData().add(new XYChart.Data<>("Expired (!Fixed)", expiredCount[0]));
        barChart.getData().add(series);
        barChart.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        HBox.setHgrow(barChart, Priority.ALWAYS);

        chartsRow.getChildren().addAll(pieChart, barChart);

        Button expiryBtn = new Button("⟳  Run Membership Expiry Check");
        expiryBtn.setStyle(
                "-fx-background-color: #E74C3C;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;" +
                        "-fx-cursor: hand;"
        );
        expiryBtn.setPadding(new Insets(10, 25, 10, 25));
        expiryBtn.setOnMouseEntered(e -> expiryBtn.setOpacity(0.85));
        expiryBtn.setOnMouseExited(e -> expiryBtn.setOpacity(1.0));
        expiryBtn.setOnAction(e -> checkAndUpdateMembershipExpiry());

        HBox btnRow = new HBox(expiryBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        vbox.getChildren().addAll(chartsRow, btnRow);
        return vbox;
    }

    private VBox createStatCard(String title, String value, String accentHex) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(20));
        card.setMinWidth(185);
        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + accentHex + ";" +
                        "-fx-border-width: 0 0 0 5;" +                        // Left accent stripe
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 8, 0, 0, 2);"
        );
        Label titleLbl = new Label(title);
        titleLbl.setTextFill(Color.web(TEXT_SECONDARY));
        titleLbl.setFont(Font.font("System", 12));

        Label valueLbl = new Label(value);
        valueLbl.setTextFill(Color.web(accentHex));
        valueLbl.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 24));

        card.getChildren().addAll(titleLbl, valueLbl);
        return card;
    }

    private VBox createMembersPane() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: " + MAIN_BG + ";");

        Label lbl = new Label("Member Registry");
        lbl.setFont(Font.font("System", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.web(TEXT_PRIMARY));

        TableView<Member> table = new TableView<>();
        setupMemberColumns(table);
        ObservableList<Member> data = FXCollections.observableArrayList();
        loadMembers(data);
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        GridPane form = createFormGrid();
        TextField idF   = new TextField(); styleField(idF);   idF.setPromptText("ID");
        TextField fnF   = new TextField(); styleField(fnF);   fnF.setPromptText("First Name");
        TextField lnF   = new TextField(); styleField(lnF);   lnF.setPromptText("Last Name");
        ComboBox<String> genB = new ComboBox<>(FXCollections.observableArrayList("Male", "Female"));
        TextField ageF  = new TextField(); styleField(ageF);  ageF.setPromptText("Age");
        TextField phF   = new TextField(); styleField(phF);   phF.setPromptText("Phone");
        TextField emF   = new TextField(); styleField(emF);   emF.setPromptText("Email");
        DatePicker dateP = new DatePicker();
        TextField trF   = new TextField(); styleField(trF);   trF.setPromptText("Trainer ID");
        ComboBox<String> stB = new ComboBox<>(FXCollections.observableArrayList("Active", "Inactive"));
        TextField typeF = new TextField(); styleField(typeF); typeF.setPromptText("Plan ID");

        addFormField(form, "ID:",         idF,   0, 0); addFormField(form, "First:",     fnF,   1, 0);
        addFormField(form, "Last:",        lnF,   0, 1); addFormField(form, "Gender:",    genB,  1, 1);
        addFormField(form, "Age:",         ageF,  0, 2); addFormField(form, "Phone:",     phF,   1, 2);
        addFormField(form, "Email:",       emF,   0, 3); addFormField(form, "Joined:",    dateP, 1, 3);
        addFormField(form, "Trainer ID:", trF,   0, 4); addFormField(form, "Status:",    stB,   1, 4);
        addFormField(form, "Plan ID:",    typeF, 0, 5);

        HBox actions = new HBox(15); actions.setAlignment(Pos.CENTER_RIGHT);
        Button add = new Button("Register"); styleButton(add);
        Button upd = new Button("Update");   styleButton(upd);
        Button del = new Button("Remove");
        del.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        del.setPadding(new Insets(10, 25, 10, 25));
        actions.getChildren().addAll(add, upd, del);

        add.setOnAction(e -> {
            try {
                insertMember(new Member(Integer.parseInt(idF.getText()), fnF.getText(), lnF.getText(), genB.getValue(),
                        Integer.parseInt(ageF.getText()), phF.getText(), emF.getText(), dateP.getValue(),
                        Integer.parseInt(trF.getText()), stB.getValue(), Integer.parseInt(typeF.getText())));
                data.clear(); loadMembers(data);
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Check all fields: " + ex.getMessage()); }
        });
        upd.setOnAction(e -> {
            try {
                updateMember(new Member(Integer.parseInt(idF.getText()), fnF.getText(), lnF.getText(), genB.getValue(),
                        Integer.parseInt(ageF.getText()), phF.getText(), emF.getText(), dateP.getValue(),
                        Integer.parseInt(trF.getText()), stB.getValue(), Integer.parseInt(typeF.getText())));
                data.clear(); loadMembers(data);
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Check all fields: " + ex.getMessage()); }
        });
        del.setOnAction(e -> {
            try { deleteMember(Integer.parseInt(idF.getText())); data.clear(); loadMembers(data); }
            catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID."); }
        });

        table.setOnMouseClicked(e -> {
            Member m = table.getSelectionModel().getSelectedItem();
            if (m != null) {
                idF.setText(String.valueOf(m.getMemberId())); fnF.setText(m.getFirstName());
                lnF.setText(m.getLastName());  genB.setValue(m.getGender());
                ageF.setText(String.valueOf(m.getAge())); phF.setText(m.getPhone());
                emF.setText(m.getEmail());     dateP.setValue(m.getJoinDate());
                trF.setText(String.valueOf(m.getTrainerId())); stB.setValue(m.getStatus());
                typeF.setText(String.valueOf(m.getMembershipTypeId()));
            }
        });

        vbox.getChildren().addAll(lbl, table, form, actions);
        return vbox;
    }
    private VBox createMembershipPane() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: " + MAIN_BG + ";");

        Label lbl = new Label("Subscription Plans");
        lbl.setFont(Font.font("System", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.web(TEXT_PRIMARY));

        TableView<Membership> table = new TableView<>();
        TableColumn<Membership, Integer> idCol   = new TableColumn<>("ID");             idCol.setCellValueFactory(new PropertyValueFactory<>("membershipId"));
        TableColumn<Membership, String>  nameCol = new TableColumn<>("Plan Name");      nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Membership, Integer> durCol  = new TableColumn<>("Duration (Mos)"); durCol.setCellValueFactory(new PropertyValueFactory<>("durationMonths"));
        TableColumn<Membership, Double>  feeCol  = new TableColumn<>("Fee (PKR)");      feeCol.setCellValueFactory(new PropertyValueFactory<>("fee"));
        table.getColumns().addAll(idCol, nameCol, durCol, feeCol);

        ObservableList<Membership> data = FXCollections.observableArrayList();
        loadMemberships(data); table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        GridPane form = createFormGrid();
        TextField idF   = new TextField(); styleField(idF);   idF.setPromptText("Plan ID");
        TextField nameF = new TextField(); styleField(nameF); nameF.setPromptText("Plan Name");
        TextField durF  = new TextField(); styleField(durF);  durF.setPromptText("Duration (months)");
        TextField feeF  = new TextField(); styleField(feeF);  feeF.setPromptText("Fee (PKR)");

        addFormField(form, "ID:",       idF,   0, 0); addFormField(form, "Name:",     nameF, 1, 0);
        addFormField(form, "Duration:", durF,  0, 1); addFormField(form, "Fee (PKR):", feeF, 1, 1);

        HBox actions = new HBox(15); actions.setAlignment(Pos.CENTER_RIGHT);
        Button add = new Button("Add Plan"); styleButton(add);
        // *** NEW: Delete Plan button added (was missing before) ***
        Button del = new Button("Delete Plan");
        del.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        del.setPadding(new Insets(10, 25, 10, 25));
        actions.getChildren().addAll(add, del);

        add.setOnAction(e -> {
            try {
                insertMembership(new Membership(Integer.parseInt(idF.getText()), nameF.getText(),
                        Integer.parseInt(durF.getText()), Double.parseDouble(feeF.getText())));
                data.clear(); loadMemberships(data);
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", ex.getMessage()); }
        });

        del.setOnAction(e -> {
            try { deleteMembership(Integer.parseInt(idF.getText())); data.clear(); loadMemberships(data); }
            catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID or plan is in use."); }
        });


        table.setOnMouseClicked(e -> {
            Membership m = table.getSelectionModel().getSelectedItem();
            if (m != null) {
                idF.setText(String.valueOf(m.getMembershipId())); nameF.setText(m.getName());
                durF.setText(String.valueOf(m.getDurationMonths())); feeF.setText(String.valueOf(m.getFee()));
            }
        });

        vbox.getChildren().addAll(lbl, table, form, actions);
        return vbox;
    }

    private VBox createTrainersPane() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: " + MAIN_BG + ";");

        Label lbl = new Label("Trainer Roster");
        lbl.setFont(Font.font("System", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.web(TEXT_PRIMARY));

        TableView<Trainer> table = new TableView<>();
        TableColumn<Trainer, Integer> idCol   = new TableColumn<>("ID");             idCol.setCellValueFactory(new PropertyValueFactory<>("trainerId"));
        TableColumn<Trainer, String>  fnCol   = new TableColumn<>("First Name");     fnCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<Trainer, String>  lnCol   = new TableColumn<>("Last Name");      lnCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        TableColumn<Trainer, String>  specCol = new TableColumn<>("Specialization"); specCol.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        TableColumn<Trainer, String>  phCol   = new TableColumn<>("Phone");          phCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        table.getColumns().addAll(idCol, fnCol, lnCol, specCol, phCol);

        ObservableList<Trainer> data = FXCollections.observableArrayList();
        loadTrainers(data); table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        GridPane form = createFormGrid();
        TextField idF  = new TextField(); styleField(idF);  idF.setPromptText("ID");
        TextField fnF  = new TextField(); styleField(fnF);  fnF.setPromptText("First Name");
        TextField lnF  = new TextField(); styleField(lnF);  lnF.setPromptText("Last Name");
        ComboBox<String> genB = new ComboBox<>(FXCollections.observableArrayList("Male", "Female"));
        TextField ageF = new TextField(); styleField(ageF); ageF.setPromptText("Age");
        TextField phF  = new TextField(); styleField(phF);  phF.setPromptText("Phone");
        TextField emF  = new TextField(); styleField(emF);  emF.setPromptText("Email");
        TextField spF  = new TextField(); styleField(spF);  spF.setPromptText("Specialization");
        DatePicker hireP = new DatePicker();

        addFormField(form, "ID:",     idF,  0, 0); addFormField(form, "First:", fnF,  1, 0);
        addFormField(form, "Last:",   lnF,  0, 1); addFormField(form, "Gender:", genB, 1, 1);
        addFormField(form, "Age:",    ageF, 0, 2); addFormField(form, "Phone:", phF,  1, 2);
        addFormField(form, "Email:",  emF,  0, 3); addFormField(form, "Spec:",  spF,  1, 3);
        addFormField(form, "Hired:",  hireP, 0, 4);

        HBox actions = new HBox(15); actions.setAlignment(Pos.CENTER_RIGHT);
        Button add = new Button("Add Trainer");  styleButton(add);
        // *** NEW: Update Trainer button (was missing before) ***
        Button upd = new Button("Update Trainer"); styleButton(upd);
        Button del = new Button("Fire Trainer");
        del.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        del.setPadding(new Insets(10, 25, 10, 25));
        actions.getChildren().addAll(add, upd, del);

        add.setOnAction(e -> {
            try {
                insertTrainer(new Trainer(Integer.parseInt(idF.getText()), fnF.getText(), lnF.getText(), genB.getValue(),
                        Integer.parseInt(ageF.getText()), phF.getText(), emF.getText(), spF.getText(), hireP.getValue()));
                data.clear(); loadTrainers(data);
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Check all fields."); }
        });
        upd.setOnAction(e -> {
            try {
                updateTrainer(new Trainer(Integer.parseInt(idF.getText()), fnF.getText(), lnF.getText(), genB.getValue(),
                        Integer.parseInt(ageF.getText()), phF.getText(), emF.getText(), spF.getText(), hireP.getValue()));
                data.clear(); loadTrainers(data);
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Check all fields."); }
        });
        del.setOnAction(e -> {
            try { deleteTrainer(Integer.parseInt(idF.getText())); data.clear(); loadTrainers(data); }
            catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Invalid ID."); }
        });

        table.setOnMouseClicked(e -> {
            Trainer t = table.getSelectionModel().getSelectedItem();
            if (t != null) {
                idF.setText(String.valueOf(t.getTrainerId())); fnF.setText(t.getFirstName());
                lnF.setText(t.getLastName());  genB.setValue(t.getGender());
                ageF.setText(String.valueOf(t.getAge())); phF.setText(t.getPhone());
                emF.setText(t.getEmail());     spF.setText(t.getSpecialization());
                hireP.setValue(t.getHireDate());
            }
        });

        vbox.getChildren().addAll(lbl, table, form, actions);
        return vbox;
    }

    private VBox createPaymentsPane() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: " + MAIN_BG + ";");

        Label lbl = new Label("Financial Records");
        lbl.setFont(Font.font("System", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.web(TEXT_PRIMARY));

        TableView<Payment> table = new TableView<>();
        TableColumn<Payment, Integer>   idCol     = new TableColumn<>("ID");          idCol.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        TableColumn<Payment, Integer>   memCol    = new TableColumn<>("Member ID");   memCol.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        TableColumn<Payment, Double>    amtCol    = new TableColumn<>("Amount (PKR)");amtCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<Payment, LocalDate> dateCol   = new TableColumn<>("Date");        dateCol.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        TableColumn<Payment, String>    methodCol = new TableColumn<>("Method");      methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        TableColumn<Payment, String>    statusCol = new TableColumn<>("Status");      statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        table.getColumns().addAll(idCol, memCol, amtCol, dateCol, methodCol, statusCol);

        ObservableList<Payment> data = FXCollections.observableArrayList();
        loadPayments(data); table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        GridPane form = createFormGrid();
        TextField idF  = new TextField(); styleField(idF);  idF.setPromptText("Payment ID");
        TextField memF = new TextField(); styleField(memF); memF.setPromptText("Member ID");
        TextField amtF = new TextField(); styleField(amtF); amtF.setPromptText("Amount (PKR)");
        DatePicker dateP = new DatePicker();
        ComboBox<String> metB  = new ComboBox<>(FXCollections.observableArrayList("Cash", "Card", "Bank"));
        ComboBox<String> statB = new ComboBox<>(FXCollections.observableArrayList("Paid", "Pending"));

        addFormField(form, "Pay ID:",    idF,   0, 0); addFormField(form, "Member ID:", memF,  1, 0);
        addFormField(form, "Amount:",    amtF,  0, 1); addFormField(form, "Date:",       dateP, 1, 1);
        addFormField(form, "Method:",   metB,  0, 2); addFormField(form, "Status:",     statB, 1, 2);

        HBox actions = new HBox(15); actions.setAlignment(Pos.CENTER_RIGHT);
        Button add = new Button("Record Payment"); styleButton(add);
        Button upd = new Button("Update Payment"); styleButton(upd);
        Button del = new Button("Delete Payment");
        del.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        del.setPadding(new Insets(10, 25, 10, 25));
        actions.getChildren().addAll(add, upd, del);

        add.setOnAction(e -> {
            try {
                insertPayment(new Payment(Integer.parseInt(idF.getText()), Integer.parseInt(memF.getText()),
                        Double.parseDouble(amtF.getText()), dateP.getValue(), metB.getValue(), statB.getValue()));
                data.clear(); loadPayments(data);
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Check all fields."); }
        });
        upd.setOnAction(e -> {
            try {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE payment SET Member_id=?, Amount=?, Payment_date=?, Payment_method=?, Status=? WHERE Payment_id=?");
                ps.setInt(1, Integer.parseInt(memF.getText()));
                ps.setDouble(2, Double.parseDouble(amtF.getText()));
                ps.setDate(3, Date.valueOf(dateP.getValue()));
                ps.setString(4, metB.getValue());
                ps.setString(5, statB.getValue());
                ps.setInt(6, Integer.parseInt(idF.getText()));
                ps.executeUpdate();
                data.clear(); loadPayments(data);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Payment updated successfully.");
            } catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Select a row first: " + ex.getMessage()); }
        });
        del.setOnAction(e -> {
            try { deletePayment(Integer.parseInt(idF.getText())); data.clear(); loadPayments(data); }
            catch (Exception ex) { showAlert(Alert.AlertType.ERROR, "Error", "Invalid Payment ID."); }
        });

        table.setOnMouseClicked(e -> {
            Payment p = table.getSelectionModel().getSelectedItem();
            if (p != null) {
                idF.setText(String.valueOf(p.getPaymentId())); memF.setText(String.valueOf(p.getMemberId()));
                amtF.setText(String.valueOf(p.getAmount()));   dateP.setValue(p.getPaymentDate());
                metB.setValue(p.getPaymentMethod());           statB.setValue(p.getStatus());
            }
        });

        vbox.getChildren().addAll(lbl, table, form, actions);
        return vbox;
    }

    private VBox createSearchPane() {
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: " + MAIN_BG + ";");

        Label lbl = new Label("Search Database");
        lbl.setFont(Font.font("System", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.web(TEXT_PRIMARY));

        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        TextField searchField = new TextField();
        styleField(searchField);
        searchField.setPromptText("Search by Name or ID...");
        searchField.setPrefWidth(300);
        Button searchBtn = new Button("Search"); styleButton(searchBtn);
        searchBar.getChildren().addAll(new Label("Find Member:"), searchField, searchBtn);

        TableView<Member> table = new TableView<>();
        setupMemberColumns(table);
        ObservableList<Member> searchResults = FXCollections.observableArrayList();
        table.setItems(searchResults);
        VBox.setVgrow(table, Priority.ALWAYS);

        Runnable doSearch = () -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) { searchResults.clear(); searchMembers(query, searchResults); }
        };
        searchBtn.setOnAction(e -> doSearch.run());
        searchField.setOnAction(e -> doSearch.run()); // *** NEW: Enter key to search ***

        vbox.getChildren().addAll(lbl, searchBar, table);
        return vbox;
    }

    private StackPane createLogoGraphic() {
        StackPane logo = new StackPane();
        logo.setPrefSize(90, 90);

        Circle outerRing = new Circle(44);
        outerRing.setFill(Color.TRANSPARENT);
        outerRing.setStroke(Color.web("#00E676"));
        outerRing.setStrokeWidth(3);

        Circle inner = new Circle(40);
        inner.setFill(Color.web("#1A1A2E")); // Deep navy

        Circle midRing = new Circle(34);
        midRing.setFill(Color.TRANSPARENT);
        midRing.setStroke(Color.web("#00E676"));
        midRing.setStrokeWidth(1);
        midRing.setOpacity(0.35);

        Text monogram = new Text("TF");
        monogram.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 27));
        monogram.setFill(Color.web("#00E676"));

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#00E676"));
        glow.setRadius(18);
        glow.setSpread(0.25);
        logo.setEffect(glow);

        logo.getChildren().addAll(inner, midRing, outerRing, monogram);
        return logo;
    }

    private void styleField(Control field) {
        field.setStyle("-fx-background-color: white; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 5; -fx-padding: 8;");
    }
    private void styleButton(Button btn) {
        btn.setStyle("-fx-background-color: " + ACCENT_COLOR + "; -fx-text-fill: #2C3E50; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        btn.setPadding(new Insets(10, 25, 10, 25));
        btn.setOnMouseEntered(e -> btn.setOpacity(0.8));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
    }
    private void styleSmallButton(Button btn) {
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + TEXT_SECONDARY + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 5; -fx-padding: 5 15;");
    }
    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(15); grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10; -fx-border-color: " + BORDER_COLOR + ";");
        return grid;
    }
    private void addFormField(GridPane grid, String label, javafx.scene.Node field, int col, int row) {
        Label l = new Label(label);
        l.setTextFill(Color.web(TEXT_PRIMARY));
        l.setFont(Font.font("System", FontWeight.BOLD, 12));
        grid.add(l, col * 2, row);
        grid.add(field, col * 2 + 1, row);
    }
    private void setupMemberColumns(TableView<Member> table) {
        TableColumn<Member, Integer> c1 = new TableColumn<>("ID");      c1.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        TableColumn<Member, String>  c2 = new TableColumn<>("First");   c2.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        TableColumn<Member, String>  c3 = new TableColumn<>("Last");    c3.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        TableColumn<Member, String>  c4 = new TableColumn<>("Phone");   c4.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Member, String>  c5 = new TableColumn<>("Status");  c5.setCellValueFactory(new PropertyValueFactory<>("status"));
        // *** NEW: Plan ID column added to member table ***
        TableColumn<Member, Integer> c6 = new TableColumn<>("Plan ID"); c6.setCellValueFactory(new PropertyValueFactory<>("membershipTypeId"));
        table.getColumns().addAll(c1, c2, c3, c4, c5, c6);
    }


    private String authenticateAndGetRole(String u, String p) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT Role FROM login WHERE Username = ? AND Password = ?")) {
            ps.setString(1, u); ps.setString(2, p);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("Role");
        } catch (SQLException ex) {
            // Role column may not exist yet — fall back to simple auth, default Admin
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM login WHERE Username = ? AND Password = ?")) {
                ps.setString(1, u); ps.setString(2, p);
                if (ps.executeQuery().next()) return "Admin";
            } catch (SQLException ex2) { ex2.printStackTrace(); }
        }
        return null;
    }

    private void loadMembers(ObservableList<Member> list) {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM member")) {
            while (rs.next())
                list.add(new Member(rs.getInt("Member_id"), rs.getString("First_name"), rs.getString("Last_name"),
                        rs.getString("Gender"), rs.getInt("Age"), rs.getString("Phone"), rs.getString("Email"),
                        rs.getDate("Join_date") != null ? rs.getDate("Join_date").toLocalDate() : null,
                        rs.getInt("Trainer_id"), rs.getString("Status"), rs.getInt("Membership_id")));
        } catch (Exception e) { e.printStackTrace(); }
    }
    private void insertMember(Member m) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO member VALUES (?,?,?,?,?,?,?,?,?,?,?)");
        ps.setInt(1, m.getMemberId()); ps.setString(2, m.getFirstName()); ps.setString(3, m.getLastName());
        ps.setString(4, m.getGender()); ps.setInt(5, m.getAge()); ps.setString(6, m.getPhone());
        ps.setString(7, m.getEmail()); ps.setDate(8, Date.valueOf(m.getJoinDate()));
        ps.setInt(9, m.getTrainerId()); ps.setString(10, m.getStatus()); ps.setInt(11, m.getMembershipTypeId());
        ps.executeUpdate();
    }
    private void updateMember(Member m) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE member SET First_name=?, Last_name=?, Gender=?, Age=?, Phone=?, Email=?, " +
                        "Join_date=?, Trainer_id=?, Status=?, Membership_id=? WHERE Member_id=?");
        ps.setString(1, m.getFirstName()); ps.setString(2, m.getLastName()); ps.setString(3, m.getGender());
        ps.setInt(4, m.getAge()); ps.setString(5, m.getPhone()); ps.setString(6, m.getEmail());
        ps.setDate(7, Date.valueOf(m.getJoinDate())); ps.setInt(8, m.getTrainerId());
        ps.setString(9, m.getStatus()); ps.setInt(10, m.getMembershipTypeId()); ps.setInt(11, m.getMemberId());
        ps.executeUpdate();
    }
    private void deleteMember(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM member WHERE Member_id=?");
        ps.setInt(1, id); ps.executeUpdate();
    }
    private void searchMembers(String q, ObservableList<Member> list) {
        String sql = "SELECT * FROM member WHERE First_name LIKE ? OR Last_name LIKE ? OR Member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + q + "%"); ps.setString(2, "%" + q + "%");
            try { ps.setInt(3, Integer.parseInt(q)); } catch (NumberFormatException e) { ps.setInt(3, -1); }
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                list.add(new Member(rs.getInt("Member_id"), rs.getString("First_name"), rs.getString("Last_name"),
                        rs.getString("Gender"), rs.getInt("Age"), rs.getString("Phone"), rs.getString("Email"),
                        rs.getDate("Join_date") != null ? rs.getDate("Join_date").toLocalDate() : null,
                        rs.getInt("Trainer_id"), rs.getString("Status"), rs.getInt("Membership_id")));
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void loadMemberships(ObservableList<Membership> list) {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM membership")) {
            while (rs.next())
                list.add(new Membership(rs.getInt("Membership_id"), rs.getString("Name"),
                        rs.getInt("Duration_months"), rs.getDouble("Fee")));
        } catch (Exception e) { e.printStackTrace(); }
    }
    private void insertMembership(Membership m) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO membership VALUES (?,?,?,?)");
        ps.setInt(1, m.getMembershipId()); ps.setString(2, m.getName());
        ps.setInt(3, m.getDurationMonths()); ps.setDouble(4, m.getFee());
        ps.executeUpdate();
    }

    private void deleteMembership(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM membership WHERE Membership_id=?");
        ps.setInt(1, id); ps.executeUpdate();
    }

    private void loadTrainers(ObservableList<Trainer> list) {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM trainer")) {
            while (rs.next())
                list.add(new Trainer(rs.getInt("Trainer_id"), rs.getString("First_name"), rs.getString("Last_name"),
                        rs.getString("Gender"), rs.getInt("Age"), rs.getString("Phone"), rs.getString("Email"),
                        rs.getString("Specialization"),
                        rs.getDate("Hire_date") != null ? rs.getDate("Hire_date").toLocalDate() : null));
        } catch (Exception e) { e.printStackTrace(); }
    }
    private void insertTrainer(Trainer t) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO trainer VALUES (?,?,?,?,?,?,?,?,?)");
        ps.setInt(1, t.getTrainerId()); ps.setString(2, t.getFirstName()); ps.setString(3, t.getLastName());
        ps.setString(4, t.getGender()); ps.setInt(5, t.getAge()); ps.setString(6, t.getPhone());
        ps.setString(7, t.getEmail()); ps.setString(8, t.getSpecialization());
        ps.setDate(9, Date.valueOf(t.getHireDate())); ps.executeUpdate();
    }
    private void updateTrainer(Trainer t) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "UPDATE trainer SET First_name=?, Last_name=?, Gender=?, Age=?, Phone=?, Email=?, " +
                        "Specialization=?, Hire_date=? WHERE Trainer_id=?");
        ps.setString(1, t.getFirstName()); ps.setString(2, t.getLastName()); ps.setString(3, t.getGender());
        ps.setInt(4, t.getAge()); ps.setString(5, t.getPhone()); ps.setString(6, t.getEmail());
        ps.setString(7, t.getSpecialization()); ps.setDate(8, Date.valueOf(t.getHireDate()));
        ps.setInt(9, t.getTrainerId()); ps.executeUpdate();
    }
    private void deleteTrainer(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM trainer WHERE Trainer_id=?");
        ps.setInt(1, id); ps.executeUpdate();
    }

    private void loadPayments(ObservableList<Payment> list) {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM payment")) {
            while (rs.next())
                list.add(new Payment(rs.getInt("Payment_id"), rs.getInt("Member_id"), rs.getDouble("Amount"),
                        rs.getDate("Payment_date") != null ? rs.getDate("Payment_date").toLocalDate() : null,
                        rs.getString("Payment_method"), rs.getString("Status")));
        } catch (Exception e) { e.printStackTrace(); }
    }
    private void insertPayment(Payment p) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO payment VALUES (?,?,?,?,?,?)");
        ps.setInt(1, p.getPaymentId()); ps.setInt(2, p.getMemberId()); ps.setDouble(3, p.getAmount());
        ps.setDate(4, Date.valueOf(p.getPaymentDate())); ps.setString(5, p.getPaymentMethod());
        ps.setString(6, p.getStatus()); ps.executeUpdate();
    }
    private void deletePayment(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM payment WHERE Payment_id=?");
        ps.setInt(1, id); ps.executeUpdate();
    }

    private void checkAndUpdateMembershipExpiry() {
        String countSQL =
                "SELECT COUNT(*) FROM member m " +
                        "JOIN membership ms ON m.Membership_id = ms.Membership_id " +
                        "WHERE DATE_ADD(m.Join_date, INTERVAL ms.Duration_months MONTH) < CURDATE() " +
                        "AND m.Status = 'Active'";
        String updateSQL =
                "UPDATE member m " +
                        "JOIN membership ms ON m.Membership_id = ms.Membership_id " +
                        "SET m.Status = 'Inactive' " +
                        "WHERE DATE_ADD(m.Join_date, INTERVAL ms.Duration_months MONTH) < CURDATE() " +
                        "AND m.Status = 'Active'";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(countSQL);
            int expiredCount = rs.next() ? rs.getInt(1) : 0;
            st.executeUpdate(updateSQL);
            showAlert(Alert.AlertType.INFORMATION, "Expiry Check Complete",
                    expiredCount + " member(s) had expired memberships and have been marked Inactive.\n" +
                            "Refresh the Members tab to see updated statuses.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Expiry Check Failed", e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType t, String head, String msg) {
        Alert a = new Alert(t); a.setHeaderText(head); a.setContentText(msg); a.showAndWait();
    }


    public static class Member {
        private final SimpleIntegerProperty memberId, age, trainerId, membershipTypeId;
        private final SimpleStringProperty firstName, lastName, gender, phone, email, status;
        private final SimpleObjectProperty<LocalDate> joinDate;
        public Member(int id, String fn, String ln, String g, int a, String p, String e, LocalDate jd, int tid, String s, int mid) {
            this.memberId = new SimpleIntegerProperty(id); this.firstName = new SimpleStringProperty(fn);
            this.lastName = new SimpleStringProperty(ln); this.gender = new SimpleStringProperty(g);
            this.age = new SimpleIntegerProperty(a); this.phone = new SimpleStringProperty(p);
            this.email = new SimpleStringProperty(e); this.joinDate = new SimpleObjectProperty<>(jd);
            this.trainerId = new SimpleIntegerProperty(tid); this.status = new SimpleStringProperty(s);
            this.membershipTypeId = new SimpleIntegerProperty(mid);
        }
        public int getMemberId(){return memberId.get();} public String getFirstName(){return firstName.get();}
        public String getLastName(){return lastName.get();} public String getStatus(){return status.get();}
        public String getGender(){return gender.get();} public int getAge(){return age.get();}
        public String getPhone(){return phone.get();} public String getEmail(){return email.get();}
        public LocalDate getJoinDate(){return joinDate.get();} public int getTrainerId(){return trainerId.get();}
        public int getMembershipTypeId(){return membershipTypeId.get();}
    }

    public static class Membership {
        private final SimpleIntegerProperty membershipId, durationMonths;
        private final SimpleStringProperty name;
        private final SimpleObjectProperty<Double> fee;
        public Membership(int id, String n, int d, double f) {
            this.membershipId = new SimpleIntegerProperty(id); this.name = new SimpleStringProperty(n);
            this.durationMonths = new SimpleIntegerProperty(d); this.fee = new SimpleObjectProperty<>(f);
        }
        public int getMembershipId(){return membershipId.get();} public String getName(){return name.get();}
        public int getDurationMonths(){return durationMonths.get();} public double getFee(){return fee.get();}
    }

    public static class Trainer {
        private final SimpleIntegerProperty trainerId, age;
        private final SimpleStringProperty firstName, lastName, gender, phone, email, specialization;
        private final SimpleObjectProperty<LocalDate> hireDate;
        public Trainer(int id, String fn, String ln, String g, int age, String phone, String email, String spec, LocalDate hire) {
            this.trainerId = new SimpleIntegerProperty(id); this.firstName = new SimpleStringProperty(fn);
            this.lastName = new SimpleStringProperty(ln); this.gender = new SimpleStringProperty(g);
            this.age = new SimpleIntegerProperty(age); this.phone = new SimpleStringProperty(phone);
            this.email = new SimpleStringProperty(email); this.specialization = new SimpleStringProperty(spec);
            this.hireDate = new SimpleObjectProperty<>(hire);
        }
        public int getTrainerId(){return trainerId.get();} public String getFirstName(){return firstName.get();}
        public String getLastName(){return lastName.get();} public String getSpecialization(){return specialization.get();}
        public String getGender(){return gender.get();} public int getAge(){return age.get();}
        public String getPhone(){return phone.get();} public String getEmail(){return email.get();}
        public LocalDate getHireDate(){return hireDate.get();}
    }

    public static class Payment {
        private final SimpleIntegerProperty paymentId, memberId;
        private final SimpleObjectProperty<Double> amount;
        private final SimpleObjectProperty<LocalDate> paymentDate;
        private final SimpleStringProperty paymentMethod, status;
        public Payment(int pid, int mid, double amt, LocalDate date, String method, String status) {
            this.paymentId = new SimpleIntegerProperty(pid); this.memberId = new SimpleIntegerProperty(mid);
            this.amount = new SimpleObjectProperty<>(amt); this.paymentDate = new SimpleObjectProperty<>(date);
            this.paymentMethod = new SimpleStringProperty(method); this.status = new SimpleStringProperty(status);
        }
        public int getPaymentId(){return paymentId.get();} public int getMemberId(){return memberId.get();}
        public double getAmount(){return amount.get();} public LocalDate getPaymentDate(){return paymentDate.get();}
        public String getPaymentMethod(){return paymentMethod.get();} public String getStatus(){return status.get();}
    }

    public static void main(String[] args) {
        launch(args);
    }
}