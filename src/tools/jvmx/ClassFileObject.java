package tools.jvmx;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.CharBuffer;

public class ClassFileObject implements JavaFileObject {

    /** The file's name.
     */
    private final String name;

    public ClassFileObject(String name) {
        this.name = name;
    }

    @Override
    public URI toUri() {
        try {
            return new URI(null, name.toString(), null);
        } catch (URISyntaxException e) {
          //  throw new CannotCreateUriError(name.toString(), e);
            return null;
        }
    }

    @Override
    public String getName() {
        return name.toString();
    }

    @Override
    public Kind getKind() {
        return getKind(getName());
    }

    public static Kind getKind(String name) {
        if (name.endsWith(Kind.CLASS.extension))
            return Kind.CLASS;
        else if (name.endsWith(Kind.SOURCE.extension))
            return Kind.SOURCE;
        else if (name.endsWith(Kind.HTML.extension))
            return Kind.HTML;
        else
            return Kind.OTHER;
    }

    @Override
    public InputStream openInputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream openOutputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reader openReader(boolean ignoreEncodingErrors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Writer openWriter() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getLastModified() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNameCompatible(String simpleName, Kind kind) {
        return true; // fail-safe mode
    }

    @Override
    public NestingKind getNestingKind() {
        return null;
    }

    @Override
    public Modifier getAccessLevel() {
        return null;
    }

    /**
     * Check if two file objects are equal.
     * SourceFileObjects are just placeholder objects for the value of a
     * SourceFile attribute, and do not directly represent specific files.
     * Two SourceFileObjects are equal if their names are equal.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (!(other instanceof ClassFileObject))
            return false;

        ClassFileObject o = (ClassFileObject) other;
        return name.equals(o.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
