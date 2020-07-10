package Database.users;

public interface Permissible {
    boolean hasPermission(Permission permission);
}
