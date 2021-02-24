import java.io.Serializable;

public class Especialidad implements Serializable {

	private static final long serialVersionUID = -7556292344769335529L;


	private int id;
	private String nombreespe;

	public Especialidad() {
	}

	public Especialidad(int id, String nombreespe) {
		this.id = id;
		this.nombreespe = nombreespe;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombreespe() {
		return nombreespe;
	}

	public void setNombreespe(String nombreespe) {
		this.nombreespe = nombreespe;
	}
}
