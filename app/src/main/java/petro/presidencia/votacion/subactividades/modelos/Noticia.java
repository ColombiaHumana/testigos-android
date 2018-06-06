package petro.presidencia.votacion.subactividades.modelos;

/**
 * Created by mike on 5/31/18.
 */

public class Noticia {
    String titulo,contenido;

    public Noticia() {
    }

    public Noticia(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
