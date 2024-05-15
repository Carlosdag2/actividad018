package com.empresa.datastructures_javafx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HelloController {
    @FXML
    private TextField txt_dato;
    @FXML
    private TextArea area_datos;

    private ArrayList<String> datosAL = new ArrayList<String>();

    @FXML
    private TextField txt_datoSet;
    @FXML
    private Label lbl_numItems;
    @FXML
    private ListView<String> listViewSet;

    private Set<String> datosSet = new HashSet<>();

    @FXML
    private Label lbl_fechaHora;

    private TreeSet<String> datosSetOrdenados = new TreeSet<>();

    private Timeline timeline;

    @FXML
    private ComboBox<String> comboJugadores;
    @FXML
    private TextField txtNuevoJugador;
    @FXML
    private TextField txtGoles;
    @FXML
    private TextArea areaClasificacion;
    @FXML
    private TextField txtJornada;

    private Map<String, Integer> golesPorJugador = new HashMap<>();

    @FXML
    protected void initialize() {
        listViewSet.setItems(FXCollections.observableArrayList(datosSet));
        actualizarNumItems();
        actualizarFechaHora();
        iniciarActualizacionPeriodica();
        comboJugadores.setItems(FXCollections.observableArrayList("Vinicius Jr", "Mbappé", "Messi", "Bellingham", "Haaland"));
    }

    @FXML
    protected void agregarAL() {
        datosAL.add(txt_dato.getText());
        txt_dato.clear();
    }

    @FXML
    protected void mostrarAL() {
        StringBuilder sb = new StringBuilder();
        for (String item : datosAL) {
            sb.append(item+"\n");
        }//cierra bucle
        area_datos.setText(sb.toString());
    } //cierra método mostrarAL

    @FXML
    protected void agregarSet() {
        datosSet.add(txt_datoSet.getText());
        txt_datoSet.clear();
        actualizarNumItems();
    }

    @FXML
    protected void mostrarSet() {
        listViewSet.setItems(FXCollections.observableArrayList(datosSet));
        mostrarDireccionIP();
    }

    @FXML
    protected void eliminarElementoSeleccionado() {
        String elementoSeleccionado = listViewSet.getSelectionModel().getSelectedItem();
        if (elementoSeleccionado != null && !elementoSeleccionado.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de Eliminación");
            alert.setHeaderText("¿Estás seguro de que deseas eliminar este elemento?");
            alert.setContentText(elementoSeleccionado);

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    datosSet.remove(elementoSeleccionado);
                    listViewSet.setItems(FXCollections.observableArrayList(datosSet));
                    actualizarNumItems();
                }
            });
        }
    }

    @FXML
    protected void ordenarSetAlfabeticamente() {
        datosSetOrdenados.clear();
        datosSetOrdenados.addAll(datosSet);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Elementos del Set ordenados alfabéticamente");
        alert.setHeaderText(null);
        alert.setContentText(datosSetOrdenados.toString());
        alert.showAndWait();
    }

    private void actualizarNumItems() {
        lbl_numItems.setText("Número de elementos: " + datosSet.size());
    }

    private void mostrarDireccionIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // Filtrar direcciones IPv4 y no locales
                    if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(":") == -1) {
                        String ip = addr.getHostAddress();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Información de Dirección IP");
                        alert.setHeaderText("Dirección IP desde la que está trabajando:");
                        alert.setContentText(ip);
                        alert.showAndWait();
                        return;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        // Si no se puede obtener ninguna dirección IP, mostrar un mensaje en el registro de errores.
        System.err.println("No se pudo obtener la dirección IP.");
    }

    private void actualizarFechaHora() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        lbl_fechaHora.setText(formattedDateTime);
    }

    private void iniciarActualizacionPeriodica() {
        timeline = new Timeline(new KeyFrame(Duration.minutes(1), event -> {
            actualizarFechaHora();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    protected void agregarGoles() {
        String jugador = comboJugadores.getValue();
        int goles = Integer.parseInt(txtGoles.getText());

        // Actualizar la cantidad de goles del jugador
        if (golesPorJugador.containsKey(jugador)) {
            int golesActuales = golesPorJugador.get(jugador);
            golesPorJugador.put(jugador, golesActuales + goles);
        } else {
            golesPorJugador.put(jugador, goles);
        }

        // Limpiar el campo de texto de goles
        txtGoles.clear();
    }

    @FXML
    protected void mostrarClasificacion() {
        // Limpiar el área de texto antes de mostrar la nueva clasificación
        areaClasificacion.clear();

        // Mostrar la jornada actual
        String jornada = txtJornada.getText();
        areaClasificacion.appendText("Jornada: " + jornada + "\n\n");

        // Ordenar los jugadores por la cantidad de goles en orden descendente
        golesPorJugador.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .forEach(entry -> areaClasificacion.appendText(entry.getKey() + ": " + entry.getValue() + " goles\n"));
    }

    @FXML
    protected void agregarJugador() {
        String nuevoJugador = txtNuevoJugador.getText();
        if (!nuevoJugador.isEmpty() && !comboJugadores.getItems().contains(nuevoJugador)) {
            comboJugadores.getItems().add(nuevoJugador);
            txtNuevoJugador.clear();
        }
    }

} //cierra clase HelloController