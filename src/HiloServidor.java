import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class HiloServidor extends Thread {

	ObjectInputStream fentrada;
	ObjectOutputStream fsalida;
	FileWriter log;
	Integer id;
	Profesor[] teachers;
	Socket socket = null;
	private final Map<Integer, Long> inicioSesion = new HashMap<>();

	public HiloServidor(Socket s, FileWriter fileWriter, int id, Profesor[] teachers) throws IOException {
		socket = s;
		fsalida = new ObjectOutputStream(socket.getOutputStream());
		fentrada = new ObjectInputStream(socket.getInputStream());
		this.log = fileWriter;
		this.id = id;
		this.teachers = teachers;
	}

	public void run() {

		ObjetoCompartido objetoCompartido = new ObjetoCompartido(this.log);
//		int numeroCliente = objetoCompartido.getNumeroCliente();
		System.out.printf("Cliente %s conectado\n", this.id);
		try {
			objetoCompartido.writeLine(this.log, String.format("Cliente: %s iniciado, (%s)\n", this.id,
															   formatDate(LocalDateTime.now())));

			inicioSesion.put(this.id, System.currentTimeMillis());

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fsalida.reset();
			fsalida.writeObject(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String cadena = "";
		while (!cadena.trim().equals("*")) {
			try {
				cadena = sendDataToClient(objetoCompartido);
			} catch (IOException | ClassNotFoundException e) {
				System.out.println("ERROR AL LEER DATOS DEL CLIENTE");
				System.err.println(e.getMessage());
			}
		}

		System.out.printf("\tEL CLIENTE %s HA FINALIZADO\n", id);

		try {
			fsalida.close();
			fentrada.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String sendDataToClient(ObjetoCompartido objetoCompartido)
			throws IOException, ClassNotFoundException {
		String cadena;
		//fentrada.reset();
		cadena = (String) fentrada.readObject();
		if (cadena.equals("*")) {
			Long startTime = inicioSesion.get(id);
			if (startTime == null) {
				throw new NullPointerException(String.format("No se ha encontrado la hora de inicio del cliente %s", id));
			}
			long totalTime = System.currentTimeMillis() - startTime;
			objetoCompartido.writeLine(this.log,
									   String.format("=>FIN con el cliente %s, Tiempo total conectado %s milisegundos (%s)\n",
													 id, totalTime, formatDate(LocalDateTime.now())));
			return cadena;
		}

		Integer idProfesor;
		try {
			idProfesor = Integer.parseInt(cadena);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Ha ocurrido un error al parsear el número " + cadena);
		}
		String mensaje = String.format("\t\tConsultando id: %s, solicitado por el cliente %s\n",
									   idProfesor, id);
		objetoCompartido.writeLine(this.log, mensaje);
		System.out.print(mensaje);
		Profesor profesorEncontrado = null;
		for (Profesor profesor : this.teachers) {
			if (profesor.getIdprofesor() == idProfesor) {
				profesorEncontrado = profesor;
				break;
			}
		}
		if (profesorEncontrado == null) {
			profesorEncontrado = new Profesor(idProfesor, "No existe",
											  new Asignatura[]{}, new Especialidad(0, "sin datos"));
		}
		fsalida.writeObject(profesorEncontrado);

		return cadena;
	}

	private String formatDate(LocalDateTime time) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		return time.format(formatter);
	}
}
