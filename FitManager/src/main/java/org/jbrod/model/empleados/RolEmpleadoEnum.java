package org.jbrod.model.empleados;

public enum RolEmpleadoEnum {

    ADMINISTRADOR(1),
    INVENTARIO(2),
    RECEPCIONISTA(3),
    ENTRENADOR(4);

    private final int id;


    RolEmpleadoEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    // Buscar por ID
    public static RolEmpleadoEnum fromId(int id) {
        for (RolEmpleadoEnum rol : values()) {
            if (rol.getId() == id) return rol;
        }
        return null;
    }
}
