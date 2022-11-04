import java.util.Objects;

public final class Parameter {
    private final String key;
    private final String value;

    Parameter(
            String key,
            String value

    ) {
        this.key = key;
        this.value = value;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Parameter) obj;
        return Objects.equals(this.key, that.key) &&
                Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "Parameter[" +
                "key=" + key + ", " +
                "value=" + value + ']';
    }
}
