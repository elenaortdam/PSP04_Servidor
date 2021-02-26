import java.io.FileWriter;
import java.io.IOException;

public class ObjetoCompartido {

	private int numeroCliente;
	private final FileWriter log;

	public ObjetoCompartido(FileWriter fileWriter) {
		this.log = fileWriter;
		this.numeroCliente += 1;
	}

	public synchronized void writeLine(FileWriter log, String newLine) throws IOException {
		if (log != null) {
			log.write(newLine);
			log.flush();
		}

	}
}
