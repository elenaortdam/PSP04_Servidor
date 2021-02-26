import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorTCP {

	private Profesor[] getProfesores() {

		Asignatura[] asignaturas = new Asignatura[9];

		asignaturas[0] = new Asignatura(1, "Programación");
		asignaturas[1] = new Asignatura(2, "PMDM");
		asignaturas[2] = new Asignatura(3, "Economía");
		asignaturas[3] = new Asignatura(4, "EIE");
		asignaturas[4] = new Asignatura(5, "FOL");
		asignaturas[5] = new Asignatura(6, "Lengua");
		asignaturas[6] = new Asignatura(7, "Física");
		asignaturas[7] = new Asignatura(8, "Química");
		asignaturas[8] = new Asignatura(9, "Matemáticas");

		final Profesor[] profesores = new Profesor[5];
		profesores[0] = new Profesor(1, "Iván", new Asignatura[]{asignaturas[0],
				asignaturas[1]}, new Especialidad(1, "INFORMÁTICA"));

		profesores[1] = new Profesor(2, "María", new Asignatura[]{asignaturas[2],
				asignaturas[3], asignaturas[4]}, new Especialidad(1, "ECONOMÍA"));

		profesores[2] = new Profesor(3, "David", new Asignatura[]{asignaturas[5]},
									 new Especialidad(1, "LENGUA"));

		profesores[3] = new Profesor(4, "Estrella", new Asignatura[]{asignaturas[5],
				asignaturas[7]}, new Especialidad(1, "FÍSICA Y QUÍMICA"));

		profesores[4] = new Profesor(5, "Eulogio", new Asignatura[]{
				asignaturas[8]}, new Especialidad(1, "MATEMÁTICAS"));

		return profesores;

	}

	private void crearServidor() throws IOException, InterruptedException {
		ServerSocket servidor;
		servidor = new ServerSocket(6000);
		System.out.println("Servidor iniciado...");

		int id = 1;
		String fileName = "FichLog.txt";

		Profesor[] profesores = getProfesores();
		FileWriter logFile = null;
		File file = new File("./" + fileName);
		if (file.exists()) {
			logFile = new FileWriter(file, true);
		} else {
			logFile = new FileWriter(fileName);
		}

		while (true) {
			try {
				Socket cliente = new Socket();
				cliente = servidor.accept();//esperando cliente

				HiloServidor hilo = new HiloServidor(cliente, logFile, id, profesores);
				id++;
				hilo.start();
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				System.out.println("Durmiendo hilo. Reintentar la conexión en 10 segundos...");
				Thread.sleep(10000);
			}
		}

	}

	public static void main(String[] args) throws IOException, InterruptedException {
		ServidorTCP servidorTCP = new ServidorTCP();
		servidorTCP.crearServidor();
	}
}
