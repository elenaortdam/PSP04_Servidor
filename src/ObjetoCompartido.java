import java.io.FileWriter;
import java.io.IOException;

public class ObjetoCompartido {

	private int numeroCliente;
	private FileWriter log;

	public ObjetoCompartido(FileWriter fileWriter) {
		this.log = fileWriter;
		this.numeroCliente += 1;
	}

	public synchronized int getNumeroCliente() {
		return numeroCliente;
	}

	public synchronized void escribirLínea(FileWriter log, String newLine) throws IOException {
		if (log != null) {
			log.write(newLine);
			log.flush();
		}

	}
}
