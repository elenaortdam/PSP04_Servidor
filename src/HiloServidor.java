import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class HiloServidor extends Thread {

	ObjectInputStream fentrada;
	ObjectOutputStream fsalida;
	FileWriter log;
	Integer id;
	Profesor[] profesores;
	Socket socket = null;
	private Map<Integer, Long> inicioSesion = new HashMap<>();

	public HiloServidor(Socket s, FileWriter fileWriter, int id, Profesor[] profesores) throws IOException {
		socket = s;
		fsalida = new ObjectOutputStream(socket.getOutputStream());
		fentrada = new ObjectInputStream(socket.getInputStream());
		this.log = fileWriter;
		this.id = id;
		this.profesores = profesores;
	}

	public void run() {// tarea a realizar con el cliente

		ObjetoCompartido objetoCompartido = new ObjetoCompartido(this.log);
		int numeroCliente = objetoCompartido.getNumeroCliente();
		System.out.printf("Cliente %s conectado\n", numeroCliente);
		try {
			//Dar formato a la fecha y hora
			objetoCompartido.escribirLínea(this.log, String.format("Cliente: %s iniciado, (%s)\n", this.id,
																   LocalDateTime.now()));

			inicioSesion.put(this.id, System.currentTimeMillis());

		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			fsalida.reset();
			fsalida.writeObject(numeroCliente);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String cadena = "";
		while (!cadena.trim().equals("*")) {

			try {
				//fentrada.reset();
				cadena = (String) fentrada.readObject();
				//TODO: elena controlar si es nulo o el caso de que sea asterisco
				int idProfesor = Integer.parseInt(cadena);
				Profesor profesor = null;
				for (int i = 0; i < this.profesores.length; i++) {
					if (this.profesores[i].getIdprofesor() == idProfesor) {
						profesor = this.profesores[i];
						break;
					}
				}
				if (profesor == null) {
					profesor = new Profesor(-1, "NOT FOUND",
											new Asignatura[]{}, new Especialidad());
				}
				fsalida.writeObject(profesor);

			} catch (IOException | ClassNotFoundException e) {
				System.out.println("ERROR AL LEER DATOS DEL CLIENTE");
				System.err.println(e.getMessage());
				break;

			}
//			fsalida.println(cadena.trim().toUpperCase());
		} // fin while

		System.out.println("FIN CON: " + socket.toString());

//		fsalida.close();
		try {
			fentrada.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
