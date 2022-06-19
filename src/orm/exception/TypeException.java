package orm.exception;

/**
 * 数组类型不支持
 */
public class TypeException extends Exception {
    public TypeException(String message) {
        super(message);
    }

    public TypeException() {
    }
}
